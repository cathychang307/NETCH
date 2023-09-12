package com.bot.cqs.query.service.impl;

import com.bot.cqs.gateway.dao.InquiryLogDao;
import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.query.command.QueryInquiryLogCommand;
import com.bot.cqs.query.dto.InquiryLogDto;
import com.bot.cqs.query.persistence.QueryBank;
import com.bot.cqs.query.persistence.TransactionRate;
import com.bot.cqs.query.service.InquiryLogManager;
import com.bot.cqs.query.util.DateUtil;
import com.bot.cqs.query.util.factory.ApplicationParameterFactory;
import com.bot.cqs.query.util.factory.QueryBankFactory;
import com.bot.cqs.query.util.factory.TransactionRateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 * 查詢明細資料表{@link tw.com.bot.cqs.gateway.persistence.InquiryLog}的月收費彙整、每筆查詢明細、的交易處理程式。
 * </pre>
 * 
 * @since 2017年1月4日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2017年1月4日,bob peng,new
 *          </ul>
 */
@Service
public class InquiryLogManagerImpl implements InquiryLogManager {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * InquiryLog 的 DAO
     */
    @Resource
    private InquiryLogDao inquiryLogDao;
    /**
     * 系統參數
     */
    @Resource
    private ApplicationParameterFactory applicationParameterFactory;
    /**
     * 費率
     */
    @Resource
    private TransactionRateFactory transactionRateFactory;
    /**
     * 分行
     */
    @Resource
    private QueryBankFactory queryBankFactory;
    /**
     * 判斷是否為合法的交易代碼
     */
    private boolean allowType;

    public void setInquiryLogDao(InquiryLogDao inquiryLogDao) {
        this.inquiryLogDao = inquiryLogDao;
    }

    /**
     * 計算指定的付費分行，及指定的日期的月收費彙整。 (付費分行可以是ALL 所有付費分行)
     * 
     * @param inquiryChargeBankId
     *            付費分行
     * @param date
     *            日期(格式：年/月/日)
     * @return
     */
    public List<InquiryLogDto> findChargeQuery(String inquiryChargeBankId, Date[] date) {
        return summaryAmount(getInquiryLogDao().findChargeQuery(inquiryChargeBankId, date), date);
    }

    public List<InquiryLogDto> findForInquiryLogDtoList(QueryInquiryLogCommand command) {
        if ("1".equalsIgnoreCase(command.getQueryMode()) && "ALL".equalsIgnoreCase(command.getInChargeBankId())) {
            return showAllBank(findChargeQuery(command.getInquiryChargeBankId(), DateUtil.getDateInterval(command.getInputYear() + "/" + command.getInputMonth())));
        } else {
            return findChargeQuery(command.getInquiryChargeBankId(), DateUtil.getDateInterval(command.getInputYear() + "/" + command.getInputMonth()));
        }
    }

    public boolean isInvalidType() {
        return allowType;
    }

    public void setInvalidType(boolean value) {
        allowType = value;
    }

    public InquiryLogDao getInquiryLogDao() {
        return inquiryLogDao;
    }

    /**
     * 搜尋條件下的查詢明細。
     * 
     * @param 從網頁得來的command條件之物件。
     * @return
     */
    public List<InquiryLog> findQueryDetail(QueryInquiryLogCommand command) {
        return getInquiryLogDao().findQueryDetail(command);
    }

    /**
     * 取得付費分行列表。
     */
    public List<InquiryLog> getChargeBankList() {

        return getInquiryLogDao().findInquiryChargeBankList();
    }

    /**
     * 儲存查詢記錄
     *
     * @param inquiryLog
     */
    public void saveInquiryLog(InquiryLog inquiryLog) {

        inquiryLogDao.save(inquiryLog);
    }

    /**
     * 細化計算公式
     *
     * @param sum
     *            筆數小計
     * @param transactionRate
     *            費率物件，包含手續費、收費費率、折扣門檻、折扣
     * @return
     */
    private double doCharge(int sum, TransactionRate transactionRate) {
        return doCharge(sum,transactionRate,0);
    }

    /**
     * 細化計算公式
     * 
     * @param sum
     *            筆數小計
     * @param transactionRate
     *            費率物件，包含手續費、收費費率、折扣門檻、折扣
     * @param floatNumber
     *            小數位浮點數
     * @return
     */
    private double doCharge(int sum, TransactionRate transactionRate,int floatNumber) {

        double temp = 0f;
        try {
            // 沒有費率的直接略過不計算。
            if (transactionRate != null && !isInvalidType()) {
                // 一般計算(交易型態總數*交易型態之費率)。
                if (applicationParameterFactory.getAccountCaculateType() == 0) {
                    temp = sum * transactionRate.getTransactionRate();
                } else {
                    // 超過門檻數做折扣。
                    if (sum >= transactionRate.getTransactionRecordsAtDiscount()) {
                        // (交易型態總數*交易型態之費率 + 總數 * 手續費) * 折扣
                        temp = (sum * transactionRate.getTransactionRate() + sum * transactionRate.getTransactionPoundage()) * transactionRate.getTransactionDiscountRate();
                    } else {
                        // (交易型態總數*交易型態之費率 + 總數 * 手續費)
                        temp = sum * transactionRate.getTransactionRate() + sum * transactionRate.getTransactionPoundage();
                    }
                }
            }
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        // 四捨五入至整數位。
        BigDecimal b = new BigDecimal(temp);

        return b.setScale(floatNumber, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 計算各類收費金額。
     * 
     * @param type
     *            1、2、3、4、5、6分別代表一類、二類、甲類、乙類、OBU一類、OBU二類。
     * @param sum
     *            筆數小計陣列
     * @param transactionRate
     *            費率陣列
     * @return
     */
    public double calculateFee(int type, int[] sum, TransactionRate[] transactionRate) {
        double temp = 0f;
        try {
            switch (type) {
            case 1: // 一類
                for (int i = 1; i <= 3; i++)
                    temp += doCharge(sum[i], transactionRate[i - 1]); // 4111, 4112, 4113
                break;
            case 2: // 二類
                for (int i = 4; i <= 6; i++)
                    temp += doCharge(sum[i], transactionRate[i - 1]); // 4114, 4115, 4116
                break;
            case 3: // 甲類
                temp += doCharge(sum[1], transactionRate[6]); // 4121
                temp += doCharge(sum[2], transactionRate[7]); // 4122
                break;
            case 4: // 乙類
                temp += doCharge(sum[3], transactionRate[8]); // 4123
                temp += doCharge(sum[4], transactionRate[9]); // 4124
                break;
            case 5:// OBU一類
            	temp += doCharge(sum[1], transactionRate[10],2);// 4132
            	temp += doCharge(sum[2], transactionRate[11],2);// 4133
            	break;
            case 6:// OBU二類
            	temp += doCharge(sum[3], transactionRate[12],2);// 4135
            	temp += doCharge(sum[4], transactionRate[13],2);// 4136
            	break;
            default:
                System.out.println("未知的查詢類別！");
                break;
            }
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        return temp;
    }

    /**
     * 查詢月收費檔-加總總計
     * 
     * @param 查詢出所有的付費分行資訊。
     * @param 網頁上的日期條件，為年/月。
     * @return
     */
    public List<InquiryLogDto> summaryAmount(List<InquiryLogDto> chargeInquiryList, Date[] date) {

        InquiryLogDto execRecord = null;
        List<InquiryLogDto> result = new ArrayList<InquiryLogDto>();
        List<QueryBank> formQueryBankList = (List<QueryBank>) queryBankFactory.getQueryBankList();
        List<TransactionRate> rateList = null;
        int[] tempTotalRows = new int[InquiryLogDto.CACHE_ARRAY_SIZE];
        for (InquiryLogDto currentInquiry : chargeInquiryList) {
            // Select Top 之內的。//if (result.size() < applicationParameterFactory.getQueryMaxRows()) {
            // inquiry_cache_flag == 0
            // if(currentInquiry.getInquiryCacheFlag() == 0){

            // 以此判斷此筆InquiryLog是否在日期條件內(為了每次查詢都選出所有的付費分行，所以沒有把日期條件，塞給DAO做)
            boolean countThisFlag = false;
            countThisFlag = ((DateUtil.toADDate(date[0]).compareTo(currentInquiry.getInquiryDate()) <= 0) && (DateUtil.toADDate(date[1]).compareTo(currentInquiry.getInquiryDate()) >= 0));
            // System.out.print(countThisFlag);
            if (execRecord == null || !execRecord.isChargeBankEqual(currentInquiry)) {
                execRecord = currentInquiry;

                for (int i = 0; i < InquiryLogDto.CACHE_ARRAY_SIZE; i++) {
                    tempTotalRows[i] = 0;
                }
                result.add(currentInquiry);
            } else {
                for (int i = 0; i < InquiryLogDto.CACHE_ARRAY_SIZE; i++) {
                    // execRecord.setTotalRows(i, tempTotalRows[i]);
                    execRecord.setTotalRows(i, tempTotalRows[i] + currentInquiry.getTotalRows(i));
                }
                // System.arraycopy(tempTotalRows, 0, execRecord.getTotalRows(), 0, execRecord.getTotalRows().length);
            }

            for (QueryBank queryBank : formQueryBankList) {
                if (queryBank.getChargeBankId().equalsIgnoreCase(currentInquiry.getInquiryChargeBankId())) {
                    currentInquiry.setBankName(queryBank.getChargeBankName());
                    break;
                }
            }

            String inquiryTxCode = currentInquiry.getInquiryTxCode();
            rateList = transactionRateFactory.getTransactionRate(inquiryTxCode, date[0]);
            // 取各TxCode的費率
            for (TransactionRate transactionRate : rateList) {
                /*
                 * System.out.println(transactionRate.getTransactionName()+ transactionRate.getTransactionPoundage()+
                 * transactionRate.getTransactionRate()+"["+transactionRate.getTransactionDiscountRate()+"]"+transactionRate.getTransactionRecordsAtDiscount());
                 */
                setInvalidType(false);
                if ("4111".equalsIgnoreCase(inquiryTxCode)) {
                    execRecord.setTransactionRate(0, transactionRate);

                } else if ("4112".equalsIgnoreCase(inquiryTxCode)) {
                    execRecord.setTransactionRate(1, transactionRate);

                } else if ("4113".equalsIgnoreCase(inquiryTxCode)) {
                    execRecord.setTransactionRate(2, transactionRate);

                } else if ("4114".equalsIgnoreCase(inquiryTxCode)) {
                    execRecord.setTransactionRate(3, transactionRate);

                } else if ("4115".equalsIgnoreCase(inquiryTxCode)) {
                    execRecord.setTransactionRate(4, transactionRate);

                } else if ("4116".equalsIgnoreCase(inquiryTxCode)) {
                    execRecord.setTransactionRate(5, transactionRate);

                } else if ("4121".equalsIgnoreCase(inquiryTxCode)) {
                    execRecord.setTransactionRate(6, transactionRate);

                } else if ("4122".equalsIgnoreCase(inquiryTxCode)) {
                    execRecord.setTransactionRate(7, transactionRate);

                } else if ("4123".equalsIgnoreCase(inquiryTxCode)) {
                    execRecord.setTransactionRate(8, transactionRate);

                } else if ("4124".equalsIgnoreCase(inquiryTxCode)) {
                    execRecord.setTransactionRate(9, transactionRate);

                } else if ("4132".equalsIgnoreCase(inquiryTxCode)) {
                	execRecord.setTransactionRate(10, transactionRate);

                } else if ("4133".equalsIgnoreCase(inquiryTxCode)) {
                	execRecord.setTransactionRate(11, transactionRate);

                } else if ("4135".equalsIgnoreCase(inquiryTxCode)) {
                	execRecord.setTransactionRate(12, transactionRate);

                } else if ("4136".equalsIgnoreCase(inquiryTxCode)) {
                	execRecord.setTransactionRate(13, transactionRate);

                } else {
                    setInvalidType(true);
                }
            }
            int t3 = 0;
            int t4 = 0;
            try {
                t3 = Integer.parseInt(Character.toString(inquiryTxCode.charAt(2)));
                t4 = Integer.parseInt(Character.toString(inquiryTxCode.charAt(3)));
                if (!"4".equalsIgnoreCase(Character.toString(inquiryTxCode.charAt(0))) || !"1".equalsIgnoreCase(Character.toString(inquiryTxCode.charAt(1)))) {
                    // TX_CODE 未知的。所以不給他計算。
                    t3 = 0;
                }
            } catch (NumberFormatException nfe) {
                System.out.println("[" + currentInquiry.getInquiryDate() + ", " + currentInquiry.getInquiryChargeBankId() + "]:，未知的交易型態：" + inquiryTxCode);
            } catch (NullPointerException e) {
                System.out.println("[" + currentInquiry.getInquiryDate() + ", " + currentInquiry.getInquiryChargeBankId() + "]:，交易型態為空值：" + inquiryTxCode);
            }
            switch (t3) {
            case 1: // 4111, 4112, 4113
                switch (t4) {
                case 1:
                case 2:
                case 3:
                    if (countThisFlag && !(currentInquiry.getInquiryCacheFlag())) {
                        execRecord.setSum1(t4, execRecord.getSum1(t4) + currentInquiry.getRowSummary());
                        execRecord.setSum1(0, execRecord.getSum1(0) + 1); // 把 sum1[0] 挪為 4111, 4112, 4113 小計用
                        tempTotalRows[0] += currentInquiry.getTotalRowSummary();
                    } else if (countThisFlag) {
                        execRecord.setCacheRows(0, execRecord.getCacheRows(0) + currentInquiry.getTotalRowSummary());
                    }
                    break;
                case 4: // 4114, 4115, 4116
                case 5:
                case 6:
                    if (countThisFlag && !(currentInquiry.getInquiryCacheFlag())) {
                        execRecord.setSum1(t4, execRecord.getSum1(t4) + currentInquiry.getRowSummary());
                        execRecord.setSum1(7, execRecord.getSum1(7) + 1); // 把 sum1[7] 挪為 4114, 4115, 4116 小計用
                        tempTotalRows[1] += currentInquiry.getTotalRowSummary();
                    } else if (countThisFlag) {
                        execRecord.setCacheRows(1, execRecord.getCacheRows(1) + currentInquiry.getTotalRowSummary());
                    }
                    break;
                default:
                    continue;
                }
                break;
            case 2: // 4121, 4122
                switch (t4) {
                case 1:
                case 2:
                    if (countThisFlag && !(currentInquiry.getInquiryCacheFlag())) {
                        execRecord.setSum2(t4, execRecord.getSum2(t4) + currentInquiry.getRowSummary());
                        execRecord.setSum2(0, execRecord.getSum2(0) + 1); // 把 sum2[0] 挪為 4121, 4122 小計用
                        tempTotalRows[2] += currentInquiry.getTotalRowSummary();
                    } else if (countThisFlag) {
                        execRecord.setCacheRows(2, execRecord.getCacheRows(2) + currentInquiry.getTotalRowSummary());
                    }
                    break;
                case 3: // 4123, 4124
                case 4:
                    if (countThisFlag && !(currentInquiry.getInquiryCacheFlag())) {
                        execRecord.setSum2(t4, execRecord.getSum2(t4) + currentInquiry.getRowSummary());
                        execRecord.setSum2(5, execRecord.getSum2(5) + 1); // 把 sum2[5] 挪為 4123, 4124 小計用
                        tempTotalRows[3] += currentInquiry.getTotalRowSummary();
                    } else if (countThisFlag) {
                        execRecord.setCacheRows(3, execRecord.getCacheRows(3) + currentInquiry.getTotalRowSummary());
                    }
                    break;
                default:
                    continue;
                }
                break;
            case 3: // 4132, 4133
                switch (t4) {
                case 2:
                case 3:
                    if (countThisFlag && !(currentInquiry.getInquiryCacheFlag())) {
                        execRecord.setSum3(t4 - 1, execRecord.getSum3(t4 - 1) + currentInquiry.getRowSummary());
                        execRecord.setSum3(0, execRecord.getSum3(0) + 1); // 把 sum3[0] 挪為 4132, 4133 小計用
                        tempTotalRows[4] += currentInquiry.getTotalRowSummary();
                    } else if (countThisFlag) {
                        execRecord.setCacheRows(4, execRecord.getCacheRows(4) + currentInquiry.getTotalRowSummary());
                    }
                    break;
                case 5: // 4135, 4136
                case 6:
                    if (countThisFlag && !(currentInquiry.getInquiryCacheFlag())) {
                        execRecord.setSum3(t4 - 2, execRecord.getSum3(t4 - 2) + currentInquiry.getRowSummary());
                        execRecord.setSum3(5, execRecord.getSum3(5) + 1); // 把 sum3[5] 挪為 4135, 4136 小計用
                        tempTotalRows[5] += currentInquiry.getTotalRowSummary();
                    } else if (countThisFlag) {
                        execRecord.setCacheRows(5, execRecord.getCacheRows(5) + currentInquiry.getTotalRowSummary());
                    }
                    break;
                default:
                    continue;
                }
                break;
            default:
                continue;
            }
            for (int i = 0; i < InquiryLogDto.CACHE_ARRAY_SIZE; i++) {
                execRecord.setTotalRows(i, tempTotalRows[i]);
            }

            /*
             * //加上最後一筆的totalRows if(chargeInquiryList.indexOf(currentInquiry) == chargeInquiryList.size()-1 ){ System.out.println("!!:: this is go through add last one record."); System.out.println(
             * " "); for (int i = 0; i < InquiryLogDto.CACHE_ARRAY_SIZE; i++) { System.out.println(tempTotalRows[i]); System.out.println(execRecord.getChargeBankShortDesc()+
             * execRecord.getTotalRows(i)); execRecord.setTotalRows(i, tempTotalRows[i]); //System.arraycopy(tempTotalRows, 0, execRecord.getTotalRows(), 0, execRecord.getTotalRows().length);
             * System.out.println(execRecord.getChargeBankShortDesc()+ execRecord.getTotalRows(i)); } //************For Test ************************************** // for (InquiryLogDto a:result){ //
             * System.out.print(a.getChargeBankShortDesc()); // System.out.print(" not charge: "); // for (int ij=0; ij<a.getTotalRows().length; ij++) // System.out.print(a.getTotalRows(ij)+ " "); //
             * // System.out.print("each 4 type: "+ a.getSum1(0)+ " "); // System.out.print(a.getSum1(7)+ " "); // System.out.print(a.getSum2(0)+ " "); // System.out.print(a.getSum2(5)+ " "); // //
             * System.out.print("cache: "); // for (int ij=0; ij<a.getCacheRows().length; ij++) // System.out.print(a.getCacheRows(ij)+ " "); // System.out.println(" "); // } //************For Test
             * ************************************** }
             */
            // 算total
            double totalsFirst = calculateFee(1, execRecord.getSum1(), execRecord.getTransactionRate());
            double totalsSecond = calculateFee(2, execRecord.getSum1(), execRecord.getTransactionRate());
            double totalsOne = calculateFee(3, execRecord.getSum2(), execRecord.getTransactionRate());
            double totalsTwo = calculateFee(4, execRecord.getSum2(), execRecord.getTransactionRate());
            double totalsObuOne = calculateFee(5, execRecord.getSum3(), execRecord.getTransactionRate());
            double totalsObuTwo = calculateFee(6, execRecord.getSum3(), execRecord.getTransactionRate());
            execRecord.setTotalsFirst(totalsFirst);
            execRecord.setTotalsSecond(totalsSecond);
            execRecord.setTotalsOne(totalsOne);
            execRecord.setTotalsTwo(totalsTwo);
            execRecord.setTotalsObuOne(totalsObuOne);
            execRecord.setTotalsObuTwo(totalsObuTwo);

        }
        /*
         * //************For Test ************************************** for (InquiryLogDto a:result){ System.out.print(a.getChargeBankShortDesc()); System.out.print(" not charge: "); for (int i=0;
         * i<a.getTotalRows().length; i++) System.out.print(a.getTotalRows(i)+ " ");
         * 
         * System.out.print("each 4 type: "+ a.getSum1(0)+ " "); System.out.print(a.getSum1(7)+ " "); System.out.print(a.getSum2(0)+ " "); System.out.print(a.getSum2(5)+ " ");
         * 
         * System.out.print("cache: "); for (int i=0; i<a.getCacheRows().length; i++) System.out.print(a.getCacheRows(i)+ " "); System.out.println(" "); } //************For Test
         * **************************************
         */ return result;
    }

    public List<InquiryLogDto> showAllBank(List<InquiryLogDto> chargeInquiryList) {
        // 放最後結果的，包含有月收費彙整的，跟沒有月收費彙整的付費銀行。

        List<InquiryLogDto> newChargeInquiryList = new ArrayList<InquiryLogDto>();
        // 取得所有分行。
        List<QueryBank> formQueryBankList = (List<QueryBank>) queryBankFactory.getQueryBankList();
        // 取得所有付費銀行(在InquiryLog裡面的)
        List<InquiryLog> allBankList = getChargeBankList();
        // 將每個銀行都變成空的。
        for (InquiryLog currentBank : allBankList) {

            // 在web查詢條件下，用來當做沒有月收費彙整資料的回覆資料。
            InquiryLogDto nonInquiry = new InquiryLogDto();

            for (QueryBank queryBank : formQueryBankList) {
                if (queryBank.getChargeBankId().equalsIgnoreCase(currentBank.getInquiryChargeBankId())) {
                    nonInquiry.setBankName(queryBank.getChargeBankName());
                    break;
                }
            }
            nonInquiry.setInquiryChargeBankId(currentBank.getInquiryChargeBankId());
            nonInquiry.setTotalsFirst(0.0f);
            nonInquiry.setTotalsTwo(0.0f);
            nonInquiry.setTotalsOne(0.0f);
            nonInquiry.setTotalsSecond(0.0f);
            nonInquiry.setTotalsObuOne(0.0f);
            nonInquiry.setTotalsObuTwo(0.0f);
            nonInquiry.setSum1(0, 0);
            nonInquiry.setSum1(1, 0);
            nonInquiry.setSum1(2, 0);
            nonInquiry.setSum1(3, 0);
            nonInquiry.setSum1(4, 0);
            nonInquiry.setSum1(5, 0);
            nonInquiry.setSum1(6, 0);
            nonInquiry.setSum1(7, 0);
            nonInquiry.setSum2(0, 0);
            nonInquiry.setSum2(1, 0);
            nonInquiry.setSum2(2, 0);
            nonInquiry.setSum2(3, 0);
            nonInquiry.setSum2(4, 0);
            nonInquiry.setSum2(5, 0);
            nonInquiry.setSum3(0, 0);
            nonInquiry.setSum3(1, 0);
            nonInquiry.setSum3(2, 0);
            nonInquiry.setSum3(3, 0);
            nonInquiry.setSum3(4, 0);
            nonInquiry.setSum3(5, 0);

            nonInquiry.setTotalRows(0, 0);
            nonInquiry.setTotalRows(1, 0);
            nonInquiry.setTotalRows(2, 0);
            nonInquiry.setTotalRows(3, 0);
            nonInquiry.setTotalRows(4, 0);
            nonInquiry.setTotalRows(5, 0);
            nonInquiry.setCacheRows(0, 0);
            nonInquiry.setCacheRows(1, 0);
            nonInquiry.setCacheRows(2, 0);
            nonInquiry.setCacheRows(3, 0);
            nonInquiry.setCacheRows(4, 0);
            nonInquiry.setCacheRows(5, 0);
            newChargeInquiryList.add(nonInquiry);

        }
        // 將web查詢條件下，有月收費彙整資料的回傳資料，設給newChargeInquiryLIst。
        for (InquiryLogDto currentInquiry : chargeInquiryList) {
            // System.out.println(currentInquiry.getInquiryChargeBankId()+", ");
            for (InquiryLogDto everyInquiryBank : newChargeInquiryList) {
                // System.out.println( ( everyInquiryBank.getInquiryChargeBankId() == null)?"":everyInquiryBank.getInquiryChargeBankId().toString());

                try {
                    // 有資料的雖然找不到付費分行資訊，我還是列出來。
                    if (currentInquiry.getInquiryChargeBankId().toString().equalsIgnoreCase(everyInquiryBank.getInquiryChargeBankId().toString())) {
                        everyInquiryBank.setBankName(currentInquiry.getBankName());
                        System.arraycopy(currentInquiry.getSum1(), 0, everyInquiryBank.getSum1(), 0, everyInquiryBank.getSum1().length);
                        System.arraycopy(currentInquiry.getSum2(), 0, everyInquiryBank.getSum2(), 0, everyInquiryBank.getSum2().length);
                        System.arraycopy(currentInquiry.getSum3(), 0, everyInquiryBank.getSum3(), 0, everyInquiryBank.getSum3().length);
                        System.arraycopy(currentInquiry.getTotalRows(), 0, everyInquiryBank.getTotalRows(), 0, everyInquiryBank.getTotalRows().length);
                        System.arraycopy(currentInquiry.getCacheRows(), 0, everyInquiryBank.getCacheRows(), 0, everyInquiryBank.getCacheRows().length);
                        /*
                         * //************For Test **************************************
                         * 
                         * System.out.print(everyInquiryBank.getChargeBankShortDesc()); System.out.print(" not charge: "); for (int i=0; i<everyInquiryBank.getTotalRows().length; i++)
                         * System.out.print(everyInquiryBank.getTotalRows(i)+ " ");
                         * 
                         * System.out.print("each 4 type: "+ everyInquiryBank.getSum1(0)+ " "); System.out.print(everyInquiryBank.getSum1(7)+ " "); System.out.print(everyInquiryBank.getSum2(0)+ " ");
                         * System.out.print(everyInquiryBank.getSum2(5)+ " ");
                         * 
                         * System.out.print("cache: "); for (int i=0; i<everyInquiryBank.getCacheRows().length; i++) System.out.print(everyInquiryBank.getCacheRows(i)+ " "); System.out.println(
                         * " showAll "); //************For Test **************************************
                         */
                        everyInquiryBank.setTotalsFirst(currentInquiry.getTotalsFirst());
                        everyInquiryBank.setTotalsTwo(currentInquiry.getTotalsTwo());
                        everyInquiryBank.setTotalsSecond(currentInquiry.getTotalsSecond());
                        everyInquiryBank.setTotalsOne(currentInquiry.getTotalsOne());
                        everyInquiryBank.setTotalsObuOne(currentInquiry.getTotalsObuOne());
                        everyInquiryBank.setTotalsObuTwo(currentInquiry.getTotalsObuTwo());
                    }
                } catch (NullPointerException npe) {
                    logger.debug(npe.getMessage());
                } catch (Exception e) {
                    logger.debug(e.getMessage());
                }
            }
        }
        return newChargeInquiryList;
    }
}
