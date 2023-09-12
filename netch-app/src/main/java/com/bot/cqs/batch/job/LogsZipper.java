/* 
 * LogsZipper.java
 * 
 * Copyright (c) 2017 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.bot.cqs.batch.job;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.PropertyResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <pre>
 * TODO Write a short description on the purpose of the program
 * </pre>
 * 
 * @since 2017年1月6日
 * @author Sunkist
 * @version
 *          <ul>
 *          <li>2017年1月6日,Sunkist,copy from suip
 *          </ul>
 */
public class LogsZipper {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private int zipBufferLength = 1024;
    private String datePattern = "yyyy-MM-dd";
    private String logFileName = "Crypto.log";
    private String targetPath = "E:/@IISI/@Project/CTCB/backup";
    private String sourcePath = "E:/@IISI/@Project/CTCB/logs";
    private int zipCycle = 1;
    private boolean zipFlag = false;
    private int zipDaysAgo = 90;
    private ZipOutputStream zos;
    private int times = 0;
    Date dateDaysAgo = null;

    public LogsZipper() {
    }

    public LogsZipper(File config) throws Exception {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(config.getAbsoluteFile());
            PropertyResourceBundle prb = new PropertyResourceBundle(fis);
            this.zipBufferLength = Integer.parseInt(prb.getString("ZipBufferLength"));
            this.datePattern = prb.getString("DatePattern");
            this.logFileName = prb.getString("LogFileName");
            this.targetPath = prb.getString("TargetPath");
            this.sourcePath = prb.getString("SourcePath");
            this.zipCycle = Integer.parseInt(prb.getString("ZIPCycle"));
            this.zipFlag = Boolean.parseBoolean(prb.getString("ZIPFlag"));
            this.zipDaysAgo = Integer.parseInt(prb.getString("ZIPDaysAgo"));
            if (this.zipDaysAgo <= 0) {
                this.zipDaysAgo = 90;
            }
            this.dateDaysAgo = countDaysAgo(new Date());
        } catch (Exception e) {
            logger.debug(e.getMessage());
        } finally {
            if (fis != null) {
                safeClose(fis);
            }
        }
    }

    /**For test
    public static void main(String[] args) {
        LogsZipper slz = new LogsZipper();
        if (args.length > 0) {
            String inputFile = args[0];
            File f = new File(inputFile);
            if (f.exists()) {
                try {
                    slz = new LogsZipper(f);
                } catch (Exception e) {
                    logger.debug(e.getMessage());
                    System.exit(-1);
                }
            } else {
                System.out.println("Config file is not found. " + inputFile);
                System.exit(-1);
            }
        }
        slz.doZipJob();
    }*/

    public int doZipJob() {
        Date startZipDate = getFirstLogDate();
        if (startZipDate == null) {
            if (this.times == 0) {
                System.out.println("Don't need to backup logs.");
                return 1;
            }
            System.out.println("Task complete.");
            return 0;
        }
        Date endZipDate = countEndDate(startZipDate);
        if (endZipDate == null) {
            if (this.times == 0) {
                System.out.println("Don't need to backup logs.");
                return 1;
            }
            System.out.println("Backup complete.");
            return 0;
        }
        try {
            if (this.zipCycle == 1) {
                if (this.dateDaysAgo.compareTo(startZipDate) <= 0) {
                    return -1;
                }
            } else if (this.dateDaysAgo.compareTo(endZipDate) <= 0) {
                return -1;
            }
            System.out.println("The Start Date : " + startZipDate.toString());
            System.out.println("The End Date   : " + endZipDate.toString());
            zipFile(startZipDate, endZipDate);
            this.times += 1;
            doZipJob();
            return 0;
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        return -1;
    }

    public Date getFirstLogDate() {
        Date date = null;
        File f = new File(this.sourcePath);
        if (f.isDirectory()) {
            String[] files = f.list();
            String dateString = "";
            Date tmpDate = null;
            SimpleDateFormat sdf = new SimpleDateFormat(this.datePattern);
            String path = "\\d{4}-\\d{2}-\\d{2}";//YYYY-MM-DD
            Pattern p = Pattern.compile(path);
            for (int i = 0; i < files.length; i++) {
                Matcher m = p.matcher(files[i]);
                if (m.matches() && (files[i].length() == this.datePattern.length())) {
                    dateString = files[i];
                    try {
                        tmpDate = sdf.parse(dateString);
                    } catch (ParseException e) {
                        logger.debug(e.getMessage());
                        tmpDate = null;
                    }
                    if ((date != null) && (tmpDate != null)) {
                        if (tmpDate.compareTo(date) < 0) {
                            date = tmpDate;
                        }
                    } else {
                        date = tmpDate;
                    }
                }
            }
        }
        return date;
    }

    public Date getLastLogDate() {
        Date date = null;
        File f = new File(this.sourcePath);
        if (f.isDirectory()) {
            String[] files = f.list();
            String dateString = "";
            Date tmpDate = null;
            SimpleDateFormat sdf = new SimpleDateFormat(this.datePattern);
            for (int i = 0; i < files.length; i++) {
                int offset = files[i].indexOf(this.logFileName);
                if ((offset == 0) && (this.logFileName.length() + this.datePattern.length() + 1 == files[i].length())) {
                    dateString = files[i].substring(this.logFileName.length() + 1);
                    try {
                        tmpDate = sdf.parse(dateString);
                    } catch (ParseException e) {
                        logger.debug(e.getMessage());
                        tmpDate = null;
                    }
                    if ((date != null) && (tmpDate != null)) {
                        if (tmpDate.compareTo(date) > 0) {
                            date = tmpDate;
                        }
                    } else {
                        date = tmpDate;
                    }
                }
            }
        }
        return date;
    }

    public Date countEndDate(Date lastZipDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(lastZipDate);
        c.add(5, this.zipCycle);
        Date needToZip = c.getTime();
        if (needToZip.compareTo(Calendar.getInstance().getTime()) >= 0) {
            needToZip = null;
        }
        return needToZip;
    }

    public Date countDaysAgo(Date nowDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(nowDate);
        c.add(5, -this.zipDaysAgo);
        Date needToProcess = c.getTime();
        if (needToProcess.compareTo(Calendar.getInstance().getTime()) >= 0) {
            needToProcess = null;
        }
        return needToProcess;
    }

    public void zipFile(Date fromDate, Date toDate) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat(this.datePattern);
        String fDate = formatter.format(fromDate);
        Calendar c = Calendar.getInstance();
        c.setTime(toDate);
        c.add(5, -1);
        String tDate = formatter.format(c.getTime());
        String target = "";
        if (this.zipCycle > 1) {
            target = this.targetPath + "/" + this.logFileName + "." + fDate + "_" + tDate + ".zip";
        } else {
            target = this.targetPath + "/" + this.logFileName + "." + fDate + ".zip";
        }
        if (this.zipFlag) {
            System.out.println("Backup File    : " + target);
        } else {
            System.out.println("Delete files only !!");
        }
        File f = new File(target);
        FileOutputStream fos = new FileOutputStream(f);
        this.zos = new ZipOutputStream(fos);
        Calendar date = Calendar.getInstance();
        date.setTime(toDate);
        boolean isAnyEntry = false;
        for (int i = 0; i < this.zipCycle; i++) {
            date.add(5, -1);
            String fileName =  this.sourcePath + File.separator + tDate ;
            File srcFile = new File(fileName);
            if (srcFile.exists()) {
                if (!this.zipFlag && srcFile.isDirectory()) {
                    FileUtils.deleteDirectory(srcFile);
                } else {
                    zipToFile(srcFile);
                    isAnyEntry = true;
                }
            }
        }
        if (isAnyEntry) {
            this.zos.close();
            fos.close();
        } else {
            this.zos = null;
            fos.close();
            if (f.exists()) {
                f.delete();
            }
        }
    }

    public void zipToFile(File srcFile) throws IOException {
        if (srcFile.isDirectory()) {
            String[] fileNames = srcFile.list();
            if (fileNames != null) {
                for (int i = 0; i < fileNames.length; i++) {
                    zipToFile(new File(srcFile, fileNames[i]));
                }
            }
        } else {
            byte[] buf = new byte[this.zipBufferLength];
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(srcFile);
                this.zos.setLevel(9);
                ZipEntry zipEntry = new ZipEntry(srcFile.getName());
                this.zos.putNextEntry(zipEntry);
                int readLength;
                while ((readLength = fis.read(buf)) >= 0) {
                    this.zos.write(buf, 0, readLength);
                }
                this.zos.closeEntry();
                srcFile.delete();
            } catch (Exception e) {
                logger.debug(e.getMessage());
            } finally {
                if (fis != null) {
                    safeClose(fis);
                }
            }

        }
    }

    public void delFile(File srcFile) throws IOException {
        if (srcFile.isDirectory()) {
            String[] fileNames = srcFile.list();
            if (fileNames != null) {
                for (int i = 0; i < fileNames.length; i++) {
                    delFile(new File(srcFile, fileNames[i]));
                }
            }
        } else {
            srcFile.delete();
        }
    }


    public void safeClose(FileInputStream fis) {
        if (fis != null) {
            try {
                fis.close();
            } catch (IOException e) {
                logger.debug(e.getMessage());
            }
        }
    }
}
