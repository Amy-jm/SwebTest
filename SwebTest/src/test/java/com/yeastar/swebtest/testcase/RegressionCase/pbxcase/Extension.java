package com.yeastar.swebtest.testcase.RegressionCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;


/**
 * Created by xlq on 2017/9/21.
 */

public class Extension extends SwebDriver{
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======  Extension  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);

        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }

//    初始化特征码
//    @BeforeClass
    public void InitFeatureCode() {
        Reporter.infoExec(" 初始化特征码设置");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.general_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
        }else{
            ys_waitingTime(3000);
        }
        featureCode.featureCode.click();
        featureCode.oneTouchRecord.shouldBe(Condition.exist);
        setCheckBox(featureCode.oneTouchRecord_check,true);
        setCheckBox(featureCode.blindTransfer_check,true);
        setCheckBox(featureCode.attendedTransfer_check,true);
        setCheckBox(featureCode.callParking_check,true);
        setCheckBox(featureCode.directedCallParking_check,true);
        setCheckBox(featureCode.resetToDefaults_check,true);
        setCheckBox(featureCode.enableForwardAllCalls_check,true);
        setCheckBox(featureCode.disableForwardAllCalls_check,true);
        setCheckBox(featureCode.enableForwardWhenBusy_check,true);
        setCheckBox(featureCode.disableForwardWhenBusy_check,true);
        setCheckBox(featureCode.enableForwardNoAnswer_check,true);
        setCheckBox(featureCode.disableForwardNoAnswer_check,true);
        setCheckBox(featureCode.enableDoNotDisturb_check,true);
        setCheckBox(featureCode.disableDoNotDisturb_check,true);
        setCheckBox(featureCode.listen_check,true);
        setCheckBox(featureCode.whisper_check,true);
        setCheckBox(featureCode.barge_in_check,true);
        featureCode.oneTouchRecord.clear();
        featureCode.oneTouchRecord.setValue("*1");
        featureCode.blindTransfer.clear();
        featureCode.blindTransfer.setValue("*03");
        featureCode.attendedTransfer.clear();
        featureCode.attendedTransfer.setValue("*3");
        featureCode.attendedTransferTimeout.clear();
        featureCode.attendedTransferTimeout.setValue("10");
        featureCode.callParking.clear();
        featureCode.callParking.setValue("*6");
        featureCode.directedCallParking.clear();
        featureCode.directedCallParking.setValue("*06");
        featureCode.parkingExtensionRange.clear();
        featureCode.parkingExtensionRange.setValue("6900-6999");
        featureCode.parkingTimeout.clear();
        featureCode.parkingTimeout.setValue("60");
        featureCode.resettoDefaults.clear();
        featureCode.resettoDefaults.setValue("*70");
        featureCode.enableForwardAllCalls.clear();
        featureCode.enableForwardAllCalls.setValue("*71");
        featureCode.disableForwardAllCalls.clear();
        featureCode.disableForwardAllCalls.setValue("*071");
        featureCode.enableForwardWhenBusy.clear();
        featureCode.enableForwardWhenBusy.setValue("*72");
        featureCode.disableForwardWhenBusy.clear();
        featureCode.disableForwardWhenBusy.setValue("*072");
        featureCode.enableForwardNoAnswer.clear();
        featureCode.enableForwardNoAnswer.setValue("*73");
        featureCode.disableForwardNoAnswer.clear();
        featureCode.disableForwardNoAnswer.setValue("*073");
        featureCode.enableDoNotDisturb.clear();
        featureCode.enableDoNotDisturb.setValue("*74");
        featureCode.disableDoNotDisturb.clear();
        featureCode.disableDoNotDisturb.setValue("*074");
        featureCode.listen.clear();
        featureCode.listen.setValue("*90");
        featureCode.whisper.clear();
        featureCode.whisper.setValue("*91");
        featureCode.barge_in.clear();
        featureCode.barge_in.setValue("*92");
        featureCode.save.click();
        ys_waitingTime(3000);
    }

    @BeforeClass
    public void Register() throws InterruptedException {
        Reporter.infoExec(" 被测设备注册分机1000,1100,1101,1105，辅助2:2000"); //执行操作
        pjsip.Pj_CreateAccount(1000,"Yeastar202","UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",UDP_PORT,2);
        pjsip.Pj_CreateAccount(1101,"Yeastar202","UDP",UDP_PORT,3);
        pjsip.Pj_CreateAccount(1105,"Yeastar202","UDP",UDP_PORT,7);
        pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1100,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1105,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
    }

//    内部分机互打，并录音
    @Test
    public void A1_sip() throws InterruptedException {
        Reporter.infoExec(" SIP内部分机互打：1000拨打1105,1105接听"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"1105",DEVICE_IP_LAN,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <1105>","Answered","","",communication_internal);
//        检查cdr录音
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        if(gridPicColor(cdRandRecordings.grid,1,cdRandRecordings.gridPlay).contains(cdRandRecordings.gridColumnColor_Gray)){
            YsAssert.fail(" 录音失败");
        }else {
            Reporter.pass(" 正确检测到录音文件");
        }
        closeCDRRecord();
    }

//    内部分机互打，未接
    @Test
    public void A2_sip() throws InterruptedException {
        Reporter.infoExec(" SIP内部分机互打：1000拨打1105，1105未接"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"1105",DEVICE_IP_LAN,false);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <1105>","No Answer","","",communication_internal);
    }

//  fxs作为被叫
    @Test
    public void A3_fxs() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(!FXS_1.equals(null)){
            Reporter.infoExec(" FXS分机：1000拨打1106，预期辅助2的2000分机响铃"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000,"1106",DEVICE_IP_LAN);
            ys_waitingTime(10000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000 <1000>","1106 <1106>","Answered","","",communication_internal);
        }
    }

//    FXS作为主叫
    @Test
    public void A4_fxs() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(!FXS_1.equals(null)){
            Reporter.infoExec(" FXS分机：2000拨打51000，预期1000分机接听"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2000,"51000",DEVICE_ASSIST_2);
            ys_waitingTime(20000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1106 <1106>","1000 <1000>","Answered","","",communication_internal);
        }
    }

//    转移
    @Test
    public void B1_AttendTranfer() throws InterruptedException {
        Reporter.infoExec(" 1000拨打1100,1100按*31105转移给分机1105"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"1100",DEVICE_IP_LAN);
        ys_waitingTime(2000);
        pjsip.Pj_Answer_Call(1100,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(1100,"*","3","1","1","0","5","#");
        YsAssert.assertEquals(getExtensionStatus(1105,RING,20),RING,"预期1105响铃");
        pjsip.Pj_Answer_Call(1105,false);
        ys_waitingTime(5000);
        YsAssert.assertEquals(getExtensionStatus(1100,TALKING,20),TALKING,"1100预期为Talking状态");
        pjsip.Pj_hangupCall(1100,1100);
        ys_waitingTime(5000);
        YsAssert.assertEquals(getExtensionStatus(1105,TALKING,20),TALKING,"预期1105为Talking");
        YsAssert.assertEquals(getExtensionStatus(1000,TALKING,20),TALKING,"预期1000为Talking");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <1105(from 1100)>","Answered","","",communication_transfer,1,2,3);
    }

//    转移超时
    @Test
    public void B2_AttendTranferTimeout() throws InterruptedException {
        Reporter.infoExec(" 1000拨打1100,1100按*31105转移给分机1105，超时恢复通话"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"1100",DEVICE_IP_LAN);
        ys_waitingTime(2000);
        pjsip.Pj_Answer_Call(1100,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(1100,"*","3","1","1","0","5","#");
        YsAssert.assertEquals(getExtensionStatus(1105,RING,20),RING,"预期1105响铃");
        ys_waitingTime(20000);
        YsAssert.assertEquals(getExtensionStatus(1100,TALKING,20),TALKING,"1100预期为Talking状态");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","1105 <1105>","No Answer","","",communication_internal,1,2);
    }

//    指定转移
    @Test
    public void B3_BlindTransfer() throws InterruptedException {
        Reporter.infoExec(" 1000拨打1100,1100按*031105转移给分机1105"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"1100",DEVICE_IP_LAN);
        ys_waitingTime(2000);
        pjsip.Pj_Answer_Call(1100,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(1100,"*","0","3","1","1","0","5","#");
        YsAssert.assertEquals(getExtensionStatus(1105,RING,20),RING,"预期1105响铃");
        pjsip.Pj_Answer_Call(1105,true);
        ys_waitingTime(5000);
        YsAssert.assertEquals(getExtensionStatus(1100,HUNGUP,20),HUNGUP,"1100预期为HangUp状态");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <1105(from 1100)>","Answered","","",communication_transfer,1,2,3);

    }

//    呼叫停泊
    @Test
    public void C1_callpark() throws InterruptedException {
        Reporter.infoExec(" 1000拨打1100,1100按*6将通话停泊"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"1100",DEVICE_IP_LAN);
        ys_waitingTime(8000);
        pjsip.Pj_Send_Dtmf(1100,"*","6");
        YsAssert.assertEquals(getExtensionStatus(1100,HUNGUP,10),HUNGUP,"预期1100为HangUp状态");
        Reporter.infoExec(" 1100拨打6900,恢复通话"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"6900",DEVICE_IP_LAN);
        ys_waitingTime(8000);
        YsAssert.assertEquals(getExtensionStatus(1100,TALKING,20),TALKING,"预期1100为Talking状态");
        YsAssert.assertEquals(getExtensionStatus(1000,TALKING,1),TALKING,"预期1000为Talking状态");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1100 <1100(from 6900)>","Answered","","",communication_internal);
    }

//    指定停泊
    @Test
    public void C2_callpark() throws InterruptedException {
        Reporter.infoExec(" 1000拨打1100,1100按*066950将通话停泊"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"1100",DEVICE_IP_LAN);
        ys_waitingTime(8000);
        pjsip.Pj_Send_Dtmf(1100,"*","0","6","6","9","5","0");
        YsAssert.assertEquals(getExtensionStatus(1100,HUNGUP,10),HUNGUP,"预期1100为HangUp状态");
        Reporter.infoExec(" 1105拨打6950,恢复通话"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1105,"6950",DEVICE_IP_LAN);
        ys_waitingTime(8000);
        YsAssert.assertEquals(getExtensionStatus(1105,TALKING,20),TALKING,"预期1100为Talking状态");
        YsAssert.assertEquals(getExtensionStatus(1000,TALKING,1),TALKING,"预期1000为Talking状态");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <1105(from 6950)>","Answered","","",communication_internal);
    }

//总是转移

    @Test
    public void D1_callforward() throws InterruptedException {
        Reporter.infoExec(" 1100拨打*711105将通话总是转移到1105"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"*711105",DEVICE_IP_LAN,false);
        ys_waitingTime(2000);
        ys_waitingTime(2000);
        Reporter.infoExec(" 1000拨打1100,预期1105响铃接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"1100",DEVICE_IP_LAN);
        YsAssert.assertEquals(getExtensionStatus(1105,RING,10),RING,"预期1105响铃");
        pjsip.Pj_Answer_Call(1105,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <1105(from 1100)>","Answered","","",communication_internal);
    }

    @Test
    public void D2_cancellForward() throws InterruptedException {
        Reporter.infoExec(" 1100拨打*071取消通话总是转移"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"*071",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        Reporter.infoExec(" 1000拨打1100,预期1100响铃接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"1100",DEVICE_IP_LAN);
        YsAssert.assertEquals(getExtensionStatus(1105,HUNGUP,10),HUNGUP,"预期1105为HangUp");
        YsAssert.assertEquals(getExtensionStatus(1100,RING,10),RING,"预期1100响铃");
        pjsip.Pj_Answer_Call(1100,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1100 <1100>","Answered","","",communication_internal);
    }

//    忙时转移
    @Test
    public void E1_whenbusy() throws InterruptedException {
        Reporter.infoExec(" 1100拨打*721105忙时转移到1105"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"*721105",DEVICE_IP_LAN,false);
        ys_waitingTime(10000);
        Reporter.infoExec(" 1000拨打1100,预期1100响铃接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"1100",DEVICE_IP_LAN);
        ys_waitingTime(2000);
        pjsip.Pj_Answer_Call(1100,486,false);
        ys_waitingTime(4000);
        Reporter.infoExec(" 1100保持通话，1101拨打1100--预期1105响铃"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1101,"1100",DEVICE_IP_LAN);
        YsAssert.assertEquals(getExtensionStatus(1105,RING,10),RING,"预期1105响铃");
        pjsip.Pj_Answer_Call(1105,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <1105(from 1100)>","Answered","","",communication_internal);
    }

    @Test
    public void E2_cancelWhenBusy() throws InterruptedException {
        Reporter.infoExec(" 1100拨打*072取消忙时转移"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"*072",DEVICE_IP_LAN,false);
        pjsip.Pj_Make_Call_Auto_Answer(1100,"*073",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        Reporter.infoExec(" 1000拨打1100,预期1100响铃接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"1100",DEVICE_IP_LAN);
        ys_waitingTime(2000);
        YsAssert.assertEquals(getExtensionStatus(1100,RING,10),RING,"预期1100为Ring");
        pjsip.Pj_Answer_Call(1100,486,true);
        ys_waitingTime(2000);
        Reporter.infoExec(" 1100保持通话，1101拨打1100--预期1101挂断"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1101,"1100",DEVICE_IP_LAN);
        YsAssert.assertEquals(getExtensionStatus(1101,HUNGUP,20),HUNGUP,"预期1101挂断状态");
        pjsip.Pj_Hangup_All();
    }

//    无应答转移
    @Test
    public void F1_NoAnswer() throws InterruptedException {
        Reporter.infoExec(" 1100拨打*731105无应答时转移到1105"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"*731105",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        pjsip.Pj_Make_Call_No_Answer(1000,"1100",DEVICE_IP_LAN);
        YsAssert.assertEquals(getExtensionStatus(1100,RING,20),RING,"预期1100响铃状态");
        Reporter.infoExec(" 1000拨打1100，超时不接，预期1105响铃"); //执行操作
        YsAssert.assertEquals(getExtensionStatus(1105,RING,60),RING,"预期1105响铃状态");
        pjsip.Pj_Answer_Call(1105,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <1105>","Answered","","",communication_internal);
    }

    @Test
    public void F2_NoAnswer() throws InterruptedException {
        Reporter.infoExec(" 1100拨打*073取消无应答转移"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"*073",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        Reporter.infoExec(" 1000拨打1100，超时不接，1000被挂断"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"1100",DEVICE_IP_LAN);
        ys_waitingTime(30000);
        YsAssert.assertEquals(getExtensionStatus(1000,HUNGUP,10),HUNGUP,"预期1000为HangUp");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1100 <1100>","No Answer","","",communication_internal);
    }

// DND
    @Test
    public void G1_dnd() throws InterruptedException {
        Reporter.infoExec(" 1100拨打*74启用免打扰"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"*74",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        Reporter.infoExec(" 1000拨打1100，预期无法呼入"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"1100",DEVICE_IP_LAN);
        YsAssert.assertEquals(getExtensionStatus(1000,HUNGUP,20),HUNGUP,"预期1000为HangUp");
        pjsip.Pj_Hangup_All();
    }

    @Test
    public void G2_canceldnd() throws InterruptedException {
        Reporter.infoExec(" 1100拨打*074关闭免打扰"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"*074",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        Reporter.infoExec(" 1000拨打1100，预期正常呼入"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"1100",DEVICE_IP_LAN);
        YsAssert.assertEquals(getExtensionStatus(1100,RING,20),RING,"预期1100响铃");
        pjsip.Pj_Answer_Call(1100,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1100 <1100>","Answered","","",communication_internal);
    }

//  Reset to Default
    @Test
    public void H1_resetToDefault() throws InterruptedException {
        Reporter.infoExec(" 1100拨打*70恢复默认值"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"*70",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
    }

    //监听，怎么验证？

    @Test
    public void I_Voicemail() throws InterruptedException {
        Reporter.infoExec(" 1000拨打1105，1105未接，到voicemail");
        pjsip.Pj_Make_Call_No_Answer(1000,"1105",DEVICE_IP_LAN);
        ys_waitingTime(60000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <1105>","Voicemail","","","Internal");
    }

    @Test
    public void J_internal() throws InterruptedException {
        Reporter.infoExec(" 1000拨打1105，1105接听，1105按*1进行一键录音");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"1105",DEVICE_IP_LAN);
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(1105,"*","1");
        ys_waitingTime(10000);
        pjsip.Pj_Send_Dtmf(1105,"*","1");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <1105>","Answered","","",communication_internal);

    }

    @Test
    public void K1_check_Voicemail(){
        Reporter.infoExec(" 分机1105登录，查看存在1000留下的语音留言"); //执行操作
        logout();
        if(PRODUCT.equals(CLOUD_PBX)) {
            login("autotest@yeastar.com", "Yeastar202");
        }else{
            login("1105","Yeastar202");
        }
        me.taskBar_Main.click();
        me.mesettingShortcut.click();
        me.me_Voicemail.click();
        ys_waitingLoading(me_voicemail.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(me_voicemail.grid))) !=0) {
            YsAssert.assertEquals((gridContent(me_voicemail.grid, 1, me_voicemail.gridColumn_Callerid)), "1000(1000)", "语音留言检查:预期第1行的CallerID为1000(1000)");
        }else{
            YsAssert.fail("语音留言检查:预期第1行的CallerID为1000(1000)");
        }
    }

    @Test
    public void K2_Check_OneTouchRecord() throws InterruptedException {
        Reporter.infoExec(" 分机1105登录，查看存在1键录音"); //执行操作
        me.taskBar_Main.click();
        me.mesettingShortcut.click();
        me.me_CDRandRecording.click();
        ys_waitingLoading(me_cdRandRecording.grid_Mask);
        if(gridPicColor(me_cdRandRecording.grid,1,me_cdRandRecording.gridPlay).contains(me_cdRandRecording.gridColumnColor_Gray)){
            YsAssert.fail(" 录音失败");
        }else {
            Reporter.pass(" 正确检测到录音文件");
        }

    }

    @AfterMethod
    public void AfterMethod(){
        if(cdRandRecordings.deleteCDR.isDisplayed()){
            closeCDRRecord();
        }
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：======  Extension  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);
    }

}




