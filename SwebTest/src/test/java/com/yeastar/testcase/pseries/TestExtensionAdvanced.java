package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.ExtensionTrunk.IExtensionPageElement;

import com.yeastar.page.pseries.TestCaseBase;
import io.qameta.allure.*;
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
            "2:创建分机号1001,DTMF_MODE->RFC4733RFC2833->3:[PJSIP]期望结果:dtmf_mode   : rfc4733" +
            "4:修改分机号1001,DTMF_MODE->INBAND->        5:[PJSIP]期望结果:dtmf_mode   : inband"+
            "6:修改分机号1001,DTMF_MODE->auto->          7:[PJSIP]期望结果:dtmf_mode   : auto"+
            "8:修改分机号1001,DTMF_MODE->info->          9:[PJSIP]期望结果:dtmf_mode   : info")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLinks(value = {@TmsLink(value = "ID1001612"), @TmsLink(value = "ID1001613"),@TmsLink(value = "ID1001614"),@TmsLink(value = "ID1001615")})
    @Test(groups = "P0,TestExtensionAdvanced,testDTMFMode,Regression,PSeries")
    public void testDTMFMode() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,DTMF_MODE->RFC4733RFC2833");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).
                editFirstData().switchToTab("Advanced").select_DTMF_Mode(IExtensionPageElement.DTMF_MODE.RFC4733RFC2833).saveBtn.click();

        assertStep("3:[PJSIP]期望结果:dtmf_mode   : rfc4733");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("dtmf_mode                     : rfc4733"),"[Assert,dtmf mode]");

        step("4:修改分机号1001,DTMF_MODE->INBAND");
        auto.extensionPage().editFirstData().switchToTab("Advanced").select_DTMF_Mode(IExtensionPageElement.DTMF_MODE.INBAND).clickSaveAndApply();

        assertStep("5:[PJSIP]期望结果：dtmf_mode   : inband");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("dtmf_mode                     : inband"),"[Assert,dtmf mode]");

        step("6:修改分机号1001,DTMF_MODE->auto");
        auto.extensionPage().editFirstData().switchToTab("Advanced").select_DTMF_Mode(IExtensionPageElement.DTMF_MODE.AUTO).clickSaveAndApply();

        assertStep("7:[PJSIP]期望结果：dtmf_mode   : auto");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("dtmf_mode                     : auto"),"[Assert,dtmf mode]");

        step("8:修改分机号1001,DTMF_MODE->info");
        auto.extensionPage().editFirstData().switchToTab("Advanced").select_DTMF_Mode(IExtensionPageElement.DTMF_MODE.INFO).clickSaveAndApply();

        assertStep("9:[PJSIP]期望结果：dtmf_mode   : info");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("dtmf_mode                     : info"),"[Assert,dtmf mode]");

        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Advanced")
    @Description("Transport 功能验证：" +
            "1:login PBX" +
            "2:创建分机号1001,Transport -> UDP->3:[PJSIP]期望结果：transport_name   : udp" +
            "4:修改分机号1001,Transport -> TCP->5:[PJSIP]期望结果：transport_name   : tcp"+
            "6:修改分机号1001,Transport -> Tls->7:[PJSIP]期望结果：transport_name   : tls")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLinks(value = {@TmsLink(value = "ID1001618"), @TmsLink(value = "ID1001617"),@TmsLink(value = "ID1001616")})
    @Test(groups = "P0,TestExtensionAdvanced,testTransport,Regression,PSeries")
    public void testTransport() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,Transport->UDP");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).
                editFirstData().switchToTab("Advanced").select_Transport(IExtensionPageElement.TRANSPORT.UDP).saveBtn.click();

        assertStep("3:[PJSIP]期望结果：transport_name   : udp");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("transport_name                : udp"),"[Assert,transport_name]");

        step("4:修改分机号1001,transport_name ->tcp");
        auto.extensionPage().editFirstData().switchToTab("Advanced").select_Transport(IExtensionPageElement.TRANSPORT.TCP).clickSaveAndApply();

        assertStep("5:[PJSIP]期望结果：transport_name   : tcp");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("transport_name                : tcp"),"[Assert,transport_name]");

        step("6:修改分机号1001,transport_name->tls");
        auto.extensionPage().editFirstData().switchToTab("Advanced").select_Transport(IExtensionPageElement.TRANSPORT.TLS).clickSaveAndApply();

        assertStep("7:[PJSIP]期望结果：transport_name   : tls");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("transport_name                : tls"),"[Assert,transport_name]");

        softAssert.assertAll();
    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("Advanced")
    @Description("STRP 功能验证：" +
            "1:login PBX" +
            "2:创建分机号1001,SRTP->禁用->3:配置生效： media_encryption : no    ;media_encryption_optimistic   : true" +
            "4:修改分机号1001,SRTP->启用 ->5:配置生效： media_encryption : sdes ;media_encryption_optimistic   : true")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLinks(value = {@TmsLink(value = "ID1001620"), @TmsLink(value = "ID1001619")})
    @Test(groups = "P0,TestExtensionAdvanced,testSRTP,Regression,PSeries")
    public void testSRTP() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,SRTP->禁用");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).
                editFirstData().switchToTab("Advanced").isCheckbox(IExtensionPageElement.ele_extension_advanced_enb_srtp_checkbox,false).saveBtn.click();

        assertStep("3:[PJSIP]期望结果： media_encryption  : no;media_encryption_optimistic   : true");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("media_encryption              : no"));
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("media_encryption_optimistic   : true"));

        step("4:修改分机号1001,SRTP->启用");
        auto.extensionPage().editFirstData().switchToTab("Advanced").isCheckbox(IExtensionPageElement.ele_extension_advanced_enb_srtp_checkbox,true).clickSaveAndApply();

        assertStep("5:[PJSIP]期望结果： media_encryption  : sdes;media_encryption_optimistic   : true");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("media_encryption              : sdes"));
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("media_encryption_optimistic   : true"));

        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Advanced")
    @Description("NAT 功能验证：" +
            "1:login PBX" +
            "2:创建分机号1001,NAT->禁用->3:配置生效： direct_media                  : false    ;media_encryption_optimistic   : true" +
            "4:修改分机号1001,NAT->启用 ->5:配置生效： ")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLinks(value = {@TmsLink(value = "ID1001624"), @TmsLink(value = "ID1001623")})
    @Test(groups = "P0,TestExtensionAdvanced,testNAT,Regression,PSeries")
    public void testNAT() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,NAT->禁用");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).
                editFirstData().switchToTab("Advanced").
                isCheckbox(IExtensionPageElement.ele_extension_advanced_enb_nat_checkbox,false).
                clickSaveAndApply();

        assertStep("3:[PJSIP]期望结果：  direct_media                  : false");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("direct_media                  : false"),"[direct_media false]");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("rtp_symmetric                 : false"),"[rtp_symmetric false]");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("force_rport                   : false"),"[force_rport false]");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("rewrite_contact               : false"),"[rewrite_contact false]");

        step("4:修改分机号1001,NAT->启用");
        auto.extensionPage().editFirstData().switchToTab("Advanced").isCheckbox(IExtensionPageElement.ele_extension_advanced_enb_nat_checkbox,true).clickSaveAndApply();

        assertStep("5:[PJSIP]期望结果： ");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("direct_media                  : false"),"[direct_media false]");//关联字段，确认为false
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("rtp_symmetric                 : true"),"[rtp_symmetric true]");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("force_rport                   : true"),"[force_rport true]");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("rewrite_contact               : true"),"[rewrite_contact true]");

        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Advanced")
    @Description("T38Support 功能验证：" +
            "1:login PBX" +
            "2:创建分机号1001,T38Support->禁用->3:配置生效： t38_udptl                     : false" +
            "4:修改分机号1001,T38Support->启用 ->5:配置生效： t38_udptl                     : true")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLinks(value = {@TmsLink(value = "ID1001626"), @TmsLink(value = "ID1001625")})
    @Test(groups = "P0,TestExtensionAdvanced,testT38Support,Regression,PSeries")
    public void testT38Support() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,T38Support->禁用");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).
                editFirstData().switchToTab("Advanced").
                isCheckbox(IExtensionPageElement.ele_extension_advanced_enb_t38_support_checkbox,true).
                clickSaveAndApply();

        assertStep("3:[PJSIP]期望结果：t38_udptl                     : true ");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("t38_udptl                     : true"),"[t38_udptl false]");

        step("4:修改分机号1001,T38Support->启用");
        auto.extensionPage().editFirstData().switchToTab("Advanced").isCheckbox(IExtensionPageElement.ele_extension_advanced_enb_t38_support_checkbox,false).clickSaveAndApply();

        assertStep("5:[PJSIP]期望结果：t38_udptl                     : false");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"1001").contains("t38_udptl                     : false"),"[t38_udptl false]");

        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Advanced")
    @Description("Quailty 功能验证：" +
            "1:login PBX" +
            "2:创建分机号1001,Quailty->禁用->3:配置生效： qualify_frequency                     : 0" +
            "4:修改分机号1001,Quailty->启用 ->5:配置生效： qualify_frequency                     : 60")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLinks(value = {@TmsLink(value = "ID1001628"), @TmsLink(value = "ID1001627")})
    @Test(groups = "P0,TestExtensionAdvanced,testQualify,Regression,PSeries")
    public void testQualify() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,Quailty->禁用");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).
                editFirstData().switchToTab("Advanced").
                isCheckbox(IExtensionPageElement.ele_extension_advanced_enb_qualify_checkbox,false).
                clickSaveAndApply();

        assertStep("3:[PJSIP]期望结果：qualify_frequency                     : 0");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_AOR+"1001").contains("qualify_frequency    : 0"),"[qualify_frequency 0]");

        step("4:修改分机号1001,Quailty->启用");
        auto.extensionPage().editFirstData().switchToTab("Advanced").isCheckbox(IExtensionPageElement.ele_extension_advanced_enb_qualify_checkbox,true).clickSaveAndApply();

        assertStep("5:[PJSIP]期望结果：qualify_frequency                     : 60 ");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_AOR+"1001").contains("qualify_frequency    : 60"),"[qualify_frequency 60]");

        softAssert.assertAll();
    }
}
