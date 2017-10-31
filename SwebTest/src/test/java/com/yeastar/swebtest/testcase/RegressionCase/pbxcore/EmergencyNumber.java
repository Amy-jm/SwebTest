package com.yeastar.swebtest.testcase.RegressionCase.pbxcore;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.SIP;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.bouncycastle.crypto.engines.CAST5Engine;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.DataReader.*;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2017/10/24.
 */
public class EmergencyNumber extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======  EmergencyNumber  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.shouldBe(Condition.exist);
        pageDeskTop.CDRandRecording.shouldBe(Condition.exist);
        pageDeskTop.maintenance.shouldBe(Condition.exist);
        if(!PRODUCT.equals(CLOUD_PBX)){
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",UDP_PORT,2);
        pjsip.Pj_CreateAccount(1101,"Yeastar202","UDP",UDP_PORT,3);
        pjsip.Pj_CreateAccount(1102,"Yeastar202","UDP",UDP_PORT,4);
        pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2001,"Yeastar202","UDP",UDP_PORT_ASSIST_2,-1);

        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2);

    }

    @BeforeClass
    public void InitEmergencyNumberTest() {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.emergencyNumber_panel.click();
        ys_waitingMask();
        deletes("初始化EmergencyNumber",emergencyNumber.grid,emergencyNumber.delete,emergencyNumber.delete_yes,emergencyNumber.grid_Mask);
    }

    @Test
    public void A_EmergencyNumber() {
        Reporter.infoExec("----新增紧急号码----"); //执行操作
        settings.emergencyNumber_tree.click();
        m_emergencyNumber.addEmergencyNumber(2001, SPS,1100);
        ys_apply();

        Reporter.infoExec("分机B通过SPS外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1101,"32000", DEVICE_IP_LAN);
        ys_waitingTime(10000);

        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(1102,"2001", DEVICE_IP_LAN);
        boolean tcpInfo = tcpSocket.getAsteriskInfo("Emergency");
        tcpSocket.closeTcpSocket();
        ys_waitingTime(10000);
        pjsip.Pj_Answer_Call(1100,true);
        pjsip.Pj_Hangup_All();

        YsAssert.assertEquals(tcpInfo,true,"进入紧急呼叫");

        m_extension.checkCDR("1102 <1102>","2001","Answered",1,2);
        m_extension.checkCDR("1102dial2001 <Emergency>","1100 <1100>","Answered",1,2);
        m_extension.checkCDR("1101 <1101>","32000","Answered",3);

    }

    @Test
    public void B_EditEmergencyNumber() {
        Reporter.infoExec("---编辑紧急号码---"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.emergencyNumber_tree.click();
        gridClick(emergencyNumber.grid,1,emergencyNumber.gridEdit);
        add_emergency_number.trunkPrepend.setValue("123");
        add_emergency_number.save.click();
        ys_waitingLoading(emergencyNumber.grid_Mask);
        ys_apply();

        Reporter.infoExec("分机B通过SPS外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1101,"32000", DEVICE_IP_LAN);
        ys_waitingTime(10000);

        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(1102,"2001", DEVICE_IP_LAN);
        boolean tcpInfo = tcpSocket.getAsteriskInfo("Emergency");
        tcpSocket.closeTcpSocket();
        ys_waitingTime(10000);
        pjsip.Pj_Answer_Call(1100,true);
        pjsip.Pj_Hangup_All();

        YsAssert.assertEquals(tcpInfo,true,"进入紧急呼叫");

    }


    @Test
    public void C_DeleteEmergencyNumber() {
        Reporter.infoExec("---删除紧急号码列表---"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        deletes("删除紧急呼叫",emergencyNumber.grid,emergencyNumber.delete,emergencyNumber.delete_yes,emergencyNumber.grid_Mask);
        ys_apply();
        //分机直接拨打  拨打失败
        pjsip.Pj_Make_Call_Auto_Answer(1100,"2001", DEVICE_IP_LAN);
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

        ys_apply();
    }

    @AfterClass
    public void AfterClass() {
        ys_waitingTime(5000);
        Reporter.infoAfterClass("执行完毕：======  EmergencyNumber  ======="); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
    }
}
