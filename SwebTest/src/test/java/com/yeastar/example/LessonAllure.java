package com.yeastar.example;

//import com.google.common.base.Verify;

import com.yeastar.untils.AllureReporterListener;
import io.qameta.allure.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.yeastar.swebtest.driver.SwebDriver.ys_apply;

/**
 * Created by Yeastar on 2018/2/9.
 */
@Listeners({AllureReporterListener.class})
public class LessonAllure {
    @BeforeClass
    public void BeforeClass() {

    }
    @Epic("Epic")
    @Feature("Feature")
    @Story("Story 1")
    @Description("Description")
    @Issue("BUG_YD001")
    @TmsLink("YD001")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void TestCase01() throws IOException {

    }
    @Step("1.login pbx")
    public void Methon_01(){

    }

    @Step("2.setting ")
    public void Methon_02(){

    }

    @Step("3.assert ")
    public void Methon_03(){

    }

}
