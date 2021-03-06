//package com.yeastar.swebtest.driver;
//
//
//
//import lombok.extern.log4j.Log4j2;
//
//import java.io.*;
//import java.net.URL;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Properties;
//
//import static com.yeastar.swebtest.driver.Config.currentPath;
////import static org.bouncycastle.asn1.cms.CMSObjectIdentifiers.data;
//
///**
// * Created by GaGa on 2017-05-12.
// */
//@Log4j2
//public class DataReader {
//
//    public static String DATA_PROPERTIES_FILE_PATH = "/data.properties";
//
//
//    /**
//     * Data.properties配置
//     */
//    public static String SCREENSHOT_PATH = System.getProperty("user.dir")+"\\"+ readFromfile("SCREENSHOT_PATH");
//    public static String FIREFOX_PATH = readFromfile("FIREFOX_PATH");
////    public static String CHROME_PATH = "src\\main\\resources\\driver\\chrome\\chromedriver.exe";
//    public static String CHROME_PATH =readFromfile("CHROME_PATH");
//
//    /**
//     * 导入文件指定
//     */
//    public static String EXPORT_PATH = System.getProperty("user.dir")+"\\"+ readFromfile("EXPORT_PATH");
//    public static String VOICEPACKAGE_NAME = readFromfile("VOICEPACKAGE_NAME");
//    public static String SYSTEM_PROMPT_LANGUAGE = readFromfile("SYSTEM_PROMPT_LANGUAGE");
//
//    /**
//     * 被测设备的配置信息
//     */
//    public static String PRODUCT = readFromfile("PRODUCT");
//    public static String DEVICE_IP_LAN = readFromfile("DEVICE_IP_LAN");
//    public static String DEVICE_IP_WAN = readFromfile("DEVICE_IP_WAN");
//    public static String DEVICE_IP_SUBNETMASK = readFromfile("DEVICE_IP_SUBNETMASK");
//    public static String DEVICE_IP_GATEWAY = readFromfile("DEVICE_IP_GATEWAY");
//    public static String DEVICE_IP_DNS = readFromfile("DEVICE_IP_DNS");
//    public static String DEVICE_PORT = readFromfile("DEVICE_PORT");
//    public static String LOGIN_USERNAME = readFromfile("LOGIN_USERNAME");
//    public static String LOGIN_PASSWORD = readFromfile("LOGIN_PASSWORD");
//
//    public static String LOGIN_ADMIN = readFromfile("LOGIN_ADMIN");
//    public static String SSH_PORT = readFromfile("SSH_PORT");
//    public static String USERNAEM_LS = readFromfile( "USERNAEM_LS");
//    public static String USERNAME_SUPPORT = readFromfile("USERNAME_SUPPORT");
//    public static String PASSWORD_SUPPORT = readFromfile("PASSWORD_SUPPORT");
//    public static int UDP_PORT = Integer.parseInt(readFromfile("UDP_PORT"));
//    public static int TCP_PORT = Integer.parseInt(readFromfile("TCP_PORT"));
//    public static int TLS_PORT = Integer.parseInt(readFromfile("TLS_PORT"));
//    public static int UDP_PORT_ASSIST_1 = Integer.parseInt(readFromfile("UDP_PORT_ASSIST_1"));
//    public static int UDP_PORT_ASSIST_2 = Integer.parseInt(readFromfile("UDP_PORT_ASSIST_2"));
//    public static int UDP_PORT_ASSIST_3 = Integer.parseInt(readFromfile("UDP_PORT_ASSIST_3"));
//    public static int AMI_PROT = Integer.parseInt(readFromfile("AMI_PORT"));
//
//    /**
//     * 测试模块定义
//     */
//    public static String FXS_1 = readFromfile("FXS_1");
////    public static String FXS_2 = readFromfile("FXS_2");
//    public static String FXO_1 = readFromfile("FXO_1");
////    public static String FXO_2 = readFromfile("FXO_2");
//    public static String BRI_1 = readFromfile("BRI_1");
//    public static String BRI_2 = readFromfile("BRI_2");
//    public static String GSM = readFromfile("GSM");
//    public static String E1 = readFromfile("E1");
//    public static String DEVICE_ASSIST_GSM = readFromfile("DEVICE_ASSIST_GSM").trim();
//    public static String DEVICE_TEST_GSM = readFromfile("DEVICE_TEST_GSM").trim();
//
//    public static String SIPTrunk = readFromfile("SIPTRUNK");
//    public static String SIPTrunk2 = readFromfile("SIPTRUNK2");
//    public static String ACCOUNTTRUNK = readFromfile("ACCOUNTTRUNK");
//    public static String IAXTrunk = readFromfile( "IAXTRUNK");
//    public static String SPS = readFromfile("SPS");
//    public static String SPX = readFromfile("SPX");
//    /**
//     * gridhub
//     */
//    public  static String IS_RUN_REMOTE_SERVER = readFromfile("IS_RUN_REMOTE_SERVER");
//    public  static String GRID_HUB_IP = readFromfile("GRID_HUB_IP");
//    public  static String GRID_HUB_PORT = readFromfile("GRID_HUB_PORT");
//
//    /**
//     * 存储设备
//     */
//    public static String NETWORK_DEVICE_NAME = readFromfile("NETWORK_DEVICE_NAME");
//    public static String NETWORK_DEVICE_IP = readFromfile("NETWORK_DEVICE_IP");
//    public static String NETWORK_DEVICE_SHARE_NAME = readFromfile("NETWORK_DEVICE_SHARE_NAME");
//    public static String NETWORK_DEVICE_USER_NAME =readFromfile("NETWORK_DEVICE_USER_NAME");
//    public static String NETWORK_DEVICE_USER_PASSWORD =readFromfile("NETWORK_DEVICE_USER_PASSWORD");
//    public static String DEVICE_RECORD_NAME = readFromfile("DEVICE_RECORD_NAME");
//
//    /**
//     * 辅助设备ip
//     */
//    public static String DEVICE_ASSIST_1 = readFromfile("DEVICE_ASSIST_1");
//    public static String DEVICE_ASSIST_2 =readFromfile("DEVICE_ASSIST_2");
//    public static String DEVICE_ASSIST_3 =readFromfile("DEVICE_ASSIST_3");
//
//    public static String DEVICE_VERSION = readFromfile("VERSION");
//    public static String[] VERSION_SPLIT = DEVICE_VERSION.split("\\.");
//    public static String BROWSER = readFromfile("BROWSER");
//    public static String URL = readFromfile("URL");
//
//    public static String HTTP_SERVER_IMAGE = readFromfile("HTTP_SERVER_IMAGE");
//    /**
//     * 导入文件名称
//     */
//    public static String IMPORT_EXTENSION = readFromfile("IMPORT_EXTENSION");
//    /**
//     * 本地log保存
//     */
//    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
//    static String currentTime = String.valueOf(sdf.format(new Date()))+"_";
//    public static String LOCAL_LOG_FILE =  currentTime+readFromfile("LOCAL_LOG_FILE");
//    public static String AMI_LOG_FILE =  currentTime+readFromfile("AMI_LOG_FILE");
//    /**
//     * 定义出错重跑
//     */
//    public static String RETRY_COUNT = readFromfile("RETRY_COUNT");
//    /**
//     * 根据key返回key值
//     * @param key
//     * @return keyvalue
//     */
//    public static String readFromfile(String key) {
//        Properties properties = new Properties();
//        InputStream inputStream = null;
////        try {
////            inputStream = new BufferedInputStream(new FileInputStream("data.properties"));
//        try {
//            String FilePath = System.getProperty("user.dir")+File.separator+"test-classes"+File.separator;
//            log.debug("[user.dir]"+System.getProperty("user.dir"));
//            log.debug("[File file]"+FilePath);
//            inputStream = new FileInputStream(new File(FilePath+"data.properties"));
////            inputStream = DataReader.class.getResourceAsStream("data.properties");
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        try {
//            properties.load(inputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////        System.out.println("Key: "+key+" Value: "+properties.getProperty(key)+ " is Null:"+properties.getProperty(key).equals("null"));
//        if (properties.getProperty(key).equals("null")){
//            return "null";
//        }else{
//            return properties.getProperty(key);
//        }
//    }
//
//    private static Properties prop;
//
//
//    /**
//     * 获取prop文件，prop需要在资源文件路径下
//     * @param propPath
//     * @return
//     */
//    public  Properties getPropertie(String propPath) {
//        prop = new Properties();
//        BufferedReader br=null;
//        try {
//            br=new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(propPath), "UTF-8"));
//            prop.load(br);
//        } catch (Exception e) {
//            log.error("读取配置文件异常！"+e);
//        }   finally {
//            try{
//                if(br!=null){
//                    br.close();
//                }
//            }catch (IOException e){
//                log.error("properties 文件关闭异常！"+e);
//            }
//        }
//        return prop;
//    }
//}
