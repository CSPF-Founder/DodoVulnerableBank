package org.cysecurity.example.dodobank.model;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

public class SecureURLOpen {

    public InputStream certInputStream;

    public HttpsURLConnection post(String urlString, String params) {
        try {
            SSLSocketFactory sslFactory = getSSLSocketFactory(); //Custom method
            URL url = new URL(urlString);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setSSLSocketFactory(sslFactory);
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            OutputStream outStream = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
            writer.write(params);
            writer.flush();
            writer.close();
            return con;
        } catch (Exception e) {
            Log.i("SecureURLOpenException", e.getMessage());
        }
        return null;
    }

    public HttpsURLConnection get(String urlString) {
        try {
            SSLSocketFactory sslFactory = getSSLSocketFactory(); //Custom method
            URL url = new URL(urlString);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setSSLSocketFactory(sslFactory);
            return con;
        } catch (Exception e) {
            Log.i("SecureURLOpenException", e.getMessage());
        }
        return null;
    }

    public SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());

            trustEveryone();//Don't Use -Insecure Method - skips host name verification -Just for Emulator
            return sc.getSocketFactory();
        } catch (Exception e) {
            new Throwable(e.getCause());
        }
        return null;
    }

    public void trustEveryone() {
        /*
            !!! Insecure Method - Will you trust Everyone? !!!

            I am using this technique to make it work in emulator.
        */
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            Log.i("SecureURLOpenException", e.getMessage());
        }
    }
}
