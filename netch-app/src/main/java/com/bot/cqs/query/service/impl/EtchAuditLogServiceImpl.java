package com.bot.cqs.query.service.impl;

import com.bot.cqs.query.command.MultiQueryCommand;
import com.bot.cqs.query.dao.EtchAuditLogDao;
import com.bot.cqs.query.persistence.EtchAuditLog;
import com.bot.cqs.query.persistence.QueryUser;
import com.bot.cqs.query.service.EtchAuditLogService;
import com.bot.cqs.security.model.EtchUserDetails;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

/**
 * <pre>
 * EtchAuditLogServiceImpl
 * </pre>
 *
 * @author cathy chang
 * @version
 *          <ul>
 *          <li>2021年1月20日,cathy chang,new
 *          </ul>
 * @since 2021年1月20日
 */
@Service
public class EtchAuditLogServiceImpl implements EtchAuditLogService {
    @Resource private EtchAuditLogDao etchAuditLogDao;

    @Resource private CapSystemConfig sysConfig;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public void saveEtchAuditLog(Request request, String functionName, String functionId, int queryCount) throws UnknownHostException {
        QueryUser user = CapSecurityContext.<EtchUserDetails>getUser().getQueryUser();
        MultiQueryCommand multiQueryCommand = CapBeanUtil.map2Bean(request, new MultiQueryCommand());
        multiQueryCommand.parseInputTransactionId();
        String soureIp = request.getServletRequest().getRemoteAddr();
        //String targetIp = InetAddress.getLocalHost().getHostAddress();
        String targetIp = getLocalHostIp();
        String inputTransactionId = multiQueryCommand.getInputTransactionId();
        String actionId = request.get("formAction");
        String sqlScript = "";
        switch (actionId) {
        case "query":
            actionId = "Q";
            break;
        case "multiInquiry":
            sqlScript = "[多筆查詢條件] ";
            actionId = "Q";
            break;
        case "inquiry":
            sqlScript = "[單筆查詢條件] ";
            actionId = "Q";
            break;
        case "printWriteLog":
            actionId = "P";
            break;
        case "downloadPdf":
            actionId = "R";
            break;
        case "downloadCsv":
            actionId = "O";
            break;
        }
        String transactionId = multiQueryCommand.getTransactionId();
        if (!CapString.isEmpty(transactionId) && CapString.isEmpty(functionId)) {
            switch (transactionId) {
            //單筆
            case "4111":
            case "4114":
            case "4121":
            case "4123":
                for (Map.Entry<String, Object> req : request.entrySet()) {
                    String key = req.getKey();
                    String KeyNum = "";
                    if (key.startsWith("personalId") && !CapString.isEmpty(request.get(key))) {
                        KeyNum = !CapString.isEmpty(key) && !"0".equals(key.split("_")[1]) ? key.split("_")[1] : "";
                        sqlScript += "身分證統一編號" + KeyNum + "：" + request.get(key) + "；";
                    }
                    if (key.startsWith("personalName") && !CapString.isEmpty(request.get(key))) {
                        KeyNum = !CapString.isEmpty(key) && !"0".equals(key.split("_")[1]) ? key.split("_")[1] : "";
                        sqlScript += "姓名" + KeyNum + "：" + request.get(key) + "；";
                    }
                }
                break;
            case "4112":
            case "4115":
            case "4122":
            case "4124":
            case "4132":
            case "4135":
                for (Map.Entry<String, Object> req : request.entrySet()) {
                    String key = req.getKey();
                    String KeyNum = "";
                    if (key.startsWith("companyId") && !CapString.isEmpty(request.get(key))) {
                        KeyNum = !CapString.isEmpty(key) && !"0".equals(key.split("_")[1]) ? key.split("_")[1] : "";
                        sqlScript += "營利事業統一編號" + KeyNum + "：" + request.get(key) + "；";
                    }
                    if (key.startsWith("principalId") && !CapString.isEmpty(request.get(key))) {
                        KeyNum = !CapString.isEmpty(key) && !"0".equals(key.split("_")[1]) ? key.split("_")[1] : "";
                        sqlScript += "負責人身分證統一編號" + KeyNum + "：" + request.get(key) + "；";
                    }
                    if (key.startsWith("companyName") && !CapString.isEmpty(request.get(key))) {
                        KeyNum = !CapString.isEmpty(key) && !"0".equals(key.split("_")[1]) ? key.split("_")[1] : "";
                        sqlScript += "公司名稱" + KeyNum + "：" + request.get(key) + "；";
                    }
                }
                break;
            case "4113":
            case "4116":
            case "4133":
            case "4136":
                for (Map.Entry<String, Object> req : request.entrySet()) {
                    String key = req.getKey();
                    String KeyNum = "";
                    if (key.startsWith("bankCode") && !CapString.isEmpty(request.get(key))) {
                        KeyNum = !CapString.isEmpty(key) && !"0".equals(key.split("_")[1]) ? key.split("_")[1] : "";
                        sqlScript += "開戶行代號" + KeyNum + "：" + request.get(key) + "；";
                    }
                    if (key.startsWith("bankAccount") && !CapString.isEmpty(request.get(key))) {
                        KeyNum = !CapString.isEmpty(key) && !"0".equals(key.split("_")[1]) ? key.split("_")[1] : "";
                        sqlScript += "開戶行帳號" + KeyNum + "：" + request.get(key) + "；";
                    }
                }
                break;
            }
        } else if (CapString.isEmpty(transactionId) && !CapString.isEmpty(functionId)) {
            inputTransactionId = functionId;
            sqlScript += "[查詢條件] 時間(起)：" + request.get("startDate") + "；時間(迄)：" + request.get("endDate") + "；查詢類型：" + request.get("inquiryTxCode");
            if (!CapString.isEmpty(request.get("inquiryChargeBankId"))) {
                sqlScript += "；付費分行：" + request.get("inquiryChargeBankId");
            }
            if (!CapString.isEmpty(request.get("inquiryAccount"))) {
                sqlScript += "；查詢人ID：" + request.get("inquiryAccount");
            }
        }

        EtchAuditLog etchAuditLog = new EtchAuditLog();
        etchAuditLog.setOid(UUIDGenerator.getUUID().substring(0, 32));
        etchAuditLog.setUserId(user.getEmployeeId());// 使用者帳號/員工編號
        etchAuditLog.setAccessObject(functionName);// 系統檔案名稱或存取物件對象名稱 (如TABLE/VIEW名稱)
        etchAuditLog.setFunctionId(inputTransactionId);// 執行程式名稱/交易代號/交易名稱
        etchAuditLog.setActionId(actionId);// 記載動作類別，如 A:新增/ D:刪除/ E:修改/ Q:查詢 R:報表/ O:匯出下載/ P:列印 等
        etchAuditLog.setSqlScript(sqlScript);// 如程式參數、檔案處理語法或資料庫指令(SQL語法)等
        etchAuditLog.setExecuteStatus("Success");// 執行成功或失敗(Success/Failure)
        etchAuditLog.setDataCount(String.valueOf(queryCount));// 記載執行成功所回傳之資料筆數
        etchAuditLog.setSourceIp(soureIp);// 來源IP/設備Hostname/終端機ID
        etchAuditLog.setExecuteDate(CapDate.getCurrentTimestamp());// 日期時間
        etchAuditLog.setTargetIp(targetIp);// 所連線目標資料庫/伺服器IP
        etchAuditLog.setAccessAccount(getDbUserName());// 存取系統檔案之帳號或存取DB物件之帳號,DB帳號
        etchAuditLog.setExecuteResult(null);// 記載執行成功所回傳之資料內容,不紀錄
        etchAuditLogDao.saveEtchAuditLog(etchAuditLog);
    }

    private String getDbUserName() {
        String dbProp = "db/database.properties";
        String dbUserName = "";
        Properties properties = new Properties();
        InputStream ips = null;
        try {
            ips = getClass().getClassLoader().getResourceAsStream(dbProp);
            properties.load(new InputStreamReader(ips, StandardCharsets.UTF_8));
            dbUserName = properties.getProperty("jdbc.username");
        } catch (Exception ex) {
            logger.error("Get DB user name error." + ex.getMessage());
        } finally {
            if (ips != null) {
                try {
                    ips.close();
                } catch (IOException e) {
                    logger.debug(e.getMessage());
                }
            }
        }
        return dbUserName;
    }

    private String getLocalHostIp() {
        String localHostIp = "";
        Properties properties = getLocalHostInfo();
        try {
            localHostIp = properties.getProperty("localhost.ip");
        } catch (Exception ex) {
            logger.error("Get Local Host ip error." + ex.getMessage());
        }
        return localHostIp;
    }
    public Properties getLocalHostInfo() {
        String configProp = "/config.properties";
        String ipConfigPath = "";
        InputStream configs = null;
        InputStream ips = null;
        Properties ipProperties = new Properties();
        Properties configProperties = new Properties();
        try {
            configs = getClass().getClassLoader().getResourceAsStream(configProp);
            configProperties.load(new InputStreamReader(configs, StandardCharsets.UTF_8));
            ipConfigPath = configProperties.getProperty("ipconfig.location");
            ips = new FileInputStream(ipConfigPath);
            ipProperties.load(new InputStreamReader(ips, StandardCharsets.UTF_8));
        } catch (Exception ex) {
            logger.error("Get Local Host Info error." + ex.getMessage());
        } finally {
            if (ips != null) {
                try {
                    ips.close();
                } catch (IOException e) {
                    logger.debug(e.getMessage());
                }
            }
            if (configs != null) {
                try {
                    configs.close();
                } catch (IOException e) {
                    logger.debug(e.getMessage());
                }
            }
        }
        return ipProperties;
    }
}
