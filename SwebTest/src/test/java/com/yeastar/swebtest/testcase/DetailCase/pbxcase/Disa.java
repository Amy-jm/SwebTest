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
import org.w3c.css.sac.ElementSelector;
import org.yaml.snakeyaml.events.Event;

import java.util.ArrayList;

/**
 * Created by Caroline on 2017/12/25.
 */
public class Disa extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：====== Disa ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }

    @Test
    public void A_addExtensions(){
        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_CreateAccount(1000, "Yeastar202", "UDP", UDP_PORT, 1);
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备1注册分机3001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3001, "Yeastar202", -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3001, DEVICE_ASSIST_1);

        Reporter.infoExec(" 辅助设备1注册分机3004"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3004, "Yeastar202", -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3004, DEVICE_ASSIST_1);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2000, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2001, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2002"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2002, "Yeastar202", -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2002, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备3注册分机4000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 4000, "Yeastar202", -1, DEVICE_ASSIST_3, UDP_PORT_ASSIST_3);
        pjsip.Pj_Register_Account_WithoutAssist(4000, DEVICE_ASSIST_3);

        Reporter.infoExec(" 辅助设备3注册分机4001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 4001, "Yeastar202", -1, DEVICE_ASSIST_3, UDP_PORT_ASSIST_3);
        pjsip.Pj_Register_Account_WithoutAssist(4001, DEVICE_ASSIST_3);
        closePbxMonitor();
    }
    @Test
    public void B1_addUnavil() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.trunks_panel.click();
        setPageShowNum(trunks.grid, 100);
        Reporter.infoExec(" 添加不可用的sip外线SIP3");
        m_trunks.addUnavailTrunk("SIP", add_voIP_trunk_basic.VoipTrunk, "SIP3", DEVICE_ASSIST_1, String.valueOf(UDP_PORT_ASSIST_1), DEVICE_ASSIST_1, "1", "1", "1", "Yeastar", false);
    }
    @Test
    public void B2_addRoute1() throws InterruptedException {

        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        Reporter.infoExec(" 添加sip的呼出路由Out_All"); //执行操作
        ArrayList<String> arrayex = new ArrayList<>();
        arrayex.add("all");
        ArrayList<String> arraytrunk = new ArrayList<>();
        arraytrunk.add("SIP3");
        arraytrunk.add(SPS);
        arraytrunk.add(SIPTrunk);
        if (!DEVICE_ASSIST_3.equals("null")) {
            arraytrunk.add(ACCOUNTTRUNK);
        }
        if (PRODUCT.equals(PC)) {
            arraytrunk.add(IAXTrunk);
            arraytrunk.add(SPX);
        } else if (!PRODUCT.equals(CLOUD_PBX)){
            arraytrunk.add(IAXTrunk);
            arraytrunk.add(SPX);
            if(!FXO_1.equals("null")) {
                arraytrunk.add(FXO_1);
            }
            if(!E1.equals("null")) {
                arraytrunk.add(E1);
            }
            if(!BRI_1.equals("null")) {
                arraytrunk.add(BRI_1);
            }
            if(!GSM.equals("null")) {
                arraytrunk.add(GSM);
            }
        }
            m_callcontrol.addOutboundRoute("Out_All", "93.", "2", "", arrayex, arraytrunk);
        }
    @Test
    public void B2_addRoute2() throws InterruptedException {
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        Reporter.infoExec(" 添加sip的呼出路由Out_sip"); //执行操作
        ArrayList<String> arrayex = new ArrayList<>();
        arrayex.add("all");
        ArrayList<String> arraytrunk = new ArrayList<>();
        arraytrunk.add("SIP2");
        m_callcontrol.addOutboundRoute("Out_sip", "92.", "2", "", arrayex, arraytrunk);
    }
    @Test
    public void B3_addDisa1(){
        ArrayList<String> valuelist = new ArrayList();
        settings.callFeatures_tree.click();
        ys_waitingTime(1000);
        callFeatures.more.click();
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        deletes("删除DISA",disa.grid,disa.delete,disa.delete_yes,disa.grid_Mask);
        disa.add.click();
        ys_waitingTime(8000);
        add_disa.name.setValue("DISA2_+?.-*12345678901234567890");
            valuelist.add("OutRoute1_sip");
            valuelist.add("OutRoute3_sps");
            if (!DEVICE_ASSIST_3.equals("null")) {
                valuelist.add("OutRoute9_account");
            }
            if(!PRODUCT.equals(CLOUD_PBX)){
                valuelist.add("OutRoute2_iax");
                valuelist.add("OutRoute4_spx");
            }
            if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC)){
                if(!FXO_1.equals("null")) {
                    valuelist.add("OutRoute5_fxo");}
                if(!BRI_1.equals("null")) {
                    valuelist.add("OutRoute6_bri");}
                if(!E1.equals("null")){
                    valuelist.add("OutRoute7_e1");}
                if(!GSM.equals("null")){
                    valuelist.add("OutRoute8_gsm");}
            }
        listSelect(add_disa.list,nameList,valuelist);
        add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
    }
    @Test
    public void B3_addDisa2(){
        disa.add.shouldBe(Condition.exist);
        disa.add.click();
        ys_waitingTime(1000);
        add_disa.name.setValue("DISA3");
        listSelect(add_disa.list,nameList,"Out_All","OutRoute1_sip","OutRoute3_sps","Out_sip");
        add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
    }
//    遍历Trunk通话
    @Test
    public void C1_editInRoute(){
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();

        deletes(" 删除所有呼入路由",inboundRoutes.grid,inboundRoutes.delete,inboundRoutes.delete_yes,inboundRoutes.grid_Mask);
        Reporter.infoExec(" 添加呼入路由InRoute1——呼入目的地到DISA ");
        ArrayList<String> arraytrunk10 = new ArrayList<>();
        arraytrunk10.add("all");
        m_callcontrol.addInboundRoutes("InRoute1_DISA","","",add_inbound_route.s_disa,"DISA2_+?.-*12345678901234567890",arraytrunk10);
        ys_apply();
    }
    @Test
    public void C2_bakckUp(){
        backupEnviroment(this.getClass().getName());
    }
    @Test
    public void D_checkTrunk1(){
//        SPS呼入，DISA走SIP
        Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，进入DISA，拨13001通过SIP1呼出");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999", DEVICE_ASSIST_2,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"1","3","0","0","1","#");
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
    public void D_checkTrunk2(){
//        SIP呼入，DISA走SPS
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入,进入DISA，拨31111通过SPS呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(3001,"3","1","1","1","1","#");
        ys_waitingTime(8000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","31111","Answered",SIPTrunk,SPS,communication_outRoute);
    }
    @Test
    public void D_checkTrunk3(){
//        SPX呼入，DISA走IAX
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }else {
            Reporter.infoExec(" 2001拨打88999通过SPX外线呼入，进入DISA，拨23001通过IAX1呼出");
            pjsip.Pj_Make_Call_Auto_Answer(2001, "88999", DEVICE_ASSIST_2,false);
            ys_waitingTime(2000);
            pjsip.Pj_Send_Dtmf(2001,"2","3","0","0","1","#");
            ys_waitingTime(8000);
            if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>", "23001", "Answered", SPX, IAXTrunk, communication_outRoute);
        }
    }
    @Test
    public void D_checkTrunk4(){
//        IAX呼入，DISA走SPX
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }else {
            Reporter.infoExec(" 3001拨打3100通过iax外线呼入，进入DISA，拨42000通过SPX呼出");
            pjsip.Pj_Make_Call_Auto_Answer(3001, "3100", DEVICE_ASSIST_1,false);
            ys_waitingTime(2000);
            pjsip.Pj_Send_Dtmf(3001,"4","2","0","0","0","#");
            ys_waitingTime(8000);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("3001 <3001>", "42000", "Answered", IAXTrunk,SPX, communication_outRoute);
        }
    }
    @Test
    public void D_checkTrunk5(){
//        SPS呼入，DISA走BRI
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!BRI_1.equals("null")){
            Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，进入DISA，拨66666通过E1呼出");
            pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2,false);
            ys_waitingTime(2000);
            pjsip.Pj_Send_Dtmf(2001,"6","6","6","6","6","#");
            ys_waitingTime(8000);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>", "66666", "Answered", SPS,BRI_1, communication_outRoute);
        }
    }
    @Test
    public void D_checkTrunk6(){
//        SPS呼入，DISA走E1
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!E1.equals("null")){
            Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，进入DISA，拨77777通过E1呼出");
            pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2,false);
            ys_waitingTime(2000);
            pjsip.Pj_Send_Dtmf(2001,"7","7","7","7","7","#");
            ys_waitingTime(8000);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>", "77777", "Answered", SPS,E1, communication_outRoute);
        }
    }
    @Test
    public void D_checkTrunk7(){
//        SPS呼入，DISA走PSTN
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!FXO_1.equals("null")){
            Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，进入DISA，拨52000通过E1呼出");
            pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2,false);
            ys_waitingTime(2000);
            pjsip.Pj_Send_Dtmf(2001,"5","2","0","0","0","#");
            ys_waitingTime(8000);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>", "52000", "Answered", SPS,FXO_1, communication_outRoute);}
    }
    @Test
    public void D_checkTrunk8() throws InterruptedException {
//        SPS呼入，DISA走GSM
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!GSM.equals("null")){
            Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，进入DISA，拨辅助设备的GSM号码通过GSM呼出");
            pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2,false);
            ys_waitingTime(2000);
//            将辅助设备的GSM号码使用toCharArray方法分割为单个
            char[] c=DEVICE_ASSIST_GSM.toCharArray();
            String[] b = new String[20];
            for(int i=0;i<c.length;i++){
                b[i] = Character.toString(c[i]);
            }
            pjsip.Pj_Send_Dtmf(2001,"8",b[0],b[1],b[2],b[3],b[4],b[5],b[6],b[7],b[8],b[9],b[10],"#");
//            pjsip.Pj_Send_Dtmf(2001,"8","1","8","4","5","0","0","2","3","7","6","4","#");

//            pjsip.Pj_Answer_Call(2000,200,false);  //需要加这行代码2000才会接听？前面的Auto Answer没有作用？
            ys_waitingTime(8000);
            if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>", "8"+DEVICE_ASSIST_GSM, "Answered", SPS,GSM, communication_outRoute);}
    }
    @Test
    public void D_checkTrunk9(){
//       SPS呼入，DISA走Account
        if (!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，进入DISA，拨914000通过Account呼出");
            pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2,false);
            ys_waitingTime(2000);
            pjsip.Pj_Send_Dtmf(2001,"9","1","4","0","0","0","#");
            ys_waitingTime(8000);
            if (getExtensionStatus(4000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机4000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机4000状态为TALKING，实际状态为"+getExtensionStatus(4000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>", "914000", "Answered", SPS,ACCOUNTTRUNK, communication_outRoute);}
    }
    @Test
    public void E1_editInRoute(){
        Reporter.infoExec("修改呼入路由目的地为DISA3");
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1_DISA",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSet(add_inbound_route.destination,nameList, "DISA3");
        //        保存编辑页面
        add_inbound_route.save.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        ys_apply();
    }
    @Test
    public void E2_checkPriority1(){
//        SPS呼入，DISA走SIP
        Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，进入DISA，拨13001通过SIP1呼出");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999", DEVICE_ASSIST_2,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"1","3","0","0","1","#");
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
    public void E2_checkPriority2(){
//        SIP呼入，DISA走SPS
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入,进入DISA，拨31111通过SPS呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(3001,"3","1","1","1","1","#");
        ys_waitingTime(8000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","31111","Answered",SIPTrunk,SPS,communication_outRoute);
 }
    @Test
    public void E2_checkPriority3(){
//        SIP呼入，DISA走Out_All呼出路由中第一条可用的路线——SPS
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入,进入DISA，拨93111"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(2000);
         pjsip.Pj_Send_Dtmf(3001,"9","3","1","1","1","#");
        ys_waitingTime(18000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","93111","Answered",SIPTrunk,SPS,communication_outRoute);
    }
    @Test
    public void E3_editOutRoute(){
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        Reporter.infoExec(" 编辑呼出路由：修改OutRoute1_sip前缀为92.  Strip 2位");
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute1_sip",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('patterns','92.')");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('strip','2')");
        //        保存编辑页面
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();
    }
    @Test
    public void E4_checkPriority(){
        //        SPS呼入，DISA走SIP
        Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，进入DISA，拨923001通过SIP1呼出");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999", DEVICE_ASSIST_2,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"9","2","3","0","0","1","#");
        ys_waitingTime(8000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","923001","Answered",SPS,SIPTrunk,communication_outRoute);
    }
    @Test
    public void E5_editDisa(){
        settings.callFeatures_tree.click();
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        ys_waitingLoading(disa.grid_Mask);
        ys_waitingTime(2000);
        gridClick(disa.grid,gridFindRowByColumn(disa.grid,disa.gridcolumn_Name,"DISA3",sort_ascendingOrder),disa.gridEdit);
        ys_waitingTime(1000);
        listSelect(add_disa.list,nameList,"Out_All","Out_sip","OutRoute1_sip","OutRoute3_sps");
        add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
        ys_apply();
    }
    @Test
    public void E6_checkPriority(){
        //        SPS呼入，DISA走SIP
        Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，进入DISA，拨923001通过SIP1呼出");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999", DEVICE_ASSIST_2,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"9","2","3","0","0","1","#");
        ys_waitingTime(8000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","923001","Answered",SPS,SIPTrunk2,communication_outRoute);
    }
    @Test
    public void E7_recoveryOutRoute(){
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        Reporter.infoExec(" 编辑呼出路由：修改OutRoute1_sip前缀为1.  Strip 1位");
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute1_sip",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('patterns','1.')");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('strip','1')");
        //        保存编辑页面
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();
    }
    @Test
    public void F1_editPin(){
        Reporter.infoExec(" 编辑DISA3：password改为test1");
        settings.callFeatures_tree.click();
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        ys_waitingLoading(disa.grid_Mask);
        ys_waitingTime(2000);
        gridClick(disa.grid,gridFindRowByColumn(disa.grid,disa.gridcolumn_Name,"DISA3",sort_ascendingOrder),disa.gridEdit);
        comboboxSelect(add_disa.password,add_disa.passwordType_Pinset);
        ys_waitingTime(2000);
        comboboxSet(add_disa.password_Pinset,nameList,"test1");
        add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
        ys_apply();
    }
    @Test
    public void F2_checkPin1(){
        //        SPS呼入，DISA走SIP
        Reporter.infoExec("2001拨打99999通过SPS外线呼入，进入DISA后输错Pin码三次");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"1","2","3","4","5","#");
        pjsip.Pj_Send_Dtmf(2001,"1","2","3","4","5","6","#");
        pjsip.Pj_Send_Dtmf(2001,"1","2","3","4","5","7","#");
        ys_waitingTime(2000);
        int state = getExtensionStatus(2001,HUNGUP,10);
        if(state == HUNGUP){
            Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，进入DISA后输错Pin码三次,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，进入DISA后输错Pin码三次,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打99999通过SPS外线呼入，进入DISA后输错Pin码三次,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void F2_checkPin2(){
        //        SPS呼入，DISA走SIP
        Reporter.infoExec("2001拨打99999通过SPS外线呼入，进入DISA后第三次输入正确的Pin码，再进行二次拨号");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"1","2","3","4","5","#");
        pjsip.Pj_Send_Dtmf(2001,"1","2","3","4","5","6","7","8","#");
        pjsip.Pj_Send_Dtmf(2001,"1","3","0","0","1","#");
        ys_waitingTime(8000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        m_extension.checkCDR("2001 <2001>","13001","Answered",SPS,SIPTrunk,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"12345678",1);
    }
    @Test
    public void F2_checkPin3(){
        //        SPS呼入，DISA走SIP，输入pin码时不加#号
        Reporter.infoExec("2001拨打99999通过SPS外线呼入，进入DISA后正确输入Pin码后不加#");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"1","2","3","4","5","6","7","8");
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"1","3","0","0","1","#");
        ys_waitingTime(5000);
        if (getExtensionStatus(3001, IDLE, 8) == IDLE) {
            Reporter.pass(" 分机3001状态--IDLE，进入DISA后正确输入Pin码，接着输入要拨打的号码13001——的确未到达3001");
        } else {
            Reporter.error(" 预期分机3001状态为IDLE，实际状态为"+getExtensionStatus(3001, IDLE, 8));
        }
        pjsip.Pj_Hangup_All();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        ys_waitingTime(1000);
        cdRandRecordings.maxWindows.click();
        ys_waitingTime(3000);
        String t = String.valueOf(gridContent(extensions.grid_CDR,1,2)).substring(0,5);
        Reporter.infoExec("CDR最新记录的Call to是："+ t);
        closeCDRRecord();

        if (t.equals("DISA3")){
            Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，进入DISA输入pin码后不加#,预期呼入失败,实际呼入失败");
        }else {
            Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，进入DISA输入pin码后不加#,预期呼入失败,实际呼入成功");
            YsAssert.fail(" 2001拨打99999通过SPS外线呼入，进入DISA输入pin码后不加#,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void F3_editInRoute(){
//        编辑呼入路由：编辑Out_All呼出路由的呼出密码为123
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        Reporter.infoExec("修改Out_All呼出路由的呼出密码为123");
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"Out_All",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        //        选择Password——Pin List
        comboboxSelect(add_outbound_routes.Password,add_outbound_routes.Password_Singlepin);
        add_outbound_routes.singlepin_edit.setValue("123");
        //        保存编辑页面
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();
    }
    @Test
    public void F4_checkPin4(){
        //        SPS呼入，DISA走SIP
        Reporter.infoExec("3001拨打3000通过SPS外线呼入，进入DISA后输入Pin码，再进行二次拨号,最后正确输入呼出路由的呼出密码");
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(3001,"1","2","3","4","5","6","7","8","#");
        pjsip.Pj_Send_Dtmf(3001,"9","3","1","1","1","1","#");
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(3001,"1","2","3","#");//输入呼出路由密码
        ys_waitingTime(8000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        m_extension.checkCDR("3001 <3001>","931111","Answered",SIPTrunk,SPS,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"123",1);
    }
    @Test
    public void G1_editSinglePin(){
//        改为SinglePin，新增呼出路由Out_sip
        settings.callFeatures_tree.click();
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        ys_waitingLoading(disa.grid_Mask);
        ys_waitingTime(2000);
        gridClick(disa.grid,gridFindRowByColumn(disa.grid,disa.gridcolumn_Name,"DISA3",sort_ascendingOrder),disa.gridEdit);
        comboboxSelect(add_disa.password,add_disa.passwordType_Singlepin);
        ys_waitingTime(1000);
        add_disa.password_Singlepin.setValue("1234567890123456789012345678901");
        listSelect(add_disa.list,nameList,"Out_sip","Out_All","OutRoute1_sip","OutRoute3_sps");
        add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
        ys_apply();
    }
    @Test
    public void G2_checkSiglePin(){
        //        SPS呼入，DISA走SIP
        Reporter.infoExec("2001拨打99999通过SPS外线呼入，进入DISA后输入Pin码，再进行二次拨号");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"1","2","3","4","5","6","7","8","9","0","1","2","3","4","5","6","7","8","9","0","1","2","3","4","5","6","7","8","9","0","1","#");
        pjsip.Pj_Send_Dtmf(2001,"1","3","0","0","1","#");
        ys_waitingTime(8000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        m_extension.checkCDR("2001 <2001>","13001","Answered",SPS,SIPTrunk,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"1234567890123456789012345678901",1);
    }
    @Test
    public void G3_checkNewSinglePin(){
        //        SPS呼入，DISA走SIP；保持通话时修改DISA的SinglePin，新的电话呼入时要输入新的SinglePin才可以成功通话
        Reporter.infoExec("2001拨打99999通过SPS外线呼入，进入DISA后输入Pin码，再进行二次拨号");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"1","2","3","4","5","6","7","8","9","0","1","2","3","4","5","6","7","8","9","0","1","2","3","4","5","6","7","8","9","0","1","#");
        pjsip.Pj_Send_Dtmf(2001,"1","3","0","0","1","#");
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
//        暂时不挂断电话
    }
    @Test
    public void G4_editSinglePin(){
        settings.callFeatures_tree.click();
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        ys_waitingLoading(disa.grid_Mask);
        ys_waitingTime(2000);
        gridClick(disa.grid,gridFindRowByColumn(disa.grid,disa.gridcolumn_Name,"DISA3",sort_ascendingOrder),disa.gridEdit);
        ys_waitingTime(1000);
        add_disa.password_Singlepin.setValue("123");
        add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
        ys_apply();
    }
    @Test
    public void G5_newCall(){
        //        SPS呼入，DISA走SIP；保持通话时修改DISA的SinglePin，新的电话呼入时要输入新的SinglePin才可以成功通话
        Reporter.infoExec("2002拨打99999通过SPS外线呼入，进入DISA后输入Pin码，再进行二次拨号");
        pjsip.Pj_Make_Call_Auto_Answer(2002,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2002,"1","2","3","4","5","6","7","8","9","0","1","2","3","4","5","6","7","8","9","0","1","2","3","4","5","6","7","8","9","0","1","#");
        pjsip.Pj_Send_Dtmf(2002,"1","2","3","#");
        pjsip.Pj_Send_Dtmf(2002,"9","2","3","0","0","4","#");
        ys_waitingTime(8000);
        if (getExtensionStatus(3004, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3004状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3004状态为TALKING，实际状态为"+getExtensionStatus(3004, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        m_extension.checkCDR("2002 <2002>","923004","Answered",SPS,SIPTrunk2,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"123",1);

        m_extension.checkCDR("2001 <2001>","13001","Answered",SPS,SIPTrunk,communication_outRoute,2);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"1234567890123456789012345678901",2);
    }
    @Test
    public void H1_editNone() {
        settings.callFeatures_tree.click();
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        ys_waitingLoading(disa.grid_Mask);
        ys_waitingTime(2000);
        gridClick(disa.grid, gridFindRowByColumn(disa.grid, disa.gridcolumn_Name, "DISA3", sort_ascendingOrder), disa.gridEdit);
        comboboxSelect(add_disa.password, add_disa.passwordType_None);
        ys_waitingTime(1000);
        add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
        ys_apply();
    }
   @Test
    public void H2_checkNewNone(){
        Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，进入DISA，拨13001通过SIP1呼出");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999", DEVICE_ASSIST_2,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"1","3","0","0","1","#");
       if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
           Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
       } else {
           Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
       }
//        先不挂断电话，去修改disa密码不为none，验证之后呼入的号码需要输入密码
    }
    @Test
    public void H3_editNone(){
        Reporter.infoExec(" 修改DISA3的SinglePin为135");
        settings.callFeatures_tree.click();
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        ys_waitingLoading(disa.grid_Mask);
        ys_waitingTime(2000);
        gridClick(disa.grid,gridFindRowByColumn(disa.grid,disa.gridcolumn_Name,"DISA3",sort_ascendingOrder),disa.gridEdit);
        ys_waitingTime(1000);
        comboboxSelect(add_disa.password,add_disa.passwordType_Singlepin);
        add_disa.password_Singlepin.setValue("135");
        add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
        ys_apply();
    }
    @Test
    public void H4_newCall(){
        //        SPS呼入，DISA走SIP；保持通话时修改DISA的SinglePin，新的电话呼入时要输入新的SinglePin才可以成功通话
        Reporter.infoExec("2002拨打99999通过SPS外线呼入，进入DISA后输入Pin码，再进行二次拨号");
        pjsip.Pj_Make_Call_Auto_Answer(2002,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2002,"1","3","5","#");
        pjsip.Pj_Send_Dtmf(2002,"9","2","3","0","0","4","#");
        ys_waitingTime(8000);
        if (getExtensionStatus(3004, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3004状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3004状态为TALKING，实际状态为"+getExtensionStatus(3004, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        m_extension.checkCDR("2002 <2002>","923004","Answered",SPS,SIPTrunk2,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"135",1);

        m_extension.checkCDR("2001 <2001>","13001","Answered",SPS,SIPTrunk,communication_outRoute,2);
    }
    @Test
    public void I1_checkResponse(){
        //        SPS呼入，DISA走SIP
        Reporter.infoExec("2001拨打99999通过SPS外线呼入，进入DISA超过设置的ResponseTimeout后被挂断通话");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        ys_waitingTime(20000);
        int state = getExtensionStatus(2001,HUNGUP,10);
        if(state == HUNGUP){
            Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，进入DISA后输错Pin码三次,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，进入DISA后输错Pin码三次,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打99999通过SPS外线呼入，进入DISA后输错Pin码三次,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void I2_editResponse(){
        Reporter.infoExec("修改DISA3：responseTimeout设置为20");
        settings.callFeatures_tree.click();
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        ys_waitingLoading(disa.grid_Mask);
        ys_waitingTime(2000);
        gridClick(disa.grid,gridFindRowByColumn(disa.grid,disa.gridcolumn_Name,"DISA3",sort_ascendingOrder),disa.gridEdit);
        ys_waitingTime(1000);
        add_disa.responseTimeout.setValue("20");
        add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
        ys_apply();
    }
    @Test
    public void I3_checkResponse1(){
        //        SPS呼入，DISA走SIP；超过设置的ResponseTimeout后被挂断通话
        Reporter.infoExec("2001拨打99999通过SPS外线呼入，超过设置的ResponseTimeout后被挂断通话");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        ys_waitingTime(30000);
        int state = getExtensionStatus(2001,HUNGUP,10);
        if(state == HUNGUP){
            Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，超过设置的ResponseTimeout后被挂断通话,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，超过设置的ResponseTimeout后被挂断通话,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打99999通过SPS外线呼入，超过设置的ResponseTimeout后被挂断通话,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void I3_checkResponse2(){
        //        SPS呼入，DISA走SIP
        Reporter.infoExec("2001拨打99999通过SPS外线呼入，进入DISA后输入Pin码，再进行二次拨号");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(10000);
        pjsip.Pj_Send_Dtmf(2001,"1","3","5","#");
        pjsip.Pj_Send_Dtmf(2001,"9","2","3","0","0","4","#");
        ys_waitingTime(8000);
        if (getExtensionStatus(3004, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3004状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3004状态为TALKING，实际状态为"+getExtensionStatus(3004, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        m_extension.checkCDR("2001 <2001>","923004","Answered",SPS,SIPTrunk2,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"135",1);
    }
    @Test
    public void J1_checkDigit(){
//        Digit默认5s时;SPS呼入，DISA走SIP
        Reporter.infoExec("2001拨打99999通过SPS外线呼入，进入DISA后输入Pin码，验证Digit");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        ys_waitingTime(1000);
        pjsip.Pj_Send_Dtmf(2001,"1","3","5","#");
        pjsip.Pj_Send_Dtmf(2001,"9");
        ys_waitingTime(6000);
        int state = getExtensionStatus(2001,HUNGUP,10);
        Reporter.infoExec(" 2001的分机状态为："+state+",预期是HANGUP状态");
        if(state == HUNGUP){
            Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，超过Digit时间,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，超过Digit时间,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打99999通过SPS外线呼入，超过Digit时间,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void J2_editDigit(){
        Reporter.infoExec(" 修改DISA3：responseTimeout设置为15，digitTimeout设置为10");
//        修改Digit为10s
        settings.callFeatures_tree.click();
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        ys_waitingLoading(disa.grid_Mask);
        ys_waitingTime(2000);
        gridClick(disa.grid,gridFindRowByColumn(disa.grid,disa.gridcolumn_Name,"DISA3",sort_ascendingOrder),disa.gridEdit);
        ys_waitingTime(1000);
        add_disa.responseTimeout.setValue("15");
        add_disa.dightTimeout.setValue("10");
        add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
        ys_apply();
    }
    @Test
    public void J3_checkDigit(){
        //        Digit10s时；SPS呼入，DISA走SIP
        Reporter.infoExec("2001拨打99999通过SPS外线呼入，进入DISA后输入Pin码，验证Digit");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        ys_waitingTime(1000);
        pjsip.Pj_Send_Dtmf(2001,"1","3","5","#");
        pjsip.Pj_Send_Dtmf(2001,"1");
        ys_waitingTime(2000);//Pj_Send_Dtmf里面就有3s的时间等待
        pjsip.Pj_Send_Dtmf(2001,"3");
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"0");
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"0");
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"1","#");
        ys_waitingTime(8000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","13001","Answered",SPS,SIPTrunk,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"135",1);
    }
    @Test
    public void J4_checkDigit(){
        //        Digit默认10s时;SPS呼入，DISA走SIP
        Reporter.infoExec("2001拨打99999通过SPS外线呼入，进入DISA后输入Pin码，验证Digit");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为"+getExtensionStatus(2001, TALKING, 8));
        }
        ys_waitingTime(1000);
        pjsip.Pj_Send_Dtmf(2001,"1","3","5","#");
        pjsip.Pj_Send_Dtmf(2001,"9");
        ys_waitingTime(11000);
        int state = getExtensionStatus(2001,HUNGUP,10);
        Reporter.infoExec(" 2001的分机状态为："+state+",预期是HANGUP状态");
        if(state == HUNGUP){
            Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，超过Digit时间,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打99999通过SPS外线呼入，超过Digit时间,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打99999通过SPS外线呼入，超过Digit时间,预期呼入失败,实际呼入成功");
        }
    }
    @Test
    public void K1_addDisa(){
        Reporter.infoExec("新增DISA1——用于后面的删除操作，外线选择sip和sps");
//        多新增一个Disa，用于后面的删除操作
        settings.callFeatures_tree.click();
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        disa.add.click();
        ys_waitingTime(8000);
        add_disa.name.setValue("DISA1");
        listSelect(add_disa.list,nameList,"OutRoute1_sip","OutRoute3_sps");
        add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
    }
    @Test
    public void K2_deleteOne_no(){
        Reporter.infoExec(" 删除单个DISA1——选择no"); //执行操作
//       定位要删除的那条DISA
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(disa.grid, disa.gridcolumn_Name, "DISA1", sort_ascendingOrder)));
        //还未操作前呼出路由的所有条数
        int rows = Integer.parseInt(String.valueOf(gridLineNum(disa.grid)));
        System.out.println("预期值:" + rows);
        gridClick(disa.grid, row, disa.gridDelete);
        disa.delete_no.click();
        ys_waitingLoading(disa.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(disa.grid)));
        System.out.println("实际值:" + row1);
        YsAssert.assertEquals(row1, rows, "删除DISA1-取消删除");
    }
    @Test
    public void K3_deleteOne_yes(){
        Reporter.infoExec(" 删除DISA1——选择yes"); //执行操作
        int rows = Integer.parseInt(String.valueOf(gridLineNum(disa.grid)));
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(disa.grid, disa.gridcolumn_Name, "DISA1", sort_ascendingOrder)));
        gridClick(disa.grid, row, disa.gridDelete);
        disa.delete_yes.click();
        ys_waitingLoading(disa.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(disa.grid)));
        System.out.println("实际值row2:" + row2);
        int row3 = rows - 1;
        System.out.println("期望值row3:" + row3);
        ys_apply();
        YsAssert.assertEquals(row2, row3, "删除DISA1——确定删除");
    }
    @Test
    public void K4_deletePart_no(){
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(disa.grid)));
        System.out.println("期望值row1:" + row1);
        int row2 = Integer.parseInt(String.valueOf(gridFindRowByColumn(disa.grid, disa.gridcolumn_Name, "DISA3", sort_ascendingOrder)));
//        全部勾选
        gridSeleteAll(disa.grid);
        ys_waitingTime(1000);
//        取消勾选
        gridCheck(disa.grid, row2, disa.gridcolumn_Check);
//        点击删除按钮
        disa.delete.click();
        disa.delete_no.click();
        ys_waitingLoading(disa.grid_Mask);
        int row4 = Integer.parseInt(String.valueOf(gridLineNum(disa.grid)));
        System.out.println("实际值row4:" + row4);
        YsAssert.assertEquals(row4, row1, "全部勾选，再取消某条的勾选后-取消删除");
    }
    @Test
    public void K5_deletePart_yes(){
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-确定删除"); //执行操作
        disa.delete.click();
        disa.delete_yes.click();
        ys_waitingLoading(disa.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(disa.grid)));
        System.out.println("实际row6:" + row1);
        ys_apply();
        YsAssert.assertEquals(row1, 1, "全部勾选，再取消某条的勾选后-确定删除");
    }
    @Test
    public void K6_deleteAll_no(){
        Reporter.infoExec(" 全部勾选-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(disa.grid)));
//        全部勾选
        gridSeleteAll(disa.grid);
//        点击删除按钮
        disa.delete.click();
        disa.delete_no.click();
        ys_waitingLoading(disa.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(disa.grid)));
        System.out.println("实际row2:" + row2);
        YsAssert.assertEquals(row1, row2, "全部勾选-取消删除");
    }
    @Test
    public void K7_deleteAll_yes(){
        Reporter.infoExec(" 全部勾选-确定删除"); //执行操作
        disa.delete.click();
        disa.delete_yes.click();
        ys_waitingLoading(disa.grid_Mask);
        int row = Integer.parseInt(String.valueOf(gridLineNum(disa.grid)));
        System.out.println("实际row:" + row);
        ys_apply();
        YsAssert.assertEquals(row, 0, "全部勾选-确定删除");
    }
    @Test
    public void K8_checkDestination(){
        //      sps外线测试;DISA删除后，呼入路由的目的地变为Hangup
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到hangup,预期呼入失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(2001,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 2001拨打99999通过sps外线呼入到hangup,预期呼入失败,实际呼入失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 2001拨打99999通过sps外线呼入到hangup,预期呼入失败,实际呼入成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 2001拨打99999通过sps外线呼入到hangup,预期呼入失败,实际呼入成功");
        }
        closeSetting();
    }
//    恢复测试环境
    @Test
    public void L1_recovery_deleteRoute(){
//        删除呼入路由Out_sip和Out_all;删除不可用SIP3;新增DISA1;新增InRoute1
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ys_waitingTime(3000);
        if(settings.callControl_panel.isDisplayed()){
            settings.callControl_panel.click();
        }else{
            settings.callControl_tree.click();
        }
//        settings.callControl_tree.click();

        outboundRoutes.outboundRoutes.click();
        setPageShowNum(outboundRoutes.grid, 100);
        Reporter.infoExec(" 删除单个呼出路由Out_sip、Out_ALL——选择yes"); //执行操作
//       定位要删除的那条呼出路由
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "Out_sip", sort_ascendingOrder)));
        int row2 = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "Out_All", sort_ascendingOrder)));
        gridCheck(outboundRoutes.grid, row, outboundRoutes.gridcolumn_Check);
        gridCheck(outboundRoutes.grid, row2, outboundRoutes.gridcolumn_Check);
        outboundRoutes.delete.click();
        outboundRoutes.delete_yes.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
    }
    @Test
    public void L2_recovery_deleteTrunk(){


        settings.trunks_tree.click();
        setPageShowNum(trunks.grid,100);
        Reporter.infoExec(" 删除SIP3——选择yes"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(trunks.grid,trunks.gridcolumn_TrunkName,"SIP3",sort_descendingOrder)));
        gridClick(trunks.grid,row,trunks.gridDelete);
        trunks.delete_yes.click();
        ys_waitingLoading(trunks.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(trunks.grid)));
        System.out.println("实际值row2:"+row2);
        int row3=row-1;
        System.out.println("期望值row3:"+row3);
        YsAssert.assertEquals(row2,row3,"删除SIP3——确定删除");
    }
    @Test
    public void L3_addDisa(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ys_waitingTime(3000);
        if(settings.callControl_panel.isDisplayed()){
            settings.callControl_panel.click();
        }else{
            settings.callControl_tree.click();
        }
        settings.callFeatures_tree.click();
        callFeatures.more.click();
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        deletes("删除所有DISA",disa.grid,disa.delete,disa.delete_yes,disa.grid_Mask);
        disa.add.click();
        ys_waitingTime(8000);
        add_disa.name.setValue("DISA1");
        listSelect(add_disa.list,nameList,"OutRoute1_sip","OutRoute3_sps");
        add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
        String actName = String.valueOf(gridContent(disa.grid,gridFindRowByColumn(disa.grid,disa.gridcolumn_Name,"DISA1",sort_ascendingOrder),disa.gridcolumn_Name));
        YsAssert.assertEquals(actName,"DISA1");
    }
    @Test
    public void L4_addInRoute(){
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        deletes(" 删除所有呼入路由",inboundRoutes.grid,inboundRoutes.delete,inboundRoutes.delete_yes,inboundRoutes.grid_Mask);
        Reporter.infoExec(" 添加呼入路由InRoute1"); //执行操作
        ArrayList<String> arraytrunk1 = new ArrayList<>();
        arraytrunk1.add("all");
        m_callcontrol.addInboundRoutes("InRoute1","","",add_inbound_route.s_extensin,"1000",arraytrunk1);
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
        Reporter.infoAfterClass("执行完毕：======  DISA  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);
    }
}
