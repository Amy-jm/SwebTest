package com.yeastar.swebtest.testcase.RegressionCase.pbxcore;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;
/**
 * Created by Yeastar on 2017/10/24.
 */
public class Blacklist_Whitelist extends SwebDriver{
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======  Blacklist_Whitelist  ======="); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.shouldBe(Condition.exist);
        pageDeskTop.CDRandRecording.shouldBe(Condition.exist);
        pageDeskTop.maintenance.shouldBe(Condition.exist);
        if(!PRODUCT.equals(CLOUD_PBX)){
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
        pjsip.Pj_CreateAccount(1000,"Yeastar202","UDP",1);
        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",2);
        pjsip.Pj_CreateAccount(1101,"Yeastar202","UDP",3);
        pjsip.Pj_CreateAccount(1102,"Yeastar202","UDP",4);
        pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",-1);
        pjsip.Pj_CreateAccount(2001,"Yeastar202","UDP",-1);


        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();

//        settings.callFeatures_tree.click();
//        callFeatures.more.click();
//        blacklist_whitelist.blacklist_Whitelist.click();
//        blacklist.blacklist.click();
    }
    @BeforeClass
    public void InitBlackList_WhiteList() {
        settings.callFeatures_tree.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid))) != 0) {
            gridSeleteAll(blacklist.grid);
            blacklist.delete.click();
            blacklist.delete_yes.click();
            ys_apply();
        }

        whitelist.whitelist.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid))) != 0) {
            gridSeleteAll(whitelist.grid);
            whitelist.delete.click();
            whitelist.delete_yes.click();
            ys_apply();
        }

        callFeatures.back.click();
    }

    @Test
    public void A_SetInRoute() {
        Reporter.infoExec("---设置呼入路由---"); //执行操作
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        inboundRoutes.add.shouldBe(Condition.exist);
        ys_waitingLoading(inboundRoutes.grid_Mask);
        gridClick(inboundRoutes.grid,Integer.valueOf(gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder)),inboundRoutes.gridEdit);
        ys_waitingMask();
        setCheckBox(add_inbound_route.enableTimeCondition,false);
        comboboxSelect(add_inbound_route.destinationType,s_extensin);
        comboboxSet(add_inbound_route.destination,extensionList,"1100");
        add_inbound_route.save.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);

    }

    @Test
    public void B_BlackListSPS() {
        Reporter.infoExec("---黑名单Inroute,SPS---"); //执行操作

        settings.callFeatures_tree.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        blacklist.blacklist.click();
        blacklist.add.click();
        m_callFeature.addBlacklist("blackList1",add_blacklist.type_Inbound,2001);
        ys_apply();
        Reporter.infoExec("辅助设备分机2000通过SPS拨打进入黑名单");
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991100",DEVICE_ASSIST_2);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("whitelist");
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"SPS通话进入黑名单列表");

    }

    @Test
    public void C_BlackListSIP1() {
        Reporter.infoExec("---黑名单OutRoute,SIP---"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        blacklist.add.click();
        m_callFeature.addBlacklist("blackList2",add_blacklist.type_Outbound,3001);
        ys_apply();
        Reporter.infoExec("被测设备分机1100通过SIP拨打进入黑名单");
        tcpSocket.connectToDevice();

        pjsip.Pj_Make_Call_Auto_Answer(1100,"13001",DEVICE_IP_LAN);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("blacklist");
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"SIP通话进入黑名单列表");
    }

    @Test
    public void D_WhiteListSPS() {
        Reporter.infoExec("白名单SPS1，Both "); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();

        whitelist.whitelist.click();
        whitelist.add.click();
        m_callFeature.addWhitelist("whiteList1",add_whitelist.type_Both,2001);
        ys_apply();
        Reporter.infoExec("辅助设备分机2000通过SPS拨打");
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991100",DEVICE_ASSIST_2,false);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("ANSWER");//Status: 1 表示分机正在忙
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"SPS白名单呼入");
    }
    @Test
    public void E_WhiteListSIP() {
        Reporter.infoExec("--白名单SIP，OutRoute--"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();

        whitelist.whitelist.click();
        whitelist.add.click();
        m_callFeature.addWhitelist("whiteList2",add_whitelist.type_Outbound,3001);
        ys_apply();
        Reporter.infoExec("被测设备分机1100通过SIP拨打");
        tcpSocket.connectToDevice();

        pjsip.Pj_Make_Call_Auto_Answer(1100,"13001",DEVICE_IP_LAN);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("ANSWER");
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"SIP白名单呼入");
    }

    @Test
    public void F_EditBlackList2() {
        Reporter.infoExec("--编辑blackList2 为Inbound--"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();

        blacklist.blacklist.click();
        blacklist.add.shouldBe(Condition.exist);
        ys_waitingLoading(blacklist.grid_loadMask);
        gridClick(blacklist.grid,gridFindRowByColumn(blacklist.grid,blacklist.gridcolumn_Name,"blackList2",sort_ascendingOrder),blacklist.gridEdit);
        add_blacklist.save.shouldBe(Condition.exist);
        executeJs("Ext.getCmp('"+add_blacklist.type+"').setValue('"+add_blacklist.type_Inbound+"')");
        add_blacklist.save.click();
        ys_waitingMask();
        ys_waitingLoading(blacklist.grid_loadMask);
        String editContent = (String) gridContent(blacklist.grid,gridFindRowByColumn(blacklist.grid,blacklist.gridcolumn_Name,"blackList2",sort_ascendingOrder),blacklist.gridcolumn_Type);
        YsAssert.assertEquals(editContent,"Inbound","blackList2 编辑为Inbound");

        Reporter.infoExec("通过sip1外线呼叫分机1100");
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(1100,"13001",DEVICE_IP_LAN);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("ANSWER");//Status: 1 表示分机正在忙
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"blackList2 编辑为Inbound呼入");
        m_extension.checkCDR("1100 <1100>","13001","Answered","","SIP1",communication_outRoute);

    }
    @Test
    public void G_EditWhiteList1() {
        Reporter.infoExec("--编辑whiteList1 为Outbound--"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();

        whitelist.whitelist.click();
        whitelist.add.shouldBe(Condition.exist);
        ys_waitingLoading(whitelist.grid_loadMask);
        gridClick(whitelist.grid,gridFindRowByColumn(whitelist.grid,whitelist.gridcolumn_Name,"whiteList1",sort_ascendingOrder),whitelist.gridEdit);
        add_whitelist.save.shouldBe(Condition.exist);
        executeJs("Ext.getCmp('"+add_whitelist.type+"').setValue('outbound')");
        add_whitelist.save.click();
        ys_waitingMask();
        ys_waitingLoading(whitelist.grid_loadMask);
        String editContent = (String) gridContent(whitelist.grid,gridFindRowByColumn(whitelist.grid,whitelist.gridcolumn_Name,"whiteList1",sort_ascendingOrder),whitelist.gridcolumn_Type);
        YsAssert.assertEquals(editContent,"Outbound","whiteList1 编辑为Outbound");

        Reporter.infoExec("辅助设备分机2000通过SPS拨打");
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991100",DEVICE_ASSIST_2,false);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("whitelist");//Status: 1 表示分机正在忙
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"白名单呼入失败");

    }
    @Test
    public void H_Export() {
        //导出黑白名单
    }
    @Test
    public void I_DeleteBlackList() {
        Reporter.infoExec("--删除黑名单--");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        blacklist.blacklist.click();
        Reporter.infoExec("删除黑名单blackList1");
        gridClick(blacklist.grid,gridFindRowByColumn(blacklist.grid,blacklist.gridcolumn_Name,"blackList1",sort_ascendingOrder),blacklist.gridDelete);
        blacklist.delete_yes.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        deletes("批量删除黑名单",blacklist.grid,blacklist.delete,blacklist.delete_yes,blacklist.grid_loadMask);
        ys_apply();
    }

    @Test
    public void J_DeleteWhiteList() {
        Reporter.infoExec("--删除白名单--"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        whitelist.whitelist.click();
        Reporter.infoExec("删除黑名单blackList1");
        gridClick(whitelist.grid,gridFindRowByColumn(whitelist.grid,whitelist.gridcolumn_Name,"whiteList1",sort_ascendingOrder),whitelist.gridDelete);
        whitelist.delete_yes.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        deletes("批量删除黑名单",whitelist.grid,whitelist.delete,whitelist.delete_yes,whitelist.grid_loadMask);
        ys_apply();
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：=======   Blacklist_Whitelist  ======="); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);
    }
}
