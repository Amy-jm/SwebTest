package com.yeastar.swebtest.driver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.swebtest.tools.reporter.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.*;

/**
 * Created by GaGa on 2017-04-16.
 */
public class SwebDriver extends Config {
    public static WebDriver webDriver = null;

    /**
     * 初始化Selenide，并根据参数打开浏览器和网页
     * @param browser
     * @param relativeOrAbsoluteUrl
     */
    public static int initialDriver(String browser, String relativeOrAbsoluteUrl) {
        //Selenide的配置信息
        Configuration.timeout = 10000;

        //选择测试浏览器
        //这里保留IE、EDGE等浏览器的判断，以及比较重要的一个：PhantomJS
        if (browser.equals("chrome")) {
            Configuration.browser = "chrome";
            System.setProperty("selenide.browser", "Chrome");
            System.setProperty("webdriver.chrome.driver", ".\\src\\main\\resources\\driver\\chrome\\chromedriver.exe");
        } else if (browser.equals("firefox")) {
            System.setProperty("webdriver.firefox.bin",FIREFOX_PATH);
        } else {
            Logger.error("浏览器参数有误："+browser);
            return 0;
        }

        //如果指定，则打开被测服务器
        if (relativeOrAbsoluteUrl != null) {
            open(relativeOrAbsoluteUrl);
        }

        //返回WebDriver的类型
        webDriver = getWebDriver();
        return 1;
    }

    /**
     * 初始化Selenide，并根据参数打开浏览器
     * @param browser
     */
    public static int initialDriver(String browser) {
        return initialDriver(browser,null);
    }

    /**
     * 登录S系列设备，默认语言为英文
     * @param username
     * @param password
     */
    public static void login(String username,String password) {
        login(username,password,null);
    }

    /**
     * 可选语言登录S系列设备
     * @param username
     * @param password
     * @param language
     */
    public static void login(String username,String password,String language){
        if (language != null) { //不修改语言
            if (language.equalsIgnoreCase("english")) { //修改语言，暂时就中文，英文
                select($(pageLogin.languageSelect), $(pageLogin.english));
            } else {
                select($(pageLogin.languageSelect), $(pageLogin.chineseSimpleFied));
            }
        }
        $(pageLogin.username).setValue(username);
        $(pageLogin.password).setValue(password);
        $(pageLogin.login).click();
        $(pageDeskTop.taskBarUser).should(exist);
    }

    /**
     * 注销登录
     */
    public static void logout() {
        $(pageDeskTop.taskBarUser).click();
        $(pageDeskTop.taskBarUser_Logout).click();
        $(pageDeskTop.messageBox_Yes).click();
        $(pageLogin.username).should(exist);
    }

    /**
     * S系列的下拉框选择过程比较特殊，单独写一个方法出来调用
     * @param select
     * @param value
     */
    public static void select(SelenideElement select, SelenideElement value) {
        select.click();
        value.click();
    }

    public static String executeJs(String js) {
        return (String)((JavascriptExecutor)webDriver).executeScript(js);
    }
}