package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.IExtensionPageElement;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.TableUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.yeastar.page.pseries.IExtensionPageElement.ele_extension_security_max_outb_call_duration_select;
import static com.yeastar.page.pseries.IPreferencesPage.ele_pbx_settings_preferences_max_call_duration_select;
import static com.yeastar.swebtest.driver.SwebDriverP.*;

/**
 * @program: SwebTest
 * @description: Extension security 用例
 * @author: huangjx@yeastar.com
 * @create: 2020/06/17
 */
public class TestExtensionSecurity extends TestCaseBase {
    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("启用disable outbound call：1:login PBX->2:创建分机号1001,启用disable outbound call->3.验证通话状态")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0","testEnableDisableOutCall","Extension","Regression","PSeries","Security"})
    public void testEnableDisableOutCall() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,启用disable outbound call");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).
                editFirstData().switchToTab("Security").
                isCheckbox(IExtensionPageElement.ele_extension_security_disable_outb_call_checkbox,true).
                clickSaveAndApply();

        assertStep("3.验证通话状态,1.预期1001会Ring ,2.预期2000不会响铃");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_1);

        pjsip.Pj_Make_Call_No_Answer(1001,"2000",DEVICE_IP_LAN,false);
        softAssert.assertEquals(getExtensionStatus(1001,HUNGUP,20),RING,"预期1001为Ring状态");
        softAssert.assertEquals(getExtensionStatus(2000,IDLE,10),IDLE,"预期2000不会响铃");
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("启用disable outbound call：1:login PBX->2:创建分机号1001,启用disable outbound call->3.验证通话状态")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0","testDisableOutCall","Extension","Regression","PSeries","Security"})
    public void testDisableOutCall() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,启用disable outbound call");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).
                editFirstData().switchToTab("Security").
                isCheckbox(IExtensionPageElement.ele_extension_security_disable_outb_call_checkbox,false).saveBtn.click();

        assertStep("3.验证通话状态,1.预期1001会Ring ,2.预期2000不会响铃");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_1);

        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(1001,"2000",DEVICE_IP_LAN,false);
        softAssert.assertEquals(getExtensionStatus(1001,TALKING,10),TALKING,"预期1001为TALKING状态");
        softAssert.assertEquals(getExtensionStatus(2000,TALKING,10),TALKING,"预期2000为TALKING状态");
        pjsip.Pj_Hangup_All();

        assertStep("[CDR]4.验证通话状态");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        //todo delete sleep
        ys_waitingTime(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(TableUtils.getCDRForHeader(getDriver(),"Status",0),"ANSWERED");
        softAssert.assertEquals(TableUtils.getCDRForHeader(getDriver(),"Call From",0),"1001<1001>");
        softAssert.assertEquals(TableUtils.getCDRForHeader(getDriver(),"Call To",0),"2000");
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("启用disable outbound calls outside business hours功能：1:login PBX->2:创建分机号1001,启用disable outbound calls outside business hours->3.验证通话状态")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0","testEnableDisableOutCallBusinessHours","Extension","Regression","PSeries","Security"})
    public void testEnableDisableOutCallBusinessHours() throws IOException, JSchException {
        //新增时间条件
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,启用disable outbound calls outside business hours");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).
                editFirstData().switchToTab("Security").
                isCheckbox(IExtensionPageElement.ele_extension_security_disable_office_time_outb_call_checkbox,true).
                clickSaveAndApply();

        assertStep("3.验证通话状态,1.预期1001会Ring ,2.预期2000不会响铃");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_1);

        //勾选，下班时间不能呼出
        pjsip.Pj_Make_Call_No_Answer(1001,"2000",DEVICE_IP_LAN,false);
        softAssert.assertEquals(getExtensionStatus(1001,HUNGUP,10),RING,"预期1001为HUNGUP状态");
        softAssert.assertEquals(getExtensionStatus(2000,IDLE,10),IDLE,"预期2000不会响铃");
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("禁用disable outbound calls outside business hours：1:login PBX->2:创建分机号1001,禁用disable outbound calls outside business hours->3.验证通话状态")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0","testDisableOutCallBusinessHours","Extension","Regression","PSeries","Security"})
    public void testDisableOutCallBusinessHours() throws IOException, JSchException {
        //新增时间条件
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,禁用disable outbound calls outside business hours");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).
                editFirstData().switchToTab("Security").
                isCheckbox(IExtensionPageElement.ele_extension_security_disable_office_time_outb_call_checkbox,false).saveBtn.click();

        assertStep("3.验证通话状态,1.预期1001会Ring ,2.预期2000不会响铃");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_1);

        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(1001,"2000",DEVICE_IP_LAN,false);
        softAssert.assertEquals(getExtensionStatus(1001,TALKING,10),TALKING,"预期1001为TALKING状态");
        softAssert.assertEquals(getExtensionStatus(2000,TALKING,10),TALKING,"预期2000为TALKING状态");
        pjsip.Pj_Hangup_All();

        assertStep("[CDR]4.验证通话状态");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        //todo delete sleep
        ys_waitingTime(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(TableUtils.getCDRForHeader(getDriver(),"Status",0),"ANSWERED");
        softAssert.assertEquals(TableUtils.getCDRForHeader(getDriver(),"Call From",0),"1001<1001>");
        softAssert.assertEquals(TableUtils.getCDRForHeader(getDriver(),"Call To",0),"2000");
        softAssert.assertAll();
    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("启动Allow Register Remotely：1:login PBX->2:创建分机号1001,启用Allow Register Remotely->3.验证通话状态")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0","testEnableAllowRegisterRemotely","Extension","Regression","PSeries","Security"})
    public void testEnableAllowRegisterRemotely() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,启用Allow Register Remotely");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).
                editFirstData().switchToTab("Security").
                isCheckbox(IExtensionPageElement.ele_extension_security_allow_reg_remotely_checkbox,false).saveBtn.click();

        assertStep("3:[PJSIP]期望结果：remoteregister                     : no ");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains(" remoteregister                : no"),"[remoteregister no]");

        step("4:修改分机号1001,remoteregister->启用");
        auto.extensionPage().editFirstData().switchToTab("Security").isCheckbox(IExtensionPageElement.ele_extension_security_allow_reg_remotely_checkbox,true).clickSaveAndApply();

        assertStep("5:[PJSIP]期望结果：remoteregister                     : yes");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains(" remoteregister                : yes"),"[remoteregister yes]");

        softAssert.assertAll();
    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("验证 Enable User Agent Registration Authorization：1:login PBX->2:创建分机号1001,启用Allow Register Remotely->3.验证通话状态")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0","testEnableUserAgentRegistrationAuthorization","Extension","Regression","PSeries","Security"})
    public void testEnableUserAgentRegistrationAuthorization() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,启用Allow Register Remotely");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).
                editFirstData().switchToTab("Security").
                isCheckbox(IExtensionPageElement.ele_extension_security_enb_user_agent_ident_checkbox,false).saveBtn.click();

        assertStep("3:[PJSIP]期望结果：enableuseragent               : no");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("enableuseragent               : no"),"[enableuseragent no]");

        step("4:修改分机号1001,remoteregister->启用");
        auto.extensionPage().editFirstData().switchToTab("Security").isCheckbox(IExtensionPageElement.ele_extension_security_enb_user_agent_ident_checkbox,true)
                .addUserAgent(0,"Yealink.")
                .addUserAgent(1,"Test.")
                .addUserAgent(2,"Yeastar.")
                .addUserAgent(3,"Zoiper.")
                .addUserAgent(4,"Y.")
                .clickSaveAndApply();

        assertStep("5:[PJSIP]期望结果：enableuseragent               : yes");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("enableuseragent               : yes"),"[enableuseragent yes]");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("limituseragent                : Yealink.,Test.,Yeastar.,Zoiper.,Y."),"[limituseragent yes]");
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("验证 SIP Registration IP Restriction：1:login PBX->2:创建分机号1001,启用Allow Register Remotely->3.验证通话状态")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0","testEnableUserAgentRegistrationAuthorization","Extension","Regression","PSeries","Security","Security"})
    public void testSIPRegistrationIPRestriction() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,启用Allow Register Remotely");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).
                editFirstData().switchToTab("Security").
                isCheckbox(IExtensionPageElement.ele_extension_security_enb_ip_rstr_checkbox,false).saveBtn.click();

        assertStep("3:[PJSIP]期望结果：enableiprestrict               : no");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("enableiprestrict              : no"),"[enableiprestrict no]");

        step("4:修改分机号1001,remoteregister->启用");
        auto.extensionPage().editFirstData().switchToTab("Security").isCheckbox(IExtensionPageElement.ele_extension_security_enb_ip_rstr_checkbox,true)
                .addIPRestriction(0,"192.168.3.0","255.255.255.0")
                .addIPRestriction(1,"192.168.0.0","255.255.0.0")
                .addIPRestriction(2,"110.80.36.162","255.255.255.255")
                .addIPRestriction(3,"192.168.202.0","255.255.254.0")
                .clickSaveAndApply();

        assertStep("5:[PJSIP]期望结果：enableiprestrict               : yes");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("enableiprestrict              : yes"),"[enableiprestrict yes]");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("permitip1                     : 192.168.3.0/255.255.255.0"),"[permitip1 ]");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("permitip2                     : 192.168.0.0/255.255.0.0"),"[permitip2 ]");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("permitip3                     : 110.80.36.162/255.255.255.255"),"[permitip3 ]");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("permitip4                     : 192.168.202.0/255.255.254.0"),"[permitip4 ]");
        softAssert.assertAll();
    }



    @Epic("P_Series")
    @Feature("Extension")
    @Story("BasicDisplayAndRegistration")
    @Description("a.分机 0，Max Call Duration -> 设置60s," +
                 "b.全局  preference，Max Call Duration -> 设置30s" +
                 "【验证内部分机互打】" +
                 "1:login PBX->2:创建分机号0->3:验证保存成功->4:删除分机->5:验证删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0","TestExtensionBasicDisplayAndRegistration","testCalled0To9999999","Regression","PSeries","Security"})
    public void testMaxCallDurationForInternalExtension() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2.创建分机：0，分机：9999999");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().
                deleAllExtension().
                createSipExtension("0","Yeastar Test0","朗视信息科技","(0591)-Ys.0","0","Yeastar202").
                createSipExtension("9999999","Yeastar Test9999999","朗视信息科技","(0591)-Ys.9999999","9999999","Yeastar202");

        assertStep("3:设置分机0，Max Call Duration 设置60s");
        auto.extensionPage().editFirstData().switchToTab("Security").setElementValue(ele_extension_security_max_outb_call_duration_select,"60")
                .clickSaveAndApply();

        assertStep("4:全局设置 preference，Max Call Duration 设置30s");
        auto.homePage().intoPage(HomePage.Menu_Level_1.pbx_settings, HomePage.Menu_Level_2.pbx_settings_tree_preferences);
        auto.preferencesPage().setElementValue(ele_pbx_settings_preferences_max_call_duration_select,"30")
                .clickSaveAndApply();


        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(9999999,DEVICE_IP_LAN);

        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(0,"9999999",DEVICE_IP_LAN,false);

        ys_waitingTime(70000);
        pjsip.Pj_Hangup_All();

        assertStep("[CDR]5.第一条记录：Reason = Exceeded the max call duration(s) ；Call Duration(s)=00:00:30");
        //todo cdr 显示全选
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        //todo delete sleep
        ys_waitingTime(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(TableUtils.getCDRForHeader(getDriver(),"Reason",0),"Exceeded the max call duration(s)");
        //todo bug 实际结果为 "00:01:00"  20200628
        softAssert.assertEquals(TableUtils.getCDRForHeader(getDriver(),"Call Duration(s)",0),"00:00:30");

        softAssert.assertAll();
    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("BasicDisplayAndRegistration")
    @Description("a.分机 0，Max Call Duration -> 设置60s," +
            "b.全局  preference，Max Call Duration -> 设置30s" +
            "【验证SPS Trunk 呼出】，0 呼叫 2000" +
            "1:login PBX->2:创建分机号0->3:验证保存成功->4:删除分机->5:验证删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0","TestExtensionBasicDisplayAndRegistration","testCalled0To9999999","Regression","PSeries","Security"})
    public void testMaxCallDurationForSPSTrunk() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2.创建分机：0");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().
                deleAllExtension().
                createSipExtension("0","Yeastar Test0","朗视信息科技","(0591)-Ys.0","0","Yeastar202");

        assertStep("3:设置分机0，Max Call Duration 设置60s");
        auto.extensionPage().editFirstData().switchToTab("Security").setElementValue(ele_extension_security_max_outb_call_duration_select,"60")
                .clickSaveAndApply();

        assertStep("4:全局设置 preference，Max Call Duration 设置30s");
        auto.homePage().intoPage(HomePage.Menu_Level_1.pbx_settings, HomePage.Menu_Level_2.pbx_settings_tree_preferences);
        auto.preferencesPage().setElementValue(ele_pbx_settings_preferences_max_call_duration_select,"30")
                .clickSaveAndApply();


        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_1);

        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(0,"2000",DEVICE_IP_LAN,false);
        ys_waitingTime(70000);
        pjsip.Pj_Hangup_All();

        assertStep("[CDR]5.第一条记录：Reason = Exceeded the max call duration(s) ；Call Duration(s)=00:00:30");
        //todo cdr 显示全选
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        //todo delete sleep
        ys_waitingTime(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(TableUtils.getCDRForHeader(getDriver(),"Reason",0),"Exceeded the max call duration(s)");
        softAssert.assertEquals(TableUtils.getCDRForHeader(getDriver(),"Call Duration(s)",0),"00:01:00");

        softAssert.assertAll();
    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("BasicDisplayAndRegistration")
    @Description("a.分机 0，Max Call Duration -> 设置60s," +
            "b.全局  preference，Max Call Duration -> 设置30s" +
            "【验证SPS Trunk 呼入】，2000 呼叫 0" +
            "1:login PBX->2:创建分机号0->3:验证保存成功->4:删除分机->5:验证删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0","TestExtensionSecurity","testMaxCallDurationForSPSTrunkTo0","Regression","PSeries","Security"})
    public void testMaxCallDurationForSPSTrunkTo0() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2.创建分机：0");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().
                deleAllExtension().
                createSipExtension("0","Yeastar Test0","朗视信息科技","(0591)-Ys.0","0","Yeastar202Yeastar202");

        assertStep("3:设置分机0，Max Call Duration 设置60s");
        auto.extensionPage().editFirstData().switchToTab("Security").setElementValue(ele_extension_security_max_outb_call_duration_select,"60")
                .clickSaveAndApply();

        assertStep("4:全局设置 preference，Max Call Duration 设置30s");
        auto.homePage().intoPage(HomePage.Menu_Level_1.pbx_settings, HomePage.Menu_Level_2.pbx_settings_tree_preferences);
        auto.preferencesPage().setElementValue(ele_pbx_settings_preferences_max_call_duration_select,"30")
                .clickSaveAndApply();


        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_1);

        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(2000,"0",DEVICE_IP_LAN,false);
        ys_waitingTime(70000);
        pjsip.Pj_Hangup_All();

        assertStep("[CDR]5.第一条记录：Reason = Exceeded the max call duration(s) ；Call Duration(s)=00:00:30");
        //todo cdr 显示全选
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        //todo delete sleep
        ys_waitingTime(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(TableUtils.getCDRForHeader(getDriver(),"Reason",0),"Exceeded the max call duration(s)");
        //todo bug 00:01:00
        softAssert.assertEquals(TableUtils.getCDRForHeader(getDriver(),"Call Duration(s)",0),"00:00:30");
        softAssert.assertAll();
    }


//    @Test
    public void testSelectTime() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,启用Allow Register Remotely");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_office_time_and_holidays);
        auto.extensionPage().selectTime("12:30");

    }

}
