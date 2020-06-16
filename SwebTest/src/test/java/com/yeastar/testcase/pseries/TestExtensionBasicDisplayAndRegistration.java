package com.yeastar.testcase.pseries;

import co.boorse.seleniumtable.SeleniumTable;
import co.boorse.seleniumtable.SeleniumTableCell;
import co.boorse.seleniumtable.SeleniumTableRow;
import com.codeborne.selenide.Condition;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.AllureReporterListener;
import com.yeastar.untils.TableUtils;
import com.yeastar.untils.TestNGListenerP;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static com.yeastar.swebtest.driver.SwebDriverP.ys_waitingTime;


@Listeners({AllureReporterListener.class, TestNGListenerP.class})
@Log4j2
public class TestExtensionBasicDisplayAndRegistration extends TestCaseBase {

    @Epic("P_Series")
    @Feature("Extension")
    @Story("BasicDisplayAndRegistration")
    @Description("添加分机0：1:login PBX->2:创建分机号0->3:验证保存成功->4:删除分机->5:验证删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("ID1001513")
    @Issue("")
    @Test(groups = "P0,TestExtensionBasicDisplayAndRegistration,testAddExtension_0,Regression,PSeries")
    public void testAddExtension_0() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号0:" +
                "[extension number] 0 "+
                "[first name]:Yeastar Test0 " +
                "[last name]:朗视信息科技 " +
                "[caller id]:(0591)-Ys.0 " +
                "[register name]:YeastarTest0 " +
                "[register password]:Yeastar202");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        //todo pbx settings->preference->User Extension 字段保持默认，1000-5999；
        //todo pbx settings->preference->display name format字段保持默认设置为：first name last name with space
        auto.extensionPage().deleAllExtension().createSipExtension("0","Yeastar Test0","朗视信息科技","(0591)-Ys.0","YeastarTest0","Yeastar202");

        assertStep("3:验证保存成功");
        String strResult = execAsterisk(PJSIP_SHOW_ENDPOINT+"0");
        Assert.assertTrue(strResult.contains("CALLERNAME                    : Yeastar Test0 朗视信息科技"),"[Assert,CALLERNAME]");
        Assert.assertTrue(strResult.contains("CALLERNUM                     : (0591)-Ys.0"),"[Assert CALLERNUM]");

    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("BasicDisplayAndRegistration")
    @Description("添加分机9999999：1:login PBX->2:创建分机号0->3:验证保存成功->4:删除分机->5:验证删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("ID1001514")
    @Issue("")
    @Test(groups = "P0,TestExtensionBasicDisplayAndRegistration,testAddExtension_9999999,Regression,PSeries")
    public void testAddExtension_9999999() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号9999999:" +
                "[extension number]：9999999"+
                "[first name]:Yeastar Test0 " +
                "[last name]:朗视信息科技 " +
                "[caller id]:(0591)-Ys.9999999 " +
                "[register name]:YeastarTest9999999 " +
                "[register password]:Yeastar202");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        //todo pbx settings->preference->User Extension 字段保持默认，1000-5999；
        //todo pbx settings->preference->display name format字段保持默认设置为：first name last name with space
        auto.extensionPage().deleAllExtension().createSipExtension("9999999","Yeastar Test9999999","朗视信息科技","(0591)-Ys.9999999","YeastarTest9999999","Yeastar202");

        assertStep("3:验证保存成功");
        String strResult = execAsterisk(PJSIP_SHOW_ENDPOINT+"9999999");
        Assert.assertTrue(strResult.contains("CALLERNAME                    : Yeastar Test9999999 朗视信息科技"),"[Assert,CALLERNAME]");
        Assert.assertTrue(strResult.contains("CALLERNUM                     : (0591)-Ys.9999999"),"[Assert CALLERNUM]");

    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("BasicDisplayAndRegistration")
    @Description("分机0呼叫分机999999：1:login PBX->2:创建分机号0->3:验证保存成功->4:删除分机->5:验证删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("1001515")
    @Issue("")
    @Test(groups = "P0,TestExtensionBasicDisplayAndRegistration,testCalled0To9999999,Regression,PSeries")
    public void testCalled0To9999999() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号0:" +
                "[extension number] 0 "+
                "[first name]:Yeastar Test0 " +
                "[last name]:朗视信息科技 " +
                "[caller id]:(0591)-Ys.0 " +
                "[register name]:YeastarTest0 " +
                "[register password]:Yeastar202"+
                "3:创建分机号9999999:" +
                "[extension number]：9999999"+
                "[first name]:Yeastar Test0 " +
                "[last name]:朗视信息科技 " +
                "[caller id]:(0591)-Ys.9999999 " +
                "[register name]:YeastarTest9999999 " +
                "[register password]:Yeastar202");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().
                deleAllExtension().
                createSipExtension("0","Yeastar Test0","朗视信息科技","(0591)-Ys.0","0","Yeastar202").
                createSipExtension("9999999","Yeastar Test9999999","朗视信息科技","(0591)-Ys.9999999","9999999","Yeastar202");

        assertStep("3:[PJSIP注册]] 注册分机0,分机 9999999");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(9999999,DEVICE_IP_LAN);

        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(0,"9999999",DEVICE_IP_LAN,false);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();

        assertStep("4:验证CDR，第一条记录：Communication Type=Internal ");
        //todo cdr 显示全选
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        //todo delete sleep
        ys_waitingTime(WaitUntils.SHORT_WAIT);
        Assert.assertEquals(TableUtils.getCDRForHeader(getDriver(),"Communication Type",0),"Internal");
    }

}




