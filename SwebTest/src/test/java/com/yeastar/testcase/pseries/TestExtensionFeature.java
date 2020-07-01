package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.IExtensionPageElement;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.MailUtils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.page.pseries.IExtensionPageElement.*;
import static com.yeastar.swebtest.driver.SwebDriverP.getExtensionStatus;

/**
 * @program: SwebTest
 * @description: test extension feature
 * @author: huangjx@yeastar.com
 * @create: 2020/07/01
 */
@Log4j2
public class TestExtensionFeature extends TestCaseBase {

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Feature")
    @Description("[验证]Send email notification when the User Password is changed 功能" +
            "1:login PBX" +
            "2:创建分机号1001(带邮箱yeastarautotest@163.com)"+
            "3.启用Send email notification when the User Password is changed 功能 ->正常接收到邮件" +
            "4.禁用Send email notification when the User Password is changed 功能 ->无法接收到邮件"+
            "[备] 用例失败，1.请先确认 邮件服务器是否正常;  2.DNS设置->192.168.1.1")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0","testEmailNotification","Extension","Regression","PSeries","Feature"})
    public void testEmailNotification() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,启用disable outbound call");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtensionWithEmail("1001",EXTENSION_PASSWORD,"yeastarautotest@163.com").clickSaveAndApply();

        int emailUnreadCount_before = MailUtils.getEmailUnreadMessageCountFrom163();
        log.debug("[邮箱数量]"+emailUnreadCount_before);
        auto.extensionPage().editFirstData().setElementValue(ele_extension_user_user_password,"NewYeastar202").clickSaveAndApply();
        sleep(30000);
        int emailUnreadCount_after = MailUtils.getEmailUnreadMessageCountFrom163();
        log.debug("[功能开启，收到邮件，数量+1]"+emailUnreadCount_after);

        softAssert.assertEquals(emailUnreadCount_before+1,emailUnreadCount_after,"功能开启，邮件正常接收");
        auto.extensionPage().editFirstData().switchToTab("Features").setCheckbox(ele_extension_feature_enb_email_pwd_chg,false).clickSaveAndApply();
        auto.extensionPage().editFirstData().setElementValue(ele_extension_user_user_password,"Yeastar202").clickSaveAndApply();

        sleep(30000);
        int emailUnreadCount_last = MailUtils.getEmailUnreadMessageCountFrom163();
        log.debug("[功能关闭，未收到邮件，数量不变]"+emailUnreadCount_last);

        softAssert.assertEquals(emailUnreadCount_last,emailUnreadCount_after,"功能关启，邮件没有接收到");
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Feature")
    @Description("[验证] send email notification on miss call 功能" +
            "1:login PBX" +
            "2:创建分机号1001(带邮箱yeastarautotest@163.com)"+
            "3.启用Send email notification when the User Password is changed 功能 ->正常接收到邮件" +
            "4.禁用Send email notification when the User Password is changed 功能 ->无法接收到邮件"+
            "[备] 用例失败，1.请先确认 邮件服务器是否正常;  2.DNS设置->192.168.1.1")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0","testEmailNotification","Extension","Regression","PSeries","Feature"})
    public void testEmailNotificationOnMissCall() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,启用disable outbound call");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);

        auto.extensionPage().deleAllExtension().createSipExtension("1002",EXTENSION_PASSWORD).createSipExtensionWithEmail("1001",EXTENSION_PASSWORD,"yeastarautotest@163.com").
               switchToTab("Features").setCheckbox(ele_extension_feature_enb_email_miss_call,true).clickSaveAndApply();

        int emailUnreadCount_before = MailUtils.getEmailUnreadMessageCountFrom163();
        log.debug("[邮箱数量]"+emailUnreadCount_before);
        auto.extensionPage().editFirstData().setElementValue(ele_extension_user_user_password,"NewYeastar202").clickSaveAndApply();
        sleep(30000);
        int emailUnreadCount_after = MailUtils.getEmailUnreadMessageCountFrom163();
        log.debug("[功能开启，收到邮件，数量+1]"+emailUnreadCount_after);

        softAssert.assertEquals(emailUnreadCount_before+1,emailUnreadCount_after,"功能开启，邮件正常接收");
        auto.extensionPage().editFirstData().switchToTab("Features").setCheckbox(ele_extension_feature_enb_email_pwd_chg,false).clickSaveAndApply();
        auto.extensionPage().editFirstData().setElementValue(ele_extension_user_user_password,"Yeastar202").clickSaveAndApply();

        sleep(30000);
        int emailUnreadCount_last = MailUtils.getEmailUnreadMessageCountFrom163();
        log.debug("[功能关闭，未收到邮件，数量不变]"+emailUnreadCount_last);

        softAssert.assertEquals(emailUnreadCount_last,emailUnreadCount_after,"功能关启，邮件没有接收到");
        softAssert.assertAll();
    }
}
