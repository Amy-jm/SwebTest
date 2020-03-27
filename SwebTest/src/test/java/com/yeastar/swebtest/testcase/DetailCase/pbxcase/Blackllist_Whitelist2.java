package com.yeastar.swebtest.testcase.DetailCase.pbxcase;

import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Caroline on 2018/2/12.
 */
public class Blackllist_Whitelist2 extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：====== Blacklist/Whitelist ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && LOGIN_ADMIN.equals("yes") && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }
    @Test
    public void A1_addExtensions(){
        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, 1);
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_CreateAccount(1100, EXTENSION_PASSWORD, "UDP", UDP_PORT, 2);
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_CreateAccount(1101, EXTENSION_PASSWORD, "UDP", UDP_PORT, 3);
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_CreateAccount(1102, EXTENSION_PASSWORD, "UDP", UDP_PORT, 4);
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_CreateAccount(1103, EXTENSION_PASSWORD, "UDP", UDP_PORT, 5);
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_CreateAccount(1105, EXTENSION_PASSWORD, "UDP", UDP_PORT, 7);
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1106"); //执行操作
        pjsip.Pj_CreateAccount(1106, EXTENSION_PASSWORD, "UDP", UDP_PORT, 8);

        Reporter.infoExec(" 辅助设备1注册分机3001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3001, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3001, DEVICE_ASSIST_1);

        Reporter.infoExec(" 辅助设备1注册分机3004"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3004, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3004, DEVICE_ASSIST_1);

        Reporter.infoExec(" 辅助设备1注册分机3005"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3005, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3005, DEVICE_ASSIST_1);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2000, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2001, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2002"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2002, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2002, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2006"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2006, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2006, DEVICE_ASSIST_2);
        closePbxMonitor();
    }
    @Test
    public void E1_onlyWhite_me1() throws InterruptedException {
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com",EXTENSION_PASSWORD);
        }else {
            login("1000", EXTENSION_PASSWORD);
        }
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
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
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
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
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
    public void E2_onlyWhite_me6(){
        Reporter.infoExec(" 2006拨打991000通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2006,"991000",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
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
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
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
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_me.checkCDR("2002 <2002>","1000 <1000>","Answered",communication_inbound);
    }
    /*
    * admin角色的黑名单命名为black1，白名单命名为white1
    * me角色的黑名单命名为 黑名单_me，白名单命名为 白名单_me
    * */
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
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("1000 <1000>","32001","Answered"," ",SPS,communication_outRoute);
    }
    //    取消勾选仅白名单模式
    @Test
    public void F1_onlyWhite1_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com",EXTENSION_PASSWORD);
        }else {
            login("1000", EXTENSION_PASSWORD);
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
    public void F1_onlyWhite1_admin6(){
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
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("1000 <1000>","32001","Answered"," ",SPS,communication_outRoute);
    }
    //    取消勾选仅白名单模式
    @Test
    public void F1_onlyWhite2_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com",EXTENSION_PASSWORD);
        }else {
            login("1000", EXTENSION_PASSWORD);
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
    public void F1_onlyWhite2_admin6(){
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
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("1000 <1000>","32001","Answered"," ",SPS,communication_outRoute);
    }
    //    取消勾选仅白名单模式
    @Test
    public void F1_onlyWhite3_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com",EXTENSION_PASSWORD);
        }else {
            login("1000", EXTENSION_PASSWORD);
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
    public void F1_onlyWhite3_admin6(){
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
    public void F1_onlyWhite4_admin3(){
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
    public void F1_onlyWhite4_admin6(){
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
            login("1000@yeastar.com",EXTENSION_PASSWORD);
        }else {
            login("1000", EXTENSION_PASSWORD);
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
    public void F1_onlyWhite6_admin3(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","32001","Answered"," ",SPS,communication_outRoute);
    }
    //    取消勾选仅白名单模式
    @Test
    public void F1_onlyWhite6_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com",EXTENSION_PASSWORD);
        }else {
            login("1000", EXTENSION_PASSWORD);
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
    public void F1_onlyWhite6_admin6(){
        Reporter.infoExec(" 1000拨打32001通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32001",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
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
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
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
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
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
            login("1000@yeastar.com",EXTENSION_PASSWORD);
        }else {
            login("1000", EXTENSION_PASSWORD);
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
//        pjsip.Pj_Unregister_Account(1000);
//        pjsip.Pj_Unregister_Account(1100);
//        pjsip.Pj_Unregister_Account(1101);
//        pjsip.Pj_Unregister_Account(1102);
//        pjsip.Pj_Unregister_Account(1103);
//        pjsip.Pj_Unregister_Account(1105);
//        pjsip.Pj_Unregister_Account(1106);
//        pjsip.Pj_Unregister_Account(3001);
//        pjsip.Pj_Unregister_Account(3004);
//        pjsip.Pj_Unregister_Account(3005);
//        pjsip.Pj_Unregister_Account(2000);
//        pjsip.Pj_Unregister_Account(2001);
//        pjsip.Pj_Unregister_Account(2002);
//        pjsip.Pj_Unregister_Account(2006);
//        ys_waitingTime(6000);
//        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
//        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
//        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
//        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
//        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
//        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
//        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 主测设备注册分机1106"); //执行操作
//        pjsip.Pj_CreateAccount(1106, EXTENSION_PASSWORD, "UDP", UDP_PORT, 8);
//
//        Reporter.infoExec(" 辅助设备1注册分机3001"); //执行操作
//        pjsip.Pj_Register_Account_WithoutAssist(3001, DEVICE_ASSIST_1);
//
//        Reporter.infoExec(" 辅助设备1注册分机3004"); //执行操作
//        pjsip.Pj_Register_Account_WithoutAssist(3004, DEVICE_ASSIST_1);
//
//        Reporter.infoExec(" 辅助设备1注册分机3005"); //执行操作
//        pjsip.Pj_Register_Account_WithoutAssist(3005, DEVICE_ASSIST_1);
//
//        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
//        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);
//
//        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
//        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
//
//        Reporter.infoExec(" 辅助设备2注册分机2002"); //执行操作
//        pjsip.Pj_Register_Account_WithoutAssist(2002, DEVICE_ASSIST_2);
//
//        Reporter.infoExec(" 辅助设备2注册分机2006"); //执行操作
//        pjsip.Pj_Register_Account_WithoutAssist(2006, DEVICE_ASSIST_2);
//        closePbxMonitor();
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
            login("1000@yeastar.com",EXTENSION_PASSWORD);
        }else {
            login("1000", EXTENSION_PASSWORD);
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
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","32001","Answered"," ",SPS,communication_outRoute);
    }
    @Test
    public void F2_onlyWhite2_admin3(){
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
    //    取消勾选仅白名单模式
    @Test
    public void F2_onlyWhite2_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com",EXTENSION_PASSWORD);
        }else {
            login("1000", EXTENSION_PASSWORD);
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
    public void F2_onlyWhite2_admin6(){
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
            login("1000@yeastar.com",EXTENSION_PASSWORD);
        }else {
            login("1000", EXTENSION_PASSWORD);
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
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("1000 <1000>","32001","Answered"," ",SPS,communication_outRoute);
    }
    //    取消勾选仅白名单模式
    @Test
    public void F2_onlyWhite4_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com",EXTENSION_PASSWORD);
        }else {
            login("1000", EXTENSION_PASSWORD);
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
            login("1000@yeastar.com",EXTENSION_PASSWORD);
        }else {
            login("1000", EXTENSION_PASSWORD);
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
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
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
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        closePbxMonitor();
        m_extension.checkCDR("1000 <1000>","32001","Answered"," ",SPS,communication_outRoute);
    }
    //    取消勾选仅白名单模式
    @Test
    public void F2_onlyWhite6_admin4_cancel(){
        logout();
        if (PRODUCT.equals(CLOUD_PBX)){
            login("1000@yeastar.com",EXTENSION_PASSWORD);
        }else {
            login("1000", EXTENSION_PASSWORD);
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
            login("1000@yeastar.com",EXTENSION_PASSWORD);
        }else {
            login("1000", EXTENSION_PASSWORD);
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
        ys_waitingTime(5000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为"+getExtensionStatus(1000, TALKING, 8));
        }
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
            login("1000@yeastar.com",EXTENSION_PASSWORD);
        }else {
            login("1000", EXTENSION_PASSWORD);
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
        Reporter.infoAfterClass("执行完毕：====== Blacklist/Whitelist ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);
    }
}
