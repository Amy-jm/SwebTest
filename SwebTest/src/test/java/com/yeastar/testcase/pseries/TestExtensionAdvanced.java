package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.IExtensionPageElement;

import com.yeastar.page.pseries.TestCaseBase;
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
    @Test
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
}
