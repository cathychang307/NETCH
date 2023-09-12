package com.bot.cqs.report.service;

import java.io.ByteArrayOutputStream;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.exception.CapException;

/**
 * <pre>
 * IJasperPdfService
 * </pre>
 * 
 * @since 2017年1月5日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2017年1月5日,bob peng,new
 *          </ul>
 */
public interface IJasperPdfService {

    /**
     * 報表產生的共同進入方法，帶入reportParameter，產生ByteArray的報表檔案。
     * 
     * @param params
     *            PageParameters
     * @param comp
     *            Component
     * @return ByteArrayOutputStream
     * @throws CapException
     */
    ByteArrayOutputStream generateReport(Request request) throws CapException;

    /**
     * Gets the report definition.
     * 
     * @return the report definition
     */
    public abstract String getReportDefinition();

}
