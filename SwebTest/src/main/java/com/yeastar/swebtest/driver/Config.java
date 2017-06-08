package com.yeastar.swebtest.driver;

import com.yeastar.swebtest.pobject.PageDeskTop;
import com.yeastar.swebtest.pobject.PageLogin;
import com.yeastar.swebtest.pobject.PageSettings.PageSettings;

/**
 * Created by GaGa on 2017-05-19.
 */
public class Config {
    /**
     * 初始化对象库的对象
     */
    public static PageLogin pageLogin = new PageLogin();
    public static PageDeskTop pageDeskTop = new PageDeskTop();
    public static PageSettings pageSettings = new PageSettings();

    /**
     * Data.properties配置
     */
    public static String SCREENSHOT_PATH = "F:\\pic\\";
    public static String FIREFOX_PATH = "D:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe";

    /**
     * log的配置
     */
    //public static String logFile = "config/log4j.properties";
}
