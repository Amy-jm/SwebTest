package com.yeastar.swebtest.driver;

import java.io.*;
import java.util.Properties;

import static org.bouncycastle.asn1.cms.CMSObjectIdentifiers.data;

/**
 * Created by GaGa on 2017-05-12.
 */
public class DataReader {

    /**
     * Data.properties配置
     */
    public static String SCREENSHOT_PATH = readFromfile("SCREENSHOT_PATH");
    public static String FIREFOX_PATH = readFromfile("FIREFOX_PATH");
    public static String CHROME_PATH = "src\\main\\resources\\driver\\chrome\\chromedriver.exe";

    /**
     * 被测设备的配置信息
     */
    public static String TESTED_IP = readFromfile("TESTED_IP");
    public static String TESTED_ADMIN_USER = readFromfile("TESTED_ADMIN_USER");
    public static String TESTED_ADMIN_PASS = readFromfile("TESTED_ADMIN_PASS");
    public static String TESTED_SMS_USER = readFromfile("TESTED_SMS_USER");

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
        return properties.getProperty(key);
    }
}
