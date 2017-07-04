package com.yeastar.swebtest.driver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.swebtest.tools.reporter.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.*;

/**
 * Created by GaGa on 2017-04-16.
 */
public class SwebDriver extends Config {
    public static WebDriver webDriver = null;

    public static int initialDriver(String browser) {
        return initialDriver(browser,"");
    }

    public static int initialDriver(String browser, String relativeOrAbsoluteUrl) {
        return initialDriver(browser,relativeOrAbsoluteUrl,null);
    }

    public static int initialDriver(String browser, String relativeOrAbsoluteUrl, String hubUrl) {
        //Selenide的配置信息
        Configuration.timeout = FINDELEMENT_TIMEOUT;
        DesiredCapabilities grid;
        //选择测试浏览器
        //这里保留IE、EDGE等浏览器的判断，以及比较重要的一个：PhantomJS
        if (browser.equals("chrome")) {
            Configuration.browser = "chrome";
            System.setProperty("selenide.browser", "Chrome");
            System.setProperty("webdriver.chrome.driver", CHROME_PATH);
            grid = DesiredCapabilities.chrome();
        } else if (browser.equals("firefox")) {
            System.setProperty("webdriver.firefox.bin",FIREFOX_PATH);
            grid = DesiredCapabilities.firefox();
        } else {
            Logger.error("浏览器参数有误："+browser);
            return 0;
        }

        if (hubUrl != null ) {
            try {
                webDriver = new RemoteWebDriver(new URL(hubUrl),grid);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            webDriver.get(relativeOrAbsoluteUrl);
            setWebDriver(webDriver);
        } else {
            //如果指定，则打开被测服务器
            open(relativeOrAbsoluteUrl);
            //返回WebDriver的类型
            webDriver = getWebDriver();
        }

        return 1;
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
    public static void login(String username,String password,String language) {
        if (language != null) { //不修改语言
            if (language.equalsIgnoreCase("english")) { //修改语言，暂时就中文，英文
                select(pageLogin.language, pageLogin.english);
            } else {
                select(pageLogin.language, pageLogin.chineseSimpleFied);
            }
        }
        pageLogin.username.setValue(username);
        pageLogin.password.setValue(password);
        pageLogin.login.click();
        pageDeskTop.taskBar_User.should(exist);
        sleep(1000);
    }

    /**
     * 注销登录
     */
    public static void logout() {
        pageDeskTop.taskBar_User.click();
        pageDeskTop.taskBar_User_Logout.click();
        pageDeskTop.messageBox_Yes.click();
        pageLogin.username.should(exist);
    }

    public static void setValue(SelenideElement element, String text) {
        element.should(exist).setValue(text);
    }
    /**
     * 点击按钮
     * @param element
     */
    public static void click(SelenideElement element) {
        element.should(exist).click();
    }

    /**
     * S系列的下拉框选择过程比较特殊，单独写一个方法出来调用
     * @param select
     * @param value
     */
    public static void select(SelenideElement select, SelenideElement value) {
        click(select);
        click(value);
    }

    /**
     * Grid表格
     * gridClick 点击按钮
     * gridCheck 单选框按钮
     * gridContent 获取信息按钮
     */
    public static void gridClick(String gridname, int row, int column) {
        executeJs("Ext.query('#'+Ext.query('#'+" +
                gridname + ".id + ' [data-recordindex]')" +
                "[" + --row + "].id + ' tr td img')[" + column + "].click()");
    }

    public static void gridCheck(String gridname, int row, int column) {
        executeJs("Ext.query('#'+Ext.query('#'+" +
                gridname + ".id + ' [data-recordindex]')" +
                "[" + --row + "].id + ' tr td .x-grid-row-checker')[" + column + "].click()");
    }

    public static String gridContent(String gridname, int row, int column) {
        return executeJs("return Ext.query('#'+Ext.query('#'+" +
                gridname + ".id + ' [data-recordindex]')" +
                "[" + --row + "].id + ' tr td')[" + column + "].textContent");
    }

    /**
     * list选择框
     */
    public static void listSelect(String listname, String recordname, ArrayList<String> valuelist) {
        String Id = "";
        for (String value :
                valuelist) {
            String listId = listGetId(listname, recordname, value);
            Id = listId + "," + Id;
        }
        Id = Id.substring(0,Id.length()-1);
        executeJs("Ext.getCmp('" + listname + "').setValue('" + Id + "')");
    }

    public static String listGetId(String listname, String recordname, String value) {
        return executeJs("return  Ext.getCmp('" + listname + "').getStore()." +
                "findRecord('" + recordname + "', \"" + value + "\", 0, false, false, true).data.id");
    }

    public static String executeJs(String js) {
        return (String)((JavascriptExecutor)webDriver).executeScript(js);
    }
}