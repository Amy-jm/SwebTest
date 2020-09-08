package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.AllureReporterListener;
import com.yeastar.untils.ExecutionListener;
import com.yeastar.untils.TestNGListenerP;
import com.yeastar.untils.WaitUntils;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.sleep;


@Listeners({ExecutionListener.class, AllureReporterListener.class, TestNGListenerP.class})
@Log4j2
public class TestDebug extends TestCaseBase {


    @Test(invocationCount = 50)
    public void testDebugLogin() {
        step("1:login PBX");
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT * 2);
    }
}




