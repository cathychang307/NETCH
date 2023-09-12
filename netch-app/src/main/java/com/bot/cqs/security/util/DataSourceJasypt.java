package com.bot.cqs.security.util;

import com.bot.cqs.monitor.proxy.MonitorDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

public class DataSourceJasypt extends TransactionAwareDataSourceProxy {
    /**
     * 解密演算法, 演算法選擇 
     * - Digest : MD2, MD5, SHA, SHA-256, SHA-384, SHA-512 
     * - PBE : PBEWITHMD5ANDDES, PBEWITHMD5ANDTRIPLEDES, PBEWITHSHA1ANDDESEDE, PBEWITHSHA1ANDRC2_40
     */
    private static final String JASYPT_ALGORITHM = "PBEWITHMD5ANDDES";
    /** 自定義加密密鑰 */
    private static final String JASYPT_ENCRYPT_TEXT = "etch";

    @Autowired
    DataSourceJasypt(ComboPooledDataSource dataSource) {
        String encryptPwd = dataSource.getPassword();
        String decryptPwd = stringEncryptor(JASYPT_ENCRYPT_TEXT, encryptPwd, false);
        dataSource.setPassword(decryptPwd);
        MonitorDataSource monitorDataSource = new MonitorDataSource(dataSource);
        super.setTargetDataSource(monitorDataSource);
    }

    /**
     * @param secretKey：密鑰。加/解密必須使用同一个密鑰
     * @param inputMsg：加/解密input的内容
     * @param isEncrypt：true表示加密、false表示解密
     * @return
     */
    public static String stringEncryptor(String secretKey, String inputMsg, boolean isEncrypt) {
        PooledPBEStringEncryptor pooledPBEStringEncryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(secretKey);
        config.setPoolSize("1");
        config.setAlgorithm(JASYPT_ALGORITHM);
        pooledPBEStringEncryptor.setConfig(config);
        String result = "";
        if (isEncrypt) {
            result = pooledPBEStringEncryptor.encrypt(inputMsg);
        } else {
            result = pooledPBEStringEncryptor.decrypt(inputMsg);
        }
        return result;
    }

    public static void main(String[] args) {
        String inputMsg = "p@ssw0rd";// DB測試密碼
        // 加密：同樣的密碼和密鑰，每次執行的密文都會不同
        // CLI指令請參考Remind #14899
        String jasyptEncrypt = stringEncryptor(JASYPT_ENCRYPT_TEXT, inputMsg, true);
        System.out.println("加密：" + jasyptEncrypt);
        // 解密
        String jasyptDecrypt = stringEncryptor(JASYPT_ENCRYPT_TEXT, "UcWq4VN5tTgsC4E6O2tTaE96BMa0EX2U", false);
        System.out.println("解密：" + jasyptDecrypt);

    }

}
