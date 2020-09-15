package com.yeastar.swebtest.driver;



import com.yeastar.untils.PropertiesUntils;
import com.yeastar.untils.UIMapUtils;
import lombok.extern.log4j.Log4j2;
import top.jfunc.json.impl.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
//import static org.bouncycastle.asn1.cms.CMSObjectIdentifiers.data;

/**
 * Created by GaGa on 2017-05-12.
 */
@Log4j2
public class DataReader2 {
    
    final static String DATA_PROPERTIES_FILE_PATH ="/data.properties";
    final static String CONFIG_PROPERTIES_FILE_PATH ="/config.properties";
    final static String PJSIP_PROPERTIES_FILE_PATH ="/pjsip.properties";
//    final static String loginName = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH, "loginName");
    public final static String PJSIP_COMMAND_DELTREE_REGISTRAR = "export LD_LIBRARY_PATH=/ysdisk/ysapps/pbxcenter/lib;asterisk -rx \"database deltree registrar\"";
    public final static String ASTERISK_CLI = "export LD_LIBRARY_PATH=/ysdisk/ysapps/pbxcenter/lib;asterisk -rx \"%s\"";
    public final static String CLEAR_CLI_LOG = "export LD_LIBRARY_PATH=/ysdisk/ysapps/pbxcenter/lib;echo '' > /ysdisk/syslog/pbxlog.0";
    public final static String PJSIP_COMMAND_reboot = "export PATH=$PATH:$HOME/bin:/sbin:/usr/bin:/usr/sbin;reboot";
    public final static String SHOW_CLI_LOG = "export PATH=$PATH:$HOME/bin:/sbin:/usr/bin:/usr/sbin;cat /ysdisk/syslog/pbxlog.0";
    public final static int PJSIP_TCP_PORT = 8022;
    public final static String PJSIP_SSH_PASSWORD = "";
    public final static String PJSIP_SSH_USER = "ls@yf";

    public final String PBX_URL = "https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT;

    public static JSONObject UI_MAP = UIMapUtils.getUIMapHandle();
    /**
     * Data.properties配置
     */
    public static String SCREENSHOT_PATH = System.getProperty("user.dir")+"\\"+ PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH, "SCREENSHOT_PATH");
    public static String FIREFOX_PATH = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH, "FIREFOX_PATH");
//    public static String CHROME_PATH = "src\\main\\resources\\driver\\chrome\\chromedriver.exe";
    public static String CHROME_PATH =PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"CHROME_PATH");

    /**
     * 导入文件指定
     */
    public static String EXPORT_PATH = System.getProperty("user.dir")+"\\"+ PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"EXPORT_PATH");
    public static String VOICEPACKAGE_NAME = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"VOICEPACKAGE_NAME");
    public static String SYSTEM_PROMPT_LANGUAGE = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"SYSTEM_PROMPT_LANGUAGE");

    /**
     * 被测设备的配置信息
     */
    public static String PRODUCT = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"PRODUCT");
    public static String DEVICE_IP_LAN = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"DEVICE_IP_LAN");
    public static String DEVICE_IP_WAN = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"DEVICE_IP_WAN");
    public static String DEVICE_IP_SUBNETMASK = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"DEVICE_IP_SUBNETMASK");
    public static String DEVICE_IP_GATEWAY = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"DEVICE_IP_GATEWAY");
    public static String DEVICE_IP_DNS = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"DEVICE_IP_DNS");
    public static String DEVICE_PORT = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"DEVICE_PORT");
    public static String LOGIN_USERNAME = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"LOGIN_USERNAME");
    public static String LOGIN_PASSWORD = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"LOGIN_PASSWORD");
    public static String EMAIL = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"EMAIL");

    public static String LOGIN_ADMIN = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"LOGIN_ADMIN");
    public static String SSH_PORT = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"SSH_PORT");
    public static String USERNAEM_LS = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH, "USERNAEM_LS");
    public static String USERNAME_SUPPORT = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"USERNAME_SUPPORT");
    public static String PASSWORD_SUPPORT = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"PASSWORD_SUPPORT");
    public static int UDP_PORT = Integer.parseInt(PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"UDP_PORT"));
    public static int TCP_PORT = Integer.parseInt(PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"TCP_PORT"));
    public static int TLS_PORT = Integer.parseInt(PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"TLS_PORT"));
    public static int UDP_PORT_ASSIST_1 = Integer.parseInt(PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"UDP_PORT_ASSIST_1"));
    public static int UDP_PORT_ASSIST_2 = Integer.parseInt(PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"UDP_PORT_ASSIST_2"));
    public static int UDP_PORT_ASSIST_3 = Integer.parseInt(PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"UDP_PORT_ASSIST_3"));
    public static int AMI_PROT = Integer.parseInt(PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"AMI_PORT"));

    /**
     * 测试模块定义
     */
    public static String FXS_1 = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"FXS_1");
//    public static String FXS_2 = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"FXS_2");
    public static String FXO_1 = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"FXO_1");
//    public static String FXO_2 = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"FXO_2");
    public static String BRI_1 = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"BRI_1");
    public static String BRI_2 = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"BRI_2");
    public static String GSM = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"GSM");
    public static String E1 = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"E1");
    public static String DEVICE_ASSIST_GSM = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"DEVICE_ASSIST_GSM").trim();
    public static String DEVICE_TEST_GSM = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"DEVICE_TEST_GSM").trim();

    public static String SIPTrunk = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"SIPTRUNK");
    public static String SIPTrunk2 = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"SIPTRUNK2");
    public static String ACCOUNTTRUNK = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"ACCOUNTTRUNK");
    public static String IAXTrunk = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH, "IAXTRUNK");
    public static String SPS = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"SPS");
    public static String SPX = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"SPX");


    /**
     * 存储设备
     */
    public static String NETWORK_DEVICE_NAME = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"NETWORK_DEVICE_NAME");
    public static String NETWORK_DEVICE_IP = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"NETWORK_DEVICE_IP");
    public static String NETWORK_DEVICE_SHARE_NAME = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"NETWORK_DEVICE_SHARE_NAME");
    public static String NETWORK_DEVICE_USER_NAME =PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"NETWORK_DEVICE_USER_NAME");
    public static String NETWORK_DEVICE_USER_PASSWORD =PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"NETWORK_DEVICE_USER_PASSWORD");
    public static String DEVICE_RECORD_NAME = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"DEVICE_RECORD_NAME");

    /**
     * 辅助设备ip
     */
    public static String DEVICE_ASSIST_1 = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"DEVICE_ASSIST_1");
    public static String DEVICE_ASSIST_2 =PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"DEVICE_ASSIST_2");
    public static String DEVICE_ASSIST_3 =PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"DEVICE_ASSIST_3");

    public static String DEVICE_VERSION = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"VERSION");
    public static String[] VERSION_SPLIT = DEVICE_VERSION.split("\\.");
    public static String BROWSER = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"BROWSER");
    public static String URL = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"URL");

    public static String HTTP_SERVER_IMAGE = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"HTTP_SERVER_IMAGE");
    /**
     * 导入文件名称
     */
    public static String IMPORT_EXTENSION = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"IMPORT_EXTENSION");
    /**
     * 本地log保存
     */
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
    static String currentTime = String.valueOf(sdf.format(new Date()))+"_";
    public static String LOCAL_LOG_FILE =  currentTime+PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"LOCAL_LOG_FILE");
    public static String AMI_LOG_FILE =  currentTime+PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"AMI_LOG_FILE");
    /**
     * 定义出错重跑
     */
    public static String RETRY_COUNT = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"RETRY_COUNT");


    /**
     * PJSIP指令
     */
    public static String PJSIP_SHOW_AOR = PropertiesUntils.getInstance().getPropertiesValue(PJSIP_PROPERTIES_FILE_PATH,"PJSIP_SHOW_AOR")+" ";
    public static String PJSIP_SHOW_ENDPOINT = PropertiesUntils.getInstance().getPropertiesValue(PJSIP_PROPERTIES_FILE_PATH,"PJSIP_SHOW_ENDPOINT")+" ";


    /**
     * 框架相关配置 config
     */
    public  static String IS_RUN_REMOTE_SERVER = PropertiesUntils.getInstance().getPropertiesValue(CONFIG_PROPERTIES_FILE_PATH,"IS_RUN_REMOTE_SERVER");
    public  static String GRID_HUB_IP = PropertiesUntils.getInstance().getPropertiesValue(CONFIG_PROPERTIES_FILE_PATH,"GRID_HUB_IP");
    public  static String GRID_HUB_PORT = PropertiesUntils.getInstance().getPropertiesValue(CONFIG_PROPERTIES_FILE_PATH,"GRID_HUB_PORT");
    public  static String RECORD_VIDEO = PropertiesUntils.getInstance().getPropertiesValue(CONFIG_PROPERTIES_FILE_PATH,"RECORD_VIDEO");
    public  static String screenResolution = PropertiesUntils.getInstance().getPropertiesValue(CONFIG_PROPERTIES_FILE_PATH,"screenResolution");//全局分辨率     用户分辨率 > 全局分辨率
    public static String screenResolutionUser = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"screenResolutionUser");//用户分辨率    用户分辨率 > 全局分辨率


    /**
     * 根据key返回key值
     * @param key
     * @return keyvalue
     */
    public static String readFromfile(String key) {
        Properties properties = new Properties();
        InputStream inputStream = null;
//        try {
//            inputStream = new BufferedInputStream(new FileInputStream("data.properties"));
        try {
            String FilePath = System.getProperty("user.dir")+File.separator+"test-classes"+File.separator;
            log.debug("[user.dir]"+System.getProperty("user.dir"));
            log.debug("[File file]"+FilePath);
            inputStream = new FileInputStream(new File(FilePath+"data.properties"));
//            inputStream = DataReader.class.getResourceAsStream("data.properties");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("Key: "+key+" Value: "+properties.getProperty(key)+ " is Null:"+properties.getProperty(key).equals("null"));
        if (properties.getProperty(key).equals("null")){
            return "null";
        }else{
            return properties.getProperty(key);
        }
    }

    private static Properties prop;


    /**
     * 获取prop文件，prop需要在资源文件路径下
     * @param propPath
     * @return
     */
    public  Properties getPropertie(String propPath) {
        prop = new Properties();
        BufferedReader br=null;
        try {
            br=new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(propPath), "UTF-8"));
            prop.load(br);
        } catch (Exception e) {
            log.error("读取配置文件异常！"+e);
        }   finally {
            try{
                if(br!=null){
                    br.close();
                }
            }catch (IOException e){
                log.error("properties 文件关闭异常！"+e);
            }
        }
        return prop;
    }
}
