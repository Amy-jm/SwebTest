package com.yeastar.swebtest.testcase.DetailCase.pbxcase;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.pobject.CDRandRecordings.CDRandRecordings;
import com.yeastar.swebtest.tools.ScreenShot;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.apache.commons.io.FileUtils;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.yeastar.swebtest.tools.ScreenShot.takeScreenshot;

/**
 * Created by Caroline on 2018/1/11.
 * 主测设备不要装linkus，否则会影响centerSetting页面中EmergencyNumber的按钮获取
 * Cloud预安装linkus就不用卸载了
 */
public class EmergencyNumber extends SwebDriver {
    String[] version = DEVICE_VERSION.split("\\.");

    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：====== Emergency Number ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }
    @Test
    public void A1_registerExtensions(){
        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_CreateAccount(1000, "Yeastar202", "UDP", UDP_PORT, 1);
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_CreateAccount(1100, "Yeastar202", "UDP", UDP_PORT, 2);
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_CreateAccount(1101, "Yeastar202", "UDP", UDP_PORT, 2);
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_CreateAccount(1102, "Yeastar202", "UDP", UDP_PORT, 4);
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_CreateAccount(1103, "Yeastar202", "UDP", UDP_PORT, 5);
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2000, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2001, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备1注册分机3001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3001, "Yeastar202", -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3001, DEVICE_ASSIST_1);

        Reporter.infoExec(" 辅助设备1注册分机3002"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3002, "Yeastar202", -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3002, DEVICE_ASSIST_1);

        Reporter.infoExec(" 辅助设备1注册分机3004"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3004, "Yeastar202", -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3004, DEVICE_ASSIST_1);

        Reporter.infoExec(" 辅助设备3注册分机4000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 4000, "Yeastar202", -1, DEVICE_ASSIST_3, UDP_PORT_ASSIST_3);
        pjsip.Pj_Register_Account_WithoutAssist(4000, DEVICE_ASSIST_3);
        closePbxMonitor();
    }

    @Test
    public void A2_addUnavailTrunk() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.trunks_panel.click();
        Reporter.infoExec(" 添加不可用的sip外线SIP4");
        m_trunks.addUnavailTrunk("SIP",add_voIP_trunk_basic.VoipTrunk,"SIP4",DEVICE_ASSIST_1,String.valueOf(UDP_PORT_ASSIST_1),DEVICE_ASSIST_1,"1","1","1","Yeastar",false);
    }
    @Test
    public void B1_addEmergencyNum(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.emergencyNumber_tree.click();
        deletes("  删除所有紧急号码",emergencyNumber.grid,emergencyNumber.delete,emergencyNumber.delete_yes,emergencyNumber.grid_Mask);
        Reporter.infoExec(" 添加紧急号码2000，通过SPS外线，通知人为1100");
        m_emergencyNumber.addEmergencyNumber(2000,SPS,1100);
        ys_apply();
    }
    @Test
    public void B2_makeCall() throws InterruptedException {
        Reporter.infoExec(" 验证紧急号码可正常使用，分机1000拨打2000，预期1100响铃");
//        验证紧急号码可正常使用
        pjsip.Pj_Make_Call_No_Answer(1000, "2000", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1100, RING, 8) == RING && getExtensionStatus(2000, RING, 8) == RING) {
            Reporter.pass(" 被通知的分机1100和分机2000状态--RING");
        } else {
            Reporter.error(" 预期被通知的分机1100状态为RING，实际状态为"+getExtensionStatus(1100, RING, 8)+"\n");
            Reporter.error(" 预期被通知的分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
        }
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(2000,200,false);
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(1100,200,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000dial2000 <Emergency>","1100 <1100>","Answered"," "," ",communication_internal,1);
        m_extension.checkCDR("1000 <1000>","2000","Answered"," ",SPS,communication_outRoute,2);
    }
    @Test
    public void B3_editEmergencyNumb(){
        Reporter.infoExec(" 编辑紧急号码，2000修改为001，trunk前缀为3，将SPS改为SIP，Notification改为1000分机");
        gridClick(emergencyNumber.grid,gridFindRowByColumn(emergencyNumber.grid,emergencyNumber.gridColumn_EmergencyNumber,"2000",sort_ascendingOrder),emergencyNumber.gridEdit);
        ys_waitingMask();
        add_emergency_number.emergencyNumber.setValue("001");
        ys_waitingTime(2000);
        add_emergency_number.trunkPrepend.setValue("3");
        ys_waitingTime(1000);

        listSelect(add_emergency_number.getTrunkSelect(0),trunkList,SIPTrunk);
        listSelect(add_emergency_number.getAdminSelect(0),extensionList,"1000");

        add_emergency_number.save.click();
        ys_waitingLoading(emergencyNumber.grid_Mask);
        ys_apply();
    }
    @Test
    public void B4_makeCall1(){
        Reporter.infoExec(" 1100拨打无效的紧急号码2000，预期呼出失败，分机1100为Hungup");
//        拨打无效的紧急号码
        pjsip.Pj_Make_Call_No_Answer(1100, "2000", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1100, HUNGUP, 8) == HUNGUP) {
            Reporter.pass(" 分机1100状态--Hungup");
        } else {
            Reporter.error(" 预期1100状态为Hungup，实际状态为"+getExtensionStatus(1100, HUNGUP, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
    }
    @Test
    public void B4_makeCall2(){
        Reporter.pass(" 分机1100通过SIP拨打001号码，预期3001响铃");
        pjsip.Pj_Make_Call_No_Answer(1100, "001", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1000, RING, 8) == RING && getExtensionStatus(3001, RING, 8) == RING) {
            Reporter.pass(" 被通知的分机1000和分机3001状态--RING");
        } else {
            Reporter.error(" 预期被通知的分机1000状态为RING，实际状态为"+getExtensionStatus(1000, RING, 8));
            Reporter.error(" 预期被通知的分机3001状态为RING，实际状态为"+getExtensionStatus(3001, RING, 8));
        }
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(3001,200,false);
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(1000,200,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100dial001 <Emergency>","1000 <1000>","Answered"," "," ",communication_internal,1);
        m_extension.checkCDR("1100 <1100>","001","Answered"," ",SIPTrunk,communication_outRoute,2);
    }
    @Test
    public void C1_edittrunk_Unavail(){
        Reporter.infoExec(" 编辑紧急号码001，号码改为2000，删除trunk前缀，线路改为SIP4，通知人为1100分机");
        gridClick(emergencyNumber.grid,gridFindRowByColumn(emergencyNumber.grid,emergencyNumber.gridColumn_EmergencyNumber,"001",sort_ascendingOrder),emergencyNumber.gridEdit);
        ys_waitingMask();
        add_emergency_number.emergencyNumber.setValue("2000");
        ys_waitingTime(2000);
        add_emergency_number.trunkPrepend.clear();
        ys_waitingTime(1000);

        listSelect(add_emergency_number.getTrunkSelect(0),trunkList,"SIP4");
        listSelect(add_emergency_number.getAdminSelect(0),extensionList,"1100");
        add_emergency_number.save.click();
        ys_waitingLoading(emergencyNumber.grid_Mask);
        ys_apply();
    }
    @Test
    public void C2_checkUnavailTrunk(){
        Reporter.infoExec(" 分机1000拨打2000，预期分机1100响铃");
        pjsip.Pj_Make_Call_No_Answer(1000, "2000", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1100, RING, 8) == RING) {
            Reporter.pass(" 被通知的分机1100状态--RING");
        } else {
            Reporter.error(" 预期被通知的分机1100状态为RING，实际状态为"+getExtensionStatus(1100, RING, 8));
        }
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(1100,200,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000dial2000 <Emergency>","1100 <1100>","Answered"," "," ",communication_internal,1);
    }
    @Test
    public void C3_addTrunk(){
        Reporter.infoExec(" 编辑紧急号码2000，号码改为1234567890123456789012345678901，呼出前缀为#*+0123456789qw，trunk顺序为：SIP4、SPS、SIP；通知人为1000");
        gridClick(emergencyNumber.grid,gridFindRowByColumn(emergencyNumber.grid,emergencyNumber.gridColumn_EmergencyNumber,"2000",sort_ascendingOrder),emergencyNumber.gridEdit);
        ys_waitingMask();
//    验证EmergencyNumber长度
        add_emergency_number.emergencyNumber.setValue("1234567890123456789012345678901");
        add_emergency_number.trunkPrepend.setValue("#*+0123456789qw");
//        点击+号进行Trunk的添加
        add_emergency_number.getAddTrunk(1).click();
        ys_waitingTime(2000);
        add_emergency_number.getAddTrunk(2).click();
        ys_waitingTime(2000);
        listSelect(add_emergency_number.getTrunkSelect(0),trunkList,"SIP4");
        ys_waitingTime(1000);
        listSelect(add_emergency_number.getTrunkSelect(1),trunkList,SPS);
        ys_waitingTime(1000);
        listSelect(add_emergency_number.getTrunkSelect(2),trunkList,SIPTrunk);
        ys_waitingTime(1000);
        listSelect(add_emergency_number.getAdminSelect(0),extensionList,"1000");
        add_emergency_number.save.click();
        ys_waitingLoading(emergencyNumber.grid_Mask);
        ys_apply();
    }
    @Test
    public void C4_checkTrunk1(){
        Reporter.infoExec(" 分机1100拨打1234567890123456789012345678901，预期被通知分机1000响铃");
        pjsip.Pj_Make_Call_No_Answer(1100, "1234567890123456789012345678901", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1000, RING, 8) == RING && getExtensionStatus(2000, RING, 8) == RING) {
            Reporter.pass(" 被通知的分机1000和分机2000状态--RING");
        } else {
            Reporter.error(" 预期被通知的分机1000状态为RING，实际状态为"+getExtensionStatus(1000, RING, 8));
            Reporter.error(" 预期被通知的分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
        }
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(2000,200,false);
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(1000,200,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100dial1234567890123456789012345678901 <Emergency>","1000 <1000>","Answered"," "," ",communication_internal,1);
        m_extension.checkCDR("1100 <1100>","1234567890123456789012345678901","Answered"," ",SPS,communication_outRoute,2);
//        CDR容易误报，可能前后两行出现顺序不同
    }
    @Test
    public void C4_checkTrunk2(){
        Reporter.infoExec(" 分机1100先通过SPS外线呼出，让SPS外线处于正忙状态");
//        分机1100先通过SPS外线呼出，SPS外线正忙
        pjsip.Pj_Make_Call_Auto_Answer(1100, "32000", DEVICE_IP_LAN, false);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING ) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        ys_waitingTime(7000);
        Reporter.infoExec(" 接着分机1102也通过SPS外线呼出，分机1100不会被迫挂断电话，软件层面会再新增一条通道让1102通过SPS呼出；预期分机1100为TALKING状态，分机1000为RING");
//        接着分机1102也通过SPS外线呼出，分机1100不会被迫挂断电话，软件层面会再新增一条通道让1102通过SPS呼出
        pjsip.Pj_Make_Call_No_Answer(1102, "1234567890123456789012345678901", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1100, TALKING, 8) == TALKING ) {
            Reporter.pass(" 分机1100状态--TALKING");
        } else {
            Reporter.error(" 预期1100状态为TALKING，实际状态为"+getExtensionStatus(1100, TALKING, 8));
        }
        if (getExtensionStatus(1000, RING, 8) == RING) {
            Reporter.pass(" 被通知的分机1000状态--RING");
        } else {
            Reporter.error(" 预期被通知的分机1000状态为RING，实际状态为"+getExtensionStatus(1000, RING, 8));
        }
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(2000,200,false);
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(1000,200,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1102dial1234567890123456789012345678901 <Emergency>","1000 <1000>","Answered"," "," ",communication_internal,1);
        m_extension.checkCDR("1102 <1102>","1234567890123456789012345678901","Answered"," ",SPS,communication_outRoute,2);
        m_extension.checkCDR("1100 <1100>","32000","Answered"," ",SPS,communication_outRoute,3);
    }
    @Test
    public void C5_deleteTrunk_addNotification(){
//        删除其中一条trunk 并且新增一个Notification
        Reporter.infoExec(" 编辑紧急号码，号码改为3001，删除一条Trunk——SIP，新增一个通知人1101");
        gridClick(emergencyNumber.grid,gridFindRowByColumn(emergencyNumber.grid,emergencyNumber.gridColumn_EmergencyNumber,"1234567890123456789012345678901",sort_ascendingOrder),emergencyNumber.gridEdit);
        ys_waitingMask();
        add_emergency_number.trunkPrepend.clear();
        add_emergency_number.emergencyNumber.setValue("3001");
        add_emergency_number.getDeleteTrunk(2).click();
        ys_waitingTime(1000);
        add_emergency_number.getAddNotification(1).click();
        ys_waitingTime(1000);
        listSelect(add_emergency_number.getAdminSelect(1),extensionList,"1101");
        add_emergency_number.save.click();
        ys_waitingLoading(emergencyNumber.grid_Mask);
        ys_apply();
    }
    @Test
    public void C6_checkTrunk(){
        Reporter.infoExec(" 分机1100拨打3001，预期分机1000和1101为RING状态");
        pjsip.Pj_Make_Call_No_Answer(1100, "3001", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1000, RING, 8) == RING && getExtensionStatus(1101, RING, 8) == RING && getExtensionStatus(3001, RING, 8) == RING) {
            Reporter.pass(" 被通知的分机1000状态和1101状态--RING，分机3001的状态也为RING");
        } else {
            Reporter.error(" 预期被通知的分机1000状态为RING，实际状态为"+getExtensionStatus(1000, RING, 8));
            Reporter.error(" 预期被通知的分机1101状态为RING，实际状态为"+getExtensionStatus(1101, RING, 8));
            Reporter.error(" 预期被通知的分机3001状态为RING，实际状态为"+getExtensionStatus(3001, RING, 8));
        }
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(3001,200,false);
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(1000,200,false);
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(1101,200,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100dial3001 <Emergency>","1101 <1101>","Answered"," "," ",communication_internal,1);
        m_extension.checkCDR("1100dial3001 <Emergency>","1000 <1000>","Answered"," "," ",communication_internal,2);
        m_extension.checkCDR("1100 <1100>","3001","Answered"," ",SIPTrunk,communication_outRoute,3);
    }
    @Test
    public void C7_deleteNotification(){
//        删除一个Notification
        Reporter.infoExec(" 编辑紧急号码，删除通知人1101");
        gridClick(emergencyNumber.grid,gridFindRowByColumn(emergencyNumber.grid,emergencyNumber.gridColumn_EmergencyNumber,"3001",sort_ascendingOrder),emergencyNumber.gridEdit);
        ys_waitingMask();
        add_emergency_number.getDeleteNotification(2).click();
        ys_waitingTime(1000);
        add_emergency_number.save.click();
        ys_waitingLoading(emergencyNumber.grid_Mask);
        ys_apply();
    }
    @Test
    public void C8_checkNotification(){
        Reporter.infoExec(" 分机1100拨打3001，预期分机1000响铃");
        pjsip.Pj_Make_Call_No_Answer(1100, "3001", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1000, RING, 8) == RING) {
            Reporter.pass(" 被通知的分机1000和分机3001状态--RING");
        } else {
            Reporter.error(" 预期被通知的分机1000状态为RING，实际状态为"+getExtensionStatus(1000, RING, 8));
            Reporter.error(" 预期被通知的分机3001状态为RING，实际状态为"+getExtensionStatus(3001, RING, 8));
        }
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(3001,200,false);
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(1000,200,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100dial3001 <Emergency>","1000 <1000>","Answered"," "," ",communication_internal,1);
        m_extension.checkCDR("1100 <1100>","3001","Answered"," ",SIPTrunk,communication_outRoute,2);
//    CDR可能会误报，前后顺序不同
    }
    /*
    * 验证紧急号码可以通过各物理外线正常呼出
    * */
    @Test
    public void D1_addEmergency_line(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.emergencyNumber_tree.click();
        Reporter.infoExec(" 验证紧急号码可以通过各物理外线正常呼出，添加经过各物理外线呼出的紧急号码");
        ys_waitingTime(1000);
        Reporter.infoExec(" 添加SPS");
        m_emergencyNumber.addEmergencyNumber(111,SPS,1100);
//        ACCOUNT不支持紧急号码
        /*if (!DEVICE_ASSIST_3.equals("null")) {
            m_emergencyNumber.addEmergencyNumber(9111,ACCOUNTTRUNK,1100);
        }*/
        if (!PRODUCT.equals(CLOUD_PBX)){
            Reporter.infoExec(" 添加IAX和SPX");
            m_emergencyNumber.addEmergencyNumber(3004,IAXTrunk,1100);
            m_emergencyNumber.addEmergencyNumber(444,SPX,1100);
        }
        if (PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(!FXO_1.equals("null")){
            Reporter.infoExec(" 添加FXO_1");
            m_emergencyNumber.addEmergencyNumber(2001,FXO_1,1100);
        }
        if(!BRI_1.equals("null")){
            Reporter.infoExec(" 添加BRI");
            m_emergencyNumber.addEmergencyNumber(666,BRI_1,1100);
        }
        if(!E1.equals("null")){
            Reporter.infoExec(" 添加E1");
            m_emergencyNumber.addEmergencyNumber(777,E1,1100);
        }
        if(!GSM.equals("null")){
            Reporter.infoExec(" 添加GSM");
            emergencyNumber.add.click();
            add_emergency_number.emergencyNumber.setValue(DEVICE_ASSIST_GSM);
            ys_waitingTime(2000);
            listSelect(add_emergency_number.getTrunkSelect(0),trunkList,GSM);
            listSelect(add_emergency_number.getAdminSelect(0),extensionList,"1100");

            add_emergency_number.save.click();
            ys_waitingLoading(emergencyNumber.grid_Mask);
        }
        ys_apply();
    }
//    ACCOUNT不支持紧急号码
   /* @Test
    public void D2_check_account(){
        if (!DEVICE_ASSIST_3.equals("null")) {
            pjsip.Pj_Make_Call_No_Answer(1000, "9111", DEVICE_IP_LAN, false);
            if (getExtensionStatus(1100, RING, 8) == RING) {
                Reporter.pass(" 被通知的分机1100状态--RING");
            } else {
                Reporter.error(" 预期被通知的分机1100状态为RING，实际状态为"+getExtensionStatus(1100, RING, 8));
            }
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(4000,200,false);
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(1100,200,false);
            ys_waitingTime(8000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000dial9111 <Emergency>","1100 <1100>","Answered"," "," ",communication_internal,1);
            m_extension.checkCDR("1000 <1000>","9111","Answered"," ",ACCOUNTTRUNK,communication_outRoute,2);
        }
    }*/
    @Test
    public void D3_check_iax(){
        if (!PRODUCT.equals(CLOUD_PBX)){
            Reporter.pass(" 分机1000通过IAX拨打3004号码，预期3004分机响铃");
            pjsip.Pj_Make_Call_No_Answer(1000, "3004", DEVICE_IP_LAN, false);
            if (getExtensionStatus(1100, RING, 8) == RING && getExtensionStatus(3004, RING, 8) == RING) {
                Reporter.pass(" 被通知的分机1100和分机3004状态--RING");
            } else {
                Reporter.error(" 预期被通知的分机1100状态为RING，实际状态为"+getExtensionStatus(1100, RING, 8));
                Reporter.error(" 预期被通知的分机3001状态为RING，实际状态为"+getExtensionStatus(3004, RING, 8));
            }
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(3004,200,false);
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(1100,200,false);
            ys_waitingTime(8000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000dial3004 <Emergency>","1100 <1100>","Answered"," "," ",communication_internal,1);
            m_extension.checkCDR("1000 <1000>","3004","Answered"," ",IAXTrunk,communication_outRoute,2);
        }
    }
    @Test
    public void D4_check_spx(){
        if (!PRODUCT.equals(CLOUD_PBX)){
            Reporter.pass(" 分机1000通过SPX拨打444号码，预期2000分机响铃");
            pjsip.Pj_Make_Call_No_Answer(1000, "444", DEVICE_IP_LAN);
            if (getExtensionStatus(1100, RING, 8) == RING && getExtensionStatus(2000, RING, 8) == RING) {
                Reporter.pass(" 被通知的分机1100和分机2000状态--RING");
            } else {
                Reporter.error(" 预期被通知的分机1100状态为RING，实际状态为"+getExtensionStatus(1100, RING, 8));
                Reporter.error(" 预期被通知的分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
            }
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(2000,200,false);
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(1100,200,false);
            ys_waitingTime(8000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000dial444 <Emergency>","1100 <1100>","Answered"," "," ",communication_internal,1);
            m_extension.checkCDR("1000 <1000>","444","Answered"," ",SPX,communication_outRoute,2);
        }
    }
    @Test
    public void D5_check_pstn(){
        if(!FXO_1.equals("null")){
            Reporter.pass(" 分机1000通过PSTN拨打2001号码，预期2001分机响铃");
            pjsip.Pj_Make_Call_No_Answer(1000, "2001", DEVICE_IP_LAN, false);
            if (getExtensionStatus(1100, RING, 8) == RING && getExtensionStatus(2001, RING, 8) == RING) {
                Reporter.pass(" 被通知的分机1100和分机2001状态--RING");
            } else {
                Reporter.error(" 预期被通知的分机1100状态为RING，实际状态为"+getExtensionStatus(1100, RING, 8));
                Reporter.error(" 预期被通知的分机2001状态为RING，实际状态为"+getExtensionStatus(2001, RING, 8));
            }
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(2001,200,false);
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(1100,200,false);
            ys_waitingTime(8000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000dial2001 <Emergency>","1100 <1100>","Answered"," "," ",communication_internal,1);
            m_extension.checkCDR("1000 <1000>","2001","Answered"," ",FXO_1,communication_outRoute,2);
        }
    }
    @Test
    public void D5_check_bri(){
        if(!BRI_1.equals("null")){
            Reporter.pass(" 分机1000通过BRI拨打666号码，预期2000分机响铃");
            pjsip.Pj_Make_Call_No_Answer(1000, "666", DEVICE_IP_LAN, false);
            if (getExtensionStatus(1100, RING, 8) == RING && getExtensionStatus(2000, RING, 8) == RING) {
                Reporter.pass(" 被通知的分机1100和分机2000状态--RING");
            } else {
                Reporter.error(" 预期被通知的分机1100状态为RING，实际状态为"+getExtensionStatus(1100, RING, 8));
                Reporter.error(" 预期被通知的分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
            }
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(2000,200,false);
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(1100,200,false);
            ys_waitingTime(8000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000dial666 <Emergency>","1100 <1100>","Answered"," "," ",communication_internal,1);
            m_extension.checkCDR("1000 <1000>","666","Answered"," ",BRI_1,communication_outRoute,2);
        }
    }
    @Test
    public void D6_check_e1(){
        if(!E1.equals("null")){
            Reporter.pass(" 分机1000通过E1拨打777号码，预期2000分机响铃");
            pjsip.Pj_Make_Call_No_Answer(1000, "777", DEVICE_IP_LAN);
            if (getExtensionStatus(1100, RING, 8) == RING && getExtensionStatus(2000, RING, 8) == RING) {
                Reporter.pass(" 被通知的分机1100和分机2000状态--RING");
            } else {
                Reporter.error(" 预期被通知的分机1100状态为RING，实际状态为"+getExtensionStatus(1100, RING, 8));
                Reporter.error(" 预期被通知的分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
            }
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(2000,200,false);
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(1100,200,false);
            ys_waitingTime(8000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000dial777 <Emergency>","1100 <1100>","Answered"," "," ",communication_internal,1);
            m_extension.checkCDR("1000 <1000>","777","Answered"," ",E1,communication_outRoute,2);
        }
    }
    @Test
    public void D7_check_gsm(){
        if(!GSM.equals("null")){
            Reporter.pass(" 分机1000通过GSM拨打辅助设备上的GSM卡号码，预期2000响铃");
            pjsip.Pj_Make_Call_No_Answer(1000, DEVICE_ASSIST_GSM, DEVICE_IP_LAN, false);
            if (getExtensionStatus(1100, RING, 8) == RING && getExtensionStatus(2000, RING, 8) == RING) {
                Reporter.pass(" 被通知的分机1100和分机2000状态--RING");
            } else {
                Reporter.error(" 预期被通知的分机1100状态为RING，实际状态为"+getExtensionStatus(1100, RING, 8));
                Reporter.error(" 预期被通知的分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
            }
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(2000,200,false);
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(1100,200,false);
            ys_waitingTime(8000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000dial"+DEVICE_ASSIST_GSM+" <Emergency>","1100 <1100>","Answered"," "," ",communication_internal,1);
//            m_extension.checkCDR("1000 <1000>",DEVICE_ASSIST_GSM,"No Answer"," ",GSM,communication_outRoute,2);
            m_extension.checkCDR("1000 <1000>",DEVICE_ASSIST_GSM,"Answered"," ",GSM,communication_outRoute,2);
        }
    }
    /*
    * 验证VOIP外线与物理外线同时存在的情况
    * */
    @Test
    public void E1_voipAndPhysical(){
        if (PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(!BRI_1.equals("null")) {
            Reporter.infoExec(" 编辑紧急号码——验证第一条sip外线不可用时，走到第二条FXO外线呼出");
            ys_waitingTime(3000);
            gridClick(emergencyNumber.grid, gridFindRowByColumn(emergencyNumber.grid, emergencyNumber.gridColumn_EmergencyNumber, "3001", sort_ascendingOrder), emergencyNumber.gridEdit);
            ys_waitingMask();
            add_emergency_number.emergencyNumber.setValue("2000");
            ys_waitingTime(2000);

            listSelect(add_emergency_number.getTrunkSelect(0), trunkList, "SIP4");
            ys_waitingTime(1000);
            listSelect(add_emergency_number.getTrunkSelect(1), trunkList, BRI_1);
            ys_waitingTime(1000);
            listSelect(add_emergency_number.getAdminSelect(0), extensionList, "1000");
            add_emergency_number.save.click();
            ys_waitingLoading(emergencyNumber.grid_Mask);
            ys_apply();
        }else{
            Reporter.infoExec("没有BRI，无法进行测试");
        }
    }
    @Test
    public void E2_checkVoipAndPhy() {
        if (PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)) {
            return;
        }
        if (!BRI_1.equals("null")) {
            Reporter.pass(" 分机1100通过BRI拨打2000号码，预期2000响铃");
            pjsip.Pj_Make_Call_No_Answer(1100, "2000", DEVICE_IP_LAN, false);
            if (getExtensionStatus(1000, RING, 8) == RING && getExtensionStatus(2000, RING, 8) == RING) {
                Reporter.pass(" 被通知的分机1000和分机2000状态--RING");
            } else {
                Reporter.error(" 预期被通知的分机1000状态为RING，实际状态为" + getExtensionStatus(1000, RING, 8));
                Reporter.error(" 预期被通知的分机2000状态为RING，实际状态为" + getExtensionStatus(2000, RING, 8));
            }
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(2000, 200, false);
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(1000, 200, false);
            ys_waitingTime(8000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1100dial2000 <Emergency>", "1000 <1000>", "Answered", " ", " ", communication_internal, 1);
            m_extension.checkCDR("1100 <1100>", "2000", "Answered", " ", BRI_1, communication_outRoute, 2);
        }else {
            Reporter.infoExec("没有BRI，无法进行测试");
        }
    }
    @Test
    public void E3_PhyAndVoip(){
        if (PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(!FXO_1.equals("null")) {
            Reporter.infoExec(" 编辑紧急号码——验证第一条FXO外线在忙时，走到第二条SPS外线呼出");
            gridClick(emergencyNumber.grid, gridFindRowByColumn(emergencyNumber.grid, emergencyNumber.gridColumn_EmergencyNumber, "2000", sort_ascendingOrder), emergencyNumber.gridEdit);
            ys_waitingMask();

            listSelect(add_emergency_number.getTrunkSelect(0), trunkList,FXO_1 );
            listSelect(add_emergency_number.getTrunkSelect(1), trunkList, SPS);
            listSelect(add_emergency_number.getAdminSelect(0), extensionList, "1100");
            add_emergency_number.save.click();
            ys_waitingLoading(emergencyNumber.grid_Mask);
            ys_apply();
        }else{
            Reporter.infoExec("没有FXO，无法进行测试");
        }
    }
    @Test
    public void E4_checkPhyAndVoip(){
        if (PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(!FXO_1.equals("null")) {
            Reporter.pass(" 分机1000通过PSTN拨打52001号码，预期2001响铃");
            pjsip.Pj_Make_Call_Auto_Answer(1000, "52001", DEVICE_IP_LAN, false);
            ys_waitingTime(7000);
//        接着分机1102拨通紧急号码呼出，分机1000没有被挂断电话，分机1102走SPS——分机2000接听
            pjsip.Pj_Make_Call_No_Answer(1102, "2000", DEVICE_IP_LAN, false);
            if (getExtensionStatus(1000, TALKING, 8) == TALKING && getExtensionStatus(2000, RING, 8) == RING) {
                Reporter.pass(" 分机1000状态--TALKING");
                Reporter.pass(" 分机2000状态--RING");
            } else {
                Reporter.error(" 预期1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
                Reporter.error(" 预期2000状态为RING，实际状态为"+getExtensionStatus(1000, RING, 8));
            }
            if (getExtensionStatus(1100, RING, 8) == RING) {
                Reporter.pass(" 被通知的分机1100状态--RING");
            } else {
                Reporter.error(" 预期被通知的分机1100状态为RING，实际状态为"+getExtensionStatus(1100, RING, 8));
            }
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(2000,200,false);
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(1100,200,false);
            ys_waitingTime(8000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1102dial2000 <Emergency>","1100 <1100>","Answered"," "," ",communication_internal,1);
            m_extension.checkCDR("1102 <1102>","2000","Answered"," ",SPS,communication_outRoute,2);
            m_extension.checkCDR("1000 <1000>","52001","Answered"," ",FXO_1,communication_outRoute,3);
        }else {
            Reporter.infoExec("没有FXO，无法进行测试");
        }
    }

    @Test
    public void E5_PhyAndPhy(){
        if (PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(!FXO_1.equals("null")) {
            if(!BRI_1.equals("null")) {
                Reporter.infoExec(" 编辑紧急号码——验证第一条FXO外线在忙时，走到第二条BRI外线呼出");
                gridClick(emergencyNumber.grid, gridFindRowByColumn(emergencyNumber.grid, emergencyNumber.gridColumn_EmergencyNumber, "2000", sort_ascendingOrder), emergencyNumber.gridEdit);
                ys_waitingMask();

                listSelect(add_emergency_number.getTrunkSelect(0), trunkList, FXO_1);
                listSelect(add_emergency_number.getTrunkSelect(1), trunkList, BRI_1);
                listSelect(add_emergency_number.getAdminSelect(0), extensionList, "1000");
                add_emergency_number.save.click();
                ys_waitingLoading(emergencyNumber.grid_Mask);
                ys_apply();
            }else{
                Reporter.infoExec("没有BRI，无法进行测试");
            }
        }else{
            Reporter.infoExec("没有FXO，无法进行测试");
        }
    }
    @Test
    public void E6_checkPhyAndPhy(){
        if (PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(!FXO_1.equals("null") && !BRI_1.equals("null")) {
            Reporter.pass(" 分机1100通过PSTN拨打52001号码，预期2001响铃");
            pjsip.Pj_Make_Call_Auto_Answer(1100, "52001", DEVICE_IP_LAN, false);
            ys_waitingTime(7000);
//        接着分机1102拨通紧急号码呼出，分机1000不会被挂断电话，分机1102走BRI
            Reporter.pass(" 分机1102通过BRI拨打2000号码，预期2000响铃");
            pjsip.Pj_Make_Call_No_Answer(1102, "2000", DEVICE_IP_LAN, false);
            if (getExtensionStatus(1100, TALKING, 8) == TALKING && getExtensionStatus(2000, RING, 8) == RING) {
                Reporter.pass(" 分机1100状态--TALKING");
                Reporter.pass(" 分机2000状态--RING");
            } else {
                Reporter.error(" 预期1100状态为TALKING，实际状态为"+getExtensionStatus(1100, TALKING, 8));
                Reporter.error(" 预期2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
            }
            if (getExtensionStatus(1000, RING, 8) == RING) {
                Reporter.pass(" 被通知的分机1000状态--RING");
            } else {
                Reporter.error(" 预期被通知的分机1000状态为RING，实际状态为"+getExtensionStatus(1000, RING, 8));
            }
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(2000,200,false);
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(1000,200,false);
            ys_waitingTime(8000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1102dial2000 <Emergency>","1000 <1000>","Answered"," "," ",communication_internal,1);
            m_extension.checkCDR("1102 <1102>","2000","Answered"," ",BRI_1,communication_outRoute,2);
            m_extension.checkCDR("1100 <1100>","52001","Answered"," ",FXO_1,communication_outRoute,3);
        }else {
            Reporter.infoExec("没有BRI/FXO，无法进行测试");
        }
    }

    @Test
    public void F1_Contacts1(){
        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.eventSettings_panel.click();
        ys_waitingTime(1000);
        notificationContacts.notificationContacts.click();
        while (Integer.parseInt(String.valueOf(gridLineNum(notificationContacts.grid))) != 0) {
            gridSeleteAll(notificationContacts.grid);
            notificationContacts.delete.click();
            notificationContacts.delete_yes.click();
            ys_waitingLoading(notificationContacts.grid_Mask);
        }
        Reporter.infoExec("添加事件中心中的通知联系人——分机1103，通知方法为Call Extension"); //执行操作
        notificationContacts.add.click();
        ys_waitingTime(3000);
        comboboxSet(add_contact.chooseContact_id,extensionList,"1103");
        setCheckBox(add_contact.callExtension,true);
        add_contact.save.click();
        ys_apply();
        closeSetting();
    }
    @Test
    public void F1_Contacts2(){
        Reporter.pass(" 分机1000通过SPS拨打111号码，预期2000响铃");
        pjsip.Pj_Make_Call_No_Answer(1000, "111", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1100, RING, 8) == RING && getExtensionStatus(1103, RING, 8) == RING && getExtensionStatus(2000, RING, 8) == RING) {
            Reporter.pass(" 被通知的分机1100和1103状态--RING");
            Reporter.pass(" 呼入路由目的地的分机2000状态--RING");
        } else {
            Reporter.error(" 预期被通知的分机1103状态为RING，实际状态为"+getExtensionStatus(1100, RING, 8)+";预期被通知的分机1103状态为RING，实际状态为"+getExtensionStatus(1103, RING, 8)+";预期分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
        }
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(2000,200,false);
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(1100,200,false);
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(1103,200,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("Warning","xlq <1103>","Answered"," "," ",communication_warning,1);
        m_extension.checkCDR("1000dial111 <Emergency>","1100 <1100>","Answered"," "," ",communication_internal,2);
        m_extension.checkCDR("1000 <1000>","111","Answered"," ",SPS,communication_outRoute,3);
    }
    @Test
    public void F1_Contacts3(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        ys_waitingTime(1000);
        cdRandRecordings.maxWindows.click();
        ys_waitingTime(3000);
        String t = String.valueOf(gridContent(extensions.grid_CDR,3,0)).substring(0,19);
        Reporter.infoExec("最近的一通紧急号码拨打时间为："+t);
        closeCDRRecord();

        Reporter.infoExec("查看Event log"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.eventLog_panel.click();
        ys_waitingTime(6000);
        String time = String.valueOf(gridContent(eventLog.grid,1,eventLog.gridColumn_Time));
        Reporter.infoExec("Event log获取到的time为："+time); //执行操作
        String type = String.valueOf(gridContent(eventLog.grid,1,eventLog.gridColumn_Type));
        Reporter.infoExec("预期type = \"telephony\"，实际Event log获取到的type为："+type);
        String name = String.valueOf(gridContent(eventLog.grid,1,eventLog.gridColumn_EventName));
        Reporter.infoExec("预期name = \"Emergency Call\"，实际Event log获取到的name为："+name);
        String message = String.valueOf(gridContent(eventLog.grid,1,eventLog.gridColumn_EventMessage));
        Reporter.infoExec("预期message = \"Extension 1000 has made an emergency call to 111\"，实际Event log获取到的message为："+message);

        YsAssert.assertInclude(time,t,"EventTime查看");
        YsAssert.assertInclude(type,"telephony","EventType查看");
        YsAssert.assertInclude(name,"Emergency Call","EventName查看");
        YsAssert.assertInclude(message,"Extension 1000 has made an emergency call to 111","EventMessage 查看");
    }

    @Test
    public void F2_EventCenter1(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.eventSettings_tree.click();
        ys_waitingTime(5000);
        Reporter.infoExec("事件中心的紧急号码——不勾选Record，勾选Notification");
        if (PRODUCT.equals(CLOUD_PBX)){
            executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Record_EmergencyCall_CloudPBX + "].down('[name=" + eventSetting.EventSetting_Record + "]').setValue(false)");
            executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Notification_EmergencyCall_CloudPBX + "].down('[name=" + eventSetting.EventSetting_Noticication + "]').setValue(true)");
        }else {
            if (version[1].equals("6")) {
                executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Record_EmergencyCall_306 + "].down('[name=" + eventSetting.EventSetting_Record + "]').setValue(false)");
            }else{
                executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Record_EmergencyCall_307 + "].down('[name=" + eventSetting.EventSetting_Record + "]').setValue(false)");
            }
            executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Notification_EmergencyCall + "].down('[name=" + eventSetting.EventSetting_Noticication + "]').setValue(true)");
        }
        closeSetting();
    }
    @Test
    public void F2_EventCenter2(){
        Reporter.pass(" 分机1102通过SPS拨打111号码，预期2000响铃");
        pjsip.Pj_Make_Call_No_Answer(1102, "111", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1100, RING, 8) == RING && getExtensionStatus(1103, RING, 8) == RING && getExtensionStatus(2000, RING, 8) == RING) {
            Reporter.pass(" 被通知的分机1100和1103和2000状态--RING");
        } else {
            Reporter.error(" 预期被通知的分机1103状态为RING，实际状态为"+getExtensionStatus(1100, RING, 8)+";预期被通知的分机1103状态为RING，实际状态为"+getExtensionStatus(1103, RING, 8)+";预期被通知的分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
        }
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(2000,200,false);
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(1100,200,false);
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(1103,200,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("Warning","xlq <1103>","Answered"," "," ",communication_warning,1);
        m_extension.checkCDR("1102dial111 <Emergency>","1100 <1100>","Answered"," "," ",communication_internal,2);
        m_extension.checkCDR("1102 <1102>","111","Answered"," ",SPS,communication_outRoute,3);
    }
    @Test
    public void F2_EventCenter3(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.eventLog_panel.click();
        ys_waitingTime(5000);
        eventLog.search.click();
//        ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"F2_EventCenter3().jpg");
//        Reporter.sendReport("link","Error: " + "F2_EventCenter3()调试", SCREENSHOT_PATH +"F2_EventCenter3().jpg");

        String type = String.valueOf(gridContent(eventLog.grid,1,eventLog.gridColumn_Type));
        Reporter.infoExec("预期type = \"telephony\"，实际Event log获取到的type为："+type);
        String name = String.valueOf(gridContent(eventLog.grid,1,eventLog.gridColumn_EventName));
        Reporter.infoExec("预期name = \"Emergency Call\"，实际Event log获取到的name为："+name);
        String message = String.valueOf(gridContent(eventLog.grid,1,eventLog.gridColumn_EventMessage));
        Reporter.infoExec("预期message = \"Extension 1000 has made an emergency call to 111\"，实际Event log获取到的message为："+message);

        YsAssert.assertInclude(type,"telephony","EventType查看");
        YsAssert.assertInclude(name,"Emergency Call","EventName查看");
        YsAssert.assertInclude(message,"Extension 1000 has made an emergency call to 111","EventMessage 查看");
    }
    @Test
    public void F2_EventCenter4() {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.eventSettings_tree.click();
        ys_waitingTime(5000);
        Reporter.infoExec("事件中心的紧急号码——不勾选Record，不勾选Notification");
        if (PRODUCT.equals(CLOUD_PBX)) {
            executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Record_EmergencyCall_CloudPBX + "].down('[name=" + eventSetting.EventSetting_Record + "]').setValue(false)");
            executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Notification_EmergencyCall_CloudPBX + "].down('[name=" + eventSetting.EventSetting_Noticication + "]').setValue(false)");
        } else {
            if (version[1].equals("6")) {
                executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Record_EmergencyCall_306 + "].down('[name=" + eventSetting.EventSetting_Record + "]').setValue(false)");
            }else{
                executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Record_EmergencyCall_307 + "].down('[name=" + eventSetting.EventSetting_Record + "]').setValue(false)");
            }
            executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Notification_EmergencyCall + "].down('[name=" + eventSetting.EventSetting_Noticication + "]').setValue(false)");
        }
    }
    @Test
    public void F2_EventCenter5(){
        Reporter.pass(" 分机1101通过SPS拨打111号码，预期2000响铃");
        pjsip.Pj_Make_Call_No_Answer(1101, "111", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1100, RING, 8) == RING && getExtensionStatus(1103, IDLE, 8) == IDLE && getExtensionStatus(2000, RING, 8) == RING) {
            Reporter.pass(" 被通知的分机1100状态--RING;分机1103状态——IDLE;分机2000状态——RING");
        } else {
            Reporter.error(" 预期被通知的分机1100状态为RING，实际状态为"+getExtensionStatus(1100, RING, 8)+";分机1103状态为IDLE，实际状态为"+getExtensionStatus(1103, IDLE, 8)+";分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
        }
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(2000,200,false);
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(1100,200,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1101dial111 <Emergency>","1100 <1100>","Answered"," "," ",communication_internal,1);
        m_extension.checkCDR("1101 <1101>","111","Answered"," ",SPS,communication_outRoute,2);
    }
    @Test
    public void F2_eventCenter6(){
        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.eventLog_panel.click();
        ys_waitingTime(5000);
        String type = String.valueOf(gridContent(eventLog.grid,1,eventLog.gridColumn_Type));
        Reporter.infoExec("预期type = \"telephony\"，实际Event log获取到的type为："+type);
        String name = String.valueOf(gridContent(eventLog.grid,1,eventLog.gridColumn_EventName));
        Reporter.infoExec("预期name = \"Emergency Call\"，实际Event log获取到的name为："+name);
        String message = String.valueOf(gridContent(eventLog.grid,1,eventLog.gridColumn_EventMessage));
        Reporter.infoExec("预期message = \"Extension 1000 has made an emergency call to 111\"，实际Event log获取到的message为："+message);

        YsAssert.assertInclude(type,"telephony","EventType查看");
        YsAssert.assertInclude(name,"Emergency Call","EventName查看");
        YsAssert.assertInclude(message,"Extension 1000 has made an emergency call to 111","EventMessage 查看");
    }
    @Test
    public void F2_eventCenter7(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.eventSettings_tree.click();
        ys_waitingTime(5000);
        Reporter.infoExec("事件中心的紧急号码——勾选Record，不勾选Notification");
        if (PRODUCT.equals(CLOUD_PBX)){
            executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Record_EmergencyCall_CloudPBX + "].down('[name=" + eventSetting.EventSetting_Record + "]').setValue(true)");
            executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Notification_EmergencyCall_CloudPBX + "].down('[name=" + eventSetting.EventSetting_Noticication + "]').setValue(false)");
        }else {
            if (version[1].equals("6")) {
                executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Record_EmergencyCall_306 + "].down('[name=" + eventSetting.EventSetting_Record + "]').setValue(true)");
            }else {
                executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Record_EmergencyCall_307 + "].down('[name=" + eventSetting.EventSetting_Record + "]').setValue(true)");
            }
            executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Notification_EmergencyCall + "].down('[name=" + eventSetting.EventSetting_Noticication + "]').setValue(false)");
        }
        closeSetting();
    }
    @Test
    public void F2_eventCenter8() {
        Reporter.pass(" 分机1102通过SPS拨打111号码，预期2000响铃");
        pjsip.Pj_Make_Call_No_Answer(1102, "111", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1100, RING, 8) == RING && getExtensionStatus(1103, IDLE, 8) == IDLE && getExtensionStatus(2000, RING, 8) == RING) {
            Reporter.pass(" 被通知的分机1100状态--RING;分机1103状态——IDLE;分机2000状态——RING");
        } else {
            Reporter.error(" 预期被通知的分机1100状态为RING，实际状态为" + getExtensionStatus(1100, RING, 8) + ";分机1103状态为IDLE，实际状态为" + getExtensionStatus(1103, IDLE, 8)+ ";分机2000状态为RING，实际状态为" + getExtensionStatus(2000, RING, 8));
        }
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(2000, 200, false);
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(1100, 200, false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1102dial111 <Emergency>", "1100 <1100>", "Answered", " ", " ", communication_internal, 1);
        m_extension.checkCDR("1102 <1102>", "111", "Answered", " ", SPS, communication_outRoute, 2);
    }
    @Test
    public void F2_eventCenter9(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        ys_waitingTime(1000);
        cdRandRecordings.maxWindows.click();
        ys_waitingTime(3000);
        String t = String.valueOf(gridContent(extensions.grid_CDR,2,0)).substring(0,19);
        Reporter.infoExec("CDR显示最近的一通紧急号码拨打时间为："+t);
        closeCDRRecord();

        Reporter.infoExec("查看Event log"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.eventLog_panel.click();
        ys_waitingTime(8000);
        String time = String.valueOf(gridContent(eventLog.grid,1,eventLog.gridColumn_Time));
        Reporter.infoExec("Event log获取到的time为："+time);
        String type = String.valueOf(gridContent(eventLog.grid,1,eventLog.gridColumn_Type));
        Reporter.infoExec("预期type = \"telephony\"，实际Event log获取到的type为："+type);
        String name = String.valueOf(gridContent(eventLog.grid,1,eventLog.gridColumn_EventName));
        Reporter.infoExec("预期name = \"Emergency Call\"，实际Event log获取到的name为："+name);
        String message = String.valueOf(gridContent(eventLog.grid,1,eventLog.gridColumn_EventMessage));
        Reporter.infoExec("预期message = \"Extension 1000 has made an emergency call to 111\"，实际Event log获取到的message为："+message);

        YsAssert.assertInclude(time,t,"EventTime查看");
        YsAssert.assertInclude(type,"telephony","EventType查看");
        YsAssert.assertInclude(name,"Emergency Call","EventName查看");
        YsAssert.assertInclude(message,"Extension 1102 has made an emergency call to 111","EventMessage 查看");
    }
    @Test
    public void F3_recoveryEvent(){
        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.eventSettings_panel.click();
        ys_waitingTime(5000);
        Reporter.infoExec("事件中心的紧急号码——勾选Record，勾选Notification");
        if (PRODUCT.equals(CLOUD_PBX)){
            executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Record_EmergencyCall_CloudPBX + "].down('[name=" + eventSetting.EventSetting_Record + "]').setValue(true)");
            executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Notification_EmergencyCall_CloudPBX + "].down('[name=" + eventSetting.EventSetting_Noticication + "]').setValue(true)");
        }else {
            executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Notification_EmergencyCall + "].down('[name=" + eventSetting.EventSetting_Noticication + "]').setValue(true)");
            if (version[1].equals("6")) {
                executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Record_EmergencyCall_306 + "].down('[name=" + eventSetting.EventSetting_Record + "]').setValue(true)");
            }else {
                executeJs("Ext.ComponentQuery.query('[xType=checkrow]')[" + eventSetting.Record_EmergencyCall_307 + "].down('[name=" + eventSetting.EventSetting_Record + "]').setValue(true)");
            }
        }
        notificationContacts.notificationContacts.click();
        Reporter.infoExec(" 全部勾选-确定删除"); //执行操作
//        全部勾选
        gridSeleteAll(notificationContacts.grid);
//        点击删除按钮
        notificationContacts.delete.click();
        notificationContacts.delete_yes.click();
        ys_waitingLoading(notificationContacts.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(notificationContacts.grid)));
        Reporter.infoExec("实际row2:" + row2);
        YsAssert.assertEquals(0, row2, "全部勾选-确定删除");
    }
    @Test
    public void G1_addNumber(){
        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.emergencyNumber_panel.click();
        if (PRODUCT.equals(CLOUD_PBX)) {
            m_emergencyNumber.addEmergencyNumber(2001, SPS, 1100);
            m_emergencyNumber.addEmergencyNumber(2000, SPS, 1100);
            m_emergencyNumber.addEmergencyNumber(2002, SPS, 1100);
            ys_apply();
        }
    }
    @Test
    public void G2_deleteOne_no(){
        Reporter.infoExec(" 删除单个紧急号码111——选择no"); //执行操作
//       定位要删除的那条呼入路由
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(emergencyNumber.grid, emergencyNumber.gridColumn_EmergencyNumber, "111", sort_ascendingOrder)));
        //还未操作前呼出路由的所有条数
        int rows = Integer.parseInt(String.valueOf(gridLineNum(emergencyNumber.grid)));
        System.out.println("预期值:" + rows);
        gridClick(emergencyNumber.grid, row, emergencyNumber.gridDelete);
        emergencyNumber.delete_no.click();
        ys_waitingLoading(emergencyNumber.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(emergencyNumber.grid)));
        System.out.println("实际值:" + row1);
        YsAssert.assertEquals(row1, rows, "删除单个紧急号码111-取消删除");
    }
    @Test
    public void G3_deleteOne_yes(){
        Reporter.infoExec(" 删除单个紧急号码111——选择yes"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridLineNum(emergencyNumber.grid)));
        //       定位要删除的那条呼入路由
        int row1 = Integer.parseInt(String.valueOf(gridFindRowByColumn(emergencyNumber.grid, emergencyNumber.gridColumn_EmergencyNumber, "111", sort_ascendingOrder)));
        gridClick(emergencyNumber.grid, row1, emergencyNumber.gridDelete);
        emergencyNumber.delete_yes.click();
        ys_waitingLoading(emergencyNumber.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(emergencyNumber.grid)));
        System.out.println("实际值row2:" + row2);
        int row3 = row - 1;
        System.out.println("期望值row3:" + row3);
        ys_apply();
        YsAssert.assertEquals(row2, row3, "删除单个紧急号码111——确定删除");
    }
    @Test
    public void G4_deletePart_no(){
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(emergencyNumber.grid)));
        System.out.println("期望值row1:" + row1);
        int row2 = Integer.parseInt(String.valueOf(gridFindRowByColumn(emergencyNumber.grid, emergencyNumber.gridColumn_EmergencyNumber, "2001", sort_ascendingOrder)));
        int row3 = Integer.parseInt(String.valueOf(gridFindRowByColumn(emergencyNumber.grid, emergencyNumber.gridColumn_EmergencyNumber, "2000", sort_ascendingOrder)));
//        全部勾选
        gridSeleteAll(emergencyNumber.grid);
//        取消勾选OutboundRoute2和OutboundRoute3
        gridCheck(emergencyNumber.grid, row2, emergencyNumber.gridcolumn_Check);
        gridCheck(emergencyNumber.grid, row3, emergencyNumber.gridcolumn_Check);
//        点击删除按钮
        emergencyNumber.delete.click();
        emergencyNumber.delete_no.click();
        ys_waitingLoading(emergencyNumber.grid_Mask);
        int row4 = Integer.parseInt(String.valueOf(gridLineNum(emergencyNumber.grid)));
        System.out.println("实际值row4:" + row4);
        YsAssert.assertEquals(row4, row1, "全部勾选，再取消某条的勾选后-取消删除");
    }
    @Test
    public void G5_deletePart_yes(){
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-确定删除"); //执行操作
        emergencyNumber.delete.click();
        emergencyNumber.delete_yes.click();
        ys_waitingLoading(emergencyNumber.grid_Mask);
        ys_apply();
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(emergencyNumber.grid)));
        Reporter.infoExec("实际row:" + row1);
        YsAssert.assertEquals(row1, 2, "全部勾选，再取消某条的勾选后-确定删除");
    }
    @Test
    public void G6_deleteAll_no(){
        Reporter.infoExec(" 全部勾选-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(emergencyNumber.grid)));
//        全部勾选
        gridSeleteAll(emergencyNumber.grid);
//        点击删除按钮
        emergencyNumber.delete.click();
        emergencyNumber.delete_no.click();
        ys_waitingLoading(emergencyNumber.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(emergencyNumber.grid)));
        System.out.println("实际row2:" + row2);
        YsAssert.assertEquals(row1, row2, "全部勾选-取消删除");
    }
    @Test
    public void G7_delteAll_yes(){
        Reporter.infoExec(" 全部勾选-确定删除"); //执行操作
        emergencyNumber.delete.click();
        emergencyNumber.delete_yes.click();
        ys_waitingLoading(emergencyNumber.grid_Mask);
        int row = Integer.parseInt(String.valueOf(gridLineNum(emergencyNumber.grid)));
        System.out.println("实际row:" + row);
        ys_apply();
        YsAssert.assertEquals(row, 0, "全部勾选-确定删除");
    }
    @Test
    public void H_recovery(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ys_waitingTime(3000);
        if(settings.trunks_panel.isDisplayed()){
            settings.trunks_panel.click();
        }else{
            settings.trunks_tree.click();
        }
        setPageShowNum(trunks.grid,100);
        Reporter.infoExec(" 删除SIP4——选择yes"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(trunks.grid,trunks.gridcolumn_TrunkName,"SIP4",sort_ascendingOrder)));
        gridClick(trunks.grid,row,trunks.gridDelete);
        trunks.delete_yes.click();
        ys_waitingLoading(trunks.grid_Mask);
        ys_apply();
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(trunks.grid)));
        Reporter.infoExec("实际值row2:"+row2);
        int row3=row-1;
        Reporter.infoExec("期望值row3:"+row3);
        YsAssert.assertEquals(row2,row3,"删除SIP4——确定删除");
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
        Reporter.infoAfterClass("执行完毕：====== Emergency Number ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);
    }
}
