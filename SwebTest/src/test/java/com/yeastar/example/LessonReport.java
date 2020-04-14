package com.yeastar.example;

//import com.google.common.base.Verify;

import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.pjsip.CLibrary;
import com.yeastar.untils.AllureReporterListener;
import com.yeastar.untils.RetryListener;
import com.yeastar.untils.TestNGListener;
import com.yeastar.untils.TestNGRetry;
import io.qameta.allure.*;
import org.openqa.selenium.Cookie;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.lang.reflect.Method;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.Selenide.sleep;

/**
 * Created by Yeastar on 2018/2/9.
 */
@Listeners({AllureReporterListener.class, RetryListener.class})
public class LessonReport extends SwebDriver{
    @BeforeClass
    @Step("[BeforeClass] Init test environment·····")
    public void BeforeClass() {
//        pjsip.Pj_Init();
//        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        //initialDriver(BROWSER,"http://192.168.4.99");
//        login("6205","GaGa6205");
    }

    @BeforeMethod
    @Step("[BeforeMethod] Config test environment·····")
    public void BeforeMethod(Method method) {
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/",method);
        step("open chrome start to test..........");
    }


    @AfterMethod
    @Step("[AfterMethod] Restore  test environment quite driver·····")
    public void AfterMethod() {
        close();
    }

    @AfterClass
    @Step("[AfterClass] Restore test class environment·····")
    public void AfterClass() {

    }

    @Epic("Epic")
    @Feature("Feature")
    @Story("Story 1")
    @Description("Description")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void TestCase01() {
        Cookie cookie = new Cookie("zaleniumTestName", "TestCase01");
        webDriver.manage().addCookie(cookie);
        //TODO 调整到 SwebDriver中通过method统一新增，减少原有代码的带动
    }

    @Epic("Epic")
    @Feature("Feature")
    @Story("Story 1")
    @Description("Description")
    @Issue("BUG_YD001")
    @TmsLink("YD001")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void TestCase02() throws IOException {
//        Cookie cookie = new Cookie("zaleniumTestName", "anyName");
//        webDriver.manage().addCookie(cookie);
        step("1. login pbx .....................");
        Methon_01();

        step("2. start method 2 ...................");
        Methon_02();

        step("3. start method 3 ..................");
        Methon_03();

        step("[Assert] expect true");
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
    public void TestCase03_FailedNotFoundElement() throws IOException {
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
    }

    @Epic("Epic")
    @Feature("Feature")
    @Story("Story 2")
    @Description("Description test pjsip on linux")
    @Issue("BUG_YD001")
    @TmsLink("YD001")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void TestCase04_PJSIP_Linux() throws IOException {
        pjsip.Pj_Init();
    }


    @Epic("Epic")
    @Feature("Feature")
    @Story("Story 2")
    @Description("Description test pjsip on linux")
    @Issue("BUG_YD001")
    @TmsLink("YD001")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void TestCase05_SO_Add() throws IOException {
        CLibrary.INSTANCE.toString();
    }

    @Step("1.login pbx")
    public void Methon_01(){
        //更新用例状态 zalenium
        Cookie cookie = new Cookie("zaleniumTestPassed", "false");
        webDriver.manage().addCookie(cookie);
        login(LOGIN_USERNAME,LOGIN_PASSWORD,"english");
    }

    @Step("2.setting ")
    public void Methon_02(){

    }

    @Step("3.assert ")
    public void Methon_03(){

    }


    /**
     * 可选语言登录S系列设备
     * @param username
     * @param password
     * @param language
     */
    public static void login(String username,String password,String language) {
        if (language != null) { //不修改语言
            if (language.equalsIgnoreCase("english")) { //修改语言，暂时就中文，英文
                select(pageLogin.language, pageLogin.english);
            } else {
                select(pageLogin.language, pageLogin.chineseSimpleFied);
            }
        }
        pageLogin.username.setValue(username);
        pageLogin.password.setValue(password);
        ys_waitingTime(1000);
        pageLogin.login.click();

        if(pageDeskTop.pp_comfirm.isDisplayed()){
            setCheckBox(pageDeskTop.pp_agreement_checkBox,true);
            pageDeskTop.pp_comfirm.click();
        }
        pageDeskTop.taskBar_User.should(exist);

    }


}
