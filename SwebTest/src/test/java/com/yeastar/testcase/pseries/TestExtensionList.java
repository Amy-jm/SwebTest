package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.AllureReporterListener;
import com.yeastar.untils.SSHLinuxUntils;
import com.yeastar.untils.TestNGListenerP;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;


@Listeners({AllureReporterListener.class, TestNGListenerP.class})
@Log4j2
public class TestExtensionList extends TestCaseBase {

    @Epic("P_Series")
    @Feature("Extension")
    @Story("List")
    @Description("删除分机功能：1:login PBX->2:创建分机号1000->3:验证保存成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("ID1001507")
    @Issue("BUG_00001")
    @Test(groups = "P0,testLoginMe,Extension,Regression,PSeries")
    public void testDeleteExtension() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1000");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1000",EXTENSION_PASSWORD);
        execAsterisk(PJSIP_SHOW_AOR+"1000");

        assertStep("3:验证保存成功");
//        Assert.assertTrue(auto.extensionPage().isSaveSuccessAlertAppear());
        Assert.assertTrue(execAsterisk(PJSIP_SHOW_AOR+"1000").contains("1000"));
        auto.extensionPage().deleAllExtension();

        assertStep("[AsteriskAssert]"+PJSIP_SHOW_AOR+"1000");
        Assert.assertTrue(execAsterisk(PJSIP_SHOW_AOR+"1000").contains("Unable to find object 1000"));
    }


}




