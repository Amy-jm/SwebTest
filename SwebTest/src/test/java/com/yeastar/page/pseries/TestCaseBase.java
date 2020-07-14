package com.yeastar.page.pseries;

import com.jcraft.jsch.JSchException;
import com.yeastar.controllers.BaseMethod;
import com.yeastar.untils.*;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.lang.reflect.Method;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;

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
        log.debug("[PBX_URL]{}",PBX_URL);
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
        sleep(WaitUntils.SHORT_WAIT);
        new BrowserUtils().getLogType_Browser(method,webDriver);
        log.debug("[remote session]{}",webDriver);
        webDriver.quit();
        getDriver().quit();
        log.debug("[getDriver quit] ...");
        log.debug("[remote session]{}",webDriver);
//        debugCleanSession();
        log.info( "\r\n****** [TearDown] "+ getTestName(method)+" [Times] "+ DataUtils.getCurrentTime("yyyy-MM-dd hh:mm:ss")+"**********************");
    }


    public String debugCleanSession(){
        String host = "192.168.3.252";
        int port = 22;
        String user = "root";
        String password = "r@@t";
        String command = "curl -sSL http://192.168.3.252:4444/grid/sessions?action=doCleanupActiveSessions";
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
