package com.yeastar.swebtest.testcase.Caroline_Practice;

import com.google.common.base.Verify;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Caroline on 2017/12/18.
 */
public class Practice extends SwebDriver {
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======  InboundRoute  ======="); //执行操作
        initialDriver(BROWSER, "https://" + DEVICE_IP_LAN + ":" + DEVICE_PORT + "/");
        login(LOGIN_USERNAME, LOGIN_PASSWORD);
        if (!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")) {
            ys_waitingMask();
            mySettings.close.click();
        }

//        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
//        pjsip.Pj_CreateAccount(1000, "Yeastar202", "UDP", UDP_PORT, 1);
//        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_CreateAccount(1101, "Yeastar202", "UDP", UDP_PORT, 3);
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2000, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

//        Reporter.infoExec(" 辅助设备1注册分机3001"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 3001, "Yeastar202", -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
//        pjsip.Pj_Register_Account_WithoutAssist(3001, DEVICE_ASSIST_1);
//        m_extension.showCDRClounm();
        webDriver.navigate().refresh();
    }

    @Test
    public void test() {
//        pjsip.Pj_Make_Call_Auto_Answer(1000, "13001", DEVICE_IP_LAN);
//        ys_waitingTime(10000);
//        pjsip.Pj_Hangup_All();

        Reporter.infoExec(" 1100拨打32000通过sps外线呼入到分机2000，分机未接"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1101, "32000", DEVICE_IP_LAN, false);
//        pjsip.Pj_Make_Call_Auto_Answer(1101,"32000",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        pjsip.Pj_Answer_Call(1100, 487, false);
//        pjsip.Pj_HangUp_Call(1101);
//
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        ys_waitingTime(5000);
        YsAssert.fail();
        YsAssert.assertEquals("a","|A");
        pjsip.Pj_Hangup_All();
        ys_waitingTime(5000);
        m_extension.checkCDR("1101 <1101>", "32000", "No Answer", "", SPS, communication_outRoute);
        Verify.verify(false);

    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：======  InboundRoute  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);
    }
}
