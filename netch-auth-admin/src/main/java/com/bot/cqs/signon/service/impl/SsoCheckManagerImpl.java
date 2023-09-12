
package com.bot.cqs.signon.service.impl;

import com.bot.cqs.query.persistence.QueryRole;
import com.bot.cqs.query.persistence.QueryUser;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.signon.SignOnException;
import com.bot.cqs.signon.service.SsoCheckManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class SsoCheckManagerImpl implements SsoCheckManager {

    public static final int SSL_INIT_FAILED = -1;
    public static final int SSL_INIT_NONE = 0;
    public static final int SSL_INIT_COMPLETED = 1;

    private String checkJumperUrl;
    private String infoPermitUrl;
    private String parameterEncoding;
    private String inputEncoding;

    private int connectionTimeout;
    // private int sslInitLevel;

    private X509TrustManager[] x509TrustManagers;
    private HostnameVerifier hostnameVerifier;
    private SSLSocketFactory sslSocketFactory;

    private Logger logger = LoggerFactory.getLogger(getClass());
    private static String SSO_MSG = "<script type=\"text/javascript\">alert(\"系統轉移失敗\\r\\n請至「行內全球資訊網」登入本系統\");top.location.href = \"https://web.bot.com.tw\";</script>";


    public SsoCheckManagerImpl() {

        // setLogger( WebAction.getSsoCheckLogger() );
        setConnectionTimeout(5);
        setParameterEncoding("UTF-8");
        // setSslInitLevel( SSL_INIT_NONE );
    }

    public Map<String, String> checkInfoId(QueryUser user, Map<String, QueryRole> roleMap) throws SignOnException {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        Iterator<String> roleIdIterator = roleMap.keySet().iterator();

        Map<String, String> result = new HashMap<String, String>();

        while (roleIdIterator.hasNext()) {

            String roleId = roleIdIterator.next();
            QueryRole role = roleMap.get(roleId);
            if (!role.isRoleEnabled())
                continue;

            getLogger().debug("Check role : " + user.getEmployeeId() + ", " + roleId);

            try {
                // URL
                HttpURLConnection conn = getHttpUrlConnection(getInfoPermitUrl());

                // parameter encoding
                Properties requestProp = new Properties();
                requestProp.setProperty("employee_id", user.getEmployeeId());
                requestProp.setProperty("info_id", roleId);
                String paramStr = encodeParameter(requestProp);

                // connect
                checkSslConnection(conn);
                conn.connect();

                // write
                outputStream = conn.getOutputStream();
                outputStream.write(paramStr.getBytes());
                outputStream.flush();

                // read
                inputStream = conn.getInputStream();
                byte[] responseData = readResponseData(inputStream);

                conn.disconnect();
                String[] responseStr = parseResponseData(responseData, ",", true);

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

                    // 成功的先看資訊是否足夠
                    // if ( responseStr.length < 5 )
                    //
                    // throw new SignOnException(
                    // "系統轉移回應資訊不足",
                    // null,
                    // SessionLog.SECONDARY_STATUS_LOGIN_UNSATIFIED_AUTHORIZATION_INFO );
                }

                getLogger().debug("assign role[" + roleId + "] to user [" + user.getEmployeeId() + "]");
                user.setQueryRole(role);

                result.put(role.getRoleId(), role.getRoleName());

            } catch (MalformedURLException mue) {
                throw new SignOnException(mue.getMessage(), mue, SessionLog.SECONDARY_STATUS_LOGIN_CONNECTION_FAILE);
            } catch (SocketTimeoutException ste) {
                throw new SignOnException(ste.getMessage(), ste, SessionLog.SECONDARY_STATUS_LOGIN_TIMEOUT);
            } catch (IOException ioe) {
                throw new SignOnException(ioe.getMessage(), ioe, SessionLog.SECONDARY_STATUS_LOGIN_CONNECTION_FAILE);
            } catch (Exception e) {
                throw e;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        logger.debug(e.getMessage());
                    }
                }
                if (outputStream != null) {
                    safeClose(outputStream);
                }
            }
        }
        return result;
    }

    public QueryUser checkJumperId(String employeeId, String jumperId) throws SignOnException {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        getLogger().debug("Check jumper : " + employeeId + ", " + jumperId);
        try {
            // URL
            HttpURLConnection conn = getHttpUrlConnection(getCheckJumperUrl());

            // parameter encoding
            Properties requestProp = new Properties();
            requestProp.setProperty("employee_id", employeeId);
            requestProp.setProperty("jumper_id", jumperId);
            String paramStr = encodeParameter(requestProp);

            // connect
            checkSslConnection(conn);
            conn.connect();

            // write
            outputStream = conn.getOutputStream();
            outputStream.write(paramStr.getBytes());
            outputStream.flush();

            // read
            inputStream = conn.getInputStream();
            byte[] responseData = readResponseData(inputStream);

            conn.disconnect();

            String[] responseStr = parseResponseData(responseData, ",", true);

            // 系統轉移 的驗證失敗
            if (responseStr.length == 0)
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

        } catch (MalformedURLException mue) {
            throw new SignOnException(mue.getMessage(), mue, SessionLog.SECONDARY_STATUS_LOGIN_CONNECTION_FAILE);
        } catch (SocketTimeoutException ste) {
            throw new SignOnException(ste.getMessage(), ste, SessionLog.SECONDARY_STATUS_LOGIN_TIMEOUT);
        } catch (IOException ioe) {
            throw new SignOnException(ioe.getMessage(), ioe, SessionLog.SECONDARY_STATUS_LOGIN_CONNECTION_FAILE);
        } catch (Exception e) {
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.debug(e.getMessage());
                }
            }
            if (outputStream != null) {
                safeClose(outputStream);
            }
        }

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
        safeClose(output);
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

    // protected int getSslInitLevel() {
    //
    // return sslInitLevel;
    // }
    //
    // protected void setSslInitLevel( int sslInitLevel ) {
    //
    // this.sslInitLevel = sslInitLevel;
    // }

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

    private void safeClose(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                logger.debug(e.getMessage());
            }
        }
    }

}
