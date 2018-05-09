package com.yeastar.swebtest.testcase.smokecase.syscase;

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
 * Created by Yeastar on 2017/8/16.
 */
public class EventCenter extends SwebDriver {
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("打开游览器并登录设备_EventCenter"); //执行操作
        initialDriver(BROWSER,"http://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.shouldBe(Condition.exist);
        mySettings.close.click();
        m_extension.showCDRClounm();
        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",3);
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
    }
    @Test
    public void A_AddContact() {
        Reporter.infoExec("Add Contact"); //执行操作
        pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
        settings.eventSettings_panel.click();
        notificationContacts.notificationContacts.click();
        notificationContacts.add.click();
        ys_waitingTime(3000);
        comboboxSet(add_contact.chooseContact_id,extensionList,"1100");
        setCheckBox(add_contact.callExtension,true);
        add_contact.save.click();
        ys_apply();

        String name= String.valueOf(gridContent(notificationContacts.grid,1,notificationContacts.gridColumn_Name));
        String notification = String.valueOf(gridContent(notificationContacts.grid,1,notificationContacts.gridColumn_NotificationMethod));
        YsAssert.assertEquals(name,"1100 - 1100","事件通知分机号");
        YsAssert.assertEquals(notification,"Call Extension","事件通知方式");
    }
    @Test
    public void B_EventSettings() {
        /**
         * 1）Event Settings的User Login Success的Notification开启
         2）登陆网页
         */
//        System.out.println("Ext.ComponentQuery.query('[xType=checkrow]')["+eventSetting.Record_UserLoginSuccess+"].down('[name="+eventSetting.EventSetting_Record+"]').setValue(true)");

        Reporter.infoExec("执行的操作"); //执行操作
        eventSetting.eventSetting.click();
        ys_waitingTime(5000);
        executeJs("Ext.ComponentQuery.query('[xType=checkrow]')["+eventSetting.Record_UserLoginSuccess+"].down('[name="+eventSetting.EventSetting_Noticication+"]').setValue(true)");

    }
    @Test
    public void C_Answer() {
        Reporter.infoExec("分机1100响铃接听"); //执行操作
        logout();
        tcpSocket.connectToDevice(0);
        login("1100","Yeastar202");
        ys_waitingTime(5000);
        pjsip.Pj_Answer_Call(1100,false);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("ANSWERED");
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(tcpInfo,true,"分机1100接听");
    }
    @Test
    public void D_CheckEventLog() {
        Reporter.infoExec("查看Event log"); //执行操作
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
        settings.eventLog_panel.click();
        ys_waitingTime(5000);
        String log = String.valueOf(gridContent(eventLog.grid,2,eventLog.gridColumn_EventName));
        String name = String.valueOf(gridContent(eventLog.grid,2,eventLog.gridColumn_EventMessage));
        YsAssert.assertInclude(log,"Login Success","EventName查看");
        YsAssert.assertInclude(name,"1100","EventMessage 查看");
    }
    @Test
    public void E_DeleteNotification() {
        Reporter.infoExec("删除Notification列表"); //执行操作
        settings.eventSettings_tree.click();
        notificationContacts.notificationContacts.click();
        gridSeleteAll(notificationContacts.grid);
        notificationContacts.delete.click();
        notificationContacts.delete_yes.click();
        gridCheckDeleteAll(notificationContacts.grid);

    }
    @Test
    public void F_CheckLog() {
        Reporter.infoExec("删除Notification分机1100不响铃"); //执行操作
        logout();
        login("1100","Yeastar202");
        ys_waitingTime(5000);
        pjsip.Pj_Answer_Call(1100,false);
        boolean tcpInfo= tcpSocket.getAsteriskInfo("ANSWERED");
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(tcpInfo,false,"分机1100未接听");

    }
    @Test
    public void G_CheckLog() {
        Reporter.infoExec("删除Notification后查看Event log"); //执行操作
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
        settings.eventLog_panel.click();
        ys_waitingTime(5000);
        String log = String.valueOf(gridContent(eventLog.grid,1,eventLog.gridColumn_EventName));
        String name = String.valueOf(gridContent(eventLog.grid,1,eventLog.gridColumn_EventMessage));
        YsAssert.assertInclude(log,"Login Success","EventName查看");
        YsAssert.assertInclude(name,"admin","EventMessage 查看");
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
