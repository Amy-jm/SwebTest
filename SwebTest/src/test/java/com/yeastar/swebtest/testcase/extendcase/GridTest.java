package com.yeastar.swebtest.testcase.extendcase;

import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/**
 * Created by GaGa on 2017-06-09.
 */
public class GridTest extends SwebDriver {
    @Test
    public void LogTest() throws InterruptedException {
        initialDriver(BROWSER,"http://192.168.4.99","http://192.168.3.13:5555/wd/hub");
        //initialDriver(BROWSER,"http://192.168.4.99");
        login("6205","GaGa6205");
        Thread.sleep(5000);
        logout();
        //需要判断是否driver为remote，然后手动quit，没法通过selenide进行quit
        webDriver.quit();
        Reporter.infoExec("1"); //执行操作
        YsAssert.assertEquals("Number3Test","Number3Test2","判断两个值");
    }
    WebDriver driver = null;
    @Test
    public void CaseName() {
        System.setProperty("selenide.browser", "Chrome");
        System.setProperty("webdriver.chrome.driver", CHROME_PATH);
        //设置Webdriver启动chrome为默认用户的配置信息（包括书签、扩展程序等）
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=C:/Users/test/AppData/Local/Google/Chrome/User Data");
        driver = new ChromeDriver(options);

        driver.get("https://192.168.7.152:8088");
        ys_waitingTime(8000);
        driver.findElement(By.id("login-username-inputEl")).sendKeys("admin");
        driver.findElement(By.id("login-password-inputEl")).sendKeys("password");
        driver.findElement(By.id("login-btn-btnEl")).click();
        ys_waitingTime(10000);
        driver.findElement(By.name("control-panel")).click();
        ys_waitingTime(5000);
        driver.findElement(By.id("menuextensions")).click();
        ys_waitingTime(10000);
        driver.findElement(By.xpath(".//div[starts-with(@id,'extension')]//span[text()='Import']")).click();
        ys_waitingTime(5000);
//        driver.findElement(By.id("st-exten-filename-button-btnEl")).click();
        executeJs("Ext.get('st-exten-filename-button-fileInputEl').dom.click()");
//        pageDeskTop.settings.click();
//        settings.extensions_panel.click();
//        extensions.export.click();
//        pageLogin.username.setValue(username);
//        pageLogin.password.setValue(password);
//        pageLogin.login.click();

       ys_waitingTime(5000);


    }
    @AfterClass
    public void CaseName2() {
        Reporter.infoAfterClass("执行的操作"); //执行操作
        driver.quit();
    }

}
