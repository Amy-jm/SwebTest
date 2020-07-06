package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.IExtensionPageElement;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.swebtest.pobject.Settings.System.Email.Email;
import com.yeastar.untils.DataUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.yeastar.swebtest.driver.SwebDriverP.getExtensionStatus;

/**
 * @program: SwebTest
 * @description: Extension user password and login model
 * @author: huangjx@yeastar.com
 * @create: 2020/07/06
 */
@Log4j2
public class TestExtensionUserPasswordAndLoginModel extends TestCaseBase {
    /**
     * 前提条件
     * 1.添加1001分机到 路由AutoTest_Route
     */
    public void prerequisite(){
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_role_management);
        auto.extensionPage().deleRole("extension").addNewRole("extension");
    }


    /**
     * 判断邮箱工作是否正常
     * @return
     */
    public Boolean isEmailServerWork(){
        step("进入System Email界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.system, HomePage.Menu_Level_2.system_tree_email);
        auto.extensionPage().testBtn.shouldBe(Condition.enabled).click();

        sleep(WaitUntils.SHORT_WAIT);
        List<WebElement> elements_input = getWebDriver().findElements(By.xpath("//input"));
        elements_input.get(elements_input.size()-1).sendKeys("yeastarautotest@163.com");
        sleep(WaitUntils.SHORT_WAIT);
        actions().sendKeys(Keys.ENTER).perform();
        sleep(WaitUntils.RETRY_WAIT);

        auto.extensionPage().getLastElementOffsetAndClick(auto.emailPage().STR_EMAIL_XPATH,2,2);

        SelenideElement element_success = $(By.xpath("//span[contains(text(),'Success')]"));
        return auto.extensionPage().waitElementDisplay(element_success, WaitUntils.TIME_OUT_SECOND);
    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("[前提条件] 1.sip ")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0","testLoginWithEmail","Extension","Regression","PSeries","UserPasswordAndLoginModel"})
    public void testLoginWithEmail() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME, LOGIN_PASSWORD);

        prerequisite();

        step("2:创建分机号1001,启用disable outbound call");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtensionWithEmail("1001", LOGIN_PASSWORD, EMAIL, "extension").
                clickSaveAndApply();

        assertStep("3.邮箱登录");
        auto.homePage().logout();
        auto.loginPage().loginWithExtension(EMAIL, LOGIN_PASSWORD, EXTENSION_PASSWORD);
//        auto.loginPage().login("1001", EXTENSION_PASSWORD);

        assertStep("4:功能菜单中只显示extension，可正常添加、编辑、删除、导入、导出分机");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("[前提条件] 1.sip ")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0","testLoginWithExtensionNumber","Extension","Regression","PSeries","UserPasswordAndLoginModel"})
    public void testLoginWithExtensionNumber() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME, LOGIN_PASSWORD);

        prerequisite();

        step("2:创建分机号1001,启用disable outbound call");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtensionWithEmail("1001", LOGIN_PASSWORD, EMAIL, "extension").
                clickSaveAndApply();

        assertStep("3.账号登录,O");
        auto.homePage().logout();
        auto.loginPage().loginWithExtension(EMAIL, LOGIN_PASSWORD, EXTENSION_PASSWORD);
//        auto.loginPage().login("1001", EXTENSION_PASSWORD);

        assertStep("4:功能菜单中只显示extension，可正常添加、编辑、删除、导入、导出分机");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("[前提条件] 1.sip " +
            "2.创建分机0，Linux Server 禁用 Email，启用 Extension number"+
            "3.邮箱登录失败" +
            "4.账号登录成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0","testLoginWithEmialWithLinkusServerUnallowEmail","Extension","Regression","PSeries","UserPasswordAndLoginModel"})
    public void testLoginWithEmialWithLinkusServerUnallowEmail() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME, LOGIN_PASSWORD);

        prerequisite();

        step("2:创建分机号0,禁用login with Email");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtensionWithEmail("1001", LOGIN_PASSWORD, EMAIL, "extension").
                clickSaveAndApply();

        assertStep("3.账号登录,O");
        auto.homePage().logout();
        auto.loginPage().loginWithExtension(EMAIL, LOGIN_PASSWORD, EXTENSION_PASSWORD);
//        auto.loginPage().login("1001", EXTENSION_PASSWORD);

        assertStep("4:功能菜单中只显示extension，可正常添加、编辑、删除、导入、导出分机");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("[前提条件] 1.sip " +
            "2.创建分机0，Linux Server 禁用 Email，启用 Extension number"+
            "3.邮箱登录成功" +
            "4.账号登录失败")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0","testLoginWithEmialWithLinkusServerUnallowNumber","Extension","Regression","PSeries","UserPasswordAndLoginModel"})
    public void testLoginWithEmialWithLinkusServerUnallowNumber() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME, LOGIN_PASSWORD);

        prerequisite();

        step("2:创建分机号0,禁用login with Email");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtensionWithEmail("1001", LOGIN_PASSWORD, EMAIL, "extension").
                clickSaveAndApply();

        assertStep("3.账号登录,O");
        auto.homePage().logout();
        auto.loginPage().loginWithExtension(EMAIL, LOGIN_PASSWORD, EXTENSION_PASSWORD);
//        auto.loginPage().login("1001", EXTENSION_PASSWORD);

        assertStep("4:功能菜单中只显示extension，可正常添加、编辑、删除、导入、导出分机");
    }
}