package com.yeastar.page.pseries;

import com.jcraft.jsch.JSchException;
import com.yeastar.controllers.BaseMethod;
import com.yeastar.untils.*;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

@Log4j2
public class TestCaseBase extends BaseMethod {
    public PageEngine auto;
    private WebDriver webDriver;
    public SoftAssert softAssert;

//    public TestCaseBase(){
//        softAssert = new SoftAssert();
//    }



    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) throws Exception
    {
        log.info("\r\n====== [SetUp] " + getTestName(method) + " [Times] " + DataUtils
                .getCurrentTime("yyyy-MM-dd hh:mm:ss") +
                "======");
        webDriver = initialDriver(BROWSER,PBX_URL,method);
        setDriver(webDriver);
        open(PBX_URL);
        auto = new PageEngine();
        softAssert = new SoftAssert();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(Method method) throws Exception
    {
        log.info("\r\n====== [afterMethod] " + getTestName(method) + " [Times] " + DataUtils
                .getCurrentTime("yyyy-MM-dd hh:mm:ss") +
                "======");
        if(EmptyUtil.isNotEmpty(pjsip)){
            log.debug("[start destroy pjsip]");
            pjsip.Pj_Destory();

        }
        new BrowserUtils().getLogType_Browser(method,webDriver);
//        log.debug("[afterMethod before session]{}",getWebDriver().manage());
        getDriver().close();
        log.debug("[afterMethod] driver close .");
        getDriver().quit();
        getWebDriver().quit();
        log.debug("[afterMethod] driver quit .");
        log.info( "\r\n****** [TearDown] "+ getTestName(method)+" [Times] "+ DataUtils.getCurrentTime("yyyy-MM-dd hh:mm:ss")+"**********************");
        Thread.sleep(WaitUntils.SHORT_WAIT);
        //
        //log.debug("[clean session] {}"+debugCleanSession());
        Thread.sleep(WaitUntils.SHORT_WAIT);
    }


    public String debugCleanSession(){
        String host = "192.168.3.252";
        int port = 22;
        String user = "root";
        String password = "r@@t";
        String command = "curl -sSL http://localhost:4444/grid/sessions?action=doCleanupActiveSessions";
        String result = "";

        try {
            result =  SSHLinuxUntils.exeCommand(host,port,user,password,command);
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }



}
