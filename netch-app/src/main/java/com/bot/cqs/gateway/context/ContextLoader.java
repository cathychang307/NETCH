package com.bot.cqs.gateway.context;

import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iisigroup.cap.utils.CapString;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.XPP3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.com.iisi.common.message.format.util.MessageUtils;

/**
 * 此類別主要用於處理Gateway模組任何設定值，包含MQ設定及Log設定。
 *
 * @author Jeff Tseng
 * @since 1.0 2007/08/31
 */
public class ContextLoader {

    protected static final Logger logger = LoggerFactory.getLogger(ContextLoader.class);

    /**
     * XPath格式字串。取得整份文件中所有符合條件元素的XPath。
     */
    private static final MessageFormat PATTERN_ALL = new MessageFormat("//*[namespace-uri(.)= ''{0}'' and local-name() = ''{1}'']");
    /**
     * XPath格式字串。取得所在元素下符合條件元素的XPath。
     */
    private static final MessageFormat PATTERN_SINGLE = new MessageFormat("/*[namespace-uri(.)= ''{0}'' and local-name() = ''{1}'']");
    /**
     * GatewayContext.xml文件的dom4j Document物件。
     */
    private static Document doc;
    /**
     * GatewayContext.xml文件的namespace URI。
     */
    private static String namespaceURI;
    /**
     * 所有訊息格式物件的儲存區，鍵值為訊息格式代碼，值為訊息格式物件。
     */
    private static GatewayContext gatewayContext = new GatewayContext();

    public static void init() {

        if (!(gatewayContext != null && gatewayContext.getIbmWebSphereMQ() != null)) {
            XPP3Reader reader = new XPP3Reader();
            // 載入GatewayContext.xml並初始所有系統設定資訊。
            URL formatResource = GatewayContext.class.getClassLoader().getResource("gateway/GatewayContext.xml");
            if (formatResource != null) {
                try {
                    doc = reader.read(formatResource);
                    namespaceURI = doc.getRootElement().getNamespaceURI();
                    initGatewayContext();
                } catch (Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(e.getMessage());
                    }
                }
            }
            reader = null;
            formatResource = null;

            // 載入log4j.properties並初始Log4J設定。
            // PropertyConfigurator.configure(ContextLoader.class.getClassLoader().getResource("META-INF/log4j.properties"));
            GatewayContext.logger = LoggerFactory.getLogger("gatewayLog");
        }
    }

    /**
     * 將特定XML元素，轉換成<code>tw.com.bot.cqs.gateway.context.GatewayContext</code>物件
     */
    private static final void initGatewayContext() {

        if (doc != null) {
            gatewayContext.setLogLocation(initLogLocation());
            gatewayContext.setIbmWebSphereMQ(initIbmWebSphereMQ());
            gatewayContext.setThreadPools(initThreadPools());
            gatewayContext.setLogFlag(initLogFlag());
        }
    }

    /**
     * 取得log_location標籤的設定值。。
     * 
     * @return String物件。
     */
    private static final String initLogLocation() {
        String logLocation = null;
        Element element = (Element) doc.selectSingleNode(getPath("gateway_context") + getPath("log_location"));

        if (element != null) {
            logLocation = element.getStringValue();
        }
        element = null;

        return logLocation;
    }

    /**
     * 取得log_location標籤的設定值。。
     * 
     * @return boolean 是否有設定log。
     */
    private static final boolean initLogFlag() {
        String initLogFlag = null;
        Element element = (Element) doc.selectSingleNode(getPath("gateway_context") + getPath("log_flag"));

        if (element != null) {
            initLogFlag = element.getStringValue();
        }
        element = null;

        return ("on".equals(initLogFlag));
    }

    /**
     * 取得ibm_websphere_mq標籤的設定值。。
     * 
     * @return IBMWebSphereMQ物件。
     */
    private static final IBMWebSphereMQ initIbmWebSphereMQ() {
        IBMWebSphereMQ mq = null;
        Element element = (Element) doc.selectSingleNode(getPath("gateway_context") + getPath("ibm_websphere_mq"));

        String tmp = null;
        if (element != null) {
            mq = new IBMWebSphereMQ();

            tmp = element.attributeValue("pool_max_size");
            mq.setPoolMaxSize(MessageUtils.nil(tmp) || MessageUtils.empty(tmp) ? 0 : Integer.parseInt(tmp));

            tmp = element.attributeValue("unused_max_size");
            mq.setUnusedMaxSize(MessageUtils.nil(tmp) || MessageUtils.empty(tmp) ? 0 : Integer.parseInt(tmp));

            mq.setInstances(initMQInstances(element));
        }
        element = null;

        return mq;
    }

    /**
     * 取得ibm_websphere_mq/instance標籤的設定值。。
     * 
     * @return MQInstance物件群。
     */
    @SuppressWarnings("unchecked")
    private static final Map<String, MQInstance> initMQInstances(Element parent) {
        Map<String, MQInstance> instances = null;
        List<Element> list = null;
        MQInstance instance = null;
        String tmp = null;
        String expiryTime = null;

        list = parent.selectNodes(parent.getUniquePath() + getPath("instance"));

        if (list != null && list.size() > 0) {
            instances = new HashMap<String, MQInstance>();
            for (Element element : list) {
                if (!element.getParent().equals(parent))
                    continue;

                instance = new MQInstance();

                instance.setCcsid(element.attributeValue("ccsid"));
                instance.setChannel(element.attributeValue("channel"));
                instance.setEncoding(element.attributeValue("encoding"));
                instance.setHost(element.attributeValue("host"));
                instance.setId(element.attributeValue("id"));
                instance.setUserId(element.attributeValue("userId"));
                instance.setPsswwdd(element.attributeValue("psswwdd"));
                instance.setInboundChannel(element.attributeValue("inbound_channel"));
                instance.setInboundQueue(element.attributeValue("inbound_queue"));
                instance.setOutboundChannel(element.attributeValue("outbound_channel"));
                instance.setOutboundQueue(element.attributeValue("outbound_queue"));
                instance.setPort(element.attributeValue("port"));
                instance.setQueueManager(element.attributeValue("queue_manager"));
                instance.setConnMode(element.attributeValue("connection_mode"));

                tmp = element.attributeValue("watting_time");
                instance.setTimeout(MessageUtils.nil(tmp) || MessageUtils.empty(tmp) ? 0 : Integer.parseInt(tmp) * 1000);

                expiryTime = element.attributeValue("expiry_time");
                if (!CapString.isEmpty(expiryTime)) {
                    instance.setExpiryTime(MessageUtils.nil(expiryTime) || MessageUtils.empty(expiryTime) ? 0 : Integer.parseInt(expiryTime) * 10);
                }
                instances.put(instance.getId(), instance);

                element = null;
            }
        }

        return instances;
    }

    /**
     * 取得thread_pool/instance標籤的設定值。。
     * 
     * @return ThreadPool物件群。
     */
    @SuppressWarnings("unchecked")
    private static final Map<String, ThreadPool> initThreadPools() {
        Map<String, ThreadPool> instances = null;
        List<Element> list = null;
        ThreadPool instance = null;

        list = doc.selectNodes(getPath("gateway_context") + getPath("thread_pool") + getPath("instance"));
        String tmp = null;
        if (list != null && list.size() > 0) {
            instances = new HashMap<String, ThreadPool>();
            for (Element element : list) {
                if (!element.getParent().equals(doc.selectSingleNode(getPath("gateway_context") + getPath("thread_pool"))))
                    continue;

                instance = new ThreadPool();

                instance.setId(element.attributeValue("id"));

                tmp = element.attributeValue("size");
                instance.setSize(MessageUtils.nil(tmp) || MessageUtils.empty(tmp) ? 30 : Integer.parseInt(tmp));

                instances.put(instance.getId(), instance);

                element = null;
            }
        }

        return instances;
    }

    /**
     * 取得某標籤的XPath字串。
     * 
     * @param namespaceURI
     *            namespace URI
     * @param tagName
     *            標籤名稱
     * @param singleNode
     *            是否只取得單一元素
     * @return 某標籤的XPath字串
     */
    private static final String getPath(String namespaceURI, String tagName, boolean singleNode) {

        String[] args = { namespaceURI, tagName };

        if (singleNode)
            return PATTERN_SINGLE.format(args);
        else
            return PATTERN_ALL.format(args);
    }

    /**
     * 取得某標籤的XPath字串。使用預設namespace。
     * 
     * @param tagName
     *            標籤名稱
     * @param singleNode
     *            是否只取得單一元素
     * @return 某標籤的XPath字串
     */
    private static final String getPath(String tagName, boolean singleNode) {

        return getPath(namespaceURI, tagName, singleNode);
    }

    /**
     * 取得某標籤的XPath字串。使用預設namespace，並僅取得單一元素下的XPath。
     * 
     * @param tagName
     *            標籤名稱
     * @return 某標籤的XPath字串
     */
    private static final String getPath(String tagName) {

        return getPath(tagName, true);
    }

    public static GatewayContext getGatewayContext() {
        return gatewayContext;
    }
}
