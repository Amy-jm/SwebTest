package com.yeastar.swebtest.testcase.DetailCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.TimeConditions.TimeConditions.TimeConditions;
import com.yeastar.swebtest.pobject.Settings.PBX.General.IAX.IAX;
import com.yeastar.swebtest.testcase.RegressionCase.pbxcase.Inbound;
import com.yeastar.swebtest.testcase.RegressionCase.pbxcase.TimeCondition;
import com.yeastar.swebtest.tools.ScreenShot;
import com.yeastar.swebtest.tools.file.ExcelUnit;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import jxl.read.biff.BiffException;
import org.junit.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Caroline on 2017/12/6.
 * 因为IVR，RingGroup、Queue、Conference、DISA和CallBack都有对应的详细测试
 * 所以在呼入路由部分，在不同外线呼入到不同目的地测试中不包含以上几项目的地
 * 呼入转呼出：对于CLoud PBX，没有测试sps呼出路线；对于S系列：没有测试gsm和pstn呼出路线
 */
public class InboundRoutes extends SwebDriver {
    String[] version = DEVICE_VERSION.split("\\.");

    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：====== InboundRoutes ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        inboundRoutes.inboundRoutes.click();
        setPageShowNum(inboundRoutes.grid,100);
    }
//  创建注册分机：主测设备分机1000，辅助1：3001；辅助2:2001；辅助3:4001
    @Test
    public void A_addExtensions(){
        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_CreateAccount(1000, "Yeastar202", "UDP", UDP_PORT, 1);
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_CreateAccount(1100, "Yeastar202", "UDP", UDP_PORT, 2);
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_CreateAccount(1101, "Yeastar202", "UDP", UDP_PORT, 3);
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_CreateAccount(1105, "Yeastar202", "UDP", UDP_PORT, 7);
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1106"); //执行操作
        pjsip.Pj_CreateAccount(1106, "Yeastar202", "UDP", UDP_PORT, 8);

        Reporter.infoExec(" 辅助设备1注册分机3000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3000, "Yeastar202", -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);

        Reporter.infoExec(" 辅助设备1注册分机3001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3001, "Yeastar202", -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3001, DEVICE_ASSIST_1);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2000, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2001, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2005"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2005, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2005, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备3注册分机4000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 4000, "Yeastar202", -1, DEVICE_ASSIST_3, UDP_PORT_ASSIST_3);
        pjsip.Pj_Register_Account_WithoutAssist(4000, DEVICE_ASSIST_3);

        Reporter.infoExec(" 辅助设备3注册分机4001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 4001, "Yeastar202", -1, DEVICE_ASSIST_3, UDP_PORT_ASSIST_3);
        pjsip.Pj_Register_Account_WithoutAssist(4001, DEVICE_ASSIST_3);
        closePbxMonitor();
    }

//    测试各种外线都能正常呼入到分机1000——目的地到SIP的Extensions
    @Test
    public void B1_checkTrunk() throws InterruptedException {
        Inbound inbound = new Inbound();
        inbound.A_callfrom1_sip();
        inbound.A_callfrom2_iax();
        inbound.A_callfrom3_sps();
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }else {
            Reporter.infoExec(" 2001拨打881100通过spx外线呼入到分机1000"); //验证存在SPX路由时，走SPX路由优先
            pjsip.Pj_Make_Call_Auto_Answer(2001, "881100", DEVICE_ASSIST_2);
            ys_waitingTime(5000);
            if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
            } else {
                pageDeskTop.taskBar_Main.click();
                pageDeskTop.pbxmonitorShortcut.click();
                ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"B1_checkTrunk()分机1000.jpg");
                Reporter.sendReport("link","Error: " + "B1_checkTrunk()调试", SCREENSHOT_PATH +"B1_checkTrunk()分机1000.jpg");
                Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>", "1000 <1000>", "Answered", SPX, " ", communication_inbound);
        }
        inbound.A_callfrom5_fxo();
        inbound.A_callfrom6_bri();
        inbound.A_callfrom7_e1();
        inbound.A_callfrom8_gsm();
//        考虑account的呼入
        if (!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec(" 4001拨打1111通过account外线呼入到分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(4001, "1111", DEVICE_ASSIST_3);
            ys_waitingTime(5000);
            if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
            } else {
                pageDeskTop.taskBar_Main.click();
                pageDeskTop.pbxmonitorShortcut.click();
                ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"B1_checkTrunk()2分机1000.jpg");
                Reporter.sendReport("link","Error: " + "B1_checkTrunk()2调试", SCREENSHOT_PATH +"B1_checkTrunk()2分机1000.jpg");
                Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("4001 <6100>", "1000 <1000>", "Answered", ACCOUNTTRUNK, " ", communication_inbound);
        }
        closePbxMonitor();
    }

    //    验证DID——从表格中获取
    @DataProvider(name="did1")
    public Object[][] DIDNumber() throws BiffException, IOException {
        ExcelUnit e=new ExcelUnit("InboundRoutes", "did");
        return e.getExcelData();
    }

    @Test(dataProvider="did1")
    public void B2_checkDID(HashMap<String, String> data) throws InterruptedException {
        Reporter.infoExec("修改呼入路由的DID为："+data.get("DID"));
        System.out.println("data.toString():"+data.toString());
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        ys_waitingTime(1000);
        add_inbound_route.DIDPatem.setValue(data.get("DID"));
        //        保存编辑页面
        add_inbound_route.edit_save();
        ys_apply();

        Reporter.infoExec(" 2001拨打"+data.get("successNumber"));
        pjsip.Pj_Make_Call_Auto_Answer(2001,data.get("successNumber"),DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"B2_checkDID()"+data.get("successNumber")+".jpg");
            Reporter.sendReport("link","Error: " + "B2_checkDID()调试", SCREENSHOT_PATH +"B2_checkDID()"+data.get("successNumber")+".jpg");
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",SPS," ",communication_inbound);

        Reporter.infoExec(" 2001拨打"+data.get("failNumber"));
        pjsip.Pj_Make_Call_Auto_Answer(2001,data.get("failNumber"),DEVICE_ASSIST_2,false);
        ys_waitingTime(10000);
        int state = getExtensionStatus(2001,HUNGUP,10);
        if(state == HUNGUP){
            Reporter.infoExec(" 2001拨打"+data.get("failNumber")+",预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打"+data.get("failNumber")+",预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打"+data.get("failNumber")+",预期呼入失败,实际呼入成功");
        }
    }

    //    验证Caller ID——选取两个进行验证_验证长度匹配
    @Test
    public void B3_checkCaller1(){
        Reporter.infoExec("修改呼入路由的Caller ID为[128]X[0-6]12");
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        add_inbound_route.DIDPatem.setValue("");
        add_inbound_route.callIDPattem.setValue("[128]X[1-6]12");
        //        保存编辑页面
        add_inbound_route.edit_save();
        ys_apply();

        Reporter.infoExec(" 2005（CallerID为2X612）拨打99999，预期呼出成功");
        pjsip.Pj_Make_Call_Auto_Answer(2005,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"B3_checkCaller1()分机1000.jpg");
            Reporter.sendReport("link","Error: " + "B3_checkCaller1()调试", SCREENSHOT_PATH +"B3_checkCaller1()分机1000.jpg");
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2005 <2X612>","1000 <1000>","Answered",SPS," ",communication_inbound);

//        CallerID为2001时呼入失败_长度不匹配
        Reporter.infoExec(" 2001拨打99999，预期呼入失败");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(10000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 2001拨打99999,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打99999,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打99999,预期呼入失败,实际呼入成功");
        }
    }
    //    验证Caller ID——选取两个进行验证_验证字母匹配 && ！匹配
    @Test
    public void B4_checkCaller2(){
        Reporter.infoExec("修改呼入路由的Caller ID为NXZ1!");
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        add_inbound_route.callIDPattem.setValue("NXZ1!");
        //        保存编辑页面
        add_inbound_route.edit_save();
        ys_apply();

        Reporter.infoExec(" 2005（CallerID为2X612）拨打99999，预期呼出成功");
        pjsip.Pj_Make_Call_Auto_Answer(2005,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"B4_checkCaller2()分机1000.jpg");
            Reporter.sendReport("link","Error: " + "B4_checkCaller2()调试", SCREENSHOT_PATH +"B4_checkCaller2()分机1000.jpg");
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2005 <2X612>","1000 <1000>","Answered",SPS," ",communication_inbound);

//        CallerID为2001时呼入成功
        Reporter.infoExec(" 2001（CallerID为2001）拨打99999，预期呼入失败");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 2001拨打99999,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打99999,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打99999,预期呼入失败,实际呼入成功");
        }
    }
    //    CallerID和DID的组合测试
    @Test
    public void B5_checkDIDAndCaller(){
        Reporter.infoExec("修改呼入路由的DID为5503301-5503305，CallerID为NXZ1!\n2001");
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        add_inbound_route.DIDPatem.setValue("5503301-5503305");
        add_inbound_route.callIDPattem.setValue("NXZ1!\n2001");
        //        保存编辑页面
        add_inbound_route.edit_save();
        ys_apply();

        Reporter.infoExec(" 2005（CallerID为2X612）拨打995503302，预期呼入成功");
        pjsip.Pj_Make_Call_Auto_Answer(2005,"995503302",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"B5_checkDIDAndCaller()分机1000.jpg");
            Reporter.sendReport("link","Error: " + "B5_checkDIDAndCaller()调试", SCREENSHOT_PATH +"B5_checkDIDAndCaller()分机1000.jpg");
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2005 <2X612>","1000 <1000>","Answered",SPS," ",communication_inbound);

        Reporter.infoExec(" 2001（CallerID为2001）拨打995503305，预期呼入成功");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"995503305",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"B5_checkDIDAndCaller()2分机1000.jpg");
            Reporter.sendReport("link","Error: " + "B5_checkDIDAndCaller()2调试", SCREENSHOT_PATH +"B5_checkDIDAndCaller()2分机1000.jpg");
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",SPS," ",communication_inbound);

        Reporter.infoExec("2005（CallerID为2X612）拨打99999，预期呼入失败");
        pjsip.Pj_Make_Call_Auto_Answer(2005,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        int state = getExtensionStatus(2005,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 2005拨打99999,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2005拨打99999,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2005拨打99999,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void B6_editDIDAndCaller(){
        Reporter.infoExec("修改呼入路由的DID和CallerID为空");
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        add_inbound_route.DIDPatem.clear();
        add_inbound_route.callIDPattem.clear();
        //        保存编辑页面
        add_inbound_route.edit_save();
        ys_apply();
    }
    @Test
    public void C1_addRoute(){
        Reporter.infoExec(" 添加呼入路由InRoute2——呼入目的地到HangUp ");
        ArrayList<String> arraytrunk1 = new ArrayList<>();
        arraytrunk1.add("all");
        m_callcontrol.addInboundRoutes("InRoute2_HangUp","","",add_inbound_route.s_hangup,"",arraytrunk1);

        Reporter.infoExec(" 添加呼入路由InRoute3——呼入目的地到sip分机1100 ");
        ArrayList<String> arraytrunk2 = new ArrayList<>();
        arraytrunk2.add("all");
        m_callcontrol.addInboundRoutes("InRoute3_SipExtension","","",add_inbound_route.s_extensin,"1100",arraytrunk2);

        if(!PRODUCT.equals(CLOUD_PBX)) {
            if(!FXO_1.equals("null")) {
                Reporter.infoExec(" 添加呼入路由InRoute4——呼入目的地到fxs_Extension ");
                ArrayList<String> arraytrunk3 = new ArrayList<>();
                arraytrunk3.add("all");
                m_callcontrol.addInboundRoutes("InRoute4_FxsExtension", "", "", add_inbound_route.s_extensin, "1106", arraytrunk3);
            }
        }
        Reporter.infoExec(" 添加呼入路由InRoute5——呼入目的地到Extension Range ");
        ArrayList<String> arraytrunk4 = new ArrayList<>();
        arraytrunk4.add("all");
        m_callcontrol.addInboundRoutes("InRoute5_ExtensionRange", "5503301-5503306", "", add_inbound_route.s_extension_range, "1100-1105", arraytrunk4);

        Reporter.infoExec(" 添加呼入路由InRoute6——呼入目的地到voicemail ");
        ArrayList<String> arraytrunk5 = new ArrayList<>();
        arraytrunk5.add("all");
        m_callcontrol.addInboundRoutes("InRoute6_voicemail", "", "", add_inbound_route.s_voicemail, "1000", arraytrunk5);

        Reporter.infoExec(" 添加呼入路由InRoute7——呼入目的地到IVR ");
        ArrayList<String> arraytrunk6 = new ArrayList<>();
        arraytrunk6.add("all");
        m_callcontrol.addInboundRoutes("InRoute7_IVR","","",add_inbound_route.s_ivr,"IVR1",arraytrunk6);

        Reporter.infoExec(" 添加呼入路由InRoute8——呼入目的地到RingGroup ");
        ArrayList<String> arraytrunk7 = new ArrayList<>();
        arraytrunk7.add("all");
        m_callcontrol.addInboundRoutes("InRoute8_RingGroup","","",add_inbound_route.s_ringGroup,"RingGroup1",arraytrunk7);

        Reporter.infoExec(" 添加呼入路由InRoute9——呼入目的地到Queue ");
        ArrayList<String> arraytrunk8 = new ArrayList<>();
        arraytrunk8.add("all");
        m_callcontrol.addInboundRoutes("InRoute9_Queue","","",add_inbound_route.s_queue,"Queue1",arraytrunk8);

        Reporter.infoExec(" 添加呼入路由InRoute10——呼入目的地到Conference ");
        ArrayList<String> arraytrunk9 = new ArrayList<>();
        arraytrunk9.add("all");
        m_callcontrol.addInboundRoutes("InRoute10_Conference","","",add_inbound_route.s_conference,"Conference1",arraytrunk9);

        Reporter.infoExec(" 添加呼入路由InRoute11——呼入目的地到DISA ");
        ArrayList<String> arraytrunk10 = new ArrayList<>();
        arraytrunk10.add("all");
        m_callcontrol.addInboundRoutes("InRoute11_DISA","","",add_inbound_route.s_disa,"DISA1",arraytrunk10);

        Reporter.infoExec(" 添加呼入路由InRoute12——呼入目的地到Callback ");
        ArrayList<String> arraytrunk11 = new ArrayList<>();
        arraytrunk11.add("all");
        m_callcontrol.addInboundRoutes("InRoute12_Callback","","",add_inbound_route.s_callback,"Callback1",arraytrunk11);

        Reporter.infoExec(" 添加呼入路由InRoute13——呼入目的地到Outbound Route_sip ");
        ArrayList<String> arraytrunk12 = new ArrayList<>();
        arraytrunk12.add("all");
        m_callcontrol.addInboundRoutes("InRoute13_Outbound_sip", "", "", add_inbound_route.s_outboundRoute, "OutRoute1_sip", arraytrunk12);

        if (!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec(" 添加呼入路由InRoute18——呼入目的地到Outbound Route_Account ");
            ArrayList<String> arraytrunk20 = new ArrayList<>();
            arraytrunk20.add("all");
            m_callcontrol.addInboundRoutes("InRoute18_account", "", "", add_inbound_route.s_outboundRoute, "OutRoute9_account", arraytrunk20);
        }

        if(!PRODUCT.equals(CLOUD_PBX)) {
            Reporter.infoExec(" 添加呼入路由InRoute14——呼入目的地到Outbound Route_iax ");
            ArrayList<String> arraytrunk13 = new ArrayList<>();
            arraytrunk13.add("all");
            m_callcontrol.addInboundRoutes("InRoute14_Outbound_iax", "", "", add_inbound_route.s_outboundRoute, "OutRoute2_iax", arraytrunk13);

            Reporter.infoExec(" 添加呼入路由InRoute15——呼入目的地到Outbound Route_spx ");
            ArrayList<String> arraytrunk15 = new ArrayList<>();
            arraytrunk15.add("all");
            m_callcontrol.addInboundRoutes("InRoute15_Outbound_spx", "", "", add_inbound_route.s_outboundRoute, "OutRoute4_spx", arraytrunk15);

            if (!PRODUCT.equals(PC)) {
                if (!BRI_1.equals("null")) {
                    Reporter.infoExec(" 添加呼入路由InRoute16——呼入目的地到Outbound Route_bri ");
                    ArrayList<String> arraytrunk17 = new ArrayList<>();
                    arraytrunk17.add("all");
                    m_callcontrol.addInboundRoutes("InRoute16_Outbound_bri", "", "", add_inbound_route.s_outboundRoute, "OutRoute6_bri", arraytrunk17);
                }
                if (!E1.equals("null")) {
                    Reporter.infoExec(" 添加呼入路由InRoute17——呼入目的地到Outbound Route_E1 ");
                    ArrayList<String> arraytrunk18 = new ArrayList<>();
                    arraytrunk18.add("all");
                    m_callcontrol.addInboundRoutes("InRoute17_Outbound_E1", "", "", add_inbound_route.s_outboundRoute, "OutRoute7_e1", arraytrunk18);
                    ys_apply();
                }
            }
        }
    }
    @Test
    public void C2_checkPriority1() throws InterruptedException {
        setPageShowNum(inboundRoutes.grid,100);
//        还未变换呼入路由顺序时，不同外线同时呼入，到达分机1000和分机1000的语音信箱
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"C2_checkPriority1()分机1000.jpg");
            Reporter.sendReport("link","Error: " + "C2_checkPriority1()调试", SCREENSHOT_PATH +"C2_checkPriority1()分机1000.jpg");
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
//        3001还未挂断电话时，2001也通过sps外线呼入分机1000
        Reporter.infoExec(" 2001拨打991105通过sps外线呼入到分机1000"); //验证SPS路由存在时，走路由规则优先
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991105",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"C2_checkPriority1()2分机1000.jpg");
            Reporter.sendReport("link","Error: " + "C2_checkPriority1()2调试", SCREENSHOT_PATH +"C2_checkPriority1()2分机1000.jpg");
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000 <1000>","Answered",SIPTrunk," ",communication_inbound,2);
        m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",SPS," ",communication_inbound);
    }

    @Test
    public void C2_checkPriority2() throws InterruptedException {
//        将InRoute10——Conference移至第一行，不同外线同时呼入，到达会议室6400
        int column = gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute10_Conference", sort_ascendingOrder);
        if(column != 1) {
            Reporter.infoExec(" 移动第" + column + "行到第一行 ");
//        使用gridClick来点击表格里的top按钮
            gridClick(inboundRoutes.grid, column, inboundRoutes.gridTop);
            ys_apply();
        }

        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到会议室"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"C2_checkPriority2()分机3001.jpg");
            Reporter.sendReport("link","Error: " + "C2_checkPriority2()调试", SCREENSHOT_PATH +"C2_checkPriority2()分机3001.jpg");
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
//        3001还未挂断电话时，2001也通过sps外线呼入分机1000
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到会议室"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"C2_checkPriority2()2分机2001.jpg");
            Reporter.sendReport("link","Error: " + "C2_checkPriority2()2调试", SCREENSHOT_PATH +"C2_checkPriority2()2分机2001.jpg");
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","6400","Answered",SIPTrunk," ",communication_inbound,2);
        m_extension.checkCDR("2001 <2001>","6400","Answered",SPS," ",communication_inbound);
    }

    @Test
    public void C2_checkPriority3() throws InterruptedException {
//        将InRoute8_RingGroup移至第一行，不同外线同时呼入，到达ringgroup
        int column = gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute8_RingGroup", sort_ascendingOrder);
        if(column != 1) {
            Reporter.infoExec(" 移动第" + column + "行到第一行 ");
//        使用gridClick来点击表格里的top按钮
            gridClick(inboundRoutes.grid, column, inboundRoutes.gridTop);
            ys_apply();
        }
        Reporter.infoExec(" 3001拨打3000呼入到响铃组，分机1105接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, RING, 8) == RING && getExtensionStatus(1100, RING, 8) == RING && getExtensionStatus(1105, RING, 8) == RING) {
            Reporter.pass(" 分机1000/1100和1105响铃");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"C2_checkPriority3()响铃组.jpg");
            Reporter.sendReport("link","Error: " + "C2_checkPriority3()响铃组", SCREENSHOT_PATH +"C2_checkPriority3()响铃组.jpg");
            Reporter.error(" 预期分机1000、1100和1105响铃，实际分机1000状态为"+getExtensionStatus(1000, RING, 8)+";分机1100状态为："+getExtensionStatus(1100, RING, 8)+";分机1105状态为："+getExtensionStatus(1105, RING, 8));
        }
        pjsip.Pj_Answer_Call(1105,true);
        if (getExtensionStatus(1105, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1105状态——TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1105为TALKING状态，实际分机1100状态为"+getExtensionStatus(1105, TALKING, 8));
        }
//        3001还未挂断电话时，2001也通过sps外线呼入到响铃组
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到响铃组，分机1100接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(8000);
        if (getExtensionStatus(1000, RING, 8) == RING && getExtensionStatus(1100, RING, 8) == RING) {
            Reporter.pass(" 分机1000和1100响铃");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"C2_checkPriority3()2响铃组.jpg");
            Reporter.sendReport("link","Error: " + "C2_checkPriority3()2响铃组", SCREENSHOT_PATH +"C2_checkPriority3()2响铃组.jpg");
            Reporter.error(" 预期分机1000、1100响铃，实际分机1000状态为"+getExtensionStatus(1000, RING, 8)+";分机1100状态为："+getExtensionStatus(1100, RING, 8));
        }
        pjsip.Pj_Answer_Call(1100,true);
        if (getExtensionStatus(1100, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1100状态——TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1100为TALKING状态，实际分机1100状态为"+getExtensionStatus(1100, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("3001 <3001>","1105 <6200(1105)>","Answered",SIPTrunk," ",communication_inbound,2);
        m_extension.checkCDR("2001 <2001>","1100 <6200(1100)>","Answered",SPS," ",communication_inbound);
    }

    @Test
    public void C2_checkPriority4_deleteRoute(){
        Reporter.infoExec(" 删除呼入路由InRoute8（RingGroup）——选择yes"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute8_RingGroup", sort_ascendingOrder)));
        gridClick(inboundRoutes.grid, row, inboundRoutes.gridDelete);
        inboundRoutes.delete_yes.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        ys_apply();
    }

    @Test
    public void C2_checkPriority5(){
//        删除InRoute8——RingGroup后，所有外线的呼入都到会议室
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到会议室"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1);
        ys_waitingTime(10000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
//        3001还未挂断电话时，2001也通过sps外线呼入会议室
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到会议室"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(8000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("3001 <3001>","6400","Answered",SIPTrunk," ",communication_inbound,2);
        m_extension.checkCDR("2001 <2001>","6400","Answered",SPS," ",communication_inbound);
    }

    @Test
    public void C3_recoveryRingGroup(){
        inboundRoutes.inboundRoutes.click();
        Reporter.infoExec(" 添加呼入路由InRoute8——呼入目的地到RingGroup ");
        ArrayList<String> arraytrunk13 = new ArrayList<>();
        arraytrunk13.add("all");
        m_callcontrol.addInboundRoutes("InRoute8_RingGroup","","",add_inbound_route.s_ringGroup,"RingGroup1",arraytrunk13);
    }
//    不同外线呼入到不同目的地的测试
//    目的地设置为hangup
    @Test
    public void D1_Hangup(){
//        pjsip.Pj_Unregister_Account(1000);
//        pjsip.Pj_Unregister_Account(1100);
//        pjsip.Pj_Unregister_Account(1101);
//        pjsip.Pj_Unregister_Account(1105);
//        pjsip.Pj_Unregister_Account(1106);
//        pjsip.Pj_Unregister_Account(3001);
//        pjsip.Pj_Unregister_Account(2000);
//        pjsip.Pj_Unregister_Account(2001);
//        pjsip.Pj_Unregister_Account(2005);
//        pjsip.Pj_Unregister_Account(4000);
//        pjsip.Pj_Unregister_Account(4001);
//        ys_waitingTime(6000);
//        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
//        pjsip.Pj_CreateAccount(1000, "Yeastar202", "UDP", UDP_PORT, 1);
//        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
//        pjsip.Pj_CreateAccount(1100, "Yeastar202", "UDP", UDP_PORT, 2);
//        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
//        pjsip.Pj_CreateAccount(1101, "Yeastar202", "UDP", UDP_PORT, 3);
//        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
//        pjsip.Pj_CreateAccount(1105, "Yeastar202", "UDP", UDP_PORT, 7);
//        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1106"); //执行操作
//        pjsip.Pj_CreateAccount(1106, "Yeastar202", "UDP", UDP_PORT, 8);
//
//        Reporter.infoExec(" 辅助设备1注册分机3001"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 3001, "Yeastar202", -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
//        pjsip.Pj_Register_Account_WithoutAssist(3001, DEVICE_ASSIST_1);
//
//        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 2000, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
//        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);
//
//        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 2001, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
//        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
//
//        Reporter.infoExec(" 辅助设备2注册分机2005"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 2005, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
//        pjsip.Pj_Register_Account_WithoutAssist(2005, DEVICE_ASSIST_2);
//
//        Reporter.infoExec(" 辅助设备3注册分机4000"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 4000, "Yeastar202", -1, DEVICE_ASSIST_3, UDP_PORT_ASSIST_3);
//        pjsip.Pj_Register_Account_WithoutAssist(4000, DEVICE_ASSIST_3);
//
//        Reporter.infoExec(" 辅助设备3注册分机4001"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 4001, "Yeastar202", -1, DEVICE_ASSIST_3, UDP_PORT_ASSIST_3);
//        pjsip.Pj_Register_Account_WithoutAssist(4001, DEVICE_ASSIST_3);
//        closePbxMonitor();
        setPageShowNum(inboundRoutes.grid,100);
//        将InRoute2_HangUp移至第一行，不同外线呼入，都被挂断
        int column = gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute2_HangUp", sort_ascendingOrder);
        if(column != 1) {
            Reporter.infoExec(" 移动第" + column + "行到第一行 ");
//        使用gridClick来点击表格里的top按钮
            gridClick(inboundRoutes.grid, column, inboundRoutes.gridTop);
            ys_apply();
        }
//      sps外线测试
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1000,预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打99999通过sps外线呼入到分机1000,预期呼入失败,实际呼入成功");
        }
    }
    //     目的地设置为FXS类型的分机
    @Test
    public void D2_FxsExtensions() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        if (!FXS_1.equals("null")) {
            int column = gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute4_FxsExtension", sort_ascendingOrder);
            Reporter.infoExec(" 移动第" + column + "行到第一行 ");
//        使用gridClick来点击表格里的top按钮
            gridClick(inboundRoutes.grid, column, inboundRoutes.gridTop);
            ys_apply();
//          sps外线测试 && 分机1106接听电话
            Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1106，分机接听"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
            ys_waitingTime(5000);
            if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>","1106 <1106>","Answered",SPS," ",communication_inbound);
        }
    }
//    目的地设置为Extension Range
    @Test
    public void D3_ExtensionRange() throws InterruptedException {
        int column = gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute5_ExtensionRange", sort_ascendingOrder);
        if(column != 1) {
            Reporter.infoExec(" 移动第" + column + "行到第一行 ");
//        使用gridClick来点击表格里的top按钮
            gridClick(inboundRoutes.grid, column, inboundRoutes.gridTop);
            ys_apply();
        }
//        sps外线测试&&分机1101接听电话
        Reporter.infoExec(" 2001拨打995503302通过sps外线呼入到分机1101"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"995503302",DEVICE_ASSIST_2);
        ys_waitingTime(5000);
        if (getExtensionStatus(1101, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1101状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"D3_ExtensionRange()分机1101.jpg");
            Reporter.sendReport("link","Error: " + "D3_ExtensionRange()调试", SCREENSHOT_PATH +"D3_ExtensionRange()分机1101.jpg");
            Reporter.error(" 预期分机1101状态为TALKING，实际状态为"+getExtensionStatus(1101, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1101 <1101>","Answered",SPS," ",communication_inbound);
//        sps外线测试&&分机1101拒接电话
        Reporter.infoExec(" 2001拨打995503302通过sps外线呼入到分机1101"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"995503302",DEVICE_ASSIST_2);
        ys_waitingTime(5000);
        if (getExtensionStatus(1101, RING, 8) == RING) {
            Reporter.pass(" 分机1101状态--RING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"D3_ExtensionRange()分机1101_rejectAnswer.jpg");
            Reporter.sendReport("link","Error: " + "D3_ExtensionRange()调试", SCREENSHOT_PATH +"D3_ExtensionRange()分机1101_rejectAnswer.jpg");
            Reporter.error(" 预期分机1101状态为RING，实际状态为"+getExtensionStatus(1101, RING, 8));
        }
        pjsip.Pj_Answer_Call(1101,486,false);
        ys_waitingTime(3000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>", "1101 <1101>", "Voicemail", SPS, " ", communication_inbound);
        m_extension.checkCDR("2001 <2001>", "1101 <1101>", "Busy", SPS, " ", communication_inbound,2);
//        sps外线测试&&分机1101未接电话
        Reporter.infoExec(" 2001拨打995503302通过sps外线呼入到分机1101"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"995503302",DEVICE_ASSIST_2);
        ys_waitingTime(5000);
        if (getExtensionStatus(1101, RING, 8) == RING) {
            Reporter.pass(" 分机1101状态--RING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"D3_ExtensionRange()分机1101_noAnswer.jpg");
            Reporter.sendReport("link","Error: " + "D3_ExtensionRange()调试", SCREENSHOT_PATH +"D3_ExtensionRange()分机1101_noAnswer.jpg");
            Reporter.error(" 预期分机1101状态为RING，实际状态为"+getExtensionStatus(1101, RING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>", "1101 <1101>", "No Answer", SPS, " ", communication_inbound);

//        SPS外线测试&&分机1105接听电话
        Reporter.infoExec(" 2001拨打995503306通过sps外线呼入到分机1105"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"995503306",DEVICE_ASSIST_2);
        ys_waitingTime(5000);
        if (getExtensionStatus(1105, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1105状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"D3_ExtensionRange()分机1105.jpg");
            Reporter.sendReport("link","Error: " + "D3_ExtensionRange()调试", SCREENSHOT_PATH +"D3_ExtensionRange()分机1105.jpg");
            Reporter.error(" 预期分机1105状态为TALKING，实际状态为"+getExtensionStatus(1105, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1105 <1105>","Answered",SPS," ",communication_inbound);
//        sps外线测试&&分机1105拒接电话
        Reporter.infoExec(" 2001拨打995503306通过sps外线呼入到分机1105"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"995503306",DEVICE_ASSIST_2);
        ys_waitingTime(5000);
        if (getExtensionStatus(1105, RING, 8) == RING) {
            Reporter.pass(" 分机1105状态--RING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"D3_ExtensionRange()分机1105_rejectAnswer.jpg");
            Reporter.sendReport("link","Error: " + "D3_ExtensionRange()调试", SCREENSHOT_PATH +"D3_ExtensionRange()分机1105_rejectAnswer.jpg");
            Reporter.error(" 预期分机1105状态为RING，实际状态为"+getExtensionStatus(1105, RING, 8));
        }
        pjsip.Pj_Answer_Call(1105,486,false);
        ys_waitingTime(3000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>", "1105 <1105>", "Voicemail", SPS, " ", communication_inbound);
        m_extension.checkCDR("2001 <2001>", "1105 <1105>", "Busy", SPS, " ", communication_inbound,2);
//        sps外线测试&&分机1105未接电话
        Reporter.infoExec(" 2001拨打995503306通过sps外线呼入到分机1105"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"995503306",DEVICE_ASSIST_2);
        ys_waitingTime(5000);
        if (getExtensionStatus(1105, RING, 8) == RING) {
            Reporter.pass(" 分机1105状态--RING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"D3_ExtensionRange()分机1105_noAnswer.jpg");
            Reporter.sendReport("link","Error: " + "D3_ExtensionRange()调试", SCREENSHOT_PATH +"D3_ExtensionRange()分机1105_noAnswer.jpg");
            Reporter.error(" 预期分机1105状态为RING，实际状态为"+getExtensionStatus(1105, RING, 8));
        }
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("2001 <2001>", "1105 <1105>", "No Answer", SPS, " ", communication_inbound);

    }
//    目的地设置为Voicemail
    @Test
    public void D4_Voicemail() throws InterruptedException {
        int column = gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute6_voicemail", sort_ascendingOrder);
        if(column != 1) {
            Reporter.infoExec(" 移动第" + column + "行到第一行 ");
//        使用gridClick来点击表格里的top按钮
            gridClick(inboundRoutes.grid, column, inboundRoutes.gridTop);
            ys_apply();
        }
//        sip外线测试
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000","Voicemail",SIPTrunk," ",communication_inbound);
    }
//    目的地设置为Outbound Route——OutRoute1_sip
//    呼入转呼出的使用方法：呼入路由前缀+呼出路由前缀+要拨打的号码
//    不测试拒接和未接的情况：因为sip线路会直接被注册分机接听
//    要考虑设备的不同性
    @Test
    public void E1_Outbound_sip() throws InterruptedException {
        int column = gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute13_Outbound_sip", sort_ascendingOrder);
        if(column != 1) {
            Reporter.infoExec(" 移动第" + column + "行到第一行 ");
//        使用gridClick来点击表格里的top按钮
            gridClick(inboundRoutes.grid, column, inboundRoutes.gridTop);
            ys_apply();
        }
//        sps测试 && 分机接听
        Reporter.infoExec(" 2001拨打9913001通过sps外线呼入转sip外线呼出到分机3001"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"9913001",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","13001","Answered",SPS,SIPTrunk,communication_outRoute);
    }
//    目的地设置为Outbound Route——OutRoute3_iax
//    不测试拒接和未接的情况：因为iax线路会直接被注册分机接听
    @Test
    public void E2_Outbound_iax(){
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        int column = gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute14_Outbound_iax", sort_ascendingOrder);
        if(column != 1) {
            Reporter.infoExec(" 移动第" + column + "行到第一行 ");
//        使用gridClick来点击表格里的top按钮
            gridClick(inboundRoutes.grid, column, inboundRoutes.gridTop);
            ys_apply();
        }
//        iax测试 && 分机接听
        Reporter.infoExec(" 2001拨打9923001通过sps外线呼入转iax外线到分机3001，分机接听"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"9923001",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","23001","Answered",SPS, IAXTrunk,communication_outRoute);
    }
//    目的地设置为Outbound Route——OutRoute4_spx
//    不测试拒接的情况，因为拒接的Busy通话记录是在辅助设备上，主测设备都是Answer状态
    @Test
    public void E3_Outbound_spx(){
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        int column = gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute15_Outbound_spx", sort_ascendingOrder);
        if(column != 1) {
            Reporter.infoExec(" 移动第" + column + "行到第一行 ");
//        使用gridClick来点击表格里的top按钮
            gridClick(inboundRoutes.grid, column, inboundRoutes.gridTop);
            ys_apply();
        }
//        spx测试 && 分机未接听
        Reporter.infoExec(" 2001拨打9942000通过sps外线呼入转spx外线到分机2000，分机未接");
        pjsip.Pj_Make_Call_No_Answer(2001,"9942000",DEVICE_ASSIST_2);
        ys_waitingTime(8000);
        if (getExtensionStatus(2000, RING, 8) == RING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, RING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","42000","No Answer",SPS, SPX,communication_outRoute);
    }
    //    目的地设置为Outbound Route——OutRoute6_bri
    //    不测试拒接的情况，因为拒接的Busy通话记录是在辅助设备上，主测设备都是Answer状态
    @Test
    public void E4_Outbound_bri() {
        if (PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)) {
            return;
        }
        if (!BRI_1.equals("null")) {
            int column = gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute16_Outbound_bri", sort_ascendingOrder);
            if(column != 1) {
                Reporter.infoExec(" 移动第" + column + "行到第一行 ");
                gridClick(inboundRoutes.grid, column, inboundRoutes.gridTop);
                ys_apply();
            }
//        BRI测试 && 分机接听
            Reporter.infoExec(" 2001拨打9962000通过sps外线呼入转bri外线到分机2000，分机接听"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2001, "9962000", DEVICE_ASSIST_2,false);
            ys_waitingTime(8000);
            if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>", "62000", "Answered", SPS, BRI_1, communication_outRoute);
//        BRI测试 && 分机未接听
            Reporter.infoExec(" 2001拨打9962000通过sps外线呼入转bri外线到分机2000，分机未接"); //执行操作
            pjsip.Pj_Make_Call_No_Answer(2001, "9962000", DEVICE_ASSIST_2);
            ys_waitingTime(8000);
            if (getExtensionStatus(2000, RING, 8) == RING) {
                Reporter.pass(" 分机2000状态--RING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为RING，实际状态为"+getExtensionStatus(2000, RING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>", "62000", "No Answer", SPS, BRI_1, communication_outRoute);
        }
    }
    //    目的地设置为Outbound Route——OutRoute7_E1
    //    不测试拒接的情况，因为拒接的Busy通话记录是在辅助设备上，主测设备都是Answer状态
    @Test
    public void E5_Outbound_e1() {
        if (PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)) {
            return;
        }
        if (!E1.equals("null")) {
            int column = gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute17_Outbound_E1", sort_ascendingOrder);
            if(column != 1) {
                Reporter.infoExec(" 移动第" + column + "行到第一行 ");
                gridClick(inboundRoutes.grid, column, inboundRoutes.gridTop);
                ys_apply();
            }
//        E1测试 && 分机接听
            Reporter.infoExec(" 2001拨打9972000通过sps外线呼入转E1外线到分机2000，分机接听"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2001, "9972000", DEVICE_ASSIST_2,false);
            ys_waitingTime(8000);
            if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>", "72000", "Answered", SPS, E1, communication_outRoute);
//        E1测试 && 分机未接听
            Reporter.infoExec(" 2001拨打9972000通过sps外线呼入转E1外线到分机2000，分机未接"); //执行操作
            pjsip.Pj_Make_Call_No_Answer(2001, "9972000", DEVICE_ASSIST_2);
            ys_waitingTime(8000);
            if (getExtensionStatus(2001, RING, 8) == RING) {
                Reporter.pass(" 分机2001状态--RING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2001状态为RING，实际状态为"+getExtensionStatus(2001, RING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>", "72000", "No Answer", SPS, E1, communication_outRoute);
        }
    }
    //    目的地设置为Outbound Route——OutRoute9_account
    //    不测试拒接的情况，因为拒接的Busy通话记录是在辅助设备上，主测设备都是Answer状态
    @Test
    public void E6_Outbound_account() {
        if (!DEVICE_ASSIST_3.equals("null")) {
            int column = gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute18_account", sort_ascendingOrder);
            if(column != 1) {
                Reporter.infoExec(" 移动第" + column + "行到第一行 ");
                gridClick(inboundRoutes.grid, column, inboundRoutes.gridTop);
                ys_apply();
            }
//        account测试 && 分机接听
            Reporter.infoExec(" 2001拨打9991111通过sps外线呼入转account外线到分机1000，分机接听"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2001, "9991111", DEVICE_ASSIST_2,false);
            ys_waitingTime(8000);
            if (getExtensionStatus(4000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机4000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机4000状态为TALKING，实际状态为"+getExtensionStatus(4000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>", "91111", "Answered", SPS, ACCOUNTTRUNK, communication_outRoute);
        }
    }

    @Test
    public void G1_addTimeCondition() throws InterruptedException {
//        settings.maximize.click();
        time_Conditions.timeConditions.click();
        Reporter.infoExec(" 删除所有时间条件——Workday");
        deletes("删除所有时间条件——Workday",timeConditions.grid,timeConditions.delete,timeConditions.delete_yes,timeConditions.grid_Mask);
        Reporter.infoExec(" 添加时间条件workday_24hour:每天24小时都是工作时间"); //执行操作
        m_callcontrol.addTimeContion("workday_24hour","00:00","23:59",false,"all");

        Reporter.infoExec(" 添加时间条件workday2_24hour:每天24小时都是工作时间"); //执行操作
        m_callcontrol.addTimeContion("workday2_24hour","00:00","23:59",false,"all");

        holiday.holiday.click();
        Reporter.infoExec(" 删除所有时间条件——Holiday");
        deletes("删除所有Holiday",holiday.grid,holiday.delete,holiday.delete_yes,holiday.grid_Mask);
        Reporter.infoExec(" 新建HolidayByDay：2018-01-01~2200-12-31"); //执行操作
        m_callcontrol.addHolidayByDay("HolidayByYear","2018-01-01","2020-12-31");
//        settings.restore.click();
        ys_apply();
    }
    @Test
    public void G2_otherTime1(){
        inboundRoutes.inboundRoutes.click();
        setPageShowNum(inboundRoutes.grid,100);
        int column = gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute10_Conference", sort_ascendingOrder);
        if(column != 1) {
            Reporter.infoExec(" 移动第" + column + "行到第一行 ");
//        使用gridClick来点击表格里的top按钮
            gridClick(inboundRoutes.grid, column, inboundRoutes.gridTop);
            ys_apply();
        }
        Reporter.infoExec("修改InRoute10_Conference呼入路由:勾选TimeCondition");
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute10_Conference",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        setCheckBox(add_inbound_route.enableTimeCondition,true);
        //        保存编辑页面
        add_inbound_route.edit_save();
        ys_apply();

        Reporter.infoExec(" 2001拨打99999，预期呼入失败");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(10000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 2001拨打99999,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打99999,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打99999,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void G2_otherTime2() throws InterruptedException {
        Reporter.infoExec("修改InRoute10_Conference呼入路由:otherTime的目的地选择分机1000");
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute10_Conference",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        add_inbound_route.SetTimeConditionTableviewDestination(1,2,add_inbound_route.s_extensin);
        add_inbound_route.SetTimeConditionTableviewDestitionEx(1,3,"1000");
        //        保存编辑页面
        add_inbound_route.edit_save();
        ys_apply();

        Reporter.infoExec(" 3001拨打3000，预期呼入成功，分机1000未接");
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1000, RING, 8) == RING) {
            Reporter.pass(" 分机1000状态--RING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"G2_otherTime2()分机1000_noAnswer.jpg");
            Reporter.sendReport("link","Error: " + "G2_otherTime2()调试", SCREENSHOT_PATH +"G2_otherTime2()分机1000_noAnswer.jpg");
            Reporter.error(" 预期分机1000状态为RING，实际状态为"+getExtensionStatus(1000, RING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000 <1000>","No Answer",SIPTrunk," ",communication_inbound);
//       开启TimeCondition后，不同的外线可以正常的呼入到目的地，分机1000接听
        Inbound inbound2 = new Inbound();
        inbound2.A_callfrom2_iax();
        inbound2.A_callfrom3_sps();
        inbound2.A_callfrom4_spx();
        inbound2.A_callfrom5_fxo();
        inbound2.A_callfrom6_bri();
        inbound2.A_callfrom7_e1();
        inbound2.A_callfrom8_gsm();
//        考虑account的呼入
        if (!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec(" 4001拨打1111通过account外线呼入到分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(4001, "1111", DEVICE_ASSIST_3);
            ys_waitingTime(5000);
            if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("4001 <6100>", "1000 <1000>", "Answered", ACCOUNTTRUNK, " ", communication_inbound);
        }
        closePbxMonitor();
    }
    @Test
    public void G3_workday(){
        Reporter.infoExec("修改InRoute10_Conference呼入路由:新增TimeCondition——workday");
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute10_Conference",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        add_inbound_route.addTimeCondition.click();
        ys_waitingTime(1000);
        add_inbound_route.SetTimeConditionTableviewDestition(1,1,"workday_24hour");
        add_inbound_route.SetTimeConditionTableviewDestination(1,2,add_inbound_route.s_extensin);
        add_inbound_route.SetTimeConditionTableviewDestitionEx(1,3,"1100");
        add_inbound_route.edit_save();
        ys_apply();

        Reporter.infoExec(" 2001拨打99999，预期呼入成功，分机1100接听");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1100, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1100状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1100状态为TALKING，实际状态为"+getExtensionStatus(1100, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1100 <1100>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void G4_holiday(){
        Reporter.infoExec("修改InRoute10_Conference呼入路由:新增TimeCondition——holiday");
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute10_Conference",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        add_inbound_route.addTimeCondition.click();
        ys_waitingTime(1000);
        add_inbound_route.SetTimeConditionTableviewDestition(2,1,"[Holiday]");
        add_inbound_route.SetTimeConditionTableviewDestination(2,2,add_inbound_route.s_queue);
        add_inbound_route.edit_save();
        ys_apply();

        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到Queue6700,1100接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1);
        ys_waitingTime(5000);
        if (getExtensionStatus(1100, RING, 10) == RING &&
                getExtensionStatus(1000, RING, 10) == RING &&
                getExtensionStatus(1105, RING, 10) == RING) {
            Reporter.pass(" 分机1100、1000、1105同时响铃");
        } else {
            Reporter.pass(" 状态检测分机1100、1000、1105未同时响铃");
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"G4_holiday()队列.jpg");
            Reporter.sendReport("link","Error: " + "G4_holiday()调试", SCREENSHOT_PATH +"G4_holiday()队列.jpg");
            Reporter.infoExec("1100的状态："+getExtensionStatus(1100, RING, 10));
            Reporter.infoExec("1000的状态"+getExtensionStatus(1000, RING, 10));
            Reporter.infoExec("1105的状态"+getExtensionStatus(1105, RING, 10));
        }
        pjsip.Pj_Answer_Call(1100, true);
        ys_waitingTime(6000);
        if (getExtensionStatus(1100, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1100状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"G4_holiday()分机1100.jpg");
            Reporter.sendReport("link","Error: " + "G4_holiday()调试", SCREENSHOT_PATH +"G4_holiday()分机1100.jpg");
            Reporter.error(" 预期分机1100状态为TALKING，实际状态为"+getExtensionStatus(1100, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("3001 <3001>", "1100 <6700(1100)>", "Answered", SIPTrunk, "", communication_inbound);
    }
    @Test
    public void G5_addTime1_noWorkday() throws InterruptedException {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
        String time = dateFormat.format( now );
        Reporter.infoExec(" 当前的时间为："+time);
        String[] weekDays = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        Reporter.infoExec(" 今天星期："+weekDays[w]);

        time_Conditions.timeConditions.click();
        ys_waitingTime(1000);
        timeConditions.timeConditions.click();
        ys_waitingTime(1000);
        Reporter.infoExec(" 添加时间条件NoWorkday:当前时间不是上班时间"); //执行操作
        m_callcontrol.addTimeContion("NoWorkday","00:00","23:59",false,"all");
        Reporter.infoExec("修改NoWorkday：取消勾选当前的星期");
        gridClick(timeConditions.grid,gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"NoWorkday",sort_ascendingOrder),timeConditions.gridEdit);
        if(weekDays[w].equals("sun"))
            add_time_condition.sunday.click();
        else if(weekDays[w].equals("mon"))
            add_time_condition.monday.click();
        else if (weekDays[w].equals("tue"))
            add_time_condition.tuesday.click();
        else if (weekDays[w].equals("wed"))
            add_time_condition.wednesday.click();
        else if (weekDays[w].equals("thu"))
            add_time_condition.thursday.click();
        else if (weekDays[w].equals("fri"))
            add_time_condition.friday.click();
        else if (weekDays[w].equals("sat"))
            add_time_condition.saturday.click();
        //        保存编辑页面
        add_time_condition.save.click();
        ys_waitingLoading(timeConditions.grid_Mask);
        ys_apply();
    }
    @Test
    public void G5_addTime2_noHoliday() throws InterruptedException {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String time = dateFormat.format( date );
        System.out.println("time1:"+time);
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, +1);
        date = calendar.getTime();
        String time2 = dateFormat.format( date );
        System.out.println("time2:"+time2);

        holiday.holiday.click();
        Reporter.infoExec(" 删除所有时间条件——Holiday");
        deletes("删除所有Holiday",holiday.grid,holiday.delete,holiday.delete_yes,holiday.grid_Mask);
        m_callcontrol.addHolidayByDay("NoHoliday",time2,time2);
        ys_apply();
    }
    @Test
    public void G6_checkPriority_NoHoliday(){
        Reporter.infoExec(" 2001拨打99999，预期呼入成功，分机1100接听");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1100, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1100状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1100状态为TALKING，实际状态为"+getExtensionStatus(1100, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1100 <1100>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void G7_checkPriority_NoWorkday(){
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        Reporter.infoExec("修改InRoute10_Conference呼入路由:编辑workday当前时间不是上班时间");
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute10_Conference",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        add_inbound_route.SetTimeConditionTableviewDestition(2,1,"NoWorkday");
        add_inbound_route.edit_save();
        ys_apply();

        Reporter.infoExec(" 2001拨打99999，预期呼入成功，分机1000接听");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void G8_timeDelay() throws InterruptedException {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");//设置获取到的时间显示格式
        String time1 = dateFormat.format( date );
        System.out.println("time1:"+time1);
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, +3);
        date = calendar.getTime();
        String time2 = dateFormat.format( date );
        System.out.println("time2:"+time2);
//        新增一条TimeCondition：三分钟后为上班时间
        settings.callControl_tree.click();
        time_Conditions.timeConditions.click();
        timeConditions.timeConditions.click();
        Reporter.infoExec(" 添加时间条件threeMinuteWork:三分钟后为上班时间"); //执行操作
        m_callcontrol.addTimeContion("threeMinuteWork",time2,"23:59",false,"all");
//        呼入路由的时间条件中新增twoMinuteWork
        inboundRoutes.inboundRoutes.click();
        Reporter.infoExec("修改InRoute10_Conference呼入路由:新增threeMinuteWork");
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute10_Conference",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        add_inbound_route.addTimeCondition.click();
        ys_waitingTime(1000);
        add_inbound_route.SetTimeConditionTableviewDestition(3,1,"threeMinuteWork");
        add_inbound_route.SetTimeConditionTableviewDestination(3,2,add_inbound_route.s_ringGroup);
        add_inbound_route.edit_save();
        ys_apply();

//        通话验证——还未到三分钟时
        Reporter.infoExec(" 2001拨打99999，预期呼入成功，分机1000接听");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"G8_timeDelay()分机1000.jpg");
            Reporter.sendReport("link","Error: " + "G8_timeDelay()调试", SCREENSHOT_PATH +"G8_timeDelay()分机1000.jpg");
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",SPS," ",communication_inbound);
//       等待三分钟后呼入
        ys_waitingTime(180000);
        ys_waitingMask();
        Reporter.infoExec(" 2001拨打99999，预期呼入成功，分机1105接听");
        pjsip.Pj_Make_Call_No_Answer(2001,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(5000);
        if (getExtensionStatus(1100, RING, 10) == RING &&
                getExtensionStatus(1000, RING, 10) == RING &&
                getExtensionStatus(1105, RING, 10) == RING) {
            Reporter.pass(" 分机1100、1000、1105同时响铃");
        } else {
            Reporter.pass(" 状态检测分机1100、1000、1105未同时响铃");
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"G8_timeDelay()响铃组.jpg");
            Reporter.sendReport("link","Error: " + "G8_timeDelay()响铃组", SCREENSHOT_PATH +"G8_timeDelay()响铃组.jpg");
            Reporter.infoExec("1100的状态："+getExtensionStatus(1100, RING, 10));
            Reporter.infoExec("1000的状态"+getExtensionStatus(1000, RING, 10));
            Reporter.infoExec("1105的状态"+getExtensionStatus(1105, RING, 10));
        }
        pjsip.Pj_Answer_Call(1105,true);
        ys_waitingTime(5000);
        if (getExtensionStatus(1105, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1105状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"G8_timeDelay()分机1105.jpg");
            Reporter.sendReport("link","Error: " + "G8_timeDelay()调试", SCREENSHOT_PATH +"G8_timeDelay()分机1105.jpg");
            Reporter.error(" 预期分机1105状态为TALKING，实际状态为"+getExtensionStatus(1105, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1105 <6200(1105)>","Answered",SPS,"",communication_inbound);
    }
    @Test
    public void G9_timeOrder1(){
        Reporter.infoExec("修改InRoute10_Conference呼入路由目的地");
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute10_Conference",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        add_inbound_route.SetTimeConditionTableviewDestition(1,1,"workday_24hour");
        add_inbound_route.SetTimeConditionTableviewDestination(1,2,add_inbound_route.s_ivr);
        add_inbound_route.SetTimeConditionTableviewDestition(2,1,"workday2_24hour");
        add_inbound_route.SetTimeConditionTableviewDestination(2,2,add_inbound_route.s_conference);
        add_inbound_route.edit_save();
        ys_apply();

//        通话测试——还未变换位置前
        Reporter.infoExec(" 2001拨打99999，预期呼入成功，IVR6500接听");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","6500","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void G9_timeOrder2(){
        Reporter.infoExec("修改InRoute10_Conference呼入路由顺序");
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute10_Conference",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        gridClick(add_inbound_route.add_Inbound_Route_TimeCondition_Grid, 2, add_inbound_route.gridUp);
        add_inbound_route.edit_save();
        ys_apply();

//        通话测试——第二行移至第一行，预期呼入到Conference
        Reporter.infoExec(" 2001拨打99999，预期呼入成功，Conference6400接听");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","6400","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void G9_timeOrder3() {
        Reporter.infoExec("修改InRoute10_Conference呼入路由顺序");
        gridClick(inboundRoutes.grid, gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute10_Conference", sort_ascendingOrder), inboundRoutes.gridEdit);
        ys_waitingMask();
        gridClick(add_inbound_route.add_Inbound_Route_TimeCondition_Grid, 3, add_inbound_route.gridTop);
        add_inbound_route.edit_save();
        ys_apply();

//        通话测试——第三行移至第一行，预期呼入到响铃组
        Reporter.infoExec(" 2001拨打99999，预期呼入成功，分机1105接听");
        pjsip.Pj_Make_Call_No_Answer(2001, "99999", DEVICE_ASSIST_2);
        ys_waitingTime(5000);
        if (getExtensionStatus(1100, RING, 10) == RING &&
                getExtensionStatus(1000, RING, 10) == RING &&
                getExtensionStatus(1105, RING, 10) == RING) {
            Reporter.pass(" 分机1100、1000、1105同时响铃");
        } else {
            Reporter.pass(" 状态检测分机1100、1000、1105未同时响铃");
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"G9_timeOrder3()响铃组.jpg");
            Reporter.sendReport("link","Error: " + "G9_timeOrder3()响铃组", SCREENSHOT_PATH +"G9_timeOrder3()响铃组.jpg");
            Reporter.infoExec("1100的状态："+getExtensionStatus(1100, RING, 10));
            Reporter.infoExec("1000的状态"+getExtensionStatus(1000, RING, 10));
            Reporter.infoExec("1105的状态"+getExtensionStatus(1105, RING, 10));
        }
        pjsip.Pj_Answer_Call(1105, false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1105, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1105状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1105状态为TALKING，实际状态为"+getExtensionStatus(1105, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>", "1105 <6200(1105)>", "Answered", SPS, "", communication_inbound);

    }
    @Test
    public void H1_FeatureCode1() {
//        pjsip.Pj_Unregister_Account(1000);
//        pjsip.Pj_Unregister_Account(1100);
//        pjsip.Pj_Unregister_Account(1101);
//        pjsip.Pj_Unregister_Account(1105);
//        pjsip.Pj_Unregister_Account(1106);
//        pjsip.Pj_Unregister_Account(3001);
//        pjsip.Pj_Unregister_Account(2000);
//        pjsip.Pj_Unregister_Account(2001);
//        pjsip.Pj_Unregister_Account(2005);
//        pjsip.Pj_Unregister_Account(4000);
//        pjsip.Pj_Unregister_Account(4001);
//        ys_waitingTime(6000);
//        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
//        pjsip.Pj_CreateAccount(1000, "Yeastar202", "UDP", UDP_PORT, 1);
//        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
//        pjsip.Pj_CreateAccount(1100, "Yeastar202", "UDP", UDP_PORT, 2);
//        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
//        pjsip.Pj_CreateAccount(1101, "Yeastar202", "UDP", UDP_PORT, 3);
//        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
//        pjsip.Pj_CreateAccount(1105, "Yeastar202", "UDP", UDP_PORT, 7);
//        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1106"); //执行操作
//        pjsip.Pj_CreateAccount(1106, "Yeastar202", "UDP", UDP_PORT, 8);
//
//        Reporter.infoExec(" 辅助设备1注册分机3001"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 3001, "Yeastar202", -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
//        pjsip.Pj_Register_Account_WithoutAssist(3001, DEVICE_ASSIST_1);
//
//        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 2000, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
//        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);
//
//        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 2001, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
//        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
//
//        Reporter.infoExec(" 辅助设备2注册分机2005"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 2005, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
//        pjsip.Pj_Register_Account_WithoutAssist(2005, DEVICE_ASSIST_2);
//
//        Reporter.infoExec(" 辅助设备3注册分机4000"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 4000, "Yeastar202", -1, DEVICE_ASSIST_3, UDP_PORT_ASSIST_3);
//        pjsip.Pj_Register_Account_WithoutAssist(4000, DEVICE_ASSIST_3);
//
//        Reporter.infoExec(" 辅助设备3注册分机4001"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 4001, "Yeastar202", -1, DEVICE_ASSIST_3, UDP_PORT_ASSIST_3);
//        pjsip.Pj_Register_Account_WithoutAssist(4001, DEVICE_ASSIST_3);
//        closePbxMonitor();

        closeSetting();
        Reporter.infoExec(" 设置分机1000具有拨打时间特征码的权限"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.general_panel.click();
        ys_waitingTime(3000);
        m_general.setExtensionPermission(true, "*88", "1000");
        ys_waitingTime(1000);
        ys_apply();
    }
    @Test
    public void H1_FeatureCode2() {
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        Reporter.infoExec("检查时间特征码+修改InRoute10_Conference呼入路由：将workday_24hour改为NoWorkday");
        gridClick(inboundRoutes.grid, gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute10_Conference", sort_ascendingOrder), inboundRoutes.gridEdit);
        ys_waitingMask();
        String featureCode1 = String.valueOf(gridContent(add_inbound_route.add_Inbound_Route_TimeCondition_Grid, 1, add_inbound_route.gridcolumn_FeatureCode));
        String featureCode2 = String.valueOf(gridContent(add_inbound_route.add_Inbound_Route_TimeCondition_Grid, 2, add_inbound_route.gridcolumn_FeatureCode));
        String featureCode3 = String.valueOf(gridContent(add_inbound_route.add_Inbound_Route_TimeCondition_Grid, 3, add_inbound_route.gridcolumn_FeatureCode));

        add_inbound_route.SetTimeConditionTableviewDestition(3, 1, "NoWorkday");
        add_inbound_route.save.click();
        if(version[1].equals("6")){
//        30.6.0.X
            if (featureCode1.equals("*8803") && featureCode2.equals("*8801") && featureCode3.equals("*8802")) {
                Reporter.pass(" 特征码生成正确");
            } else {
                Reporter.pass(" 特征码生成错误！预期值为*8804、*8802、*8803，实际featureCode1：" + featureCode1 + ";" + "featureCode2：" + featureCode2 + "featureCode3：" + featureCode3);
                System.out.println(" 特征码生成错误！featureCode1：" + featureCode1 + ";" + "featureCode2：" + featureCode2 + "featureCode3：" + featureCode3);
                YsAssert.fail("特征码生成错误！");
            }
        }else {
//        30.7.0.X——在30.7.X部分对otherTime也增加了特征码，所以其他部分的特征码从*802开始
            if (featureCode1.equals("*8804") && featureCode2.equals("*8802") && featureCode3.equals("*8803")) {
                Reporter.pass(" 特征码生成正确");
            } else {
                Reporter.pass(" 特征码生成错误！预期值为*8804、*8802、*8803，实际featureCode1：" + featureCode1 + ";" + "featureCode2：" + featureCode2 + "featureCode3：" + featureCode3);
                System.out.println(" 特征码生成错误！featureCode1：" + featureCode1 + ";" + "featureCode2：" + featureCode2 + "featureCode3：" + featureCode3);
                YsAssert.fail("特征码生成错误！");
            }
        }
    }
    @Test
    public void H1_FeatureCode3(){
        Reporter.infoExec("检查时间特征码");
        gridClick(inboundRoutes.grid,2, inboundRoutes.gridEdit);
        ys_waitingMask();
        setCheckBox(add_inbound_route.enableTimeCondition,true);
        ys_waitingTime(1000);
        add_inbound_route.addTimeCondition.click();
        ys_waitingTime(1000);
        add_inbound_route.addTimeCondition.click();
        ys_waitingTime(1000);
        String featureCode1 = String.valueOf(gridContent(add_inbound_route.add_Inbound_Route_TimeCondition_Grid,1,add_inbound_route.gridcolumn_FeatureCode));
        String featureCode2 = String.valueOf(gridContent(add_inbound_route.add_Inbound_Route_TimeCondition_Grid,2,add_inbound_route.gridcolumn_FeatureCode));
        add_inbound_route.cancel.click();
        if (version[1].equals("6")) {
//        30.6.0.X
            if (featureCode1.equals("*88101") && featureCode2.equals("*88102")) {
                Reporter.pass(" 特征码生成正确");
            } else {
                Reporter.pass(" 特征码生成错误！预期值*88102、*88103，实际featureCode1：" + featureCode1 + ";" + "featureCode2：" + featureCode2);
                System.out.println(" 特征码生成错误！featureCode1：" + featureCode1 + ";" + "featureCode2：" + featureCode2);
                YsAssert.fail("特征码生成错误！");
            }
        }else {
//        30.7.0.X
            if (featureCode1.equals("*88102") && featureCode2.equals("*88103")) {
                Reporter.pass(" 特征码生成正确");
            } else {
                Reporter.pass(" 特征码生成错误！预期值*88102、*88103，实际featureCode1：" + featureCode1 + ";" + "featureCode2：" + featureCode2);
                System.out.println(" 特征码生成错误！featureCode1：" + featureCode1 + ";" + "featureCode2：" + featureCode2);
                YsAssert.fail("特征码生成错误！");
            }
        }
    }
    @Test
    public void H2_checkFeatureCode1(){
        if (version[1].equals("6")) {
//        30.6.0.X
            Reporter.infoExec(" 分机1000拨打特征码*8802强制启用非工作时间"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*8802", DEVICE_IP_LAN, false);
        }else {
//        30.7.0.X
            Reporter.infoExec(" 分机1000拨打特征码*8803强制启用非工作时间"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*8803", DEVICE_IP_LAN, false);
        }
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        //        建立通话验证特征码生效
        Reporter.infoExec(" 建立通话验证特征码启用生效：3001拨打3000通过sip外线呼入，预期呼入到ivr");
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","6500","Answered",SIPTrunk," ",communication_inbound);
    }
    @Test
    public void H2_checkFeatureCode2(){
        if (version[1].equals("6")) {
//        30.6.0.X
            Reporter.infoExec(" 分机1000拨打特征码*8801强制启用工作时间"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*8801", DEVICE_IP_LAN, false);
        }else {
//        30.7.0.X
            Reporter.infoExec(" 分机1000拨打特征码*8802强制启用工作时间"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*8802", DEVICE_IP_LAN, false);
        }
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
//        建立通话验证特征码生效
        Reporter.infoExec(" 建立通话验证特征码启用生效：3001拨打3000通过sip外线呼入，预期呼入到conference");
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1);
        ys_waitingTime(8000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","6400","Answered",SIPTrunk," ",communication_inbound);
    }
    @Test
    public void H2_checkFeatureCode3(){
        Reporter.infoExec(" 分机1000拨打特征码*8800重置"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*8800",DEVICE_IP_LAN,false);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
//        建立通话验证特征码生效
        Reporter.infoExec(" 2001拨打99999，预期呼入到响铃组，分机1105接听");
        pjsip.Pj_Make_Call_No_Answer(2001, "99999", DEVICE_ASSIST_2);
        ys_waitingTime(5000);
        if (getExtensionStatus(1100, RING, 10) == RING &&
                getExtensionStatus(1000, RING, 10) == RING &&
                getExtensionStatus(1105, RING, 10) == RING) {
            Reporter.pass(" 分机1100、1000、1105同时响铃");
        } else {
            Reporter.pass(" 状态检测分机1100、1000、1105未同时响铃");
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"H2_checkFeatureCode4()响铃组.jpg");
            Reporter.sendReport("link","Error: " + "H2_checkFeatureCode4()响铃组", SCREENSHOT_PATH +"H2_checkFeatureCode4()响铃组.jpg");
            Reporter.infoExec("1100的状态："+getExtensionStatus(1100, RING, 10));
            Reporter.infoExec("1000的状态"+getExtensionStatus(1000, RING, 10));
            Reporter.infoExec("1105的状态"+getExtensionStatus(1105, RING, 10));
        }
        pjsip.Pj_Answer_Call(1105, true);
        ys_waitingTime(5000);
        if (getExtensionStatus(1105, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1105状态--TALKING，通话正常建立");
        } else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH +"H2_checkFeatureCode4()分机1105.jpg");
            Reporter.sendReport("link","Error: " + "H2_checkFeatureCode4()调试", SCREENSHOT_PATH +"H2_checkFeatureCode4()分机1105.jpg");
            Reporter.error(" 预期分机1105状态为TALKING，实际状态为"+getExtensionStatus(1105, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>", "1105 <6200(1105)>", "Answered", SPS, "", communication_inbound);
    }
    @Test
    public void H3_editDestination1() {
        closeSetting();
        Reporter.infoExec(" 设置分机1000具有拨打时间特征码的权限"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.general_panel.click();
        ys_waitingTime(3000);
        m_general.setExtensionPermission(true, "*8", "1000");
        ys_waitingTime(1000);
        ys_apply();
    }
    @Test
    public void H3_editDestination2() {
        Reporter.infoExec("修改TimeCondition的目的地");
        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        ys_waitingTime(2000);
        inboundRoutes.inboundRoutes.click();
        gridClick(inboundRoutes.grid, gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute10_Conference", sort_ascendingOrder), inboundRoutes.gridEdit);
        ys_waitingMask();
        ys_waitingTime(1000);
        add_inbound_route.addTimeCondition.click();
        ys_waitingTime(1000);
        add_inbound_route.SetTimeConditionTableviewDestination(1,2,add_inbound_route.s_disa);
        add_inbound_route.SetTimeConditionTableviewDestination(2,2,add_inbound_route.s_callback);
        add_inbound_route.SetTimeConditionTableviewDestination(3,2,add_inbound_route.s_outboundRoute);
        add_inbound_route.SetTimeConditionTableviewDestition(4,1,"workday_24hour");
        add_inbound_route.SetTimeConditionTableviewDestination(4,2,add_inbound_route.s_extensin);
        add_inbound_route.SetTimeConditionTableviewDestitionEx(4,3,"1106");
        add_inbound_route.edit_save();
        ys_apply();
    }
    @Test
    public void I1_Disa(){
        Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，进入DISA，拨13001通过SIP1呼出");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999", DEVICE_ASSIST_2,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"1","3","0","0","1","#");
        ys_waitingTime(5000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","13001","Answered",SPS,SIPTrunk,communication_outRoute);
    }
    @Test
    public void I2_callBack() {
        if (version[1].equals("6")) {
//        通过强制启用第二时间条件，来调整呼入目的地_30.6.0.X
            Reporter.infoExec(" 分机1000拨打特征码*801"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*801", DEVICE_IP_LAN, false);
        }else {
//        通过强制启用第二时间条件，来调整呼入目的地_30.7.0.X
            Reporter.infoExec(" 分机1000拨打特征码*802"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*802", DEVICE_IP_LAN, false);
        }
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();

        Reporter.infoExec(" 2000拨打99999通过sps外线呼入，等待2秒挂断通话"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(2000);
        pjsip.Pj_Hangup_All();
        ys_waitingTime(2000);
        YsAssert.assertEquals(getExtensionStatus(2000,RING,20),RING,"预期2000会响铃");
        pjsip.Pj_Answer_Call(2000,false);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING && getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000和1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000和1000状态为TALKING，实际2000状态为"+getExtensionStatus(1000, TALKING, 8)+";1000状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("callback2000","1000 <1000>","Answered",SPS,"",communication_callback);
    }
    @Test
    public void I3_FxsExtension(){
        if (version[1].equals("6")) {
//        通过强制启用第四时间条件，来调整呼入目的地_30.6.0.X
        Reporter.infoExec(" 分机1000拨打特征码*804"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*804",DEVICE_IP_LAN,false);
        }else {
//        通过强制启用第四时间条件，来调整呼入目的地_30.7.0.X
            Reporter.infoExec(" 分机1000拨打特征码*805"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*805", DEVICE_IP_LAN, false);
        }
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();

        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        if (!FXS_1.equals("null")) {
//          sps外线测试 && 分机1106接听电话
            Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1106，分机接听"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
            ys_waitingTime(8000);
            if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>","1106 <1106>","Answered",SPS," ",communication_inbound);
        }
    }
    @Test
    public void I4_timecondition_outbound_sip(){
//        pjsip.Pj_Unregister_Account(1000);
//        pjsip.Pj_Unregister_Account(1100);
//        pjsip.Pj_Unregister_Account(1101);
//        pjsip.Pj_Unregister_Account(1105);
//        pjsip.Pj_Unregister_Account(1106);
//        pjsip.Pj_Unregister_Account(3001);
//        pjsip.Pj_Unregister_Account(2000);
//        pjsip.Pj_Unregister_Account(2001);
//        pjsip.Pj_Unregister_Account(2005);
//        pjsip.Pj_Unregister_Account(4000);
//        pjsip.Pj_Unregister_Account(4001);
//        ys_waitingTime(6000);
//        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
//        pjsip.Pj_CreateAccount(1000, "Yeastar202", "UDP", UDP_PORT, 1);
//        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
//        pjsip.Pj_CreateAccount(1100, "Yeastar202", "UDP", UDP_PORT, 2);
//        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
//        pjsip.Pj_CreateAccount(1101, "Yeastar202", "UDP", UDP_PORT, 3);
//        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
//        pjsip.Pj_CreateAccount(1105, "Yeastar202", "UDP", UDP_PORT, 7);
//        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1106"); //执行操作
//        pjsip.Pj_CreateAccount(1106, "Yeastar202", "UDP", UDP_PORT, 8);
//
//        Reporter.infoExec(" 辅助设备1注册分机3001"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 3001, "Yeastar202", -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
//        pjsip.Pj_Register_Account_WithoutAssist(3001, DEVICE_ASSIST_1);
//
//        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 2000, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
//        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);
//
//        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 2001, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
//        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
//
//        Reporter.infoExec(" 辅助设备2注册分机2005"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 2005, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
//        pjsip.Pj_Register_Account_WithoutAssist(2005, DEVICE_ASSIST_2);
//
//        Reporter.infoExec(" 辅助设备3注册分机4000"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 4000, "Yeastar202", -1, DEVICE_ASSIST_3, UDP_PORT_ASSIST_3);
//        pjsip.Pj_Register_Account_WithoutAssist(4000, DEVICE_ASSIST_3);
//
//        Reporter.infoExec(" 辅助设备3注册分机4001"); //执行操作
//        pjsip.Pj_CreateAccount("UDP", 4001, "Yeastar202", -1, DEVICE_ASSIST_3, UDP_PORT_ASSIST_3);
//        pjsip.Pj_Register_Account_WithoutAssist(4001, DEVICE_ASSIST_3);
//        closePbxMonitor();

        if (version[1].equals("6")) {
            //        通过强制启用第二时间条件，来调整呼入目的地——30.6.0.X
        Reporter.infoExec(" 分机1000拨打特征码*802"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*802",DEVICE_IP_LAN,false);
        }else {
            //        通过强制启用第二时间条件，来调整呼入目的地——30.7.0.X
            Reporter.infoExec(" 分机1000拨打特征码*803"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*803", DEVICE_IP_LAN, false);
        }
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();

        Reporter.infoExec(" 2001拨打9913001通过SPS外线呼入转sip呼出");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"9913001",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","13001","Answered",SPS,SIPTrunk,communication_outRoute);
    }
    @Test
    public void I5_timecondition_outbound_iax(){
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
//        编辑timeCondition的目的地
        Reporter.infoExec("修改第三时间条件目的地到outbound_iax");
        gridClick(inboundRoutes.grid, gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute10_Conference", sort_ascendingOrder), inboundRoutes.gridEdit);
        ys_waitingMask();
        add_inbound_route.SetTimeConditionTableviewDestition(3,3,"OutRoute2_iax");
        add_inbound_route.edit_save();
        ys_apply();

//        iax测试 && 分机接听
        Reporter.infoExec(" 2001拨打9923001通过sps外线呼入转iax外线到分机3001，分机接听"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"9923001",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","23001","Answered",SPS, IAXTrunk,communication_outRoute);
    }
    @Test
    public void I6_timecondition_outbound_spx(){
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
//        编辑timeCondition的目的地
        Reporter.infoExec("修改第三时间条件目的地到outbound_spx");
        gridClick(inboundRoutes.grid, gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute10_Conference", sort_ascendingOrder), inboundRoutes.gridEdit);
        ys_waitingMask();
        add_inbound_route.SetTimeConditionTableviewDestition(3,3,"OutRoute4_spx");
        add_inbound_route.edit_save();
        ys_apply();

//        spx测试 && 分机未接听
        Reporter.infoExec(" 2001拨打9942000通过sps外线呼入转spx外线到分机2000，分机未接"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"9942000",DEVICE_ASSIST_2);
        ys_waitingTime(8000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","42000","No Answer",SPS, SPX,communication_outRoute);
    }
    @Test
    public void I7_timecondition_outbound_bri() {
        if (PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)) {
            return;
        }
        if (!BRI_1.equals("null")) {
//        编辑timeCondition的目的地
            Reporter.infoExec("修改第三时间条件目的地到outbound_bri");
            gridClick(inboundRoutes.grid, gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute10_Conference", sort_ascendingOrder), inboundRoutes.gridEdit);
            ys_waitingMask();
            add_inbound_route.SetTimeConditionTableviewDestition(3, 3, "OutRoute6_bri");
            add_inbound_route.edit_save();
            ys_apply();

//        BRI测试 && 分机接听
            Reporter.infoExec(" 2001拨打9962000通过sps外线呼入转bri外线到分机2000，分机接听"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2001, "9962000", DEVICE_ASSIST_2,false);
            ys_waitingTime(8000);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>", "62000", "Answered", SPS, BRI_1, communication_outRoute);
        }
    }
    @Test
    public void I8_timecondition_outbound_e1() {
        if (PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)) {
            return;
        }
        if (!E1.equals("null")) {
//        编辑timeCondition的目的地
            Reporter.infoExec("修改第三时间条件目的地到outbound_E1");
            gridClick(inboundRoutes.grid, gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute10_Conference", sort_ascendingOrder), inboundRoutes.gridEdit);
            ys_waitingMask();
            add_inbound_route.SetTimeConditionTableviewDestition(3, 3, "OutRoute7_e1");
            add_inbound_route.edit_save();
            ys_apply();

//        E1测试 && 分机接听
            Reporter.infoExec(" 2001拨打9972000通过sps外线呼入转E1外线到分机2000，分机接听"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2001, "9972000", DEVICE_ASSIST_2,false);
            ys_waitingTime(8000);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>", "72000", "Answered", SPS, E1, communication_outRoute);
        }
    }
    @Test
    public void I8_timecondition_outbound_account() {
        if (!DEVICE_ASSIST_3.equals("null")) {
//        编辑timeCondition的目的地
            Reporter.infoExec("修改第三时间条件目的地到outbound_account");
            gridClick(inboundRoutes.grid, gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute10_Conference", sort_ascendingOrder), inboundRoutes.gridEdit);
            ys_waitingMask();
            add_inbound_route.SetTimeConditionTableviewDestition(3, 3, "OutRoute9_account");
            add_inbound_route.edit_save();
            ys_apply();

//        account测试 && 分机接听
            Reporter.infoExec(" 2001拨打9991111通过sps外线呼入转account外线到分机4000，分机接听"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2001, "99914000", DEVICE_ASSIST_2,false);
            ys_waitingTime(8000);
            if (getExtensionStatus(4000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机4000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机4000状态为TALKING，实际状态为"+getExtensionStatus(4000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>", "914000", "Answered", SPS, ACCOUNTTRUNK, communication_outRoute);
        }
    }
    @Test
    public void I9_timecondition_extensionRange(){
        Reporter.infoExec("修改第三时间条件目的地到ExtensionRange");
        gridClick(inboundRoutes.grid, gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute10_Conference", sort_ascendingOrder), inboundRoutes.gridEdit);
        ys_waitingMask();
        add_inbound_route.DIDPatem.setValue("5503301-5503306");
        add_inbound_route.SetTimeConditionTableviewDestination(3,2,add_inbound_route.s_extension_range);
        add_inbound_route.SetTimeConditionTableviewDestination(3, 3, "1100-1105");
        add_inbound_route.edit_save();
        ys_apply();

        //        sps外线测试&&分机1101未接电话
        Reporter.infoExec(" 2001拨打995503302通过sps外线呼入到分机1101"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"995503302",DEVICE_ASSIST_2);
        ys_waitingTime(8000);
        if (getExtensionStatus(1101, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1101状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1101状态为TALKING，实际状态为"+getExtensionStatus(1101, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>", "1101 <1101>", "No Answer", SPS, " ", communication_inbound);

//        SPS外线测试&&分机1105接听电话
        Reporter.infoExec(" 2001拨打995503306通过sps外线呼入到分机1105"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"995503306",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1105, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1105状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1105状态为TALKING，实际状态为"+getExtensionStatus(1105, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1105 <1105>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void J1_disTimeCondition(){
        Reporter.infoExec("取消勾选TimeConditon");
        gridClick(inboundRoutes.grid, gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute10_Conference", sort_ascendingOrder), inboundRoutes.gridEdit);
        ys_waitingMask();
        setCheckBox(add_inbound_route.enableTimeCondition,false);
        add_inbound_route.edit_save();
        ys_apply();

//        通话验证
        Reporter.infoExec(" 2001拨打995503306通过sps外线呼入到conference"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"995503306",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","6400","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void J2_deleteConference(){
        settings.callFeatures_tree.click();
        conference.conference.click();
        conference.add.shouldBe(Condition.exist);
        Reporter.infoExec(" 全部勾选-确定删除"); //执行操作
//        全部勾选
        gridSeleteAll(conference.grid);
//        点击删除按钮
        conference.delete.click();
        conference.delete_yes.click();
        ys_waitingLoading(conference.grid_Mask);
        ys_apply();
        int row = Integer.parseInt(String.valueOf(gridLineNum(conference.grid)));
        YsAssert.assertEquals(row,0,"删除全部的Conference-确定删除");
    }
    @Test
    public void J3_checkConference(){
//       删除了conference后，目的地直接到hangup
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到conference,预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 2001拨打99999通过sps外线呼入到conference,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打99999通过sps外线呼入到conference,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打99999通过sps外线呼入到分机conference,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void K1_deletePartRoute_no(){
        settings.callControl_tree.click();
        ys_waitingTime(2000);
        setPageShowNum(inboundRoutes.grid,100);

        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid)));
        Reporter.infoExec("期望值row1:" + row1);
        int row2 = Integer.parseInt(String.valueOf(gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute2_HangUp", sort_ascendingOrder)));
        Reporter.infoExec("要取消勾选的row2:" + row2);
        int row3 = Integer.parseInt(String.valueOf(gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute3_SipExtension", sort_ascendingOrder)));
        Reporter.infoExec("要取消勾选的row3:" + row3);
        ys_waitingTime(1000);
//        全部勾选
        gridSeleteAll(inboundRoutes.grid);
//        取消勾选OutboundRoute2和OutboundRoute3
        gridCheck(inboundRoutes.grid, row2, inboundRoutes.gridcolumn_Check);
        gridCheck(inboundRoutes.grid, row3, inboundRoutes.gridcolumn_Check);
//        点击删除按钮
        inboundRoutes.delete.click();
        inboundRoutes.delete_no.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        int row4 = Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid)));
        System.out.println("实际值row4:" + row4);
        YsAssert.assertEquals(row4, row1, "全部勾选，再取消某条的勾选后-取消删除");
    }
    @Test
    public void K2_deletePartRoute_yes(){
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-确定删除"); //执行操作
        inboundRoutes.delete.click();
        inboundRoutes.delete_yes.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid)));
        System.out.println("实际row6:" + row1);
        ys_apply();
        YsAssert.assertEquals(row1, 2, "全部勾选，再取消某条的勾选后-确定删除");
    }
    @Test
    public void K3_deleteAllRoute_no(){
        Reporter.infoExec(" 全部勾选-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid)));
//        全部勾选
        gridSeleteAll(inboundRoutes.grid);
//        点击删除按钮
        inboundRoutes.delete.click();
        inboundRoutes.delete_no.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid)));
        System.out.println("实际row2:" + row2);
        YsAssert.assertEquals(row1, row2, "全部勾选-取消删除");
    }
    @Test
    public void K4_deleteAllRoute_yes(){
        Reporter.infoExec(" 全部勾选-确定删除"); //执行操作
        inboundRoutes.delete.click();
        inboundRoutes.delete_yes.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        int row = Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid)));
        System.out.println("实际row:" + row);
        ys_apply();
        YsAssert.assertEquals(row, 0, "全部勾选-确定删除");
    }
    @Test
    public void L1_import(){
        inboundRoutes.Import.click();
        ys_waitingTime(1000);
        inboundRoutes.browse.click();
        ys_waitingTime(1000);
        Reporter.infoExec(EXPORT_PATH +"InboundRoute.csv");
        importFile(EXPORT_PATH +"InboundRoute.csv");
        inboundRoutes.Import_import.click();
        ys_waitingTime(2000);
        inboundRoutes.import_OK.click();
        ys_apply();
    }
    @Test
    public void L2_checkDIDPriority1(){
        Reporter.infoExec(" 2001拨打9992999");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"9992999",DEVICE_ASSIST_2,false);
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
    public void L3_checkDIDPriority2(){
        Reporter.infoExec(" 2001拨打9982999");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"9982999",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1100, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1100状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1100状态为TALKING，实际状态为"+getExtensionStatus(1100, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1100 <1100>","Answered",SPS," ",communication_inbound);

        //        验证这条呼入路由只对SPS外线有效
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入,预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1);
        ys_waitingTime(8000);
        int state = getExtensionStatus(3000,IDLE,8);
        if(state == IDLE){
            Reporter.infoExec(" 3000通话状态为："+state);
            Reporter.infoExec(" 3001拨打3000通过sip外线呼入,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 3001拨打3000通过sip外线呼入,预期呼入失败,实际呼入成功");
            Reporter.infoExec(" 3000通话状态为："+state);
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 3001拨打3000通过sip外线呼入,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void L4_checkDIDPriority3(){
        Reporter.infoExec(" 2001拨打9991999");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"9991999",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1105, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1105状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1105状态为TALKING，实际状态为"+getExtensionStatus(1105, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1105 <1105>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void M1_deleteOneRoute_no(){
        Reporter.infoExec(" 删除单个呼入路由InRoute3——选择no"); //执行操作
//       定位要删除的那条呼入路由
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute3", sort_ascendingOrder)));
        //还未操作前呼出路由的所有条数
        int rows = Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid)));
        System.out.println("预期值:" + rows);
        gridClick(inboundRoutes.grid, row, inboundRoutes.gridDelete);
        inboundRoutes.delete_no.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid)));
        System.out.println("实际值:" + row1);
        YsAssert.assertEquals(row1, rows, "删除呼出路由OutboundRoute1-取消删除");
    }
    @Test
    public void M2_deleteOneRoute_yes(){
        Reporter.infoExec(" 删除单个呼入路由InRoute3——选择yes"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid)));
        //       定位要删除的那条呼入路由
        int row1 = Integer.parseInt(String.valueOf(gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute3", sort_ascendingOrder)));
        gridClick(inboundRoutes.grid, row1, inboundRoutes.gridDelete);
        inboundRoutes.delete_yes.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid)));
        System.out.println("实际值row2:" + row2);
        int row3 = row - 1;
        System.out.println("期望值row3:" + row3);
        ys_apply();
        YsAssert.assertEquals(row2, row3, "删除单个呼入路由InRoute3——确定删除");
    }
    @Test
    public void M3_editRoute(){
        Reporter.infoExec("修改InRoute1:DID清空，CallerID设置为3.");
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        add_inbound_route.DIDPatem.clear();
        add_inbound_route.callIDPattem.setValue("3.");
//        新增一条SIP外线，让3001可以呼入
        ArrayList<String> arraytrunk = new ArrayList<>();
        arraytrunk.add(SIPTrunk);
        arraytrunk.add(SPS);
        listSelect(add_inbound_route.list,trunkList,arraytrunk);
        //        保存编辑页面
        add_inbound_route.edit_save();

        Reporter.infoExec("修改InRoute2:DID清空，CallerID设置为X.");
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute2",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        add_inbound_route.DIDPatem.clear();
        add_inbound_route.callIDPattem.setValue("X.");
        //        新增一条SIP外线，让3001可以呼入
        ArrayList<String> arraytrunk2 = new ArrayList<>();
        arraytrunk2.add(SIPTrunk);
        arraytrunk2.add(SPS);
        listSelect(add_inbound_route.list,trunkList,arraytrunk2);
        //        保存编辑页面
        add_inbound_route.edit_save();
        ys_apply();
    }
    @Test
    public void N_checkCallerIDPriority1() throws InterruptedException {
//        3001通过sip外线呼入，匹配到3.的CallerID
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000 <1000>","Answered",SIPTrunk," ",communication_inbound);
    }
    @Test
    public void N_checkCallerIDPriority2() throws InterruptedException {
//        2001通过SPS外线呼入，匹配到X.的CallerID
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1100, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1100状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1100状态为TALKING，实际状态为"+getExtensionStatus(1100, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1100 <1100>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void O_deleteAllRoute(){
        Reporter.infoExec(" 全部勾选-确定删除"); //执行操作
//        全部勾选
        gridSeleteAll(inboundRoutes.grid);
//        点击删除按钮
        inboundRoutes.delete.click();
        inboundRoutes.delete_yes.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        int row = Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid)));
        System.out.println("实际row:" + row);
        ys_apply();
        YsAssert.assertEquals(row, 0, "全部勾选-确定删除");
    }
//    验证无SPS路由规则时，可以直接走分机号码
    @Test
    public void P1_checkSPS(){
        Reporter.infoExec(" 2001拨打991105通过sps外线呼入到分机1105"); //验证SPS路由存在时，走路由规则优先
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991105",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1105, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1105状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1105状态为TALKING，实际状态为"+getExtensionStatus(1105, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1105 <1105>","Answered",SPS," ",communication_inbound);
    }
//    验证无SPX路由规则时，可以直接走分机号码
    @Test
    public void P2_checkSPX(){
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 2001拨打881000通过spx外线呼入到分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"881000",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",SPX," ",communication_inbound);
    }
//    恢复环境：新增呼入路由Inbound1；新增conference1;删除时间条件TimeCondition
    @Test
    public void Q_recovery() throws InterruptedException {
        BeforeTest recover = new BeforeTest();
        recover.F_addInRoute();
//        删除时间条件
        time_Conditions.timeConditions.click();
        Reporter.infoExec(" 删除所有时间条件——Workday");
        deletes("删除所有时间条件——Workday", timeConditions.grid, timeConditions.delete, timeConditions.delete_yes, timeConditions.grid_Mask);
    }
    @Test
    public void Q_recovery2() throws InterruptedException {
        //        添加默认的时间条件
        Reporter.infoExec(" 添加时间条件Workday:默认的工作时间"); //执行操作
        m_callcontrol.addTimeContion("Workday","08:30","12:00",false,"mon","tue","wed","thu","fri");
        Reporter.infoExec(" 编辑Workday");
        gridClick(timeConditions.grid,gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"Workday",sort_ascendingOrder),timeConditions.gridEdit);
        ys_waitingTime(3000);
        add_time_condition.getAddTime(1).click();
//        add_time_condition.addTime.click();
        ys_waitingTime(1000);
        ArrayList<String> arrayStartTime = new ArrayList<>();
        arrayStartTime.add("starthour");
        arrayStartTime.add("endhour");
        ArrayList<String> arrayTime = new ArrayList<>();
        arrayTime.add("14");
        arrayTime.add("18");
        add_time_condition.setTime_One(2,arrayStartTime,arrayTime);
        add_time_condition.save.click();
    }
    @Test
    public void Q_recovery3(){
        //        删除Holiday
        holiday.holiday.click();
        Reporter.infoExec(" 删除所有时间条件——Holiday");
        deletes("删除所有Holiday",holiday.grid,holiday.delete,holiday.delete_yes,holiday.grid_Mask);
    }
    @Test
    public void Q_recovery4() throws InterruptedException {
//        恢复会议室
        BeforeTest recover = new BeforeTest();
        settings.callFeatures_tree.click();
        recover.K_addconference();
    }
    @Test
    public void Q_recovery5(){
//        取消分机1000具有拨打时间特征码的权限
        Reporter.infoExec(" 取消分机1000具有拨打时间特征码的权限"); //执行操作
        settings.general_tree.click();
        featureCode.featureCode.click();
        ys_waitingTime(2000);
        featureCode.setExtensionPermission.click();
        ys_waitingTime(3000);
        featureCode.extension_RemoveAllFromSelect.click();
        ys_waitingTime(2000);
        featureCode.list_save.click();
        featureCode.save.click();
        ys_apply();
    }
    //    AfterMethod是在每个Test执行后都要来执行的方法
    @AfterMethod
    public void AfterMethod(){
        if (cdRandRecordings.deleteCDR.isDisplayed()){
            Reporter.infoExec("admin角色的cdr页面未关闭");
            closeCDRRecord();
            Reporter.infoExec("admin角色的cdr页面已关闭");
        }
        if (pbxMonitor.extension.isDisplayed()){
            Reporter.infoExec("admin角色的PBXMonitor页面未关闭");
            closePbxMonitor();
            Reporter.infoExec("admin角色的PBXMonitor页面已关闭");
        }
    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：======  InboundRoutes  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);
    }
}
