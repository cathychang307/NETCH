package com.bot.cqs.query.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bot.cqs.gateway.dao.CacheDao;
import com.bot.cqs.gateway.persistence.Cache;
import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.query.persistence.QueryBank;
import com.bot.cqs.query.persistence.QueryUser;
import com.bot.cqs.query.service.EtchAuditLogService;
import com.bot.cqs.query.service.InquiryManager;
import com.bot.cqs.query.util.QueryCacheUtil;
import com.bot.cqs.query.util.factory.ApplicationParameterFactory;
import com.bot.cqs.query.util.inquiry.InquirySender;
import com.bot.cqs.query.util.inquiry.InquiryThread;
import com.bot.cqs.query.util.queryField.QueryFieldException;
import com.bot.cqs.query.util.queryField.QueryInputFieldDefinition;
import com.bot.cqs.query.util.queryField.QueryRequestDefinition;
import com.iisigroup.cap.component.Request;

@Service
public class InquiryManagerImpl extends BasicQueryManager implements InquiryManager {

    private DateFormat inquiryLogDateFormat;

    @Value("#{systemConfig.getProperty('inquiry.sender.timeout')}")
    private int inquiryTimeout;

    @Resource
    private CacheDao queryCache;

    @Resource
    EtchAuditLogService etchAuditLogService;

    public InquiryManagerImpl() {

        super();
        inquiryLogDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    }

    /**
     * 取得查詢最大等候時間, 以秒為單位
     * 
     * @return
     */
    public int getInquiryTimeout() {

        return inquiryTimeout;
    }

    /**
     * 設定查詢最大等候時間, 以秒為單位
     * 
     * @param inquiryTimeout
     */
    public void setInquiryTimeout(int inquiryTimeout) {

        this.inquiryTimeout = inquiryTimeout;
    }

    public InquiryThread[] getInquiryResult(InquiryLog[] inquiryLogs) {
        Cache cache = null;
        for (int i = 0; i < inquiryLogs.length; i++) {
            // 此的工作為，若為查詢cache table裡的資料，就帶出當時的時間。<code>cache.getInquiryTime();</code>
            QueryCacheUtil.changeFieldValue(inquiryLogs[i], false);
            if (ApplicationParameterFactory.newInstance().getQueryCacheInterval() != 0) {
                cache = queryCache.findByDummyKey(QueryCacheUtil.formatCacheKey(inquiryLogs[i]));
            }
            if (cache != null) {
                inquiryLogs[i].setInquiryCacheTime(cache.getInquiryTime());
            }
            QueryCacheUtil.changeFieldValue(inquiryLogs[i], true);
        }
        InquirySender sender = new InquirySender(inquiryLogs);
        sender.setTimeout(getInquiryTimeout());
        sender.start();
        return sender.waitForInquiry();
    }

    public InquiryLog[] getInquiryLogFromParams(Request request, Map<String, String[]> paramMap, QueryRequestDefinition queryDef, QueryUser user, QueryBank bank, int maxCount)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, QueryFieldException, UnknownHostException {

        if (maxCount < 1)
            return new InquiryLog[] {};

        // 1. 取得所有輸入資料
        List<InquiryLog> array = new ArrayList<InquiryLog>();
        for (int i = 0; i < maxCount; i++) {

            QueryInputFieldDefinition[] fieldDefArray = queryDef.getQueryInputFieldDefinition();
            String[] values = new String[fieldDefArray.length];
            InquiryLog inquiryLog = new InquiryLog();
            for (int j = 0; j < fieldDefArray.length; j++) {

                QueryInputFieldDefinition fieldDef = fieldDefArray[j];

                String[] reqValues = paramMap.get(fieldDef.getFieldName() + '_' + i);
                if (reqValues == null || reqValues.length == 0)
                    values[j] = null;
                else
                    values[j] = reqValues[0];
            }

            // 全沒輸入就不要加入查詢
            if (isEmptyRequest(values))
                continue;
            else {
                for (int j = 0; j < fieldDefArray.length; j++) {
                    QueryInputFieldDefinition fieldDef = fieldDefArray[j];
                    String fieldValue = null;
                    try {
                        fieldValue = fieldDef.convert(values[j]);
                    } catch (QueryFieldException e) {
                        e.setRequestFieldIndex(i);
                        throw e;
                    }
                    setFieldAttribute(fieldDef, inquiryLog, fieldValue);

                    // 蓋掉原值
                    String[] reqValues = paramMap.get(fieldDef.getFieldName() + '_' + i);
                    if (reqValues != null && reqValues.length > 0)
                        reqValues[0] = fieldValue;
                }

                inquiryLog.setRequestFieldPosition(i);
                array.add(inquiryLog);
            }
        }

        // 2. 填入非使用者輸入的資料
        InquiryLog[] result = new InquiryLog[array.size()];
        array.toArray(result);

        Date currentDate = new Date();
        String currentDateStr = inquiryLogDateFormat.format(currentDate);
        String ymd = currentDateStr.substring(0, 8);
        String hms = currentDateStr.substring(8);

        for (int i = 0; i < result.length; i++) {
            result[i].setInquiryUserName(user.getEmployeeName());
            result[i].setInquiryAccount(user.getEmployeeId());
            result[i].setInquiryTchId(bank.getTchId());
            result[i].setInquiryQryBankId(bank.getChargeBankId());
            result[i].setInquiryChargeBankId(bank.getChargeBankId());
            result[i].setInquiryTxCode(queryDef.getTransactionId());
            result[i].setInquiryDate(ymd);
            result[i].setInquiryTime(hms);
            result[i].setInquiryStartDatetime(currentDate);
            result[i].setInquiryResponseFormat("H");
        }

        return result;
    }

    private boolean isEmptyRequest(String[] values) {

        for (String s : values)
            if (s != null && s.length() > 0)
                return false;

        return true;
    }

    private void setFieldAttribute(QueryInputFieldDefinition fieldDef, InquiryLog inquiryLog, String value) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        // 要用 reflection 來指定
        // 1. method name
        StringBuffer setterMethodName = new StringBuffer("set");
        setterMethodName.append(fieldDef.getTargetFieldName());
        setterMethodName.setCharAt(3, Character.toUpperCase(setterMethodName.charAt(3)));

        // 2. invoke
        Method method = inquiryLog.getClass().getMethod(setterMethodName.toString(), new Class[] { String.class });
        method.invoke(inquiryLog, new Object[] { value });

    }

}
