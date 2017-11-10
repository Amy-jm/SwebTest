package com.yeastar.swebtest.testcase.smokecase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by Yeastar on 2017/7/25.
 */
public class VoicePrompts extends SwebDriver {
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("打开游览器并登录设备VoicePrompts"); //执行操作
        initialDriver("chrome","https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",5060,3);
        pjsip.Pj_CreateAccount(1101,"Yeastar202","UDP",5060,4);
        pjsip.Pj_CreateAccount(1102,"Yeastar202","UDP",5060,5);

        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);
    }
    @BeforeMethod
    public void waitTimeBeforeMethod() {
        ys_waitingTime(1000);
    }
    @Test
    public void A_SystemPrompt() throws InterruptedException {
        Reporter.infoExec("Default选择上传的一个提示音");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.voicePrompts_panel.click();
        systemPrompt.systemPrompt.click();
        systemPrompt.browse.click();
        ys_waitingTime(2000);
        importFile(EXPORT_PATH +VOICEPACKAGE_NAME);
        ys_waitingTime(2000);

        systemPrompt.upload.click();

        ys_waitingTime(10000);
        gridCheck(systemPrompt.grid,Integer.valueOf(String.valueOf(gridLineNum(systemPrompt.grid))),systemPrompt.Default);
        systemPrompt.save.click();
        ys_apply();
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(1100,"6400", DEVICE_IP_LAN);
        boolean tcpInfo = tcpSocket.getAsteriskInfo(SYSTEM_PROMPT_LANGUAGE);
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"提示音更换");

    }

    @Test
    public void B_MusicOnHold() throws InterruptedException {
        Reporter.infoExec("选择Music on Hold提示音列表的第一个提示音，点击“Play”在1100播放");
        if(Single_Device_Test){
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
            settings.voicePrompts_panel.click();
            systemPrompt.systemPrompt.click();
        }else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
        }

        musicOnHold.musicOnHold.click();
        gridClick(musicOnHold.grid,1,musicOnHold.gridPlay);
        ys_waitingTime(2000);
        comboboxSet(musicOnHold.plauToExtension,extensionList,"1100");
        ys_waitingTime(1000);
        tcpSocket.connectToDevice();
        musicOnHold.play.click();
        boolean tcpInfo = tcpSocket.getAsteriskInfo("macroformcold_day");
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"等待音乐播放");
    }

    @Test
    public void C_DeleteMusicOnHold() throws InterruptedException {
        Reporter.infoExec("删除Music on Hold提示音列表的第一个提示音");
        if(Single_Device_Test){
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
            settings.voicePrompts_panel.click();
            musicOnHold.musicOnHold.click();
        }
        ys_waitingTime(1000);
        gridClick(musicOnHold.grid,2,musicOnHold.gridDelete);
        musicOnHold.delete_yes.click();
        ys_apply();
    }

    @Test
    public void D_CreateNewPlaylist() throws InterruptedException {
        Reporter.infoExec("创建一个新的playlist");
        if(Single_Device_Test){
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
            settings.voicePrompts_panel.click();
            musicOnHold.musicOnHold.click();
        }
        musicOnHold.createNewPlaylist.click();
        m_voicePrompts.addMOHPlaylist("play1",add_moh_playlist.playlistOrder_Random);
        ys_apply();
        String plist =musicOnHold.chooseMOHPlaylist_input.getValue();
        String gridLine = String.valueOf(gridLineNum(musicOnHold.grid)) ;
        System.out.println("play1 line="+gridLine+" "+plist);
        YsAssert.assertEquals(plist,"play1","play1未自动显示");
        YsAssert.assertEquals(gridLine,"0","play1新增的等待提示音列表为空");
    }
    @Test
    public void E_UploadMusicOnHold(){
        //上传提示音为xxx
        Reporter.infoExec("新的playlist上传音乐molihua.wav");
        executeJs("Ext.getCmp('st-moh-choosefolder').setValue('play1')");
        ys_waitingTime(3000);
        musicOnHold.browse.click();
        ys_waitingTime(2000);
        importFile(EXPORT_PATH +"molihua.wav");
        ys_waitingTime(2000);
        musicOnHold.upload.click();
        ys_waitingTime(5000);

        gridClick(musicOnHold.grid,1,musicOnHold.gridPlay);
        ys_waitingTime(2000);
        comboboxSet(musicOnHold.plauToExtension,extensionList,"1100");
        ys_waitingTime(1000);
        tcpSocket.connectToDevice();
        musicOnHold.play.click();
        boolean tcpInfo = tcpSocket.getAsteriskInfo("molihua");
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"play1等待音乐播放");

    }


    @Test
    public void F_SelectPlay1() throws InterruptedException {
        Reporter.infoExec("Voice Prompts->Prompt Preference中的Music on Hold选择play1,分机1100拨打6400");
        if(Single_Device_Test){
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
            settings.voicePrompts_panel.click();
            promptPreference.promptPreference.click();
        }
        promptPreference.promptPreference.click();
        comboboxSelect(promptPreference.MusicOnHold,"play1");
        promptPreference.save.click();
        pageDeskTop.apply.click();
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(1100,"6400", DEVICE_IP_LAN);
        boolean tcpInfo = tcpSocket.getAsteriskInfo("Language: zh");
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"播放play1中的xx提示音");
    }

    @Test
    public void G_DeleteMusicOnHold() throws InterruptedException {
        Reporter.infoExec("全选所有提示音，点击“Delete”");
        if(Single_Device_Test){
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
            settings.voicePrompts_panel.click();
            promptPreference.promptPreference.click();
        }else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
        }
        musicOnHold.musicOnHold.click();
        ys_waitingLoading(musicOnHold.grid_Mask);
        if(!String.valueOf(gridLineNum(musicOnHold.grid)).equals("0")){
            gridSeleteAll(musicOnHold.grid);
            musicOnHold.deleteHoldFiles.click();
            musicOnHold.delete_yes.click();
        }
    }

    @Test
    public void H_RecordNew() throws InterruptedException {
        Reporter.infoExec("录一个新的录音作为提示音");
        if(Single_Device_Test){
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
            settings.voicePrompts_panel.click();
        }
        customPrompts.customPrompts.click();
        ys_waitingLoading(customPrompts.grid_Mask);
        customPrompts.recordNew.click();
        record_new_prompt.name.setValue("custom1");
        tcpSocket.connectToDevice();
        comboboxSet(record_new_prompt.recordExtension,extensionList,"1100");
        record_new_prompt.record.click();
        ys_waitingTime(5000);
        pjsip.Pj_Answer_Call(1100,false);
        boolean tcpInfo = tcpSocket.getAsteriskInfo("RecordFile");
        ys_waitingTime(15000);
        pjsip.Pj_Hangup_All();
//        ys_apply();
        tcpSocket.closeTcpSocket();
        customPrompts.refresh.click();
        ys_waitingLoading(customPrompts.grid_Mask);
        YsAssert.assertEquals(tcpInfo,true,"RecordFile 开始录音");
        YsAssert.assertEquals(String.valueOf(gridContent(customPrompts.grid,1,customPrompts.gridcolumn_Name)),"custom1","自定义提示音生成");
    }
    @Test
    public void I_UploadCustomPromts(){
        Reporter.infoExec("上传一个音乐作为提示音");
        customPrompts.upload.click();
        upload_a_prompt.broese.click();
        importFile(currentPath+"molihua.wav");
        upload_a_prompt.upload.click();


        gridClick(customPrompts.grid,2,customPrompts.gridPlay);
        ys_waitingTime(2000);
        comboboxSet(customPrompts.playToExtension,extensionList,"1100");
        ys_waitingTime(1000);
        tcpSocket.connectToDevice();
        customPrompts.play.click();
        boolean tcpInfo = tcpSocket.getAsteriskInfo("molihua");
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"custom Prompts音乐播放");
    }
    @Test
    public void J_SelectCustomPromts(){
        Reporter.infoExec("上传一个音乐作为提示音");
        gridClick(customPrompts.grid,1,customPrompts.gridRecord);
        ys_waitingTime(2000);
        comboboxSet(customPrompts.playToExtension,extensionList,"1100");
        ys_waitingTime(1000);
        tcpSocket.connectToDevice();
        customPrompts.record_play.click();
        boolean tcpInfo = tcpSocket.getAsteriskInfo("RecordFile");
        tcpSocket.closeTcpSocket();
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"custom record重新录音");
    }
    @Test
    public void K_DownloadCustom(){
        Reporter.infoExec("下载自定义提示音");
        gridClick(customPrompts.grid,1,customPrompts.gridDownload);
    }
    @Test
    public void L_DeleteCustom()  {
        Reporter.infoExec("删除第一个自定义提示音");
        gridClick(customPrompts.grid,1,customPrompts.gridDelete);
        customPrompts.delete_yes.click();

    }
    @Test
    public void M_DeleteAllCustom() {
        Reporter.infoExec("删除全部自定义提示音");
        if(!String.valueOf(gridLineNum(customPrompts.grid)).equals("0")){
            gridSeleteAll(customPrompts.grid);
            customPrompts.delete.click();
            customPrompts.delete_yes.click();
            gridCheckDeleteAll(customPrompts.grid);
            ys_apply();
        }

    }
    @Test
    public void Z_Recover() throws InterruptedException {
        if(Single_Device_Test){
            pageDeskTop.settings.click();
            settings.voicePrompts_panel.click();
        }
        musicOnHold.musicOnHold.click();
        ys_waitingTime(3000);
        executeJs("Ext.getCmp('st-moh-choosefolder').setValue('play1')");
        ys_waitingTime(3000);
        if(musicOnHold.chooseMOHPlaylist_input.getValue().equals("play1")){
            musicOnHold.delete.click();
            musicOnHold.delete_yes.click();
        }
        systemPrompt.systemPrompt.click();
        ys_waitingTime(2000);
        gridCheck(systemPrompt.grid,1,systemPrompt.Default);
        systemPrompt.save.click();
        ys_apply();
        if(Integer.valueOf(String.valueOf(gridLineNum(systemPrompt.grid))) >1){
            gridClick(systemPrompt.grid,2,systemPrompt.gridDelete);
            systemPrompt.delete_yes.click();
        }


    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(10000);
        Reporter.infoAfterClass("关闭游览器_VoicePrompts"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(10000);
    }
}
