package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.AllureReporterListener;
import com.yeastar.untils.ExecutionListener;
import com.yeastar.untils.TestNGListenerP;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


@Listeners({ExecutionListener.class, AllureReporterListener.class, TestNGListenerP.class})
@Log4j2
public class TestDebug extends TestCaseBase {


    @Test(invocationCount = 30, groups = "debug")
    public void testDebugLogin() {
        step("1:login PBX");
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));
    }
}




