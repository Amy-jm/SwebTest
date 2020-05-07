package com.yeastar.swebtest.testcase.RegressionCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.untils.AllureReporterListener;
import com.yeastar.untils.RetryListener;
import com.yeastar.untils.TestNGListener;
import org.testng.annotations.*;

/**
 * Created by AutoTest on 2017/10/13.
 * Record Keep Time 、Digits Match 未测试！！！
 */
@Listeners({AllureReporterListener.class, RetryListener.class, TestNGListener.class})
public class AutoCLIP extends SwebDriver {
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        Reporter.infoBeforeClass("开始执行：======  AutoCLIP  ======="); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();

    }

    @Test(groups = "A1",priority = 0)
    public void A0_init(){
        pjsip.Pj_Init();
        //        被测设备注册分机1000，辅助1：分机3001，辅助2：分机2001
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(1101,EXTENSION_PASSWORD,"UDP",UDP_PORT,3);
        pjsip.Pj_CreateAccount(3001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
    }
//    默认选择所有外线
    @Test(groups = "A1", priority = 1)
    public void A1_clip_default() throws InterruptedException {

        Reporter.infoExec(" ----AutoCLIP选择所有外线,其它默认----"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        autoCLIPRoutes.autoCLIPRoutes.click();
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
        }else{
            ys_waitingTime(5000);
        }

        autoCLIPRoutes.viewAutoCLIPList.shouldBe(Condition.exist);
        ys_waitingTime(1000);
        setCheckBox(autoCLIPRoutes.deleteUsedRecords,false);
        setCheckBox(autoCLIPRoutes.onlyKeepMissedCallRecords,false);
        setCheckBox(autoCLIPRoutes.matchOutgoingTrunk,false);
        autoCLIPRoutes.mt_AddAllToSelect.click();
        ys_waitingTime(1000);
        autoCLIPRoutes.viewAutoCLIPList.shouldBe(Condition.exist);
        autoCLIPRoutes.viewAutoCLIPList.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        gridSeleteAll(autoCLIPRoutes.grid);
        autoCLIPRoutes.delete.click();
        ys_waitingTime(3000);
        if (autoCLIPRoutes.delete_yes.isDisplayed()){
            autoCLIPRoutes.delete_yes.click();
        }
        if (autoCLIPRoutes.delete_ok.isDisplayed()){
            autoCLIPRoutes.delete_ok.click();
        }

        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        autoCLIPRoutes.closeAutoClIP_List();
        ys_waitingTime(3000);
        autoCLIPRoutes.save.click();
        ys_waitingMask();
        ys_apply();
    }

    @Test(groups = "A1",priority = 2)
    public void A2_makeCall() throws InterruptedException {
        //        通话测试
        Reporter.infoExec(" 1101拨打13001通过sip外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1101,"13001",DEVICE_IP_LAN);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        autoCLIPRoutes.checkCliplist("1101","3001",SIPTrunk);

//        AutoCLIP呼入测试
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入--预期：分机1101接听");
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1101 <1101>","Answered",SIPTrunk," ",communication_inbound);

    }

    @Test(priority = 3)
    public void A3_makeCall() throws InterruptedException {
        //        删除AutoCLIP list的记录
        Reporter.infoExec(" 删除AutoCLIP List的所有记录");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        autoCLIPRoutes.viewAutoCLIPList.shouldBe(Condition.exist);
        autoCLIPRoutes.viewAutoCLIPList.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        gridSeleteAll(autoCLIPRoutes.grid);
        autoCLIPRoutes.delete.click();
        ys_waitingTime(3000);
        if (autoCLIPRoutes.delete_yes.isDisplayed()){
            autoCLIPRoutes.delete_yes.click();
        }
        if (autoCLIPRoutes.delete_ok.isDisplayed()){
            autoCLIPRoutes.delete_ok.click();
        }
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        autoCLIPRoutes.closeAutoClIP_List();
        ys_waitingTime(3000);

//        重新呼入
        Reporter.infoExec(" 删除AutoCLIP List后，3001拨打3000通过sip外线呼入--预期：分机1000接听");
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000 <1000>","Answered",SIPTrunk," ",communication_inbound);


    }


//    Delete Used Records
    @Test(priority = 4)
    public void B_deleteUsedRecords() throws InterruptedException {
        Reporter.infoExec(" ----启用Delete Used Records----"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        autoCLIPRoutes.viewAutoCLIPList.shouldBe(Condition.exist);
        setCheckBox(autoCLIPRoutes.deleteUsedRecords,true);
        setCheckBox(autoCLIPRoutes.onlyKeepMissedCallRecords,false);
        setCheckBox(autoCLIPRoutes.matchOutgoingTrunk,false);
        autoCLIPRoutes.save.click();
        ys_waitingMask();
        ys_apply();

//        通话测试
        Reporter.infoExec(" 1101拨打13001通过sip外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1101,"13001",DEVICE_IP_LAN);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        autoCLIPRoutes.checkCliplist("1101","3001",SIPTrunk);

//        AutoCLIP呼入测试
        Reporter.infoExec(" 3001拨打3000通过sip外线第1次呼入--预期：分机1101接听");
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1101 <1101>","Answered",SIPTrunk," ",communication_inbound);

        Reporter.infoExec(" 3001拨打3000通过sip外线第2次呼入--预期：分机1000接听");
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000 <1000>","Answered",SIPTrunk," ",communication_inbound);

    }

//    Only Keep Missed Call Records
    @Test(priority = 5)
    public void C_OnlyKeepMissedCallRecords() throws InterruptedException {
        Reporter.infoExec(" ----启用Only Keep Missed Call Records----"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        autoCLIPRoutes.viewAutoCLIPList.shouldBe(Condition.exist);
        setCheckBox(autoCLIPRoutes.deleteUsedRecords,false);
        setCheckBox(autoCLIPRoutes.onlyKeepMissedCallRecords,true);
        setCheckBox(autoCLIPRoutes.matchOutgoingTrunk,false);
        autoCLIPRoutes.save.click();
        ys_waitingMask();
        ys_apply();

//        通话测试
        Reporter.infoExec(" 1101拨打13001通过sip外线呼出，被叫接听--预期不会生成cliplist");
        pjsip.Pj_Make_Call_Auto_Answer(1101,"13001",DEVICE_IP_LAN);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        ys_waitingTime(2000);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        autoCLIPRoutes.viewAutoCLIPList.shouldBe(Condition.exist);
        autoCLIPRoutes.viewAutoCLIPList.click();
        ys_waitingTime(2000);
        if(gridLineNum(autoCLIPRoutes.grid).equals("0")){
            Reporter.pass(" 被叫接听，检查AutoCLIP List未生成记录");
        }else{
            Reporter.error("被叫接听，检查AutoCLIP List生成了记录");
        }
        autoCLIPRoutes.closeAutoClIP_List();

        Reporter.infoExec(" 1101拨打13001通过sip外线呼出，被叫未接--预期生成cliplist");
        pjsip.Pj_Make_Call_No_Answer(1101,"13001",DEVICE_ASSIST_1);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        ys_waitingTime(2000);
        autoCLIPRoutes.viewAutoCLIPList.shouldBe(Condition.exist);
        autoCLIPRoutes.viewAutoCLIPList.click();
        ys_waitingTime(2000);
        if(!gridLineNum(autoCLIPRoutes.grid).equals("0")){
            Reporter.pass(" 被叫未接，检查AutoCLIP List生成记录");
        }else{
            Reporter.error("被叫未接，检查AutoCLIP List未生成记录");
        }
        autoCLIPRoutes.closeAutoClIP_List();
    }

//    Match Outgoing Trunk
    @Test(priority = 6)
    public void D_MatchOutgoingTrunk() throws InterruptedException {
        Reporter.infoExec(" ----启用Match Outgoing Trunk----"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        autoCLIPRoutes.viewAutoCLIPList.shouldBe(Condition.exist);
        setCheckBox(autoCLIPRoutes.deleteUsedRecords,false);
        setCheckBox(autoCLIPRoutes.onlyKeepMissedCallRecords,false);
        setCheckBox(autoCLIPRoutes.matchOutgoingTrunk,true);
        autoCLIPRoutes.save.click();
        ys_waitingMask();
        ys_apply();

//        通话验证
        Reporter.infoExec(" 1101拨打13001通过sip外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1101,"13001",DEVICE_IP_LAN);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        autoCLIPRoutes.checkCliplist("1101","3001",SIPTrunk);

//        AutoCLIP呼入测试
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入--预期：分机1101接听");
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1101 <1101>","Answered",SIPTrunk," ",communication_inbound);

        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 3001拨打3100通过iax外线呼入--预期：分机1000接听");
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3100",DEVICE_ASSIST_1);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000 <1000>","Answered",IAXTrunk," ",communication_inbound);
    }

//    取消AutoCLIP
    @Test(priority = 7)
    public void E_Disable_AutoCLIP() throws InterruptedException {
        Reporter.infoExec(" ----不启用AutoCLIP----"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        autoCLIPRoutes.viewAutoCLIPList.shouldBe(Condition.exist);
        setCheckBox(autoCLIPRoutes.deleteUsedRecords,false);
        setCheckBox(autoCLIPRoutes.onlyKeepMissedCallRecords,false);
        setCheckBox(autoCLIPRoutes.matchOutgoingTrunk,false);
        autoCLIPRoutes.mt_RemoveAllFromSelect.click();
        ys_waitingTime(1000);
        autoCLIPRoutes.viewAutoCLIPList.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        gridSeleteAll(autoCLIPRoutes.grid);
        autoCLIPRoutes.delete.click();
        ys_waitingTime(3000);
        if (autoCLIPRoutes.delete_yes.isDisplayed()){
            autoCLIPRoutes.delete_yes.click();
        }
        if (autoCLIPRoutes.delete_ok.isDisplayed()){
            autoCLIPRoutes.delete_ok.click();
        }
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        autoCLIPRoutes.closeAutoClIP_List();
        ys_waitingTime(3000);
        autoCLIPRoutes.save.click();
        ys_waitingMask();
        ys_apply();

//        通话验证
        Reporter.infoExec(" 1101拨打13001通过sip外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1101,"13001",DEVICE_IP_LAN);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入--预期：分机1000接听");
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000 <1000>","Answered",SIPTrunk," ",communication_inbound);

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
        Reporter.infoAfterClass("执行完毕：======   AutoCLIP   ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        ys_waitingTime(10000);
        killChromePid();

    }
}
