package com.yeastar.swebtest.testcase.RegressionCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;

/**
 * 自定义提示音
 * Created by AutoTest on 2017/10/18.
 */
public class CustomPrompts extends SwebDriver{
    String[] version = DEVICE_VERSION.split("\\.");
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======  VoicePrompts  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);

        if(!PRODUCT.equals(CLOUD_PBX) && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
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
        if(!PRODUCT.equals(CLOUD_PBX)){
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
    public void A_record() throws InterruptedException {
        Reporter.infoExec(" 分机1000录制提示音prompt1"); //执行操作
        customPrompts.recordNew.click();
        record_new_prompt.name.setValue("prompt1");
        comboboxSet(record_new_prompt.recordExtension,extensionList,"1000");
        record_new_prompt.record.click();
        getExtensionStatus(1000,RING,10);
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        if (Integer.valueOf(version[1]) <= 8) {
            m_extension.checkCDR("1000 <RecordFile>","1000","Answered","","",communication_internal);
        }else{
            m_extension.checkCDR("RecordFile","1000","Answered","","",communication_internal);
        }
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        customPrompts.refresh.click();
        YsAssert.assertEquals(gridContent(customPrompts.grid,1,customPrompts.gridcolumn_Name),"prompt1","生成自定义提示音");
    }

//    播放
    @Test
    public void B_play() throws InterruptedException {
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
        if (Integer.valueOf(version[1]) <= 8) {
            m_extension.checkCDR("play_file","1000 <1000>","Answered","","",communication_internal);
        }else{
            m_extension.checkCDR("play_file","1000","Answered","","",communication_internal);
        }
    }

//    重新录制
    @Test
    public void C_reRecord() throws InterruptedException {
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
    public void D_upload() throws InterruptedException {
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
        YsAssert.assertEquals(String.valueOf(gridLineNum(customPrompts.grid)),"2","导入提示音autotestprompt");

    }
//    删除--后续IVR等需要用到提示音，暂不删除

    @AfterMethod
    public void AfterMethod(){
        if(cdRandRecordings.deleteCDR.isDisplayed()){
            closeCDRRecord();
        }
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：======  VoicePrompts  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);

    }
}
