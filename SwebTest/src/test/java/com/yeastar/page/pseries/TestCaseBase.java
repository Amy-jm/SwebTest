package com.yeastar.page.pseries;

import com.yeastar.controllers.BaseMethod;
import com.yeastar.untils.BrowserUtils;
import com.yeastar.untils.DataUtils;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;

import java.lang.reflect.Method;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

@Log4j2
public class TestCaseBase extends BaseMethod {
    public PageEngine auto;
    private WebDriver webDriver;
    public SoftAssert softAssert;

    public TestCaseBase(){
        softAssert = new SoftAssert();
    }



    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) throws Exception
    {
        log.info("====== [SetUp] " + getTestName(method) + " [Times] " + DataUtils
                .getCurrentTime("yyyy-MM-dd hh:mm:ss") +
                "======");
        webDriver = initialDriver(BROWSER,PBX_URL,method);
        setDriver(webDriver);
        open(PBX_URL);
        auto = new PageEngine();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(Method method) throws Exception
    {
        new BrowserUtils().getLogType_Browser(method,webDriver);
        getWebDriver().quit();
        log.info( "****** [TearDown] "+ getTestName(method)+" [Times] "+ DataUtils.getCurrentTime("yyyy-MM-dd hh:mm:ss")+"**********************");
    }



}
