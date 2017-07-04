package com.yeastar.swebtest.driver;

import com.yeastar.swebtest.pobject.*;
import com.yeastar.swebtest.pobject.Settings.*;
import com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.*;

/**
 * Created by GaGa on 2017-05-19.
 */
public class Config extends DataReader {

    public static long TEST_TIMEOUT = 3000;  //测试过程的时间差
    public static long FINDELEMENT_TIMEOUT = 10000;  //元素查找的时间差
    public static String CHROME = "chrome";
    public static String FIREFOX = "firefox";
    public static String IE = "ie";
    /**
     * 初始化对象库的对象
     */
    public static PageLogin pageLogin = new PageLogin();
    public static PageDeskTop pageDeskTop = new PageDeskTop();
    public static Settings settings = new Settings();
    public static Extensions extensions = new Extensions();
    /**
     * log的配置
     */
    //public static String logFile = "config/log4j.properties";
}
