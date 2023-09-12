
package com.bot.cqs.query.util.inquiry;

import com.bot.cqs.gateway.context.MQInstance;
import com.bot.cqs.gateway.handler.channel.impl.IBMWebSphereMQUtils;
import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.gateway.service.BOTGatewayService;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.iisigroup.cap.utils.CapString;

public class InquiryThread implements Runnable {

    public static final int STATUS_INITIALIZED = 0;
    public static final int STATUS_RUNNING = 10;
    public static final int STATUS_COMPLETED = 20;
    public static final int STATUS_ERROR = 30;

    private int status;
    private InquiryLog inquiryLog;
    private Thread thread;
    private Throwable throwable;
    private MQInstance tchInstance;
    private MQQueueManager botMQTCHQueueManager = null;

    public InquiryThread(InquiryLog inquiryLog) {

        this.inquiryLog = inquiryLog;
        setStatus(STATUS_INITIALIZED);

        tchInstance = IBMWebSphereMQUtils.TCHInstance;
        setTchInstance(tchInstance);
        try {
            botMQTCHQueueManager = new MQQueueManager(tchInstance.getQueueManager(), IBMWebSphereMQUtils.getMQProperies(IBMWebSphereMQUtils.TCH), IBMWebSphereMQUtils.BOTMQTCHConnMgr);
            setBotMQTCHQueueManager(botMQTCHQueueManager);
        } catch (MQException e) {
        }
    }

    public InquiryLog getInquiryLog() {

        return inquiryLog;
    }

    public Throwable getThrowable() {

        return throwable;
    }

    public int getStatus() {

        return status;
    }

    private void setStatus(int status) {

        this.status = status;
    }


    public void start() {

        if (thread == null) {
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * @return the tchInstance
     */
    public MQInstance getTchInstance() {
        return tchInstance;
    }

    /**
     * @param tchInstance the tchInstance to set
     */
    public void setTchInstance(MQInstance tchInstance) {
        this.tchInstance = tchInstance;
    }

    /**
     * @return the botMQTCHQueueManager
     */
    public MQQueueManager getBotMQTCHQueueManager() {
        return botMQTCHQueueManager;
    }

    /**
     * @param botMQTCHQueueManager the botMQTCHQueueManager to set
     */
    public void setBotMQTCHQueueManager(MQQueueManager botMQTCHQueueManager) {
        this.botMQTCHQueueManager = botMQTCHQueueManager;
    }

    public void run() {

        setStatus(STATUS_RUNNING);

        try {
            String inquiryTxCode = inquiryLog.getInquiryTxCode();
            boolean isOBU = false;
            if (!CapString.isEmpty(inquiryTxCode) & inquiryTxCode.matches("4132|4133|4135|4136"))
                isOBU = true;
            BOTGatewayService.enquire(botMQTCHQueueManager, inquiryLog, isOBU);
            setStatus(STATUS_COMPLETED);
        } catch (Exception e) {
            setStatus(STATUS_ERROR);
            throwable = e;
        }
    }

}
