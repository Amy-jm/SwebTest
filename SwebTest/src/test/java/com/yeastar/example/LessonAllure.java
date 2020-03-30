package com.yeastar.example;

//import com.google.common.base.Verify;

import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.untils.AllureReporterListener;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;

import static com.yeastar.swebtest.driver.SwebDriver.ys_apply;

/**
 * Created by Yeastar on 2018/2/9.
 */
@Listeners({AllureReporterListener.class})
public class LessonAllure  extends SwebDriver{
    @BeforeClass
    @Step("[BeforeClass] Init test environment·····")
    public void BeforeClass() {
        pjsip.Pj_Init();
    }

    @BeforeMethod
    @Step("[BeforeMethod] Config test environment·····")
    public void BeforeMethod() {
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
    }


    @BeforeMethod
    @Step("[AfterMethod] Restore  test environment·····")
    public void AfterMethod() {
    }

    @AfterClass
    @Step("[AfterClass] Restore test class environment·····")
    public void AfterClass() {

    }

    @Epic("Epic")
    @Feature("Feature")
    @Story("Story 1")
    @Description("Description")
    @Issue("BUG_YD001")
    @TmsLink("YD001")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void TestCase01_Passed() throws IOException {
        Methon_01();
        Methon_02();
        Methon_03();
        Assert.assertTrue(true);
    }

    @Epic("Epic")
    @Feature("Feature")
    @Story("Story 1")
    @Description("Description")
    @Issue("BUG_YD001")
    @TmsLink("YD001")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void TestCase02_Failed() throws IOException {
        Methon_01();
        Methon_02();
        Methon_03();
        Assert.assertTrue(false);
    }

    @Step("1.login pbx")
    public void Methon_01(){
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
    }

    @Step("2.setting ")
    public void Methon_02(){

    }

    @Step("3.assert ")
    public void Methon_03(){

    }


}
