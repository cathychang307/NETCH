package com.bot.cqs.query.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;

import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.query.command.NetchLogContent;
import com.bot.cqs.query.command.QueryInquiryLogCommand;
import com.bot.cqs.query.dto.InquiryLogDto;
import com.bot.cqs.query.persistence.QueryBank;
import com.bot.cqs.query.persistence.QueryRole;
import com.bot.cqs.query.service.InquiryLogManager;
import com.bot.cqs.query.service.QueryFunctionService;
import com.bot.cqs.query.service.QueryRoleService;
import com.bot.cqs.query.util.factory.QueryBankFactory;
import com.iisigroup.cap.annotation.HandlerType;
import com.iisigroup.cap.annotation.HandlerType.HandlerTypeEnum;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.component.impl.BeanGridResult;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.model.Page;
import com.iisigroup.cap.db.service.CommonService;
import com.iisigroup.cap.exception.CapFormatException;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.formatter.BeanFormatter;
import com.iisigroup.cap.formatter.Formatter;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapString;

/**
 * <pre>
 * ConfigureRoleHandler
 * </pre>
 * 
 * @since 2016年12月29日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2016年12月29日,bob peng,new
 *          </ul>
 */
@Controller("configurerolehandler")
public class ConfigureRoleHandler extends NetchMFormHandler {

    @Resource
    private CommonService commonService;
    @Resource
    private QueryRoleService queryRoleService;
    @Resource
    private InquiryLogManager inquiryLogManager;
    @Resource
    private QueryBankFactory queryBankFactory;
    @Resource
    private QueryFunctionService queryFunctionService;
    
    @Override
    NetchLogContent generateDataForLogWriting(Request request) {
        // TODO Auto-generated method stub
        return null;
    }

    @HandlerType(HandlerTypeEnum.GRID)
    public BeanGridResult query(SearchSetting search, Request request) {
        Page<QueryRole> page = commonService.findPage(QueryRole.class, search);
        Map<String, Formatter> fmt = new HashMap<String, Formatter>();
        fmt.put("roleName", new RoleNameFormatter());
        return new BeanGridResult(page.getContent(), page.getTotalRow(), fmt);
    }
    
    class RoleNameFormatter implements BeanFormatter {
        private static final long serialVersionUID = 1L;
        @SuppressWarnings("unchecked")
        public String reformat(Object in) throws CapFormatException {
            String result = "";
            if (in instanceof QueryRole) {
                QueryRole queryRole = (QueryRole) in;
                if (queryRole.isRoleEnabled()) {
                    result = queryRole.getRoleName();
                } else {
                    result = CapAppContext.getMessage("js.configureRole.msg.03") + queryRole.getRoleName(); // <span class=\"text-red\">(已停用)</span>
                }
            }
            return result;
        }
    }

    public Result getInChargeBankId(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        List<InquiryLog> chargeBankIdList = inquiryLogManager.getChargeBankList();
        List<QueryBank> formQueryBankList = queryBankFactory.getQueryBankList();
        Map<String, String> map = new TreeMap<String, String>();
        for (InquiryLog currentInquiry : chargeBankIdList) {
            for (QueryBank queryBank : formQueryBankList) {
                if (queryBank.getChargeBankId().equalsIgnoreCase(currentInquiry.getInquiryChargeBankId())) {
                    map.put(queryBank.getChargeBankId(), queryBank.getChargeBankName());
                    break;
                }
            }
            if (!map.containsKey(currentInquiry.getInquiryChargeBankId())) {
                map.put(currentInquiry.getInquiryChargeBankId(), "");
            }
        }
        result.putAll(map);
        return result;
    }

    public Result checkIfDataExist(Request request) {
        AjaxFormResult result = new AjaxFormResult();

        String inChargeBankId = request.get("inChargeBankId");
        String inputYear = request.get("inputYear");
        String inputMonth = request.get("inputMonth");
        QueryInquiryLogCommand command = new QueryInquiryLogCommand();
        command.setInputYear(inputYear);
        command.setInputMonth(inputMonth);
        command.setInChargeBankId(CapString.isEmpty(inChargeBankId) ? "ALL" : inChargeBankId);
        command.setQueryMode("1");
        List<InquiryLogDto> list = inquiryLogManager.findForInquiryLogDtoList(command);
        if (CollectionUtils.isEmpty(list)) {
            throw new CapMessageException(CapAppContext.getMessage("js.chargeQuery.msg.02"), this.getClass());// 選取的範圍無資料
        }

        return result;
    }



}
