package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.jcraft.jsch.JSchException;
import com.yeastar.controllers.WebDriverFactory;
import com.yeastar.page.pseries.CallControl.IInboundRoutePageElement;
import com.yeastar.page.pseries.ExtensionTrunk.ExtensionPage;
import com.yeastar.page.pseries.ExtensionTrunk.IExtensionPageElement;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage.RECORD;

import java.io.IOException;
import java.util.ArrayList;

import static com.codeborne.selenide.Selenide.sleep;

/**
 * @program: SwebTest
 * @description: Operator Panel Test Case
 * @author: huangjx@yeastar.com
 * @create: 2020/07/30
 */
public class TestOperatorPanel extends TestCaseBase {


    /**
     * 前提条件
     * 1.添加0分机和sps中继到 路由AutoTest_Route
     */
    public void prerequisite(){
        //新增呼出路由 添加分机0 ，到路由AutoTest_Route
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        list.clear();
        list.add(SPS);
        list2.clear();
        list2.add("0");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        //创建分机
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("0",EXTENSION_PASSWORD).
                switchToTab("Linkus Clients").editDataByEditImage("all").editLinksClientsUserType(ExtensionPage.USER_TYPE.Manager).clickSaveAndApply();
        auto.extensionPage().createSipExtension("1000",EXTENSION_PASSWORD).
                switchToTab("Linkus Clients").editDataByEditImage("all").editLinksClientsUserType(ExtensionPage.USER_TYPE.Manager).clickSaveAndApply();
//        auto.extensionPage().createSipExtension("1001",EXTENSION_PASSWORD).
//                switchToTab("Linkus Clients").editDataByEditImage("all").editLinksClientsUserType(ExtensionPage.USER_TYPE.Manager).clickSaveAndApply();
//        auto.extensionPage().createSipExtension("1002",EXTENSION_PASSWORD).
//                switchToTab("Linkus Clients").editDataByEditImage("all").editLinksClientsUserType(ExtensionPage.USER_TYPE.Manager).clickSaveAndApply();
        //创建trunk
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_trunks);
        auto.trunkPage().deleteTrunk(getDriver(),SPS).createSpsTrunk(SPS,DEVICE_ASSIST_2,DEVICE_ASSIST_2).clickSaveAndApply();

        //创建Inbound
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().deleteAllInboundRoutes().createInboundRoute("InRoute1",list).selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(),"1000").clickSaveAndApply();

        auto.homePage().logout();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->呼入状态：ringing\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRingStatus","Regression","PSeries"})
    public void testIncomingRingStatus(){
        prerequisite();

        //TODO 前提条件 1.新增分机0,1000   2.呼入路由到 1000
        step("1:login PBX");
        auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 1001 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_IP_LAN,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:Ring显示状态 ");
        WebDriverFactory.getDriver().navigate().refresh();//todo  bug  首次登录界面没不会显示，需要刷新界面重新订阅服务subscribe,修复后 删除刷新操作
        sleep(WaitUntils.SHORT_WAIT);

        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ring");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1000 A[1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"External");
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->DragAndDrop 到1002（Taking）\n" +
            "1:分机0,login web client\n" +
            "2:内部分机[1003]-->[1002]通话\n+" +
            "3：")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRingStatus","Regression","PSeries"})
    public void testIncomingDragAndDropWithCTaking(){
        //TODO 前提条件 1.新增分机0,1000   2.呼入路由到 1000




        step("1:login PBX");
        auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 1001 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"991001",DEVICE_IP_LAN,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:Ring显示状态 ");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ring");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1000 A[1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"External");
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->DragAndDrop 到1002（idle）\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRingStatus","Regression","PSeries"})
    public void testIncomingDragAndDropWithCIdle(){
        //TODO 前提条件 1.新增分机0,1000   2.呼入路由到 1000
        step("1:login PBX");
        auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 1001 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"991001",DEVICE_IP_LAN,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:Ring显示状态 ");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ring");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1000 A[1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"External");
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->DragAndDrop 到1002（unregistered）\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRingStatus","Regression","PSeries"})
    public void testIncomingDragAndDropWithCunregistered(){
        //TODO 前提条件 1.新增分机0,1000   2.呼入路由到 1000
        step("1:login PBX");
        auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 1001 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"991001",DEVICE_IP_LAN,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:Ring显示状态 ");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ring");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1000 A[1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"External");
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();

    }
}
