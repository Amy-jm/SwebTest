package com.yeastar.swebtest.testcase.RegressionCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Yeastar on 2017/10/24.
 */
public class EmergencyNumber extends SwebDriver {
    @BeforeClass
    public void A_Login() {

        Reporter.infoBeforeClass("开始执行：======  EmergencyNumber  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }
    @BeforeClass
    public void C_InitEmergencyNumberTest() {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.emergencyNumber_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
        }else{
            ys_waitingTime(3000);
        }
        deletes("初始化EmergencyNumber：删除所有紧急号码",emergencyNumber.grid,emergencyNumber.delete,emergencyNumber.delete_yes,emergencyNumber.grid_Mask);
    }
    @Test
    public  void A0_Register(){
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1100,EXTENSION_PASSWORD,"UDP",UDP_PORT,2);
        pjsip.Pj_CreateAccount(1101,EXTENSION_PASSWORD,"UDP",UDP_PORT,3);
        pjsip.Pj_CreateAccount(1102,EXTENSION_PASSWORD,"UDP",UDP_PORT,4);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2002,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);

        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2002,DEVICE_ASSIST_2);
        closePbxMonitor();
    }
    @Test
    public void A1_EmergencyNumber() {
        Reporter.infoExec("新增紧急号码：2001，Trunk：sps外线，Notification：分机1100"); //执行操作
        settings.emergencyNumber_tree.click();
        m_emergencyNumber.addEmergencyNumber(2001, SPS,1100);
        ys_waitingTime(5000);
        ys_apply();
    }

    @Test
    public void A2_EmergencyNumber_call() throws InterruptedException {
        Reporter.infoExec("1101拨打32000通过SPS外线呼出，并保持通话");
        pjsip.Pj_Make_Call_Auto_Answer(1101,"32002", DEVICE_IP_LAN);
        ys_waitingTime(10000);
        tcpSocket.connectToDevice();
        Reporter.infoExec("1102拨打紧急号码2001，预期1100会响铃");
        pjsip.Pj_Make_Call_Auto_Answer(1102,"2001", DEVICE_IP_LAN);
        boolean tcpInfo = tcpSocket.getAsteriskInfo("Emergency");
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Answer_Call(1100,true);
        YsAssert.assertEquals(getExtensionStatus(2000,TALKING,20),TALKING,"预期2000为Talking");
        pjsip.Pj_Hangup_All();
//        YsAssert.assertEquals(tcpInfo,true,"进入紧急呼叫");
        m_extension.checkCDR("1102 <1102>","2001","Answered","",SPS,communication_outRoute,1,2);
        m_extension.checkCDR("1102dial2001 <Emergency>","1100 <1100>","Answered",1,2);
        m_extension.checkCDR("1101 <1101>","32002","Answered",3);
    }

    @Test
    public void B1_EditEmergencyNumber_prepend() {
        Reporter.infoExec("编辑紧急号码：trunk prepend为123"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.emergencyNumber_tree.click();
        gridClick(emergencyNumber.grid,1,emergencyNumber.gridEdit);
        add_emergency_number.trunkPrepend.setValue("123");
        add_emergency_number.save.click();
        ys_waitingLoading(emergencyNumber.grid_Mask);
        ys_apply();
    }

    @Test
    public void B2_EditEmergencyNumber_prepend_call() throws InterruptedException {
        Reporter.infoExec("1101拨打32000通过SPS外线呼出，并保持通话");
        pjsip.Pj_Make_Call_Auto_Answer(1101,"32002", DEVICE_IP_LAN);
        ys_waitingTime(10000);
        tcpSocket.connectToDevice();
        Reporter.infoExec("1102拨打紧急号码2001，预期1100会响铃");
        pjsip.Pj_Make_Call_Auto_Answer(1102,"2001", DEVICE_IP_LAN);
        boolean tcpInfo = tcpSocket.getAsteriskInfo("Emergency");
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Answer_Call(1100,true);
        YsAssert.assertEquals(getExtensionStatus(2000,TALKING,20),TALKING,"预期2000为Talking,送出去的号码为");
        pjsip.Pj_Hangup_All();
//        YsAssert.assertEquals(tcpInfo,true,"进入紧急呼叫");
        m_extension.checkCDR("1102 <1102>","2001","Answered","",SPS,communication_outRoute,1,2);
        m_extension.checkCDR("1102dial2001 <Emergency>","1100 <1100>","Answered",1,2);
        m_extension.checkCDR("1101 <1101>","32002","Answered",3);
    }

    @Test
    public void C1_DeleteEmergencyNumber() {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        deletes("删除紧急号码",emergencyNumber.grid,emergencyNumber.delete,emergencyNumber.delete_yes,emergencyNumber.grid_Mask);
        ys_waitingTime(5000);
        ys_apply();
    }

    @Test
    public void C2_Delete_Call() throws InterruptedException {
        //分机直接拨打  拨打失败
        Reporter.infoExec("1102拨打紧急号码2001，预期1100为挂断状态");
        pjsip.Pj_Make_Call_Auto_Answer(1102,"2001", DEVICE_IP_LAN);
        ys_waitingTime(10000);
        YsAssert.assertEquals(getExtensionStatus(1100,HUNGUP,10),HUNGUP,"预期1100为HangUp");
        pjsip.Pj_Hangup_All();
    }


    @Test
    public void D_BlukDelete() {
        Reporter.infoExec("---批量删除紧急号码---"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();

        ys_waitingTime(2000);
        m_emergencyNumber.addEmergencyNumber(1100,SPS,1100);
        m_emergencyNumber.addEmergencyNumber(1102,SIPTrunk,1102);

        gridSeleteAll(emergencyNumber.grid);
        emergencyNumber.delete.click();
        emergencyNumber.delete_yes.click();
        ys_waitingTime(5000);
        ys_apply();
    }

    @AfterMethod
    public void AfterMethod(){
        if(cdRandRecordings.deleteCDR.isDisplayed()){
            closeCDRRecord();
        }
    }

    @AfterClass
    public void AfterClass() {
        ys_waitingTime(5000);
        Reporter.infoAfterClass("执行完毕：======  EmergencyNumber  ======="); //执行操作
        pjsip.Pj_Destory();
        ys_waitingTime(10000);
        killChromePid();    }
}
