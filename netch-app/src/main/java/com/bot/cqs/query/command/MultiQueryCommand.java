
package com.bot.cqs.query.command;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.iisigroup.cap.model.GenericBean;

/**
 * 票信查詢 (網頁) 對應的 command object.
 * 
 * @author Damon Lu
 */
@Component
public class MultiQueryCommand extends GenericBean{

    public static final int MAX_QUERY_COUNT = 10;

    public String action;
    public String inputTransactionId;
    public String transactionId;

    /**
     * 取得對應於網頁的 "action" 欄位
     * 
     * @return
     */
    public String getAction() {

        return action;
    }

    /**
     * @return
     */
    public void setAction(String action) {

        this.action = action;
    }

    /**
     * 取得對應於網頁的 "inputTransactionId" 欄位
     * 
     * @return
     */
    public String getInputTransactionId() {

        return inputTransactionId;
    }

    public void setInputTransactionId(String inputTransactionId) {

        this.inputTransactionId = inputTransactionId;
    }

    /**
     * 取得 Transaction ID, 這將在呼叫 {@link #parseInputTransactionId()} 後, 解析 {@link #getInputTransactionId()} 的值而產生
     * 
     * @return
     * @see #getInputTransactionId()
     * @see #parseInputTransactionId()
     */
    public String getTransactionId() {

        return transactionId;
    }

    /**
     * 
     * @param transactionId
     */
    public void setTransactionId(String transactionId) {

        this.transactionId = transactionId;
    }

    /**
     * 這個 method 會將 {@link #getInputTransactionId()} 的值中抽出 transaction ID. 這裡假設 {@link #getInputTransactionId()} 為 4 位 transaction ID + 1 位空白 + 任意長度 transaction description.
     *
     */
    public void parseInputTransactionId() {

        if (StringUtils.hasText(getInputTransactionId())) {

            int pos = getInputTransactionId().indexOf(' ');
            if (pos == -1)
                setTransactionId(getInputTransactionId());
            else
                setTransactionId(getInputTransactionId().substring(0, pos));

        }
    }
}
