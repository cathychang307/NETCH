package com.bot.cqs.query.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.bot.cqs.query.annotation.NetchLogWritingAction;
import com.bot.cqs.query.command.NetchLogContent;
import com.bot.cqs.query.exception.NetchLogMessageException;
import com.bot.cqs.query.util.NetchLogWritingUtil;
import com.iisigroup.cap.action.Action;
import com.iisigroup.cap.annotation.HandlerType;
import com.iisigroup.cap.annotation.HandlerType.HandlerTypeEnum;
import com.iisigroup.cap.component.GridResult;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.constants.GridEnum;
import com.iisigroup.cap.context.CapParameter;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.dao.impl.SearchSettingImpl;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.model.OpStepContext;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.cap.operation.Operation;
import com.iisigroup.cap.operation.OperationStep;
import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapBeanUtil;
import com.iisigroup.cap.utils.CapString;

import net.sf.json.JSONArray;

public abstract class NetchMFormHandler extends MFormHandler {

    @Resource(name = "handlerOpMapping")
    private CapParameter handlerOp;

    abstract NetchLogContent generateDataForLogWriting(Request request);
    
    public String getReturnMsg(Request request) {
        String msg = request.get("returnMsg");
        return msg;
    }

    public void setReturnMessage(Request request, String returnMsg) {
        request.put("returnMsg", returnMsg);
    }

    /**
     * <pre>
     * 直接以method name來執行
     * </pre>
     * 
     * @param formAction
     *            action
     * @return IAction
     */
    @Override
    public Action getAction(String formAction) {
        return new MethodExecuteAction(this);
    }

    /**
     * <pre>
     * MethodExecuteAction
     * </pre>
     */
    private class MethodExecuteAction implements Action {

        MFormHandler executeHandler;

        public MethodExecuteAction(MFormHandler executeObj) {
            this.executeHandler = executeObj;
        }

        @Override
        public Result doWork(Request request) {
            Result rtn = null;
            String methodId = request.get(FORM_ACTION);
            if (CapString.isEmpty(methodId)) {
                methodId = "doWork";
            }
            boolean hasMethod = false;
            NetchLogContent netchLogContent = null;
            try {
                Method method = CapBeanUtil.findMethod(executeHandler.getClass(), methodId, (Class<?>) null);
                if (method != null) {
                    // writeLogBeforeAction start----------------------
                    NetchLogWritingAction netchLogWritingAction = method.getAnnotation(NetchLogWritingAction.class);
                    if (netchLogWritingAction != null) {
                        netchLogContent = generateDataForLogWriting(request);
                        List<Object> newContent = new ArrayList<Object>();
                        newContent.add(CapSecurityContext.getUserId());
                        newContent.add(netchLogWritingAction.functionId());
                        newContent.add(CapAppContext.getMessage("menu." + netchLogWritingAction.functionId()));
                        newContent.addAll(netchLogContent.getContent());
                        netchLogContent.setContent(newContent);
                        if (netchLogWritingAction.writeLogBeforeAction()) {
                            NetchLogWritingUtil.writeLogBeforeAction(netchLogContent);
                        }
                    }
                    // writeLogBeforeAction end------------------------

                    HandlerType type = method.getAnnotation(HandlerType.class);
                    if (type != null && HandlerTypeEnum.GRID.equals(type.value())) {
                        rtn = getGridData(method, request);
                    } else {
                        rtn = (Result) method.invoke(executeHandler, request);
                    }
                    hasMethod = true;

                    // writeLogAfterAction start----------------------
                    if (netchLogWritingAction != null && netchLogWritingAction.writeSuccessLogAfterAction()) {
                        if(!CapString.isEmpty(getReturnMsg(request))){
                            netchLogContent.setReturnMsg(getReturnMsg(request));
                        }
                        NetchLogWritingUtil.writeLogAfterAction(netchLogContent);
                    }
                    // writeLogAfterAction end------------------------

                }
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof CapMessageException) {
                    // writeLogAfterAction start----------------------
                    if(e.getCause() instanceof NetchLogMessageException){
                        NetchLogMessageException exception = (NetchLogMessageException) (e.getCause());
                        String returnMsg = exception.getReturnMsg();
                        if(CapString.isEmpty(returnMsg)){
                            returnMsg = exception.getMessage();
                        }
                        if (netchLogContent != null && returnMsg != null) {
                            netchLogContent.setReturnMsg(returnMsg);
                            NetchLogWritingUtil.writeLogAfterAction(netchLogContent);
                        }
                    }
                    // writeLogAfterAction end------------------------
                    throw (CapMessageException) e.getCause();
                } else if (e.getCause() instanceof CapException) {
                    throw (CapException) e.getCause();
                } else {
                    throw new CapException(e.getCause(), executeHandler.getClass());
                }
            } catch (Throwable t) {
                throw new CapException(t, executeHandler.getClass());
            }
            if (!hasMethod) {
                throw new CapMessageException("action not found", getClass());
            }
            return rtn;
        }

    }

    @SuppressWarnings({ "rawtypes" })
    private Result getGridData(Method method, Request params) {
        SearchSetting search = createSearchTemplete();
        boolean pages = params.containsParamsKey(GridEnum.PAGE.getCode());
        int page = 0, pageRows = 0, startRow = 0;
        if (pages) {
            page = params.getParamsAsInteger(GridEnum.PAGE.getCode());
            pageRows = params.getParamsAsInteger(GridEnum.PAGEROWS.getCode());
            startRow = (page - 1) * pageRows;
            search.setFirstResult(startRow).setMaxResults(pageRows);
        }
        boolean sort = params.containsParamsKey(GridEnum.SORTCOLUMN.getCode()) && !CapString.isEmpty(params.get(GridEnum.SORTCOLUMN.getCode()));
        if (sort) {
            String[] sortBy = params.get(GridEnum.SORTCOLUMN.getCode()).split("\\|");
            String[] isAscAry = params.get(GridEnum.SORTTYPE.getCode(), "asc").split("\\|");
            for (int i = 0; i < sortBy.length; i++) {
                String isAsc = (i < isAscAry.length) ? isAscAry[i] : "asc";
                search.addOrderBy(sortBy[i], !GridEnum.SORTASC.getCode().equals(isAsc));
            }
        }
        GridResult result = null;
        try {
            result = (GridResult) method.invoke(this, search, params);
            result.setColumns(getColumns(params.get(GridEnum.COL_PARAM.getCode())));
            result.setPage(page);
            result.setPageCount(result.getRecords(), pageRows);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof CapMessageException) {
                throw (CapMessageException) e.getCause();
            } else if (e.getCause() instanceof CapException) {
                throw (CapException) e.getCause();
            } else {
                throw new CapException(e.getCause(), this.getClass());
            }
        } catch (Throwable t) {
            throw new CapException(t, this.getClass());
        }
        return result;
    }

    /**
     * 取得iGrid中的Column Name
     * 
     * @param params
     *            String
     * @return String string[]
     */
    @SuppressWarnings("unchecked")
    protected String[] getColumns(String params) {
        JSONArray arr = JSONArray.fromObject(params);
        String[] colNames = new String[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            Map<String, String> m = (Map<String, String>) arr.get(i);
            if (m.containsKey(GridEnum.COL_INDEX.getCode())) {
                colNames[i] = new StringBuffer().append(m.get(GridEnum.COL_NAME.getCode())).append("|").append(m.get(GridEnum.COL_INDEX.getCode())).toString();
            } else {
                colNames[i] = m.get(GridEnum.COL_NAME.getCode());
            }
        }
        return colNames;
    };

    /**
     * <pre>
     * 若未傳送formAction值，則default執行此method
     * </pre>
     * 
     * @param params
     *            PageParameters
     * @return IResult
     */
    public Result doWork(Request params) {
        return null;
    }

    @Override
    public Result execute(Request params) {
        Operation oper = getOperation(params);
        if (oper != null) {
            OpStepContext ctx = new OpStepContext(OperationStep.NEXT);
            oper.execute(ctx, params, this);
            return ctx.getResult();
        }
        return null;
    }

    protected String getOperationName(Request params) {
        String methodId = params.get(FORM_ACTION);
        Method method = CapBeanUtil.findMethod(this.getClass(), methodId, (Class<?>) null);
        if (method != null) {
            HandlerType type = method.getAnnotation(HandlerType.class);
            if (type != null) {
                String op = type.name();
                if (op == null || "".equals(op)) {
                    op = type.value().name();
                }
                return handlerOp.getValue(op, SIMPLE_OPERATION);
            }
        }
        return SIMPLE_OPERATION;
    }

    protected Operation getOperation(Request params) {
        return (Operation) CapAppContext.getApplicationContext().getBean(getOperationName(params));
    }

    private SearchSetting createSearchTemplete() {
        return new GridSearch();
    }

    /**
     * <pre>
     * GridSearch extends AbstractSearchSetting
     * </pre>
     */
    private class GridSearch extends SearchSettingImpl {

        private static final long serialVersionUID = 1L;

    }

    @Override
    public String getHandlerName() {
        return getPluginName();
    }

}
