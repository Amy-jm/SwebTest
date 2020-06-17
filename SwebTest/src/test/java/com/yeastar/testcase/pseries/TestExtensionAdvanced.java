package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.IExtensionPageElement;

import com.yeastar.page.pseries.TestCaseBase;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * @program: SwebTest
 * @description: 分机模块用例-advanced
 * @author: huangjx@yeastar.com
 * @create: 2020/06/16
 */
public class TestExtensionAdvanced extends TestCaseBase {
    @Epic("P_Series")
    @Feature("Extension")
    @Story("Advanced")
    @Description("DTMF MODE 功能验证：" +
            "1:login PBX" +
            "2:创建分机号1001,DTMF_MODE 修改为RFC4733RFC2833->3:配置生效：dtmf_mode   : rfc4733" +
            "4:修改分机号1001,DTMF_MODE 修改为INBAND ->5:配置生效：dtmf_mode   : inband"+
            "6:修改分机号1001,DTMF_MODE 修改为auto->7:配置生效：dtmf_mode   : auto"+
            "8:修改分机号1001,DTMF_MODE 修改为info->9:配置生效：dtmf_mode   : info")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLinks(value = {@TmsLink(value = "Математика"), @TmsLink(value = ""),@TmsLink(value = ""),@TmsLink(value = "")})
    @Test(groups = "P0,TestExtensionAdvanced,testDTMFMode,Regression,PSeries")
    public void testDTMFMode() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,DTMF_MODE 修改为RFC4733RFC2833");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).
                editFirstData().switchToTab("Advanced").select_DTMF_Mode(IExtensionPageElement.DTMF_MODE.RFC4733RFC2833).saveBtn.click();

        assertStep("3:配置生效：dtmf_mode   : rfc4733");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("dtmf_mode                     : rfc4733"),"[Assert,dtmf mode]");

        step("4:修改分机号1001,DTMF_MODE 修改为INBAND");
        auto.extensionPage().editFirstData().switchToTab("Advanced").select_DTMF_Mode(IExtensionPageElement.DTMF_MODE.INBAND).clickSaveAndApply();

        assertStep("5:配置生效：dtmf_mode   : inband");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("dtmf_mode                     : inband"),"[Assert,dtmf mode]");

        step("6:修改分机号1001,DTMF_MODE 修改为auto");
        auto.extensionPage().editFirstData().switchToTab("Advanced").select_DTMF_Mode(IExtensionPageElement.DTMF_MODE.AUTO).clickSaveAndApply();

        assertStep("7:配置生效：dtmf_mode   : auto");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("dtmf_mode                     : auto"),"[Assert,dtmf mode]");

        step("8:修改分机号1001,DTMF_MODE 修改为info");
        auto.extensionPage().editFirstData().switchToTab("Advanced").select_DTMF_Mode(IExtensionPageElement.DTMF_MODE.INFO).clickSaveAndApply();

        assertStep("9:配置生效：dtmf_mode   : info");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("dtmf_mode                     : info"),"[Assert,dtmf mode]");

        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Advanced")
    @Description("Transport 功能验证：" +
            "1:login PBX" +
            "2:创建分机号1001,Transport 修改为UDP->3:配置生效：transport_name   : udp" +
            "4:修改分机号1001,Transport 修改为TCP ->5:配置生效：transport_name   : tcp"+
            "6:修改分机号1001,Transport 修改为Tls->7:配置生效：transport_name   : tls")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLinks(value = {@TmsLink(value = "Математика"), @TmsLink(value = ""),@TmsLink(value = ""),@TmsLink(value = "")})
    @Test(groups = "P0,TestExtensionAdvanced,testTransport,Regression,PSeries")
    public void testTransport() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,Transport 修改为UDP");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).
                editFirstData().switchToTab("Advanced").select_Transport(IExtensionPageElement.TRANSPORT.UDP).saveBtn.click();

        assertStep("3:配置生效：transport_name   : udp");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("transport_name                : udp"),"[Assert,transport_name]");

        step("4:修改分机号1001,transport_name 修改为tcp");
        auto.extensionPage().editFirstData().switchToTab("Advanced").select_Transport(IExtensionPageElement.TRANSPORT.TCP).clickSaveAndApply();

        assertStep("5:配置生效：transport_name   : tcp");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("transport_name                : tcp"),"[Assert,transport_name]");

        step("6:修改分机号1001,transport_name 修改为tls");
        auto.extensionPage().editFirstData().switchToTab("Advanced").select_Transport(IExtensionPageElement.TRANSPORT.TLS).clickSaveAndApply();

        assertStep("7:配置生效：transport_name   : tls");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("transport_name                : tls"),"[Assert,transport_name]");

        softAssert.assertAll();
    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("Advanced")
    @Description("STRP 功能验证：" +
            "1:login PBX" +
            "2:创建分机号1001,SRTP 修改为禁用->3:配置生效： media_encryption              : no" +
            "4:修改分机号1001,SRTP 修改为启用 ->5:配置生效： media_encryption              : yes")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLinks(value = {@TmsLink(value = ""), @TmsLink(value = ""),@TmsLink(value = ""),@TmsLink(value = "")})
    @Test(groups = "P0,TestExtensionAdvanced,testSRTP,Regression,PSeries")
    public void testSRTP() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,SRTP 修改为禁用");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).
                editFirstData().switchToTab("Advanced").isCheckbox(IExtensionPageElement.ele_extension_advanced_enb_srtp_checkbox,false).saveBtn.click();

        assertStep("3:配置生效： media_encryption  : no;media_encryption_optimistic   : true");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("media_encryption              : no"));
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("media_encryption_optimistic   : true"));


        step("4:修改分机号1001,transport_name 修改为tcp");
        auto.extensionPage().editFirstData().switchToTab("Advanced").isCheckbox(IExtensionPageElement.ele_extension_advanced_enb_srtp_checkbox,true).clickSaveAndApply();

        assertStep("5:配置生效： media_encryption  : yes;media_encryption_optimistic   : true");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("media_encryption              : sdes"));
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("media_encryption_optimistic   : true"));

        softAssert.assertAll();
    }
}
