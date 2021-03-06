package com.yeastar.swebtest.testcase.smokecase.syscase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Yeastar on 2017/8/16.
 */
public class Security extends SwebDriver {

    @BeforeClass
    public void BeforeClass() throws InterruptedException {

        Reporter.infoBeforeClass("打开游览器并登录设备_Security"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.shouldBe(Condition.exist);
        mySettings.close.click();
        m_extension.showCDRClounm();
    }
    @Test
    public void B_SetService() {
        Reporter.infoExec("网页访问协议设置HTTP，开启ssh端口22"); //执行操作
        //1）Protocol下拉选择为Http，点击“Save”并点击“Apply”
        //2）勾选SSH，点击“Save”并点击“Apply”
        //3）CRT通过ssh连接到设备
        pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
        settings.security_panel.click();
        service.service.click();
        ys_waitingTime(6666);
        comboboxSelect(service.protocol,service.HTTP);

        if(String.valueOf(return_executeJs("Ext.getCmp('"+service.enableSSH_check +"').getValue()")).equals("false")){
            setCheckBox(service.enableSSH_check,true);
            service.secure_OK.click();
        }
        boolean sshPortChange = false;
        System.out.println("current ssh port "+service.enableSSH.getValue()+" getText "+String.valueOf(return_executeJs("Ext.getCmp('st-service-sshport').getValue()")));
        if(!service.enableSSH.getValue().equals(SSH_PORT)){
            sshPortChange =true;
            service.enableSSH.setValue(SSH_PORT);
        }
        service.save.click();
        if(sshPortChange){
            service.secure_OK.click();
        }
        ys_waitingTime(3000);
//        ys_apply();
    }
    @Test
    public void C_ConnectToSSH() {
        Reporter.infoExec("连接SSH"); //执行操作
        int suc= ssh.CreatConnect(DEVICE_IP_LAN,USERNAME_SUPPORT,PASSWORD_SUPPORT);

        YsAssert.assertEquals(suc,0,"连接SSH");
        if(suc == 0){
            ssh.Close();
        }
    }



    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("关闭游览器"); //执行操作

        quitDriver();
        Thread.sleep(10000);
    }
}
