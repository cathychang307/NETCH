
package com.bot.cqs.query.util;

import com.bot.cqs.query.command.NetchLogContent;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapString;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
/**
 * <pre>
 * TODO Write a short description on the purpose of the program
 * </pre>
 * @since  2017年1月13日
 * @author bob peng
 * @version <ul>
 *           <li>2017年1月13日,bob peng,new
 *          </ul>
 */
public class NetchLogWritingUtil {

    private static final String BEFORE_LOG_MESSAGE_ID = "accessFunction.";
    private static final String AFTER_LOG_MESSAGE_ID = "afterFunction.";
    protected final static Logger logger = LoggerFactory.getLogger("queryDefaultLog");

    public static void writeLogBeforeAction(NetchLogContent content) {
        if(content == null){
            return;
        }
        List<Object> argsList = content.getContent();
        String messageId = BEFORE_LOG_MESSAGE_ID + argsList.size();
        String message = CapAppContext.getMessage(messageId, argsList.toArray());
        logger.info(NetchLogWritingUtil.utf8EncodedString(message));
    }

    public static void writeLogAfterAction(NetchLogContent content) {
        if(content == null){
            return;
        }
        List<Object> argsList = content.getContent();
        String returnMsg = content.getReturnMsg();
        if(!CapString.isEmpty(returnMsg)){
            argsList.add(returnMsg);
        }
        String messageId = AFTER_LOG_MESSAGE_ID + argsList.size();
        String message = CapAppContext.getMessage(messageId, argsList.toArray());
        logger.info(message);

    }

    public static String utf8EncodedString(String str) {
        byte[] bytes = StringUtils.getBytesUtf8(str);
        String utf8EncodedString = StringUtils.newStringUtf8(bytes);
        return utf8EncodedString;
    }
}
