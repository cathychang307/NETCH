package com.bot.cqs.query.dto;

import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.query.persistence.TransactionRate;

import java.util.Date;

/**
 * 承接帳務報表-月收費彙整的DTO物件。
 * 
 * @author sunkistwang
 * @since 2007/08/02 v1.0
 */
public class InquiryLogDto extends InquiryLog {

    private static final int ARRAY_SIZE_1 = 8; // 一、二類下的 6 個子類別，再加上一、二類的小計，所以大小為 8。 ex: [ 一類小計, 一類4111, 一類4112, 一類4113, 二類4114, 二類4115, 二類4116, 二類小計]

    private static final int ARRAY_SIZE_2 = 6; // 甲、乙類下的 4 個子類別，再加上甲、乙類的小計，所以大小為 6。 ex: [ 甲類小計, 甲類4121, 甲類4122, 乙類4113, 乙類4114, 乙類小計]

    private static final int ARRAY_SIZE_3 = 6; // OBU一、OBU二類下的 4 個子類別，再加上OBU一、OBU二類的小計，所以大小為 6。 ex: [ OBU一類小計, OBU一類4132, OBU一類4133, OBU二類4135, OBU二類4136, OBU二類小計]

    private static final int AMOUNT_ARRAY_SIZE = 14;

    private static final int TRANSACTION_RATE_ARRAY_SIZE = 14; // 共 14 個子類別的費率設定

    public static final int CACHE_ARRAY_SIZE = 6; // 一、二、甲、乙、OBU一、OBU二類 6 種小計

    private TransactionRate[] transactionRate = null;

    private Date inputDateFrom;
    private int finalCacheRow;
    private Date inputDateTo;

    private String inputChargeBankId;

    private int rowSummary;
    private int totalRowSummary;
    /*
     * inquiry_cache_flag =0中的相同條件(不計費)的筆數。
     */
    private int[] totalRows;
    /*
     * inquiry_cache_flag 非0的cache筆數
     */
    private int[] cacheRows;

    /*
     * 一類含 (4111, 4112, 4113)、二類含 (4113, 4114, 4115) 使用
     * 此陣列內容是一類、二類下的 6 個子類別，再加上一類、二類的小計
     * ex: [ 一類小計, 一類4111, 一類4112, 一類4113, 二類4114, 二類4115, 二類4116, 二類小計]
     */
    private int[] sum1 = null;

    /*
     * 甲類含 (4121, 4122)、乙類含 (4123, 4124) 使用
     * 此陣列內容是甲類、乙類下的 4 個子類別，再加上甲類、乙類的小計
     * ex: [ 甲類小計, 甲類4121, 甲類4122, 乙類4113, 乙類4114, 乙類小計]
     */
    private int[] sum2 = null;

    /*
     * OBU一類含 (4132, 4133)、OBU二類含 (4135, 4136) 使用
     * 此陣列內容是 OBU一類、OBU二類下的 4 個子類別，再加上OBU一類、OBU二類的小計
     * ex: [ OBU一類小計, OBU一類4132, OBU一類4133, OBU二類4135, OBU二類4136, OBU二類小計]
     */
    private int[] sum3 = null;

    private String bankName;

    private float[] amount;

    private double totalsFirst;
    private double totalsSecond;
    private double totalsOne;
    private double totalsTwo;
    private double totalsObuOne;
    private double totalsObuTwo;

    /**
     * 初始一些陣列放置計算要使用的數值。
     *
     */
    public InquiryLogDto() {
        sum1 = new int[ARRAY_SIZE_1];
        sum2 = new int[ARRAY_SIZE_2];
        sum3 = new int[ARRAY_SIZE_3];
        amount = new float[AMOUNT_ARRAY_SIZE];
        transactionRate = new TransactionRate[TRANSACTION_RATE_ARRAY_SIZE];
        totalRows = new int[CACHE_ARRAY_SIZE];
        cacheRows = new int[CACHE_ARRAY_SIZE];
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public float[] getAmount() {
        return amount;
    }

    public void setAmount(float[] amount) {
        this.amount = amount;
    }

    public int[] getSum1() {
        return sum1;
    }

    public void setSum1(int[] sum) {
        this.sum1 = sum;
    }

    public float getAmount(int pos) {
        return amount[pos];
    }

    public void setAmount(int pos, float value) {
        this.amount[pos] = (float) value;
    }

    public void setSum1(int pos, int value) {
        if (ARRAY_SIZE_1 > pos)
            sum1[pos] = value;
    }

    public int getSum1(int pos) {
        if (ARRAY_SIZE_1 <= pos)
            return -1;
        return sum1[pos];
    }

    public int[] getSum2() {
        return sum2;
    }

    public void setSum2(int[] sum) {
        this.sum2 = sum;
    }

    public void setSum2(int pos, int value) {
        if (ARRAY_SIZE_2 > pos)
            sum2[pos] = value;
    }

    public int getSum2(int pos) {
        if (ARRAY_SIZE_2 <= pos)
            return -1;
        return sum2[pos];
    }

    public int[] getSum3() {
        return sum3;
    }

    public void setSum3(int[] sum) {
        this.sum3 = sum;
    }

    public void setSum3(int pos, int value) {
        if (ARRAY_SIZE_3 > pos)
            sum3[pos] = value;
    }

    public int getSum3(int pos) {
        if (ARRAY_SIZE_3 <= pos)
            return -1;
        return sum3[pos];
    }

    public int getRowSummary() {
        return rowSummary;
    }

    public void setRowSummary(int rowSummary) {
        this.rowSummary = rowSummary;
    }

    public String getInputChargeBankId() {

        return inputChargeBankId;
    }

    public void setInChargeBankId(String inputChargeBankId) {

        this.inputChargeBankId = inputChargeBankId;
    }

    public Date getInputDateFrom() {

        return inputDateFrom;
    }

    public void setInputDateFrom(Date inputDateFrom) {

        this.inputDateFrom = inputDateFrom;
    }

    public Date getInputDateTo() {

        return inputDateTo;
    }

    public void setInputDateTo(Date inputDateTo) {

        this.inputDateTo = inputDateTo;
    }

    public boolean isChargeBankEqual(InquiryLogDto inquiryLogDto) {

        if (inquiryLogDto == null)
            return false;

        if (getInquiryChargeBankId() == null) {
            if (inquiryLogDto.getInquiryChargeBankId() == null)
                return true;
            else
                return false;
        } else {
            if (inquiryLogDto.getInquiryChargeBankId() == null)
                return false;
            else
                return getInquiryChargeBankId().equals(inquiryLogDto.getInquiryChargeBankId());
        }

    }

    public double getTotalsFirst() {
        return totalsFirst;
    }

    public void setTotalsFirst(double totalsFirst) {
        this.totalsFirst = totalsFirst;
    }

    public double getTotalsOne() {
        return totalsOne;
    }

    public void setTotalsOne(double totalsOne) {
        this.totalsOne = totalsOne;
    }

    public double getTotalsObuOne() {
        return totalsObuOne;
    }

    public void setTotalsObuOne(double totalsObuOne) {
        this.totalsObuOne = totalsObuOne;
    }

    public double getTotalsSecond() {
        return totalsSecond;
    }

    public void setTotalsSecond(double totalsSecond) {
        this.totalsSecond = totalsSecond;
    }

    public double getTotalsTwo() {
        return totalsTwo;
    }

    public void setTotalsTwo(double totalsTwo) {
        this.totalsTwo = totalsTwo;
    }

    public double getTotalsObuTwo() {
        return totalsObuTwo;
    }

    public void setTotalsObuTwo(double totalsObuTwo) {
        this.totalsObuTwo = totalsObuTwo;
    }

    public TransactionRate[] getTransactionRate() {
        return transactionRate;
    }

    public void setTransactionRate(TransactionRate[] transactionRate) {
        this.transactionRate = transactionRate;
    }

    public TransactionRate getTransactionRate(int pos) {
        return transactionRate[pos];
    }

    public void setTransactionRate(int pos, TransactionRate transactionRate) {
        this.transactionRate[pos] = transactionRate;
    }

    public int[] getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int[] totalRows) {

        this.totalRows = totalRows;
    }

    public int[] getCacheRows() {
        return cacheRows;
    }

    public void setCacheRows(int[] cacheRows) {

        this.cacheRows = cacheRows;
    }

    public int getCacheRows(int pos) {
        return cacheRows[pos];
    }

    public void setCacheRows(int pos, int cacheRow) {
        this.cacheRows[pos] = cacheRow;
    }

    public int getTotalRows(int pos) {
        return totalRows[pos];
    }

    /**
     * 未統計到 inquiry_cache_flag 非0的筆數。
     * 
     * @param pos
     * @param totalRows
     * @return
     */
    public int getFinalCacheRow(int pos, int totalRows) {
        finalCacheRow = 0;
        switch (pos) {
        case 0:
            finalCacheRow = totalRows - sum1[0];
            break;
        case 1:
            finalCacheRow = totalRows - sum1[7];
            break;
        case 2:
            finalCacheRow = totalRows - sum2[0];
            break;
        case 3:
            finalCacheRow = totalRows - sum2[5];
            break;
        case 4:
            finalCacheRow = totalRows - sum3[0];
            break;
        case 5:
            finalCacheRow = totalRows - sum3[5];
            break;
        default:
            break;
        }
        return finalCacheRow < 0 ? 0 : finalCacheRow;
    }

    public void setTotalRows(int pos, int totalRows) {
        this.totalRows[pos] = getFinalCacheRow(pos, totalRows);
    }

    public int getTotalRowSummary() {
        return totalRowSummary;
    }

    public void setTotalRowSummary(int totalRowSummary) {
        this.totalRowSummary = totalRowSummary;
    }

    /**
     * 轉成DB的資料型別boolean.
     * 
     * @param cacheFlag
     *            非零為false。
     */
    public void setInquiryCacheFlag(int cacheFlag) {
        if (cacheFlag == 0)
            super.setInquiryCacheFlag(false);
        else
            super.setInquiryCacheFlag(true);
    }
}
