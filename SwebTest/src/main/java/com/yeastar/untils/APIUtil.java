package com.yeastar.untils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.velocity.test.provider.Person;
import top.jfunc.json.impl.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.yeastar.swebtest.driver.DataReader2.*;
import static org.apache.log4j.spi.Configurator.NULL;


@Log4j2
public class APIUtil {

    private static String webSession = NULL;

    public static List<CDRObject> getCDRRecord(int row) throws IOException {
        loginWeb();
        String req = "https://"+DEVICE_IP_LAN+":8088/api/v1.0/cdr/search?page=1&page_size="+(row+1)+"&sort_by=id&order_by=desc";
        String respondJson = doGetRequest(req);

        List<CDRObject> cdrList = new ArrayList<>();
        for (int k=0; k < row; k++){
            CDRObject p = new CDRObject(respondJson, k);
            cdrList.add(p);
        }

        return cdrList;
    }


    /**
     * 登录aip，获取cookie
     * @return
     */
    public static String loginWeb(){

        String req = "https://"+DEVICE_IP_LAN+":8088/api/v1.0/login?username="+LOGIN_USERNAME+"&password="+enBase64(DigestUtils.md5Hex(LOGIN_PASSWORD));
        String respond = doGetRequest(req);

        return respond;
    }

    /**
     * 执行Get请求
     * @param urlpath
     * @return
     */
    public static String doGetRequest(String urlpath) {
        log.debug("deGetRequest cmd: "+urlpath);
        HttpsURLConnection.setDefaultHostnameVerifier(new APIUtil().new NullHostNameVerifier());

        String responeData = null;
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            URL url = new URL(urlpath);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // 设置访问提交模式，表单提交
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("cookie","websession="+webSession);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if(conn.getResponseCode() == 200){
                String cookies = conn.getHeaderField("Set-Cookie");
                log.debug("[获取Cookies] "+ cookies);
                if(cookies != null){
                    webSession = cookies.split(";")[0].replace("websession=","");
                }
                InputStream inStream = conn.getInputStream();
                int inStreamLength = inStream.available();
                byte[] bytes = new byte[inStreamLength+1];
                inStream.read(bytes, 0, inStream.available());
                responeData = new String(bytes, "utf-8").trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responeData;
    }

    static TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // TODO Auto-generated method stub
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // TODO Auto-generated method stub
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
    };

    public class NullHostNameVerifier implements HostnameVerifier {
        /*
         * (non-Javadoc)
         * 
         * @see javax.net.ssl.HostnameVerifier#verify(java.lang.String,
         * javax.net.ssl.SSLSession)
         */
        @Override
        public boolean verify(String arg0, SSLSession arg1) {
            // TODO Auto-generated method stub
            return true;
        }
    }

    /**
     * Base64加密字符串
     * @param str
     * @return
     */
    private static String enBase64(String str) {
        byte[] bytes = str.getBytes();

        String encoded = Base64.getEncoder().encodeToString(bytes);
        System.out.println("Base 64 加密后：" + encoded);

        return encoded;
    }
}
