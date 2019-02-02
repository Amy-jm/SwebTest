package com.yeastar.swebtest.testcase;

import com.codeborne.selenide.Condition;

//import com.google.common.base.Verify;
import com.google.common.base.Verify;
import com.google.common.base.VerifyException;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import jxl.common.AssertionFailed;
import org.apache.tools.ant.taskdefs.WaitFor;
import org.apache.tools.ant.types.Assertions;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;
import static com.codeborne.selenide.Selenide.refresh;
import static com.yeastar.swebtest.driver.SwebDriver.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by Yeastar on 2018/2/9.
 */
public class test {
    @BeforeClass
    public void BeforeClass() {
//        pjsip.Pj_Init();
        initialDriver(BROWSER, "https://" + DEVICE_IP_LAN + ":" + DEVICE_PORT + "/");
        login(LOGIN_USERNAME, LOGIN_PASSWORD);
        if (!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")) {
            ys_waitingMask();
            mySettings.close.click();
        }

    }
    @Test
    public void CaseName() throws IOException {
//        Reporter.infoExec("执行的操作"); //执行操作
//        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Init();
        System.out.println("sadfffffffffffffffffffff");
//        YsAssert.fail("sdfasfd");
        System.out.println(DEVICE_VERSION);
//        DEVICE_VERSION = "30.8.6.06";
//        System.out.println(DEVICE_VERSION);
//        String[] version = DEVICE_VERSION.split("\\.");
//
//        if (Integer.valueOf(version[1]) <= 8) {
//            System.out.println("sfassdf");
//        }
//        pageDeskTop.taskBar_Main.click();
//        pageDeskTop.settingShortcut.click();
//        settings.callFeatures_panel.click();
//        ringGroup.ringGroup.click();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.extensions_panel.click();
        extensions.add.click();
//        ringGroup.add.click();
        ys_waitingMask();
//        ys_waitingMask();
//        ys_waitingTime(3000);
        Assert a;
//        $(By.id("st-rg-noansweraction-labelEl")).scrollIntoView(false);
        System.out.printf("ddddddd "+ System.getProperty("selenide.reportsUrl"));
//        Runtime.getRuntime().exec("taskkill /im 123.exe /f");
//        Runtime.getRuntime().exec("taskkill /im chromedriver.exe /f");
//        ys_waitingTime(5000);
        System.out.println("111111111111");
        System.out.println("333333333333");
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Reporter.infoAfterClass("执行的操作"); //执行操作
        Thread.sleep(15000);
        Reporter.infoAfterClass("执行完毕：======  InboundRoute  ======"); //执行操作
//        pjsip.Pj_Destory();
        quitDriver();
        System.out.println("quitDriver end");
//        killChromePid();
        Thread.sleep(5000);
    }

}
