/* 
 * ResponseContent.java
 * 
 * Copyright (c) 2017 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.bot.cqs.query.dto;

import com.bot.cqs.query.persistence.ApplicationParameter;

/**
 * <pre>
 * Response Content
 * </pre>
 * 
 * @since 2017年1月17日
 * @author Sunkist
 * @version
 *          <ul>
 *          <li>2017年1月17日,Sunkist,new
 *          </ul>
 */
public class ResponseContent {
    boolean isErrorOccurs;
    String responseMessage;
    Object inputValue;
    ApplicationParameter parameter;

    public ResponseContent(boolean isError, String responseText, Object inputValue) {
        this.isErrorOccurs = isError;
        this.responseMessage = responseText;
        this.inputValue = inputValue;
    }

    public ApplicationParameter getParameter() {
        return parameter;
    }

    public void setParameter(ApplicationParameter parameter) {
        this.parameter = parameter;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public boolean isErrorOccurs() {
        return isErrorOccurs;
    }

    public void setErrorOccurs(boolean isErrorOccurs) {
        this.isErrorOccurs = isErrorOccurs;
    }

    public Object getInputValue() {
        return inputValue;
    }

    public void setInputValue(Object inputValue) {
        this.inputValue = inputValue;
    }

}
