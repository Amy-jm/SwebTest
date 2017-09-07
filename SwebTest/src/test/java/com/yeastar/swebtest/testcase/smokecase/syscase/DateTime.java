package com.yeastar.swebtest.testcase.smokecase.syscase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.Config.mySettings;
import static com.yeastar.swebtest.driver.Config.pageDeskTop;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2017/8/16.
 */
public class DateTime {
    String systemTime = "";
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("打开游览器并登录设备_DateTime"); //执行操作
        initialDriver(CHROME,"http://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.shouldBe(Condition.exist);
        pageDeskTop.CDRandRecording.shouldBe(Condition.exist);
        pageDeskTop.maintenance.shouldBe(Condition.exist);
        mySettings.close.click();
        m_extension.showCDRClounm();
    }
    @Test
    public void A_SetTimeZone() {
        Reporter.infoExec("设置Time Zone为8 China (Beijing)"); //执行操作
        pageDeskTop.settings.click();
        settings.dateTime_panel.click();
        ys_waitingTime(5000);
        executeJs("Ext.getCmp('"+dateTime.timeZone_id+"').setValue('"+dateTime.chinaTime+"')");
        dateTime.save.click();
        ys_apply();

        if(pageDeskTop.reboot_Yes.isDisplayed()){
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        }else if(pageDeskTop.loginout_OK.isDisplayed()){
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(10000);
        }else{
            ys_waitingTime(10000);
        }
        System.out.println(pageDeskTop.reboot_No.isDisplayed());
        login(LOGIN_USERNAME,LOGIN_PASSWORD);

        pageDeskTop.settings.click();
        settings.dateTime_panel.click();
        ys_waitingTime(6000);
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        String currnttime =String.valueOf(dateTime.current_time.getText()) ;
        systemTime = currnttime.substring(0,13);
        System.out.println("当前时间：" + String.valueOf(sdf.format(d))+ "   currnttime:  "+currnttime.substring(0,13));
        YsAssert.assertEquals(currnttime.substring(0,13),String.valueOf(sdf.format(d)));
    }
    @Test
    public void B_CallTest() throws InterruptedException {
        Reporter.infoExec("分机A打给分机B，通话10s"); //执行操作
        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",5060,3);
        pjsip.Pj_CreateAccount(1101,"Yeastar202","UDP",5060,4);
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        pjsip.Pj_Make_Call_Auto_Answer(1100,1101,DEVICE_IP_LAN);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();

        m_extension.checkCDR("1100 <1100>","1101 <1101>","Answered",systemTime,1);
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("关闭游览器"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(10000);
    }
}
