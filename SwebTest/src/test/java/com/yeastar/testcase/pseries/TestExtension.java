package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.APIObject.IVRObject;
import com.yeastar.untils.AllureReporterListener;
import com.yeastar.untils.ExecutionListener;
import com.yeastar.untils.RetryListener;
import com.yeastar.untils.TestNGListenerP;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.ArrayList;


@Listeners({ExecutionListener.class,AllureReporterListener.class, TestNGListenerP.class})
@Log4j2
public class TestExtension extends TestCaseBase {

    @Epic("P_Series")
    @Feature("Extension")
    @Story("新增分机1001，能正常loginMe")
    @Description("1:login PBX->2:创建分机号1001->3:验证保存成功->4:loginMe->5:分机login success")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("ID1001628")
    @Issue("BUG_00001")
    @Test(groups = "P0,testLoginMe,Extension,Regression,PSeries")
    public void testLoginMe(){
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001",EXTENSION_PASSWORD).clickSaveAndApply();

        assertStep("3:验证保存成功");
        Assert.assertTrue(execAsterisk(PJSIP_SHOW_AOR+"1001").contains("1001"));

        step("4:loginMe");
        auto.homePage().logout();
        auto.loginPage().loginWithExtension("1001",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW);
        auto.loginPage().login("1001",EXTENSION_PASSWORD_NEW);

        assertStep("5:分机login success");
        Assert.assertTrue(auto.homePage().header_box_name.shouldHave(Condition.text("1001")).isDisplayed());
    }


    @Epic("Extension")
    @Feature("新增分机1001，注册密码强度不够，分机列表中有提示图标")
    @Story("注册相关->UserExtension nUser->User->registration password")
    @Description("1:login PBX->2:创建分机号1001->3:提示注册密码强度不够，继续保存成功->4:分机列表，有提示注册密码强度不够图标显示")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "B_")
    public void B_registration_password_not_strong_warning(){
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001", EXTENSION_PASSWORD, "ABCDEFGHIJK").clickSave();

        assertStep("3:提示注册密码强度不够，继续保存成功");
        auto.extensionPage().registration_Password_Alert_Exist_And_GoOn();

        assertStep("4:分机列表，有提示注册密码强度不够图标显示");
        auto.extensionPage().ele_extension_list_warning_registration_warning_img.shouldBe(Condition.exist);
    }

    @Epic("Extension")
    @Feature("内部分机互打，always，转移到IVR")
    @Story("注册相关->Presence->DND->call forwarding")
    @Description("1.创建/注册分机1001,1002\n" +
                 "2.创建IVR 6200\n" +
                 "3.设置1002，Internal Calls 设置 always 到IVR\n" +
                 "4.1001 拨打1002\n" +
                 "5.查看CDR")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void C_extension_always_ivr(){
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001，1002");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("1001", EXTENSION_PASSWORD,EXTENSION_PASSWORD).clickSave();
        auto.extensionPage().createSipExtensionAndConf("1002", EXTENSION_PASSWORD,EXTENSION_PASSWORD).configPresence().clickSaveAndApply();

        step("2:创建IVR 6200");
//        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
//        auto.ivrPage().deleAllIVR().createIVR("6200","6200","Yeastar Test9999999 朗视信息科技").clickSaveAndApply();

        ArrayList<IVRObject.PressKeyObject> pressKeyObjects_1 = new ArrayList<>();
        pressKeyObjects_1.clear();
        pressKeyObjects_1.add(new IVRObject.PressKeyObject(IVRObject.PressKey.press0, "", "", "", 0));
        apiUtil.deleteAllIVR().createIVR("6200", "6200", pressKeyObjects_1);

        assertStep("3:提示注册密码强度不够，继续保存成功");
        auto.extensionPage().registration_Password_Alert_Exist_And_GoOn();

        assertStep("4:分机列表，有提示注册密码强度不够图标显示");
        auto.extensionPage().ele_extension_list_warning_registration_warning_img.shouldBe(Condition.exist);
    }

}




