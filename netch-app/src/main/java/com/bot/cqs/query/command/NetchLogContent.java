package com.bot.cqs.query.command;

import java.util.List;

import com.iisigroup.cap.utils.CapAppContext;

/**
 * 
 * @author bob peng
 *
 */
public class NetchLogContent {

    List<Object> content;
    String returnMsg;

    public NetchLogContent(List<Object> content) {
        this.content = content;
        this.returnMsg = CapAppContext.getMessage("common.completed");
    }
    
    public NetchLogContent(List<Object> content, String successMsg) {
        this.content = content;
        this.returnMsg = successMsg;
    }

    public List<Object> getContent() {
        return content;
    }

    public void setContent(List<Object> content) {
        this.content = content;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

}
