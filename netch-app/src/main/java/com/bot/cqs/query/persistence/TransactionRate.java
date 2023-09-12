
package com.bot.cqs.query.persistence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.context.support.ResourceBundleMessageSource;

import com.bot.cqs.query.util.DateUtil;
import com.iisigroup.cap.model.GenericBean;
import com.iisigroup.cap.utils.CapAppContext;

@Entity
@Table(name = "TransactionRate")
public class TransactionRate  extends GenericBean{

    private static final ResourceBundleMessageSource DEFAULT_MESSAGE_SOURCE = new ResourceBundleMessageSource();
    public static final String DEFAULT_MESSAGE_SOURCE_BASENAME = "classpath:/i18n/transactionRateMessage_zh_TW";

    public static final String TRANSACTION_TYPE_PREFIX = TransactionRate.class.getSimpleName() + "_TYPE_";
    public static final String TRANSACTION_NAME_PREFIX = TransactionRate.class.getSimpleName() + "_NAME_";

    public static final List<TransactionRate> AVAILABLE_TRANSACTION_RATE = new ArrayList<TransactionRate>();

    private TransactionRateKey key;
    private double transactionRate;
    private double transactionPoundage;
    private int transactionRecordsAtDiscount;
    private double transactionDiscountRate;
    private int version;

    static {
        DEFAULT_MESSAGE_SOURCE.setBasename(DEFAULT_MESSAGE_SOURCE_BASENAME);
    }

    @EmbeddedId
    public TransactionRateKey getKey() {

        return key;
    }

    public void setKey(TransactionRateKey key) {

        this.key = key;
    }

    @Column(name = "trans_discount_rate", columnDefinition = "float", nullable = false)
    public double getTransactionDiscountRate() {

        return transactionDiscountRate;
    }

    public void setTransactionDiscountRate(double transactionDiscountRate) {

        this.transactionDiscountRate = transactionDiscountRate;
    }

    @Column(name = "trans_poundage", columnDefinition = "float", nullable = false)
    public double getTransactionPoundage() {

        return transactionPoundage;
    }

    public void setTransactionPoundage(double transactionPoundage) {

        this.transactionPoundage = transactionPoundage;
    }

    @Column(name = "trans_rate", columnDefinition = "float", nullable = false)
    public double getTransactionRate() {

        return transactionRate;
    }

    public void setTransactionRate(double transactionRate) {

        this.transactionRate = transactionRate;
    }

    @Column(name = "trans_records_at_discount", nullable = false)
    public int getTransactionRecordsAtDiscount() {

        return transactionRecordsAtDiscount;
    }

    public void setTransactionRecordsAtDiscount(int transactionRecordsAtDiscount) {

        this.transactionRecordsAtDiscount = transactionRecordsAtDiscount;
    }

    @Column(name = "version", nullable = false)
    public int getVersion() {

        return version;
    }

    public void setVersion(int version) {

        this.version = version;
    }

    @Transient
    public String getTransactionName() {

        if (getKey() == null || getKey().getTransactionId() == null)
            return null;

        return getTransactionName(getKey().getTransactionId());
    }

    public static String getTransactionName(String transactionId) {

        return CapAppContext.getMessage(TRANSACTION_NAME_PREFIX + transactionId);
    }

    @Transient
    public String getTransactionType() {

        if (getKey() == null || getKey().getTransactionId() == null)
            return null;

        return getTransactionType(getKey().getTransactionId());

    }

    public static String getTransactionType(String transactionId) {
        return CapAppContext.getMessage(TRANSACTION_TYPE_PREFIX + transactionId);
    }

    @Transient
    public String getTransactionShortDesc() {

        return getTransactionShortDesc(getKey().getTransactionId());
    }

    public static String getTransactionShortDesc(String transactionId) {

        return transactionId + " " + getTransactionName(transactionId);
    }

    public static boolean isTransactionIdValid(String transactionId) {

        if (transactionId == null)
            return false;

        for (TransactionRate rate : AVAILABLE_TRANSACTION_RATE) {
            if (rate.key.getTransactionId().equals(transactionId))
                return true;
        }
        return false;
    }

    @Transient
    public boolean isModifiable() {

        return isModifiable(this);
    }

    public static boolean isModifiable(TransactionRate rate) {

        if (rate == null)
            return false;
        if (rate.getKey() == null)
            return false;
        if (rate.getKey().getTransactionRateEffectDate() == null)
            return false;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        long baseTime = DateUtil.toMinTime(calendar.getTime()).getTime();
        long currentTime = rate.getKey().getTransactionRateEffectDate().getTime();
        return currentTime >= baseTime;
    }
}
