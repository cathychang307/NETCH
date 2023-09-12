
package com.bot.cqs.security.support;

import com.bot.cqs.query.util.SHA256TransferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <pre>
 * NoSsoVerifyUtil
 * </pre>
 * 
 * @since 2017年1月13日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2017年1月13日,bob peng,new
 *          </ul>
 */
public class NoSsoVerifyUtil {

    protected static final Logger logger = LoggerFactory.getLogger(NoSsoVerifyUtil.class);

    /**
     * 轉為十六進位可見字元用。
     */
    public static final char[] charTable = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    /**
     * unpack 成<code>charTable[]</code>裡的字元
     * 
     * @param num
     * @return
     */
    public static char valueToHex(int num) {
        return charTable[num];
    }

    /**
     * 轉成字串格式
     * 
     * @param value
     * @return
     */
    public static String byteToHexString(byte[] value) {
        return new String(byteToHexChar(value));
    }

    /**
     * 轉成<code>char</code>格式。
     * 
     * @param value
     * @return
     */
    public static char[] byteToHexChar(byte[] value) {
        char[] result = new char[value.length * 2];
        for (int i = 0; i < result.length; i++) {
            int j = i / 2;
            int r = i % 2;
            // int k = value[j] + 128;
            if (r == 0)
                result[i] = valueToHex((value[j] & 0xf0) >> 4);
            else
                result[i] = valueToHex(value[j] & 0x0f);
        }
        return result;
    }

    public static boolean verifyPwd(String username, String presentedPwd, String encodedPwd) {
        boolean verifyPwd = false;
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            logger.debug(e.getMessage());
        }
        byte[] digest = null;
        if (messageDigest != null) {
            synchronized (messageDigest) {
                // user input pwd here....
                messageDigest.update(presentedPwd.trim().getBytes());
                digest = messageDigest.digest();
            }
            String encodedInputPwd = byteToHexString(digest);
            verifyPwd = encodedInputPwd.equals(encodedPwd);
        }
        return verifyPwd;
    }
}
