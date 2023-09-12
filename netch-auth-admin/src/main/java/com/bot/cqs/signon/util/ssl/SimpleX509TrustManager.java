
package com.bot.cqs.signon.util.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class SimpleX509TrustManager implements X509TrustManager {

    public void checkClientTrusted( X509Certificate[] chain, String authType )
            throws CertificateException {

    }

    public void checkServerTrusted( X509Certificate[] chain, String authType )
            throws CertificateException {

    }

    @Override public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

}
