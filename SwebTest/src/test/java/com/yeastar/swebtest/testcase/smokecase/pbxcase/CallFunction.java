package com.yeastar.swebtest.testcase.smokecase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.Config.mySettings;
import static com.yeastar.swebtest.driver.Config.pageDeskTop;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2017/7/24.
 */
public class CallFunction {
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();

        Reporter.infoBeforeClass("打开游览器并登录设备PagingFunction"); //执行操作
        initialDriver(CHROME,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.shouldBe(Condition.exist);
        pageDeskTop.CDRandRecording.shouldBe(Condition.exist);
        pageDeskTop.maintenance.shouldBe(Condition.exist);
        mySettings.close.click();
        m_extension.showCDRClounm();

        pjsip.Pj_CreateAccount(1000,"Yeastar202","UDP",1);
        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",3);
        pjsip.Pj_CreateAccount(1101,"Yeastar202","UDP",4);
        pjsip.Pj_CreateAccount(1102,"Yeastar202","UDP",5);
        pjsip.Pj_CreateAccount(6300,"Yeastar202","UDP",-1);
        pjsip.Pj_CreateAccount(991,"Yeastar202","UDP",-1);
        pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",-1);


        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
    }
    @Test
    public void A_PagingIntercom() throws InterruptedException {
        Reporter.infoExec("分机B(1101)拨打6300");
        //分机B拨打6300
        pjsip.Pj_Make_Call_Auto_Answer(1101,"6300", DEVICE_IP_LAN);
        ys_waitingTime(6000);
        pjsip.Pj_Hangup_All();
        //分机A和C自动应答，A与C能直接听到B的声音，B听不到A与C方的声音。查看cdr，cdr记录正确
        m_extension.checkCDR("1101 <1101>","1102 <6300(1102)>","Answered",1,2);
        m_extension.checkCDR("1101 <1101>","1100 <6300(1100)>","Answered",1,2);
    }
    @Test
    public void B_AddBlacklist() throws InterruptedException {
        Reporter.infoExec("添加黑名单test1，分机2000通过sps线路呼入到设备1，呼入失败");
        pageDeskTop.settings.click();
        settings.callControl_panel.click();
        outboundRoutes.outboundRoutes.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        gridClick(outboundRoutes.grid,Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid))),outboundRoutes.gridEdit);
        ys_waitingMask();
        add_outbound_routes.mt_RemoveAllFromSelected.click();
        ArrayList<String> selectTrunk = new ArrayList<>();
        selectTrunk.add("SPS");
        listSelect(add_outbound_routes.list_Trunk,trunkList,selectTrunk);
        add_outbound_routes.save.click();

        inboundRoutes.inboundRoutes.click();
        ys_waitingLoading(inboundRoutes.gridLoading);
        gridClick(inboundRoutes.grid,Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid))),inboundRoutes.gridEdit);
        ys_waitingMask();
        add_inbound_route.SetDestination(add_inbound_route.s_extensin,extensionList,"1000");
        add_inbound_route.save.click();

        settings.callFeatures_tree.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        blacklist.add.click();
        m_callFeature.addBlacklist("test1",add_blacklist.type_Inbound,2000);
        ys_apply();
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(2000,2000,"99",DEVICE_ASSIST_2);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("whitelist",50);
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"通话进入黑名单列表");

    }
    @Test
    public void C_ExportBlacklist(){

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        Reporter.infoExec("导出黑名单");
        blacklist.export.click();
    }
    @Test
    public void D_DeleteBlacklist() throws InterruptedException {
        Reporter.infoExec("删除黑名单，分机2000通过sps线路呼入到设备1，呼入成功");
        if(Single_Device_Test){
            pageDeskTop.settings.click();
            settings.callFeatures_panel.click();
            callFeatures.more.click();
            blacklist_whitelist.blacklist_Whitelist.click();
            ys_waitingLoading(blacklist.grid_loadMask);
        }
        gridClick(blacklist.grid,1,blacklist.gridDelete);
        blacklist.delete_yes.shouldBe(Condition.exist).click();
        ys_apply();
        Thread.sleep(10000);
        pjsip.Pj_Make_Call_Auto_Answer(2000,2000,"99",DEVICE_ASSIST_2);
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();
        ys_waitingTime(5000);
        m_extension.checkCDR("2000 <2000>","1000 <1000>","Answered");
    }
    @Test
    public void E_ImportBlacklist(){
        Reporter.infoExec("点击“Import”，选择之前导出的文件导入");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        blacklist.Import.click();
        import_blacklist.browse.click();
        ys_waitingTime(2000);
        importFile(EXPORT_PATH +"blacklist.csv");
        ys_waitingTime(2000);
        import_blacklist.Import.click();
        import_blacklist.ImportOK.click();

        YsAssert.assertEquals(String.valueOf(gridLineNum(blacklist.grid)),"1","导入黑名单");
    }
    @Test
    public void F_AddWhitelist() throws InterruptedException {
        Reporter.infoExec("添加白名单test2，分机2000通过sps线路呼入到设备1，呼入成功");
        if(Single_Device_Test) {
            pageDeskTop.settings.click();
            settings.callControl_panel.click();
            settings.callFeatures_tree.click();
            callFeatures.more.click();
            blacklist_whitelist.blacklist_Whitelist.click();
        }else{
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
        }
        whitelist.whitelist.click();
        whitelist.add.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        m_callFeature.addWhitelist("test2",add_whitelist.type_Inbound,2000);
//        String actname =  String.valueOf(gridContent(whitelist.grid,Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid))),1)) ;
        ys_apply();
        Thread.sleep(10000);
        pjsip.Pj_Make_Call_Auto_Answer(2000,2000,"99",DEVICE_ASSIST_2);
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1000 <1000>","Answered");
    }
    @Test
    public void G_ExportWhitelist(){
        Reporter.infoExec("导出白名单");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        whitelist.export.click();
    }
    @Test
    public void H_DelWhitelist() throws InterruptedException {
        Reporter.infoExec("删除白名单，分机2000通过sps线路呼入到设备1，呼入失败");
        if(Single_Device_Test) {
            pageDeskTop.settings.click();
            settings.callControl_panel.click();
            settings.callFeatures_tree.click();
            callFeatures.more.click();
            blacklist_whitelist.blacklist_Whitelist.click();
            whitelist.whitelist.click();
            ys_waitingLoading(whitelist.grid_loadMask);
        }
        gridClick(whitelist.grid,1,whitelist.gridDelete);
        whitelist.delete_yes.shouldBe(Condition.exist).click();
        ys_apply();
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(2000,2000,"99",DEVICE_ASSIST_2);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("whitelist",50);
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"通话进入黑名单列表");

    }
    @Test
    public void I_ImportWhitelist() throws InterruptedException {
        Reporter.infoExec("导入白名单");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        whitelist.Import.click();
        import_whitelist.browse.click();
        ys_waitingTime(2000);
        importFile(EXPORT_PATH +"me_whitelist.csv");
        ys_waitingTime(2000);
        import_whitelist.Import.click();
        import_whitelist.ImportOK.click();

        YsAssert.assertEquals(String.valueOf(gridLineNum(whitelist.grid)),"1","导入白名单");
    }
    @Test
    public void J_DelAllBlacklist() throws InterruptedException {
        Reporter.infoExec("批量删除黑名单");

        blacklist.blacklist.click();
        blacklist.add.click();
        m_callFeature.addBlacklist("testall",add_blacklist.type_Inbound,2000);

        gridSeleteAll(blacklist.grid);
        blacklist.delete.click();
        blacklist.delete_yes.click();

    }
    @Test
    public void K_DelAllWhitelist() throws InterruptedException {
        Reporter.infoExec("批量删除白名单");
        whitelist.whitelist.click();
        whitelist.add.click();
        m_callFeature.addWhitelist("testall",add_blacklist.type_Inbound,2000);

        gridSeleteAll(whitelist.grid);
        whitelist.delete.click();
        whitelist.delete_yes.click();
        ys_apply();
    }
    @Test
    public void L_ExtensionPage() throws InterruptedException {
        Reporter.infoExec("登录1100分机页面创建黑名单test1");
        logout();
        login("1100","Yeastar202");
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        m_me.addMeBlacklist("test1",me_add_blacklist.routeType_Inbound,2000);
        ys_me_apply();
        YsAssert.assertEquals(String.valueOf(gridContent(me_blacklist.grid,Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid))),me_blacklist.gridColumn_Name)),"test1");
        YsAssert.assertEquals(String.valueOf(gridContent(me_blacklist.grid,Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid))),me_blacklist.gridColumn_Number)),"2000");
        YsAssert.assertEquals(String.valueOf(gridContent(me_blacklist.grid,Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid))),me_blacklist.gridColumn_Type)),"Inbound");

        tcpSocket.connectToDevice();
        //分机B通过sps线路呼入到设备1    2000 call (sps)9999 -> A 呼入失败，听到呼入失败提示音
        pjsip.Pj_Make_Call_Auto_Answer(2000,2000,"99",DEVICE_ASSIST_2,false);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("whitelist",50);
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"进入黑名单列表");
    }
//    @Test
    public void M_ExtensionExport(){
        Reporter.infoExec("1100分机页面导出黑名单");
        me_blacklist.export.click();
    }
    @Test
    public void N_DeleteTest1Sps() throws InterruptedException {
        Reporter.infoExec("1100分机页面，删除黑名单test1，分机2000通过sps呼入到设备1，呼入成功");
        gridClick(me_blacklist.grid,1,me_blacklist.gridDelete);
        me_blacklist.delete_yes.click();
        ys_me_apply();
        tcpSocket.connectToDevice();
//        分机B通过sps呼入到设备1
        pjsip.Pj_Make_Call_Auto_Answer(2000,2000,"99",DEVICE_ASSIST_2,false);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("ANSWER",20);
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"删除黑名单呼入成功");
    }
    @Test
    public void O_BlacklistImport() throws InterruptedException {
        Reporter.infoExec("1100分机页面，点击“Import”，选择之前导出黑名单的文件导入");
//        m_me.addMeBlacklist("Metest",me_add_blacklist.routeType_Inbound,2000);
        if(Single_Device_Test){
            logout();
            login("1100","Yeastar202");
            me.me.click();
            me.me_Blacklist_Whitelist.click();
            me_blacklist.me_Blacklist.click();
        }
        me_blacklist.Import.click();
        me_import_blacklist.browse.click();
        ys_waitingTime(2000);
        importFile(EXPORT_PATH +"me_blacklist.csv");
        ys_waitingTime(2000);
        me_import_blacklist.Import.click();
        ys_waitingTime(2000);
        me_import_blacklist.ImportOK.click();
        ys_me_apply();
        YsAssert.assertEquals(String.valueOf(gridContent(me_blacklist.grid,Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid))),me_blacklist.gridColumn_Name)),"test1","分机页面导入黑名单");
    }
    @Test
    public void P_ExtensionWhitelist() throws InterruptedException {
        Reporter.infoExec("1100分机页面，删除黑名单test1，分机2000通过sps呼入到设备1，呼入成功");
        logout();
        login("1100","Yeastar202");
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        m_me.addMeWhitelist("test1",me_add_whitelist.routeType_Inbound,2000);
        ys_me_apply();
        YsAssert.assertEquals(String.valueOf(gridContent(me_whitelist.grid,Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid))),me_whitelist.gridColumn_Name)),"test1");
        YsAssert.assertEquals(String.valueOf(gridContent(me_whitelist.grid,Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid))),me_whitelist.gridColumn_Number)),"2000");
        YsAssert.assertEquals(String.valueOf(gridContent(me_whitelist.grid,Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid))),me_whitelist.gridColumn_Type)),"Inbound");

        //2）分机B通过sps线路呼入到设备1  呼入成功
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(2000,2000,"99",DEVICE_ASSIST_2,false);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("ANSWER",20);//Status: 1 表示分机正在忙
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"删除黑名单呼入成功");
    }
//    @Test
    public void Q_ExtensionExport(){
        Reporter.infoExec("1100分机页面，导出白名单");
        me_whitelist.export.click();

    }
    @Test
    public void R_WhiteTest1Delete() throws InterruptedException {
        Reporter.infoExec("1100分机页面，删除白名单，分机2000通过sps呼入到设备1，呼入失败");
        gridClick(me_whitelist.grid,1,me_whitelist.gridDelete);
        me_whitelist.delete_yes.click();
//        分机B通过sps呼入到设备1
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(2000,2000,"99",DEVICE_ASSIST_2,false);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("whitelist",50);
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"进入黑名单列表");
    }
    @Test
    public void S_WhiteImport(){
        Reporter.infoExec("1100分机页面，导入白名单");
        if(Single_Device_Test){
            logout();
            login("1100","Yeastar202");
        }
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        me_whitelist.Import.click();
        me_import_whitelist.browse.click();
        ys_waitingTime(2000);
        importFile(EXPORT_PATH +"me_whitelist.csv");
        ys_waitingTime(2000);
        me_import_whitelist.Import.click();
        ys_waitingTime(2000);
        me_import_whitelist.ImportOK.click();
        //Import
    }
    @Test
    public void T_BlacklistDeleteAll() throws InterruptedException  {
        Reporter.infoExec("1100分机页面，批量删除黑名单");
        me_blacklist.me_Blacklist.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        m_me.addMeBlacklist("alltest",me_add_blacklist.routeType_Inbound,1000);
        ys_me_apply();
        me_blacklist.me_Blacklist.click();
        gridSeleteAll(me_blacklist.grid);
        me_blacklist.delete.click();
        me_blacklist.delete_yes.click();

    }
    @Test
    public void U_WhitelistDeleteAll() throws InterruptedException {
        Reporter.infoExec("1100分机页面，批量删除白名单");
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        m_me.addMeWhitelist("alltestw",me_add_whitelist.routeType_Inbound,1001);
        ys_me_apply();
        me_whitelist.me_Whitelist.click();
        gridSeleteAll(me_whitelist.grid);
        me_whitelist.delete.click();
        me_whitelist.delete_yes.click();
        me.me_apply.click();
    }
    @Test
    public void V_OnlyWhitelistCheck() throws InterruptedException {
        Reporter.infoExec("1100分机页面，勾选OnlyWhitelist，分机2000通过sps呼入到设备1，呼入失败");
        me_whitelist.me_Whitelist.click();
        me_whitelist.whitelistOnly.click();
        me_whitelist.ok.click();
//        2000 call (sps)9999 -> A
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(2000,2000,"99",DEVICE_ASSIST_2,false);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("whitelist",30);
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"进入黑名单列表");
    }
    @Test
    public void W_OnlyWhitelistUnCheck() throws InterruptedException {
        Reporter.infoExec("1100分机页面，取消勾选OnlyWhitelist，分机2000通过sps呼入到设备1，呼入成功");
        me_whitelist.whitelistOnly.click();
//        2000 call (sps)992000 -> A
        ys_me_apply();
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(2000,2000,"99",DEVICE_ASSIST_2,false);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("ANSWER",20);
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true);

    }

    @Test
    public void X_AddSpeedDial() throws InterruptedException {
        Reporter.infoExec("添加快速拨号，分机1000拨打*991");
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        speedDial.speedDial.click();
        ys_waitingMask();

        m_callFeature.addSpeedDial("1",902000);
        ys_apply();
        //2）分机A拨打*991
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*991", DEVICE_IP_LAN);
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();
        ys_waitingTime(5000);
        m_extension.checkCDR("1000 <1000>","902000","Answered");
    }
    @Test
    public void Y_ExportSpeedDial(){
        Reporter.infoExec("导出快速拨号列表");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        speedDial.export.click();
    }
    @Test
    public void Z_DeleteSpeedDial() throws InterruptedException {
        if(Single_Device_Test){
            pageDeskTop.settings.click();
            settings.callFeatures_panel.click();
            callFeatures.more.click();
            speedDial.speedDial.click();
            ys_waitingLoading(speedDial.grid_Mask);
        }
        Reporter.infoExec("删除快速拨号列表第一条，分机1000，拨打*991");
        gridClick(speedDial.grid,1,speedDial.gridDelete);
        speedDial.delete_yes.click();
        ys_apply();
//        2）分机A拨打*991
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*991", DEVICE_IP_LAN);
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();
    }
    @Test
    public void a_ImportSpeedDial(){
        Reporter.infoExec("导入快速拨号列表");
        if(Single_Device_Test){
            pageDeskTop.settings.click();
            settings.callFeatures_panel.click();
            callFeatures.more.click();
            speedDial.speedDial.click();
            ys_waitingLoading(speedDial.grid_Mask);
        }else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
        }
//        speeddial.csv
        speedDial.speedDial.click();
        speedDial.Import.click();
        import_speed_dial_number.browse.click();
        ys_waitingTime(2000);
        importFile(EXPORT_PATH +"speeddial.csv");
        ys_waitingTime(2000);
        import_speed_dial_number.Import.click();
        import_speed_dial_number.ImportOK.click();
        YsAssert.assertEquals(String.valueOf(gridContent(speedDial.grid,1,speedDial.gridcolumn_PhoneNumber)),"902000");
    }
    @Test
    public void b_DeleteAllSpeedDial() throws InterruptedException {
        Reporter.infoExec("批量删除快速拨号列表");
        m_callFeature.addSpeedDial("2",902001);
        gridSeleteAll(speedDial.grid);
        speedDial.delete.click();
        speedDial.delete_yes.click();
        ys_apply();
    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(10000);
        Reporter.infoAfterClass("关闭游览器PagingFunction"); //执行操作
//        tcpSocket.closeTcpSocket();
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(10000);
    }
}
