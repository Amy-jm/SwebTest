package com.yeastar.swebtest.testcase.DetailCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.PortUnreachableException;
import java.util.ArrayList;

/**
 * Created by Caroline on 2018/1/4.
 * Only Record Missed Calls：PSTN线路只记录未接；这部分没有编码，因为自动化里的PSTN线路是默认已接状态
 * AutoCLIP不支持Account线路
 */
public class AutoCLIP extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：====== AutoCLIP ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }
    @Test
    public void A_addExtension(){
        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_CreateAccount(1000, "Yeastar202", "UDP", UDP_PORT, 1);
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_CreateAccount(1100, "Yeastar202", "UDP", UDP_PORT, 2);
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2000, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2001, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2002"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2002, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2002, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2006"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2006, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2006, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备1注册分机3001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3001, "Yeastar202", -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3001, DEVICE_ASSIST_1);

        Reporter.infoExec(" 辅助设备3注册分机4000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 4000, "Yeastar202", -1, DEVICE_ASSIST_3, UDP_PORT_ASSIST_3);
        pjsip.Pj_Register_Account_WithoutAssist(4000, DEVICE_ASSIST_3);

        Reporter.infoExec(" 辅助设备3注册分机4001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 4001, "Yeastar202", -1, DEVICE_ASSIST_3, UDP_PORT_ASSIST_3);
        pjsip.Pj_Register_Account_WithoutAssist(4001, DEVICE_ASSIST_3);
        closePbxMonitor();
    }
    @Test
    public void B1_default(){
        Reporter.infoExec(" ----所有选项默认----"); //执行操作
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
        autoCLIPRoutes.mt_RemoveAllFromSelect.click();
        ys_waitingTime(1000);
        autoCLIPRoutes.viewAutoCLIPList.shouldBe(Condition.exist);
        autoCLIPRoutes.viewAutoCLIPList.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        gridSeleteAll(autoCLIPRoutes.grid);
        autoCLIPRoutes.delete.click();
        if (autoCLIPRoutes.delete_yes.isDisplayed()){
            autoCLIPRoutes.delete_yes.click();
        }
        if (autoCLIPRoutes.delete_ok.isDisplayed()){
            autoCLIPRoutes.delete_ok.click();
        }

        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        autoCLIPRoutes.closeAutoClIP_List();
        ys_waitingTime(2000);
        autoCLIPRoutes.save.click();
        ys_waitingMask();
        ys_apply();
    }
    @Test
    public void B2_checkDefault(){
        Reporter.infoExec(" 1100拨打32000通过sps外线呼入到分机2000,分机接听"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32000",DEVICE_IP_LAN,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","32000","Answered"," ",SPS,communication_outRoute);
//        sps外线测试&&分机2000未接电话
        Reporter.infoExec(" 1100拨打32000通过sps外线呼入到分机2000，分机未接"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"32000",DEVICE_IP_LAN,false);
        if (getExtensionStatus(2000, RING, 8) == RING) {
            Reporter.pass(" 分机2000状态--RING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
        }
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>", "32000", "No Answer", " ",SPS,  communication_outRoute);
    }
    @Test
    public void B3_checkDefault(){
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到分机1000，是呼入到路由目的地，而不是1100"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1000 <1000>","Answered",SPS," ",communication_inbound);
    }
    /*
    * 验证Member Trunk
    * */
    @Test
    public void C_addAllTrunk(){
        Reporter.infoExec(" ----AutoCLIP选择所有外线,其它默认----"); //执行操作
        autoCLIPRoutes.mt_AddAllToSelect.click();
        ys_waitingTime(1000);
        autoCLIPRoutes.save.click();
        ys_waitingMask();
        ys_apply();
    }
    @Test
    public void D1_makeCall1(){
        Reporter.infoExec(" 1100拨打32000通过sps外线呼入到分机2000,分机接听"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32000",DEVICE_IP_LAN,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","32000","Answered"," ",SPS,communication_outRoute);
    }
//    @Test
  /*  public void D1_makeCall2(){
        Reporter.infoExec(" 1100拨打13001通过sip外线呼入到分机3001,分机接听"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"13001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","13001","Answered"," ",SIPTrunk,communication_outRoute);
    }*/
@Test
public void D1_makeCall2(){
    Reporter.infoExec(" 1100拨打33001通过sps外线呼入到分机2000,分机接听"); //执行操作
    pjsip.Pj_Make_Call_Auto_Answer(1100,"33001",DEVICE_IP_LAN,false);
    if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
        Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
    } else {
        Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
    }
    ys_waitingTime(8000);
    pjsip.Pj_Hangup_All();
    m_extension.checkCDR("1100 <1100>","33001","Answered"," ",SPS,communication_outRoute);
}
//AutoCLIP不支持Account
/*@Test
public void D1_makeCall3(){
    Reporter.infoExec(" 1100拨打34000通过sps外线呼入到分机2000,分机接听"); //执行操作
    pjsip.Pj_Make_Call_Auto_Answer(1100,"34000",DEVICE_IP_LAN,false);
    ys_waitingTime(8000);
    pjsip.Pj_Hangup_All();
    m_extension.checkCDR("1100 <1100>","34000","Answered"," ",SPS,communication_outRoute);
}*/
    @Test
    public void D1_makeCall4(){
        Reporter.infoExec(" 1100拨打3+DEVICE_ASSIST_GSM通过sps外线呼入到分机2000,分机接听"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"3"+DEVICE_ASSIST_GSM,DEVICE_IP_LAN,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","3"+DEVICE_ASSIST_GSM,"Answered"," ",SPS,communication_outRoute);
    }
//    验证不同外线
    @Test
    public void D2_checkTrunk1_SPS(){
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <1100>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void D2_checkTrunk2_SIP() throws InterruptedException {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1100 <1100>","Answered",SIPTrunk," ",communication_inbound);
    }

//    AutoCLIP不支持Account线路
    /*@Test
    public void D2_checkTrunk3_Account(){
        if (!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec(" 4000拨打1111通过account外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(4000, "1111", DEVICE_ASSIST_3,false);
            ys_waitingTime(8000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("4000 <6100>", "1100 <1100>", "Answered", ACCOUNTTRUNK, " ", communication_inbound);
        }
    }*/
    @Test
    public void D2_checkTrunk4_IAX(){
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 3001拨打3100通过iax外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3100",DEVICE_ASSIST_1,false);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1100 <1100>","Answered",IAXTrunk," ",communication_inbound);
    }
    @Test
    public void D2_checkTrunk5_SPX(){
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 2000拨打881100通过spx外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //验证存在SPX路由时，走SPX路由优先
        pjsip.Pj_Make_Call_Auto_Answer(2000, "881100", DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>", "1100 <1100>", "Answered", SPX, " ", communication_inbound);
    }
    @Test
    public void D2_checkTrunk6_BRI(){
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!BRI_1.equals("null")){
            Reporter.infoExec(" 2000拨打66666通过bri外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2000,"66666",DEVICE_ASSIST_2,false);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            ys_waitingTime(8000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2000 <2000>","1100 <1100>","Answered",BRI_1," ",communication_inbound);
        }
    }
    @Test
    public void D2_checkTrunk7_PSTN(){
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!FXO_1.equals("null")){
            Reporter.infoExec(" 2000拨打2010通过fxo外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2000,"2010",DEVICE_ASSIST_2,false);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            ys_waitingTime(8000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2000 <2000>","1100 <1100>","Answered",FXO_1," ",communication_inbound);
        }
    }
    @Test
    public void D2_checkTrunk8_E1(){
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!E1.equals("null")){
            Reporter.infoExec(" 2000拨打77777通过E1外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2000,"77777",DEVICE_ASSIST_2,false);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            ys_waitingTime(8000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2000 <2000>","1100 <1100>","Answered",E1," ",communication_inbound);
        }
    }
    @Test
    public void D2_checkTrunk9_GSM(){
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!GSM.equals("null")){
            Reporter.infoExec(" 2000拨打被测设备的gsm号码呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2000,DEVICE_TEST_GSM,DEVICE_ASSIST_2,false);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            ys_waitingTime(15000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR(DEVICE_ASSIST_GSM +" <"+DEVICE_ASSIST_GSM+">","1100 <1100>","Answered",GSM," ",communication_inbound);
        }
    }
    @Test
    public void D3_diffNum1(){
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到路由目的地——分机1000，而不是呼入到分机1100"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void D3_diffNum2_makeCall(){
        Reporter.infoExec(" 1100拨打32001通过sps外线呼入到分机2000,分机接听"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32001",DEVICE_IP_LAN,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","32001","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void D3_diffNum3(){
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1100 <1100>","Answered",SPS," ",communication_inbound);
    }
    /*
    * 测试Digits Match
    * */
    @Test
    public void E1_editAutoCLIP(){
//        删除AutoCLIP记录 && digitsMatch设置为31
        Reporter.infoExec(" 删除AutoCLIP记录 && digitsMatch设置为31"); //执行操作
        autoCLIPRoutes.viewAutoCLIPList.shouldBe(Condition.exist);
        autoCLIPRoutes.viewAutoCLIPList.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        gridSeleteAll(autoCLIPRoutes.grid);
        autoCLIPRoutes.delete.click();
        if (autoCLIPRoutes.delete_yes.isDisplayed()){
            autoCLIPRoutes.delete_yes.click();
        }
        if (autoCLIPRoutes.delete_ok.isDisplayed()){
            autoCLIPRoutes.delete_ok.click();
        }
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        autoCLIPRoutes.closeAutoClIP_List();
        ys_waitingTime(1000);
        executeJs("Ext.getCmp('"+autoCLIPRoutes.digitsMatch+"').setValue('20')");
        ys_waitingTime(1000);
        autoCLIPRoutes.save.click();
        ys_waitingMask();
        ys_apply();
    }
    @Test
    public void E2_checkDigits_makeCall(){
//        分机1100通过SPS呼出，记录到AutoCLIP中
        Reporter.infoExec(" 1100拨打301234501234567890123456789通过sps外线呼入到分机2000,分机接听"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"3101234501234567890123456789",DEVICE_IP_LAN,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","3101234501234567890123456789","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void E3_checkDigits1(){
        Reporter.infoExec(" 2006（CallerID为01234567890123456789）通过sps外线呼入到分机1100,而不会到达路由目的地——1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2006,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2006, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2006状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2006状态为TALKING，实际状态为"+getExtensionStatus(2006, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2006 <01234567890123456789>","1100 <1100>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void E3_checkDigits2() throws InterruptedException {
        Reporter.infoExec(" 2000通过sps外线呼入到路由目的地——1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1000 <1000>","Answered",SPS," ",communication_inbound);
    }
    /*
    * 验证Match Outgoing Trunk
    * */
    @Test
    public void F1_matchOutTrunk_edit(){
        Reporter.infoExec(" 删除AutoCLIP记录 && digitsMatch设置为7 && 勾选 匹配呼出中继"); //执行操作
        ys_waitingMask();
        executeJs("Ext.getCmp('"+autoCLIPRoutes.digitsMatch+"').setValue('7')");
        setCheckBox(autoCLIPRoutes.matchOutgoingTrunk,true);

        autoCLIPRoutes.viewAutoCLIPList.shouldBe(Condition.exist);
        autoCLIPRoutes.viewAutoCLIPList.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        gridSeleteAll(autoCLIPRoutes.grid);
        autoCLIPRoutes.delete.click();
        if (autoCLIPRoutes.delete_yes.isDisplayed()){
            autoCLIPRoutes.delete_yes.click();
        }
        if (autoCLIPRoutes.delete_ok.isDisplayed()){
            autoCLIPRoutes.delete_ok.click();
        }

        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        autoCLIPRoutes.closeAutoClIP_List();
        autoCLIPRoutes.save.click();
        ys_waitingMask();
        ys_apply();
    }
    @Test
    public void F2_matchOutTrunk_makeCall1(){
//        拨打的号码为 2000
        Reporter.infoExec(" 1100拨打32000通过sps外线呼入到分机2000,分机接听"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32000",DEVICE_IP_LAN,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","32000","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void F2_matchOutTrunk_makeCall2(){
//        拨打的号码为 2000
        Reporter.infoExec(" 1100拨打33001通过sps外线呼入到分机2000,分机接听"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"33001",DEVICE_IP_LAN,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","33001","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void F3_matchOutTrunk1(){
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <1100>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void F3_matchOutTrunk2() throws InterruptedException {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到路由目的地——分机1000，而不是呼入到分机1100"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_hangupCall(3001,1000);
        m_extension.checkCDR("3001 <3001>","1000 <1000>","Answered",SIPTrunk," ",communication_inbound);
    }
    @Test
    public void F4_disMatchOutTrunk(){
//        取消勾选匹配呼出中继选项
        Reporter.infoExec(" 取消勾选 匹配呼出中继"); //执行操作
        settings.callControl_tree.click();
        autoCLIPRoutes.autoCLIPRoutes.click();
        setCheckBox(autoCLIPRoutes.matchOutgoingTrunk,false);
        ys_waitingTime(1000);
        autoCLIPRoutes.save.click();
        ys_waitingMask();
        ys_apply();
    }
    @Test
    public void F5_matchOutTrunk() throws InterruptedException {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1100 <1100>","Answered",SIPTrunk," ",communication_inbound);
    }
    /*
    * SIP外线、IAX、PSTN、都是已接状态，GSM线路无法做到已接状态，只能是未接。
    * 所以无法去验证missRecord是否生效，即以上几种线路都不测试
    *
    * 测试Only Keep Missed Call Records功能
    * 只有对方未接的情况下，该通话才会记录到AutoCLIP中
    * 1、通话为已接/拒接情况下，不做记录
    * 2、通话为未接情况下，AutoCLIP做记录
    * 3、取消勾选，通话为已接/拒接的情况下，AutoCLIP会做记录
    * */
    @Test
    public void G1_OnlyMissedRecord(){
//        勾选  只记录未接电话
        Reporter.infoExec(" 勾选 只记录未接电话 && 删除AutoCLIP记录"); //执行操作
        ys_waitingMask();
        setCheckBox(autoCLIPRoutes.matchOutgoingTrunk,false);//取消勾选matchOutTrunk
        ys_waitingTime(1000);
        setCheckBox(autoCLIPRoutes.onlyKeepMissedCallRecords,true);
        ys_waitingTime(1000);
        autoCLIPRoutes.viewAutoCLIPList.shouldBe(Condition.exist);
        autoCLIPRoutes.viewAutoCLIPList.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        gridSeleteAll(autoCLIPRoutes.grid);
        autoCLIPRoutes.delete.click();
        if (autoCLIPRoutes.delete_yes.isDisplayed()){
            autoCLIPRoutes.delete_yes.click();
        }
        if (autoCLIPRoutes.delete_ok.isDisplayed()){
            autoCLIPRoutes.delete_ok.click();
        }

        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        autoCLIPRoutes.closeAutoClIP_List();
        autoCLIPRoutes.save.click();
        ys_waitingMask();
        ys_apply();
    }
    @Test
    public void G2_makeCall1_sps_accept(){
        Reporter.infoExec(" 1100拨打32000通过sps外线呼出到分机2000,分机接听"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32000",DEVICE_IP_LAN,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","32000","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void G3_makeCall_sps_reject(){
//        sps外线测试&&分机2000拒接电话
        Reporter.infoExec(" 1100拨打32001通过sps外线呼出到分机2000，分机拒接"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"32001",DEVICE_IP_LAN);
        if (getExtensionStatus(2000, RING, 8) == RING) {
            Reporter.pass(" 分机2000状态--RING，1100呼出成功");
        } else {
            Reporter.error(" 预期分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
        }
        ys_waitingTime(8000);
        pjsip.Pj_Answer_Call(2000,486,false);
        ys_waitingTime(2000);
        if (getExtensionStatus(2000, IDLE, 8) == IDLE) {
            Reporter.pass(" 分机2000状态--IDLE，分机2000拒接成功");
        } else {
            Reporter.error(" 预期分机2000状态为IDLE，实际状态为"+getExtensionStatus(2000, IDLE, 8));
        }
        pjsip.Pj_Hangup_All();
        if (PRODUCT.equals(CLOUD_PBX)) {
            m_extension.checkCDR("1100 <1100>", "32001", "Busy", " ", SPS, communication_outRoute);
        }else {
            m_extension.checkCDR("1100 <1100>", "32001", "Answered"," ", SPS,  communication_outRoute);
        }
    }
   /* @Test
    public void G4_makeCall_account_accept(){
        if(!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec(" 1100拨打914001通过account外线呼出到分机4000，分机接听"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1100, "914001", DEVICE_IP_LAN,false);
            ys_waitingTime(10000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1100 <1100>", "914001", "Answered", " ", ACCOUNTTRUNK, communication_outRoute);
        }
    }*/
    /*@Test
    public void G5_makeCall_account_reject(){
        if(!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec(" 1100拨打914001通过account外线呼出到分机4000，分机拒接"); //执行操作
            pjsip.Pj_Make_Call_No_Answer(1100,"914001",DEVICE_IP_LAN,false);
            ys_waitingTime(8000);
            pjsip.Pj_Answer_Call(4000,486,false);
            ys_waitingTime(2000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1100 <1100>", "914001", "Answered", " ", ACCOUNTTRUNK, communication_outRoute);
        }
    }*/
    @Test
    public void H1_checkMissRecord_SPS(){
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到路由目的地——分机1000，而不是呼入到分机1100"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1000 <1000>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void H2_checkMissRecord_SPX(){
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }else {
            Reporter.infoExec(" 2000拨打881100通过spx外线呼入到路由目的地——分机1000，而不是呼入到分机1100"); //验证存在SPX路由时，走SPX路由优先
            pjsip.Pj_Make_Call_Auto_Answer(2000, "881100", DEVICE_ASSIST_2,false);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            ys_waitingTime(5000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2000 <2000>", "1000 <1000>", "Answered", SPX, " ", communication_inbound);
        }
    }
    @Test
    public void H3_checkMissRecord_BRI(){
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!BRI_1.equals("null")){
            Reporter.infoExec(" 2000拨打66666通过bri外线呼入到路由目的地——分机1000，而不是呼入到分机1100"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2000,"66666",DEVICE_ASSIST_2,false);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            ys_waitingTime(5000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2000 <2000>","1000 <1000>","Answered",BRI_1," ",communication_inbound);
        }
    }
    @Test
    public void H4_checkMissRecord_E1(){
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!E1.equals("null")){
            Reporter.infoExec(" 2000拨打77777通过E1外线呼入到路由目的地——分机1000，而不是呼入到分机1100"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2000,"77777",DEVICE_ASSIST_2,false);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            ys_waitingTime(5000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2000 <2000>","1000 <1000>","Answered",E1," ",communication_inbound);
        }
    }
   /* @Test
    public void H5_checkMissRecord_Accout(){
        if (!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec(" 4001拨打1111通过account外线呼入到路由目的地——分机1000，而不是呼入到分机1100"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(4001, "1111", DEVICE_ASSIST_3,false);
            ys_waitingTime(10000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("4001 <6100>", "1000 <1000>", "Answered", ACCOUNTTRUNK, " ", communication_inbound);
        }
    }*/
    @Test
    public void I1_makeCall_SPS_noAnswer(){
        //        sps外线测试&&分机2000未接电话
        Reporter.infoExec(" 1100拨打32000通过sps外线呼入到分机2000，分机未接"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"32000",DEVICE_IP_LAN,false);
        if (getExtensionStatus(2000, RING, 8) == RING) {
            Reporter.pass(" 分机2000状态--RING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>", "32000", "No Answer", " ",SPS,  communication_outRoute);
    }
   /* @Test
    public void I1_makeCall_SPS_noAnswer2(){
        //        sps外线测试&&分机2000未接电话
        Reporter.infoExec(" 1100拨打34001通过sps外线呼入到分机4000，分机未接"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"34001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>", "34001", "No Answer", " ",SPS,  communication_outRoute);
    }*/
    /*@Test
    public void I2_makeCall_Account_noAnswer(){
        //        account外线测试&&分机4000未接电话
        if(!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec(" 1100拨打914001通过account外线呼出到分机4000，分机未接"); //执行操作
            pjsip.Pj_Make_Call_No_Answer(1100, "914001", DEVICE_IP_LAN,false);
            ys_waitingTime(8000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1100 <1100>", "914001", "No Answer", " ", ACCOUNTTRUNK, communication_outRoute);
        }
    }*/
    @Test
    public void I3_checkMissRecord_SPS(){
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <1100>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void I4_checkMissRecord_SPX(){
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }else {
            Reporter.infoExec(" 2000拨打881100通过spx外线呼入到分机1100，而不是呼入到路由目的地——分机1000");
            pjsip.Pj_Make_Call_Auto_Answer(2000, "881100", DEVICE_ASSIST_2,false);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            ys_waitingTime(5000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2000 <2000>", "1100 <1100>", "Answered", SPX, " ", communication_inbound);
        }
    }
    @Test
    public void I5_checkMissRecord_BRI(){
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!BRI_1.equals("null")){
            Reporter.infoExec(" 2000拨打66666通过bri外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2000,"66666",DEVICE_ASSIST_2,false);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            ys_waitingTime(5000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2000 <2000>","1100 <1100>","Answered",BRI_1," ",communication_inbound);
        }
    }
    @Test
    public void I6_checkMissRecord_E1(){
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!E1.equals("null")){
            Reporter.infoExec(" 2000拨打77777通过E1外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2000,"77777",DEVICE_ASSIST_2,false);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            ys_waitingTime(5000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2000 <2000>","1100 <1100>","Answered",E1," ",communication_inbound);
        }
    }
   /* @Test
    public void I7_checkMissRecord_Account(){
        if (!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec(" 4001拨打1111通过account外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(4001, "1111", DEVICE_ASSIST_3,false);
            ys_waitingTime(10000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("4001 <6100>", "1100 <1100>", "Answered", ACCOUNTTRUNK, " ", communication_inbound);
        }
    }*/
    @Test
    public void I8_disMissRecord(){
        Reporter.infoExec(" 取消勾选 只记录未接电话"); //执行操作
        setCheckBox(autoCLIPRoutes.onlyKeepMissedCallRecords,false);
        ys_waitingTime(1000);
        autoCLIPRoutes.save.click();
        ys_waitingMask();
        ys_apply();
    }
    @Test
    public void I9_checkMissRecord1(){
        Reporter.infoExec(" 1100拨打32000通过sps外线呼出到分机2000,分机接听"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32000",DEVICE_IP_LAN,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","32000","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void I9_checkMissRecord2(){
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <1100>","Answered",SPS," ",communication_inbound);
    }
    /*
    * 测试Delete Used Record功能
    * */
    @Test
    public void J1_deleteUsedRecords(){
//        勾选  删除使用过的记录
        Reporter.infoExec(" 勾选  删除使用过的记录"); //执行操作
        ys_waitingMask();
        setCheckBox(autoCLIPRoutes.deleteUsedRecords,true);

        autoCLIPRoutes.save.click();
        ys_waitingMask();
        ys_apply();
    }
    @Test
    public void J2_checkUsedRecord_makeCall(){
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <1100>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void J3_checkUsedRecord_makeCall(){
//        再呼入一次，验证UsedRecord生效
        Reporter.infoExec(" 再呼入一次，验证UsedRecord生效"); //执行操作
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到路由目的地——分机1000，而不是呼入到分机1100"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1000 <1000>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void J4_disUsedRecord(){
        Reporter.infoExec(" 取消勾选 删除使用过的记录"); //执行操作
        ys_waitingMask();
        setCheckBox(autoCLIPRoutes.deleteUsedRecords,false);

        autoCLIPRoutes.save.click();
        ys_waitingMask();
        ys_apply();
    }
    @Test
    public void J5_makeCall_SPS(){
        Reporter.infoExec(" 1100拨打32000通过sps外线呼出到分机2000,分机接听"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32000",DEVICE_IP_LAN,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","32000","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void J6_checkUsedRecord_makeCall(){
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <1100>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void J7_checkUsedRecord_makeCall(){
//        再呼入一次，验证UsedRecord未生效
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <1100>","Answered",SPS," ",communication_inbound);
    }
    /*
    * Delete Used Records、Only Keep Missed Call Records和Match Outgoing Trunk全都勾选的情况下
    * */
    @Test
    public void K1_combine(){
        Reporter.infoExec(" ----所有选项默认----"); //执行操作
        autoCLIPRoutes.viewAutoCLIPList.shouldBe(Condition.exist);
        ys_waitingTime(2000);
        setCheckBox(autoCLIPRoutes.deleteUsedRecords,true);
        setCheckBox(autoCLIPRoutes.onlyKeepMissedCallRecords,true);
        setCheckBox(autoCLIPRoutes.matchOutgoingTrunk,true);
        ys_waitingTime(1000);
        autoCLIPRoutes.viewAutoCLIPList.shouldBe(Condition.exist);
        autoCLIPRoutes.viewAutoCLIPList.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        gridSeleteAll(autoCLIPRoutes.grid);
        autoCLIPRoutes.delete.click();
        if (autoCLIPRoutes.delete_yes.isDisplayed()){
            autoCLIPRoutes.delete_yes.click();
        }
        if (autoCLIPRoutes.delete_ok.isDisplayed()){
            autoCLIPRoutes.delete_ok.click();
        }

        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        autoCLIPRoutes.closeAutoClIP_List();
        ys_waitingTime(2000);
        autoCLIPRoutes.save.click();
        ys_waitingMask();
        ys_apply();
    }
    @Test
    public void K2_makeCall_sps_accept(){
//        勾选了只记录未接电话，所以这通电话不会记录到AutoCLIP上
        Reporter.infoExec(" 1100拨打32000通过sps外线呼出到分机2000,分机接听"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32000",DEVICE_IP_LAN,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","32000","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void K3_checkTrunk_sps(){
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到路由目的地——分机1000，而不是呼入到分机1100"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1000 <1000>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void K4_makeCall1_SPS_noAnswer(){
        //        sps外线测试&&分机2000未接电话
        Reporter.infoExec(" 1100拨打32000通过sps外线呼入到分机2000，分机未接"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"32000",DEVICE_IP_LAN,false);
        if (getExtensionStatus(2000, RING, 8) == RING) {
            Reporter.pass(" 分机2000状态--RING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>", "32000", "No Answer", " ",SPS,  communication_outRoute);
    }
    @Test
    public void K4_makeCall2_SPS_noAnswer(){
        //        sps外线测试&&分机2000未接电话
        Reporter.infoExec(" 1100拨打33001通过sps外线呼入到分机2000，分机未接"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"33001",DEVICE_IP_LAN,false);
        if (getExtensionStatus(2000, RING, 8) == RING) {
            Reporter.pass(" 分机2000状态--RING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>", "33001", "No Answer", " ",SPS,  communication_outRoute);
    }
//    验证Match Outgoing Trunk
    @Test
    public void K5_checkTrunk_sip() throws InterruptedException {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到路由目的地——分机1000，而不是呼入到分机1100"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000 <1000>","Answered",SIPTrunk," ",communication_inbound);
    }
    @Test
    public void K6_checkTrunk_sps(){
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <1100>","Answered",SPS," ",communication_inbound);
    }
//    线路已经被匹配成功，此刻的AutoCLIP中没有任何记录，2000再次呼入，则直接到呼入路由目的地
    @Test
    public void K7_checkUsedRecord(){
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到路由目的地——分机1000，而不是呼入到分机1100"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1000 <1000>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void K8_makeCall_sip_noAnswer(){
        Reporter.infoExec(" 1100拨打13001通过sip外线呼出"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"13001",DEVICE_IP_LAN);
        if (getExtensionStatus(3001, RING, 8) == RING) {
            Reporter.pass(" 分机3001状态--RING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为RING，实际状态为"+getExtensionStatus(3001, RING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","13001","No Answer"," ",SIPTrunk,communication_outRoute);
    }
    @Test
    public void K9_checkTrunk_sip(){
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1100，而不是呼入到路由目的地——分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1100 <1100>","Answered",SIPTrunk," ",communication_inbound);
    }
//    为后面的AutoCLIP删除做准备
    @Test
    public void L_addCallRecord1(){
        //        sps外线测试&&分机2000未接电话
        Reporter.infoExec(" 1100拨打32000通过sps外线呼入到分机2000，分机未接"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"32000",DEVICE_IP_LAN,false);
        if (getExtensionStatus(2000, RING, 8) == RING) {
            Reporter.pass(" 分机2000状态--RING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>", "32000", "No Answer", " ",SPS,  communication_outRoute);
    }
    @Test
    public void L_addCallRecord2(){
        //        sps外线测试&&分机2000未接电话
        Reporter.infoExec(" 1100拨打32000通过sps外线呼入到分机2000，分机未接"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"32001",DEVICE_IP_LAN,false);
        if (getExtensionStatus(2000, RING, 8) == RING) {
            Reporter.pass(" 分机2000状态--RING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>", "32001", "No Answer", " ",SPS,  communication_outRoute);
    }
    @Test
    public void L_addCallRecord3(){
        //        sps外线测试&&分机2000未接电话
        Reporter.infoExec(" 1100拨打32002通过sps外线呼入到分机2002（因为辅助设备有一个呼入路由DID是2002，所以会匹配这条，导致呼入到2002），分机未接"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"32002",DEVICE_IP_LAN,false);
        if (getExtensionStatus(2002, RING, 8) == RING) {
            Reporter.pass(" 分机2002状态--RING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2002状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>", "32002", "No Answer", " ",SPS,  communication_outRoute);
    }
    @Test
    public void L_addCallRecord4(){
        //        sps外线测试&&分机2000未接电话
        Reporter.infoExec(" 1100拨打32003通过sps外线呼入到分机2000，分机未接"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"32003",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, RING, 8) == RING) {
            Reporter.pass(" 分机2000状态--RING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>", "32003", "No Answer", " ",SPS,  communication_outRoute);
    }
    @Test
    public void L_addCallRecord5(){
        //        sps外线测试&&分机2000未接电话
        Reporter.infoExec(" 1100拨打32004通过sps外线呼入到分机2000，分机未接"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"32004",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, RING, 8) == RING) {
            Reporter.pass(" 分机2000状态--RING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>", "32004", "No Answer", " ",SPS,  communication_outRoute);
    }

    @Test
    public void M1_deleteOne_no(){
        autoCLIPRoutes.viewAutoCLIPList.shouldBe(Condition.exist);
        autoCLIPRoutes.viewAutoCLIPList.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);

        Reporter.infoExec(" 删除单个AutoCLIP记录——选择no"); //执行操作
//       定位要删除的那条呼入路由
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(autoCLIPRoutes.grid, autoCLIPRoutes.gridColumn_CallerNumber, "2004", sort_ascendingOrder)));
        System.out.println("要删除的那条row："+row);
        //还未操作前呼出路由的所有条数
        int rows = Integer.parseInt(String.valueOf(gridLineNum(autoCLIPRoutes.grid)));
        System.out.println("预期值:" + rows);
        gridClick(autoCLIPRoutes.grid, row, autoCLIPRoutes.gridColumn_Delete);
        autoCLIPRoutes.delete_no.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(autoCLIPRoutes.grid)));
        System.out.println("实际值:" + row1);
        YsAssert.assertEquals(row1, rows, "删除AutoCLIP记录-取消删除");
    }
    @Test
    public void M2_deleteOne_yes(){
        Reporter.infoExec(" 删除单个AutoCLIP记录——选择yes"); //执行操作
        int rows = Integer.parseInt(String.valueOf(gridLineNum(autoCLIPRoutes.grid)));
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(autoCLIPRoutes.grid, autoCLIPRoutes.gridColumn_CallerNumber, "2004", sort_ascendingOrder)));
        System.out.println("要删除的那条row："+row);
        gridClick(autoCLIPRoutes.grid, row, autoCLIPRoutes.gridColumn_Delete);
        autoCLIPRoutes.delete_yes.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(autoCLIPRoutes.grid)));
        System.out.println("实际值row2:" + row2);
        int row3 = rows - 1;
        System.out.println("期望值row3:" + row3);
        YsAssert.assertEquals(row2, row3, "删除单个AutoCLIP记录——确定删除");
    }
    @Test
    public void M3_deletePart_no(){
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(autoCLIPRoutes.grid)));
        System.out.println("期望值row1:" + row1);
        int row2 = Integer.parseInt(String.valueOf(gridFindRowByColumn(autoCLIPRoutes.grid, autoCLIPRoutes.gridColumn_CallerNumber, "2003", sort_ascendingOrder)));
        System.out.println("row2:"+row2);
        int row3 = Integer.parseInt(String.valueOf(gridFindRowByColumn(autoCLIPRoutes.grid, autoCLIPRoutes.gridColumn_CallerNumber, "2002", sort_ascendingOrder)));
        System.out.println("row3:"+row3);
        //        全部勾选
        System.out.println("gridSeleteAll");
        gridSeleteAll(autoCLIPRoutes.grid);
//        取消勾选2001和2002
        System.out.println("gridCheck_row2");
        gridCheck(autoCLIPRoutes.grid, row2, autoCLIPRoutes.gridColumn_Checked);
        System.out.println("gridCheck_row3");
        gridCheck(autoCLIPRoutes.grid, row3, autoCLIPRoutes.gridColumn_Checked);
//        点击删除按钮
        autoCLIPRoutes.delete.click();
        autoCLIPRoutes.delete_no.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        int row4 = Integer.parseInt(String.valueOf(gridLineNum(autoCLIPRoutes.grid)));
        System.out.println("实际值row4:" + row4);
        YsAssert.assertEquals(row4, row1, "全部勾选，再取消某条的勾选后-取消删除");
    }
    @Test
    public void M4_deltePart_yes(){
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-确定删除"); //执行操作
        autoCLIPRoutes.delete.click();
        autoCLIPRoutes.delete_yes.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(autoCLIPRoutes.grid)));
        System.out.println("实际row6:" + row1);
        YsAssert.assertEquals(row1, 2, "全部勾选，再取消某条的勾选后-确定删除");
    }
    @Test
    public void M5_delteAll_no(){
        Reporter.infoExec(" 全部勾选-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(autoCLIPRoutes.grid)));
//        全部勾选
        gridSeleteAll(autoCLIPRoutes.grid);
//        点击删除按钮
        autoCLIPRoutes.delete.click();
        autoCLIPRoutes.delete_no.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(autoCLIPRoutes.grid)));
        System.out.println("实际row2:" + row2);
        YsAssert.assertEquals(row1, row2, "全部勾选-取消删除");
    }
    @Test
    public void M6_delteAll_yes(){
        Reporter.infoExec(" 全部勾选-确定删除"); //执行操作
        autoCLIPRoutes.delete.click();
        autoCLIPRoutes.delete_yes.click();
        ys_waitingLoading(autoCLIPRoutes.grid_Mask);
        int row = Integer.parseInt(String.valueOf(gridLineNum(autoCLIPRoutes.grid)));
        System.out.println("实际row:" + row);
        YsAssert.assertEquals(row, 0, "全部勾选-确定删除");
    }
    @Test
    public void N_recovery(){
        Reporter.infoExec(" ----所有选项默认----"); //执行操作
        autoCLIPRoutes.closeAutoClIP_List();

        autoCLIPRoutes.viewAutoCLIPList.shouldBe(Condition.exist);
        ys_waitingTime(1000);
        setCheckBox(autoCLIPRoutes.deleteUsedRecords,false);
        setCheckBox(autoCLIPRoutes.onlyKeepMissedCallRecords,false);
        setCheckBox(autoCLIPRoutes.matchOutgoingTrunk,false);
        autoCLIPRoutes.mt_RemoveAllFromSelect.click();
        ys_waitingTime(2000);
        autoCLIPRoutes.save.click();
        ys_waitingMask();
        ys_apply();
    }
    //    AfterMethod是在每个Test执行后都要来执行的方法
    @AfterMethod
    public void AfterMethod(){
        if (cdRandRecordings.deleteCDR.isDisplayed()){
            System.out.println("admin角色的cdr页面未关闭");
            closeCDRRecord();
            System.out.println("admin角色的cdr页面已关闭");
        }
    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：====== AutoCLIP ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);
    }
}
