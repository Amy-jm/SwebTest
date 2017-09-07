package com.yeastar.swebtest.testcase.smokecase.pbxcase;

import com.codeborne.selenide.Condition;
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
public class FeatureCode {

    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("打开游览器并登录设备"); //执行操作
        initialDriver(CHROME,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.shouldBe(Condition.exist);
        pageDeskTop.CDRandRecording.shouldBe(Condition.exist);
        pageDeskTop.maintenance.shouldBe(Condition.exist);
        mySettings.close.click();
        m_extension.showCDRClounm();
        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",5060,3);
        pjsip.Pj_CreateAccount(1101,"Yeastar202","UDP",5060,4);
        pjsip.Pj_CreateAccount(1102,"Yeastar202","UDP",5060,5);


        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);
    }

    @Test
    public void F_Transfer() throws InterruptedException {
        Reporter.infoExec("Transfer：分机A打给分机B，通过过程中，A拨打*3C");
        pjsip.Pj_Make_Call_Auto_Answer(1100,1101, DEVICE_IP_LAN);
        ys_waitingTime(10000);

        pjsip.Pj_Send_Dtmf(1100,"*","3","1","1","0","2");
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","1102 <1102>","Answered");
        m_extension.checkCDR("1100 <1100>","1101 <1101>","Answered",2);


    }
    @Test
    public void G_Transfer() throws InterruptedException {
        Reporter.infoExec("Transfer：分机A打给分机B，通过过程中，A拨打*03C");
        pjsip.Pj_Make_Call_Auto_Answer(1100,1101, DEVICE_IP_LAN);
        ys_waitingTime(10000);

        pjsip.Pj_Send_Dtmf(1100,"*","0","3","1","1","0","2");
        ys_waitingTime(10000);

        String caller_status = String.valueOf(gridExtensonStatus(extensions.grid_status,5,0));
        YsAssert.assertEquals(caller_status,"Busy");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","1101 <1101>","Answered",2);
        m_extension.checkCDR("1101 <1101>","1102 <1102(from 1100)>","Answered");

    }
    @Test
    public void H_CallPickup() throws InterruptedException {
        Reporter.infoExec("CallPickup：分机A打给C，分机C响铃中，分机B拨打*04C截答");
        pjsip.Pj_Make_Call_No_Answer(1100,1102, DEVICE_IP_LAN);
        ys_waitingTime(8000);
//        pjsip.Pj_Send_Dtmf(1101,"*","0","4","1","1","0","2");
        pjsip.Pj_Make_Call_Auto_Answer(1101,"*041102", DEVICE_IP_LAN);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","1102 <1102>","No Answer",2);
        m_extension.checkCDR("1100 <1100>","1101 <1101(pickup 1102)>","Answered");

    }
    @Test
    public void I_CallPickup() throws InterruptedException {
        Reporter.infoExec("CallPickup：分机C打给分机B，分机B响铃中，分机A拨打*4截答");
        pjsip.Pj_Make_Call_No_Answer(1102,1101, DEVICE_IP_LAN);
        ys_waitingTime(8000);
//        pjsip.Pj_Send_Dtmf(1100,"*","4");
        pjsip.Pj_Make_Call_Auto_Answer(1100,"*4", DEVICE_IP_LAN);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1102 <1102>","1101 <1101>","No Answer",2);
        m_extension.checkCDR("1102 <1102>","1100 <1100(pickup 1101)>","Answered");

    }
    @Test
    public void J_CallParking() throws InterruptedException {
        Reporter.infoExec("CallParking：分机A打给分机B，通话过程中，A拨打*6，15s后，分机A拨打6900");
        pjsip.Pj_Make_Call_Auto_Answer(1100,1101, DEVICE_IP_LAN);
        ys_waitingTime(1000);
        pjsip.Pj_Send_Dtmf(1100,"*","6");
        ys_waitingTime(15000);
        pjsip.Pj_Make_Call_Auto_Answer(1100,"6900", DEVICE_IP_LAN);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1101 <1101>","1100 <1100(from 6900)>","Answered");
        m_extension.checkCDR("1101 <1101>","6900(from 1100)","Answered",2);
        m_extension.checkCDR("1100 <1100>","1101 <1101>","Answered",3);
    }
    @Test
    public void K_CallParking() throws InterruptedException {
        Reporter.infoExec("CallParking：分机A打给分机B，通话过程中，分机A拨打*066950，15s后，分机A拨打6950");
        pjsip.Pj_Make_Call_Auto_Answer(1100,1101, DEVICE_IP_LAN);
        ys_waitingTime(1000);
        pjsip.Pj_Send_Dtmf(1100,"*","0","6","6","9","5","0");
        ys_waitingTime(20000);
        pjsip.Pj_Make_Call_Auto_Answer(1100,"6950", DEVICE_IP_LAN);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1101 <1101>","1100 <1100(from 6950)>","Answered",1);
        m_extension.checkCDR("1101 <1101>","6950(from 1100)","Answered",2);
        m_extension.checkCDR("1100 <1100>","1101 <1101>","Answered",3);
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
