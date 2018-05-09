package com.yeastar.swebtest.testcase.smokecase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.Config.mySettings;
import static com.yeastar.swebtest.driver.Config.pageDeskTop;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2017/7/25.
 */
public class EmergencyNumberTest extends SwebDriver {
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("打开游览器并登录设备"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.shouldBe(Condition.exist);
        if(!PRODUCT.equals(CLOUD_PBX)){
            mySettings.close.click();
        }
        m_extension.showCDRClounm();

        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",5060,3);
        pjsip.Pj_CreateAccount(1101,"Yeastar202","UDP",5060,4);
        pjsip.Pj_CreateAccount(1102,"Yeastar202","UDP",5060,5);
        pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",5060,-1);
        pjsip.Pj_CreateAccount(2001,"Yeastar202","UDP",5060,-1);

        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2);
    }
//    @BeforeClass
    public void InitEmergencyNumberTest(){
        pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
        settings.emergencyNumber_panel.click();
        deletes("初始化紧急呼叫",emergencyNumber.grid,emergencyNumber.delete,emergencyNumber.delete_yes,emergencyNumber.grid_Mask);
        closeSetting();
    }

    @Test
    public void A_addEmergencyNumber() throws InterruptedException {
        Reporter.infoExec("添加紧急呼叫号码");
        pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        outboundRoutes.outboundRoutes.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        gridClick(outboundRoutes.grid,1,outboundRoutes.gridEdit);
        add_outbound_routes.mt_RemoveAllFromSelected.click();
        listSelect(add_outbound_routes.list_Trunk,trunkList,FXO_1);
        add_outbound_routes.save.click();

        settings.emergencyNumber_tree.click();
        m_emergencyNumber.addEmergencyNumber(2001,FXO_1,1101);
        ys_apply();
    }
    @Test
    public void B_CallEmergencyNumber() throws InterruptedException {
        Reporter.infoExec("分机C通过PSTN线路呼出，通话中。分机1100直接拨打紧急号码2001");
        pjsip.Pj_Make_Call_Auto_Answer(1102,"902000", DEVICE_IP_LAN);
        ys_waitingTime(10000);

        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(1100,"2001", DEVICE_IP_LAN);
        boolean tcpInfo = tcpSocket.getAsteriskInfo("Emergency");
        tcpSocket.closeTcpSocket();
        ys_waitingTime(10000);
        pjsip.Pj_Answer_Call(1101,true);
        pjsip.Pj_Hangup_All();

        YsAssert.assertEquals(tcpInfo,true,"进入紧急呼叫");

        m_extension.checkCDR("1100dial2001 <Emergency>","1101 <1101>","Answered",1,2);
        m_extension.checkCDR("1100 <1100>","2001","Answered",1,2);

        m_extension.checkCDR("1102 <1102>","902000","Answered",3);

    }
    @Test
    public void C_DeleteEmergencyNumber() throws InterruptedException {
        Reporter.infoExec("删除紧急号码");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ys_waitingTime(2000);
        gridClick(emergencyNumber.grid,1,emergencyNumber.gridDelete);
        emergencyNumber.delete_yes.click();
        ys_apply();
        //分机直接拨打  拨打失败
        pjsip.Pj_Make_Call_Auto_Answer(1100,"2001", DEVICE_IP_LAN);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
    }
    @Test
    public void D_DeleteAllEmergencyNumber() throws InterruptedException {
        Reporter.infoExec("批量删除紧急号码");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ys_waitingTime(2000);
        m_emergencyNumber.addEmergencyNumber(1001,FXO_1,1001);
        m_emergencyNumber.addEmergencyNumber(1100,BRI_2,1100);
        m_emergencyNumber.addEmergencyNumber(1101,BRI_1,1101);

        gridSeleteAll(emergencyNumber.grid);
        emergencyNumber.delete.click();
        emergencyNumber.delete_yes.click();

        ys_apply();
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(10000);
        Reporter.infoAfterClass("关闭游览器"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(10000);
    }
}
