package com.yeastar.swebtest.testcase.smokecase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.SwebDriver.*;
import static com.yeastar.swebtest.driver.SwebDriver.executeJs;

/**
 * Created by Yeastar on 2017/7/19.
 */
public class Storage extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {
        if(!PRODUCT.equals(CLOUD_PBX)){
            //        pjsip.Pj_Init();
            Reporter.infoBeforeClass("打开游览器并登录设备_StorageTest"); //执行操作
            initialDriver("chrome","https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
            login(LOGIN_USERNAME,LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.shouldBe(Condition.exist);
            if(!PRODUCT.equals(CLOUD_PBX)){
                mySettings.close.click();
            }
            m_extension.showCDRClounm();
        }
    }
    @BeforeClass
    public void InitStorage() {
        if(!PRODUCT.equals(CLOUD_PBX)){
            pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
            settings.storage_panel.click();
            ys_waitingTime(5000);
            executeJs("Ext.getCmp('st-storage-slotvm').setValue('"+preference.local_CDR +"')");
            int row = gridFindRowByColumn(preference.grid,preference.gridColumn_Name,"sharetest1",sort_descendingOrder);
            if(row != 0){
                gridClick(preference.grid,row,preference.gridUnmountNetDisk);
                preference.Unmount_NetDisk_Yes.click();
                preference.Unmount_NetDisk_OK.click();
                ys_waitingLoading(preference.grid_Mask);
            }
            closeSetting();
        }
    }
    @Test
    public void A_JudgeStorage() throws InterruptedException {
        if(!PRODUCT.equals(PC) && !PRODUCT.equals(CLOUD_PBX)){
            Reporter.infoExec("SD/HD/USB检测");
            pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
            settings.storage_panel.click();
            m_storage.isExistStorage("SD");
            m_storage.isExistStorage("HD");
            m_storage.isExistStorage("USB");
        }
    }

    @Test
    public void B_AddNetworkDrive() throws InterruptedException {
        if(!PRODUCT.equals(CLOUD_PBX)){
            if(Single_Device_Test){
                pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
                settings.storage_panel.click();
            }
            Reporter.infoExec("挂载网络磁盘sharetest1");
            m_storage.AddNetworkDrive(NETWORK_DEVICE_NAME,NETWORK_DEVICE_IP,NETWORK_DEVICE_SHARE_NAME,"","");
        }
    }

    @Test
    public void C_SetFileShare(){
        if(!PRODUCT.equals(CLOUD_PBX)){
            Reporter.infoExec("勾选Enable File Sharing");
            if(Single_Device_Test){
                pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
                settings.storage_panel.click();
            }
            fileShare.fileShare.click();
            executeJs("Ext.getCmp('st-share-enable').setValue('true')");
            fileShare.save.click();
        }
    }
    @Test
    public void D_StorageLocations() throws InterruptedException {
        if(!PRODUCT.equals(CLOUD_PBX)){
            Reporter.infoExec("Storage Locations中CDR、Recordings和Logs下拉选择TF/SD卡,Voicemail&One Touch Recordings下拉选择sharetest1");
            preference.preference.click();
            if(PRODUCT.equals(PC)){
                executeJs("Ext.getCmp('"+preference.CDR+"').setValue('"+preference.local_CDR+"')");
                executeJs("Ext.getCmp('st-storage-slotrecording').setValue('"+preference.local_CDR+"')");
                executeJs("Ext.getCmp('st-storage-slotlog').setValue('"+preference.local_CDR+"')");
                executeJs("Ext.getCmp('st-storage-slotvm').setValue('"+preference.local_CDR+"')");
            }else {
                executeJs("Ext.getCmp('"+preference.CDR+"').setValue('"+preference.sdtf_CDR+"')");
                executeJs("Ext.getCmp('st-storage-slotrecording').setValue('"+preference.sdtf_CDR+"')");
                executeJs("Ext.getCmp('st-storage-slotlog').setValue('"+preference.sdtf_CDR+"')");
                executeJs("Ext.getCmp('st-storage-slotvm').setValue('netdisk-2')");
            }

            preference.save.click();
        }
    }

    @Test
    public void E_Recording_Settings() throws InterruptedException {
        if(!PRODUCT.equals(CLOUD_PBX)){
            Reporter.infoExec("Record Trunks全选所有Trunk、Record Extensions全选所有分机");
            if(Single_Device_Test){
                pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
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
            if(Integer.valueOf(DEVICE_VERSION.split("\\.")[1]) > 5 ){
                executeJs("Ext.getCmp('"+recording.enableInternslCallRecord+"').setValue('true')");
            }
            ys_apply();
        }
    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        if(!PRODUCT.equals(CLOUD_PBX)){
            Thread.sleep(5000);
            Reporter.infoAfterClass("关闭游览器_StorageTest"); //执行操作
//        pjsip.Pj_Destory();
            quitDriver();
            Thread.sleep(10000);
        }
    }
}
