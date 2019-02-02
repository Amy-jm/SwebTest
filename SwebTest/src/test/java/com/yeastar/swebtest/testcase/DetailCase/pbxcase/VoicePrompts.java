package com.yeastar.swebtest.testcase.DetailCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 自定义提示音--前置操作步骤--在IVR、Queue等队列中需要用到
 * Created by AutoTest on 2017/10/18.
 */
public class VoicePrompts extends SwebDriver{
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======  VoicePrompts  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);

        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes") && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();

//        被测设备注册分机1000
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.voicePrompts_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
        }else{
            ys_waitingTime(5000);
        }
        customPrompts.customPrompts.click();
        ys_waitingLoading(customPrompts.grid_Mask);
        customPrompts.recordNew.shouldBe(Condition.exist);
        deletes(" 删除所有提示音",customPrompts.grid,customPrompts.delete,customPrompts.delete_yes,customPrompts.grid_Mask);
    }

//    录制
    @Test
    public void A1_record_satisfaction() {
        Reporter.infoExec(" 分机1000录制提示音satisfaction"); //执行操作
        customPrompts.recordNew.click();
        record_new_prompt.name.setValue("satisfaction");
        comboboxSet(record_new_prompt.recordExtension, extensionList, "1000");
        record_new_prompt.record.click();
        getExtensionStatus(1000, RING, 10);
        pjsip.Pj_Answer_Call(1000, true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
//        m_extension.checkCDR("1000 <RecordFile>", "1000", "Answered", "", "", communication_internal);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        customPrompts.refresh.click();
        YsAssert.assertEquals(gridContent(customPrompts.grid, 1, customPrompts.gridcolumn_Name), "satisfaction", "生成自定义提示音");
    }

    @Test
    public void A2_record_prompt1() {
        Reporter.infoExec(" 分机1000录制提示音prompt1"); //执行操作
        customPrompts.recordNew.click();
        record_new_prompt.name.setValue("prompt1");
        comboboxSet(record_new_prompt.recordExtension,extensionList,"1000");
        record_new_prompt.record.click();
        getExtensionStatus(1000,RING,10);
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
//        m_extension.checkCDR("1000 <RecordFile>","1000","Answered","","",communication_internal);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        customPrompts.refresh.click();
        YsAssert.assertEquals(gridContent(customPrompts.grid,1,customPrompts.gridcolumn_Name),"prompt1","生成自定义提示音");
    }

    @Test
    public void A3_record_agentid() {
        Reporter.infoExec(" 分机1000录制提示音agentid"); //执行操作
        customPrompts.recordNew.click();
        record_new_prompt.name.setValue("agentid");
        comboboxSet(record_new_prompt.recordExtension,extensionList,"1000");
        record_new_prompt.record.click();
        getExtensionStatus(1000,RING,10);
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
//        m_extension.checkCDR("1000 <RecordFile>","1000","Answered","","",communication_internal);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        customPrompts.refresh.click();
        YsAssert.assertEquals(gridContent(customPrompts.grid,1,customPrompts.gridcolumn_Name),"agentid","生成自定义提示音");
    }

    @Test
    public void A4_record_Join() {
        Reporter.infoExec(" 分机1000录制提示音Join"); //执行操作
        customPrompts.recordNew.click();
        record_new_prompt.name.setValue("Join");
        comboboxSet(record_new_prompt.recordExtension,extensionList,"1000");
        record_new_prompt.record.click();
        getExtensionStatus(1000,RING,10);
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
//        m_extension.checkCDR("1000 <RecordFile>","1000","Answered","","",communication_internal);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        customPrompts.refresh.click();
        YsAssert.assertEquals(gridContent(customPrompts.grid,1,customPrompts.gridcolumn_Name),"Join","生成自定义提示音");
    }

    @Test
    public void A5_record_AgentAnnouncement() {
        Reporter.infoExec(" 分机1000录制提示音AgentAnnouncement"); //执行操作
        customPrompts.recordNew.click();
        record_new_prompt.name.setValue("AgentAnnouncement");
        comboboxSet(record_new_prompt.recordExtension,extensionList,"1000");
        record_new_prompt.record.click();
        getExtensionStatus(1000,RING,10);
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
//        m_extension.checkCDR("1000 <RecordFile>","1000","Answered","","",communication_internal);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        customPrompts.refresh.click();
        YsAssert.assertEquals(gridContent(customPrompts.grid,1,customPrompts.gridcolumn_Name),"AgentAnnouncement","生成自定义提示音");
    }

//    播放
    @Test
    public void B_play() {
        Reporter.infoExec(" 分机1000播放提示音prompt1"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        gridClick(customPrompts.grid,1,customPrompts.gridPlay);
        comboboxSet(customPrompts.playToExtension,extensionList,"1000");
        customPrompts.play.click();
        getExtensionStatus(1000,RING,10);
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("play_file","1000 <1000>","Answered","","",communication_internal);

    }

//    重新录制
    @Test
    public void C_reRecord() {
        Reporter.infoExec(" 分机1000重新录制提示音prompt1"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        gridClick(customPrompts.grid,1,customPrompts.gridRecord);
        comboboxSet(customPrompts.playToExtension,extensionList,"1000");
        customPrompts.record_play.click();
        getExtensionStatus(1000,RING,10);
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <RecordFile>","1000","Answered","","",communication_internal);
    }

//    上传
    @Test
    public void D_upload() {
        Reporter.infoExec(" 上传提示音autotestprompt"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        customPrompts.recordNew.shouldBe(Condition.exist);
        customPrompts.upload.click();
        upload_a_prompt.broese.click();
        ys_waitingTime(2000);
        importFile(EXPORT_PATH +"autotestprompt.wav");
        ys_waitingTime(2000);
        upload_a_prompt.upload.click();
        ys_waitingTime(2000);
        YsAssert.assertEquals(String.valueOf(gridLineNum(customPrompts.grid)),"6","导入提示音autotestprompt");

    }
//    删除--后续IVR等需要用到提示音，暂不删除


//    music on hold
    @Test
    public void E_MusicOnHold() {
        Reporter.infoExec("选择Music on Hold提示音列表的第一个提示音，点击“Play”在1000播放");
        musicOnHold.musicOnHold.click();
        gridClick(musicOnHold.grid,1,musicOnHold.gridPlay);
        ys_waitingTime(2000);
        comboboxSet(musicOnHold.plauToExtension,extensionList,"1000");
        ys_waitingTime(1000);
        tcpSocket.connectToDevice();
        musicOnHold.play.click();
        boolean tcpInfo = tcpSocket.getAsteriskInfo("macroformcold_day");
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"等待音乐播放");
    }

    @Test
    public void F_CreateNewPlaylist() {
        Reporter.infoExec("创建一个新的Autotest");
        musicOnHold.musicOnHold.click();
        System.out.println("nnnnnnnnn："+return_executeJs("Ext.getCmp('st-moh-choosefolder').store.getCount()"));
        if(!String.valueOf(return_executeJs("Ext.getCmp('st-moh-choosefolder').store.getCount()")).equals("1")){
            executeJs("Ext.getCmp('st-moh-choosefolder').setValue('Autotest')");
            musicOnHold.chooseMOHPlaylist_delete.click();
            if(musicOnHold.delete_yes.isDisplayed()){
                musicOnHold.delete_yes.click();
            }
        }
        musicOnHold.createNewPlaylist.click();
        m_voicePrompts.addMOHPlaylist("Autotest",add_moh_playlist.playlistOrder_Random);
        ys_apply();
        String plist =musicOnHold.chooseMOHPlaylist_input.getValue();
        String gridLine = String.valueOf(gridLineNum(musicOnHold.grid)) ;
        System.out.println("Autotest line="+gridLine+" "+plist);
        YsAssert.assertEquals(plist,"Autotest","Autotest未自动显示");
        YsAssert.assertEquals(gridLine,"0","Autotest新增的等待提示音列表为空");
    }

    @Test
    public void G_UploadMusicOnHold(){
        //上传提示音为xxx
        Reporter.infoExec("新的Autotest上传音乐molihua.wav");
        executeJs("Ext.getCmp('st-moh-choosefolder').setValue('Autotest')");
        ys_waitingTime(3000);
        musicOnHold.browse.click();
        ys_waitingTime(2000);
        importFile(EXPORT_PATH +"molihua.wav");
        ys_waitingTime(2000);
        musicOnHold.upload.click();
        ys_waitingTime(5000);

        gridClick(musicOnHold.grid,1,musicOnHold.gridPlay);
        ys_waitingTime(2000);
        comboboxSet(musicOnHold.plauToExtension,extensionList,"1000");
        ys_waitingTime(1000);
        tcpSocket.connectToDevice();
        musicOnHold.play.click();
        boolean tcpInfo = tcpSocket.getAsteriskInfo("molihua");
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"Autotest等待音乐播放");
    }

    @AfterMethod
    public void AfterMethod(){
        if(cdRandRecordings.deleteCDR.isDisplayed()){
            closeCDRRecord();
        }
    }

    @AfterClass
    public void AfterClass() {
        Reporter.infoAfterClass("执行完毕：======  VoicePrompts  ======"); //执行操作
        quitDriver();
        pjsip.Pj_Destory();
        ys_waitingTime(30000);
    }
}
