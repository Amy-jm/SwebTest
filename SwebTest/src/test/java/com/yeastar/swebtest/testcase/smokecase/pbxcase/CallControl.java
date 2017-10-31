package com.yeastar.swebtest.testcase.smokecase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Yeastar on 2017/7/24.
 */
public class CallControl extends SwebDriver {
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("打开游览器并登录设备"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.shouldBe(Condition.exist);
        pageDeskTop.CDRandRecording.shouldBe(Condition.exist);
        pageDeskTop.maintenance.shouldBe(Condition.exist);
        if(!PRODUCT.equals(CLOUD_PBX)){
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
        if(!Single_Device_Test){
            pjsip.Pj_CreateAccount(1000,"Yeastar202","UDP",5060,1);
            pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",5060,-1);
            pjsip.Pj_Register_Account_WithoutAssist(1000, DEVICE_IP_LAN);
            pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        }
    }
    @BeforeClass
    public void InitAutoCLIP(){
        pageDeskTop.settings.click();
        settings.callControl_panel.click();
        autoCLIPRoutes.autoCLIPRoutes.click();
        autoCLIPRoutes.viewAutoCLIPList.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);

        setPageShowNum(autoCLIPRoutes.grid_Page, 100);
        while (Integer.parseInt(String.valueOf(gridLineNum(autoCLIPRoutes.grid))) != 0) {
            gridSeleteAll(autoCLIPRoutes.grid);
            autoCLIPRoutes.delete.click();
            autoCLIPRoutes.delete_yes.click();
            ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        }
        gridCheckDeleteAll(autoCLIPRoutes.grid);
        autoCLIPRoutes.closeAutoClIP_List();
        closeSetting();

    }

    @Test
    public void A_AutoCLIP() throws InterruptedException {
        Reporter.infoExec("编辑inrouter，Destination设置到Conference，meet1");
        pageDeskTop.settings.click();
        settings.callControl_panel.click();
        outboundRoutes.outboundRoutes.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        gridClick(outboundRoutes.grid,1,outboundRoutes.gridEdit);
        ys_waitingMask();
        add_outbound_routes.mt_RemoveAllFromSelected.click();
        listSelect(add_outbound_routes.list_Trunk,trunkList,BRI_1);
        add_outbound_routes.save.click();


        inboundRoutes.inboundRoutes.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        gridClick(inboundRoutes.grid,1,inboundRoutes.gridEdit);
        ys_waitingMask();
//        add_inbound_route.SetTimeConditionTableviewDestination(1,2,add_inbound_route.s_conference);
        add_inbound_route.SetDestination(add_inbound_route.s_conference,"name","meet1");
        Thread.sleep(3000);
        add_inbound_route.save.click();

        autoCLIPRoutes.autoCLIPRoutes.click();
        Thread.sleep(2000);
        listSelectAll(autoCLIPRoutes.list);
        autoCLIPRoutes.save.click();
        ys_waitingMask();
        ys_apply();
    }

    @Test
    public void B_CallBri() throws InterruptedException {
        Reporter.infoExec("设备A 1000 call (bri) B 2000");
//        1000 call (bri) 2000
//        2000接听5s后挂断
        if(Single_Device_Test){
            pjsip.Pj_CreateAccount(1000,"Yeastar202","UDP",5060,1);
            pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",5060,-1);
            pjsip.Pj_Register_Account_WithoutAssist(1000, DEVICE_IP_LAN);
            pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        }
        pjsip.Pj_Make_Call_Auto_Answer(1000,"902000", DEVICE_IP_LAN);
        Thread.sleep(5000);
        pjsip.Pj_Hangup_All();
        if(Single_Device_Test) {
            pageDeskTop.settings.click();
            settings.callControl_panel.click();
        }else{
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
            settings.callControl_tree.click();
        }
        autoCLIPRoutes.autoCLIPRoutes.click();
        autoCLIPRoutes.viewAutoCLIPList.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        String ExtNumber = String.valueOf(gridContent(autoCLIPRoutes.grid,1,autoCLIPRoutes.gridColumn_ExtNumber));
        String CallerNumber = String.valueOf(gridContent(autoCLIPRoutes.grid,1,autoCLIPRoutes.gridColumn_CallerNumber));
        String Trunk = String.valueOf(gridContent(autoCLIPRoutes.grid,1,autoCLIPRoutes.gridColumn_Trunk));
        autoCLIPRoutes.closeAutoClIP_List();
        YsAssert.assertEquals(ExtNumber,"1000");
        YsAssert.assertEquals(CallerNumber,"2000");
        YsAssert.assertEquals(Trunk,BRI_1);
    }
    @Test
    public void C_CallBri() throws InterruptedException {
        Reporter.infoExec("分机2000通过bri线路呼入到设备1的1000");
        if(Single_Device_Test){
            pjsip.Pj_CreateAccount(1000,"Yeastar202","UDP",5060,1);
            pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",5060,-1);
            pjsip.Pj_Register_Account_WithoutAssist(1000, DEVICE_IP_LAN);
            pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        }
        pjsip.Pj_Make_Call_No_Answer(2000,"772000",DEVICE_ASSIST_2);
        pjsip.Pj_Answer_Call(1000,false);
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1000 <1000>" ,"Answered",BRI_1,"",communication_inbound);
    }

    @Test
    public void D_DeleteViewAutoCLIP() throws InterruptedException {
        Reporter.infoExec("删除AutoCLIP记录");
        if(Single_Device_Test){
            pageDeskTop.settings.click();
            settings.callControl_panel.click();
            autoCLIPRoutes.autoCLIPRoutes.click();
        }else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
            settings.callControl_tree.click();
            autoCLIPRoutes.autoCLIPRoutes.click();
        }

        autoCLIPRoutes.viewAutoCLIPList.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        gridSeleteAll(autoCLIPRoutes.grid);

        autoCLIPRoutes.delete.click();
        autoCLIPRoutes.delete_yes.click();

        //检查列表全删除情况
        gridCheckDeleteAll(autoCLIPRoutes.grid);
        autoCLIPRoutes.closeAutoClIP_List();

    }
    @Test
    public void E_CallBri() throws InterruptedException {
//        1000 call (bri) 2000
//        2000接听5s后挂断
//        呼入到会议室meet1。
        Reporter.infoExec("设备A 1000 call (bri) B 2000呼入到会议室meet1");
        if(Single_Device_Test){
            pjsip.Pj_CreateAccount(1000,"Yeastar202","UDP",5060,1);
            pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",5060,-1);
            pjsip.Pj_Register_Account_WithoutAssist(1000, DEVICE_IP_LAN);
            pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        }
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(2000,"772000",DEVICE_ASSIST_2);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("meet1");
        tcpSocket.closeTcpSocket();
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();

        YsAssert.assertEquals(tcpInfo,true,"设备A通过autoClip呼入");
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Reporter.infoAfterClass("关闭游览器"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(10000);
    }
}
