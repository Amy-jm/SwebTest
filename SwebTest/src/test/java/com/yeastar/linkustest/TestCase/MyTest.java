package com.yeastar.linkustest.TestCase;

import com.yeastar.linkustest.driver.AppDriver;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static com.codeborne.selenide.Selenide.sleep;

public class MyTest extends AppDriver {

    public static AndroidDriver driver;
    public static AndroidDriver driver2;
    @BeforeTest
    public static void  beforeTEst(){}

    @Test
    public void   a_test(){
    }
    @Test
    public static   void main() throws MalformedURLException, InterruptedException {


        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName", "TWHU49UCTCL7UWMV");
        capabilities.setCapability("udid", "TWHU49UCTCL7UWMV");// 测试机adb devices获取
        capabilities.setCapability("automationName", "Appium");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("platformVersion", "6.0");
        capabilities.setCapability("appPackage", "com.yeastar.linkus");
        capabilities.setCapability("appActivity", "com.yeastar.linkus.business.WelcomeActivity");
        capabilities.setCapability("resetKeyboard", "False");
        capabilities.setCapability("noReset", "True");//防止重安装app
        driver = new AndroidDriver(new URL("http://127.0.0.1:4727/wd/hub"), capabilities);

        DesiredCapabilities capabilities2 = new DesiredCapabilities();
        capabilities2.setCapability("deviceName", "DU2TAN151A022043");
        capabilities2.setCapability("udid", "DU2TAN151A022043");// 测试机adb devices获取
        capabilities2.setCapability("automationName", "Appium");
        capabilities2.setCapability("platformName", "Android");
        capabilities2.setCapability("platformVersion", "6.0");
        capabilities2.setCapability("appPackage", "com.yeastar.linkus");
        capabilities2.setCapability("appActivity", "com.yeastar.linkus.business.WelcomeActivity");
        capabilities2.setCapability("resetKeyboard", "False");
        capabilities2.setCapability("noReset", "True");//防止重安装app
        driver2 = new AndroidDriver(new URL("http://127.0.0.1:4725/wd/hub"), capabilities2);

        driver.findElementById("com.yeastar.linkus:id/me_layout").click();
        driver.findElementById("com.yeastar.linkus:id/dial_layout").click();
        driver.findElementByXPath("//*[@resource-id='com.yeastar.linkus:id/tab_dial_dialpad_gv']/android.widget.RelativeLayout[5]").click();


        driver2.findElementById("com.yeastar.linkus:id/me_layout").click();
        driver2.findElementById("com.yeastar.linkus:id/dial_layout").click();
        driver2.findElementByXPath("//*[@resource-id='com.yeastar.linkus:id/tab_dial_dialpad_gv']/android.widget.RelativeLayout[1]").click();
        Thread.sleep(20000);


    }

    @AfterClass
    public static void afterTest(){
        sleep(5000);
        driver.quit();
        driver2.quit();
    }
}
