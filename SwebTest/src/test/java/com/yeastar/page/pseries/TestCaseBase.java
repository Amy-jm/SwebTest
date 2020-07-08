package com.yeastar.page.pseries;

import com.yeastar.controllers.BaseMethod;
import com.yeastar.untils.BrowserUtils;
import com.yeastar.untils.DataUtils;
import com.yeastar.untils.EmptyUtil;
import com.yeastar.untils.WaitUntils;
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
        String strURL="http://192.168.3.252:4444/grid/sessions?action=doCleanupActiveSessions";
        String result = "";


        try {
            URL url= new URL(strURL);
            URLConnection connection = url.openConnection();
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine())!= null)
            {
                result += line;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

            return result;

    }



}
