
package com.bot.cqs.signon.util.ssl;

import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class SimpleHostnameVerifier implements HostnameVerifier {

    private List<String> trustHostnameList;

    public List<String> getTrustHostnameList() {

        return trustHostnameList;
    }

    public void setTrustHostnameList( List<String> trustHostnameList ) {

        this.trustHostnameList = trustHostnameList;
    }

    public boolean verify( String hostname, SSLSession session ) {

        if ( hostname == null || trustHostnameList == null )
            return false;

        for ( String trustFostname : trustHostnameList ) {

            if ( hostname.equalsIgnoreCase( trustFostname ) )
                return true;
        }

        return false;
    }

}
