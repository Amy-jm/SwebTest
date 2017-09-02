package com.yeastar.swebtest.testcase.smokecase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.tools.reporter.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.SwebDriver.*;
import static com.yeastar.swebtest.driver.SwebDriver.executeJs;

/**
 * Created by Yeastar on 2017/7/19.
 */
public class Storage {
    @BeforeClass
    public void BeforeClass() {
//        pjsip.Pj_Init();
        Reporter.infoBeforeClass("打开游览器并登录设备_StorageTest"); //执行操作
        initialDriver("chrome","https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.shouldBe(Condition.exist);
        pageDeskTop.CDRandRecording.shouldBe(Condition.exist);
        pageDeskTop.maintenance.shouldBe(Condition.exist);
        mySettings.close.click();
    }
    @Test
    public void CaseName() {
        Reporter.infoExec("执行的操作"); //执行操作

    }
    @Test
    public void A_JudgeStorage() throws InterruptedException {
        Reporter.infoExec("SD/HD/USB检测");
        pageDeskTop.settings.click();
        settings.storage_panel.click();
        m_storage.isExistStorage("SD");
        m_storage.isExistStorage("HD");
        m_storage.isExistStorage("USB");
    }

    @Test
    public void B_AddNetworkDrive() throws InterruptedException {
        Reporter.infoExec("挂载网络磁盘sharetest1");

        m_storage.AddNetworkDrive(NETWORK_DEVICE_NAME,NETWORK_DEVICE_IP,NETWORK_DEVICE_SHARE_NAME,"","");
    }

    @Test
    public void C_SetFileShare(){
        Reporter.infoExec("勾选Enable File Sharing");
        fileShare.fileShare.click();
        fileShare.enableFileSharing.click();
        fileShare.save.click();
    }
    @Test
    public void D_StorageLocations() throws InterruptedException {
        Reporter.infoExec("Storage Locations中CDR、Recordings和Logs下拉选择TF/SD卡,Voicemail&One Touch Recordings下拉选择sharetest1");
        preference.preference.click();
        executeJs("Ext.getCmp('st-storage-slotcdr').setValue('"+preference.sdtf_CDR+"')");
        executeJs("Ext.getCmp('st-storage-slotrecording').setValue('"+preference.sdtf_CDR+"')");
        executeJs("Ext.getCmp('st-storage-slotlog').setValue('"+preference.sdtf_CDR+"')");
        executeJs("Ext.getCmp('st-storage-slotvm').setValue('netdisk-2')");
        preference.save.click();

    }

    @Test
    public void E_Recording_Settings() throws InterruptedException {
        Reporter.infoExec("Record Trunks全选所有Trunk、Record Extensions全选所有分机");
        if(Single_Device_Test){
            pageDeskTop.settings.click();
            settings.storage_panel.click();
        }

        Thread.sleep(1000);
        preference.recordingSettings.click();
        ys_waitingTime(10000);
        recording.rt_AddAllToSelect.click();
        Thread.sleep(1000);
        recording.re_AddAllToSelect.click();
        Thread.sleep(1000);
        recording.save.click();
        Thread.sleep(1000);
        ys_apply();
    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(10000);
        Reporter.infoAfterClass("关闭游览器_StorageTest"); //执行操作
//        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(10000);
    }
}
