package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.yeastar.pageObject.pSeries.HomePage;
import com.yeastar.pageObject.pSeries.LoginPage;
import com.yeastar.pageObject.pSeries.TestCaseBase;
//import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.untils.AllureReporterListener;
import com.yeastar.untils.RetryListener;
import com.yeastar.untils.TestNGListenerP;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Cookie;
import org.testng.Assert;
import org.testng.annotations.*;

import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;


@Listeners({AllureReporterListener.class, RetryListener.class, TestNGListenerP.class})
@Log4j2
public class Extension extends TestCaseBase {

    @Epic("Extension")
    @Feature("Feature")
    @Story("Extension story 1")
    @Description("loginMe")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void A_loginMe(){
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1000");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);

        step("3:验证保存成功");

        step("4：loginMe");
//        auto.homePage().logout();

        step("5：logout");
        auto.homePage().logout();
    }

}




