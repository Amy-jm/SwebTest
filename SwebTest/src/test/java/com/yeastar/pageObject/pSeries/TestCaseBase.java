package com.yeastar.pageObject.pSeries;

import com.yeastar.controllers.BaseMethod;
import com.yeastar.controllers.WebDriverFactory;
import com.yeastar.pageObject.pSeries.LoginPage;
import com.yeastar.swebtest.driver.SwebDriverP;
import com.yeastar.untils.DataUtils;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

@Log4j2
public class TestCaseBase extends BaseMethod {
    public PageEngine auto;
    private WebDriver webDriver;


    @BeforeMethod
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

    @AfterMethod
    public void afterMethod() throws Exception
    {
        getWebDriver().quit();
    }



}
