package com.yeastar.swebtest.testcase.DetailCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.ScreenShot;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.ArrayList;

/**
 * Created by Caroline on 2018/1/8.
 */
public class OutboundRestriction extends SwebDriver {
    BeforeTest beforeTest = new BeforeTest();
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：====== OutboundRestriction ======"); //执行操作
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

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_CreateAccount(1102, "Yeastar202", "UDP", UDP_PORT, 4);
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_CreateAccount(1103, "Yeastar202", "UDP", UDP_PORT, 5);
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_CreateAccount(1104, "Yeastar202", "UDP", UDP_PORT, 6);
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2000, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2001, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);

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
    public void B1_addRestriction(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        outboundRestriction.outboundRestriction.click();
        outboundRestriction.add.should(Condition.exist);

        deletes("  删除所有Outbound Restriction",outboundRestriction.grid,outboundRestriction.delete,outboundRestriction.delete_yes,outboundRestriction.grid_Mask);
        Reporter.infoExec(" 新建呼出限制_?.~12349012345678901！~·……*（）——，2分钟不能超过4通");
        outboundRestriction.add.click();
        ys_waitingMask();
        add_outbound_restriction.name.setValue("_?.~12349012345678901！~·……*（）——");
        add_outbound_restriction.timeLimit.setValue("2");
        add_outbound_restriction.numberofCallsLimit.setValue("4");
        add_outbound_restriction.save.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
        ys_apply();
    }
    @Test
    public void B2_addEmergencyNum(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.emergencyNumber_tree.click();
        deletes("  删除所有紧急号码",emergencyNumber.grid,emergencyNumber.delete,emergencyNumber.delete_yes,emergencyNumber.grid_Mask);
        m_emergencyNumber.addEmergencyNumber(123,SPS,1103);
        ys_apply();
    }
    @Test
    public void C1_checkRestriction(){
        //      通话测试
        for (int i=1;i<=7;i++) {
            if(i==7) {
                ys_waitingTime(80000);
            }
            pjsip.Pj_Make_Call_Auto_Answer(1000, "3333", DEVICE_IP_LAN);
            ys_waitingTime(8000);
            closePbxMonitor();
            System.out.println("=============================第"+i+"次循环打电话========================");
            int state = getExtensionStatus(1000,TALKING,1);
//            呼出限制无法实时生效，大概要1分钟
            if(i==7) {
                if(state == HUNGUP){
                    Reporter.infoExec(" 1000拨打3333通过sps外线呼出,预期第7次呼出失败,实际呼出失败");
                }else{
                    Reporter.infoExec(" 1000拨打3333通过sps外线呼出,预期第7次呼出失败,实际呼出成功");
                    Reporter.infoExec(" 分机1000的通话状态为："+state);
                    pjsip.Pj_Hangup_All();

                    Reporter.infoExec(" 分机1000没有被限制住，为了不影响接下来的test，手动去分机页面勾选呼出限制");
                    settings.extensions_tree.click();
                    extensions.Extensions.click();
                    setPageShowNum(extensions.grid,100);
                    gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Name,"1000",sort_ascendingOrder),extensions.gridEdit);
                    ys_waitingTime(1000);
                    editSelectedExtensionsCallPermission.callPermission.click();
                    ys_waitingTime(1000);
                    setCheckBox(editSelectedExtensionsCallPermission.outboundRoutes_checkbox,true);
                    ys_waitingTime(1000);
                    addExtensionBasic.save.click();
                    ys_waitingLoading(extensions.grid_Mask);
                    ys_apply();

                    YsAssert.fail(" 1000拨打3333通过sps外线呼出,预期第7次呼出失败,实际呼出成功");
                }
            }else {
                if (state == TALKING) {
                    Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
                } else {
                    pageDeskTop.taskBar_Main.click();
                    pageDeskTop.pbxmonitorShortcut.click();
                    ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH + "C1_checkRestriction()第" + i + "次分机1000.jpg");
                    Reporter.sendReport("link", "Error: " + "C1_checkRestriction()调试", SCREENSHOT_PATH + "C1_checkRestriction()第" + i + "次分机1000.jpg");
                    Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + state);
                }
            }
            pjsip.Pj_Hangup_All();
        }
    }
//    分机被限制后，内部分机互打可以成功
    @Test
    public void C2_checkCallOut_internal(){
        pjsip.Pj_Make_Call_Auto_Answer(1000, "1100", DEVICE_IP_LAN, false);
        ys_waitingTime(6000);
        if (getExtensionStatus(1100, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1100状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"C2_checkCallOut_internal()分机1000.jpg");
            Reporter.sendReport("link","Error: " + "C2_checkCallOut_internal()调试", SCREENSHOT_PATH +"C2_checkCallOut_internal()分机1000.jpg");
            Reporter.error(" 预期分机1100状态为TALKING，实际状态为"+getExtensionStatus(1100, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1100 <1100>","Answered"," "," ",communication_internal);
    }
//    分机1000被限制后，通过其他外线也无法成功呼出
    @Test
    public void C3_checkCallOut1_sip(){
        pjsip.Pj_Make_Call_Auto_Answer(1000, "13001", DEVICE_IP_LAN, false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1000拨打13001通过sip外线呼出,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打13001通过sip外线呼出,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打13001通过sip外线呼出,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void C3_checkCallOut2_spx(){
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 1000拨打42000通过spx外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"42000",DEVICE_IP_LAN,false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1000拨打42000通过spx外线呼出,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打42000通过spx外线呼出,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打42000通过spx外线呼出,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void C3_checkCallOut3_iax(){
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 1000拨打23001通过iax外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"23001",DEVICE_IP_LAN,false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1000拨打23001通过iax外线呼出,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打23001通过iax外线呼出,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打23001通过iax外线呼出,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void C3_checkCallOut4_bri(){
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if (!BRI_1.equals("null")) {
            Reporter.infoExec(" 1000拨打62000通过BRI外线呼出"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "62000", DEVICE_IP_LAN,false);
            ys_waitingTime(6000);
            int state = getExtensionStatus(1000,HUNGUP,8);
            if(state == HUNGUP){
                Reporter.infoExec(" 1000拨打62000通过bri外线呼出,预期呼出失败,实际呼出失败");
                pjsip.Pj_Hangup_All();
            }else{
                Reporter.infoExec(" 1000拨打62000通过bri外线呼出,预期呼出失败,实际呼出成功");
                pjsip.Pj_Hangup_All();
                YsAssert.fail(" 1000拨打62000通过bri外线呼出,预期呼出失败,实际呼出成功");
            }
        }
    }
    @Test
    public void C3_checkCallOut5_e1(){
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if (!E1.equals("null")) {
            Reporter.infoExec(" 1000拨打72000通过E1外线呼出"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "72000", DEVICE_IP_LAN,false);
            ys_waitingTime(6000);
            int state = getExtensionStatus(1000,HUNGUP,8);
            if(state == HUNGUP){
                Reporter.infoExec(" 1000拨打72000通过e1外线呼出,预期呼出失败,实际呼出失败");
                pjsip.Pj_Hangup_All();
            }else{
                Reporter.infoExec(" 1000拨打72000通过e1外线呼出,预期呼出失败,实际呼出成功");
                pjsip.Pj_Hangup_All();
                YsAssert.fail(" 1000拨打72000通过e1外线呼出,预期呼出失败,实际呼出成功");
            }
        }
    }
    @Test
    public void C3_checkCallOut6_pstn(){
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if (!FXO_1.equals("null")) {
            Reporter.infoExec(" 1000拨打52000通过PSTN外线呼出"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "52000", DEVICE_IP_LAN,false);
            ys_waitingTime(6000);
            int state = getExtensionStatus(1000,HUNGUP,8);
            if(state == HUNGUP){
                Reporter.infoExec(" 1000拨打52000通过pstn外线呼出,预期呼出失败,实际呼出失败");
                pjsip.Pj_Hangup_All();
            }else{
                Reporter.infoExec(" 1000拨打52000通过pstn外线呼出,预期呼出失败,实际呼出成功");
                pjsip.Pj_Hangup_All();
                YsAssert.fail(" 1000拨打52000通过pstn外线呼出,预期呼出失败,实际呼出成功");
            }
        }
    }
    @Test
    public void C3_checkCallOut7_gsm(){
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if (!GSM.equals("null")) {
            Reporter.infoExec(" 1000拨打辅助设备的GSM号码呼出"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "8"+DEVICE_ASSIST_GSM, DEVICE_IP_LAN,false);
            ys_waitingTime(6000);
            int state = getExtensionStatus(1000,HUNGUP,8);
            if(state == HUNGUP){
                Reporter.infoExec(" 1000拨打\"8\"+DEVICE_ASSIST_GSM通过gsm外线呼出,预期呼出失败,实际呼出失败");
                pjsip.Pj_Hangup_All();
            }else{
                Reporter.infoExec(" 1000拨打\"8\"+DEVICE_ASSIST_GSM通过gsm外线呼出,预期呼出失败,实际呼出成功");
                pjsip.Pj_Hangup_All();
                YsAssert.fail(" 1000拨打\"8\"+DEVICE_ASSIST_GSM通过gsm外线呼出,预期呼出失败,实际呼出成功");
            }
        }
    }
    @Test
    public void C3_checkCallOut8_account(){
        if(!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec(" 1000拨打9111通过account外线呼出"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "9111", DEVICE_IP_LAN, false);
            ys_waitingTime(6000);
            int state = getExtensionStatus(1000, HUNGUP, 8);
            if (state == HUNGUP) {
                Reporter.infoExec(" 1000拨打9111通过account外线呼出,预期呼出失败,实际呼出失败");
                pjsip.Pj_Hangup_All();
            } else {
                Reporter.infoExec(" 1000拨打9111通过account外线呼出,预期呼出失败,实际呼出成功");
                pjsip.Pj_Hangup_All();
                YsAssert.fail(" 1000拨打9111通过account外线呼出,预期呼出失败,实际呼出成功");
            }
        }
    }
//    分机1000被限制后，不影响所有外线的呼入
    @Test
    public void D1_checkCallIn1_sip() throws InterruptedException {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(6000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"D1_checkCallIn1_sip()分机1000.jpg");
            Reporter.sendReport("link","Error: " + "D1_checkCallIn1_sip()调试", SCREENSHOT_PATH +"D1_checkCallIn1_sip()分机1000.jpg");
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000 <1000>","Answered",SIPTrunk," ",communication_inbound);
    }
    @Test
    public void D1_checkCallIn2_sps(){
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(6000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void D1_checkCallIn3_iax(){
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 3001拨打3100通过iax外线呼入到分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3100",DEVICE_ASSIST_1,false);
        ys_waitingTime(6000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000 <1000>","Answered",IAXTrunk," ",communication_inbound);
    }
    @Test
    public void D1_checkCallIn4_spx(){
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }else {
            Reporter.infoExec(" 2001拨打881000通过spx外线呼入到分机1000");
            pjsip.Pj_Make_Call_Auto_Answer(2001, "881000", DEVICE_ASSIST_2,false);
            ys_waitingTime(5000);
            if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>", "1000 <1000>", "Answered", SPX, " ", communication_inbound);
        }
    }
    @Test
    public void D1_checkCallIn5_bri(){
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!BRI_1.equals("null")){
            Reporter.infoExec(" 2001拨打66666通过bri外线呼入到分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2001,"66666",DEVICE_ASSIST_2,false);
            ys_waitingTime(5000);
            if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",BRI_1," ",communication_inbound);

        }
    }
    @Test
    public void D1_checkCallIn6_e1(){
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!E1.equals("null")){
            Reporter.infoExec(" 2001拨打77777通过E1外线呼入到分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2001,"77777",DEVICE_ASSIST_2,false);
            ys_waitingTime(5000);
            if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",E1," ",communication_inbound);
        }
    }
    @Test
    public void D1_checkCallIn7_pstn(){
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!FXO_1.equals("null")){
            Reporter.infoExec(" 2001拨打2010通过fxo外线呼入到分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2001,"2010",DEVICE_ASSIST_2,false);
            ys_waitingTime(5000);
            if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",FXO_1," ",communication_inbound);
        }
    }
    @Test
    public void D1_checkCallIn8_gsm(){
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!GSM.equals("null")){
            Reporter.infoExec(" 2001拨打被测设备的gsm号码呼入到分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2001,DEVICE_TEST_GSM,DEVICE_ASSIST_2,false);
            ys_waitingTime(15000);
            if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR(DEVICE_ASSIST_GSM +" <"+DEVICE_ASSIST_GSM+">","1000 <1000>","Answered",GSM," ",communication_inbound);
        }
    }
    @Test
    public void D1_checkCallIn9_account(){
        if (!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec(" 4001拨打1111通过account外线呼入到分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(4001, "1111", DEVICE_ASSIST_3,false);
            ys_waitingTime(5000);
            if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("4001 <6100>", "1000 <1000>", "Answered", ACCOUNTTRUNK, " ", communication_inbound);
        }
    }
    @Test
    public void D2_checkCallIn1_internal(){
        pjsip.Pj_Make_Call_Auto_Answer(1100, "1000", DEVICE_IP_LAN, false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","1000 <1000>","Answered"," "," ",communication_internal);
    }
    //    紧急号码呼出成功
    @Test
    public void D2_checkCallOut9_EmergencyNum(){
        pjsip.Pj_Make_Call_No_Answer(1000, "123", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1103, RING, 8) == RING) {
            Reporter.pass(" 被通知的分机1103状态--RING");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"D2_checkCallOut9_EmergencyNum()分机1000.jpg");
            Reporter.sendReport("link","Error: " + "D2_checkCallOut9_EmergencyNum()调试", SCREENSHOT_PATH +"D2_checkCallOut9_EmergencyNum()分机1000.jpg");
            Reporter.error(" 预期被通知的分机1103状态为RING，实际状态为"+getExtensionStatus(1100, RING, 8));
        }
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(2000,200,false);
        ys_waitingTime(2000);
        pjsip.Pj_Answer_Call(1103,200,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        ys_waitingTime(2000);//CDR生成可能有时间延迟
        m_extension.checkCDR("1000dial123 <Emergency>","xlq <1103>","Answered"," "," ",communication_internal,1);
        m_extension.checkCDR("1000 <1000>","123","Answered"," ",SPS,communication_outRoute,2);
    }
    @Test
    public void D3_deleteRestriction(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_tree.click();
        ys_waitingTime(1000);
        outboundRestriction.outboundRestriction.click();
        setPageShowNum(outboundRestriction.grid,100);
        Reporter.infoExec(" 表格删除：_?.~12349012345678901！~·……*（）——-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRestriction.grid,outboundRestriction.gridcolumn_Name,"_?.~12349012345678901！~·……*（）——",sort_ascendingOrder)));
        int rows=Integer.parseInt(String.valueOf(gridLineNum(outboundRestriction.grid)));
        gridClick(outboundRestriction.grid,row,outboundRestriction.gridDelete);
        outboundRestriction.delete_no.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(outboundRestriction.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：_?.~12349012345678901！~·……*（）——-取消删除");

        Reporter.infoExec(" 表格删除：_?.~12349012345678901！~·……*（）——-确定删除"); //执行操作
        gridClick(outboundRestriction.grid,row,outboundRestriction.gridDelete);
        outboundRestriction.delete_yes.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
        ys_apply();
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(outboundRestriction.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：_?.~12349012345678901！~·……*（）——-确定删除");
    }
    @Test
    public void D4_checkCallOut_sps(){
        Reporter.infoExec(" 1000拨打3333通过sps外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"3333",DEVICE_IP_LAN,false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1000拨打3333通过sps外线呼出,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打3333通过sps外线呼出,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打3333通过sps外线呼出,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void D5_checkExtension_no(){
        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ys_waitingTime(1000);
        settings.extensions_panel.click();
        ys_waitingTime(5000);
        gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Name,"1000",sort_ascendingOrder),0);
        if(extensions.delete_no.exists()){
            extensions.delete_no.click();
            System.out.println("取消呼出限制_NO");
            Reporter.pass(" 查看分机1000状态已被限制");
        }else {
            addExtensionBasic.cancel.click();
        }
        ys_waitingTime(5000);
    }
    @Test
    public void D6_checkRestriction(){
        Reporter.infoExec(" 1000拨打3333通过sps外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"3333",DEVICE_IP_LAN,false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1000拨打3333通过sps外线呼出,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打3333通过sps外线呼出,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打3333通过sps外线呼出,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void D7_checkExtension_yes(){
        gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Name,"1000",sort_ascendingOrder),0);
        if(extensions.delete_yes.exists()){
            extensions.delete_yes.click();
            System.out.println("取消呼出限制_Yes");
            Reporter.pass(" 查看分机1000状态已被取消限制");
        }else {
            addExtensionBasic.cancel.click();
        }
        ys_waitingTime(5000);
    }
    @Test
    public void D8_checkRestriction(){
        Reporter.infoExec(" 1000拨打32000通过sps外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32000",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","32000","Answered"," ",SPS,communication_outRoute);
    }
//    分机成员选择“已选分机”——分机组ExtensionGroup1和分机1102
    @Test
    public void E1_addRestriction(){
        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        outboundRestriction.outboundRestriction.click();
        outboundRestriction.add.should(Condition.exist);

        deletes("  删除所有Outbound Restriction",outboundRestriction.grid,outboundRestriction.delete,outboundRestriction.delete_yes,outboundRestriction.grid_Mask);
        Reporter.infoExec(" 新建呼出限制OutRestriction1，1分钟不能超过1通");
        outboundRestriction.add.click();
        ys_waitingMask();
        add_outbound_restriction.name.setValue("OutRestriction1");
        add_outbound_restriction.timeLimit.setValue("2");
        add_outbound_restriction.numberofCallsLimit.setValue("1");
        add_outbound_restriction.selectExtensions.click();

        ArrayList<String> extendsion = new ArrayList<>();
        extendsion.add("ExtensionGroup1");
        extendsion.add("1102");
        listSelect(add_outbound_restriction.list,extensionList,extendsion);
        add_outbound_restriction.save.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
        ys_apply();
    }
//    验证呼出规则对分机组里的分机生效
    @Test
    public void E2_Restriction1(){
        //      通话测试
        for (int i=1;i<=4;i++) {
            if(i==4) {
                //            呼出限制无法实时生效，大概要1分钟
                ys_waitingTime(140000);
            }
            pjsip.Pj_Make_Call_Auto_Answer(1000, "3333", DEVICE_IP_LAN,false);
            ys_waitingTime(2000);
            System.out.println("=============================第"+i+"次循环打电话========================");
            int state = getExtensionStatus(1000,TALKING,1);
            if(i==4) {
                if(state == HUNGUP){
                    Reporter.infoExec(" 1000拨打3333通过sps外线呼出,预期第4次呼出失败,实际呼出失败");
                }else{
                    Reporter.infoExec(" 1000拨打3333通过sps外线呼出,预期第4次呼出失败,实际呼出成功");
                    Reporter.infoExec(" 分机1000的通话状态为："+state);
                    pjsip.Pj_Hangup_All();

                    Reporter.infoExec(" 分机1000没有被限制住，为了不影响接下来的test，手动去分机页面勾选呼出限制");
                    settings.extensions_tree.click();
                    extensions.Extensions.click();
                    setPageShowNum(extensions.grid,100);
                    gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Name,"1000",sort_ascendingOrder),extensions.gridEdit);
                    ys_waitingTime(1000);
                    editSelectedExtensionsCallPermission.callPermission.click();
                    ys_waitingTime(1000);
                    setCheckBox(editSelectedExtensionsCallPermission.outboundRoutes_checkbox,true);
                    ys_waitingTime(1000);
                    addExtensionBasic.save.click();
                    ys_waitingLoading(extensions.grid_Mask);
                    ys_apply();

                    YsAssert.fail(" 1000拨打3333通过sps外线呼出,预期第4次呼出失败,实际呼出成功");
                }
            }else {
                if (state == TALKING) {
                    Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
                } else {
                    pageDeskTop.taskBar_Main.click();
                    pageDeskTop.pbxmonitorShortcut.click();
                    ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH + "E2_Restriction1()第" + i + "次分机1000.jpg");
                    Reporter.sendReport("link", "Error: " + "E2_Restriction1()调试", SCREENSHOT_PATH + "E2_Restriction1()第" + i + "次分机1000.jpg");
                    Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + state);
                }
            }
            pjsip.Pj_Hangup_All();
        }
    }
    @Test
    public void E2_Restriction2(){
        //      通话测试
        for (int i=1;i<=4;i++) {
            if(i==4) {
                ys_waitingTime(140000);
            }
            pjsip.Pj_Make_Call_Auto_Answer(1100, "3333", DEVICE_IP_LAN);
            ys_waitingTime(2000);
            closePbxMonitor();
            System.out.println("=============================第"+i+"次循环打电话========================");
            int state = getExtensionStatus(1100,TALKING,1);
//            呼出限制无法实时生效，大概要1分钟
            if(i==4) {
                if(state == HUNGUP){
                    Reporter.infoExec(" 1100拨打3333通过sps外线呼出,预期第4次呼出失败,实际呼出失败");
                }else{
                    Reporter.infoExec(" 1100拨打3333通过sps外线呼出,预期第4次呼出失败,实际呼出成功");
                    Reporter.infoExec(" 分机1100的通话状态为："+state);
                    pjsip.Pj_Hangup_All();

                    Reporter.infoExec(" 分机1100没有被限制住，为了不影响接下来的test，手动去分机页面勾选呼出限制");
                    settings.extensions_tree.click();
                    extensions.Extensions.click();
                    setPageShowNum(extensions.grid,100);
                    gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Name,"1100",sort_ascendingOrder),extensions.gridEdit);
                    ys_waitingTime(1000);
                    editSelectedExtensionsCallPermission.callPermission.click();
                    ys_waitingTime(1000);
                    setCheckBox(editSelectedExtensionsCallPermission.outboundRoutes_checkbox,true);
                    ys_waitingTime(1000);
                    addExtensionBasic.save.click();
                    ys_waitingLoading(extensions.grid_Mask);
                    ys_apply();

                    YsAssert.fail(" 1100拨打3333通过sps外线呼出,预期第4次呼出失败,实际呼出成功");
                }
            }else {
                if (state == TALKING) {
                    Reporter.pass(" 分机1100状态--TALKING，通话正常建立");
                } else {
                    pageDeskTop.taskBar_Main.click();
                    pageDeskTop.pbxmonitorShortcut.click();
                    ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH + "E2_Restriction2()第" + i + "次分机1100.jpg");
                    Reporter.sendReport("link", "Error: " + "E2_Restriction2()调试", SCREENSHOT_PATH + "E2_Restriction2()第" + i + "次分机1100.jpg");
                    Reporter.error(" 预期分机1100状态为TALKING，实际状态为" + state);
                }
            }
            pjsip.Pj_Hangup_All();
        }
    }
//    验证呼出规则对所选择的单个分机生效
    @Test
    public void E2_Restriction3(){
        //      通话测试
        for (int i=1;i<=4;i++) {
            if(i==4) {
                ys_waitingTime(140000);
            }
            pjsip.Pj_Make_Call_Auto_Answer(1102, "3333", DEVICE_IP_LAN);
            ys_waitingTime(2000);
            closePbxMonitor();
            System.out.println("=============================第"+i+"次循环打电话========================");
            int state = getExtensionStatus(1102,TALKING,1);
//            呼出限制无法实时生效，大概要1分钟
            if(i==4) {
                if(state == HUNGUP){
                    Reporter.infoExec(" 1102拨打3333通过sps外线呼出,预期第4次呼出失败,实际呼出失败");
                }else{
                    Reporter.infoExec(" 1102拨打3333通过sps外线呼出,预期第4次呼出失败,实际呼出成功");
                    Reporter.infoExec(" 分机1102的通话状态为："+state);
                    pjsip.Pj_Hangup_All();

                    Reporter.infoExec(" 分机1102没有被限制住，为了不影响接下来的test，手动去分机页面勾选呼出限制");
                    settings.extensions_tree.click();
                    extensions.Extensions.click();
                    setPageShowNum(extensions.grid,100);
                    gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Name,"1102",sort_ascendingOrder),extensions.gridEdit);
                    ys_waitingTime(1000);
                    editSelectedExtensionsCallPermission.callPermission.click();
                    ys_waitingTime(1000);
                    setCheckBox(editSelectedExtensionsCallPermission.outboundRoutes_checkbox,true);
                    ys_waitingTime(1000);
                    addExtensionBasic.save.click();
                    ys_waitingLoading(extensions.grid_Mask);
                    ys_apply();

                    YsAssert.fail(" 1102拨打3333通过sps外线呼出,预期第4次呼出失败,实际呼出成功");
                }
            }else {
                if (state == TALKING) {
                    Reporter.pass(" 分机1102状态--TALKING，通话正常建立");
                } else {
                    pageDeskTop.taskBar_Main.click();
                    pageDeskTop.pbxmonitorShortcut.click();
                    ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH + "E2_Restriction3()第" + i + "次分机1102.jpg");
                    Reporter.sendReport("link", "Error: " + "E2_Restriction3()调试", SCREENSHOT_PATH + "E2_Restriction3()第" + i + "次分机1102.jpg");
                    Reporter.error(" 预期分机1102状态为TALKING，实际状态为" + state);
                }
            }
            pjsip.Pj_Hangup_All();
        }
    }
//    不在“分机选择”里的分机   呼出限制规则不生效
//    如果长时间不操作页面，会被迫退出登录，所以这边通话选择true——去检查pbxmonitor，后面再关闭
    @Test
    public void E2_Restriction4(){
        for (int i=1;i<=3;i++){
            if(i==3) {
                ys_waitingTime(140000);
            }
            pjsip.Pj_Make_Call_Auto_Answer(1103, "3333", DEVICE_IP_LAN);
            ys_waitingTime(2000);
            System.out.println("=============================第"+i+"次循环打电话========================");
            int state = getExtensionStatus(1103,TALKING,1);
//            呼出限制无法实时生效，大概要1分钟
            if(i==3) {
                if(state == TALKING){
                    Reporter.infoExec(" 1103拨打3333通过sps外线呼出,预期呼出成功,实际呼出成功");
                }else{
                    Reporter.infoExec(" 1103拨打3333通过sps外线呼出,预期呼出成功,实际呼出失败");
                    pjsip.Pj_Hangup_All();

                    Reporter.infoExec(" 分机1103被限制住，为了不影响接下来的test，手动去分机页面取消勾选呼出限制");
                    settings.extensions_tree.click();
                    extensions.Extensions.click();
                    setPageShowNum(extensions.grid,100);
                    gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Name,"1103",sort_ascendingOrder),extensions.gridEdit);
                    ys_waitingTime(1000);
                    editSelectedExtensionsCallPermission.callPermission.click();
                    ys_waitingTime(1000);
                    setCheckBox(editSelectedExtensionsCallPermission.outboundRoutes_checkbox,false);
                    ys_waitingTime(1000);
                    addExtensionBasic.save.click();
                    ys_waitingLoading(extensions.grid_Mask);
                    ys_apply();

                    YsAssert.fail(" 1103拨打3333通过sps外线呼出,预期呼出成功,实际呼出失败");
                }
            }else {
                if (state == TALKING) {
                    Reporter.pass(" 分机1103状态--TALKING，通话正常建立");
                } else {
                    pageDeskTop.taskBar_Main.click();
                    pageDeskTop.pbxmonitorShortcut.click();
                    ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH + "E2_Restriction4()分机1103.jpg");
                    Reporter.sendReport("link", "Error: " + "E2_Restriction4()调试", SCREENSHOT_PATH + "E2_Restriction4()分机1103.jpg");
                    Reporter.error(" 预期分机1103状态为TALKING，实际状态为" + state);
                }
            }
            pjsip.Pj_Hangup_All();
        }
        closePbxMonitor();
    }
    @Test
    public void E3_check1_callOut(){
        pjsip.Pj_Make_Call_Auto_Answer(1000, "13001", DEVICE_IP_LAN, false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1000拨打13001通过sip外线呼出,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打13001通过sip外线呼出,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打13001通过sip外线呼出,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void E3_check2_callOut(){
        pjsip.Pj_Make_Call_Auto_Answer(1102, "13001", DEVICE_IP_LAN, false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1102,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1102拨打13001通过sip外线呼出,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1102拨打13001通过sip外线呼出,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1102拨打13001通过sip外线呼出,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void E3_check3_callOut_internal(){
        pjsip.Pj_Make_Call_Auto_Answer(1102, "1100", DEVICE_IP_LAN);
        ys_waitingTime(6000);
        if (getExtensionStatus(1100, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1100状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"E3_check3_callOut_internal()分机1100.jpg");
            Reporter.sendReport("link","Error: " + "E3_check3_callOut_internal()调试", SCREENSHOT_PATH +"E3_check3_callOut_internal()分机1100.jpg");
            Reporter.error(" 预期分机1100状态为TALKING，实际状态为"+getExtensionStatus(1100, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("1102 <1102>","1100 <1100>","Answered"," "," ",communication_internal);
    }
    @Test
    public void E3_check4_callIn(){
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(6000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"E3_check4_callIn()分机1000.jpg");
            Reporter.sendReport("link","Error: " + "E3_check4_callIn()调试", SCREENSHOT_PATH +"E3_check4_callIn()分机1000.jpg");
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000 <1000>","Answered",SIPTrunk," ",communication_inbound);
    }
    @Test
    public void E3_check5_callIn_internal(){
        pjsip.Pj_Make_Call_Auto_Answer(1100, "1102", DEVICE_IP_LAN, false);
        ys_waitingTime(6000);
        if (getExtensionStatus(1100, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1100状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"E3_check5_callIn_internal()分机1100.jpg");
            Reporter.sendReport("link","Error: " + "E3_check5_callIn_internal()调试", SCREENSHOT_PATH +"E3_check5_callIn_internal()分机1100.jpg");
            Reporter.error(" 预期分机1100状态为TALKING，实际状态为"+getExtensionStatus(1100, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","1102 <1102>","Answered"," "," ",communication_internal);
    }
    @Test
    public void E3_check6_EmergencyNum(){
        pjsip.Pj_Make_Call_No_Answer(1000, "123", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1103, RING, 8) == RING) {
            Reporter.pass(" 被通知的分机1103状态--RING");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"E3_check6_EmergencyNum()分机1103.jpg");
            Reporter.sendReport("link","Error: " + "E3_check6_EmergencyNum()调试", SCREENSHOT_PATH +"E3_check6_EmergencyNum()分机1103.jpg");
            Reporter.error(" 预期被通知的分机1103状态为RING，实际状态为"+getExtensionStatus(1103, RING, 8));
        }
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(2000,200,false);
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(1103,200,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000dial123 <Emergency>","xlq <1103>","Answered"," "," ",communication_internal,1);
        m_extension.checkCDR("1000 <1000>","123","Answered"," ",SPS,communication_outRoute,2);
    }
    @Test
    public void E6_deleteExtension(){
//        先取消分机注册，再来删除分机
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1102);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.extensions_tree.click();
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Name,"1000",sort_ascendingOrder)));
        System.out.println("row:"+row);
        gridClick(extensions.grid,row,2);//因为多了一个呼出限制的图标，所以这里删除按钮应该算第2个（从0开始）
        ys_waitingTime(3000);
        extensions.delete_yes.click();
        int row2 = Integer.parseInt(String.valueOf(gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Name,"1102",sort_ascendingOrder)));
        gridClick(extensions.grid,row2,2);
        ys_waitingTime(3000);
        extensions.delete_yes.click();
        ys_waitingLoading(extensions.grid_Mask);
    }
    @Test
    public void E7_1addExtension(){
        Reporter.infoExec(" 添加分机1000和1102");
        m_extension.addSipExtension(1000, "Yeastar202");
        Reporter.infoExec(" 编辑分机1000的邮箱为1000@yeastar.com"); //执行操作
        gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Extensions,"1000",sort_ascendingOrder),extensions.gridEdit);
        ys_waitingMask();
        addExtensionBasic.email.setValue("1000@yeastar.com");
        addExtensionBasic.save.click();
        ys_waitingLoading(extensions.grid_Mask);

        m_extension.addSipExtension(1102, "Yeastar202");
        ys_apply();
    }
    @Test
    public void E7_2editExtensionGroup() throws InterruptedException {
        beforeTest.A4_addExtensionGroup();
    }
    @Test
    public void E7_3editOutRoute() throws InterruptedException {
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        Reporter.infoExec(" 编辑呼出路由OutRoute1_sip");
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute1_sip",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        add_outbound_routes.me_AddAllToSelect.click();
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);

        Reporter.infoExec(" 编辑呼出路由OutRoute3_sps");
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute3_sps",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        add_outbound_routes.me_AddAllToSelect.click();
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);

        if (!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec(" 编辑呼出路由OutRoute9_account");
            gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute9_account",sort_ascendingOrder),outboundRoutes.gridEdit);
            ys_waitingMask();
            add_outbound_routes.me_AddAllToSelect.click();
            add_outbound_routes.save.click();
            ys_waitingLoading(outboundRoutes.grid_Mask);
        }

        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }else {
            Reporter.infoExec(" 编辑呼出路由OutRoute2_iax");
            gridClick(outboundRoutes.grid, gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutRoute2_iax", sort_ascendingOrder), outboundRoutes.gridEdit);
            ys_waitingMask();
            add_outbound_routes.me_AddAllToSelect.click();
            add_outbound_routes.save.click();
            ys_waitingLoading(outboundRoutes.grid_Mask);

            Reporter.infoExec(" 编辑呼出路由OutRoute4_spx");
            gridClick(outboundRoutes.grid, gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutRoute4_spx", sort_ascendingOrder), outboundRoutes.gridEdit);
            ys_waitingMask();
            add_outbound_routes.me_AddAllToSelect.click();
            add_outbound_routes.save.click();
            ys_waitingLoading(outboundRoutes.grid_Mask);

            if(!PRODUCT.equals(PC)){
                if(!FXO_1.equals("null")){
                    Reporter.infoExec(" 编辑呼出路由OutRoute5_fxo");
                    gridClick(outboundRoutes.grid, gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutRoute5_fxo", sort_ascendingOrder), outboundRoutes.gridEdit);
                    ys_waitingMask();
                    add_outbound_routes.me_AddAllToSelect.click();
                    add_outbound_routes.save.click();
                    ys_waitingLoading(outboundRoutes.grid_Mask);
                }
                if(!BRI_1.equals("null")){
                    Reporter.infoExec(" 编辑呼出路由OutRoute6_bri");
                    gridClick(outboundRoutes.grid, gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutRoute6_bri", sort_ascendingOrder), outboundRoutes.gridEdit);
                    ys_waitingMask();
                    add_outbound_routes.me_AddAllToSelect.click();
                    add_outbound_routes.save.click();
                    ys_waitingLoading(outboundRoutes.grid_Mask);
                }
                if(!E1.equals("null")){
                    Reporter.infoExec(" 编辑呼出路由OutRoute7_e1");
                    gridClick(outboundRoutes.grid, gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutRoute7_e1", sort_ascendingOrder), outboundRoutes.gridEdit);
                    ys_waitingMask();
                    add_outbound_routes.me_AddAllToSelect.click();
                    add_outbound_routes.save.click();
                    ys_waitingLoading(outboundRoutes.grid_Mask);
                }
                if(!GSM.equals("null")){
                    Reporter.infoExec(" 编辑呼出路由OutRoute8_gsm");
                    gridClick(outboundRoutes.grid, gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutRoute8_gsm", sort_ascendingOrder), outboundRoutes.gridEdit);
                    ys_waitingMask();
                    add_outbound_routes.me_AddAllToSelect.click();
                    add_outbound_routes.save.click();
                    ys_waitingLoading(outboundRoutes.grid_Mask);
                }
            }
        }
    }
    @Test
    public void E7_4editInRoute()  {
        inboundRoutes.inboundRoutes.click();
        Reporter.infoExec(" 编辑呼入路由InRoute1");
        gridClick(inboundRoutes.grid, gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute1", sort_ascendingOrder), inboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_inbound_route.destinationType,add_inbound_route.s_extensin);
        comboboxSet(add_inbound_route.destination, extensionList,"1000");
        add_inbound_route.save.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        ys_apply();
    }
    @Test
    public void E8_registerExtension(){
//        需要再次重新注册分机信息
        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);
        closePbxMonitor();
    }
//    删除分机后，呼出限制失效，可以成功呼出
    @Test
    public void E9_checkExtension1(){
        Reporter.infoExec(" 1000拨打32000通过sps外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32000",DEVICE_IP_LAN,false);
        ys_waitingTime(6000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","32000","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void E9_checkExtension2(){
        Reporter.infoExec(" 1102拨打32000通过sps外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1102,"32000",DEVICE_IP_LAN,false);
        ys_waitingTime(6000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1102 <1102>","32000","Answered"," ",SPS,communication_outRoute);
    }
//    验证分机组里除了1000外  还是受呼出限制限制
    @Test
    public void E9_checkExtension3(){
        Reporter.infoExec(" 1100拨打32000通过sps外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32000",DEVICE_IP_LAN,false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1100,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1100拨打3333通过sps外线呼出,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1100拨打3333通过sps外线呼出,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1100拨打3333通过sps外线呼出,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void F1_removeExtension(){
//        从呼出限制中移除整个分机组，还是无法解除分机的限制
        settings.callControl_tree.click();
        outboundRestriction.outboundRestriction.click();
        Reporter.infoExec("修改呼出限制的已选分机——1103;3min内不超过1通电话");
        gridClick(outboundRestriction.grid,gridFindRowByColumn(outboundRestriction.grid,outboundRestriction.gridcolumn_Name,"OutRestriction1",sort_ascendingOrder),outboundRestriction.gridEdit);
        ys_waitingMask();
        add_outbound_restriction.timeLimit.setValue("3");
        add_outbound_restriction.numberofCallsLimit.setValue("1");
        add_outbound_restriction.selectExtensions.click();

        ArrayList<String> extendsion = new ArrayList<>();
        extendsion.add("1103");
        listSelect(add_outbound_restriction.list,extensionList,extendsion);
        add_outbound_restriction.save.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
        ys_apply();
    }
    @Test
    public void F2_checkExtension(){
//        虽然移除了分机组，但分机组里的分机1100还是受呼出限制
        Reporter.infoExec(" 1100拨打32000通过sps外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32000",DEVICE_IP_LAN,false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1100,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1100拨打32000通过sps外线呼出,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1100拨打32000通过sps外线呼出,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1100拨打32000通过sps外线呼出,预期呼出失败,实际呼出成功");
        }
    }
    //    编辑分机页面取消限制
    @Test
    public void F3_CancelRestriction(){
        Reporter.infoExec("编辑分机1100——取消勾选呼出限制");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.extensions_tree.click();
        setPageShowNum(extensions.grid,100);
        gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Name,"1100",sort_ascendingOrder),1);//因为有呼出限制图标，所以edit图标变为1
        ys_waitingMask();
        ys_waitingTime(1000);
        editSelectedExtensionsCallPermission.callPermission.click();
        ys_waitingTime(1000);
        setCheckBox(editSelectedExtensionsCallPermission.outboundRoutes_checkbox,false);
        ys_waitingTime(1000);
        addExtensionBasic.save.click();
        ys_waitingLoading(extensions.grid_Mask);
        ys_apply();
    }
    @Test
    public void F4_checkRestriction(){
//        验证分机的限制已被取消
        Reporter.infoExec(" 1100拨打32000通过sps外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32000",DEVICE_IP_LAN,false);
        ys_waitingTime(6000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","32000","Answered"," ",SPS,communication_outRoute);
    }
    //    手动去分机页面勾选呼出限制，验证效果
    @Test
    public void F5_Restriction(){
        Reporter.infoExec("编辑分机1100——勾选呼出限制");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.extensions_tree.click();
        extensions.Extensions.click();
        setPageShowNum(extensions.grid,100);
        gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Name,"1100",sort_ascendingOrder),extensions.gridEdit);
        ys_waitingTime(1000);
        editSelectedExtensionsCallPermission.callPermission.click();
        ys_waitingTime(1000);
        setCheckBox(editSelectedExtensionsCallPermission.outboundRoutes_checkbox,true);
        ys_waitingTime(1000);
        addExtensionBasic.save.click();
        ys_waitingLoading(extensions.grid_Mask);
        ys_apply();
    }
    @Test
    public void F6_checkRestriction(){
//        验证分机已被限制
        Reporter.infoExec(" 1100拨打32000通过sps外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32000",DEVICE_IP_LAN,false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1100,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1100拨打32000通过sps外线呼出,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1100拨打32000通过sps外线呼出,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1100拨打32000通过sps外线呼出,预期呼出失败,实际呼出成功");
        }
    }
    //验证一个分机多条规则的情况

     @Test
     public void G1_addRestriction_Extension(){
         pageDeskTop.taskBar_Main.click();
         pageDeskTop.settingShortcut.click();
         settings.callControl_tree.click();
         outboundRestriction.outboundRestriction.click();
         Reporter.infoExec(" 新建呼出限制OutRestriction2，1分钟不能超过5通");
         outboundRestriction.add.click();
         ys_waitingMask();
         add_outbound_restriction.name.setValue("OutRestriction2");
         add_outbound_restriction.timeLimit.setValue("1");
         add_outbound_restriction.numberofCallsLimit.setValue("5");
         add_outbound_restriction.selectExtensions.click();

         ArrayList<String> extendsion = new ArrayList<>();
         extendsion.add("1103");
         extendsion.add("1104");
         listSelect(add_outbound_restriction.list,extensionList,extendsion);
         add_outbound_restriction.save.click();
         ys_waitingLoading(outboundRestriction.grid_Mask);
         ys_apply();
     }
     @Test
     public void G2_checkRestriction1(){
         //      通话测试
         for (int i=1;i<=3;i++) {
             if(i==3) {
                 ys_waitingTime(180000);
             }
             pjsip.Pj_Make_Call_Auto_Answer(1103, "32000", DEVICE_IP_LAN);
             ys_waitingTime(2000);
             closePbxMonitor();
             System.out.println("=============================第"+i+"次循环打电话========================");
             int state = getExtensionStatus(1103,TALKING,1);
//            呼出限制无法实时生效，大概要1分钟
             if(i==3) {
                 if(state == HUNGUP){
                     Reporter.infoExec(" 1103拨打32000通过sps外线呼出,预期第3次呼出失败,实际呼出失败");
                 }else{
                     Reporter.infoExec(" 1103拨打32000通过sps外线呼出,预期第3次呼出失败,实际呼出成功");
                     Reporter.infoExec(" 分机1103的通话状态为："+state);
                     pjsip.Pj_Hangup_All();

                     Reporter.infoExec(" 分机1103没有被限制住，为了不影响接下来的test，手动去分机页面勾选呼出限制");
                     settings.extensions_tree.click();
                     extensions.Extensions.click();
                     setPageShowNum(extensions.grid,100);
                     gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Name,"1103",sort_ascendingOrder),extensions.gridEdit);
                     ys_waitingTime(1000);
                     editSelectedExtensionsCallPermission.callPermission.click();
                     ys_waitingTime(1000);
                     setCheckBox(editSelectedExtensionsCallPermission.outboundRoutes_checkbox,true);
                     ys_waitingTime(1000);
                     addExtensionBasic.save.click();
                     ys_waitingLoading(extensions.grid_Mask);
                     ys_apply();

                     YsAssert.fail(" 1103拨打32000通过sps外线呼出,预期第3次呼出失败,实际呼出成功");
                 }
             }else {
                 if (state == TALKING) {
                     Reporter.pass(" 分机1103状态--TALKING，通话正常建立");
                 } else {
                     pageDeskTop.taskBar_Main.click();
                     pageDeskTop.pbxmonitorShortcut.click();
                     ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH + "G2_checkRestriction1()第" + i + "次分机1103.jpg");
                     Reporter.sendReport("link", "Error: " + "G2_checkRestriction1()调试", SCREENSHOT_PATH + "G2_checkRestriction1()第" + i + "次分机1103.jpg");
                     Reporter.error(" 预期分机1103状态为TALKING，实际状态为" + state);
                 }
             }
             pjsip.Pj_Hangup_All();
         }
     }
    @Test
    public void G4_addRestriction(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_tree.click();
        outboundRestriction.outboundRestriction.click();
        Reporter.infoExec(" 新建呼出限制OutRestriction3，1分钟不能超过1通");
        outboundRestriction.add.click();
        ys_waitingMask();
        add_outbound_restriction.name.setValue("OutRestriction3");
        add_outbound_restriction.timeLimit.setValue("1");
        add_outbound_restriction.numberofCallsLimit.setValue("1");
        add_outbound_restriction.save.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
        ys_apply();
    }
     @Test
     public void G5_checkRestriction1(){
         //      通话测试
         for (int i=1;i<=3;i++) {
             if(i==3) {
                 ys_waitingTime(80000);
             }
             pjsip.Pj_Make_Call_Auto_Answer(1104, "32000", DEVICE_IP_LAN);
             ys_waitingTime(2000);
             closePbxMonitor();
             System.out.println("=============================第"+i+"次循环打电话========================");
             int state = getExtensionStatus(1104,TALKING,1);
//            呼出限制无法实时生效，大概要1分钟
             if(i==3) {
                 if(state == HUNGUP){
                     Reporter.infoExec(" 1104拨打32000通过sps外线呼出,预期第3次呼出失败,实际呼出失败");
                 }else{
                     Reporter.infoExec(" 1104拨打32000通过sps外线呼出,预期第3次呼出失败,实际呼出成功");
                     Reporter.infoExec(" 分机1104的通话状态为："+state);
                     pjsip.Pj_Hangup_All();

                     Reporter.infoExec(" 分机1104没有被限制住，为了不影响接下来的test，手动去分机页面勾选呼出限制");
                     settings.extensions_tree.click();
                     extensions.Extensions.click();
                     setPageShowNum(extensions.grid,100);
                     gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Name,"1104",sort_ascendingOrder),extensions.gridEdit);
                     ys_waitingTime(1000);
                     editSelectedExtensionsCallPermission.callPermission.click();
                     ys_waitingTime(1000);
                     setCheckBox(editSelectedExtensionsCallPermission.outboundRoutes_checkbox,true);
                     ys_waitingTime(1000);
                     addExtensionBasic.save.click();
                     ys_waitingLoading(extensions.grid_Mask);
                     ys_apply();

                     YsAssert.fail(" 1104拨打32000通过sps外线呼出,预期第3次呼出失败,实际呼出成功");
                 }
             }else {
                 if (state == TALKING) {
                     Reporter.pass(" 分机1104状态--TALKING，通话正常建立");
                 } else {
                     pageDeskTop.taskBar_Main.click();
                     pageDeskTop.pbxmonitorShortcut.click();
                     ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH + "G5_checkRestriction1()第" + i + "次分机1104.jpg");
                     Reporter.sendReport("link", "Error: " + "G5_checkRestriction1()调试", SCREENSHOT_PATH + "G5_checkRestriction1()第" + i + "次分机1104.jpg");
                     Reporter.error(" 预期分机1104状态为TALKING，实际状态为" + state);
                 }
             }
             pjsip.Pj_Hangup_All();
         }
     }
//     手动去分机页面勾选呼出限制，验证效果
     @Test
     public void G6_CancelRestriction1() {
         pageDeskTop.taskBar_Main.click();
         pageDeskTop.settingShortcut.click();
         settings.extensions_tree.click();
         setPageShowNum(extensions.grid, 100);
         gridClick(extensions.grid, gridFindRowByColumn(extensions.grid, extensions.gridcolumn_Name, "1100", sort_ascendingOrder), 0);
         if (extensions.delete_yes.exists()) {
             extensions.delete_yes.click();
             System.out.println("取消呼出限制_Yes");
             Reporter.pass(" 查看分机1100状态已被取消限制");
         } else {
             addExtensionBasic.cancel.click();
         }
         ys_waitingTime(5000);
     }
    @Test
    public void G6_CancelRestriction2() {
        gridClick(extensions.grid, gridFindRowByColumn(extensions.grid, extensions.gridcolumn_Name, "xlq", sort_ascendingOrder), 0);
        if (extensions.delete_yes.exists()) {
            extensions.delete_yes.click();
            System.out.println("取消呼出限制_Yes");
            Reporter.pass(" 查看分机1103状态已被取消限制");
        } else {
            addExtensionBasic.cancel.click();
        }
        ys_waitingTime(5000);
    }
    @Test
    public void G6_CancelRestriction3() {
         gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Name,"xll",sort_ascendingOrder),0);
         if(extensions.delete_yes.exists()){
             extensions.delete_yes.click();
             System.out.println("取消呼出限制_Yes");
             Reporter.pass(" 查看分机1104状态已被取消限制");
         }else {
             addExtensionBasic.cancel.click();
         }
         ys_waitingTime(5000);
     }
     @Test
     public void G7_checkRestriction1(){
//         分机1100、1103、1104取消限制后，能够正常通过外线呼出
         Reporter.infoExec(" 1100拨打32000通过sps外线呼出"); //执行操作
         pjsip.Pj_Make_Call_Auto_Answer(1100,"32000",DEVICE_IP_LAN,false);
         ys_waitingTime(6000);
         if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
             Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
         } else {
             Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
         }
         pjsip.Pj_Hangup_All();
         m_extension.checkCDR("1100 <1100>","32000","Answered"," ",SPS,communication_outRoute);
     }
    @Test
    public void G7_checkRestriction2(){
//         分机1100、1103、1104取消限制后，能够正常通过外线呼出
        Reporter.infoExec(" 1103拨打32000通过sps外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1103,"32000",DEVICE_IP_LAN,false);
        ys_waitingTime(6000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("xlq <1103>","32000","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void G7_checkRestriction3(){
//         分机1100、1103、1104取消限制后，能够正常通过外线呼出
        Reporter.infoExec(" 1104拨打32000通过sps外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1104,"32000",DEVICE_IP_LAN,false);
        ys_waitingTime(6000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("xll <1104>","32000","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void H1_deletePart_no(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_tree.click();
        outboundRestriction.outboundRestriction.click();
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(outboundRestriction.grid)));
        System.out.println("期望值row1:" + row1);
        int row2 = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRestriction.grid, outboundRestriction.gridcolumn_Name, "OutRestriction2", sort_ascendingOrder)));
//        全部勾选
        gridSeleteAll(outboundRestriction.grid);
//        取消勾选OutboundRoute2和OutboundRoute3
        gridCheck(outboundRestriction.grid, row2, outboundRestriction.gridcolumn_Check);
//        点击删除按钮
        outboundRestriction.delete.click();
        outboundRestriction.delete_no.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
        int row4 = Integer.parseInt(String.valueOf(gridLineNum(outboundRestriction.grid)));
        System.out.println("实际值row4:" + row4);
        YsAssert.assertEquals(row4, row1, "全部勾选，再取消某条的勾选后-取消删除");
    }
    @Test
    public void H2_deletePart_yes(){
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-确定删除"); //执行操作
        outboundRestriction.delete.click();
        outboundRestriction.delete_yes.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(outboundRestriction.grid)));
        System.out.println("实际row6:" + row1);
        ys_apply();
        YsAssert.assertEquals(row1, 1, "全部勾选，再取消某条的勾选后-确定删除");
    }
    @Test
    public void H3_deleteAll_no(){
        Reporter.infoExec(" 全部勾选-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(outboundRestriction.grid)));
//        全部勾选
        gridSeleteAll(outboundRestriction.grid);
//        点击删除按钮
        outboundRestriction.delete.click();
        outboundRestriction.delete_no.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(outboundRestriction.grid)));
        System.out.println("实际row2:" + row2);
        YsAssert.assertEquals(row1, row2, "全部勾选-取消删除");
    }
    @Test
    public void H4_deleteAll_yes(){
        Reporter.infoExec(" 全部勾选-确定删除"); //执行操作
        outboundRestriction.delete.click();
        outboundRestriction.delete_yes.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
        int row = Integer.parseInt(String.valueOf(gridLineNum(outboundRestriction.grid)));
        System.out.println("实际row:" + row);
        ys_apply();
        YsAssert.assertEquals(row, 0, "全部勾选-确定删除");
    }
    @Test
    public void I_recovery1(){
        Reporter.infoExec(" 新建呼出限制default，1分钟不能超过5通");
        outboundRestriction.add.click();
        ys_waitingMask();
        add_outbound_restriction.name.setValue("default");
        add_outbound_restriction.timeLimit.setValue("1");
        add_outbound_restriction.numberofCallsLimit.setValue("5");
        add_outbound_restriction.save.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
    }
    @Test
    public void I_recovery2(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.emergencyNumber_tree.click();
        Reporter.infoExec(" EmergencyNumber全部勾选-确定删除"); //执行操作
//        全部勾选
        gridSeleteAll(emergencyNumber.grid);
//        点击删除按钮
        emergencyNumber.delete.click();
        emergencyNumber.delete_yes.click();
        ys_waitingLoading(emergencyNumber.grid_Mask);
        ys_apply();
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(emergencyNumber.grid)));
        System.out.println("实际row2:" + row2);
        YsAssert.assertEquals(row2, 0, "全部勾选-确定删除");
    }
    @Test
    public void I_recovery3() throws InterruptedException {
        beforeTest.H_addivr();
    }
    @Test
    public void I_recovery4() throws InterruptedException {
        beforeTest.I_addringgroup();
    }
    @Test
    public void I_recovery5() throws InterruptedException {
        beforeTest.J_addqueue();
    }
    @Test
    public void I_recovery6() throws InterruptedException {
        beforeTest.L_addcallback();
    }
    @Test
    public void I_recovery7() throws InterruptedException {
        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.recording_panel.click();
        ys_waitingMask();
        Reporter.infoExec(" 选择全部外线、分机、会议室进行录音");
        ArrayList<String> arrayex = new ArrayList<>();
        arrayex.add("all");
        ArrayList<String> arraytrunk = new ArrayList<>();
        arraytrunk.add("all");
        ArrayList<String> arraycon = new ArrayList<>();
        arraycon.add("all");
        m_storage.selectRecord(arraytrunk, arrayex, arraycon);
    }
    //    AfterMethod是在每个Test执行后都要来执行的方法
    @AfterMethod
    public void AfterMethod(){
        if (cdRandRecordings.deleteCDR.isDisplayed()){
            System.out.println("admin角色的cdr页面未关闭");
            closeCDRRecord();
            System.out.println("admin角色的cdr页面已关闭");
        }
        if (pbxMonitor.extension.isDisplayed()){
            System.out.println("admin角色的PBXMonitor页面未关闭");
            closePbxMonitor();
            System.out.println("admin角色的PBXMonitor页面已关闭");
        }
    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：====== OutboundRestriction ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);
    }
}
