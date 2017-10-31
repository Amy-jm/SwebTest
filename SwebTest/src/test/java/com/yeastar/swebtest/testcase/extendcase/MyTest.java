package com.yeastar.swebtest.testcase.extendcase;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.AutoCLIPRoutes.AutoCLIPRoutes;
import com.yeastar.swebtest.tools.ScreenShot;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import gherkin.lexer.Ca;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static com.codeborne.selenide.Condition.id;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Screenshots.takeScreenShot;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.yeastar.swebtest.driver.SwebDriver.initialDriver;
import static com.yeastar.swebtest.driver.SwebDriver.login;

/**
 * Created by Yeastar on 2017/10/11.
 */
public class MyTest extends SwebDriver {

    @BeforeClass
    public void BeforeClass() throws InterruptedException {
//        pjsip.Pj_Init();
        Reporter.infoBeforeClass("执行的操作414"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.shouldBe(Condition.exist);
        pageDeskTop.CDRandRecording.shouldBe(Condition.exist);
        pageDeskTop.maintenance.shouldBe(Condition.exist);
        mySettings.close.click();
//        m_extension.showCDRClounm();
//
    }


//    @Test
    public void A_dd() {
        Reporter.infoExec("执行的操作"); //执行操作
        ScreenShot shot = new ScreenShot();
        TakesScreenshot drivername;
        String filename;
        pageDeskTop.settings.click();
        String currentPath = System.getProperty("user.dir"); //get current work folder
        System.out.println(currentPath);
        filename =  "test_screenshot1.jpg";
        drivername = (TakesScreenshot) webDriver;
        File scrFile = drivername.getScreenshotAs(OutputType.FILE);
        // Now you can do whatever you need to do with it, for example copy somewhere
        try {
            System.out.println("save snapshot path is:"+currentPath+"/"+filename);
            FileUtils.copyFile(scrFile, new File(currentPath+"\\"+filename));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Can't save screenshot");
            e.printStackTrace();
        }
        finally
        {
            System.out.println("screen shot finished");
        }

    }
//    @Test
    public void AssertCaseName()  {
        Reporter.infoExec("测试 YsAssert"); //执行操作

        pageDeskTop.settings.click();
        settings.callControl_panel.click();
        autoCLIPRoutes.autoCLIPRoutes.click();
//        $$("#st-or-timecondition span").findBy(Condition."faaaa")).click();;
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.pbxmonitorShortcut.click();
        ys_waitingTime(5000);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ys_waitingMask();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.pbxmonitorShortcut.click();
        ys_waitingTime(5000);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ys_waitingMask();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.pbxmonitorShortcut.click();
        ys_waitingTime(5000);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ys_waitingMask();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.pbxmonitorShortcut.click();
        ys_waitingTime(5000);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ys_waitingMask();

        System.out.println("异常后");//可以执行
        ys_waitingTime(20000);
    }

//    @Test
    public void CaseName() {
        Reporter.infoExec("执行的操作"); //执行操作

        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.default_content_settings.popups",0);
        chromePrefs.put("download.default_directory","D:\\");

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs",chromePrefs);

        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\driver\\chrome\\chromedriver.exe");
        WebDriver webDriver= new ChromeDriver(options);
        webDriver.get("https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
//
        ys_waitingTime(8000);
        webDriver.findElement(By.id("login-language-inputEl")).click();
        ys_waitingTime(2000);
        webDriver.findElement(By.xpath(".//*[@data-recordindex='1']"));
        ys_waitingTime(2000);
        webDriver.findElement(By.id("login-username-inputEl")).sendKeys("admin");
        webDriver.findElement(By.id("login-password-inputEl")).sendKeys("password");
        ys_waitingTime(2000);
        webDriver.findElement(By.id("login-btn-btnEl")).click();
        ys_waitingTime(5000);
        webDriver.findElement(By.name("control-panel")).click();
        ys_waitingTime(2000);
        webDriver.findElement(By.id("menuextensions")).click();
        ys_waitingTime(13000);
//        System.out.println(webDriver1.findElement(By.xpath(".//div[starts-with(@id,'extension')]//span[text()='Export']")));
        webDriver.findElement(By.xpath(".//div[starts-with(@id,'extension')]//span[text()='Export']")).click();
        ys_waitingTime(8000);
//        pageDeskTop.settings.click();
//        settings.extensions_panel.click();
//        extensions.export.click();
//        takeScreenShot("download test ");
//        settings.extensions_tree.click();
    }
//    @Test
    public void TestCallingStatus() throws InterruptedException {
        Reporter.infoExec("执行的操作"); //执行操作
        pjsip.Pj_CreateAccount(1000,"Yeastar202","UDP",1);
        pjsip.Pj_CreateAccount(1001,"Yeastar202","UDP",2);

        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1001,DEVICE_IP_LAN);

        pjsip.Pj_Make_Call_No_Answer(1000,"1001",DEVICE_IP_LAN);
        ys_waitingTime(3000);
//        if(getExtensionStatus("100", CALLING,20)){
//
//            System.out.println("extension is Talking ");
//        }else {
//            System.out.println("find extension fail");
//        }
        pjsip.Pj_Hangup_All();
    }

    @Test
    public void CaseNameRetry() {
        Reporter.infoExec("执行的操作"); //执行操作

        YsAssert.assertEquals(1,1,"出错了");


    }
    @Test
    public void DCaseName() {
        Reporter.infoExec("执行的操作"); //执行操作
        System.out.println("after error test ........");
        pageDeskTop.settings.click();
        conference.conference.click();

    }
    @AfterClass
    public void CaseafterName() {
        ys_waitingTime(5000);
        Reporter.infoAfterClass("执行的操作"); //执行操作
        quitDriver();
    }
}
