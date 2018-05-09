package com.yeastar.swebtest.testcase.DetailCase.pbxcase;

import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.driver.YSMethod.YS_Extension;
import com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.SIP;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.swing.plaf.PanelUI;
import java.util.ArrayList;

/**
 * Created by Caroline on 2018/1/22.
 * 在me页面检查完cdr时，要注意重新点开所要操作的页面，admin角色cdr和setting页面不在同一个，所以可以直接将cdr页面关闭；
 * 但是me的cdr和其他 都是在同一个me页面上
 */
public class Blacklist_Whitelist extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：====== Blacklist/Whitelist ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }
    @Test
    public void A1_addExtensions(){
        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_CreateAccount(1000, "Yeastar202", "UDP", UDP_PORT, 1);
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_CreateAccount(1100, "Yeastar202", "UDP", UDP_PORT, 2);
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_CreateAccount(1101, "Yeastar202", "UDP", UDP_PORT, 3);
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_CreateAccount(1102, "Yeastar202", "UDP", UDP_PORT, 4);
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_CreateAccount(1103, "Yeastar202", "UDP", UDP_PORT, 5);
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_CreateAccount(1105, "Yeastar202", "UDP", UDP_PORT, 7);
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1106"); //执行操作
        pjsip.Pj_CreateAccount(1106, "Yeastar202", "UDP", UDP_PORT, 8);

        Reporter.infoExec(" 辅助设备1注册分机3001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3001, "Yeastar202", -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3001, DEVICE_ASSIST_1);

        Reporter.infoExec(" 辅助设备1注册分机3004"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3004, "Yeastar202", -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3004, DEVICE_ASSIST_1);

        Reporter.infoExec(" 辅助设备1注册分机3005"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3005, "Yeastar202", -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3005, DEVICE_ASSIST_1);

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
        closePbxMonitor();
    }
    @Test
    public void A2_init(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        whitelist.whitelist.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid))) != 0) {
            gridSeleteAll(whitelist.grid);
            whitelist.delete.click();
            ys_waitingTime(1000);
            whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid))) != 0) {
            gridSeleteAll(blacklist.grid);
            blacklist.delete.click();
            ys_waitingTime(1000);
            blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }
    }
    /*
    * admin黑白名单测试组合
    * B1_black_admin和B2_white_admin是一对，后面以此类推
    * */
    @Test
    public void B1_black_admin1(){
//        使用import方法
        blacklist.Import.click();
        ys_waitingTime(1000);
        import_blacklist.browse.click();
        ys_waitingTime(1000);
        System.out.println(EXPORT_PATH +"blacklist_admin.csv");
        importFile(EXPORT_PATH +"blacklist_admin.csv");
        import_blacklist.Import.click();
        ys_waitingTime(2000);
        import_blacklist.ImportOK.click();
        ys_apply();
    }
    @Test
    public void B1_black_admin2(){
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打99999,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打99999,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打99999,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void B1_black_admin3(){
        Reporter.infoExec(" 2002拨打99999通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2002,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(2002,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2002拨打99999,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2002拨打99999,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2002拨打99999,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void B1_black_admin4(){
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1000 <1000>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void B1_black_admin5(){
        Reporter.infoExec(" 1100拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","32001","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void B1_black_admin6(){
//        验证内部分机互打——黑名单呼入限制不对内部分机生效
        Reporter.infoExec("1105呼入到1000——内部分机互打，预期呼入成功");
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(1105,"1000",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1105 <1105>","1000 <1000>","Answered"," "," ",communication_internal);
    }
    @Test
    public void B2_white_admin1(){
        whitelist.whitelist.click();
        whitelist.add.click();
        m_callFeature.addWhitelist("white1",add_whitelist.type_Inbound,2001);
        ys_apply();
    }
    @Test
    public void B2_white_admin2(){
        Reporter.infoExec("辅助设备分机2001通过SPS呼入到1000，预期呼入成功");
        tcpSocket.connectToDevice();
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
    public void B2_white_admin3(){
        Reporter.infoExec(" 2002拨打99999通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2002,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(2002,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2002拨打99999,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2002拨打99999,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2002拨打99999,预期呼入失败,实际呼入成功");
        }
    }
//    第二个组合
    @Test
    public void B3_black_admin1(){
//        删除第一个组合
        if(Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid))) != 0) {
            gridSeleteAll(whitelist.grid);
            whitelist.delete.click();
            ys_waitingTime(1000);
            whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid))) != 0) {
            gridSeleteAll(blacklist.grid);
            blacklist.delete.click();
            ys_waitingTime(1000);
            blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }
        m_callFeature.addBlacklist_String("QWERTYUIOPasdfghjklkzxclvbnm12345678901234567890123456789012345",add_blacklist.type_Outbound,"2001","XZN.","1105");
        ys_apply();
    }
    @Test
    public void B3_black_admin2(){
//        验证黑名单只针对呼出，不针对呼入
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
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
    public void B3_black_admin3(){
        Reporter.infoExec(" 1100拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1100,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1100拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1100拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1100拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void B3_black_admin4(){
//        验证不在黑名单上的号码可以成功呼出
        Reporter.infoExec(" 1100拨打32002通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32002",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2002, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2002状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2002状态为TALKING，实际状态为"+getExtensionStatus(2002, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","32002","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void B3_black_admin5(){
//        验证不同的分机呼出黑名单号码失败
        Reporter.infoExec(" 1101拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1101,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1101,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1101拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1101拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1101拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void B3_black_admin6(){
//        验证XZN.生效
        Reporter.infoExec(" 1101拨打30123通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1101,"30123",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1101,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1101拨打30123,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1101拨打30123,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1101拨打30123,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void B3_black_admin7(){
//        验证呼出黑名单对内部分机也生效
        Reporter.infoExec(" 1000拨打1105——内部分机互打，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"1105",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打1105,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打1105,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打1105,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void B4_white_admin1(){
        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        whitelist.whitelist.click();
        //        使用import方法
        whitelist.Import.click();
        ys_waitingTime(1000);
        import_whitelist.browse.click();
        ys_waitingTime(1000);
        System.out.println(EXPORT_PATH +"whitelist_admin.csv");
        importFile(EXPORT_PATH +"whitelist_admin.csv");
        import_whitelist.Import.click();
        ys_waitingTime(2000);
        import_whitelist.ImportOK.click();
        ys_apply();
//        whitelist.add.click();
//        m_callFeature.addWhitelist("черныйсписок",add_whitelist.type_Outbound,2001,1105);
    }
    @Test
    public void B4_white_admin2(){
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
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
//    @Test
    public void B4_white_admin3(){
        Reporter.infoExec(" 1101拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1101,"32001",DEVICE_IP_LAN);
        ys_waitingTime(5000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("1101 <1101>","32001","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void B4_white_admin4(){
//        验证白名单对内部分机也生效
        Reporter.infoExec(" 1000拨打1105——内部分机互打，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"1105",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1105, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1105状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1105状态为TALKING，实际状态为"+getExtensionStatus(1105, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <1105>","Answered"," "," ",communication_internal);
    }
    @Test
    public void B5_black_admin1(){
        //        删除第二个组合
        if(Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid))) != 0) {
            gridSeleteAll(whitelist.grid);
            whitelist.delete.click();
            ys_waitingTime(1000);
            whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid))) != 0) {
            gridSeleteAll(blacklist.grid);
            blacklist.delete.click();
            ys_waitingTime(1000);
            blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }
        ArrayList<String> extendsion = new ArrayList<>();
        extendsion.add("1000");
        extendsion.add("1100");
        m_callFeature.addBlacklist_OutBound("黑名单",extendsion,"3001");
        ys_apply();
    }
    @Test
    public void B5_black_admin2(){
        Reporter.infoExec(" 1000拨打13001通过sip外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"13001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打13001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打13001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打13001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void B5_black_admin3(){
        Reporter.infoExec(" 1100拨打13001通过sip外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"13001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1100,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1100拨打13001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1100拨打13001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1100拨打13001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void B5_black_admin4(){
        Reporter.infoExec(" 1101拨打13001通过sip外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1101,"13001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1101 <1101>","13001","Answered"," ",SIPTrunk,communication_outRoute);
    }
    @Test
    public void B5_black_admin5(){
        Reporter.infoExec(" 1100拨打13004通过sip外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"13004",DEVICE_IP_LAN);
        ys_waitingTime(5000);
        if (getExtensionStatus(3004, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3004状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3004状态为TALKING，实际状态为"+getExtensionStatus(3004, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("1100 <1100>","13004","Answered"," ",SIPTrunk,communication_outRoute);
    }
    @Test
    public void B5_black_admin6(){
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1000，预期呼入成功"); //执行操作
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
    public void B6_white_admin1(){
        whitelist.whitelist.click();
        whitelist.add.click();
        m_callFeature.addWhitelist("白名单",add_whitelist.type_Outbound,3001);
        ys_apply();
    }
    @Test
    public void B6_white_admin2(){
        Reporter.infoExec(" 1000拨打13001通过sip外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"13001",DEVICE_IP_LAN,false);
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
    public void B6_white_admin3(){
        Reporter.infoExec(" 1100拨打13001通过sip外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"13001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","13001","Answered"," ",SIPTrunk,communication_outRoute);
    }
    @Test
    public void B7_black_admin1(){
//        删除第三个组合
        if(Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid))) != 0) {
            gridSeleteAll(whitelist.grid);
            whitelist.delete.click();
            ys_waitingTime(1000);
            whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        Reporter.infoExec("修改黑名单");
        gridClick(blacklist.grid, gridFindRowByColumn(blacklist.grid, blacklist.gridcolumn_Name, "黑名单", sort_ascendingOrder), blacklist.gridEdit);
        ys_waitingTime(2000);
        add_blacklist.number.setValue("3001\n3004");
        add_blacklist.addAllToSelected.click();
        ys_waitingTime(1000);
        add_blacklist.save.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        ys_apply();
    }
    @Test
    public void B7_black_admin2(){
        Reporter.infoExec(" 1105拨打13001通过sip外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1105,"13001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1105,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1105拨打13001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1105拨打13001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1105拨打13001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void B7_black_admin3(){
        Reporter.infoExec(" 1100拨打33004通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"33004",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1100,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1100拨打33004,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1100拨打33004,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1100拨打33004,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void B7_black_admin4(){
        Reporter.infoExec(" 3004拨打3000通过sip外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3004,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3004 <3004>","1000 <1000>","Answered",SIPTrunk," ",communication_inbound);
    }
    @Test
    public void B8_white_admin1(){
        whitelist.whitelist.click();
        whitelist.add.click();
        m_callFeature.addWhitelist("ブラックリスト白名单",add_whitelist.type_Outbound,3001,3004);
        ys_apply();
    }
    @Test
    public void B8_white_admin2(){
        Reporter.infoExec(" 1100拨打33004通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"33004",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","33004","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void B8_white_admin3(){
        Reporter.infoExec(" 1105拨打13001通过sip外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1105,"13001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1105 <1105>","13001","Answered"," ",SIPTrunk,communication_outRoute);
    }
    @Test
    public void C1_black_admin1(){
        //        删除第四个组合
        if(Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid))) != 0) {
            gridSeleteAll(whitelist.grid);
            whitelist.delete.click();
            ys_waitingTime(1000);
            whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid))) != 0) {
            gridSeleteAll(blacklist.grid);
            blacklist.delete.click();
            ys_waitingTime(1000);
            blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }
        ArrayList<String> extendsion = new ArrayList<>();
        extendsion.add("ExtensionGroup1");
        extendsion.add("1102");
        m_callFeature.addBlacklist_OutBound("black1",extendsion,"2001","2002","1105");
        ys_apply();
    }
    @Test
    public void C1_black_admin2(){
        Reporter.infoExec(" 1101拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1101,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1101,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1101拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1101拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1101拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void C1_black_admin3(){
        Reporter.infoExec(" 1100拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1100,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1100拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1100拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1100拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void C1_black_admin4(){
        Reporter.infoExec(" 1102拨打32002通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1102,"32002",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1102,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1102拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1102拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1102拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void C1_black_admin5(){
        Reporter.infoExec(" 1103拨打32002通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1103,"32002",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2002, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2002状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2002状态为TALKING，实际状态为"+getExtensionStatus(2002, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("xlq <1103>","32002","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void C1_black_admin6(){
        Reporter.infoExec(" 2002拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2002,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2002 <2002>","1000 <1000>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void C1_black_admin7(){
        Reporter.infoExec(" 1102拨打1105——内部分机互打，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1102,"1105",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1102,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1102拨打1105,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1102拨打1105,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1102拨打1105,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void C2_white_admin1(){
        whitelist.whitelist.click();
        whitelist.add.click();
        m_callFeature.addWhitelist("white1",add_whitelist.type_Outbound,2001,1105);
        ys_apply();
    }
    @Test
    public void C2_white_admin2(){
        Reporter.infoExec(" 1102拨打32002通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1102,"32002",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1102,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1102拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1102拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1102拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void C2_white_admin3(){
        Reporter.infoExec(" 1100拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","32001","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void C2_white_admin4(){
        Reporter.infoExec(" 1102拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1102,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1102 <1102>","32001","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void C2_white_admin5(){
        Reporter.infoExec(" 1102拨打1105——内部分机互打，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1102,"1105",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1105, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1105状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1105状态为TALKING，实际状态为"+getExtensionStatus(1105, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1102 <1102>","1105 <1105>","Answered"," "," ",communication_internal);
    }
    @Test
    public void C3_black_admin1(){
//        删除第五组白名单
        if(Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid))) != 0) {
            gridSeleteAll(whitelist.grid);
            whitelist.delete.click();
            ys_waitingTime(1000);
            whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        Reporter.infoExec("修改黑名单");
        gridClick(blacklist.grid, gridFindRowByColumn(blacklist.grid, blacklist.gridcolumn_Name, "black1", sort_ascendingOrder), blacklist.gridEdit);
        ys_waitingTime(2000);
        add_blacklist.number.setValue("X.");
        ys_waitingTime(1000);
        listSelect(add_blacklist.list,extensionList,"ExtensionGroup1");
        ys_waitingTime(1000);
        add_blacklist.save.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        ys_apply();
    }
    @Test
    public void C3_black_admin2() {
        Reporter.infoExec(" 1102拨打32002通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1102,"32002",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2002, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2002状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2002状态为TALKING，实际状态为"+getExtensionStatus(2002, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1102 <1102>","32002","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void C3_black_admin3() {
//        验证分机
        Reporter.infoExec(" 1100拨打32002通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32002",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1100,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1100拨打32002,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1100拨打32002,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1100拨打32002,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void C3_black_admin4() {
//        验证黑名单号码
        Reporter.infoExec(" 1100拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1100,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1100拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1100拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1100拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void C3_black_admin5() {
        Reporter.infoExec(" 2002拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2002,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2002 <2002>","1000 <1000>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void C4_white_admin1(){
        whitelist.whitelist.click();
        whitelist.add.click();
        m_callFeature.addWhitelist("white1",add_whitelist.type_Outbound,2002);
        ys_apply();
    }
    @Test
    public void C4_white_admin2(){
        Reporter.infoExec(" 1100拨打32002通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32002",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2002, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2002状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2002状态为TALKING，实际状态为"+getExtensionStatus(2002, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","32002","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void C4_white_admin3(){
        Reporter.infoExec(" 1100拨打32001通过sps外线呼出,预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1100,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1100拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1100拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1100拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void C5_black_admin1(){
        //        删除第六个组合
        if(Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid))) != 0) {
            gridSeleteAll(whitelist.grid);
            whitelist.delete.click();
            ys_waitingTime(1000);
            whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid))) != 0) {
            gridSeleteAll(blacklist.grid);
            blacklist.delete.click();
            ys_waitingTime(1000);
            blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }
        m_callFeature.addBlacklist_String("blacklist",add_blacklist.type_Both,"[012-9][012-9].","300[0-3]");
        ys_apply();
    }
    @Test
    public void C5_black_admin2(){
        Reporter.infoExec(" 1100拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1100,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1100拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1100拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1100拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void C5_black_admin3(){
        Reporter.infoExec(" 1101拨打13004通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1101,"13004",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1101,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1101拨打13004,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1101拨打13004,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1101拨打13004,预期呼出失败,实际呼出成功");
        }
    }
//    @Test
//    public void C5_black_admin4(){
//        Reporter.infoExec(" 1101拨打34001通过sps外线呼出"); //执行操作
//        pjsip.Pj_Make_Call_Auto_Answer(1101,"34001",DEVICE_IP_LAN,false);
//        ys_waitingTime(8000);
//        pjsip.Pj_Hangup_All();
//        m_extension.checkCDR("1101 <1101>","34001","Answered"," ",SPS,communication_outRoute);
//    }
    @Test
    public void C5_black_admin5(){
        Reporter.infoExec(" 2002拨打991000通过sps外线呼入到分机1000,预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2002,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1101,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2002拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2002拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2002拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void C5_black_admin6(){
        Reporter.infoExec(" 3004拨打3000通过sip外线呼入到分机1000,预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3004,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(3004,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 3004拨打3000,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 3004拨打3000,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 3004拨打3000,预期呼出失败,实际呼出成功");
        }
    }
//    @Test
//    public void C5_black_admin7(){
//        Reporter.infoExec(" 3005拨打3000通过sip外线呼入到分机1000"); //执行操作
//        pjsip.Pj_Make_Call_Auto_Answer(3005,"3000",DEVICE_ASSIST_1,false);
//        ys_waitingTime(8000);
//        pjsip.Pj_Hangup_All();
//        m_extension.checkCDR("3005 <3005>","1000 <1000>","Answered",SIPTrunk," ",communication_inbound);
//    }
    @Test
    public void C6_white_admin1(){
        whitelist.whitelist.click();
        ys_waitingTime(1000);
        whitelist.add.click();
        ys_waitingTime(1000);
        m_callFeature.addWhitelist("whitelist",add_whitelist.type_Both,3004,2001);
        ys_apply();
    }
    @Test
    public void C6_white_admin2(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
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
    public void C6_white_admin3(){
        Reporter.infoExec(" 1101拨打33004通过sip外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1101,"13004",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(3004, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3004状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3004状态为TALKING，实际状态为"+getExtensionStatus(3004, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1101 <1101>","13004","Answered"," ",SIPTrunk,communication_outRoute);
    }
    @Test
    public void C7_black_admin1(){
        //        删除第七个组合
        if(Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid))) != 0) {
            gridSeleteAll(whitelist.grid);
            whitelist.delete.click();
            ys_waitingTime(1000);
            whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid))) != 0) {
            gridSeleteAll(blacklist.grid);
            blacklist.delete.click();
            ys_waitingTime(1000);
            blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }
        m_callFeature.addBlacklist_String("blacklist",add_blacklist.type_Both,"XXXX!");
        ys_apply();
    }
    @Test
    public void C7_black_admin2(){
        Reporter.infoExec(" 1101拨打3123通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1101,"3123",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1101 <1101>","3123","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void C7_black_admin3(){
        Reporter.infoExec(" 1101拨打32234通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1101,"32234",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1101,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1101拨打32234,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1101拨打32234,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1101拨打32234,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void C7_black_admin4(){
        Reporter.infoExec(" 1101拨打312345通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1101,"312345",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1101,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1101拨打312345,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1101拨打312345,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1101拨打312345,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void C7_black_admin5(){
        Reporter.infoExec(" 2002拨打991234通过sip外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2002,"991234",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(2002,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2002拨打991234,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2002拨打991234,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2002拨打991234,预期呼入失败,实际呼入失败");
        }
    }
    @Test
    public void C8_white_admin1(){
        whitelist.whitelist.click();
        m_callFeature.addWhitelist_String("whitelist",add_whitelist.type_Both,"XXXX!");
        ys_apply();
    }
    @Test
    public void C8_white_admin2(){
        Reporter.infoExec(" 1101拨打32234通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1101,"32234",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1101 <1101>","32234","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void C8_white_admin3(){
        Reporter.infoExec(" 2002拨打991234通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2002,"991234",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2002 <2002>","1000 <1000>","Answered",SPS," ",communication_inbound);
    }
//    验证黑名单为both，白名单为outbound时是否对呼出生效
    @Test
    public void C8_white_admin4(){
        Reporter.infoExec("修改白名单——whitelist");
        gridClick(whitelist.grid, gridFindRowByColumn(whitelist.grid, whitelist.gridcolumn_Name, "whitelist", sort_ascendingOrder), whitelist.gridEdit);
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('st-bw-type').setValue('outbound')");
        ys_waitingTime(1000);
        add_whitelist.save.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        ys_apply();
    }
    @Test
    public void C8_white_admin5(){
        Reporter.infoExec(" 2002拨打991234通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2002,"991234",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(2002,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2002拨打991234,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2002拨打991234,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2002拨打991234,预期呼入失败,实际呼入失败");
        }
    }
    @Test
    public void C8_white_admin6(){
        Reporter.infoExec(" 1101拨打32234通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1101,"32234",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1101 <1101>","32234","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void C9_EmergencyNumber1(){
        //        删除第七个组合
        if(Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid))) != 0) {
            gridSeleteAll(whitelist.grid);
            whitelist.delete.click();
            ys_waitingTime(1000);
            whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid))) != 0) {
            gridSeleteAll(blacklist.grid);
            blacklist.delete.click();
            ys_waitingTime(1000);
            blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }
        m_callFeature.addBlacklist_String("blacklist",add_blacklist.type_Outbound,"3001");
    }
    @Test
    public void C9_EmergencyNumber2(){
        Reporter.infoExec(" 新增紧急号码：3001，trunk选择SPS");
        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.emergencyNumber_panel.click();
        emergencyNumber.add.click();
        add_emergency_number.emergencyNumber.setValue(String.valueOf(3001));
        ys_waitingTime(2000);
        listSelect(add_emergency_number.trunkSelect,trunkList,SPS);
        add_emergency_number.save.click();
        ys_waitingLoading(emergencyNumber.grid_Mask);
        ys_apply();
    }
    @Test
    public void C9_EmergencyNumber3(){
        Reporter.infoExec(" 1101拨打3001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1101,"3001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1101 <1101>","3001","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void C9_EmergencyNumber4(){
        Reporter.infoExec(" 全部勾选紧急号码-确定删除"); //执行操作
//        全部勾选
        gridSeleteAll(emergencyNumber.grid);
//        点击删除按钮
        emergencyNumber.delete.click();
        ys_waitingTime(1000);
        emergencyNumber.delete_yes.click();
        ys_waitingTime(1000);
        ys_waitingLoading(emergencyNumber.grid_Mask);
    }
    @Test
    public void C9_EmergencyNumber5(){
        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid))) != 0) {
            gridSeleteAll(blacklist.grid);
            blacklist.delete.click();
            ys_waitingTime(1000);
            blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }
        ys_apply();
    }
    @Test
    public void D1_black_me1() throws InterruptedException {
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com","Yeastar202");
        }else {
            login("1000", "Yeastar202");
        }
//        init初始化
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid))) != 0) {
            gridSeleteAll(me_whitelist.grid);
            me_whitelist.delete.click();
            ys_waitingTime(1000);
            me_whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        me_blacklist.me_Blacklist.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid))) != 0) {
            gridSeleteAll(me_blacklist.grid);
            me_blacklist.delete.click();
            ys_waitingTime(1000);
            me_blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }

        m_me.addMeBlacklist("黑名单_me",me_add_blacklist.routeType_Outbound,2001);
        ys_me_apply();
    }
    @Test
    public void D1_black_me2(){
        Reporter.infoExec(" 1100拨打32001通过sps外线呼出,预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100,"32001",DEVICE_IP_LAN,false);
        System.out.println("state1预期TALKING:"+getExtensionStatus(1100,TALKING,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(1100,TALKING,8);
        System.out.println("state2:"+state);
        if(state == TALKING){
            Reporter.infoExec(" 1100拨打32001,预期呼出成功,实际呼出成功");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1100拨打32001,预期呼出成功,实际呼出失败");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1100拨打32001,预期呼出成功,实际呼出失败");
        }
    }
    @Test
    public void D1_black_me3(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void D1_black_me4(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void D2_white_me1() throws InterruptedException {
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        m_me.addMeWhitelist("白名单_me",me_add_whitelist.routeType_Outbound,2001);
        ys_me_apply();
    }
    @Test
    public void D2_white_me2(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("1000 <1000>","32001","Answered",communication_outRoute);
    }
    @Test
    public void D3_black_me1() throws InterruptedException {
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        //        删除第一个组合
        if(Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid))) != 0) {
            gridSeleteAll(me_whitelist.grid);
            me_whitelist.delete.click();
            ys_waitingTime(1000);
            me_whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        me_blacklist.me_Blacklist.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid))) != 0) {
            gridSeleteAll(me_blacklist.grid);
            me_blacklist.delete.click();
            ys_waitingTime(1000);
            me_blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }
        m_me.addMeBlacklist_String("black_me",me_add_blacklist.routeType_Inbound,"X.");
        ys_me_apply();
    }
    @Test
    public void D3_black_me2(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("1000 <1000>","32001","Answered",communication_outRoute);
    }
    @Test
    public void D3_black_me3(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void D4_white_me1() throws InterruptedException {
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        m_me.addMeWhitelist("white_me",me_add_whitelist.routeType_Both,2001); //这里白名单用both，验证黑白名单的type不同时是否生效
        ys_me_apply();
    }
    @Test
    public void D4_white_me2(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void D4_white_me3(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        Reporter.infoExec("修改白名单——white_me");
        gridClick(me_whitelist.grid, gridFindRowByColumn(me_whitelist.grid, me_whitelist.gridColumn_Name, "white_me", sort_ascendingOrder), me_whitelist.gridEdit);
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('"+me_add_whitelist.routeTypeId+"').setValue('inbound')");
        ys_waitingTime(1000);
        me_add_whitelist.save.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_me_apply();
    }
    @Test
    public void D4_white_me4(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void D5_black_me1(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        //        删除第一个组合
        if(Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid))) != 0) {
            gridSeleteAll(me_whitelist.grid);
            me_whitelist.delete.click();
            ys_waitingTime(1000);
            me_whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        me_blacklist.me_Blacklist.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        Reporter.infoExec("修改黑名单——black_me");
        gridClick(me_blacklist.grid, gridFindRowByColumn(me_blacklist.grid, me_blacklist.gridColumn_Name, "black_me", sort_ascendingOrder), me_blacklist.gridEdit);
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('"+me_add_blacklist.routeTypeId+"').setValue('both')");
        ys_waitingTime(1000);
        me_add_blacklist.number.setValue("2001\n3001");
        ys_waitingTime(1000);
        me_add_blacklist.save.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        ys_me_apply();
    }
    @Test
    public void D5_black_me2(){
        Reporter.infoExec(" 1000拨打33001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"33001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打33001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打33001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打33001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void D5_black_me3(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void D5_black_me4(){
        Reporter.infoExec(" 1000拨打33002通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"33002",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("1000 <1000>","33002","Answered",communication_outRoute);
    }
    @Test
    public void D5_black_me5(){
        Reporter.infoExec(" 2002拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2002,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2002 <2002>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void D6_white_me1() throws InterruptedException {
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        m_me.addMeWhitelist("white_me",me_add_whitelist.routeType_Outbound,2001,3001); //这里白名单用both，验证黑白名单的type不同时是否生效
        ys_me_apply();
    }
    @Test
    public void D6_white_me2(){
        Reporter.infoExec(" 1000拨打33001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"33001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("1000 <1000>","33001","Answered",communication_outRoute);
    }
    @Test
    public void D6_white_me3(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入失败");
        }
    }
    @Test
    public void D6_white_me4(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        Reporter.infoExec("修改me白名单——white_me");
        gridClick(me_whitelist.grid, gridFindRowByColumn(me_whitelist.grid, me_whitelist.gridColumn_Name, "white_me", sort_ascendingOrder), me_whitelist.gridEdit);
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('"+me_add_whitelist.routeTypeId+"').setValue('inbound')");
        ys_waitingTime(1000);
        me_add_whitelist.save.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_me_apply();
    }
    @Test
    public void D6_white_me5(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void D6_white_me6(){
        Reporter.infoExec(" 1000拨打33001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"33001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打33001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打33001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打33001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void D6_white_me7(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        Reporter.infoExec("修改me白名单——white_me");
        gridClick(me_whitelist.grid, gridFindRowByColumn(me_whitelist.grid, me_whitelist.gridColumn_Name, "white_me", sort_ascendingOrder), me_whitelist.gridEdit);
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('"+me_add_whitelist.routeTypeId+"').setValue('both')");
        ys_waitingTime(1000);
        me_add_whitelist.save.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_me_apply();
    }
    @Test
    public void D6_white_me8(){
        Reporter.infoExec(" 1000拨打33001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"33001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("1000 <1000>","33001","Answered",communication_outRoute);
    }
    @Test
    public void D6_white_me9(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    /*@Test
    public void E1_onlyWhite_me1() throws InterruptedException {
        me.me_Blacklist_Whitelist.click();
        me_blacklist.me_Blacklist.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid))) != 0) {
            gridSeleteAll(me_blacklist.grid);
            me_blacklist.delete.click();
            ys_waitingTime(1000);
            me_blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }
        me_whitelist.me_Whitelist.click();
        if(Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid))) != 0) {
            gridSeleteAll(me_whitelist.grid);
            me_whitelist.delete.click();
            ys_waitingTime(1000);
            me_whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        me_whitelist.ok.click();
        ys_waitingTime(1000);

        m_me.addMeWhitelist_String("white_me",me_add_whitelist.routeType_Both,"."); //这里白名单用both，验证黑白名单的type不同时是否生效
        ys_me_apply();
    }
    @Test
    public void E1_onlyWhite_me2(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void E1_onlyWhite_me3(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        Reporter.infoExec("修改me白名单——white_me");
        gridClick(me_whitelist.grid, gridFindRowByColumn(me_whitelist.grid, me_whitelist.gridColumn_Name, "white_me", sort_ascendingOrder), me_whitelist.gridEdit);
        ys_waitingTime(2000);
        me_add_whitelist.number.setValue("XXXX.");
        ys_waitingTime(1000);
        me_add_whitelist.save.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_me_apply();
    }
    @Test
    public void E1_onlyWhite_me4(){
        Reporter.infoExec(" 2006(Caller ID为01234567890123456789)拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2006,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2006 <01234567890123456789>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void E1_onlyWhite_me5(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入失败");
        }
    }
    @Test
    public void E1_onlyWhite_me6(){
        Reporter.infoExec(" 1000拨打33001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"33001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("1000 <1000>","33001","Answered",communication_outRoute);
    }
    @Test
    public void E1_onlyWhite_me7(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        Reporter.infoExec("修改me白名单——white_me");
        gridClick(me_whitelist.grid, gridFindRowByColumn(me_whitelist.grid, me_whitelist.gridColumn_Name, "white_me", sort_ascendingOrder), me_whitelist.gridEdit);
        ys_waitingTime(2000);
        me_add_whitelist.number.setValue("XXXXX!");
        ys_waitingTime(1000);
        executeJs("Ext.getCmp('"+me_add_whitelist.routeTypeId+"').setValue('inbound')");
        ys_waitingTime(1000);
        me_add_whitelist.save.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_me_apply();
    }
    @Test
    public void E1_onlyWhite_me8(){
        Reporter.infoExec(" 2006(Caller ID为01234567890123456789)拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2006,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2006 <01234567890123456789>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void E1_onlyWhite_me9(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入失败");
        }
    }
    @Test
    public void E2_onlyWhite_me1(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        Reporter.infoExec("修改白名单——white_me");
        gridClick(me_whitelist.grid, gridFindRowByColumn(me_whitelist.grid, me_whitelist.gridColumn_Name, "white_me", sort_ascendingOrder), me_whitelist.gridEdit);
        ys_waitingTime(2000);
        me_add_whitelist.number.setValue("200[12-3]");
        ys_waitingTime(1000);
        me_add_whitelist.save.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_me_apply();
    }
    @Test
    public void E2_onlyWhite_me2(){
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(3001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(3001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 3001拨打3000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 3001拨打3000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 3001拨打3000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void E2_onlyWhite_me3(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void E2_onlyWhite_me4(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        Reporter.infoExec("修改白名单——white_me");
        gridClick(me_whitelist.grid, gridFindRowByColumn(me_whitelist.grid, me_whitelist.gridColumn_Name, "white_me", sort_ascendingOrder), me_whitelist.gridEdit);
        ys_waitingTime(2000);
        me_add_whitelist.number.setValue("X.");
        ys_waitingTime(1000);
        me_add_whitelist.save.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_me_apply();
    }
    @Test
    public void E2_onlyWhite_me5(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void E2_onlyWhite_me6(){
        Reporter.infoExec(" 2006拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2006,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2006 <01234567890123456789>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void E2_onlyWhite_me7(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        Reporter.infoExec("修改白名单——white_me");
        gridClick(me_whitelist.grid, gridFindRowByColumn(me_whitelist.grid, me_whitelist.gridColumn_Name, "white_me", sort_ascendingOrder), me_whitelist.gridEdit);
        ys_waitingTime(2000);
        me_add_whitelist.number.setValue("Z.");
        ys_waitingTime(1000);
        me_add_whitelist.save.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_me_apply();
    }
    @Test
    public void E2_onlyWhite_me8(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void E2_onlyWhite_me9(){
        Reporter.infoExec(" 2006拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2006,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2006,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2006,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2006拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2006拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2006拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void E3_onlyWhite_me1(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        Reporter.infoExec("修改me白名单——white_me");
        gridClick(me_whitelist.grid, gridFindRowByColumn(me_whitelist.grid, me_whitelist.gridColumn_Name, "white_me", sort_ascendingOrder), me_whitelist.gridEdit);
        ys_waitingTime(2000);
        me_add_whitelist.number.setValue("N.");
        ys_waitingTime(1000);
        me_add_whitelist.save.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_me_apply();
    }
    @Test
    public void E3_onlyWhite_me2(){
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("3001 <3001>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void E3_onlyWhite_me3(){
        Reporter.infoExec(" 2006拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2006,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2006,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2006,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2006拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2006拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2006拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void E3_onlyWhite_me4(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        Reporter.infoExec("修改me白名单——white_me");
        gridClick(me_whitelist.grid, gridFindRowByColumn(me_whitelist.grid, me_whitelist.gridColumn_Name, "white_me", sort_ascendingOrder), me_whitelist.gridEdit);
        ys_waitingTime(2000);
        me_add_whitelist.number.setValue("ZX0N");
        ys_waitingTime(1000);
        me_add_whitelist.save.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_me_apply();
    }
    @Test
    public void E3_onlyWhite_me5(){
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(3001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(3001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 3001拨打3000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 3001拨打3000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 3001拨打3000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void E3_onlyWhite_me6(){
        Reporter.infoExec(" 2002拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2002,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2002 <2002>","1000 <1000>","Answered",communication_inbound);
    }
    *//*
    * admin角色的黑名单命名为black1，白名单命名为white1
    * me角色的黑名单命名为 黑名单_me，白名单命名为 白名单_me
    * *//*
    @Test
    public void F1_onlyWhite1_admin1(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        if(Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid))) != 0) {
            gridSeleteAll(me_whitelist.grid);
            me_whitelist.delete.click();
            ys_waitingTime(1000);
            me_whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        ys_me_apply();
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        m_callFeature.addBlacklist_String("black1",add_blacklist.type_Inbound,"2001");
        whitelist.whitelist.click();
        m_callFeature.addWhitelist_String("white1",add_whitelist.type_Inbound,"2001");
        ys_apply();
//        此时情况：admin黑名单：呼入2001    admin白名单：呼入2001
//                  me黑名单：空            me白名单：空
//                  勾选了仅白名单
    }
    @Test
    public void F1_onlyWhite1_admin2(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void F1_onlyWhite1_admin3(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("1000 <1000>","32001","Answered"," ",SPS,communication_outRoute);
    }
//    取消勾选仅白名单模式
    @Test
    public void F1_onlyWhite1_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com","Yeastar202");
        }else {
            login("1000", "Yeastar202");
        }
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        ys_me_apply();
    }
//    验证取消白名单模式后的呼出呼入，注意这里检查的cdr是在me页面
    @Test
    public void F1_onlyWhite1_admin5(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void F1_onlyWhite1_admin6(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("1000 <1000>","32001","Answered",communication_outRoute);
    }
//    重新勾选仅白名单模式，然后admin角色进行黑白名单修改
    @Test
    public void F1_onlyWhite1_admin7(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        me_whitelist.ok.click();
        ys_waitingTime(1000);
        ys_me_apply();
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
    }
    @Test
    public void F1_onlyWhite2_admin1(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        whitelist.whitelist.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        Reporter.infoExec("修改admin白名单——white1");
        gridClick(whitelist.grid, gridFindRowByColumn(whitelist.grid, whitelist.gridcolumn_Name, "white1", sort_ascendingOrder), whitelist.gridEdit);
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('st-bw-type').setValue('outbound')");
        ys_waitingTime(1000);
        add_blacklist.save.click();
        ys_waitingTime(1000);
        ys_waitingLoading(whitelist.grid_loadMask);

        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        Reporter.infoExec("修改admin黑名单——black1");
        gridClick(blacklist.grid, gridFindRowByColumn(blacklist.grid, blacklist.gridcolumn_Name, "black1", sort_ascendingOrder), blacklist.gridEdit);
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('st-bw-type').setValue('outbound')");
        ys_waitingTime(1000);
        add_blacklist.save.click();
        ys_waitingTime(1000);
        ys_waitingLoading(blacklist.grid_loadMask);
        ys_apply();
//        此时情况：admin黑名单：呼出2001    admin白名单：呼出2001
//                  me黑名单：空            me白名单：空
//                  勾选了仅白名单
    }
    @Test
    public void F1_onlyWhite2_admin2(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void F1_onlyWhite2_admin3(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("1000 <1000>","32001","Answered"," ",SPS,communication_outRoute);
    }
//    取消勾选仅白名单模式
    @Test
    public void F1_onlyWhite2_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com","Yeastar202");
        }else {
            login("1000", "Yeastar202");
        }
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        ys_me_apply();
    }
    //    验证取消白名单模式后的呼出呼入，注意这里检查的cdr是在me页面
    @Test
    public void F1_onlyWhite2_admin5(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("1000 <1000>","32001","Answered",communication_outRoute);
    }
    @Test
    public void F1_onlyWhite2_admin6(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    //    重新勾选仅白名单模式，然后进行admin角色进行黑白名单修改
    @Test
    public void F1_onlyWhite2_admin7(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        me_whitelist.ok.click();
        ys_waitingTime(1000);
        ys_me_apply();
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
    }
    @Test
    public void F1_onlyWhite3_admin1(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        whitelist.whitelist.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        Reporter.infoExec("修改白名单——white1");
        gridClick(whitelist.grid, gridFindRowByColumn(whitelist.grid, whitelist.gridcolumn_Name, "white1", sort_ascendingOrder), whitelist.gridEdit);
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('st-bw-type').setValue('both')");
        ys_waitingTime(1000);
        add_blacklist.save.click();
        ys_waitingLoading(whitelist.grid_loadMask);

        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        Reporter.infoExec("修改黑名单——black1");
        gridClick(blacklist.grid, gridFindRowByColumn(blacklist.grid, blacklist.gridcolumn_Name, "black1", sort_ascendingOrder), blacklist.gridEdit);
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('st-bw-type').setValue('both')");
        ys_waitingTime(1000);
        add_blacklist.save.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        ys_apply();
//        此时情况：admin黑名单：呼出呼入2001    admin白名单：呼出呼入2001
//                  me黑名单：空            me白名单：空
//                  勾选了仅白名单
    }
    @Test
    public void F1_onlyWhite3_admin2(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void F1_onlyWhite3_admin3(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("1000 <1000>","32001","Answered"," ",SPS,communication_outRoute);
    }
    //    取消勾选仅白名单模式
    @Test
    public void F1_onlyWhite3_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com","Yeastar202");
        }else {
            login("1000", "Yeastar202");
        }
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        ys_me_apply();
    }
    //    验证取消白名单模式后的呼出呼入，注意这里检查的cdr是在me页面
    @Test
    public void F1_onlyWhite3_admin5(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void F1_onlyWhite3_admin6(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("1000 <1000>","32001","Answered",communication_outRoute);
    }
    //    重新勾选仅白名单模式，然后对me角色的黑白名单修改
    @Test
    public void F1_onlyWhite3_admin7(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        me_whitelist.ok.click();
        ys_waitingTime(1000);
    }
    @Test
    public void F1_onlyWhite4_admin1() throws InterruptedException {
//        使用import功能
        me_whitelist.Import.click();
        ys_waitingTime(1000);
        me_whitelist.browse.click();
        ys_waitingTime(1000);
        System.out.println(EXPORT_PATH +"whitelist_me");
        importFile(EXPORT_PATH +"whitelist_me.csv");
        me_whitelist.Import_import.click();
        ys_waitingTime(2000);
        me_whitelist.Import_OK.click();

        me_blacklist.me_Blacklist.click();
        //        使用import功能
        me_blacklist.Import.click();
        ys_waitingTime(1000);
        me_blacklist.browse.click();
        ys_waitingTime(1000);
        System.out.println(EXPORT_PATH +"blacklist_me.csv");
        importFile(EXPORT_PATH +"blacklist_me.csv");
        me_blacklist.Import_import.click();
        ys_waitingTime(2000);
        me_blacklist.Import_OK.click();

        ys_me_apply();
//        此时情况：admin黑名单：呼出呼入2001    admin白名单：呼出呼入2001
//                  me黑名单：呼出呼入2001          me白名单：呼出呼入2001
//                  勾选了仅白名单
    }
    @Test
    public void F1_onlyWhite4_admin2() throws InterruptedException {
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void F1_onlyWhite4_admin3(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("1000 <1000>","32001","Answered",communication_outRoute);
    }
    //    取消勾选仅白名单模式
    @Test
    public void F1_onlyWhite4_admin4_cancel(){
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        ys_me_apply();
    }
    //    验证取消白名单模式后的呼出呼入，注意这里检查的cdr是在me页面
    @Test
    public void F1_onlyWhite4_admin5(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void F1_onlyWhite4_admin6(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("1000 <1000>","32001","Answered",communication_outRoute);
    }
    //    重新勾选仅白名单模式，然后对admin角色的黑白名单修改
    @Test
    public void F1_onlyWhite4_admin7(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        me_whitelist.ok.click();
        ys_waitingTime(1000);
        ys_me_apply();
    }
    @Test
    public void F1_onlyWhite5_admin1(){
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        whitelist.whitelist.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid))) != 0) {
            gridSeleteAll(whitelist.grid);
            whitelist.delete.click();
            ys_waitingTime(1000);
            whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid))) != 0) {
            gridSeleteAll(blacklist.grid);
            blacklist.delete.click();
            ys_waitingTime(1000);
            blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }
        blacklist.add.click();
        m_callFeature.addBlacklist("黑名单_me",add_blacklist.type_Both,2001);
        ys_apply();
//        此时情况：admin黑名单：呼出呼入2001    admin白名单：空
//                  me黑名单：呼出呼入2001          me白名单：呼出呼入2001
//                  勾选了仅白名单
    }
    @Test
    public void F1_onlyWhite5_admin2(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void F1_onlyWhite5_admin3(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(1000,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    //    取消勾选仅白名单模式
    @Test
    public void F1_onlyWhite5_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com","Yeastar202");
        }else {
            login("1000", "Yeastar202");
        }
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        ys_me_apply();
    }
    //    验证取消白名单模式后的呼出呼入，注意这里检查的cdr是在me页面
    @Test
    public void F1_onlyWhite5_admin5(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void F1_onlyWhite5_admin6(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(1000,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    //    重新勾选仅白名单模式，然后对me角色的黑白名单修改
    @Test
    public void F1_onlyWhite5_admin7(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        me_whitelist.ok.click();
        ys_waitingTime(1000);
    }
    @Test
    public void F1_onlyWhite6_admin1(){
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid))) != 0) {
            gridSeleteAll(blacklist.grid);
            blacklist.delete.click();
            ys_waitingTime(1000);
            blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }
        ys_apply();
//        此时情况：admin黑名单：空    admin白名单：空
//                  me黑名单：呼出呼入2001          me白名单：呼出呼入2001
//                  勾选了仅白名单
    }
    @Test
    public void F1_onlyWhite6_admin2(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",SPS," ",communication_inbound);
    }
    @Test
    public void F1_onlyWhite6_admin3(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","32001","Answered"," ",SPS,communication_outRoute);
    }
    //    取消勾选仅白名单模式
    @Test
    public void F1_onlyWhite6_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com","Yeastar202");
        }else {
            login("1000", "Yeastar202");
        }
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        ys_me_apply();
    }
    //    验证取消白名单模式后的呼出呼入，注意这里检查的cdr是在me页面
    @Test
    public void F1_onlyWhite6_admin5(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void F1_onlyWhite6_admin6(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("1000 <1000>","32001","Answered",communication_outRoute);
    }
    //    重新勾选仅白名单模式，然后对me角色的黑白名单修改
    @Test
    public void F1_onlyWhite6_admin7(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        me_whitelist.ok.click();
        ys_waitingTime(1000);
    }
    @Test
    public void F1_onlyWhite7_admin1(){
        Reporter.infoExec("修改me白名单——白名单_me");
        gridClick(me_whitelist.grid, gridFindRowByColumn(me_whitelist.grid, me_whitelist.gridColumn_Name, "白名单_me", sort_ascendingOrder), me_whitelist.gridEdit);
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('"+me_add_whitelist.routeTypeId+"').setValue('inbound')");
        ys_waitingTime(1000);
        me_add_whitelist.save.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_me_apply();
//        此时情况：admin黑名单：空    admin白名单：空
//                  me黑名单：呼出呼入2001          me白名单：呼入2001
//                  勾选了仅白名单
    }
    @Test
    public void F1_onlyWhite7_admin2(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(1000,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void F1_onlyWhite7_admin3(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    //    取消勾选仅白名单模式
    @Test
    public void F1_onlyWhite7_admin4_cancel(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        ys_me_apply();
    }
    //    验证取消白名单模式后的呼出呼入，注意这里检查的cdr是在me页面
    @Test
    public void F1_onlyWhite7_admin5(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    @Test
    public void F1_onlyWhite7_admin6(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出,预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(1000,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    //    重新勾选仅白名单模式，然后对me角色的黑白名单修改
    @Test
    public void F1_onlyWhite7_admin7(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        me_whitelist.ok.click();
        ys_waitingTime(1000);
    }
    @Test
    public void F1_onlyWhite8_admin1(){
        Reporter.infoExec("修改me白名单——白名单_me");
        gridClick(me_whitelist.grid, gridFindRowByColumn(me_whitelist.grid, me_whitelist.gridColumn_Name, "白名单_me", sort_ascendingOrder), me_whitelist.gridEdit);
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('"+me_add_whitelist.routeTypeId+"').setValue('outbound')");
        ys_waitingTime(1000);
        me_add_whitelist.save.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_me_apply();
//        此时情况：admin黑名单：空    admin白名单：空
//                  me黑名单：呼出呼入2001          me白名单：呼出2001
//                  勾选了仅白名单
    }
    @Test
    public void F1_onlyWhite8_admin2(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("1000 <1000>","32001","Answered",communication_outRoute);
    }
    @Test
    public void F1_onlyWhite8_admin3(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    //    取消勾选仅白名单模式
    @Test
    public void F1_onlyWhite8_admin4_cancel(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        ys_me_apply();
    }
    //    验证取消白名单模式后的呼出呼入，注意这里检查的cdr是在me页面
    @Test
    public void F1_onlyWhite8_admin5(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void F1_onlyWhite8_admin6(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("1000 <1000>","32001","Answered",communication_outRoute);
    }
    //    重新勾选仅白名单模式，然后对me角色的黑白名单修改
    @Test
    public void F1_onlyWhite8_admin7(){
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        me_whitelist.ok.click();
        ys_waitingTime(1000);
        if(Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid))) != 0) {
            gridSeleteAll(me_whitelist.grid);
            me_whitelist.delete.click();
            ys_waitingTime(1000);
            me_whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        ys_me_apply();
    }
    @Test
    public void F1_onlyWhite9_admin1(){
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        whitelist.whitelist.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        whitelist.add.click();
        m_callFeature.addWhitelist("white1",add_whitelist.type_Both,2001);
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        blacklist.add.click();
        m_callFeature.addBlacklist("black1",add_blacklist.type_Both,2001);
        ys_apply();
//        此时情况：admin黑名单：呼出呼入2001    admin白名单：呼出呼入2001
//                  me黑名单：呼出呼入2001          me白名单：空
//                  勾选了仅白名单
    }
    @Test
    public void F1_onlyWhite9_admin2(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void F1_onlyWhite9_admin3(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(1000,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    //    取消勾选仅白名单模式
    @Test
    public void F1_onlyWhite9_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com","Yeastar202");
        }else {
            login("1000", "Yeastar202");
        }
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        ys_me_apply();
    }
    //    验证取消白名单模式后的呼出呼入，注意这里检查的cdr是在me页面
    @Test
    public void F1_onlyWhite9_admin5(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void F1_onlyWhite9_admin6(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(1000,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    //    重新勾选仅白名单模式，然后对admin角色的黑白名单修改
    @Test
    public void F1_onlyWhite9_admin7() {
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        me_whitelist.ok.click();
        ys_waitingTime(1000);
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
    }
    @Test
    public void F2_onlyWhite1_admin1(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid))) != 0) {
            gridSeleteAll(blacklist.grid);
            blacklist.delete.click();
            ys_waitingTime(1000);
            blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }
        ys_apply();
//        此时情况：admin黑名单：空    admin白名单：呼出呼入2001
//                  me黑名单：呼出呼入2001          me白名单：空
//                  勾选了仅白名单
    }
    @Test
    public void F2_onlyWhite1_admin2(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void F2_onlyWhite1_admin3(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(1000,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    //    取消勾选仅白名单模式
    @Test
    public void F2_onlyWhite1_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com","Yeastar202");
        }else {
            login("1000", "Yeastar202");
        }
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        ys_me_apply();
    }
    //    验证取消白名单模式后的呼出呼入，注意这里检查的cdr是在me页面
    @Test
    public void F2_onlyWhite1_admin5(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void F2_onlyWhite1_admin6(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(1000,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    //    重新勾选仅白名单模式，然后对me角色和admin角色的黑白名单修改
    @Test
    public void F2_onlyWhite1_admin7() {
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        me_whitelist.ok.click();
        ys_waitingTime(1000);
    }
    @Test
    public void F2_onlyWhite2_admin1() throws InterruptedException {
        me_blacklist.me_Blacklist.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid))) != 0) {
            gridSeleteAll(me_blacklist.grid);
            me_blacklist.delete.click();
            ys_waitingTime(1000);
            me_blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }

        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid))) != 0) {
            gridSeleteAll(me_whitelist.grid);
            me_whitelist.delete.click();
            ys_waitingTime(1000);
            me_whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        m_me.addMeWhitelist("白名单_me",me_add_whitelist.routeType_Both,2001);
        ys_me_apply();

        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        blacklist.add.click();
        m_callFeature.addBlacklist("black1",add_blacklist.type_Both,2001);
        ys_apply();
//        此时情况：admin黑名单：呼出呼入2001    admin白名单：呼出呼入2001
//                  me黑名单：空          me白名单：呼出呼入2001
//                  勾选了仅白名单
    }
    @Test
    public void F2_onlyWhite2_admin2(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","32001","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void F2_onlyWhite2_admin3(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",SPS," ",communication_inbound);
    }
    //    取消勾选仅白名单模式
    @Test
    public void F2_onlyWhite2_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com","Yeastar202");
        }else {
            login("1000", "Yeastar202");
        }
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        ys_me_apply();
    }
    //    验证取消白名单模式后的呼出呼入，注意这里检查的cdr是在me页面
    @Test
    public void F2_onlyWhite2_admin5(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("1000 <1000>","32001","Answered",communication_outRoute);
    }
    @Test
    public void F2_onlyWhite2_admin6(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    //    重新勾选仅白名单模式，然后对me角色和admin角色的黑白名单修改
    @Test
    public void F2_onlyWhite2_admin7() {
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        me_whitelist.ok.click();
        ys_waitingTime(1000);
    }
    @Test
    public void F2_onlyWhite3_admin1() throws InterruptedException {
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        whitelist.whitelist.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid))) != 0) {
            gridSeleteAll(whitelist.grid);
            whitelist.delete.click();
            ys_waitingTime(1000);
            whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        ys_apply();
//        此时情况：admin黑名单：呼出呼入2001    admin白名单：空
//                  me黑名单：空          me白名单：呼出呼入2001
//                  勾选了仅白名单
    }
    @Test
    public void F2_onlyWhite3_admin2(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void F2_onlyWhite3_admin3(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(1000,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    //    取消勾选仅白名单模式
    @Test
    public void F2_onlyWhite3_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com","Yeastar202");
        }else {
            login("1000", "Yeastar202");
        }
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        ys_me_apply();
    }
    //    验证取消白名单模式后的呼出呼入，注意这里检查的cdr是在me页面
    @Test
    public void F2_onlyWhite3_admin5(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(1000,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void F2_onlyWhite3_admin6(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    //    重新勾选仅白名单模式，然后对me角色和admin角色的黑白名单修改
    @Test
    public void F2_onlyWhite3_admin7() {
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        me_whitelist.ok.click();
        ys_waitingTime(1000);
    }
    @Test
    public void F2_onlyWhite4_admin1() throws InterruptedException {
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_blacklist.me_Blacklist.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid))) != 0) {
            gridSeleteAll(me_blacklist.grid);
            me_blacklist.delete.click();
            ys_waitingTime(1000);
            me_blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }

        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid))) != 0) {
            gridSeleteAll(me_whitelist.grid);
            me_whitelist.delete.click();
            ys_waitingTime(1000);
            me_whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        m_me.addMeWhitelist("白名单_me",me_add_whitelist.routeType_Inbound,2001);
        ys_me_apply();

        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        Reporter.infoExec("修改admin黑名单——black1");
        gridClick(blacklist.grid, gridFindRowByColumn(blacklist.grid, blacklist.gridcolumn_Name, "black1", sort_ascendingOrder), blacklist.gridEdit);
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('st-bw-type').setValue('inbound')");
        ys_waitingTime(1000);
        add_blacklist.save.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        ys_apply();
//        此时情况：admin黑名单：呼入2001    admin白名单：空
//                  me黑名单：空          me白名单：呼入2001
//                  勾选了仅白名单
    }
    @Test
    public void F2_onlyWhite4_admin2(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void F2_onlyWhite4_admin3(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出,预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("1000 <1000>","32001","Answered"," ",SPS,communication_outRoute);
    }
    //    取消勾选仅白名单模式
    @Test
    public void F2_onlyWhite4_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com","Yeastar202");
        }else {
            login("1000", "Yeastar202");
        }
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        ys_me_apply();
    }
    //    验证取消白名单模式后的呼出呼入，注意这里检查的cdr是在me页面
    @Test
    public void F2_onlyWhite4_admin5(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("1000 <1000>","32001","Answered",communication_outRoute);
    }
    @Test
    public void F2_onlyWhite4_admin6(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    //    重新勾选仅白名单模式，然后对me角色和admin角色的黑白名单修改
    @Test
    public void F2_onlyWhite4_admin7() {
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        me_whitelist.ok.click();
        ys_waitingTime(1000);
    }
    @Test
    public void F2_onlyWhite5_admin1(){
        Reporter.infoExec("修改me白名单——白名单_me");
        gridClick(me_whitelist.grid, gridFindRowByColumn(me_whitelist.grid, me_whitelist.gridColumn_Name, "白名单_me", sort_ascendingOrder), me_whitelist.gridEdit);
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('"+me_add_whitelist.routeTypeId+"').setValue('outbound')");
        ys_waitingTime(1000);
        me_add_whitelist.save.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_me_apply();

        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        Reporter.infoExec("修改admin黑名单");
        gridClick(blacklist.grid, gridFindRowByColumn(blacklist.grid, blacklist.gridcolumn_Name, "black1", sort_ascendingOrder), blacklist.gridEdit);
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('st-bw-type').setValue('outbound')");
        ys_waitingTime(1000);
        add_blacklist.save.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        ys_apply();
//        此时情况：admin黑名单：呼出2001    admin白名单：空
//                  me黑名单：空          me白名单：呼出2001
//                  勾选了仅白名单
    }
    @Test
    public void F2_onlyWhite5_admin2(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void F2_onlyWhite5_admin3(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(1000,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    //    取消勾选仅白名单模式
    @Test
    public void F2_onlyWhite5_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com","Yeastar202");
        }else {
            login("1000", "Yeastar202");
        }
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        ys_me_apply();
    }
    //    验证取消白名单模式后的呼出呼入，注意这里检查的cdr是在me页面
    @Test
    public void F2_onlyWhite5_admin5(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(1000,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void F2_onlyWhite5_admin6(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
    //    重新勾选仅白名单模式，然后对me角色和admin角色的黑白名单修改
    @Test
    public void F2_onlyWhite5_admin7() {
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        me_whitelist.ok.click();
        ys_waitingTime(1000);
    }
    @Test
    public void F2_onlyWhite6_admin1() throws InterruptedException {
        if(Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid))) != 0) {
            gridSeleteAll(me_whitelist.grid);
            me_whitelist.delete.click();
            ys_waitingTime(1000);
            me_whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        me_blacklist.me_Blacklist.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid))) != 0) {
            gridSeleteAll(me_blacklist.grid);
            me_blacklist.delete.click();
            ys_waitingTime(1000);
            me_blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }
        m_me.addMeBlacklist("黑名单_me",me_add_blacklist.routeType_Inbound,2001);
        ys_me_apply();

        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid))) != 0) {
            gridSeleteAll(blacklist.grid);
            blacklist.delete.click();
            ys_waitingTime(1000);
            blacklist.delete_yes.click();
            ys_waitingTime(1000);
        }
        whitelist.whitelist.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid))) != 0) {
            gridSeleteAll(whitelist.grid);
            whitelist.delete.click();
            ys_waitingTime(1000);
            whitelist.delete_yes.click();
            ys_waitingTime(1000);
        }
        whitelist.add.click();
        m_callFeature.addWhitelist("white1",add_whitelist.type_Inbound,2001);
        ys_apply();
//        此时情况：admin黑名单：空    admin白名单：呼入2001
//                  me黑名单：呼入2001          me白名单：空
//                  勾选了仅白名单
    }
    @Test
    public void F2_onlyWhite6_admin2(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void F2_onlyWhite6_admin3(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("1000 <1000>","32001","Answered"," ",SPS,communication_outRoute);
    }
    //    取消勾选仅白名单模式
    @Test
    public void F2_onlyWhite6_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com","Yeastar202");
        }else {
            login("1000", "Yeastar202");
        }
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        ys_me_apply();
    }
    //    验证取消白名单模式后的呼出呼入，注意这里检查的cdr是在me页面
    @Test
    public void F2_onlyWhite6_admin5(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("1000 <1000>","32001","Answered",communication_outRoute);
    }
    @Test
    public void F2_onlyWhite6_admin6(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    //    重新勾选仅白名单模式，然后对me角色和admin角色的黑白名单修改
    @Test
    public void F2_onlyWhite6_admin7() {
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        me_whitelist.ok.click();
        ys_waitingTime(1000);
    }
    @Test
    public void F2_onlyWhite7_admin1(){
        me_blacklist.me_Blacklist.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        Reporter.infoExec("修改me黑名单——black_me");
        gridClick(me_blacklist.grid, gridFindRowByColumn(me_blacklist.grid, me_blacklist.gridColumn_Name, "黑名单_me", sort_ascendingOrder), me_blacklist.gridEdit);
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('"+me_add_blacklist.routeTypeId+"').setValue('outbound')");
        ys_waitingTime(1000);
        me_add_blacklist.save.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        ys_me_apply();

        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        whitelist.whitelist.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        Reporter.infoExec("修改admin白名单——white1");
        gridClick(whitelist.grid, gridFindRowByColumn(whitelist.grid, whitelist.gridcolumn_Name, "white1", sort_ascendingOrder), whitelist.gridEdit);
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('st-bw-type').setValue('outbound')");
        ys_waitingTime(1000);
        add_whitelist.save.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        ys_apply();
//        此时情况：admin黑名单：空    admin白名单：呼2001
//                  me黑名单：呼2001          me白名单：空
//                  勾选了仅白名单
    }
    @Test
    public void F2_onlyWhite7_admin2(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(2001,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打991000,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打991000,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void F2_onlyWhite7_admin3(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(1000,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    //    取消勾选仅白名单模式
    @Test
    public void F2_onlyWhite7_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com","Yeastar202");
        }else {
            login("1000", "Yeastar202");
        }
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        ys_waitingTime(1000);
        me_whitelist.whitelistOnly.click();
        ys_waitingTime(1000);
        ys_me_apply();
    }
    //    验证取消白名单模式后的呼出呼入，注意这里检查的cdr是在me页面
    @Test
    public void F2_onlyWhite7_admin5(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        System.out.println("state1预期HUNGUP："+getExtensionStatus(1000,HUNGUP,8));
        ys_waitingTime(5000);
        int state = getExtensionStatus(1000,HUNGUP,8);
        System.out.println("state2："+state);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1000拨打32001,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打32001,预期呼出失败,实际呼出成功");
        }
    }
    @Test
    public void F2_onlyWhite7_admin6(){
        Reporter.infoExec(" 2001拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2001 <2001>","1000 <1000>","Answered",communication_inbound);
    }
//    先删admin的黑白名单，再来删me的，如果先去删me的，那么me中可能会存在admin角色的黑白名单（无法删除）那统计行数可能会出错
    @Test
    public void G1_blackAdmin_deleteOne_no() throws InterruptedException {
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        blacklist_whitelist.blacklist_Whitelist.click();
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        m_callFeature.addBlacklist_String("black1",add_blacklist.type_Outbound,"2001");
        m_callFeature.addBlacklist_String("black2",add_blacklist.type_Inbound,"2002");
        m_callFeature.addBlacklist_String("black3",add_blacklist.type_Both,"2003");
        m_callFeature.addBlacklist_String("black4",add_blacklist.type_Outbound,"2004");

        setPageShowNum(blacklist.grid, 100);
        Reporter.infoExec(" 删除单个admin黑名单black1——选择no"); //执行操作
//       定位要删除的那条呼出路由
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(blacklist.grid, blacklist.gridcolumn_Name, "black1", sort_ascendingOrder)));
        //还未操作前呼出路由的所有条数
        int rows = Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid)));
        System.out.println("预期值:" + rows);
        gridClick(blacklist.grid, row, blacklist.gridDelete);
        blacklist.delete_no.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid)));
        System.out.println("实际值:" + row1);
        YsAssert.assertEquals(row1, rows, "删除单个admin黑名单black1-取消删除");
    }
    @Test
    public void G2_blackAdmin_deleteOne_yes(){
        Reporter.infoExec(" 删除单个admin黑名单black1——选择yes"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid)));
        //       定位要删除的那条呼出路由
        int row1 = Integer.parseInt(String.valueOf(gridFindRowByColumn(blacklist.grid, blacklist.gridcolumn_Name, "black1", sort_ascendingOrder)));
        gridClick(blacklist.grid, row1, blacklist.gridDelete);
        blacklist.delete_yes.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid)));
        System.out.println("实际值row2:" + row2);
        int row3 = row - 1;
        System.out.println("期望值row3:" + row3);
        ys_apply();
        YsAssert.assertEquals(row2, row3, "删除单个admin黑名单black1——确定删除");
    }
    @Test
    public void G3_blackAdmin_deletePart_no() throws InterruptedException {
        Reporter.infoExec(" 全部勾选admin_black，再取消某条的勾选后-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid)));
        System.out.println("期望值row1:" + row1);
        int row2 = Integer.parseInt(String.valueOf(gridFindRowByColumn(blacklist.grid, blacklist.gridcolumn_Name, "black2", sort_ascendingOrder)));
        int row3 = Integer.parseInt(String.valueOf(gridFindRowByColumn(blacklist.grid, blacklist.gridcolumn_Name, "black3", sort_ascendingOrder)));
//        全部勾选
        gridSeleteAll(blacklist.grid);
//        取消勾选OutboundRoute2和OutboundRoute3
        gridCheck(blacklist.grid, row2, blacklist.gridcolumn_Check);
        gridCheck(blacklist.grid, row3, blacklist.gridcolumn_Check);
//        点击删除按钮
        blacklist.delete.click();
        blacklist.delete_no.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        int row4 = Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid)));
        System.out.println("实际值row4:" + row4);
        YsAssert.assertEquals(row4, row1, "全部勾选admin_black，再取消某条的勾选后-取消删除");
    }
    @Test
    public void G4_blackAdmin_deletePart_yes() throws InterruptedException {
        Reporter.infoExec(" 全部勾选admin_black，再取消某条的勾选后-确定删除"); //执行操作
        blacklist.delete.click();
        blacklist.delete_yes.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid)));
        System.out.println("实际row6:" + row1);
        ys_apply();
        YsAssert.assertEquals(row1, 2, "全部勾选admin_black，再取消某条的勾选后-确定删除");
    }
    @Test
    public void G5_blackAdmin_deleteAll_no() throws InterruptedException {
//        勾选全部进行删除
        Reporter.infoExec(" 勾选全部admin_black-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid)));
//        全部勾选
        gridSeleteAll(blacklist.grid);
//        点击删除按钮
        blacklist.delete.click();
        blacklist.delete_no.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid)));
        System.out.println("实际row2:" + row2);
        YsAssert.assertEquals(row1, row2, "全部勾选admin_black-取消删除");
    }
    @Test
    public void G6_blackAdmin_deleteAll_yes() throws InterruptedException {
        Reporter.infoExec(" 勾选全部admin_black-确定删除"); //执行操作
        blacklist.delete.click();
        blacklist.delete_yes.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        int row = Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid)));
        System.out.println("实际row:" + row);
        ys_apply();
        YsAssert.assertEquals(row, 0, "全部勾选admin_black-确定删除");
    }
    @Test
    public void H1_whiteAdmin_deleteOne_no() throws InterruptedException {
        whitelist.whitelist.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        m_callFeature.addWhitelist_String("white5",add_whitelist.type_Outbound,"2001");
        m_callFeature.addWhitelist_String("white2",add_whitelist.type_Inbound,"2002");
        m_callFeature.addWhitelist_String("white3",add_whitelist.type_Both,"2003");
        m_callFeature.addWhitelist_String("white4",add_whitelist.type_Outbound,"2004");

        setPageShowNum(whitelist.grid, 100);
        Reporter.infoExec(" 删除单个admin白名单white1——选择no"); //执行操作
//       定位要删除的那条呼出路由
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(whitelist.grid, whitelist.gridcolumn_Name, "white1", sort_ascendingOrder)));
        //还未操作前呼出路由的所有条数
        int rows = Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid)));
        System.out.println("预期值:" + rows);
        gridClick(whitelist.grid, row, whitelist.gridDelete);
        whitelist.delete_no.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid)));
        System.out.println("实际值:" + row1);
        YsAssert.assertEquals(row1, rows, "删除单个admin白名单white1-取消删除");
    }
    @Test
    public void H2_whiteAdmin_deleteOne_yes(){
        Reporter.infoExec(" 删除单个admin白名单white1——选择yes"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid)));
        //       定位要删除的那条呼出路由
        int row1 = Integer.parseInt(String.valueOf(gridFindRowByColumn(whitelist.grid, whitelist.gridcolumn_Name, "white1", sort_ascendingOrder)));
        gridClick(whitelist.grid, row1, whitelist.gridDelete);
        whitelist.delete_yes.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid)));
        System.out.println("实际值row2:" + row2);
        int row3 = row - 1;
        System.out.println("期望值row3:" + row3);
        ys_apply();
        YsAssert.assertEquals(row2, row3, "删除单个admin白名单white1——确定删除");
    }
    @Test
    public void H3_whiteAdmin_deletePart_no() throws InterruptedException {
        Reporter.infoExec(" 全部勾选admin_white，再取消某条的勾选后-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid)));
        System.out.println("期望值row1:" + row1);
        int row2 = Integer.parseInt(String.valueOf(gridFindRowByColumn(whitelist.grid, whitelist.gridcolumn_Name, "white2", sort_ascendingOrder)));
        int row3 = Integer.parseInt(String.valueOf(gridFindRowByColumn(whitelist.grid, whitelist.gridcolumn_Name, "white3", sort_ascendingOrder)));
//        全部勾选
        gridSeleteAll(whitelist.grid);
//        取消勾选white2和white3
        gridCheck(whitelist.grid, row2, whitelist.gridcolumn_Check);
        gridCheck(whitelist.grid, row3, whitelist.gridcolumn_Check);
//        点击删除按钮
        whitelist.delete.click();
        whitelist.delete_no.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        int row4 = Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid)));
        System.out.println("实际值row4:" + row4);
        YsAssert.assertEquals(row4, row1, "全部勾选admin_white，再取消某条的勾选后-取消删除");
    }
    @Test
    public void H4_whiteAdmin_deletePart_yes() throws InterruptedException {
        Reporter.infoExec(" 全部勾选admin_white，再取消某条的勾选后-确定删除"); //执行操作
        whitelist.delete.click();
        whitelist.delete_yes.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid)));
        System.out.println("实际row6:" + row1);
        ys_apply();
        YsAssert.assertEquals(row1, 2, "全部勾选admin_white，再取消某条的勾选后-确定删除");
    }
    @Test
    public void H5_whiteAdmin_deleteAll_no() throws InterruptedException {
//        勾选全部进行删除
        Reporter.infoExec(" 全部勾选admin_white-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid)));
//        全部勾选
        gridSeleteAll(whitelist.grid);
//        点击删除按钮
        whitelist.delete.click();
        whitelist.delete_no.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid)));
        System.out.println("实际row2:" + row2);
        YsAssert.assertEquals(row1, row2, "全部勾选admin_white-取消删除");
    }
    @Test
    public void H6_whiteAdmin_deleteAll_yes() throws InterruptedException {
        Reporter.infoExec(" 全部勾选admin_white-确定删除"); //执行操作
        whitelist.delete.click();
        whitelist.delete_yes.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        int row = Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid)));
        System.out.println("实际row:" + row);
        ys_apply();
        YsAssert.assertEquals(row, 0, "全部勾选admin_white-确定删除");
    }
    @Test
    public void I1_whiteMe_deleteOne_no() throws InterruptedException {
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com","Yeastar202");
        }else {
            login("1000", "Yeastar202");
        }
        me.me.click();
        me.me_Blacklist_Whitelist.click();
        me_whitelist.me_Whitelist.click();
        m_me.addMeWhitelist("white1",me_add_whitelist.routeType_Both,2001);
        m_me.addMeWhitelist("white2",me_add_whitelist.routeType_Inbound,2002);
        m_me.addMeWhitelist("white3",me_add_whitelist.routeType_Outbound,2003);
        m_me.addMeWhitelist("white4",me_add_whitelist.routeType_Both,2004);
        Reporter.infoExec(" 删除单个me白名单white1——选择no"); //执行操作
//       定位要删除的那条呼出路由
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(me_whitelist.grid, me_whitelist.gridColumn_Name, "white1", sort_ascendingOrder)));
        //还未操作前呼出路由的所有条数
        int rows = Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid)));
        System.out.println("预期值:" + rows);
        gridClick(me_whitelist.grid, row, me_whitelist.gridDelete);
        me_whitelist.delete_no.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid)));
        System.out.println("实际值:" + row1);
        YsAssert.assertEquals(row1, rows, "删除单个me白名单white1-取消删除");
    }
    @Test
    public void I2_whiteMe_deleteOne_yes(){
        Reporter.infoExec(" 删除单个me白名单white1——选择yes"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid)));
        //       定位要删除的那条呼出路由
        int row1 = Integer.parseInt(String.valueOf(gridFindRowByColumn(me_whitelist.grid, me_whitelist.gridColumn_Name, "white1", sort_ascendingOrder)));
        gridClick(me_whitelist.grid, row1, me_whitelist.gridDelete);
        me_whitelist.delete_yes.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid)));
        System.out.println("实际值row2:" + row2);
        int row3 = row - 1;
        System.out.println("期望值row3:" + row3);
        ys_apply();
        YsAssert.assertEquals(row2, row3, "删除单个me白名单white1——确定删除");
    }
    @Test
    public void I3_whiteMe_deletePart_no() throws InterruptedException {
        Reporter.infoExec(" 全部勾选me_white，再取消某条的勾选后-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid)));
        System.out.println("期望值row1:" + row1);
        int row2 = Integer.parseInt(String.valueOf(gridFindRowByColumn(me_whitelist.grid, me_whitelist.gridColumn_Name, "white2", sort_ascendingOrder)));
        int row3 = Integer.parseInt(String.valueOf(gridFindRowByColumn(me_whitelist.grid, me_whitelist.gridColumn_Name, "white3", sort_ascendingOrder)));
//        全部勾选
        gridSeleteAll(me_whitelist.grid);
//        取消勾选OutboundRoute2和OutboundRoute3
        gridCheck(me_whitelist.grid, row2, me_whitelist.gridColumn_Check);
        gridCheck(me_whitelist.grid, row3, me_whitelist.gridColumn_Check);
//        点击删除按钮
        me_whitelist.delete.click();
        me_whitelist.delete_no.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        int row4 = Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid)));
        System.out.println("实际值row4:" + row4);
        YsAssert.assertEquals(row4, row1, "全部勾选me_white，再取消某条的勾选后-取消删除");
    }
    @Test
    public void I4_whiteMe_deletePart_yes() throws InterruptedException {
        Reporter.infoExec(" 全部勾选me_white，再取消某条的勾选后-确定删除"); //执行操作
        me_whitelist.delete.click();
        me_whitelist.delete_yes.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid)));
        System.out.println("实际row6:" + row1);
        ys_apply();
        YsAssert.assertEquals(row1, 2, "全部勾选me_white，再取消某条的勾选后-确定删除");
    }
    @Test
    public void I5_whiteMe_deleteAll_no() throws InterruptedException {
//        勾选全部进行删除
        Reporter.infoExec(" 勾选全部me_white-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid)));
//        全部勾选
        gridSeleteAll(me_whitelist.grid);
//        点击删除按钮
        me_whitelist.delete.click();
        me_whitelist.delete_no.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid)));
        System.out.println("实际row2:" + row2);
        YsAssert.assertEquals(row1, row2, "全部勾选me_white-取消删除");
    }
    @Test
    public void I6_whiteMe_deleteAll_yes() throws InterruptedException {
        Reporter.infoExec(" 勾选全部me_white-确定删除"); //执行操作
        me_whitelist.delete.click();
        me_whitelist.delete_yes.click();
        ys_waitingLoading(me_whitelist.grid_Mask);
        int row = Integer.parseInt(String.valueOf(gridLineNum(me_whitelist.grid)));
        System.out.println("实际row:" + row);
        ys_apply();
        YsAssert.assertEquals(row, 0, "全部勾选me_white-确定删除");
    }
    @Test
    public void J1_blackMe_deleteOne_no() throws InterruptedException {
        me.me_Blacklist_Whitelist.click();
        me_blacklist.me_Blacklist.click();
        m_me.addMeBlacklist("black1",me_add_blacklist.routeType_Both,2001);
        m_me.addMeBlacklist("black2",me_add_blacklist.routeType_Inbound,2002);
        m_me.addMeBlacklist("black3",me_add_blacklist.routeType_Outbound,2003);
        Reporter.infoExec(" 删除单个me黑名单black1——选择no"); //执行操作
//       定位要删除的那条呼出路由
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(me_blacklist.grid, me_blacklist.gridColumn_Name, "black1", sort_ascendingOrder)));
        //还未操作前呼出路由的所有条数
        int rows = Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid)));
        System.out.println("预期值:" + rows);
        gridClick(me_blacklist.grid, row, me_blacklist.gridDelete);
        me_blacklist.delete_no.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid)));
        System.out.println("实际值:" + row1);
        YsAssert.assertEquals(row1, rows, "删除单个me黑名单black1-取消删除");
    }
    @Test
    public void J2_blackMe_deleteOne_yes(){
        Reporter.infoExec(" 删除单个me黑名单black1——选择yes"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid)));
        //       定位要删除的那条呼出路由
        int row1 = Integer.parseInt(String.valueOf(gridFindRowByColumn(me_blacklist.grid, me_blacklist.gridColumn_Name, "black1", sort_ascendingOrder)));
        gridClick(me_blacklist.grid, row1, me_blacklist.gridDelete);
        me_blacklist.delete_yes.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid)));
        System.out.println("实际值row2:" + row2);
        int row3 = row - 1;
        System.out.println("期望值row3:" + row3);
        ys_apply();
        YsAssert.assertEquals(row2, row3, "删除单个me黑名单black1——确定删除");
    }
    @Test
    public void J3_blackMe_deletePart_no() throws InterruptedException {
        Reporter.infoExec(" 全部勾选me_black，再取消某条的勾选后-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid)));
        System.out.println("期望值row1:" + row1);
        int row2 = Integer.parseInt(String.valueOf(gridFindRowByColumn(me_blacklist.grid, me_blacklist.gridColumn_Name, "black2", sort_ascendingOrder)));
        int row3 = Integer.parseInt(String.valueOf(gridFindRowByColumn(me_blacklist.grid, me_blacklist.gridColumn_Name, "black3", sort_ascendingOrder)));
//        全部勾选
        gridSeleteAll(me_blacklist.grid);
//        取消勾选OutboundRoute2和OutboundRoute3
        gridCheck(me_blacklist.grid, row2, me_blacklist.gridColumn_Check);
        gridCheck(me_blacklist.grid, row3, me_blacklist.gridColumn_Check);
//        点击删除按钮
        me_blacklist.delete.click();
        me_blacklist.delete_no.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        int row4 = Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid)));
        System.out.println("实际值row4:" + row4);
        YsAssert.assertEquals(row4, row1, "全部勾选me_black，再取消某条的勾选后-取消删除");
    }
    @Test
    public void J4_blackMe_deletePart_yes() throws InterruptedException {
        Reporter.infoExec(" 全部勾选me_black，再取消某条的勾选后-确定删除"); //执行操作
        me_blacklist.delete.click();
        me_blacklist.delete_yes.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid)));
        System.out.println("实际row6:" + row1);
        ys_apply();
        YsAssert.assertEquals(row1, 2, "全部勾选me_black，再取消某条的勾选后-确定删除");
    }
    @Test
    public void J5_blackMe_deleteAll_no() throws InterruptedException {
//        勾选全部进行删除
        Reporter.infoExec(" 全部勾选me_black-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid)));
//        全部勾选
        gridSeleteAll(me_blacklist.grid);
//        点击删除按钮
        me_blacklist.delete.click();
        me_blacklist.delete_no.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid)));
        System.out.println("实际row2:" + row2);
        YsAssert.assertEquals(row1, row2, "全部勾选me_black-取消删除");
    }
    @Test
    public void J6_blackMe_deleteAll_yes() throws InterruptedException {
        Reporter.infoExec(" 全部勾选me_black-确定删除"); //执行操作
        me_blacklist.delete.click();
        me_blacklist.delete_yes.click();
        ys_waitingLoading(me_blacklist.grid_Mask);
        int row = Integer.parseInt(String.valueOf(gridLineNum(me_blacklist.grid)));
        System.out.println("实际row:" + row);
        ys_apply();
        YsAssert.assertEquals(row, 0, "全部勾选me_black-确定删除");
    }*/
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
        Reporter.infoAfterClass("执行完毕：====== Blacklist/Whitelist ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);
    }
}
