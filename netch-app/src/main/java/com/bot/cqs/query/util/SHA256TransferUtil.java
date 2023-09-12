package com.bot.cqs.query.util;

import com.bot.cqs.gateway.handler.channel.impl.IBMWebSphereMQUtils;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256TransferUtil {

    protected static final Logger logger = LoggerFactory.getLogger(SHA256TransferUtil.class);

    private MessageDigest messageDigest;

    public SHA256TransferUtil() throws NoSuchAlgorithmException {

        messageDigest = MessageDigest.getInstance("SHA-256");
        // messageDigest = MessageDigest.getInstance("MD5");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        boolean loop = true;
        JFrame jFrame = new JFrame();
        JTextArea blackBoard = new JTextArea();
        while (loop) {
            String inputValue = JOptionPane.showInputDialog("請輸入密碼");

            try {
                jFrame.setSize(400, 50);
                jFrame.setTitle("密文");

                if (inputValue == null) {
                    loop = false;
                } else {
                    blackBoard.setText(new SHA256TransferUtil().getSha256Password(inputValue));
                    jFrame.add(blackBoard);
                    jFrame.setVisible(true);
                }
            } catch (Exception e) {
                logger.debug(e.getMessage());
            } finally {
                ;
            }
        }
        jFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        jFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(1);
            }
        });

        // System.exit(1);
    }

    private String getSha256Password(String password) {

        byte[] digest = null;
        synchronized (messageDigest) {
            messageDigest.update(password.getBytes());
            digest = messageDigest.digest();
        }
        return StringProcess.byteToHexString(digest);

    }
}
