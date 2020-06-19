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


}
