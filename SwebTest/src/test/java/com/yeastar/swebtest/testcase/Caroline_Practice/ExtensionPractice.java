package com.yeastar.swebtest.testcase.Caroline_Practice;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Caroline on 2017/11/17.
 */
public class ExtensionPractice extends SwebDriver{
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======前置环境设置—BeforeTest======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();

    }

        @Test
        public void O1_setRecord()  {
            if (PRODUCT.equals(CLOUD_PBX)) {
                return;
            }
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
            settings.storage_panel.click();
            preference.preference.click();
            ys_waitingLoading(preference.grid_Mask);
            if (!NETWORK_DEVICE_NAME.equals("null")) {
                int rows = Integer.parseInt(String.valueOf(gridLineNum(preference.grid)));
                int row = gridFindRowByColumn(preference.grid, preference.gridColumn_Name, NETWORK_DEVICE_NAME, sort_ascendingOrder);
                System.out.println("rows:" + rows);
                System.out.println("row:" + row);
                if (row > rows) {
                    Reporter.infoExec(" 添加网络磁盘" + NETWORK_DEVICE_NAME); //执行操作
                    m_storage.AddNetworkDrive(NETWORK_DEVICE_NAME, NETWORK_DEVICE_IP, NETWORK_DEVICE_SHARE_NAME, NETWORK_DEVICE_USER_NAME, NETWORK_DEVICE_USER_PASSWORD);
                }
            }
        }
        @Test
        public void O2_setRecord()  {
            if (PRODUCT.equals(CLOUD_PBX)) {
                return;
            }
            String value = "null";
            if (!DEVICE_RECORD_NAME.equals("null")) {
                Reporter.infoExec(" 设置录音存储在：" + DEVICE_RECORD_NAME);
                if (DEVICE_RECORD_NAME.equals("SD") || DEVICE_RECORD_NAME.equals("TF") || DEVICE_RECORD_NAME.equals("TF/SD")) {
                    value = "tf/sd-1";
                } else if (DEVICE_RECORD_NAME.equals("HD") || DEVICE_RECORD_NAME.equals("hd")) {
                    value = "hd-1";
                } else if (DEVICE_RECORD_NAME.equals("USB") || DEVICE_RECORD_NAME.equals("usb")) {
                    value = "usb-1";
                } else if (DEVICE_RECORD_NAME.equals("Local") || DEVICE_RECORD_NAME.equals("local")) {
                    value = "local-1";
                } else {
                    value = DEVICE_RECORD_NAME;
                }
                comboboxSelect(preference.recordings, value);
                if (preference.storage_yes.isDisplayed()){
                    preference.storage_yes.click();
                }
                preference.save.click();
            }
            preference.recordingSettings.click();
            ys_waitingMask();
            Reporter.infoExec(" 选择全部外线、分机、会议室进行录音");
            ArrayList<String> arrayex = new ArrayList<>();
            arrayex.add("all");
            ArrayList<String> arraytrunk = new ArrayList<>();
            arraytrunk.add("all");
            ArrayList<String> arraycon = new ArrayList<>();
            arraycon.add("all");
            m_storage.selectRecord(arraytrunk, arrayex, arraycon);
            closeSetting();
        }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：======  ExtensionPractice  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);
    }
}
