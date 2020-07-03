package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.yeastar.page.pseries.CallControl.IInboundRoutePageElement;
import com.yeastar.page.pseries.CdrRecording.ICdrPageElement;
import com.yeastar.page.pseries.ExtensionTrunk.IExtensionPageElement;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.PbxSettings.IPreferencesPageElement;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.AllureReporterListener;
import com.yeastar.untils.TableUtils;
import com.yeastar.untils.TestNGListenerP;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.sleep;

//移动分机3000  外线分机2000
//@Listeners({AllureReporterListener.class, TestNGListenerP.class})
@Log4j2
public class TestExtensionPresence extends TestCaseBase{

    @Test
    public void CaseName() {

        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));
        preparationStepsAway();

    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceAvailable1000Call0NoAnswer(){

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
        softAssert.assertEquals(pjsip.getUserAccountInfo(1000).status,RING);
        softAssert.assertEquals(pjsip.getUserAccountInfo(3000).status,RING);


        assertStep("响铃30s超时无应答，分机9999999振铃");
        sleep(30000);
        softAssert.assertEquals(getExtensionStatus(9999999, RING, 8),RING);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceAvailable1000Call0Busy(){

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

        assertStep("辅助设备分机3000和分机0 同时振铃");
        softAssert.assertEquals(pjsip.getUserAccountInfo(1000).status,RING);
        softAssert.assertEquals(pjsip.getUserAccountInfo(3000).status,RING);

        assertStep("3000挂断，0回复486拒接，3000和0处于挂断状态");
        pjsip.Pj_Answer_Call(3000,603,false);
        pjsip.Pj_Answer_Call(0,486,false);
        softAssert.assertEquals(getExtensionStatus(3000, HUNGUP, 8),HUNGUP);
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP);
        softAssert.assertEquals(getExtensionStatus(1000, TALKING, 8),TALKING);

        sleep(20000);
        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        //todo 待验证
        assertStep("判断CDR");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(), ICdrPageElement.CDR_HEADER.Call_From.getAlias(),0),"F1000 朗视信息科技<1000>");
//        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Call_To.getAlias(),0),"Voicemail Yeastar Test0 朗视信息科技<0>");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Status.getAlias(),0),"VOICEMAIL");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Reason.getAlias(),0),"F1000 朗视信息科技<1000> hung up");

    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceAvailable2000Call0ToIVR(){

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAvailable();

        step("sps 外线呼入分机0");
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
        pjsip.Pj_Make_Call_No_Answer(2000,"8550330",DEVICE_IP_LAN,false);
        sleep(1000);

        assertStep("分机0、移动分机3000均未响铃");
        softAssert.assertEquals(getExtensionStatus(1000, IDLE, 8),IDLE);
        softAssert.assertEquals(getExtensionStatus(3000, IDLE, 8),IDLE);

        step("转接到Ivr6200，按0");
        pjsip.Pj_Send_Dtmf(2000,"0");

        assertStep("分机9999999响铃");
        softAssert.assertEquals(getExtensionStatus(9999999, RING, 8),RING);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        //todo 待验证
//        assertStep("判断CDR");
//        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(), ICdrPageElement.CDR_HEADER.Call_From.getAlias(),0),"F1000 朗视信息科技<1000>");
//        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Call_To.getAlias(),0),"Voicemail Yeastar Test0 朗视信息科技<0>");
//        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Status.getAlias(),0),"VOICEMAIL");
//        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Reason.getAlias(),0),"F1000 朗视信息科技<1000> hung up");

    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceAvailable2000CallRingGroup(){

        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAvailable();

        step("设置呼入路由到响铃组");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                           .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.RING_GROUP.getAlias(),"6300-RingGroup");

        step("sps外线呼入响铃组6300");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"8550330",DEVICE_IP_LAN,false);
        sleep(1000);

        assertStep("移动分机3000和分机0 同时振铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING);

        step("分机0响铃20s后应答");
        sleep(20000);
        pjsip.Pj_Answer_Call(0,200,false);

        assertStep("移动分机3000不再响铃");
        softAssert.assertEquals(getExtensionStatus(3000, HUNGUP, 8),HUNGUP);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        //todo 待验证
//        assertStep("判断CDR");
//        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(), ICdrPageElement.CDR_HEADER.Call_From.getAlias(),0),"F1000 朗视信息科技<1000>");
//        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Call_To.getAlias(),0),"Voicemail Yeastar Test0 朗视信息科技<0>");
//        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Status.getAlias(),0),"VOICEMAIL");
//        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Reason.getAlias(),0),"F1000 朗视信息科技<1000> hung up");

    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceAvailable2000CallQueue1(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAvailable();

        step("设置呼入路由到队列6400");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.RING_GROUP.getAlias(),"6400-Queue");

        step("sps外线呼入响铃组6400");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"8550330",DEVICE_IP_LAN,false);
        sleep(1000);

        assertStep("移动分机3000和分机0 同时振铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING);

        step("移动分机3000响铃20s后应答");
        sleep(20000);
        pjsip.Pj_Answer_Call(3000,200,false);

        assertStep("分机0不再响铃");
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        //todo 待验证
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceAvailable2000CallQueue2(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAvailable();

        step("设置呼入路由到响铃组");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.RING_GROUP.getAlias(),"6402-Queue");

        step("sps外线呼入队列6402");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"8550330",DEVICE_IP_LAN,false);
        sleep(1000);

        assertStep("移动分机3000和分机0 同时振铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING);

        step("分机0应答");
        pjsip.Pj_Answer_Call(0,200,false);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        //todo 待验证
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceAway1000Call0(){
        step("登录pbx");
        loginWithAdmin();

        //todo 创建响铃组，设置
        step("编辑响铃组6300，分机9999999");

        step("初始化环境");
        preparationStepsAway();


        step("内部分机1000呼叫分机0");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(1000,"0",DEVICE_IP_LAN,false);
        sleep(1000);

        assertStep("移动分机3000和分机0 均未响铃");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE);
        softAssert.assertEquals(getExtensionStatus(3000, IDLE, 8),IDLE);

        assertStep("分机9999999均未响铃");
        softAssert.assertEquals(getExtensionStatus(9999999, RING, 8),RING);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceAway2000Call0ToNoAnswer(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAway();

        //todo 编辑队列，成员9999999
        step("编辑队列，成员9999999");


        step("设置呼入路由到分机0");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(),"0-Yeastar Test0 朗视信息科技");

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
        pjsip.Pj_Make_Call_No_Answer(2000,"8550330",DEVICE_IP_LAN,false);
        sleep(1000);

        assertStep("移动分机3000和分机0 均未响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);
        softAssert.assertEquals(getExtensionStatus(3000, IDLE, 8),IDLE);

        assertStep("响铃10s超时无应答回复480/404,转接到队列6400，成员9999999响铃");
        sleep(11000);
        softAssert.assertEquals(getExtensionStatus(9999999, RING, 8),RING);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceAway2000Call0ToBusy(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAway();

        step("设置呼入路由到分机0");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(),"0-Yeastar Test0 朗视信息科技");

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
        pjsip.Pj_Make_Call_No_Answer(2000,"8550330",DEVICE_IP_LAN,false);
        sleep(1000);

        assertStep("移动分机3000和分机0 均未响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);
        softAssert.assertEquals(getExtensionStatus(3000, IDLE, 8),IDLE);

        assertStep("分机0拒接回复486、600,转接呼叫mobile，移动分机3000响铃");
        pjsip.Pj_Answer_Call(2000,486,false);
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceAway1000CallRingGroup(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAway();

        //todo 编辑响铃组，成员0
        step("编辑响铃组，成员0");

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
        pjsip.Pj_Make_Call_No_Answer(1000,"6300",DEVICE_IP_LAN,false);
        sleep(1000);

        assertStep("分机0响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceAway1000CallQueue1(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAway();

        //todo 编辑队列6401，成员0
        step("编辑队列，成员0");

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
        pjsip.Pj_Make_Call_No_Answer(1000,"6401",DEVICE_IP_LAN,false);
        sleep(1000);

        assertStep("分机0响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceAway1000CallQueue2(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsAway();

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
        pjsip.Pj_Make_Call_No_Answer(1000,"6402",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0响铃");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE);

        //todo  ?????
        assertStep("CLI验证：queue show queue 6402 确认分机0状态为pause");
        Assert.assertTrue(execAsterisk("queue show queue-6402").contains("pause"));

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceBusinessTrip1000Call0ToNoAnswer(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsBusinessTrip();

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
        pjsip.Pj_Make_Call_No_Answer(1000,"0",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0、移动分机同时响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING);

        step("响铃10s超时无应答回复480/404");
        sleep(11000);
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP);
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();


    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceBusinessTrip1000Call0ToBusy(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsBusinessTrip();

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
        pjsip.Pj_Make_Call_No_Answer(1000,"0",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0、移动分机同时响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING);

        step("响铃10s超时无应答回复486/600");
        pjsip.Pj_Answer_Call(0,486,false);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        assertStep("CDR 转接到播放两次提示音test.wav，播放两次后通话挂断");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceBusinessTrip2000Call0ToNoAnswer(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsBusinessTrip();

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
        pjsip.Pj_Make_Call_No_Answer(2000,"8550330",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0、移动分机同时响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING);

        step("响铃10s超时无应答回复486/600");
        sleep(11000);
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP);
        softAssert.assertEquals(getExtensionStatus(3000, HUNGUP, 8),HUNGUP);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();


    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceBusinessTrip2000Call0ToBusy(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsBusinessTrip();

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
        pjsip.Pj_Make_Call_No_Answer(2000,"8550330",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0、移动分机同时响铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING);

        step("响铃10s超时无应答回复486/600");
        pjsip.Pj_Answer_Call(2000,486,false);
        sleep(20000);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceBusinessTrip1000CallQueue6402(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsBusinessTrip();

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
        pjsip.Pj_Make_Call_No_Answer(1000,"0",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0、移动分机同时响铃");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        //todo  ?????
        assertStep("CLI验证：queue show queue 6402 确认分机0不在队列6402中");
        Assert.assertTrue(execAsterisk("queue show queue-6402").contains("pause"));

    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceDoNotDisturb1000Call0(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsDoNotDisturb();

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
        pjsip.Pj_Make_Call_No_Answer(1000,"0",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0、移动分机同时响铃");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE);
        softAssert.assertEquals(getExtensionStatus(3000, IDLE, 8),IDLE);
        softAssert.assertEquals(getExtensionStatus(1000, HUNGUP, 8),HUNGUP);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();
    }
    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceDoNotDisturb2000Call0(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsDoNotDisturb();

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
        pjsip.Pj_Make_Call_No_Answer(2000,"8550330",DEVICE_ASSIST_2,false);
        sleep(3000);

        assertStep("分机0及移动分机均不响铃，通话被转移到手机90 3000");
        //TODO 3000分机重复
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE);
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceDoNotDisturb1000CallQueue6401(){
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
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceDoNotDisturb2000CallQueue6401(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsDoNotDisturb();

        step("设置呼入路由到队列6401");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.RING_GROUP.getAlias(),"6401-6401");

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
        pjsip.Pj_Make_Call_No_Answer(2000,"8550330",DEVICE_ASSIST_2,false);
        sleep(3000);

        assertStep("分机0不会响铃");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceDoNotDisturb1000CallQueue6402(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsDoNotDisturb();


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
        pjsip.Pj_Make_Call_No_Answer(1000,"6402",DEVICE_IP_LAN,false);
        sleep(3000);

        assertStep("分机0不会响铃");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        assertStep("CLI验证：queue show queue 6402 确认分机0不在队列6402中");
        Assert.assertTrue(!execAsterisk("queue show queue-6402").contains("Yeastar Test0"));

    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceLunch1000Call0ToNoAnswer(){
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
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING);

        sleep(60000);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

    }
    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceLunch1000Call0ToBusy(){
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
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING);

        pjsip.Pj_Answer_Call(1000,486,false);
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP);
        softAssert.assertEquals(getExtensionStatus(3000, HUNGUP, 8),HUNGUP);
        softAssert.assertEquals(getExtensionStatus(1000, HUNGUP, 8),HUNGUP);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceLunch2000Call0ToNoAnswer(){
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
        sleep(1000);

        assertStep("移动分机和分机0 同时振铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING);

        assertStep("响铃40s超时无应答回复480/404,通话直接被挂断");
        sleep(4000);
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP);
        softAssert.assertEquals(getExtensionStatus(3000, HUNGUP, 8),HUNGUP);
        softAssert.assertEquals(getExtensionStatus(1000, HUNGUP, 8),HUNGUP);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceLunch2000Call0ToBusy(){
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
        sleep(1000);

        assertStep("移动分机和分机0 同时振铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING);

        pjsip.Pj_Answer_Call(1000,486,false);
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP);
        softAssert.assertEquals(getExtensionStatus(3000, HUNGUP, 8),HUNGUP);
        softAssert.assertEquals(getExtensionStatus(1000, HUNGUP, 8),HUNGUP);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();
    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceLunch1000CallQueue6402(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsLunchBreak();


        step("SPS 外线呼入到队列6402");
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
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        assertStep("CLI验证：queue show queue 6402 确认分机0不在队列6402中");
        Assert.assertTrue(!execAsterisk("queue show queue-6402").contains("Yeastar Test0"));

    }



    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceOffWork1000Call0ToNoAnswer(){
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

        assertStep("移动分机和分机0 同时振铃");
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING);

        sleep(25000);
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP);
        softAssert.assertEquals(getExtensionStatus(3000, HUNGUP, 8),HUNGUP);
        softAssert.assertEquals(getExtensionStatus(1000, HUNGUP, 8),HUNGUP);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

    }
    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceOffWork1000Call0ToBusy(){
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
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);
        softAssert.assertEquals(getExtensionStatus(3000, RING, 8),RING);

        pjsip.Pj_Answer_Call(1000,486,false);
        softAssert.assertEquals(getExtensionStatus(0, HUNGUP, 8),HUNGUP);
        softAssert.assertEquals(getExtensionStatus(3000, HUNGUP, 8),HUNGUP);
        softAssert.assertEquals(getExtensionStatus(1000, HUNGUP, 8),HUNGUP);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceOffWork2000Call0(){
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
        sleep(1000);

        assertStep("移动分机和分机0 同时振铃");
        softAssert.assertEquals(getExtensionStatus(0, IDLE, 8),IDLE);
        softAssert.assertEquals(getExtensionStatus(3000, IDLE, 8),IDLE);

        sleep(2000);
        softAssert.assertEquals(getExtensionStatus(9999999, RING, 8),RING);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Presence MODE 功能验证：")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionPresence,testPresenceModel,Regression,PSeries")
    public void testPresenceOffWork1000CallQueue6402(){
        step("登录pbx");
        loginWithAdmin();

        step("初始化环境");
        preparationStepsOffWork();


        step("SPS 外线呼入到队列6402");
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
        softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING);

        step("挂断所有通话，注销pjsip");
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        assertStep("CLI验证：queue show queue 6402 确认分机0在队列6402中");
        Assert.assertTrue(execAsterisk("queue show queue-6402").contains("Yeastar Test0"));

    }



    /**
     * Available模式前置步骤
     */
    void preparationStepsAvailable(){

        preparationStepNameDisplay();

        //删除所有分机 创建分机0
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("0","Yeastar Test0","朗视信息科技","(0591)-Ys.0","0",EXTENSION_PASSWORD).clickSave();
        auto.extensionPage().createSipExtension("9999999","Yeastar Test9999999","朗视信息科技","(0591)-Ys.9999999","9999999",EXTENSION_PASSWORD).clickSave();
        auto.extensionPage().createSipExtension("1000","F1000","朗视信息科技","(0591)-Ys.1000","1000",EXTENSION_PASSWORD).clickSave();

        auto.extensionPage().editExtension(getDriver(),"0").ele_extension_user_mobile_number.setValue("3000");
        auto.extensionPage().switchToTab(IExtensionPageElement.TABLE_MENU.PRESENCE.getAlias()).ele_extension_presence_available_tab.click();
        auto.extensionPage().configCallForwardInternalNoAnswer(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.EXTENSION.getAlias(),"9999999-Yeastar Test9999999 朗视信息科技")
                .configCallForwardInternalBusy(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.VOICEMAIL.getAlias())
                .configCallForwardExternalAlways(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.IVR.getAlias(),"VR 6200")
                .isCheckbox(IExtensionPageElement.ele_extension_presence_ring_simultaneously_checkBox,true)
                .selectCombobox(IExtensionPageElement.AGENT_STATUS_AUTO_SWITCH.LOGIN.getAlias())
                .ele_extension_presence_ring_simultaneously_prefix_input.setValue("90");
        auto.extensionPage().clickSave();

        auto.extensionPage().selectExtensionPresence("0","Available");

        auto.extensionPage().clickApply();
    }

    /**
     * Away模式前置步骤
     */
    void preparationStepsAway(){
            //TODO 创建分机队列IVR
        preparationStepNameDisplay();

        //删除所有分机 创建分机0
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("0","Yeastar Test0","朗视信息科技","(0591)-Ys.0","0",EXTENSION_PASSWORD).clickSave();
        auto.extensionPage().createSipExtension("9999999","Yeastar Test9999999","朗视信息科技","(0591)-Ys.9999999","9999999",EXTENSION_PASSWORD).clickSave();
        auto.extensionPage().createSipExtension("1000","F1000","朗视信息科技","(0591)-Ys.1000","1000",EXTENSION_PASSWORD).clickSave();

        auto.extensionPage().editExtension(getDriver(),"0").ele_extension_user_mobile_number.setValue("3000");
        auto.extensionPage().switchToTab(IExtensionPageElement.TABLE_MENU.PRESENCE.getAlias()).ele_extension_presence_away_tab.click();
        auto.extensionPage().configCallForwardInternalAlways(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.RING_GROUP.getAlias(),"6300-RG")
                .configCallForwardExternalNoAnswer(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.QUEUE.getAlias(),"6400-queue")
                .configCallForwardExternalBusy(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.MOBILE_NUMBER.getAlias(),"90")
                .isCheckbox(IExtensionPageElement.ele_extension_presence_ring_simultaneously_checkBox,false)
                .selectCombobox(IExtensionPageElement.AGENT_STATUS_AUTO_SWITCH.PAUSE.getAlias())
                .selectComm(auto.extensionPage().ele_extension_presence_ring_timeout_combobox,"10");
        auto.extensionPage().clickSave();

        auto.extensionPage().selectExtensionPresence("0","Away");

        auto.extensionPage().clickApply();


    }

    /**
     * BusinessTrip模式前置步骤
     */
    void preparationStepsBusinessTrip(){

        preparationStepNameDisplay();

        //删除所有分机 创建分机0
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("0","Yeastar Test0","朗视信息科技","(0591)-Ys.0","0",EXTENSION_PASSWORD).clickSave();
        auto.extensionPage().createSipExtension("9999999","Yeastar Test9999999","朗视信息科技","(0591)-Ys.9999999","9999999",EXTENSION_PASSWORD).clickSave();
        auto.extensionPage().createSipExtension("1000","F1000","朗视信息科技","(0591)-Ys.1000","1000",EXTENSION_PASSWORD).clickSave();

        auto.extensionPage().editExtension(getDriver(),"0").ele_extension_user_mobile_number.setValue("3000");
        auto.extensionPage().switchToTab(IExtensionPageElement.TABLE_MENU.PRESENCE.getAlias()).ele_extension_presence_businessTrip_tab.click();
        auto.extensionPage().configCallForwardInternalNoAnswer(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.EXTERNAL_NUMBER.getAlias(),"90","2000")
                .configCallForwardInternalBusy(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.PLAY_GREETING_THEN_HANG_UP.getAlias(),"test.wav","2")
                .configCallForwardExternalNoAnswer(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.HANG_UP.getAlias())
                .configCallForwardExternalBusy(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.VOICEMAIL.getAlias())
                .isCheckbox(IExtensionPageElement.ele_extension_presence_ring_simultaneously_checkBox,false)
                .selectCombobox(IExtensionPageElement.AGENT_STATUS_AUTO_SWITCH.LOGOUT.getAlias())
                .selectComm(auto.extensionPage().ele_extension_presence_ring_timeout_combobox,"20");

        auto.extensionPage().clickSave();

        auto.extensionPage().selectExtensionPresence("0","Business Trip");

        auto.extensionPage().clickApply();
    }

    /**
     * Do Not Disturb模式前置步骤
     */
    void preparationStepsDoNotDisturb(){

        preparationStepNameDisplay();

        //删除所有分机 创建分机0
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("0","Yeastar Test0","朗视信息科技","(0591)-Ys.0","0",EXTENSION_PASSWORD).clickSave();
        auto.extensionPage().createSipExtension("9999999","Yeastar Test9999999","朗视信息科技","(0591)-Ys.9999999","9999999",EXTENSION_PASSWORD).clickSave();
        auto.extensionPage().createSipExtension("1000","F1000","朗视信息科技","(0591)-Ys.1000","1000",EXTENSION_PASSWORD).clickSave();

        auto.extensionPage().editExtension(getDriver(),"0").ele_extension_user_mobile_number.setValue("3000");
        auto.extensionPage().switchToTab(IExtensionPageElement.TABLE_MENU.PRESENCE.getAlias()).ele_extension_presence_doNotDisturb_tab.click();
        auto.extensionPage().configCallForwardInternalAlways(false)
                .configCallForwardExternalAlways(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.MOBILE_NUMBER.getAlias(),"90")
                .selectCombobox(IExtensionPageElement.AGENT_STATUS_AUTO_SWITCH.DO_NOTHING.getAlias());

        auto.extensionPage().clickSave();

        auto.extensionPage().selectExtensionPresence("0","Do Not Disturb");

        auto.extensionPage().clickApply();
    }

    /**
     * Lunch Break模式前置步骤
     */
    void preparationStepsLunchBreak(){

        preparationStepNameDisplay();

        //删除所有分机 创建分机0
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("0","Yeastar Test0","朗视信息科技","(0591)-Ys.0","0",EXTENSION_PASSWORD).clickSave();
        auto.extensionPage().createSipExtension("9999999","Yeastar Test9999999","朗视信息科技","(0591)-Ys.9999999","9999999",EXTENSION_PASSWORD).clickSave();
        auto.extensionPage().createSipExtension("1000","F1000","朗视信息科技","(0591)-Ys.1000","1000",EXTENSION_PASSWORD).clickSave();

        auto.extensionPage().editExtension(getDriver(),"0").ele_extension_user_mobile_number.setValue("3000");
        auto.extensionPage().switchToTab(IExtensionPageElement.TABLE_MENU.PRESENCE.getAlias()).ele_extension_presence_launch_tab.click();
        auto.extensionPage().configCallForwardInternalNoAnswer(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.VOICEMAIL.getAlias())
                .configCallForwardInternalBusy(false)
                .configCallForwardExternalAlways(false)
                .configCallForwardExternalNoAnswer(false)
                .configCallForwardExternalBusy(false)
                .isCheckbox(IExtensionPageElement.ele_extension_presence_ring_simultaneously_checkBox,true)
                .selectCombobox(IExtensionPageElement.AGENT_STATUS_AUTO_SWITCH.LOGOUT.getAlias())
                .selectComm(auto.extensionPage().ele_extension_presence_ring_timeout_combobox,"40");

        auto.extensionPage().clickSave();

        auto.extensionPage().selectExtensionPresence("0","Lunch Break");

        auto.extensionPage().clickApply();
    }

    /**
     * OFF WORK模式前置步骤
     */
    void preparationStepsOffWork(){

        preparationStepNameDisplay();

        //删除所有分机 创建分机0
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("0","Yeastar Test0","朗视信息科技","(0591)-Ys.0","0",EXTENSION_PASSWORD).clickSave();
        auto.extensionPage().createSipExtension("9999999","Yeastar Test9999999","朗视信息科技","(0591)-Ys.9999999","9999999",EXTENSION_PASSWORD).clickSave();
        auto.extensionPage().createSipExtension("1000","F1000","朗视信息科技","(0591)-Ys.1000","1000",EXTENSION_PASSWORD).clickSave();

        auto.extensionPage().editExtension(getDriver(),"0").ele_extension_user_mobile_number.setValue("3000");
        auto.extensionPage().switchToTab(IExtensionPageElement.TABLE_MENU.PRESENCE.getAlias()).ele_extension_presence_off_work_tab.click();
        auto.extensionPage().configCallForwardInternalAlways(false)
                .configCallForwardInternalNoAnswer(false)
                .configCallForwardInternalBusy(false)
                .configCallForwardExternalAlways(true, IExtensionPageElement.CALL_FORWARDING_DESTINATION.EXTENSION.getAlias(),"9999999-Yeastar Test9999999 朗视信息科技")
                .isCheckbox(IExtensionPageElement.ele_extension_presence_ring_simultaneously_checkBox,false)
                .selectCombobox(IExtensionPageElement.AGENT_STATUS_AUTO_SWITCH.DO_NOTHING.getAlias())
                .ele_extension_presence_ring_timeout_input.setValue("25");

        auto.extensionPage().clickSave();

        auto.extensionPage().selectExtensionPresence("0","Off Work");

        auto.extensionPage().clickApply();
    }
}
