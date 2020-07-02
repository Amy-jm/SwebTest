package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.IExtensionPageElement;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.MailUtils;
import com.yeastar.untils.TableUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.page.pseries.IExtensionPageElement.*;
import static com.yeastar.swebtest.driver.SwebDriverP.getExtensionStatus;
import static com.yeastar.swebtest.driver.SwebDriverP.ys_waitingTime;

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
        sleep(30000);//等待接收邮件
        int emailUnreadCount_after = MailUtils.getEmailUnreadMessageCountFrom163();
        log.debug("[功能开启，收到邮件，数量+1]"+emailUnreadCount_after);

        softAssert.assertEquals(emailUnreadCount_before+1,emailUnreadCount_after,"功能开启，邮件正常接收");
        auto.extensionPage().editFirstData().switchToTab("Features").setCheckbox(ele_extension_feature_enb_email_pwd_chg,false).clickSaveAndApply();
        auto.extensionPage().editFirstData().setElementValue(ele_extension_user_user_password,"Yeastar202").clickSaveAndApply();

        sleep(30000);//等待接收邮件
        int emailUnreadCount_last = MailUtils.getEmailUnreadMessageCountFrom163();
        log.debug("[功能关闭，未收到邮件，数量不变]"+emailUnreadCount_last);

        softAssert.assertEquals(emailUnreadCount_last,emailUnreadCount_after,"功能关启，邮件没有接收到");
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Feature")
    @Description("[验证] send email notification on miss call 功能->被叫1001收到 miss call 邮件" +
            "1:login PBX" +
            "2:创建分机号1001(带邮箱yeastarautotest@163.com)"+
            "3.启用Send email notification when the User Password is changed 功能 ->正常接收到邮件" +
            "4.禁用Send email notification when the User Password is changed 功能 ->无法接收到邮件"+
            "[备] 用例失败，1.请先确认 邮件服务器是否正常;  2.DNS设置->192.168.1.1")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("miss call 功能未实现")
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
        sleep(30000);//等待接收邮件
        int emailUnreadCount_after = MailUtils.getEmailUnreadMessageCountFrom163();
        log.debug("[邮箱数量] "+emailUnreadCount_before+"-->>[验证邮箱功能，数量+1] "+emailUnreadCount_after);

        softAssert.assertEquals(emailUnreadCount_before+1,emailUnreadCount_after,"功能开启，邮件正常接收");
        auto.extensionPage().editFirstData().switchToTab("Features").setCheckbox(ele_extension_feature_enb_email_miss_call,true).clickSaveAndApply();


        assertStep("3:[PJSIP注册]] 1002 呼叫 1001");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);

        pjsip.Pj_Make_Call_No_Answer(1002,"1001",DEVICE_IP_LAN,false);
        long startTime=System.currentTimeMillis();
        sleep(40*1000);//等待自动挂断
        pjsip.Pj_Hangup_All();
        log.debug("[持续等待时间]{}",(System.currentTimeMillis()-startTime)/1000,"秒");
        assertStep("[CDR] ");
        //todo cdr 显示全选
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        sleep(WaitUntils.SHORT_WAIT);//等待自动挂断
        auto.cdrPage().refreshBtn.shouldBe(Condition.visible).click();
        softAssert.assertEquals(TableUtils.getCDRForHeader(getDriver(),"Status",1),"NO ANSWER");//CDR 会产生2条数据，第一条为到voicemail数据， 第二条数据为：no answer数据（目前对该条数据进行验证）
        softAssert.assertEquals(TableUtils.getCDRForHeader(getDriver(),"Call To",1),"1001<1001>");
//        ys_waitingTime(WaitUntils.SHORT_WAIT*10);//等待接收邮件
//        int emailUnreadCount_last = MailUtils.getEmailUnreadMessageCountFrom163();
//        log.debug("[邮箱数量] "+emailUnreadCount_before+"-->>[验证邮箱功能，数量+1] "+emailUnreadCount_after+"-->>[收到Miss Call邮件，数量+1] "+emailUnreadCount_last);
//
//        softAssert.assertEquals(emailUnreadCount_last,emailUnreadCount_after+1,"收到Miss Call邮件");//todo bug <version 23> 功能未实现
        softAssert.assertAll();
    }
}
