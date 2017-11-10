package com.yeastar.swebtest.testcase.smokecase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Yeastar on 2017/7/20.
 */
public class OutboundRoutes extends SwebDriver {

    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("打开游览器并登录设备_OutboundRoutes"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
        pjsip.Pj_CreateAccount(1000,"Yeastar202","UDP",1);
        pjsip.Pj_CreateAccount(3000,"Yeastar202","UDP",-1);
        pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",-1);

        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
    }
    @BeforeClass
    public void InitOutboundRoutes() {
        if(Single_Init){
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
            settings.callControl_panel.click();
            outboundRoutes.outboundRoutes.click();
            ys_waitingLoading(outboundRoutes.grid_Mask);
            if(Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid))) != 0) {
                gridSeleteAll(outboundRoutes.grid);
                outboundRoutes.delete.click();
                outboundRoutes.delete_yes.click();
            }
            time_Conditions.timeConditions.click();
            timeConditions.timeConditions.click();
            ys_waitingTime(2000);
            if(Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid))) != 0){
                gridSeleteAll(timeConditions.grid);
                timeConditions.delete.click();
                timeConditions.delete_yes.click();
            }
            holiday.holiday.click();
            ys_waitingTime(2000);
            if(Integer.parseInt(String.valueOf(gridLineNum(holiday.grid))) != 0) {
                gridSeleteAll(holiday.grid);
                holiday.delete.click();
                holiday.delete_yes.click();
            }
            settings.callFeatures_tree.click();
            ivr.IVR.click();
            ys_waitingLoading(ivr.grid_Mask);
            if(Integer.parseInt(String.valueOf(gridLineNum(ivr.grid))) != 0) {
                gridSeleteAll(ivr.grid);
                ivr.delete.click();
                ivr.delete_yes.click();
            }
            callFeatures.more.click();
            disa.DISA.click();
            ys_waitingLoading(disa.grid_Mask);
            if(Integer.parseInt(String.valueOf(gridLineNum(disa.grid))) != 0) {
                gridSeleteAll(disa.grid);
                disa.delete.click();
                disa.delete_yes.click();
            }
            closeSetting();
        }
    }
    @BeforeMethod
    public void waitMethod() throws InterruptedException {
        ys_waitingTime(1000);
    }
    @Test
    public void A_TimeCondition() throws InterruptedException {
        Reporter.infoExec("设置Time Conditions");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        time_Conditions.timeConditions.click();
        m_callcontrol.addTimeContion("time1","00:00","23:00",false,"all");
    }
    @Test
    public void B_Holiday() throws InterruptedException {
        Reporter.infoExec("设置Holiday");
        holiday.holiday.click();
        m_callcontrol.addHolidayByDay("holiday1","2017-04-05","2017-04-05");
    }

    @Test
    public void C_CreateOutRoutes() throws InterruptedException {
        Reporter.infoExec("创建呼出路由");
        if(Single_Device_Test){
            pageDeskTop.settings.click();
            settings.callControl_panel.click();
        }
        outboundRoutes.outboundRoutes.click();
        ArrayList<String> arrayTrunk = new ArrayList<>();
        if(!SIPTrunk.equals("null")){
            arrayTrunk.add(SIPTrunk);
        }
        if(!IAXTrunk.equals("null")){
            arrayTrunk.add(IAXTrunk);
        }
        if(!SPS.equals("null")){
            arrayTrunk.add(SPS);
        }
        if(!SPX.equals("null")){
            arrayTrunk.add(SPX);
        }
        if(!FXO_1.equals("null")){
            arrayTrunk.add(FXO_1);
        }
        if(!BRI_1.equals("null")){
            arrayTrunk.add(BRI_1);
        }
        if(!E1.equals("null")) {
            arrayTrunk.add(E1);
        }
        if(!GSM.equals("null")){
            arrayTrunk.add(GSM);
        }
        ArrayList<String> arrayExtension = new ArrayList<>();
        arrayExtension.add("extgroup1");

        m_callcontrol.addOutboundRoute("outrouter1","90.","2","",arrayExtension,arrayTrunk);
        Thread.sleep(2000);
        gridClick(outboundRoutes.grid,1,outboundRoutes.gridUp);
        ys_waitingLoading(outboundRoutes.grid_Mask);
        Thread.sleep(2000);
        gridClick(outboundRoutes.grid,1,outboundRoutes.gridEdit);
        ys_waitingMask();
//        add_outbound_routes.rrmemoryHunt.click();
        System.out.println("Ext.getCmp('"+add_outbound_routes.list_TimeContion1+"').setValue(true)");
        executeJs("Ext.getCmp("+add_outbound_routes.list_TimeContion1+").setValue(true)");
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        pageDeskTop.apply.click();
    }

    @Test
    public void D_CreateDISA() throws InterruptedException {
        Reporter.infoExec("创建Disa");
        settings.callFeatures_tree.click();
        callFeatures.more.click();
        disa.DISA.click();
        m_callFeature.addDISA("disa1","",0,0,"outrouter1");

    }

    @Test
    public void E_CreateIVR() throws InterruptedException {
        Reporter.infoExec("创建IVR");
        if(Single_Device_Test){
            pageDeskTop.settings.click();
            settings.callFeatures_panel.click();
        }
        callFeatures.back.click();
        ivr.IVR.click();
        m_callFeature.addIVR("ivr1","6500");
        gridClick(ivr.grid, gridFindRowByColumn(ivr.grid,ivr.gridcolumn_Name,"ivr1",sort_ascendingOrder),ivr.gridEdit);
        add_ivr_keyPressEvent.keyPressEvent.click();
        Thread.sleep(2000);
        executeJs("Ext.getCmp('"+add_ivr_keyPressEvent.s_press0+"').setValue('"+add_ivr_keyPressEvent.s_extensin+"')");
        Thread.sleep(500);
        executeJs("Ext.getCmp('"+add_ivr_keyPressEvent.s_press1+"').setValue('"+add_ivr_keyPressEvent.s_voicemail+"')");
        Thread.sleep(500);
        executeJs("Ext.getCmp('"+add_ivr_keyPressEvent.s_press2+"').setValue('"+add_ivr_keyPressEvent.s_ringGroup+"')");
        Thread.sleep(500);
        executeJs("Ext.getCmp('"+add_ivr_keyPressEvent.s_press3+"').setValue('"+add_ivr_keyPressEvent.s_queue+"')");
        Thread.sleep(500);
        executeJs("Ext.getCmp('"+add_ivr_keyPressEvent.s_press4+"').setValue('"+add_ivr_keyPressEvent.s_conference+"')");
        Thread.sleep(500);
        executeJs("Ext.getCmp('"+add_ivr_keyPressEvent.s_press5+"').setValue('"+add_ivr_keyPressEvent.s_disa+"')");
        Thread.sleep(500);
        executeJs("Ext.getCmp('"+add_ivr_keyPressEvent.s_press6+"').setValue('"+add_ivr_keyPressEvent.s_callback+"')");
        Thread.sleep(500);
        executeJs("Ext.getCmp('"+add_ivr_keyPressEvent.s_press7+"').setValue('"+add_ivr_keyPressEvent.s_faxToMail+"')");
        Thread.sleep(500);
        comboboxSet(add_ivr_keyPressEvent.d_press0,extensionList,"1100");
        Thread.sleep(500);
        comboboxSet(add_ivr_keyPressEvent.d_press1,extensionList,"1100");
        Thread.sleep(500);
//        comboboxSelect(add_ivr_keyPressEvent.d_press7,extensionList,"1000 - 1000 ( Not Set Email )");
        add_ivr_keyPressEvent.save.click();
        ys_apply();
    }

    @Test
    public void G_CallSip() throws InterruptedException {
        Reporter.infoExec("SipTrunk外线呼出 1000打 903000");
        Thread.sleep(5000);
        pjsip.Pj_Make_Call_Auto_Answer(1000,"903000", DEVICE_IP_LAN,true);
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();
        Thread.sleep(2000);
        m_extension.checkCDR("1000 <1000>","903000","Answered","",SIPTrunk,communication_outRoute);
    }
    @Test
    public void H_CallFail() throws InterruptedException {
        Reporter.infoExec("SipTrunk外线呼出失败1001打903000");

        Thread.sleep(5000);
//        String callee_status = pjsip.Pj_Make_Call_Auto_Answer(1001,3000,"90", DEVICE_IP_LAN,true);
//        YsAssert.assertEquals(callee_status,"Busy",String.valueOf(3000)+"接听失败");

//        m_extension.checkCDR(1001,3000,"idle");
    }
    @Test
    public void I_CallIax() throws InterruptedException {
        Reporter.infoExec("IAXTrunk外线呼出1000打903000");
        if(Single_Device_Test){
            pageDeskTop.settings.click();
            settings.callControl_panel.click();
        }else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
        }
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"outrouter1",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        add_outbound_routes.mt_RemoveAllFromSelected.click();
        listSelect(add_outbound_routes.list_Trunk,trunkList,IAXTrunk);
        add_outbound_routes.save.click();
        ys_apply();
        pjsip.Pj_Make_Call_Auto_Answer(1000,"903000", DEVICE_IP_LAN,true);
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","903000","Answered","",IAXTrunk,communication_outRoute);
    }
    @Test
    public void J_CallSps() throws InterruptedException {
        Reporter.infoExec("SPS外线呼出1000打902000");
        if(Single_Device_Test){
            pageDeskTop.settings.click();
            settings.callControl_panel.click();
        }else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
        }
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"outrouter1",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        add_outbound_routes.mt_RemoveAllFromSelected.click();
        listSelect(add_outbound_routes.list_Trunk,trunkList,SPS);
        add_outbound_routes.save.click();
        ys_apply();
        pjsip.Pj_Make_Call_Auto_Answer(1000,"902000",DEVICE_IP_LAN,true);
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();

        m_extension.checkCDR("1000 <1000>","902000","Answered","",SPS,communication_outRoute);
    }
    @Test
    public void K_CallSpx() throws InterruptedException {
        Reporter.infoExec("SPX外线呼出1000打902000");
        if(Single_Device_Test){
            pageDeskTop.settings.click();
            settings.callControl_panel.click();
        }else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
        }
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"outrouter1",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        add_outbound_routes.mt_RemoveAllFromSelected.click();
        listSelect(add_outbound_routes.list_Trunk,trunkList,SPX);
        add_outbound_routes.save.click();
        ys_apply();
        pjsip.Pj_Make_Call_Auto_Answer(1000,"902000", DEVICE_IP_LAN,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","902000","Answered","",SPX,communication_outRoute);
    }
    @Test
    public void L_CallPstn() throws InterruptedException {
        Reporter.infoExec("PSTN外线呼出1000打902000");
        if(Single_Device_Test){
            pageDeskTop.settings.click();
            settings.callControl_panel.click();
        }else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
        }
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"outrouter1",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        add_outbound_routes.mt_RemoveAllFromSelected.click();
        listSelect(add_outbound_routes.list_Trunk,trunkList,FXO_1);
        add_outbound_routes.save.click();
        ys_apply();
        pjsip.Pj_Make_Call_Auto_Answer(1000,"903000" ,DEVICE_IP_LAN,true);
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","903000","Answered","",FXO_1,communication_outRoute);

    }
    @Test
    public void M_CallBri() throws InterruptedException {
        if(BRI_1.equals("null")){
            return;
        }
        Reporter.infoExec("BRI外线呼出1000打902000");
        if(Single_Device_Test){
            pageDeskTop.settings.click();
            settings.callControl_panel.click();
        }else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
        }
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"outrouter1",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        add_outbound_routes.mt_RemoveAllFromSelected.click();
        listSelect(add_outbound_routes.list_Trunk,trunkList,BRI_1);
        add_outbound_routes.save.click();
        ys_apply();
        pjsip.Pj_Make_Call_Auto_Answer(1000,"902000", DEVICE_IP_LAN,true);
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","902000","Answered","",BRI_1,communication_outRoute);

    }
    @Test
    public void N_CallE1() throws InterruptedException {
        if(E1.equals("null")){
           return;
        }
        Reporter.infoExec("E1外线呼出1000打902000");
        if(Single_Device_Test){
            pageDeskTop.settings.click();
            settings.callControl_panel.click();
        }else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
        }
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"outrouter1",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        add_outbound_routes.mt_RemoveAllFromSelected.click();
        listSelect(add_outbound_routes.list_Trunk,trunkList,E1);
        add_outbound_routes.save.click();
        ys_apply();
        pjsip.Pj_Make_Call_Auto_Answer(1000,"902000", DEVICE_IP_LAN,true);
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","902000","Answered","",E1,communication_outRoute);

    }
    @Test
    public void O_CallGsm() throws InterruptedException {
        if(GSM.equals("null")){
            return;
        }
        Reporter.infoExec("GSM外线呼出1000打903000");
        if(Single_Device_Test){
            pageDeskTop.settings.click();
            settings.callControl_panel.click();
        }else {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
        }
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"outrouter1",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        add_outbound_routes.mt_RemoveAllFromSelected.click();
        listSelect(add_outbound_routes.list_Trunk,trunkList,GSM);
        add_outbound_routes.save.click();
        ys_apply();
        pjsip.Pj_Make_Call_Auto_Answer(1000,"90"+ DEVICE_ASSIST_GSM, DEVICE_IP_LAN,true);
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","90"+ DEVICE_ASSIST_GSM,"Answered","",GSM,communication_outRoute);
    }


    @Test
    public void P_PinList() throws InterruptedException {
        Reporter.infoExec("呼出路由设置Pinlist");
        if(Single_Device_Test){
            pageDeskTop.settings.click();
            settings.callControl_panel.click();
        }
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"outrouter1",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        add_outbound_routes.mt_RemoveAllFromSelected.click();
        listSelect(add_outbound_routes.list_Trunk,trunkList,SIPTrunk);
        comboboxSelect(add_outbound_routes.Password,add_outbound_routes.Password_Pinset);
        comboboxSelect(add_outbound_routes.combobox_PinsetPassword,getDynamicData(add_outbound_routes.combobox_PinsetPassword,0));
        add_outbound_routes.save.click();
        pageDeskTop.apply.click();


    }

    @Test
    public void Q_PinListCall() throws InterruptedException, IOException {
        Reporter.infoExec("PinList外线呼出1000打903000");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"903000", DEVICE_IP_LAN,false);
        tcpSocket.connectToDevice();
        boolean showKeyWord= tcpSocket.getAsteriskInfo("pin");
        tcpSocket.closeTcpSocket();
        System.out.println("Q_PinListCall TcpSocket return: "+showKeyWord);
        pjsip.Pj_Send_Dtmf(1000,"1","2","3");
//        pjsip.Pj_Answer_Call(3000,false);
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(showKeyWord,true,"外线呼出进入pin1List");
        m_extension.checkCDR("1000 <1000>","903000","Answered","",SIPTrunk,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"123",1);
    }
    @Test
    public void R_PWD_None() throws InterruptedException {
        Reporter.infoExec("设置呼出路由无Pinlist");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        Thread.sleep(2000);
        gridClick(outboundRoutes.grid,1,outboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_outbound_routes.Password,add_outbound_routes.Password_None);
        add_outbound_routes.save.click();
        pageDeskTop.apply.click();
    }
    @Test
    public void S_PinListCall() throws InterruptedException, IOException {
        Reporter.infoExec("设置呼出路由无Pinlist 1000拨打903000");
        Thread.sleep(5000);
        pjsip.Pj_Make_Call_Auto_Answer(1000,"903000", DEVICE_IP_LAN,false);
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();

    }
    @Test
    public void T_RoutePermission() throws InterruptedException {
        Reporter.infoExec("登录1000分机判断路由是否存在");
        logout();
        login("1000","Yeastar202");
        me.me.click();
        me.me_RoutePetmission.click();
        ys_waitingLoading(me_routePermission.grid_Mask);
        String act= String.valueOf(gridContent(me_routePermission.grid,1,me_routePermission.gridColumn_Name));
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        YsAssert.assertEquals(act,"outrouter1");


    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Reporter.infoAfterClass("关闭游览器_OutboundRoutes"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(10000);
    }
}
