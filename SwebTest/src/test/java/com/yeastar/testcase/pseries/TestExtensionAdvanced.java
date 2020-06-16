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

        step("2:创建分机号1001");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).
                editFirstData().switchToTab("Advanced").select_DTMF_Mode(IExtensionPageElement.DTMF_MODE.RFC4733RFC2833).clickSaveAndApply();

        assertStep("3:验证保存成功");
        String strResult = execAsterisk(PJSIP_SHOW_ENDPOINT+"1001");
        Assert.assertTrue(strResult.contains("dtmf_mode :rfc4733"),"[Assert,dtmf mode]");

    }
}
