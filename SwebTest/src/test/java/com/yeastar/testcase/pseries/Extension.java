package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.pseries.pages.LoginPage;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import com.yeastar.untils.AllureReporterListener;
import com.yeastar.untils.RetryListener;
import com.yeastar.untils.TestNGListener;
import io.qameta.allure.Step;
import org.testng.annotations.*;

import java.lang.reflect.Method;


@Listeners({AllureReporterListener.class, RetryListener.class, TestNGListener.class})
public class Extension extends com.yeastar.swebtest.driver.SwebDriverP{
    LoginPage loginPage = new LoginPage();
    @BeforeClass
    public void BeforeClass() {
        Reporter.infoBeforeClass("开始执行：======  Extension  ======"); //执行操作
    }


    @BeforeMethod
    @Step("[BeforeMethod] Config test environment·····")
    public void BeforeMethod(Method method) {
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/",method);
        loginPage.login(LOGIN_USERNAME,LOGIN_PASSWORD);
    }


    @Test()
    public void A_loginMe(){
        step("1-5:登录 PBX");

        step("2-5:创建分机号1000");

        step("[Assert]3-5:验证保存成功");

        step("4-5：loginMe");
    }


    @AfterClass
    public void AfterClass(){
        ys_waitingTime(5000);
        Reporter.infoAfterClass("执行完毕：======  Extension  ======"); //执行操作
//        pjsip.Pj_Unregister_Account(3000);
//        pjsip.Pj_Destory();
        quitDriver();
        ys_waitingTime(10000);
        killChromePid();    }

}




