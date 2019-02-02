package com.yeastar.swebtest.testcase.RegressionCase.pbxcase;

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
    public void A_Login() {

        Reporter.infoBeforeClass("开始执行：======  Blacklist_Whitelist  ======="); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }

    @BeforeClass
    public void C_InitBlackList_WhiteList() {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
        }else{
            ys_waitingTime(5000);
        }
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        blacklist.add.shouldBe(Condition.exist);
        Reporter.infoExec("初始化黑名单，删除所有黑名单"); //执行操作
        if(Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid))) != 0) {
            gridSeleteAll(blacklist.grid);
            blacklist.delete.click();
            blacklist.delete_yes.click();
            ys_apply();
        }

        Reporter.infoExec("初始化白名单，删除所有白名单"); //执行操作
        whitelist.whitelist.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        whitelist.add.shouldBe(Condition.exist);
        if(Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid))) != 0) {
            gridSeleteAll(whitelist.grid);
            whitelist.delete.click();
            whitelist.delete_yes.click();
            ys_apply();
        }
        callFeatures.back.click();
    }
    @Test
    public void A0_Register() {
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(1100,EXTENSION_PASSWORD,"UDP",UDP_PORT,2);
        pjsip.Pj_CreateAccount(1101,EXTENSION_PASSWORD,"UDP",UDP_PORT,3);
        pjsip.Pj_CreateAccount(1102,EXTENSION_PASSWORD,"UDP",UDP_PORT,4);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(3001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN,UDP_PORT);
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN,UDP_PORT);
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN,UDP_PORT);
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN,UDP_PORT);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2,UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2,UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1,UDP_PORT_ASSIST_1);
        closePbxMonitor();
    }
    @Test
    public void A1_EditInRoute1() {
        Reporter.infoExec("编辑呼入路由InRoute1，呼入目的地：分机1100"); //执行操作
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        inboundRoutes.add.shouldBe(Condition.exist);
        ys_waitingLoading(inboundRoutes.grid_Mask);
        gridClick(inboundRoutes.grid,Integer.valueOf(gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder)),inboundRoutes.gridEdit);
        ys_waitingMask();
        setCheckBox(add_inbound_route.enableTimeCondition,false);
        comboboxSelect(add_inbound_route.destinationType,s_extensin);
        comboboxSet(add_inbound_route.destination,extensionList,"1100");
        ys_waitingTime(2000);
        add_inbound_route.save.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
    }

    @Test
    public void B1_blackList1_In_2001_add() {
        Reporter.infoExec("添加黑名单blackList1，Type：Inbound，Num：2001"); //执行操作
        settings.callFeatures_tree.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        blacklist.blacklist.click();
        blacklist.add.click();
        m_callFeature.addBlacklist("blackList1",add_blacklist.type_Inbound,2001);
        ys_apply();
        ys_waitingTime(5000);
    }

    @Test
    public void B2_blackList1_In_2001_call() throws InterruptedException {
        Reporter.infoExec("2001拨打991100通过sps外线呼入，预期呼入失败");
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991100",DEVICE_ASSIST_2);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("whitelist-inbound");
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(getExtensionStatus(2001,HUNGUP,20),HUNGUP,"预期2001为HangUp");
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"SPS通话进入黑名单列表");
    }

    @Test
    public void C1_blackList2_Out_3001_add() {
        Reporter.infoExec("添加黑名单blackList2，Type：Outbound，Num：3001"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        blacklist.add.shouldBe(Condition.exist).click();
        m_callFeature.addBlacklist("blackList2",add_blacklist.type_Outbound,3001);
        ys_apply();
        ys_waitingTime(5000);
    }

    @Test
    public void C2_blackList2_Out_3001_call() throws InterruptedException {
        Reporter.infoExec("1100拨打13001通过sip外线呼出，预期呼出失败");
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(1100,"13001",DEVICE_IP_LAN);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("blacklist");
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"SIP通话进入黑名单列表");
    }

    @Test
    public void D1_whiteList1_Both_2001_add() {
        Reporter.infoExec("添加白名单whiteList1，Type：Both，Num：2001 "); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        whitelist.whitelist.click();
        whitelist.add.click();
        m_callFeature.addWhitelist("whiteList1",add_whitelist.type_Both,2001);
        ys_apply();
        ys_waitingTime(5000);
    }

    @Test
    public void D2_whiteList1_Both_2001_call() throws InterruptedException {
        Reporter.infoExec("2001拨打991100通过sps外线呼入，预期呼入成功，1100响铃并接听");
//        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991100",DEVICE_ASSIST_2,false);
//        boolean tcpInfo= tcpSocket.getAsteriskInfo("ANSWER");//Status: 1 表示分机正在忙
//        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(getExtensionStatus(1100,TALKING,10),TALKING,"预期1100正在Talking");
        pjsip.Pj_Hangup_All();
//        YsAssert.assertEquals(tcpInfo,true,"SPS白名单呼入");
    }

    @Test
    public void E1_whiteList2_Out_3001_add() {
        Reporter.infoExec("添加白名单whiteList2，Type：Outbound，Num：3001"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        whitelist.whitelist.click();
        whitelist.add.click();
        m_callFeature.addWhitelist("whiteList2",add_whitelist.type_Outbound,3001);
        ys_apply();
        ys_waitingTime(5000);
    }

    @Test
    public void E2_whiteList2_Out_3001_call() throws InterruptedException {
        Reporter.infoExec("1100拨打13001通过sip外线呼出，预期呼出成功，3001响铃并接听");
//        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(1100,"13001",DEVICE_IP_LAN);
//        boolean tcpInfo= tcpSocket.getAsteriskInfo("ANSWER");
//        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(getExtensionStatus(3001,TALKING,10),TALKING,"预期3001正在Talking");
        pjsip.Pj_Hangup_All();
//        YsAssert.assertEquals(tcpInfo,true,"SIP白名单呼入");
    }

    @Test
    public void F1_blackList2_In_3001_edit() {
        Reporter.infoExec("编辑blackList2，Type：Inbound"); //执行操作
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
        ys_apply();
        ys_waitingTime(5000);
    }

    @Test
    public void F2_blackList2_In_3001_callout() throws InterruptedException {
        Reporter.infoExec("1100拨打13001通过sip外线呼出，预期呼出成功，3001响铃并接听");
//        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(1100,"13001",DEVICE_IP_LAN);
//        boolean tcpInfo= tcpSocket.getAsteriskInfo("ANSWER");//Status: 1 表示分机正在忙
//        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(getExtensionStatus(3001,TALKING,10),TALKING,"预期3001正在Talking");
        pjsip.Pj_Hangup_All();
//        YsAssert.assertEquals(tcpInfo,true,"blackList2 编辑为Inbound呼入");
        m_extension.checkCDR("1100 <1100>","13001","Answered","","SIP1",communication_outRoute);

    }

    @Test
    public void F3_blackList2_In_3001_callin() throws InterruptedException {
        Reporter.infoExec("3001拨打3000通过sip外线呼入，预期呼入失败");
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("whitelist-inbound");//Status: 1 表示分机正在忙
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(getExtensionStatus(3001,HUNGUP,20),HUNGUP,"预期3001为HangUp");
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"blackList2 编辑为Inbound呼入");
    }

    @Test
    public void G1_whiteList1_Out_2001_edit() {
        Reporter.infoExec("编辑whiteList1，Type：Outbound"); //执行操作
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
        ys_apply();
        ys_waitingTime(5000);
    }

    @Test
    public void G2_whiteList1_Out_2001_callin() throws InterruptedException {
        Reporter.infoExec("2001拨打991100通过sps外线呼入，预期呼入失败");
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991100",DEVICE_ASSIST_2,false);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("whitelist-inbound");//Status: 1 表示分机正在忙
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(getExtensionStatus(2001,HUNGUP,20),HUNGUP,"预期2001为HangUp");
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"whiteList1 编辑为Outbound，呼入失败");
    }

    @Test
    public void G3_whiteList1_Out_2001_callout() throws InterruptedException {
        Reporter.infoExec("1100拨打32001通过sps外线呼出，预期呼出成功");
//        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32001",DEVICE_IP_LAN,false);
//        boolean tcpInfo= tcpSocket.getAsteriskInfo("ANSWER");//Status: 1 表示分机正在忙
//        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(getExtensionStatus(2000,TALKING,10),TALKING,"预期2000正在Talking");
        pjsip.Pj_Hangup_All();
//        YsAssert.assertEquals(tcpInfo,true,"whiteList1 编辑为Outbound，呼出成功");
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

    @Test
    public void K_Recovery() throws InterruptedException {
        Reporter.infoExec(" 恢复呼入路由InRoute1到分机1000");
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        inboundRoutes.add.shouldBe(Condition.exist);
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_inbound_route.destinationType,s_extensin);
        comboboxSet(add_inbound_route.destination,extensionList,"1000");
        add_inbound_route.save.click();
        ys_waitingTime(5000);
        ys_apply();
    }

    @AfterMethod
    public void AfterMethod(){
        pjsip.Pj_Hangup_All();
        if(cdRandRecordings.deleteCDR.isDisplayed()){
            closeCDRRecord();
        }
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：=======   Blacklist_Whitelist  ======="); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        ys_waitingTime(10000);
        killChromePid();    }
}
