
package com.bot.cqs.query.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.bot.cqs.query.util.DateUtil;

@Embeddable
public class TransactionRateKey implements Serializable {

    private String transactionId;
    private Date transactionRateEffectDate;
    private int[] effectDateAttr;

    @Column(name = "trans_id", columnDefinition = "char", length = 4, nullable = false)
    public String getTransactionId() {

        return transactionId;
    }

    public void setTransactionId(String transactionId) {

        this.transactionId = transactionId;
    }

    @Column(name = "trans_rate_effect_date", nullable = false)
    public Date getTransactionRateEffectDate() {

        return transactionRateEffectDate;
    }

    public void setTransactionRateEffectDate(Date transactionRateEffectDate) {

        this.transactionRateEffectDate = transactionRateEffectDate;
        setEffectDateAttr(DateUtil.getRocAttributes(transactionRateEffectDate));
    }

    @Transient
    public int[] getEffectDateAttr() {

        return effectDateAttr;
    }

    public void setEffectDateAttr(int[] effectDateAttr) {

        this.effectDateAttr = effectDateAttr;
    }

    @Transient
    public String getEffectDateRocYMD() {

        return DateUtil.toRocDate(getTransactionRateEffectDate());
    }

    @Transient
    public String getEffectDateRocYM() {

        return DateUtil.toRocYearMonth(getTransactionRateEffectDate());
    }

    @Transient
    public String getEffectDateYear() {

        return String.valueOf(getEffectDateAttr()[0]);
    }

    @Transient
    public String getEffectDateRocYear() {

        return String.valueOf(getEffectDateAttr()[0] - 1911);
    }

    @Transient
    public String getEffectDateMonth() {

        return String.valueOf(getEffectDateAttr()[1]);
    }

    @Transient
    public String getEffectDateDay() {

        return String.valueOf(getEffectDateAttr()[2]);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null)
            return false;

        if (!(obj instanceof TransactionRateKey))
            return false;

        TransactionRateKey key = (TransactionRateKey) obj;
        return new EqualsBuilder().append(getTransactionId(), key.getTransactionId()).append(getTransactionRateEffectDate(), key.getTransactionRateEffectDate()).isEquals();
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder().append(getTransactionId()).append(getTransactionRateEffectDate()).toHashCode();
    }

}
