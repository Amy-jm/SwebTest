package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.jcraft.jsch.JSchException;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.yeastar.page.pseries.CallControl.IInboundRoutePageElement;
import com.yeastar.page.pseries.ExtensionTrunk.IExtensionPageElement;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.PageEngine;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.*;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

//移动分机3000  外线分机2000
@Listeners({AllureReporterListener.class, TestNGListenerP.class})
@Log4j2
public class TestExtensionPresence extends TestCaseBase{

    private boolean runRecoveryEnvFlag=true;
//    @BeforeMethod

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->前置条件")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","Extension", "Regression", "PSeries","A_RecoveryEnv"},priority =0 )
    public void A_RecoveryEnv() {
        if(runRecoveryEnvFlag){
            ArrayList<String> list = new ArrayList<>();
            ArrayList<String> list2 = new ArrayList<>();

            step("【环境准备】1、登录pbx");
            loginWithAdmin();

//            step("【环境准备】1、设置名称显示格式");
//            preparationStepNameDisplay();
//
//            step("【环境准备】2、创建分机");
//            auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
//            auto.extensionPage().deleAllExtension().createSipExtension("0","Yeastar Test0","朗视信息科技","(0591)-Ys.0","0",EXTENSION_PASSWORD).clickSave();
//            auto.extensionPage().createSipExtension("9999999","Yeastar Test9999999","朗视信息科技","(0591)-Ys.9999999","9999999",EXTENSION_PASSWORD).clickSave();
//            auto.extensionPage().createSipExtension("1000","F1000","朗视信息科技","(0591)-Ys.1000","1000",EXTENSION_PASSWORD).clickSave();
//
//            step("【环境准备】3、创建Trunk");
//            auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_trunks);
//
//            step("【环境准备】4、创建队列");
//            auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_queue);
//            list.clear();
//            list.add("Yeastar Test9999999 朗视信息科技");
//            auto.queuePage().deleAllQueue().createQueue("6400","6400").editStaticAgents(list).clickSave();
//            list.clear();
//            list.add("Yeastar Test0 朗视信息科技");
//            auto.queuePage().createQueue("6402","6402").editDynamicAgents(list).clickSave();
//            auto.queuePage().createQueue("6401","6401").editStaticAgents(list).clickSave();
//
//            step("【环境准备】5、创建IVR");
//            auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
//            auto.ivrPage().deleAllIVR().createIVR("6200","6200","Yeastar Test9999999 朗视信息科技").clickSave();
//
//            step("【环境准备】6、创建响铃组");
//            auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ring_group);
//            list.clear();
//            list.add("Yeastar Test9999999 朗视信息科技");
//            auto.ringGroupPage().deleAllRingGroup().createRingGroup("6300","6300",list).clickSave();
//            list.clear();
//            list.add("Yeastar Test0 朗视信息科技");
//            auto.ringGroupPage().createRingGroup("6301","6301",list).clickSave();
//
//            step("【环境准备】7、创建呼入路由");
//            auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
//            list.clear();
//            list.add("SPS1");
//            auto.inboundRoute().deleteAllInboundRoutes()
//                    .createInboundRoute("InRoute1",list)
//                    .editInbound("InRoute1","Name")
//                    .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(),"0-Yeastar Test0 朗视信息科技")
//                    .clickSave();

            step("【环境准备】8、创建呼出路由");
            auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_outbound_routes);
            list.clear();
            list.add("SPS1");
            list2.clear();
            list2.add("Yeastar Test0 朗视信息科技");
            list2.add("Yeastar Test9999999 朗视信息科技");
//            auto.outBoundRoutePage().deleteAllOutboundRoutes().createOutbound("Outbound1",list,list2)
//                    .addPatternAndStrip(0,"90.","2").clickSave();
            auto.outBoundRoutePage().editOutbound("Outbound1","Name")
                    .addPatternAndStrip(0,"90.","2").clickSave();

            auto.ringGroupPage().clickApply();

            auto.homePage().logout();
            runRecoveryEnvFlag = false;
            pjsip.Pj_Init();
        }

    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Available：" +
            "内部分机1000呼叫分机0" +
            "移动分机和分机0 同时振铃" +
            "响铃30s超时无应答回复480/404，分机9999999响铃")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence", "PresenceAvailable","Extension", "Regression", "PSeries"} )
    public void testPresenceAvailable1000Call0NoAnswer(){

        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAvailable();

        step("内部分机1000呼叫分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"0",DEVICE_IP_LAN,false);
        sleep(1000);

        assertStep("辅助设备移动分机3000和分机0 同时振铃");
        softAssert.assertEquals(pjsip.getUserAccountInfo(1000).status,RING,"分机1000预期响铃");
        softAssert.assertEquals(pjsip.getUserAccountInfo(3000).status,RING,"移动分机3000预期响铃");

        assertStep("响铃30s超时无应答，分机9999999振铃");
        sleep(30000);
        softAssert.assertEquals(getExtensionStatus(9999999, RING, 8),RING,"响铃30s超时无应答，分机9999999预期振铃");

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Available：" +
            "内部分机1000呼叫分机0" +
            "移动分机和分机0 同时振铃" +
            "分机0拒接回复486、600，进入分机0 voicemail（cli确认进入voicemail）" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceAvailable", "Extension", "Regression", "PSeries"})
    public void testPresenceAvailable1000Call0Busy() {

        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAvailable();

        clearasteriskLog();

        step("内部分机1000呼叫分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"0",DEVICE_IP_LAN,false);
        sleep(1000);

        assertStep("辅助设备分机3000和分机0 同时振铃");
        softAssert.assertEquals(pjsip.getUserAccountInfo(1000).status,RING,"分机1000预期响铃");
        softAssert.assertEquals(pjsip.getUserAccountInfo(3000).status,RING,"移动分机3000预期响铃");

        assertStep("3000挂断，0回复486拒接，3000和0处于挂断状态");
        pjsip.Pj_Answer_Call(3000,486,false);
        sleep(1000);
        pjsip.Pj_Answer_Call(0,486,false);
        softAssert.assertEquals(getExtensionStatus(3000, HUNGUP, 8),HUNGUP,"预期移动分机3000挂断");
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP,"预期分机0挂断");
        softAssert.assertEquals(getExtensionStatus(1000, TALKING, 8),TALKING,"预期分机1000通话中");

        sleep(20000);

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();

        assertStep("判断CLI中应该存在判断CLI中应该存在");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("vm-greeting-leave-after-tone.slin"),"[Assert,cli确认voicemail提示音]");
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Available：" +
            "sps 外线呼入分机0" +
            "分机0、移动分机均未响铃" +
            "转接到Ivr6200，按0，分机9999999响铃")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceAvailable", "Extension", "Regression", "PSeries"})
    public void testPresenceAvailable2000Call0ToIVR(){

        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAvailable();

        step("编辑呼入路由，目的地到分机0");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(),"0-Yeastar Test0 朗视信息科技").clickSaveAndApply();

        step("sps外线呼入分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(1000);

        assertStep("分机0、移动分机3000均未响铃");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE,"预期分0未响铃，处于空闲");
        softAssert.assertEquals(getExtensionStatus(3000, IDLE, 8),IDLE,"预期移动分机3000未响铃，处于空闲");

        step("转接到IVR 6200-6200，按0");
        pjsip.Pj_Send_Dtmf(2000,"0");

        assertStep("分机9999999响铃");
        softAssert.assertEquals(getExtensionStatus(9999999, RING, 8),RING,"预期分机9999999响铃");
        pjsip.Pj_Answer_Call(9999999,200,false);
        softAssert.assertEquals(getExtensionStatus(9999999, TALKING, 8),TALKING,"预期分机9999999通话中");
        sleep(5000);

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Available：" +
            "sps外线呼入响铃组6301" +
            "移动分机和分机0 同时振铃" +
            "分机0响铃20s后应答,移动分机不再响铃")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceAvailable", "Extension", "Regression", "PSeries"})
    public void testPresenceAvailable2000CallRingGroup(){

        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAvailable();

        step("编辑呼入路由，目的地到响铃组6301");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                           .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.RING_GROUP.getAlias(),"6301-6301").clickSaveAndApply();

        step("sps外线呼入响铃组6300");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(1000);

        assertStep("移动分机3000和分机0 同时振铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"预期分机0响铃");
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING,"预期移动分机3000响铃");

        step("分机0响铃20s后应答");
        sleep(20000);
        pjsip.Pj_Answer_Call(0,200,false);

        assertStep("移动分机3000不再响铃");
        softAssert.assertEquals(getExtensionStatus(3000, HUNGUP, 8),HUNGUP,"预期移动分机3000挂断");

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Available：" +
            "sps 外线呼入队列6401" +
            "移动分机和分机0 同时振铃" +
            "移动分机响铃20s应答,分机0不再响铃")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceAvailable", "Extension", "Regression", "PSeries"})
    public void testPresenceAvailable2000CallQueue6401(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAvailable();

        step("设置呼入路由到队列6401");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.QUEUE.getAlias(),"6401-6401").clickSaveAndApply();

        step("sps外线呼入队列6401");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(1000);

        assertStep("移动分机3000和分机0 同时振铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"分机0预期响铃");
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING,"移动分机3000预期响铃");

        step("移动分机3000响铃20s后应答");
        sleep(20000);
        pjsip.Pj_Answer_Call(3000,200,false);

        assertStep("分机0不再响铃");
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP,"预期分机0不再响铃");

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Available：" +
            "sps 外线呼入队列6402" +
            "移动分机响铃20s应答" +
            "分机0不再响铃，cdr记录正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceAvailable", "PresenceAvailable", "Extension", "Regression", "PSeries"})
    public void testPresenceAvailable2000CallQueue6402(){

        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAvailable();

        step("设置呼入路由到队列6402");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.QUEUE.getAlias(),"6402-6402").clickSaveAndApply();

        step("sps外线呼入队列6402");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(1000);

        assertStep("移动分机3000和分机0 同时振铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"分机0预期响铃");
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING,"预期移动分机3000响铃");

        step("分机0应答");
        pjsip.Pj_Answer_Call(0,200,false);


        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Away：" +
            "内部分机1000呼叫分机0" +
            "分机0响铃、移动分机未响铃" +
            "响铃10s超时无应答回复480/404" +
            "转接到队列6400，成员9999999响铃")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceAway", "Extension", "Regression", "PSeries"})
    public void testPresenceAway1000Call0(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAway();

        step("内部分机1000呼叫分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"0",DEVICE_IP_LAN,false);
        sleep(1000);

        assertStep("移动分机3000和分机0 均未响铃");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE,"预期分机00未响铃，处于空闲");
        softAssert.assertEquals(getExtensionStatus(3000, IDLE, 8),IDLE,"预期移动分机3000未响铃，处于空闲");

        assertStep("分机9999999响铃");
        softAssert.assertEquals(getExtensionStatus(9999999, RING, 8),RING);


        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Away：" +
            "sps 外线呼入分机0" +
            "检查分机0和移动分机状态" +
            "分机0响铃、移动分机未响铃" +
            "响铃10s超时无应答回复480/404 转接到队列6400，成员9999999响铃")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceAway", "Extension", "Regression", "PSeries"})
    public void testPresenceAway2000Call0ToNoAnswer(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAway();

        step("设置呼入路由到分机0");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(),"0-Yeastar Test0 朗视信息科技").clickSaveAndApply();

        step("sps 外线呼入分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(1000);

        assertStep("分机0响铃、移动分机3000未响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"预期分机0响铃");
        softAssert.assertEquals(getExtensionStatus(3000, IDLE, 8),IDLE,"预期分机3000不响铃");

        assertStep("响铃10s超时无应答回复480/404,转接到队列6400，成员9999999响铃");
        sleep(11000);
        softAssert.assertEquals(getExtensionStatus(9999999, RING, 8),RING,"预期分机9999999响铃");

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Away：" +
            "sps 外线呼入分机0" +
            "检查分机0和移动分机状态" +
            "分机0拒接回复486、600" +
            "转接呼叫mobile，移动分机3000响铃")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceAway", "Extension", "Regression", "PSeries"})
    public void testPresenceAway2000Call0ToBusy(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAway();

        step("设置呼入路由到分机0");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(),"0-Yeastar Test0 朗视信息科技").clickSaveAndApply();

        step("sps 外线呼入分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(1000);

        assertStep("分机0响铃、移动分机3000未响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"预期分机0响铃");
        softAssert.assertEquals(getExtensionStatus(3000, IDLE, 8),IDLE,"预期移动分机3000响铃");

        assertStep("分机0拒接回复486、600,转接呼叫mobile，移动分机3000响铃");
        pjsip.Pj_Answer_Call(0,486,false);
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING,"预期移动分机3000响铃");

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Away：" +
            "分机1000呼入响铃组6301" +
            "分机0响铃20s后应答")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceAway", "Extension", "Regression", "PSeries"})
    public void testPresenceAway1000CallRingGroup(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAway();

        step("分机1000呼入响铃组6301");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"6301",DEVICE_IP_LAN,false);
        sleep(1000);

        assertStep("分机0响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"预期分机0响铃");
        pjsip.Pj_Answer_Call(0,200,false);

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Away：" +
            "分机1000呼入队列6401" +
            "分机0响铃20s后应答" +
            "cdr记录正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence", "PresenceAway","Extension", "Regression", "PSeries"})
    public void testPresenceAway1000CallQueue6401(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAway();

        step("分机1000呼入队列6401");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"6401",DEVICE_IP_LAN,false);
        sleep(1000);

        assertStep("预期分机0不响铃");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE,"预期分机0不响铃");
//        sleep(20000);
//        pjsip.Pj_Answer_Call(0,200,false);
//        sleep(5000);

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Away：" +
            "内部分机呼入到队列6402" +
            "分机0不会响铃" +
            "queue show queue 6402 确认分机0状态为pause")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("功能没有完成")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceAway", "Extension", "Regression", "PSeries"})
    public void testPresenceAway1000CallQueue6402(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAway();

        step("内部分机呼入到队列6402");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"6402",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0不会响铃");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE,"分机0不会响铃");

        assertStep("CLI验证：queue show queue 6402 确认分机0状态为pause");
        Assert.assertTrue(execAsterisk("queue show queue-6402").contains("pause"));


        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Business Trip：" +
            "内部分机1000呼叫分机0" +
            "分机0、移动分机同时响铃" +
            "响铃10s超时无应答回复480/404" +
            "转接到自定义号码903000,辅助设备3000分机响铃")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence", "PresenceBusinessTrip","Extension", "Regression", "PSeries"})
    public void testPresenceBusinessTrip1000Call0ToNoAnswer(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsBusinessTrip();

        step("内部分机1000呼叫分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"0",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0、移动分机3000同时响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"预期分机0响铃");
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING,"预期分机3000响铃");

        step("响铃10s超时无应答回复480/404");
        sleep(11000);
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP,"分机0响铃10s超时,预期分机0挂断");
        softAssert.assertEquals(getExtensionStatus(2000, RING, 8),RING,"分机0响铃10s超时，预期分机2000响铃");

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Business Trip：" +
            "内部分机1000呼叫分机0" +
            "分机0、移动分机同时响铃" +
            "分机0拒接回复486、600" +
            "转接到播放两次提示音test.wav，播放两次后通话挂断,cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("分机486后走到noanser")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceBusinessTrip", "Extension", "Regression", "PSeries"})
    public void testPresenceBusinessTrip1000Call0ToBusy(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsBusinessTrip();

        clearasteriskLog();

        step("内部分机1000呼叫分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"0",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0、移动分机同时响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"预期分机0响铃");
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING,"预期分机3000响铃");

        step("分机0拒接回复486、600");
        pjsip.Pj_Answer_Call(3000,486,false);
        pjsip.Pj_Answer_Call(0,486,false);
        sleep(10000);
        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();

        assertStep("CDR 转接到播放两次提示音test.wav，播放两次后通话挂断");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("test.slin"),"[Assert,cli确认提示音]");
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Business Trip：" +
            "sps 外线呼入分机0" +
            "移动分机和分机0 同时振铃" +
            "响铃10s超时无应答回复480/404" +
            "转接到hangup，通话直接被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceBusinessTrip", "Extension", "Regression", "PSeries"})
    public void testPresenceBusinessTrip2000Call0ToNoAnswer(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsBusinessTrip();

        step("编辑呼入路由，目的地到分机0");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(),"0-Yeastar Test0 朗视信息科技").clickSaveAndApply();

        step("sps 外线呼入分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(3000);

        assertStep("分机0、移动分机同时响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"预期分机0响铃");
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING,"预期分机3000响铃");

        step("响铃10s超时无应答回复486/600");
        sleep(11000);
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP,"分机0响铃超时10s后预期挂断");
        softAssert.assertEquals(getExtensionStatus(3000, HUNGUP, 8),HUNGUP,"分机0响铃超时10s后，移动分机3000预期挂断");

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Business Trip：" +
            "sps 外线呼入分机0" +
            "移动分机和分机0 同时振铃" +
            "分机0拒接回复486、600" +
            "转接到voicemail")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceBusinessTrip", "Extension", "Regression", "PSeries"})
    public void testPresenceBusinessTrip2000Call0ToBusy() {
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsBusinessTrip();

        step("编辑呼入路由，目的地到分机0");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(),"0-Yeastar Test0 朗视信息科技").clickSaveAndApply();

        clearasteriskLog();

        step("sps 外线呼入分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(3000);

        assertStep("分机0、移动分机3000同时响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"预期分机0响铃");
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING,"预期分机3000响铃");

        step("分机0拒接回复486、600");
        pjsip.Pj_Answer_Call(3000,486,false);
        pjsip.Pj_Answer_Call(0,486,false);
        sleep(20000);

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();

        assertStep("转接到voicemail，判断CLI中应该存在");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("vm-greeting-leave-after-tone.slin"),"[Assert,cli确认voicemail提示音]");
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Business Trip：" +
            "内部分机呼入到队列6402" +
            "分机0不会响铃" +
            "queue show queue 6402 确认分机0不在队列6402中")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceBusinessTrip", "Extension", "Regression", "PSeries"})
    public void testPresenceBusinessTrip1000CallQueue6402(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsBusinessTrip();

        step("内部分机呼入到队列6402");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"6402",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0不会响铃");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE,"预期分机0不响铃");

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();

        assertStep("CLI验证：queue show queue 6402 确认分机0不在队列6402中");
        softAssert.assertFalse(execAsterisk("queue show queue-6402").contains("Yeastar Test0 朗视信息科技"));
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Do Not Disturb：" +
            "内部分机1000呼叫分机0" +
            "分机0及移动分机均不响铃，通话被直接挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceDoNotDisturb", "Extension", "Regression", "PSeries"})
    public void testPresenceDoNotDisturb1000Call0(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsDoNotDisturb();

        step("内部分机1000呼叫分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"0",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0及移动分机均不响铃，通话被直接挂断");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE,"预期分机0不响铃");
        softAssert.assertEquals(getExtensionStatus(3000, IDLE, 8),IDLE,"预期分机3000不响铃");
        softAssert.assertEquals(getExtensionStatus(1000, HUNGUP, 8),HUNGUP,"预期分机1000挂断");

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }
    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Do Not Disturb：" +
            "sps 外线呼入分机0" +
            "分机0及移动分机均不响铃，通话被转移到手机90 3000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceDoNotDisturb", "Extension", "Regression", "PSeries"})
    public void testPresenceDoNotDisturb2000Call0(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsDoNotDisturb();

        step("编辑呼入路由，目的地到分机0");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(),"0-Yeastar Test0 朗视信息科技").clickSaveAndApply();

        step("sps 外线呼入分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(3000);

        assertStep("分机0及移动分机均不响铃，通话被转移到手机90 3000");

        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE,"预期分机0不响铃");
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING,"预期移动分机3000响铃");

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Do Not Disturb：" +
            "内部分机呼入到队列6401" +
            "分机0不会响铃")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceDoNotDisturb", "Extension", "Regression", "PSeries"})
    public void testPresenceDoNotDisturb1000CallQueue6401(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsDoNotDisturb();

        step("内部分机1000呼入到队列6401");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"6401",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0不会响铃");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE,"预期分机0不响铃");

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Do Not Disturb：" +
            "SPS 外线呼入到队列6401" +
            "分机0不会响铃")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceDoNotDisturb", "Extension", "Regression", "PSeries"})
    public void testPresenceDoNotDisturb2000CallQueue6401(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsDoNotDisturb();

        step("设置呼入路由到队列6401");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.QUEUE.getAlias(),"6401-6401").clickSaveAndApply();

        step("SPS 外线呼入到队列6401");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(3000);

        assertStep("分机0不会响铃");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE,"预期分机0不响铃");

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Do Not Disturb：" +
            "内部分机呼入到队列6402" +
            "分机0不会响铃" +
            "queue show queue 6402 确认分机0不在队列6402中")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceDoNotDisturb",  "Extension", "Regression", "PSeries"})
    public void testPresenceDoNotDisturb1000CallQueue6402(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsDoNotDisturb();

        step("内部分机1000呼入到队列6402");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"6402",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0不会响铃");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE,"预期分机0不响铃");


        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();

        assertStep("CLI验证：queue show queue 6402 确认分机0不在队列6402中");
        softAssert.assertTrue(!execAsterisk("queue show queue-6402").contains("Yeastar Test0"));
        softAssert.assertAll();
    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Lunch：" +
            "内部分机1000呼叫分机0" +
            "移动分机和分机0 同时振铃" +
            "响铃40s超时无应答回复480/404,转接到voicemail")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence", "PresenceLunch", "Extension", "Regression", "PSeries"})
    public void testPresenceLunch1000Call0ToNoAnswer() {
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsLunchBreak();

        clearasteriskLog();

        step("内部分机1000呼叫分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"0",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("移动分机和分机0 同时振铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"预期分机0响铃");
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING,"预期分机3000响铃");

        sleep(60000);

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();

        assertStep("判断CLI中应该存在语音留言提示音");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("vm-greeting-leave-after-tone.slin"),"[Assert,cli确认voicemail提示音]");
        softAssert.assertAll();
    }
    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Lunch：" +
            "内部分机1000呼叫分机0" +
            "移动分机和分机0 同时振铃" +
            "分机0拒接回复486、600" +
            "通话直接被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("zoudao vm")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceLunch",  "Extension", "Regression", "PSeries"})
    public void testPresenceLunch1000Call0ToBusy(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsLunchBreak();

        step("内部分机1000呼叫分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"0",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("移动分机和分机0 同时振铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"预期分机0响铃");
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING,"预期分机3000响铃");

        assertStep("分机0拒接回复486、600，通话直接被挂断");
        pjsip.Pj_Answer_Call(3000,486,false);
        pjsip.Pj_Answer_Call(0,486,false);
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP,"预期分机0挂断");
        softAssert.assertEquals(getExtensionStatus(3000, HUNGUP, 8),HUNGUP,"预期分机3000挂断");
        softAssert.assertEquals(getExtensionStatus(1000, HUNGUP, 8),HUNGUP,"预期分机1000挂断");

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Lunch：" +
            "sps 外线呼入分机0" +
            "移动分机和分机0 同时振铃" +
            "响铃40s超时无应答回复480/404,通话直接被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence", "PresenceLunch", "Extension", "Regression", "PSeries"})
    public void testPresenceLunch2000Call0ToNoAnswer(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsLunchBreak();

        step("编辑呼入路由，目的地到分机0");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(),"0-Yeastar Test0 朗视信息科技").clickSaveAndApply();

        step("sps外线呼入分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(1000);

        assertStep("移动分机和分机0 同时振铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"预期分机0响铃");
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING,"预期分机3000响铃");

        assertStep("响铃40s超时无应答回复480/404,通话直接被挂断");
        sleep(40000);
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP,"分机0响铃40s超时无应答后，预期分机0挂断");
        softAssert.assertEquals(getExtensionStatus(3000, HUNGUP, 8),HUNGUP,"分机0响铃40s超时无应答后，预期分机3000挂断");

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Lunch：" +
            "sps 外线呼入分机0" +
            "移动分机和分机0 同时振铃" +
            "分机0拒接回复486、600,通话直接被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceLunch",  "Extension", "Regression", "PSeries"})
    public void testPresenceLunch2000Call0ToBusy(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsLunchBreak();

        step("编辑呼入路由，目的地到分机0");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(),"0-Yeastar Test0 朗视信息科技").clickSaveAndApply();

        step("sps 外线呼入分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"995503300",DEVICE_ASSIST_2,false);
        sleep(1000);

        assertStep("移动分机和分机0 同时振铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"分机0响铃");
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING,"移动分机3000响铃");

        assertStep("分机0拒接回复486、600，通话直接被挂断");
        pjsip.Pj_Answer_Call(3000,486,false);
        pjsip.Pj_Answer_Call(0,486,false);
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP,"分机0拒接回复486,分机0挂断");
        softAssert.assertEquals(getExtensionStatus(3000, HUNGUP, 8),HUNGUP,"分机3000拒接回复486,分机0挂断");
        softAssert.assertEquals(getExtensionStatus(2000, HUNGUP, 8),HUNGUP,"分机3000拒接回复486,分机0挂断");

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Lunch：" +
            "内部分机呼入到队列6402" +
            "分机0不会响铃,queue show queue 6402 确认分机0不在队列6402中")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceLunch", "Extension", "Regression", "PSeries"})
    public void testPresenceLunch1000CallQueue6402(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsLunchBreak();

        step("内部分机呼入到队列6402");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"6402",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0不会响铃");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE,"分机0不会响铃");


        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();

        assertStep("CLI验证：queue show queue 6402 确认分机0不在队列6402中");
        softAssert.assertTrue(!execAsterisk("queue show queue-6402").contains("Yeastar Test0"));
        softAssert.assertAll();
    }



    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Off Work：" +
            "内部分机1000呼叫分机0" +
            "检查分机0和移动分机状态" +
            "分机0响铃，移动分机不会响铃" +
            "响铃25s超时无应答回复480/404,通话直接被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence", "PresenceOffWork","Extension", "Regression", "PSeries"})
    public void testPresenceOffWork1000Call0ToNoAnswer(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsOffWork();

        step("内部分机1000呼叫分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"0",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0响铃，移动分机不会响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"分机0响铃");
        softAssert.assertEquals(getExtensionStatus(3000, IDLE, 8),IDLE,"移动分机3000不会响铃");

        assertStep("响铃25s超时无应答回复480/404，通话直接被挂断");
        sleep(25000);
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP,"分机0挂断");
        softAssert.assertEquals(getExtensionStatus(3000, IDLE, 8),IDLE,"移动分机3000不会响铃");
        softAssert.assertEquals(getExtensionStatus(1000, HUNGUP, 8),HUNGUP,"分机1000挂断");


        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }
    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Off Work：" +
            "内部分机1000呼叫分机0" +
            "分机0响铃，移动分机不会响铃" +
            "分机0拒接回复486、600,通话直接被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceOffWork", "Extension", "Regression", "PSeries"})
    public void testPresenceOffWork1000Call0ToBusy(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsOffWork();

        step("内部分机1000呼叫分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"0",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0响铃，移动分机不会响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"分机0响铃");
        softAssert.assertEquals(getExtensionStatus(3000, IDLE, 8),IDLE,"移动分机3000不会响铃");

        assertStep("分机0拒接回复486、600，通话直接被挂断");
        pjsip.Pj_Answer_Call(0,486,false);
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP,"分机0挂断");
        softAssert.assertEquals(getExtensionStatus(3000, IDLE, 8),IDLE,"移动分机3000不会响铃");
        softAssert.assertEquals(getExtensionStatus(1000, HUNGUP, 8),HUNGUP,"分机1000挂断");

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Off Work：" +
            "sps 外线呼入分机0" +
            "分机0、移动分机均不会响铃" +
            "通话转接到分机9999999,9999999分机响铃")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceOffWork", "Extension", "Regression", "PSeries"})
    public void testPresenceOffWork2000Call0(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsOffWork();

        step("编辑呼入路由，目的地到分机0");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(),"0-Yeastar Test0 朗视信息科技").clickSaveAndApply();

        step("sps 外线呼入分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(1000);

        assertStep("分机0、移动分机均不会响铃");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE,"分机0预期不会响铃");
        softAssert.assertEquals(getExtensionStatus(3000, IDLE, 8),IDLE,"分机3000预期不会响铃");

        assertStep("通话转接到分机9999999,9999999分机响铃");
        softAssert.assertEquals(getExtensionStatus(9999999, RING, 8),RING,"分机9999999预期响铃");

        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->Off Work：" +
            "内部分机呼入到队列6402" +
            "分机0会响铃" +
            "queue show queue 6402 确认分机0在队列6402中")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue("")
    @Test(groups = {"P0", "TestExtensionPresence", "Presence","PresenceOffWork", "Extension", "Regression", "PSeries"})
    public void testPresenceOffWork1000CallQueue6402(){
        A_RecoveryEnv();

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsOffWork();

        step("内部分机呼入到队列6402");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(9999999,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"6402",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0会响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"预期分机0响铃");


        pjsip.Pj_Hangup_All();
        //pjsip.Pj_Destory();

        assertStep("CLI验证：queue show queue 6402 确认分机0在队列6402中");
        softAssert.assertTrue(execAsterisk("queue show queue-6402").contains("Local/0@only-dialextension-q6402"));
        softAssert.assertAll();
    }

    /**
     * Available模式前置步骤
     */
    void preparationStepsAvailable(){


        //删除所有分机 创建分机0
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);

        auto.extensionPage().editExtension(getDriver(),"0").ele_extension_user_mobile_number.setValue("3000");
        auto.extensionPage().switchToTab(IExtensionPageElement.TABLE_MENU.PRESENCE.getAlias()).ele_extension_presence_available_tab.click();
        auto.extensionPage().configCallForwardInternalNoAnswer(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.EXTENSION.getAlias(),"9999999-Yeastar Test9999999 朗视信息科技")
                .configCallForwardInternalBusy(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.VOICEMAIL.getAlias())
                .configCallForwardExternalAlways(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.IVR.getAlias(),"6200-6200")
                .isCheckbox(IExtensionPageElement.ele_extension_presence_ring_simultaneously_checkBox,true)
                .selectCombobox(IExtensionPageElement.AGENT_STATUS_AUTO_SWITCH.LOGIN.getAlias())
                .ele_extension_presence_ring_simultaneously_prefix_input.setValue("90");
        auto.extensionPage().clickSave();

        auto.extensionPage().selectExtensionPresence("0",IExtensionPageElement.TABLE_PRESENCE_LIST.AVAILABLE.getAlias()).clickApply();

        auto.extensionPage().clickApply();
    }

    /**
     * Away模式前置步骤
     */
    void preparationStepsAway(){

        //删除所有分机 创建分机0
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);

        auto.extensionPage().editExtension(getDriver(),"0").ele_extension_user_mobile_number.setValue("3000");
        auto.extensionPage().switchToTab(IExtensionPageElement.TABLE_MENU.PRESENCE.getAlias()).ele_extension_presence_away_tab.click();
        auto.extensionPage().configCallForwardInternalAlways(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.RING_GROUP.getAlias(),"6300-6300")
                .configCallForwardExternalNoAnswer(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.QUEUE.getAlias(),"6400-6400")
                .configCallForwardExternalBusy(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.MOBILE_NUMBER.getAlias(),"90")
                .isCheckbox(IExtensionPageElement.ele_extension_presence_ring_simultaneously_checkBox,false)
                .selectCombobox(IExtensionPageElement.AGENT_STATUS_AUTO_SWITCH.PAUSE.getAlias())
                .selectComm(auto.extensionPage().ele_extension_presence_ring_timeout_combobox,"10");
        auto.extensionPage().clickSave();

        auto.extensionPage().selectExtensionPresence("0",IExtensionPageElement.TABLE_PRESENCE_LIST.AWAY.getAlias()).clickApply();

        auto.extensionPage().clickApply();


    }

    /**
     * BusinessTrip模式前置步骤
     */
    void preparationStepsBusinessTrip(){

        //删除所有分机 创建分机0
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);

        auto.extensionPage().editExtension(getDriver(),"0").ele_extension_user_mobile_number.setValue("3000");
        auto.extensionPage().switchToTab(IExtensionPageElement.TABLE_MENU.PRESENCE.getAlias()).ele_extension_presence_businessTrip_tab.click();
        auto.extensionPage().configCallForwardInternalNoAnswer(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.EXTERNAL_NUMBER.getAlias(),"90","2000")
                .configCallForwardInternalBusy(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.PLAY_GREETING_THEN_HANG_UP.getAlias(),"test.wav","2")
                .configCallForwardExternalNoAnswer(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.HANG_UP.getAlias())
                .configCallForwardExternalBusy(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.VOICEMAIL.getAlias())
                .isCheckbox(IExtensionPageElement.ele_extension_presence_ring_simultaneously_checkBox,true)
                .selectCombobox(IExtensionPageElement.AGENT_STATUS_AUTO_SWITCH.LOGOUT.getAlias())
                .selectComm(auto.extensionPage().ele_extension_presence_ring_timeout_combobox,"20");

        auto.extensionPage().clickSave();

        auto.extensionPage().selectExtensionPresence("0",IExtensionPageElement.TABLE_PRESENCE_LIST.BUSINESSTRIP.getAlias()).clickApply();

        auto.extensionPage().clickApply();
    }

    /**
     * Do Not Disturb模式前置步骤
     */
    void preparationStepsDoNotDisturb(){

        //删除所有分机 创建分机0
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);

        auto.extensionPage().editExtension(getDriver(),"0").ele_extension_user_mobile_number.setValue("3000");
        auto.extensionPage().switchToTab(IExtensionPageElement.TABLE_MENU.PRESENCE.getAlias()).ele_extension_presence_doNotDisturb_tab.click();
        auto.extensionPage().configCallForwardInternalAlways(false)
                .configCallForwardExternalAlways(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.MOBILE_NUMBER.getAlias(),"90")
                .selectCombobox(IExtensionPageElement.AGENT_STATUS_AUTO_SWITCH.DO_NOTHING.getAlias());

        auto.extensionPage().clickSave();

        auto.extensionPage().selectExtensionPresence("0",IExtensionPageElement.TABLE_PRESENCE_LIST.DONotDISTURB.getAlias()).clickApply();

        auto.extensionPage().clickApply();
    }

    /**
     * Lunch Break模式前置步骤
     */
    void preparationStepsLunchBreak(){

        //删除所有分机 创建分机0
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);

        auto.extensionPage().editExtension(getDriver(),"0").ele_extension_user_mobile_number.setValue("3000");
        auto.extensionPage().switchToTab(IExtensionPageElement.TABLE_MENU.PRESENCE.getAlias()).ele_extension_presence_launch_tab.click();
        auto.extensionPage().configCallForwardInternalNoAnswer(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.VOICEMAIL.getAlias())
                .configCallForwardInternalBusy(false)
                .configCallForwardExternalAlways(false)
                .configCallForwardExternalNoAnswer(false)
                .configCallForwardExternalBusy(false)
                .isCheckbox(IExtensionPageElement.ele_extension_presence_ring_simultaneously_checkBox,true)
                .selectCombobox(IExtensionPageElement.AGENT_STATUS_AUTO_SWITCH.LOGOUT.getAlias())
                .ele_extension_presence_ring_timeout_input.setValue("40");

        auto.extensionPage().clickSave();

        auto.extensionPage().selectExtensionPresence("0", IExtensionPageElement.TABLE_PRESENCE_LIST.AVAILABLE.getAlias()).clickApply();
        sleep(3000);
        auto.extensionPage().selectExtensionPresence("0", IExtensionPageElement.TABLE_PRESENCE_LIST.LUNCHBREAK.getAlias()).clickApply();

    }

    /**
     * OFF WORK模式前置步骤
     */
    void preparationStepsOffWork(){

        //删除所有分机 创建分机0
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);

        auto.extensionPage().editExtension(getDriver(),"0").ele_extension_user_mobile_number.setValue("3000");
        auto.extensionPage().switchToTab(IExtensionPageElement.TABLE_MENU.PRESENCE.getAlias()).ele_extension_presence_available_tab.click();
        auto.extensionPage().selectCombobox(IExtensionPageElement.AGENT_STATUS_AUTO_SWITCH.LOGIN.getAlias());

        auto.extensionPage().switchToTab(IExtensionPageElement.TABLE_MENU.PRESENCE.getAlias()).ele_extension_presence_off_work_tab.click();
        auto.extensionPage().configCallForwardInternalAlways(false)
                .configCallForwardInternalNoAnswer(false)
                .configCallForwardInternalBusy(false)
                .configCallForwardExternalAlways(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.EXTENSION.getAlias(),"9999999-Yeastar Test9999999 朗视信息科技")
                .isCheckbox(IExtensionPageElement.ele_extension_presence_ring_simultaneously_checkBox,false)
                .selectCombobox(IExtensionPageElement.AGENT_STATUS_AUTO_SWITCH.DO_NOTHING.getAlias())
                .ele_extension_presence_ring_timeout_input.setValue("25");

        auto.extensionPage().clickSave();

        auto.extensionPage().selectExtensionPresence("0", IExtensionPageElement.TABLE_PRESENCE_LIST.AVAILABLE.getAlias()).clickApply();
        sleep(1000);
        auto.extensionPage().selectExtensionPresence("0", IExtensionPageElement.TABLE_PRESENCE_LIST.OFFWORK.getAlias()).clickApply();

    }
}
