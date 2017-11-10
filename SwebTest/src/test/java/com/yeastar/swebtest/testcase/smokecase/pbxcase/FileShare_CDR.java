package com.yeastar.swebtest.testcase.smokecase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Yeastar on 2017/7/21.
 */
public class FileShare_CDR extends SwebDriver {
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("打开游览器并登录设备FileShare_CDR"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",5060,3);
        pjsip.Pj_CreateAccount(1101,"Yeastar202","UDP",5060,4);

        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);
    }
    @BeforeClass
    public void InitFileShare(){

    }
    @Test
    public void A_FileShare(){

    }
    @Test
    public void B_DelCdr() throws InterruptedException {
        if(!PRODUCT.equals(CLOUD_PBX)){
            Reporter.infoExec("删除一行CDR");
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.CDRandRecordShortcut.click();
//            pageDeskTop.CDRandRecording.click();
            ys_waitingLoading(cdRandRecordings.grid_Mask);
            ys_waitingTime(2000);
            gridClick(cdRandRecordings.grid,1,cdRandRecordings.gridDeleteCDR);
            cdRandRecordings.delete_yes.click();
        }
    }
    @Test
    public void C_DownloadCDR(){
        if(!PRODUCT.equals(CLOUD_PBX)){
            Reporter.infoExec("下载所有CDR");
            cdRandRecordings.downloadCDR.click();
        }

    }
    @Test
    public void D_Recording(){
        if(!PRODUCT.equals(CLOUD_PBX)){
            Reporter.infoExec("下载Recording");
            cdRandRecordings.downloadRecordings.click();
        }

    }
    @Test
    public void E_RecordingPlay(){
        if(!PRODUCT.equals(CLOUD_PBX)){
            if(Single_Device_Test){
                pageDeskTop.CDRandRecording.click();
            }
            ys_waitingTime(5000);
            int useRow = 2;

            String callFrom = String.valueOf(gridContent(cdRandRecordings.grid,useRow,cdRandRecordings.gridColumn_CallFrom));
            String callTo = String.valueOf(gridContent(cdRandRecordings.grid,useRow,cdRandRecordings.gridColumn_CallTo));
            gridClick(cdRandRecordings.grid,useRow,cdRandRecordings.gridPlay);
            ys_waitingTime(5000);
            System.out.println(cdRandRecordings.recordingInfo_CallFrom.getText());
            System.out.println(cdRandRecordings.recordingInfo_CallTo.getText());
            String recordingInfo_CallFrom = cdRandRecordings.recordingInfo_CallFrom.getText();
            String recordingInfo_CallTo = cdRandRecordings.recordingInfo_CallTo.getText();
            comboboxSet(cdRandRecordings.playToExtension_id,extensionList,"1100");
            ys_waitingTime(3000);
            tcpSocket.connectToDevice();
            cdRandRecordings.recordingInfo_Play.click();
            ys_waitingTime(10000);
            pjsip.Pj_Answer_Call(1100,false);

            boolean tcpInfo = tcpSocket.getAsteriskInfo("/mmc1/autorecords");
            tcpSocket.closeTcpSocket();
//        play_file/mmc1/autorecords
            pjsip.Pj_Hangup_All();
            YsAssert.assertEquals(recordingInfo_CallFrom,callFrom,"PlayRecord CallFrom");
            YsAssert.assertEquals(recordingInfo_CallTo,callTo,"PlayRecord CallTo");
            YsAssert.assertEquals(tcpInfo,true,"从分机A 1100播放录音");
        }
    }
    @Test
    public void F_Downloader() throws InterruptedException {
        if(!PRODUCT.equals(CLOUD_PBX)){
            if(Single_Device_Test){
                pageDeskTop.CDRandRecording.click();
                ys_waitingLoading(cdRandRecordings.grid_Mask);
            }
            int row=1;
            String lineNume = String.valueOf(gridLineNum(cdRandRecordings.grid));
            for (int i=1; i<=Integer.parseInt(lineNume); i++){
                if(!gridPicColor(cdRandRecordings.grid,i,cdRandRecordings.gridDeleteRecord).contains(cdRandRecordings.gridColumnColor_Gray)){
                    System.out.println("find  row = "+row );
                    row = i;
                    break;
                }
            }
            gridClick(cdRandRecordings.grid,row,cdRandRecordings.gridDownload);
            gridClick(cdRandRecordings.grid,row,cdRandRecordings.gridDeleteRecord);
            cdRandRecordings.delete_yes.click();
            ys_waitingLoading(cdRandRecordings.grid_Mask);
            ys_waitingTime(5000);
            String actColorDown= gridPicColor(cdRandRecordings.grid,row,cdRandRecordings.gridDownload);
            String actColorPlay= gridPicColor(cdRandRecordings.grid,row,cdRandRecordings.gridPlay);
            String actColorRecord= gridPicColor(cdRandRecordings.grid,row,cdRandRecordings.gridDeleteRecord);

            if(actColorDown.contains(cdRandRecordings.gridColumnColor_Gray)){
            }else{
                YsAssert.assertEquals(actColorDown,cdRandRecordings.gridColumnColor_Gray,"DownLoad图标颜色没有变为灰色");
            }
            if(actColorPlay.contains(cdRandRecordings.gridColumnColor_Gray)){
            }else{
                YsAssert.assertEquals(actColorDown,cdRandRecordings.gridColumnColor_Gray,"Play图标颜色没有变为灰色");
            }
            if(actColorRecord.contains(cdRandRecordings.gridColumnColor_Gray)){
            }else{
                YsAssert.assertEquals(actColorDown,cdRandRecordings.gridColumnColor_Gray,"DeleteRecord图标颜色没有变为灰色");
            }
        }

    }
    @Test
    public void G_DeleteallCDR(){
        if(!PRODUCT.equals(CLOUD_PBX)){
            Reporter.infoExec("点击“Delete”，弹出删除的提示，点击“yes”");
            cdRandRecordings.deleteCDR.click();
            cdRandRecordings.delete_yes.click();
        }
    }
    @Test
    public void H_PromprPreference() throws InterruptedException {
        Reporter.infoExec("点击CDR中Recording Options中为蓝色的Downloader图标");

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
//        pageDeskTop.taskBar_Main.click();
        settings.voicePrompts_panel.click();
        ys_waitingTime(5000);
        executeJs("Ext.getCmp('st-pp-monitorstart').setValue('[Default]')");
        ys_waitingTime(5000);
        promptPreference.save.click();
        ys_waitingTime(1000);
        ys_apply();
        //分机A打给B，通话中，A按*1
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(1100,"1101", DEVICE_IP_LAN);
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(1100,"*","1");
        boolean tcpInfo = tcpSocket.getAsteriskInfo("MIXMONITOR_BEGIN");
        tcpSocket.closeTcpSocket();
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();

        YsAssert.assertEquals(tcpInfo,true,"分机B:1101进入录音");
        m_extension.checkCDR("1100 <1100>","1101 <1101>","Answered");
//        mySettings.close.click();
//        pageDeskTop.boxSettings.shouldBe(Condition.disappear);
    }
    @Test
    public void I_Me_CDRRecording() throws InterruptedException {
        Reporter.infoExec("登录1000分机网页查看最新的CDR");
        logout();
        login("1100","Yeastar202");
        me.me.click();
        me.me_CDRandRecording.click();
        ys_waitingLoading(me_cdRandRecording.grid_Mask);
        Thread.sleep(5000);
        String callfrom = (String) gridContent(me_cdRandRecording.grid,1,me_cdRandRecording.gridColumn_CallFrom);
        String callto = (String) gridContent(me_cdRandRecording.grid,1,me_cdRandRecording.gridColumn_CallTo);

        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);

        YsAssert.assertEquals(callfrom.substring(0,4).trim(),String.valueOf(1100),"CDR呼叫方");
        YsAssert.assertEquals(callto.substring(0,4).trim(),String.valueOf(1101),"CDR被叫方");
    }
    @Test
    public void J_DownloaderPrompt() throws InterruptedException {
        Reporter.infoExec("返回admin用户下载Recording Options");
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.CDRandRecording.click();

        gridClick(cdRandRecordings.grid,1,cdRandRecordings.gridDownload);

        gridClick(cdRandRecordings.grid,1,cdRandRecordings.gridDeleteRecord);
        cdRandRecordings.delete_yes.click();
        //断言图标变为灰色
        ys_waitingTime(5000);
        String actColorDown= gridPicColor(cdRandRecordings.grid,1,cdRandRecordings.gridDownload);
        String actColorPlay= gridPicColor(cdRandRecordings.grid,1,cdRandRecordings.gridPlay);
        String actColorRecord= gridPicColor(cdRandRecordings.grid,1,cdRandRecordings.gridDeleteRecord);

        if(actColorDown.contains(cdRandRecordings.gridColumnColor_Gray)){
        }else{
            YsAssert.assertEquals(actColorDown,cdRandRecordings.gridColumnColor_Gray,"DownLoad图标颜色没有变为灰色");
        }
        if(actColorPlay.contains(cdRandRecordings.gridColumnColor_Gray)){
        }else{
            YsAssert.assertEquals(actColorDown,cdRandRecordings.gridColumnColor_Gray,"Play图标颜色没有变为灰色");
        }
        if(actColorRecord.contains(cdRandRecordings.gridColumnColor_Gray)){
        }else{
            YsAssert.assertEquals(actColorDown,cdRandRecordings.gridColumnColor_Gray,"DeleteRecord图标颜色没有变为灰色");
        }
    }

    @Test
    public void K_DeleteSharetest() throws InterruptedException {
        if(!PRODUCT.equals(CLOUD_PBX)){
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
            settings.storage_panel.click();
            ys_waitingTime(5000);
            executeJs("Ext.getCmp('st-storage-slotvm').setValue('"+preference.local_CDR +"')");
            String lineNum = String.valueOf(gridLineNum(preference.grid));
            int row=0;
            for(row = Integer.parseInt(lineNum) ; row>0 ; row--){
                String actTrunkName = String.valueOf(gridContent(preference.grid,row,preference.gridColumn_Name));
                if(actTrunkName.equals(NETWORK_DEVICE_NAME)){
                    break;
                }
            }
            gridClick(preference.grid,row,preference.gridUnmountNetDisk);
//        preference.确认
            preference.Unmount_NetDisk_Yes.click();
            preference.Unmount_NetDisk_OK.click();
            ys_waitingLoading(preference.grid_Mask);
            String afterDelete = String.valueOf(gridContent(preference.grid,Integer.valueOf(String.valueOf(gridLineNum(preference.grid))),preference.gridColumn_Name));
            if (afterDelete.equals(NETWORK_DEVICE_NAME)){
                YsAssert.assertEquals(afterDelete,NETWORK_DEVICE_NAME,NETWORK_DEVICE_NAME+"删除失败");
            }
        }
    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(10000);
        Reporter.infoAfterClass("关闭游览器FileShare_CDR"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(10000);
    }
}
