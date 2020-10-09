package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.CallControl.IInboundRoutePageElement;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.*;
import com.yeastar.untils.APIObject.IVRObject;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.page.pseries.ExtensionTrunk.IExtensionPageElement.*;
import static com.yeastar.swebtest.driver.SwebDriverP.ys_waitingTime;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: test extension feature
 * @author: huangjx@yeastar.com
 * @create: 2020/07/01
 */
@Listeners({ExecutionListener.class, AllureReporterListener.class, TestNGListenerP.class})
@Log4j2
public class TestExtensionFeature extends TestCaseBase {
    APIUtil apiUtil = new APIUtil();

    public void prerequisiteCreateIVR() {
        step("创建IVR_0");
        ArrayList<IVRObject.PressKeyObject> pressKeyObjects_0 = new ArrayList<>();
        pressKeyObjects_0.add(new IVRObject.PressKeyObject(IVRObject.PressKey.press0, "extension", "", "1000", 0));
        apiUtil.deleteAllIVR().createIVR("6200", "6200", pressKeyObjects_0);
        apiUtil.apply();
    }
    /**
     * 前提条件
     * 1.添加0分机和sps中继到 路由AutoTest_Route
     */
    public void prerequisite() {
        //新增呼出路由 添加分机0 ，到路由AutoTest_Route
        ArrayList<String> trunklist = new ArrayList<>();
        trunklist.clear();
        trunklist.add(SPS);


        //创建trunk
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_trunks);
        auto.trunkPage().deleteTrunk(getDriver(), SPS).createSpsTrunk(SPS, DEVICE_ASSIST_2, DEVICE_ASSIST_2).clickSaveAndApply();

        //创建呼入路由
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().deleteAllInboundRoutes()
                .createInboundRoute("InRoute1", trunklist)
//                .editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(), "1001-1001")
                .clickSaveAndApply();

//
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Feature")
    @Description("[验证]Send email notification when the User Password is changed 功能" +
            "1:login PBX" +
            "2:创建分机号1001(带邮箱yeastarautotest@163.com)" +
            "3.启用Send email notification when the User Password is changed 功能 ->正常接收到邮件" +
            "4.禁用Send email notification when the User Password is changed 功能 ->无法接收到邮件" +
            "[备] 用例失败，1.请先确认 邮件服务器是否正常;  2.DNS设置->192.168.1.1")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "testEmailNotification", "Extension", "Regression", "PSeries", "Feature"})
    public void testEmailNotification() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME, LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,启用disable outbound call");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtensionWithEmail("1001", EXTENSION_PASSWORD, "yeastarautotest@163.com").clickSaveAndApply();
        auto.extensionPage().editDataByEditImage("1001").switchToTab("Features").setCheckbox(ele_extension_feature_enb_email_pwd_chg, true).clickSaveAndApply();

        int emailUnreadCount_before = MailUtils.getEmailUnreadMessageCountFrom163();
        log.debug("[邮箱数量]" + emailUnreadCount_before);
        auto.extensionPage().editFirstData().setElementValue(ele_extension_user_user_password, "NewYeastar202").clickSaveAndApply();
        sleep(30000);//等待接收邮件
        int emailUnreadCount_after = MailUtils.getEmailUnreadMessageCountFrom163();
        log.debug("[功能开启，收到邮件，数量+1]" + emailUnreadCount_after);

        softAssert.assertEquals(emailUnreadCount_before + 1, emailUnreadCount_after, "功能开启，邮件正常接收");
        auto.extensionPage().editFirstData().switchToTab("Features").setCheckbox(ele_extension_feature_enb_email_pwd_chg, false).clickSaveAndApply();
        auto.extensionPage().editFirstData().setElementValue(ele_extension_user_user_password, "Yeastar202").clickSaveAndApply();

        sleep(30000);//等待接收邮件
        int emailUnreadCount_last = MailUtils.getEmailUnreadMessageCountFrom163();
        log.debug("[功能关闭，未收到邮件，数量不变]" + emailUnreadCount_last);

        softAssert.assertEquals(emailUnreadCount_last, emailUnreadCount_after, "功能关启，邮件没有接收到");
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Feature")
    @Description("[验证] send email notification on miss call 功能->被叫1001收到 miss call 邮件" +
            "1:login PBX" +
            "2:创建分机号1001(带邮箱yeastarautotest@163.com)" +
            "3.启用Send email notification when the User Password is changed 功能 ->正常接收到邮件" +
            "4.禁用Send email notification when the User Password is changed 功能 ->无法接收到邮件" +
            "[备] 用例失败，1.请先确认 邮件服务器是否正常;  2.DNS设置->192.168.1.1")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("miss call 功能未实现")
    @Test(groups = {"P0", "testEmailNotificationOnMissCall", "Extension", "Regression", "PSeries", "Feature"})
    public void testEmailNotificationOnMissCall() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME, LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,启用disable outbound call");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);

        auto.extensionPage().deleAllExtension().createSipExtension("1002", EXTENSION_PASSWORD).clickSave();
        auto.extensionPage().createSipExtensionWithEmail("1001", EXTENSION_PASSWORD, "yeastarautotest@163.com").
                switchToTab("Features").setCheckbox(ele_extension_feature_enb_email_miss_call, true).clickSaveAndApply();

        int emailUnreadCount_before = MailUtils.getEmailUnreadMessageCountFrom163();
        step("3:【验证邮箱服务器是否能正常】通过修改分机1001密码，验证是否能收到邮件");
        auto.extensionPage().editFirstData().setElementValue(ele_extension_user_user_password, "NewYeastar202").clickSaveAndApply();
        sleep(30000);//等待接收邮件
        int emailUnreadCount_after = MailUtils.getEmailUnreadMessageCountFrom163();

        log.debug("3.[邮箱服务器功能验证][测试前邮箱数量] " + emailUnreadCount_before + "-->>[验证邮箱功能，数量+1] " + emailUnreadCount_after);
        softAssert.assertEquals(emailUnreadCount_before + 1, emailUnreadCount_after, "邮箱服务器正常，邮件正常接收");


        assertStep("3:[PJSIP注册]] 1002 呼叫 1001");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001, DEVICE_IP_LAN);

        pjsip.Pj_Make_Call_No_Answer(1002, "1001", DEVICE_IP_LAN, false);
        sleep(40 * 1000);//等待自动挂断
        pjsip.Pj_Hangup_All();

        assertStep("5[CDR]验证，第二条记录status为 NO ANSWER, Call To为 \"1001<1001>\"");
        //todo cdr 显示全选
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        sleep(WaitUntils.SHORT_WAIT);//等待自动挂断
        auto.cdrPage().refreshBtn.shouldBe(Condition.visible).click();
        softAssert.assertEquals(TableUtils.getTableForHeader(getDriver(), "Status", 1), "NO ANSWER");//CDR 会产生2条数据，第一条为到voicemail数据， 第二条数据为：no answer数据（目前对该条数据进行验证）
        softAssert.assertEquals(TableUtils.getTableForHeader(getDriver(), "Call To", 1), "1001<1001>");
        assertStep("[邮箱验证]收到Miss Call邮件");
        ys_waitingTime(WaitUntils.SHORT_WAIT * 10);//等待接收邮件
        int emailUnreadCount_last = MailUtils.getEmailUnreadMessageCountFrom163();

        log.debug("6 [功能开始->期望结果：邮箱数量+1][测试前邮箱数量] " + emailUnreadCount_after + "-->>[等待30s,再次查看邮箱数量] " + emailUnreadCount_last);
        softAssert.assertEquals(emailUnreadCount_last, emailUnreadCount_after + 1, "期望，收到Miss Call邮件，功能未实现");//todo bug <version 23> 功能未实现，删除“功能未实现”注解

        step("7:禁用disable outbound call");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().editDataByEditImage("1001").switchToTab("Features").setCheckbox(ele_extension_feature_enb_email_miss_call, false).clickSaveAndApply();
        int emailUnreadCount_close_start = MailUtils.getEmailUnreadMessageCountFrom163();
        pjsip.Pj_Make_Call_No_Answer(1002, "1001", DEVICE_IP_LAN, false);
        sleep(40 * 1000);//等待自动挂断
        pjsip.Pj_Hangup_All();

        assertStep("8 [CDR]验证，第二条记录status为 NO ANSWER, Call To为 \"1001<1001>\"");
        //todo cdr 显示全选
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        sleep(WaitUntils.SHORT_WAIT);//等待自动挂断
        auto.cdrPage().refreshBtn.shouldBe(Condition.visible).click();
        softAssert.assertEquals(TableUtils.getTableForHeader(getDriver(), "Status", 1), "NO ANSWER");//CDR 会产生2条数据，第一条为到voicemail数据， 第二条数据为：no answer数据（目前对该条数据进行验证）
        softAssert.assertEquals(TableUtils.getTableForHeader(getDriver(), "Call To", 1), "1001<1001>");

        int emailUnreadCount_close_end = MailUtils.getEmailUnreadMessageCountFrom163();
        log.debug("9 [功能关闭->期望结果：邮箱数量不变][测试前邮箱数量] " + emailUnreadCount_close_start + "-->>[等待30s,再次查看邮箱数量] " + emailUnreadCount_last);
        sleep(30000);//等待接收邮件
        softAssert.assertEquals(emailUnreadCount_close_start, emailUnreadCount_close_end, "期望，不收到Miss Call邮件");

        softAssert.assertAll();
    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("Feature")
    @Description("[验证]call backing 功能" +
            "1:login PBX" +
            "2:创建分机号1001，编辑call blocking" +
            "3.2000 呼叫 1001 ,1001不响铃，IVR6200 响铃" +
            "4.2001 呼叫 1001,1001正常响铃")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "testCallBacking", "Extension", "Regression", "PSeries", "Feature"})
    public void testCallBacking() throws IOException, JSchException {
        prerequisiteCreateIVR();

        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME, LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001，编辑call blocking");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().clickSaveAndApply();
        auto.extensionPage().deleAllExtension().createSipExtension("1002", EXTENSION_PASSWORD).clickSaveAndApply();
        auto.extensionPage().createSipExtension("1001", EXTENSION_PASSWORD).
                switchToTab(TABLE_MENU.FEATURES.getAlias()).addCallHandingRule("2000", "IVR", "", "").clickSaveAndApply();

        step("删除呼入路由 -> 创建呼入路由InRoute1");
//        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
//        ArrayList<String> trunklist = new ArrayList<>();
//        trunklist.add(SPS);
//        auto.inboundRoute().deleteAllInboundRoutes()
//                .createInboundRoute("InRoute1",trunklist)
////                .editInbound("InRoute1","Name")
//                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(),"1001-1001")
//                .clickSaveAndApply();

        prerequisite();
        sleep(WaitUntils.SHORT_WAIT * 3);

        assertStep("3:[PJSIP注册]] ，2000 呼叫 1001 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(2000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000, DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002, DEVICE_IP_LAN);

        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(2000, "991001", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 3);//等待自动挂断
        pjsip.Pj_hangupCall(2000);

        assertStep("5[CDR]IVR6200 响铃");
        //todo cdr 显示全选
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
//        auto.cdrPage().refreshBtn.shouldBe(Condition.visible).click();
//        //CDR 会产生2条数据，第一条为到voicemail数据， 第二条数据为：no answer数据（目前对该条数据进行验证）
//        auto.cdrPage().assertCDRRecord(getDriver(), 0, "2000<2000>", "IVR 6200<6200>", "ANSWERED", "2000<2000> hung up", communication_inbound, SPS, "");

        assertStep("[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason")
                .contains(tuple("2000<2000>", "IVR 6200<6200>", "ANSWERED", "2000<2000> hung up"));


        assertStep("3:[PJSIP注册]] ，2001 呼叫 1001 ");
        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(1002, "1001", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT * 2);
        pjsip.Pj_hangupCall(1002);

        assertStep("5[CDR]1001<1001> 响铃");
        List<CDRObject> resultCDRAfter = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDRAfter).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason")
                .contains(tuple("1002<1002>", "1001<1001>", "ANSWERED", "1002<1002> hung up"));

        softAssertPlus.assertAll();

    }
}
