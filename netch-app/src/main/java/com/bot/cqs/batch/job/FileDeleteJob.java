package com.bot.cqs.batch.job;

/* 
 * FileDeleteJob.java
 * 
 * Copyright (c) 2013-2014 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */

import java.io.File;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.cap.utils.CapString;

/**
 * <pre>
 * File cleaner Quartz Job.
 * </pre>
 * 
 * @since 2017年1月4日
 * @author Sunkist
 * @version
 *          <ul>
 *          <li>2017年1月4日,Sunkist,new
 *          </ul>
 */
public class FileDeleteJob {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String FOLDER_DATE_FORMAT = "yyyy.MM.dd";

    public void clean(String delDaysBefore, String tempPath) {
        File dir;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("=== Batch FileDeleteJob Start ===");
            }
            dir = new File(tempPath);
            String tDay = CapDate.formatDate(CapDate.shiftDays(CapDate.getCurrentTimestamp(), -Integer.parseInt(delDaysBefore)), FOLDER_DATE_FORMAT);

            if (dir.exists() && !CapString.isEmpty(tDay)) {

                File[] files = dir.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        String tempFile = file.getName();
                        int compare = -1;
                        if (logger.isDebugEnabled()) {
                            logger.debug("processing folder [" + tempFile + "]");
                        }
                        try {
                            if (isValidDateFolder(tempFile, FOLDER_DATE_FORMAT)) {
                                compare = CapDate.calculateDays(tDay, tempFile, FOLDER_DATE_FORMAT);
                                if (compare >= 0) {
                                    // 刪除目錄
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("deleting...");
                                    }
                                    FileUtils.deleteDirectory(file);
                                }
                            }
                        } catch (Exception e) {
                            if (logger.isDebugEnabled()) {
                                logger.error(e.getMessage(), e);
                            }
                        } finally {
                            if (logger.isDebugEnabled()) {
                                logger.debug("process folder [" + tempFile + "] done");
                            }
                        }
                    }
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug(tempPath + " not exists or delete target day is null. batch not excute.");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("=== Batch FileDeleteJob End ===");
            }
        }
    }

    /**
     * 判斷資料夾格式是否為Date Format日期格式
     * 
     * @param folderName
     *            資料夾名
     * @param dateFormat
     *            日期格式
     * @return boolean
     */
    private boolean isValidDateFolder(String folderName, String dateFormat) {
        SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);
        try {
            fmt.parse(folderName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
