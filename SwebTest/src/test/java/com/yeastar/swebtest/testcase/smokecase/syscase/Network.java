package com.yeastar.swebtest.testcase.smokecase.syscase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2017/8/16.
 */
public class Network extends SwebDriver {
    @BeforeClass
    public void BeforeClass() throws InterruptedException {

        Reporter.infoBeforeClass("打开游览器并登录设备_Network"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
//        initialDriver(BROWSER,"https://"+"192.168.7.151"+":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.shouldBe(Condition.exist);
        mySettings.close.click();
        m_extension.showCDRClounm();
    }

    @Test
    public void A_DualSetting() throws InterruptedException {
        //1）修改Mode为Dual，WAN口配置Static IP Address，IP Address
        // 配置为192.168.yy.yy，Subnet Mask为255.255.255.0，Gateway为192.168.yy.1，
        // DNS为192.168.1.1，点击“Save”并点击“Apply”
        Reporter.infoExec("修改网络设置"); //执行操作
        pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
        settings.network_panel.click();
        ys_waitingTime(5000);
        boolean reboot = false;
        System.out.println("sssssssss " +basicSettings.mode_input.getValue());
        if(!basicSettings.mode_input.getValue().equals("Dual")){
            executeJs("Ext.getCmp('"+basicSettings.mode+"').setValue('"+basicSettings.mode_Dual+"')");
            reboot = true;
        }


        System.out.println("Ext.get("+basicSettings.w_staticIPAddress_id+").dom.click()");
        executeJs("Ext.get("+basicSettings.l_staticIPAddress_id+").dom.click()");
        executeJs("Ext.get("+basicSettings.w_staticIPAddress_id+").dom.click()");


        System.out.println("network info :"+basicSettings.w_IPAddress.getValue()+basicSettings.w_subnetMask.getValue()+basicSettings.w_gateway.getValue() );
        if(!basicSettings.w_IPAddress.getValue().equals(DEVICE_IP_WAN)){
            reboot = true;
            basicSettings.w_IPAddress.setValue(DEVICE_IP_WAN);
        }
        if(!basicSettings.w_subnetMask.getValue().equals(DEVICE_IP_SUBNETMASK)){
            reboot = true;
            basicSettings.w_subnetMask.setValue(DEVICE_IP_SUBNETMASK);
        }
        if(!basicSettings.w_gateway.getValue().equals(DEVICE_IP_GATEWAY)){
            reboot = true;
            basicSettings.w_gateway.setValue(DEVICE_IP_GATEWAY);
        }
        if(!basicSettings.w_preferredDNSServer.getValue().equals(DEVICE_IP_DNS)){
            reboot = true;
            basicSettings.w_preferredDNSServer.setValue(DEVICE_IP_DNS);
        }
        basicSettings.save.click();
        if(reboot){
            System.out.println("ready to reboot");
            pageDeskTop.reboot_Yes.click();
            waitReboot();
        }
    }
    @Test
    public void B_DualSetting() {
        Reporter.infoExec("从wan口IP登录"); //执行操作
        quitDriver();
        ys_waitingTime(5000);
        initialDriver(BROWSER,"https://"+ DEVICE_IP_WAN +":"+DEVICE_PORT+"/");
        pageLogin.username.shouldBe(Condition.exist);
        pageLogin.username.is(Condition.exist);
        System.out.println(webDriver.getCurrentUrl());
//        YsAssert.assertEquals();
        if(!webDriver.getCurrentUrl().contains(DEVICE_IP_WAN)){
            YsAssert.assertEquals(String.valueOf(webDriver.getCurrentUrl()),DEVICE_IP_WAN);
        }
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!webDriver.getCurrentUrl().contains(DEVICE_IP_LAN)){
            YsAssert.assertEquals(String.valueOf(webDriver.getCurrentUrl()),DEVICE_IP_LAN);
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
