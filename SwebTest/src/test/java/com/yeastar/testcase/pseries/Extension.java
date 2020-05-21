package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.driver.SwebDriverP;
import com.yeastar.swebtest.pseries.pages.LoginPage;
//import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import com.yeastar.untils.AllureReporterListener;
import com.yeastar.untils.RetryListener;
import com.yeastar.untils.TestNGListener;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.*;

import java.lang.reflect.Method;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.swebtest.driver.DataReader2.*;


@Listeners({AllureReporterListener.class, RetryListener.class,TestNGListener.class})
@Log4j2
public class Extension extends SwebDriverP {

    @BeforeClass
    public void BeforeClass() {
        log.debug("开始执行：======  Extension  ======"); //执行操作
    }


    @BeforeMethod
    @Step("[BeforeMethod] Config test environment·····")
    public void BeforeMethod(Method method) {
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/",method);
        loginPage.login(LOGIN_USERNAME,LOGIN_PASSWORD);
    }

    @Step("{0}")
    public void step(String desc){
        log.debug("[step] "+desc);
        sleep(5);
//        Cookie cookie = new Cookie("zaleniumMessage", desc);
//        webDriver.manage().addCookie(cookie);
    }


    @Epic("Extension")
    @Feature("Feature")
    @Story("Extension story 1")
    @Description("loginMe")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void A_loginMe(){
        log.debug("[*********start  test***********]");
        step("1-5:登录 PBX");

        step("2-5:创建分机号1000");

        step("[Assert]3-5:验证保存成功");

        step("4-5：loginMe");
    }


    @AfterClass
    public void AfterClass(){
        ys_waitingTime(5000);
        log.debug("执行完毕：======  Extension  ======"); //执行操作
//        pjsip.Pj_Unregister_Account(3000);
//        pjsip.Pj_Destory();
        quitDriver();
        ys_waitingTime(10000);
        //killChromePid();
        }

}




