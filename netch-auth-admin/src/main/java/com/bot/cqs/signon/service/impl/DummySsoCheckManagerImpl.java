
package com.bot.cqs.signon.service.impl;

import com.bot.cqs.query.persistence.QueryRole;
import com.bot.cqs.query.persistence.QueryUser;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.signon.SignOnException;
import com.bot.cqs.signon.service.SsoCheckManager;
import com.iisigroup.cap.utils.CapString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class DummySsoCheckManagerImpl implements SsoCheckManager {

    public static final int SSL_INIT_FAILED = -1;
    public static final int SSL_INIT_NONE = 0;
    public static final int SSL_INIT_COMPLETED = 1;

    private String checkJumperUrl;
    private String infoPermitUrl;
    private String parameterEncoding;
    private String inputEncoding;

    private int connectionTimeout;

    private X509TrustManager[] x509TrustManagers;
    private HostnameVerifier hostnameVerifier;
    private SSLSocketFactory sslSocketFactory;
    private static String SSO_MSG = "<script type=\"text/javascript\">alert(\"系統轉移失敗\\r\\n請至「行內全球資訊網」登入本系統\");top.location.href = \"https://web.bot.com.tw\";</script>";


    private Logger logger = LoggerFactory.getLogger(getClass());
    Properties p = new Properties();

    public DummySsoCheckManagerImpl() {
        setConnectionTimeout(5);
        setParameterEncoding("UTF-8");
    }

    public Map<String, String> checkInfoId(QueryUser user, Map<String, QueryRole> roleMap) throws SignOnException {

        Iterator<String> roleIdIterator = roleMap.keySet().iterator();

        while (roleIdIterator.hasNext()) {

            String roleId = roleIdIterator.next();
            QueryRole role = roleMap.get(roleId);
            if (!role.isRoleEnabled())
                continue;

            getLogger().debug("Check role : " + user.getEmployeeId() + ", " + roleId);

            String str = p.getProperty(user.getEmployeeId() + "." + roleId);
            if (CapString.isEmpty(str)) {
                continue;
            }
            String[] responseStr = str.split(",");

            // debug information
            StringBuffer s = new StringBuffer("InfoPermit accept " + responseStr.length + " data");
            for (int i = 0; i < responseStr.length; i++)
                s.append("\n" + i + "\t" + responseStr[i]);
            logger.debug(s.toString());

            if (responseStr.length < 2)
                throw new SignOnException(SSO_MSG, null, SessionLog.SECONDARY_STATUS_LOGIN_UNSATIFIED_AUTHORIZATION_INFO);
            else {
                // 這個角色不接受, 換下一個
                if (!Boolean.parseBoolean(responseStr[0]))
                    continue;
            }

            getLogger().debug("assign role[" + roleId + "] to user [" + user.getEmployeeId() + "]");
            user.setQueryRole(role);
            return null;

        }
        return null;
    }

    public QueryUser checkJumperId(String employeeId, String jumperId) throws SignOnException {

        getLogger().debug("Check jumper : " + employeeId + ", " + jumperId);

        String str = p.getProperty(employeeId + ".userInfo", "");
        String[] responseStr = str.split(",");

        // 系統轉移 的驗證失敗
        if (CapString.isEmpty(str) || responseStr.length == 0)
            throw new SignOnException(SSO_MSG, null, SessionLog.SECONDARY_STATUS_LOGIN_UNSATIFIED_AUTHORIZATION_INFO);

        if (!Boolean.parseBoolean(responseStr[0])) {
            String msg = (responseStr.length > 1) ? responseStr[1] : "";
            logger.debug("[SignOnException] Msg:" + msg);
            throw new SignOnException(SSO_MSG, null, SessionLog.SECONDARY_STATUS_LOGIN_CHECK_JUMPER_FAILED);
        }

        // 成功的先看資訊是否足夠
        if (responseStr.length < 5)
            throw new SignOnException(SSO_MSG, null, SessionLog.SECONDARY_STATUS_LOGIN_UNSATIFIED_AUTHORIZATION_INFO);

        QueryUser user = new QueryUser();
        user.setEmployeeId(employeeId);
        user.setEmployeeName(responseStr[1]);
        user.setDepartmentId(responseStr[2]);
        user.setDepartmentName(responseStr[3]);
        user.setRank(responseStr[4]);
        return user;
    }

    protected HttpURLConnection getHttpUrlConnection(String url) throws MalformedURLException, IOException {

        URL targetUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection();
        conn.setConnectTimeout(getConnectionTimeout() * 1000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setReadTimeout(getConnectionTimeout() * 1000);
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        return conn;
    }

    protected String encodeParameter(Properties prop) throws UnsupportedEncodingException {

        int count = 0;
        StringBuffer s = new StringBuffer();
        Enumeration<String> enu = (Enumeration<String>) prop.propertyNames();
        while (enu.hasMoreElements()) {
            String name = enu.nextElement();
            String value = prop.getProperty(name);

            if (count > 0)
                s.append('&');

            s.append(URLEncoder.encode(name, getParameterEncoding()));
            s.append('=');
            s.append(URLEncoder.encode(value, getParameterEncoding()));
            count++;
        }

        return s.toString();
    }

    protected byte[] readResponseData(InputStream input) throws IOException {

        ByteArrayOutputStream output = new ByteArrayOutputStream(128);
        byte[] buff = new byte[128];
        int len;
        while ((len = input.read(buff)) != -1)
            output.write(buff);

        output.close();
        return output.toByteArray();
    }

    protected String[] parseResponseData(byte[] data, String separator, boolean trimEach) throws UnsupportedEncodingException {

        String str;
        if (getInputEncoding() == null)
            str = new String(data);
        else
            str = new String(data, getInputEncoding());

        List<String> array = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(str, separator);
        while (tokenizer.hasMoreTokens()) {
            if (trimEach)
                array.add(tokenizer.nextToken().trim());
            else
                array.add(tokenizer.nextToken());
        }

        String[] result = new String[array.size()];
        array.toArray(result);
        return result;
    }

    protected void checkSslConnection(HttpURLConnection conn) throws SignOnException {

        if (!(conn instanceof HttpsURLConnection))
            return;

        int sslInitLevel = SSL_INIT_NONE;
        try {
            runSslInit();
            sslInitLevel = SSL_INIT_COMPLETED;
        } catch (Exception e) {
            sslInitLevel = SSL_INIT_FAILED;
            new SignOnException(SessionLog.SECONDARY_STATUS_LOGIN_SSL_INIT_FAILED);
        }

        if (sslInitLevel == SSL_INIT_COMPLETED) {

            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;

            if (getSslSocketFactory() != null)
                httpsConn.setSSLSocketFactory(getSslSocketFactory());
            if (getHostnameVerifier() != null)
                httpsConn.setHostnameVerifier(getHostnameVerifier());
        } else
            throw new SignOnException(SessionLog.SECONDARY_STATUS_LOGIN_SSL_INIT_FAILED);

    }

    protected void runSslInit() throws NoSuchAlgorithmException, KeyManagementException {

        if (getX509TrustManagers() != null) {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, getX509TrustManagers(), new SecureRandom());
            setSslSocketFactory(sslContext.getSocketFactory());
        }
    }

    public String getCheckJumperUrl() {

        return checkJumperUrl;
    }

    public void setCheckJumperUrl(String checkJumperUrl) {
        this.checkJumperUrl = checkJumperUrl;
        InputStream is = null;
        try {
            is = this.getClass().getResourceAsStream(checkJumperUrl);
            p.load(is);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public String getInfoPermitUrl() {

        return infoPermitUrl;
    }

    public void setInfoPermitUrl(String infoPermitUrl) {

        this.infoPermitUrl = infoPermitUrl;
    }

    public String getInputEncoding() {

        return inputEncoding;
    }

    public void setInputEncoding(String inputEncoding) {

        this.inputEncoding = inputEncoding;
    }

    public String getParameterEncoding() {

        return parameterEncoding;
    }

    public void setParameterEncoding(String parameterEncoding) {

        this.parameterEncoding = parameterEncoding;
    }

    public X509TrustManager[] getX509TrustManagers() {

        return x509TrustManagers;
    }

    public void setX509TrustManagers(X509TrustManager[] trustManagers) {

        x509TrustManagers = trustManagers;
    }

    public HostnameVerifier getHostnameVerifier() {

        return hostnameVerifier;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {

        this.hostnameVerifier = hostnameVerifier;
    }

    public int getConnectionTimeout() {

        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {

        this.connectionTimeout = connectionTimeout;
    }

    protected SSLSocketFactory getSslSocketFactory() {

        return sslSocketFactory;
    }

    protected void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {

        this.sslSocketFactory = sslSocketFactory;
    }

    protected Logger getLogger() {

        return logger;
    }

    private void setLogger(Logger logger) {

        this.logger = logger;
    }
}
