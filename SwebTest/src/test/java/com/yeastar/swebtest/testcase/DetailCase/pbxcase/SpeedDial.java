package com.yeastar.swebtest.testcase.DetailCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * Created by Caroline on 2018/1/3.
 */
public class SpeedDial extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {

        Reporter.infoBeforeClass("开始执行：====== SpeedDial ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && LOGIN_ADMIN.equals("yes") && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
//            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }
    @Test
    public void A1_addExtension(){
        pjsip.Pj_Init();
        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, 1);
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_CreateAccount(1100, EXTENSION_PASSWORD, "UDP", UDP_PORT, 2);
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_CreateAccount(1101, EXTENSION_PASSWORD, "UDP", UDP_PORT, 3);
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备1注册分机3001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3001, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3001, DEVICE_ASSIST_1);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2000, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备3注册分机4000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 4000, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_3, UDP_PORT_ASSIST_3);
        pjsip.Pj_Register_Account_WithoutAssist(4000, DEVICE_ASSIST_3);
        closePbxMonitor();
    }
    @Test
    public void A2_init(){
        //        修改呼出路由
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        outboundRoutes.outboundRoutes.click();
        Reporter.infoExec(" 编辑呼出路由OutRoute3_sps,分机选择全部");
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute3_sps",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        add_outbound_routes.me_AddAllToSelect.click();
        ys_waitingTime(1000);
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        closeSetting();
    }
    @Test
    public void B1_addSpeedDial() throws InterruptedException {
        Reporter.infoExec(" 添加速拨码，speedDialCode=1，phoneNumber=1100");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        speedDial.speedDial.click();
        setPageShowNum(speedDial.grid,100);
        deletes("删除所有速拨码",speedDial.grid,speedDial.delete,speedDial.delete_yes,speedDial.grid_Mask);
        m_callFeature.addSpeedDial("1",1100);
        ys_waitingTime(1000);
        speedDial.speedDialPrefix.setValue("*99");
        speedDial.speedDialPrefix_button.click();
        ys_apply();
    }
    @Test
    public void B2_checkSpeedDial(){
        Reporter.infoExec(" 1000拨打*991，预期呼出成功");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*991",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1100, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1100状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1100状态为TALKING，实际状态为"+getExtensionStatus(1100, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1100 <1100>","Answered"," "," ",communication_internal);
    }
    @Test
    public void B3_editSpeedDial(){
        Reporter.infoExec(" 修改速拨码，speedDialCode=2，phoneNumber=1101");
        gridClick(speedDial.grid,gridFindRowByColumn(speedDial.grid,speedDial.gridcolumn_SpeedDialCode,"1",sort_ascendingOrder),speedDial.gridEdit);
        ys_waitingTime(1000);
        add_speed_dial.speedDialCode.setValue("2");
        add_speed_dial.phoneNumber.setValue(String.valueOf("1101"));
        add_speed_dial.save.click();
        ys_apply();
    }
    @Test
    public void B4_checkSpeedDial1(){
        Reporter.infoExec(" 1000拨打*991，预期呼出失败");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*991",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1000拨打*991，预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打*991，预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail();
        }
    }
    @Test
    public void B4_checkSpeedDial2(){
        Reporter.infoExec(" 1000拨打*992，预期呼出成功");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*992",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1101, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1101状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1101状态为TALKING，实际状态为"+getExtensionStatus(1101, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1101 <1101>","Answered"," "," ",communication_internal);
    }
    @Test
    public void C1_editPrefix(){
        Reporter.infoExec(" 修改speedDialPrefix为*999");
//        合法前缀
        speedDial.speedDialPrefix.setValue("*999");
        speedDial.speedDialPrefix_button.click();
        ys_apply();
    }
    @Test
    public void C2_checkPrefix1(){
        Reporter.infoExec(" 1000拨打*992，预期呼出失败");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*992",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1000拨打*992，预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打*992，预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail();
        }
    }
    @Test
    public void C2_checkPrefix2(){
        Reporter.infoExec(" 1000拨打*99992，预期呼出失败");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*99992",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1000拨打*99992，预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打*99992，预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail();
        }
    }
    @Test
    public void C2_checkPrefix3(){
        Reporter.infoExec(" 1000拨打*9992，预期呼出成功，分机1101接听");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*9992",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1101, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1101状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1101状态为TALKING，实际状态为"+getExtensionStatus(1101, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1101 <1101>","Answered"," "," ",communication_internal);
    }
    @Test
    public void C3_editPrefix(){
        Reporter.infoExec("修改speedDialPrefix为*******");
//        最大长度
        speedDial.speedDialPrefix.setValue("*******");
        speedDial.speedDialPrefix_button.click();
        ys_apply();
    }
    @Test
    public void C4_checkPrefix(){
        Reporter.infoExec("分机1000拨打*******2，预期呼出成功");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*******2",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1101, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1101状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1101状态为TALKING，实际状态为"+getExtensionStatus(1101, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1101 <1101>","Answered"," "," ",communication_internal);
    }

    @Test
    public void C5_editPrefix(){
        Reporter.infoExec("修改speedDialPrefix为0987654");
        //        最大长度
        speedDial.speedDialPrefix.setValue("0987654");
        speedDial.speedDialPrefix_button.click();
        ys_apply();
    }
    @Test
    public void C6_checkPrefix(){
        Reporter.infoExec("分机1000拨打09876542，预期呼出成功");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"09876542",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1101, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1101状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1101状态为TALKING，实际状态为"+getExtensionStatus(1101, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1101 <1101>","Answered"," "," ",communication_internal);
    }
    @Test
    public void C7_editPrefix(){
        Reporter.infoExec("修改speedDialPrefix为*123456");
        //        最大长度
        speedDial.speedDialPrefix.setValue("*123456");
        speedDial.speedDialPrefix_button.click();
        ys_apply();
    }
    @Test
    public void C8_checkPrefix(){
        Reporter.infoExec("分机1000拨打*1234562，预期呼出成功");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*1234562",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1101, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1101状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1101状态为TALKING，实际状态为"+getExtensionStatus(1101, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1101 <1101>","Answered"," "," ",communication_internal);
    }
    //    导入呼出路由，注意不同设备的区别，所以代码中的导入文件只含SIP 和 SPS
    @Test
    public void D1_import(){
        Reporter.infoExec("导入speedDial");
        speedDial.Import.click();
        ys_waitingTime(1000);
        speedDial.Import_browse.click();
        ys_waitingTime(3000);
        importFile(EXPORT_PATH +"speeddial2.csv");
        speedDial.Import_import.click();
        ys_waitingTime(2000);
        speedDial.import_OK.click();
        setPageShowNum(speedDial.grid,100);
    }
    @Test
    public void D2_add_outRouteDial() throws InterruptedException {
        Reporter.infoExec("添加外线速拨码");
        if (!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec("Code：1234567；Number：914000");
            m_callFeature.addSpeedDial("1234567",914000);//ACCOUNT外线
        }
        if(!PRODUCT.equals(CLOUD_PBX)){
            Reporter.infoExec("Code：0000000；Number：23001");
            Reporter.infoExec("Code：*2；Number：42000");
            m_callFeature.addSpeedDial("0000000",23001);//IAX外线
            m_callFeature.addSpeedDial("*2",42000);//SPX外线
            if(!PRODUCT.equals(PC)){
                if(!BRI_1.equals("null")) {
                    Reporter.infoExec("Code：0；Number：62000");
                    m_callFeature.addSpeedDial("0", 62000);//BRI外线
                }
                if(!FXO_1.equals("null")) {
                    Reporter.infoExec("Code：*******；Number：52000");
                    m_callFeature.addSpeedDial("*******", 52000);//PSTN外线
                }
                if(!E1.equals("null")) {
                    Reporter.infoExec("Code：1；Number：72000");
                    m_callFeature.addSpeedDial("1", 72000);//E1外线
                }
                if(!GSM.equals("null")){
                    String gsm = "8" + DEVICE_ASSIST_GSM;
                    System.out.println("GSM号码："+gsm);
                    speedDial.add.click();
                    add_speed_dial.speedDialCode.setValue("#######");
                    add_speed_dial.phoneNumber.setValue(String.valueOf(gsm));
                    add_speed_dial.save.click();
                    ys_waitingLoading(speedDial.grid_Mask);
                    Reporter.infoExec("Code：#######；Number："+String.valueOf(gsm));
                }
            }
        }
    }
    @Test
    public void D3_add_incorrect() throws InterruptedException {
        Reporter.infoExec("添加一个SPS呼出路由前缀不对的速拨码,Code:111,Number:932000");
//        添加一个SPS呼出路由前缀不对的速拨码
        m_callFeature.addSpeedDial("111",932000);
        ys_apply();
    }
    @Test
    public void E1_checkSIP(){
        Reporter.infoExec("验证SIP外线速拨码");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*1234569999999",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","13001","Answered"," ",SIPTrunk,communication_outRoute);
    }
    @Test
    public void E2_checkSPS(){
        Reporter.infoExec("验证SPS外线速拨码");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*123456890*#",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","32000","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void E3_checkAccount(){
        Reporter.infoExec("验证ACCOUNT外线速拨码");
        if (PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        if (!DEVICE_ASSIST_3.equals("null")) {
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*1234561234567", DEVICE_IP_LAN, false);
            ys_waitingTime(5000);
            if (getExtensionStatus(4000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机4000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机4000状态为TALKING，实际状态为"+getExtensionStatus(4000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000 <1000>", "914000", "Answered", " ", ACCOUNTTRUNK, communication_outRoute);
        }
    }
    @Test
    public void E4_checkIAX(){
        Reporter.infoExec("验证IAX外线速拨码");
        if (PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*1234560000000",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","23001","Answered"," ",IAXTrunk,communication_outRoute);
    }
    @Test
    public void E5_checkSPX(){
        Reporter.infoExec("验证SPX外线速拨码");
        if (PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*123456*2",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","42000","Answered"," ",SPX,communication_outRoute);
    }
    @Test
    public void E6_checkBRI(){
        Reporter.infoExec("验证BRI外线速拨码");
        if (PRODUCT.equals(CLOUD_PBX)||PRODUCT.equals(PC)){
            return;
        }
        if(!BRI_1.equals("null")) {
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*1234560", DEVICE_IP_LAN, false);
            ys_waitingTime(5000);
            if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000 <1000>", "62000", "Answered", " ", BRI_1, communication_outRoute);
        }
    }
    @Test
    public void E7_checkPSTN(){
        Reporter.infoExec("验证PSTN外线速拨码");
        if (PRODUCT.equals(CLOUD_PBX)||PRODUCT.equals(PC)){
            return;
        }
        if(!FXO_1.equals("null")) {
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*123456*******", DEVICE_IP_LAN, false);
            ys_waitingTime(5000);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000 <1000>", "52000", "Answered", " ", FXO_1, communication_outRoute);
        }
    }
    @Test
    public void E8_checkE1(){
        Reporter.infoExec("验证E1外线速拨码");
        if (PRODUCT.equals(CLOUD_PBX)||PRODUCT.equals(PC)){
            return;
        }
        if(!E1.equals("null")) {
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*1234561", DEVICE_IP_LAN, false);
            ys_waitingTime(5000);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000 <1000>", "72000", "Answered", " ", E1, communication_outRoute);
        }
    }
    @Test
    public void E9_checkGSM(){
        Reporter.infoExec("验证GSM外线速拨码");
        if (PRODUCT.equals(CLOUD_PBX)||PRODUCT.equals(PC)){
            return;
        }
        if(!GSM.equals("null")) {
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*123456#######", DEVICE_IP_LAN, false);
            ys_waitingTime(5000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000 <1000>", "8"+DEVICE_ASSIST_GSM, "No Answer", " ", GSM, communication_outRoute);
        }
    }
    @Test
    public void F_checkIncorrect(){
        Reporter.infoExec("验证不正确的呼出前缀SPS外线速拨码");
        pjsip.Pj_Make_Call_Auto_Answer(1000, "*123456111", DEVICE_IP_LAN, false);
        ys_waitingTime(8000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1000拨打*12345#111，预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打*12345#111，预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail();
        }
    }
    @Test
    public void G1_editOutRoute(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        ys_waitingTime(2000);
        Reporter.infoExec(" 编辑呼出路由OutRoute3_sps,分机不选择1000");
        int row = gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute3_sps",sort_ascendingOrder);
        System.out.println("row："+row);
        gridClick(outboundRoutes.grid,row,outboundRoutes.gridEdit);
        ys_waitingMask();
        add_outbound_routes.me_RemoveAllFromSelected.click();
        ArrayList<String> arrayex = new ArrayList<>();
        arrayex.add("1100");
        listSelect(add_outbound_routes.list_Extension, extensionList,arrayex);
        ys_waitingTime(1000);
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();
    }
    @Test
    public void G2_checkSPS(){
        Reporter.infoExec("验证分机1000没有SPS呼出权限时，是否可成功呼出");
//        验证分机1000没有SPS呼出权限时，是否可成功呼出
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*123456890*#",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1000拨打*12345#890*#，预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打*12345#890*#，预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail();
        }
    }
    @Test
    public void G3_checkSPS(){
        Reporter.infoExec("验证有SPS呼出权限时，是否可成功呼出");
//        分机1100拥有呼出权限
        pjsip.Pj_Make_Call_Auto_Answer(1100, "*123456890*#", DEVICE_IP_LAN, false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>", "32000", "Answered", " ", SPS, communication_outRoute);
    }
    @Test
    public void H1_deleteOne_no(){
        settings.callFeatures_tree.click();
        speedDial.speedDial.click();
        Reporter.infoExec(" 删除单个SpeedDial——选择no"); //执行操作
//       定位要删除的那条SpeedDial
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(speedDial.grid,speedDial.gridcolumn_SpeedDialCode,"2",sort_ascendingOrder)));
        //还未操作前呼出路由的所有条数
        int rows = Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid)));
        System.out.println("预期值:" + rows);
        gridClick(speedDial.grid, row, speedDial.gridDelete);
        speedDial.delete_no.click();
        ys_waitingLoading(speedDial.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid)));
        System.out.println("实际值:" + row1);
        YsAssert.assertEquals(row1, rows, "删除DISA1-取消删除");
    }
    @Test
    public void H2_deleteOne_yes(){
        Reporter.infoExec(" 删除PIN——选择yes"); //执行操作
        int rows = Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid)));
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(speedDial.grid,speedDial.gridcolumn_SpeedDialCode,"2",sort_ascendingOrder)));
        gridClick(speedDial.grid, row, speedDial.gridDelete);
        speedDial.delete_yes.click();
        ys_waitingLoading(speedDial.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid)));
        System.out.println("实际值row2:" + row2);
        int row3 = rows - 1;
        System.out.println("期望值row3:" + row3);
        ys_apply();
        YsAssert.assertEquals(row2, row3, "删除DISA1——确定删除");
    }
    @Test
    public void H3_deletePart_no(){
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid)));
        System.out.println("期望值row1:" + row1);
        int row2 = Integer.parseInt(String.valueOf(gridFindRowByColumn(speedDial.grid,speedDial.gridcolumn_SpeedDialCode,"9999999",sort_ascendingOrder)));
//        全部勾选
        gridSeleteAll(speedDial.grid);
        ys_waitingTime(1000);
//        取消勾选
        gridCheck(speedDial.grid, row2, speedDial.gridcheck);
//        点击删除按钮
        speedDial.delete.click();
        speedDial.delete_no.click();
        ys_waitingLoading(speedDial.grid_Mask);
        int row4 = Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid)));
        System.out.println("实际值row4:" + row4);
        YsAssert.assertEquals(row4, row1, "全部勾选，再取消某条的勾选后-取消删除");
    }
    @Test
    public void H4_deletePart_yes(){
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-确定删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid)));
        System.out.println("未删除前的row:" + row);
        speedDial.delete.click();
        speedDial.delete_yes.click();
        ys_waitingLoading(speedDial.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid)));
        System.out.println("实际row6:" + row1);
        ys_apply();
        YsAssert.assertEquals(row1, 1, "全部勾选，再取消某条的勾选后-确定删除");
    }
    @Test
    public void H5_deleteAll_no(){
        Reporter.infoExec(" 全部勾选-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid)));
//        全部勾选
        gridSeleteAll(speedDial.grid);
//        点击删除按钮
        speedDial.delete.click();
        speedDial.delete_no.click();
        ys_waitingLoading(speedDial.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid)));
        System.out.println("实际row2:" + row2);
        YsAssert.assertEquals(row1, row2, "全部勾选-取消删除");
    }
    @Test
    public void H6_deleteAll_yes(){
        Reporter.infoExec(" 全部勾选-确定删除"); //执行操作
        speedDial.delete.click();
        speedDial.delete_yes.click();
        ys_waitingLoading(speedDial.grid_Mask);
        int row = Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid)));
        System.out.println("实际row:" + row);
        ys_apply();
        YsAssert.assertEquals(row, 0, "全部勾选-确定删除");
    }
    @Test
    public void I_recovery1(){
        Reporter.infoExec(" 修改速拨码的prefix为*99");
//        修改速拨码的Prefix
        speedDial.speedDialPrefix.setValue("*99");
        speedDial.speedDialPrefix_button.click();
        ys_apply();
    }
    @Test
    public void I_recovery2(){
//        修改呼出路由
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        Reporter.infoExec(" 编辑呼出路由OutRoute3_sps,分机选择全部");
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute3_sps",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        add_outbound_routes.me_AddAllToSelect.click();
        ys_waitingTime(1000);
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
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
        Reporter.infoAfterClass("执行完毕：======  SpeedDial  ======"); //执行操作
        quitDriver();
        pjsip.Pj_Destory();

        ys_waitingTime(10000);
        killChromePid();
    }
}
