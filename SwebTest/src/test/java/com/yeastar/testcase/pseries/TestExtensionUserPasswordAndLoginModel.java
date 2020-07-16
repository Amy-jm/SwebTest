package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.ExtensionTrunk.IExtensionPageElement;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.swebtest.pobject.Settings.System.Email.Email;
import com.yeastar.untils.MailUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

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
    @Description("[前提条件] 新增role 组extension" +
            "1.登录系统，创建分机 0，邮箱为 yeastarautotest@163.com" +
            "2.通过邮箱登录，-修改密码" +
            "3.查看Web Client admin下 只显示分机菜单" +
            "4.能正常新增，编辑，删除分机")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("bug 分机多显示 CDR菜单")
    @Test(groups = {"P0","testLoginWithEmail","Extension","Regression","PSeries","UserPasswordAndLoginModel"})
    public void testLoginWithEmail() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME, LOGIN_PASSWORD);

        prerequisite();

        step("2:创建分机号1001,启用disable outbound call");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtensionWithEmail("0", LOGIN_PASSWORD, EMAIL, "extension").choiceLinkusServer().
                setCheckbox(IExtensionPageElement.ele_extension_server_enable_extension_login_checkbox,true).
                setCheckbox(IExtensionPageElement.ele_extension_server_enable_email_login_checkbox,true).
                clickSaveAndApply();

        assertStep("3.邮箱登录");
        auto.homePage().logout();
        auto.loginPage().loginWithExtension(EMAIL, LOGIN_PASSWORD, EXTENSION_PASSWORD);
        auto.loginPage().login(EMAIL, EXTENSION_PASSWORD);
        auto.extensionPage().switchWebClient();
        softAssert.assertEquals(auto.extensionPage().getMenuNumWithDashBoardBrother().menuNumber,"1","[期望结果为，只显示一个Extension菜单] 实际显示-->"+auto.extensionPage().getMenuNumWithDashBoardBrother().menuStr);
        assertStep("4:功能菜单中只显示extension，可正常添加、编辑、删除");

        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().createSipExtension("1003", LOGIN_PASSWORD).editDataByEditImage("1003").setElementValue(auto.extensionPage().ele_extension_user_last_name,"test").clickSaveAndApply();
        auto.extensionPage().deleDataByDeleImage("1003").clickSaveAndApply();

        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("[前提条件] 新增role 组extension" +
            "1.登录系统，创建分机 0，邮箱为 yeastarautotest@163.com" +
            "2.通过账号登录，-修改密码" +
            "3.通过邮箱登录" +
            "4.收到邮件")
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
        auto.extensionPage().deleAllExtension().createSipExtensionWithEmail("0", LOGIN_PASSWORD, EMAIL, "extension").choiceLinkusServer().
                setCheckbox(IExtensionPageElement.ele_extension_server_enable_extension_login_checkbox,true).
                setCheckbox(IExtensionPageElement.ele_extension_server_enable_email_login_checkbox,true).
                clickSaveAndApply();

        //获取邮箱数量
        int emailUnreadCount_before = MailUtils.getEmailUnreadMessageCountFrom163();
        log.debug("[邮箱数量]"+emailUnreadCount_before);

        assertStep("3.账号登录,修改密码");
        auto.homePage().logout();
        auto.loginPage().loginWithExtension("0", LOGIN_PASSWORD, EXTENSION_PASSWORD);
        assertStep("4.通过邮箱登录");
        auto.loginPage().login(EMAIL, EXTENSION_PASSWORD);

        //再次获取邮箱数量
        sleep(WaitUntils.SHORT_WAIT*10);
        int emailUnreadCount_after = MailUtils.getEmailUnreadMessageCountFrom163();
        log.debug("[修改密码，收到邮件，数量+1]"+emailUnreadCount_after);
        assertStep("4.邮箱数量");
        softAssert.assertEquals(emailUnreadCount_before+1,emailUnreadCount_after);
        softAssert.assertAll();
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
        auto.extensionPage().deleAllExtension().createSipExtensionWithEmail("0", LOGIN_PASSWORD, EMAIL, "extension").choiceLinkusServer().
                setCheckbox(IExtensionPageElement.ele_extension_server_enable_extension_login_checkbox,true).
                setCheckbox(IExtensionPageElement.ele_extension_server_enable_email_login_checkbox,false).
                clickSaveAndApply();
        auto.homePage().logout();

        assertStep("3.账号登录,正常登录");
        auto.loginPage().loginWithExtension("0", LOGIN_PASSWORD, EXTENSION_PASSWORD);

        assertStep("4.邮箱登录,登录失败");
        Assert.assertFalse(auto.loginPage().login(EMAIL, EXTENSION_PASSWORD).isLoginSuccess);

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
        auto.extensionPage().deleAllExtension().createSipExtensionWithEmail("0", LOGIN_PASSWORD, EMAIL, "extension").choiceLinkusServer().
                setCheckbox(IExtensionPageElement.ele_extension_server_enable_extension_login_checkbox,false).
                setCheckbox(IExtensionPageElement.ele_extension_server_enable_email_login_checkbox,true).
                clickSaveAndApply();
        auto.homePage().logout();

        assertStep("3.账号登录,正常登录");
        auto.loginPage().loginWithExtension(EMAIL, LOGIN_PASSWORD, EXTENSION_PASSWORD);

        assertStep("4.邮箱登录,登录失败");
        Assert.assertFalse(auto.loginPage().login("0", EXTENSION_PASSWORD).isLoginSuccess);
    }
}