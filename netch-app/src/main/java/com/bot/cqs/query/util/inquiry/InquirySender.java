
package com.bot.cqs.query.util.inquiry;

import com.bot.cqs.gateway.persistence.InquiryLog;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;

public class InquirySender implements Runnable {

    public static final int DEFAULT_TIMEOUT = 300; // 5 min

    private InquiryThread[] inquiryThreads;
    private int timeout;
    private Thread thread;
    private boolean completedFlag;
    private long deadline;

    public InquirySender(InquiryLog[] inquiryLogs) {

        if (inquiryLogs == null)
            inquiryThreads = new InquiryThread[0];
        else
            inquiryThreads = new InquiryThread[inquiryLogs.length];

        for (int i = 0; i < inquiryLogs.length; i++)
            inquiryThreads[i] = new InquiryThread(inquiryLogs[i]);

        setTimeout(DEFAULT_TIMEOUT);
        completedFlag = false;
    }

    public int getTimeout() {

        return timeout;
    }

    public void setTimeout(int timeout) {

        this.timeout = timeout;
    }

    public void start() {

        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void run() {

        // 最後時限
        deadline = System.currentTimeMillis() + getTimeout() * 1000;

        synchronized (this) {
            for (int i = 0; i < inquiryThreads.length; i++)
                inquiryThreads[i].start();

            while (true) {

                try {
                    wait(500);

                } catch (InterruptedException e) {
                }

                // check status
                int competedCount = 0;
                for (int i = 0; i < inquiryThreads.length; i++)
                    if (inquiryThreads[i].getStatus() > InquiryThread.STATUS_RUNNING)
                        competedCount++;

                if (competedCount >= inquiryThreads.length)
                    completedFlag = true;

                if (completedFlag)
                    break;

                // 時間到了就不管了
                if (System.currentTimeMillis() > deadline) {
                    completedFlag = true;
                    break;
                }
            }

            notifyAll();
        }

        for (int i = 0; i < inquiryThreads.length; i++) {

            MQQueueManager botMQTCHQueueManager = inquiryThreads[i].getBotMQTCHQueueManager();

            if (botMQTCHQueueManager != null) {
                try {
                    botMQTCHQueueManager.disconnect();
                } catch (MQException e) {
                }
                botMQTCHQueueManager = null;
            }
        }

    }

    public synchronized InquiryThread[] waitForInquiry() {

        if (!completedFlag) {
            try {
                wait(getTimeout() * 1000 + 1000);
            } catch (InterruptedException e) {
            }
        }
        return inquiryThreads;
    }
}
