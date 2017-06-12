package com.yeastar.swebtest.driver;

import com.yeastar.swebtest.pobject.PageDeskTop;
import com.yeastar.swebtest.pobject.PageLogin;
import com.yeastar.swebtest.pobject.PageSettings.PageSettings;

import static com.yeastar.swebtest.driver.DataReader.*;

/**
 * Created by GaGa on 2017-05-19.
 */
public class Config extends DataReader {

    public static long FINDELEMENTTIMEOUT = 10000;
    public static String CHROME = "chrome";
    public static String FIREFOX = "firefox";
    public static String IE = "ie";
    /**
     * 初始化对象库的对象
     */
    public static PageLogin pageLogin = new PageLogin();
    public static PageDeskTop pageDeskTop = new PageDeskTop();
    public static PageSettings pageSettings = new PageSettings();

    /**
     * log的配置
     */
    //public static String logFile = "config/log4j.properties";
}
