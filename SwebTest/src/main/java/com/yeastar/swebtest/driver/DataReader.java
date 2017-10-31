package com.yeastar.swebtest.driver;

import com.sun.jna.platform.win32.WinBase;

import java.io.*;
import java.util.Properties;

import static com.yeastar.swebtest.driver.Config.currentPath;
import static org.bouncycastle.asn1.cms.CMSObjectIdentifiers.data;

/**
 * Created by GaGa on 2017-05-12.
 */
public class DataReader {

    /**
     * Data.properties配置
     */
    public static String SCREENSHOT_PATH = System.getProperty("user.dir")+"\\"+ readFromfile("SCREENSHOT_PATH");
    public static String FIREFOX_PATH = readFromfile("FIREFOX_PATH");
//    public static String CHROME_PATH = "src\\main\\resources\\driver\\chrome\\chromedriver.exe";
    public static String CHROME_PATH =readFromfile("CHROME_PATH");

    /**
     * 导入文件指定
     */
    public static String EXPORT_PATH = System.getProperty("user.dir")+"\\"+ readFromfile("EXPORT_PATH");
    public static String VOICEPACKAGE_NAME = readFromfile("VOICEPACKAGE_NAME");
    public static String SYSTEM_PROMPT_LANGUAGE = readFromfile("SYSTEM_PROMPT_LANGUAGE");

    /**
     * 被测设备的配置信息
     */
    public static String PRODUCT = readFromfile("PRODUCT");
    public static String DEVICE_IP_LAN = readFromfile("DEVICE_IP_LAN");
    public static String DEVICE_IP_WAN = readFromfile("DEVICE_IP_WAN");
    public static String DEVICE_IP_SUBNETMASK = readFromfile("DEVICE_IP_SUBNETMASK");
    public static String DEVICE_IP_GATEWAY = readFromfile("DEVICE_IP_GATEWAY");
    public static String DEVICE_IP_DNS = readFromfile("DEVICE_IP_DNS");
    public static String DEVICE_PORT = readFromfile("DEVICE_PORT");
    public static String LOGIN_USERNAME = readFromfile("LOGIN_USERNAME");
    public static String LOGIN_PASSWORD = readFromfile("LOGIN_PASSWORD");
    public static String SSH_PORT = readFromfile("SSH_PORT");
    public static String USERNAEM_LS = readFromfile( "USERNAEM_LS");
    public static String USERNAME_SUPPORT = readFromfile("USERNAME_SUPPORT");
    public static String PASSWORD_SUPPORT = readFromfile("PASSWORD_SUPPORT");
    public static int UDP_PORT = Integer.parseInt(readFromfile("UDP_PORT"));
    public static int TCP_PORT = Integer.parseInt(readFromfile("TCP_PORT"));
    public static int TLS_PORT = Integer.parseInt(readFromfile("TLS_PORT"));
    public static int UDP_PORT_ASSIST_1 = Integer.parseInt(readFromfile("UDP_PORT_ASSIST_1"));
    public static int UDP_PORT_ASSIST_2 = Integer.parseInt(readFromfile("UDP_PORT_ASSIST_2"));

    /**
     * 测试模块定义
     */
    public static String FXS_1 = readFromfile("FXS_1");
//    public static String FXS_2 = readFromfile("FXS_2");
    public static String FXO_1 = readFromfile("FXO_1");
//    public static String FXO_2 = readFromfile("FXO_2");
    public static String BRI_1 = readFromfile("BRI_1");
    public static String BRI_2 = readFromfile("BRI_2");
    public static String GSM = readFromfile("GSM");
    public static String E1 = readFromfile("E1");
    public static String DEVICE_ASSIST_GSM = readFromfile("DEVICE_ASSIST_GSM");
    public static String DEVICE_TEST_GSM = readFromfile("DEVICE_TEST_GSM");

    public static String SIPTrunk = readFromfile("SIPTRUNK");
    public static String IAXTrunk = readFromfile( "IAXTRUNK");
    public static String SPS = readFromfile("SPS");
    public static String SPX = readFromfile("SPX");

    /**
     * 存储设备
     */
    public static String NETWORK_DEVICE_NAME = readFromfile("NETWORK_DEVICE_NAME");
    public static String NETWORK_DEVICE_IP = readFromfile("NETWORK_DEVICE_IP");
    public static String NETWORK_DEVICE_SHARE_NAME = readFromfile("NETWORK_DEVICE_SHARE_NAME");
    public static String NETWORK_DEVICE_USER_NAME =readFromfile("NETWORK_DEVICE_USER_NAME");
    public static String NETWORK_DEVICE_USER_PASSWORD =readFromfile("NETWORK_DEVICE_USER_PASSWORD");
    public static String DEVICE_RECORD_NAME = readFromfile("DEVICE_RECORD_NAME");

    /**
     * 辅助设备ip
     */
    public static String DEVICE_ASSIST_1 = readFromfile("DEVICE_ASSIST_1");
    public static String DEVICE_ASSIST_2 =readFromfile("DEVICE_ASSIST_2");

    public static String DEVICE_VERSION = readFromfile("VERSION");
    public static String BROWSER = readFromfile("BROWSER");
    public static String URL = readFromfile("URL");

    /**
     * 导入文件名称
     */
    public static String IMPORT_EXTENSION = readFromfile("IMPORT_EXTENSION");
    /**
     * 定义出错重跑
     */
    public static String RETRY_COUNT = readFromfile("RETRY_COUNT");
    /**
     * 根据key返回key值
     * @param key
     * @return keyvalue
     */
    public static String readFromfile(String key) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream("src/main/resources/data.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (properties.getProperty(key).equals("null")){
            return "null";
        }else{
            return properties.getProperty(key);
        }

    }
}
