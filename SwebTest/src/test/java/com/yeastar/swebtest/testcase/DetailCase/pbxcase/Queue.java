package com.yeastar.swebtest.testcase.DetailCase.pbxcase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import jxl.read.biff.BiffException;
import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.file.ExcelUnit;
import com.yeastar.swebtest.tools.reporter.Reporter;
import org.testng.annotations.*;

import static com.codeborne.selenide.Selenide.screenshot;
import static com.codeborne.selenide.Selenide.sleep;


/**
 * Created by AutoTest on 2017/12/6.
 */
public class Queue extends SwebDriver{
    @BeforeClass
    public void BeforeClass() {

        Reporter.infoBeforeClass("开始执行：=======  Queue  ======="); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
//          mySettings.close.click();
        }
        m_extension.showCDRClounm();


    }

    @Test
    public void A1_initBeforeTest(){
        //初始化beforetest
//        resetoreBeforetest("BeforeTest_Local.bak");
    }

    @Test
    public void A3_Init()  {
         pageDeskTop.taskBar_Main.click();
         pageDeskTop.settingShortcut.click();
         settings.callFeatures_panel.click();
         if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && LOGIN_ADMIN.equals("yes") && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
             ys_waitingMask();
         }else{
             ys_waitingTime(5000);
         }
         queue.queue.click();
         deletes(" 删除所有Queue",queue.grid,queue.delete,queue.delete_yes,queue.grid_Mask);
         ys_apply();

    }

    @DataProvider(name="add")
    public Object[][] Numbers() throws BiffException, IOException{
        ExcelUnit e=new ExcelUnit("database", "queue");
        return e.getExcelData();
    }

    @Test(dataProvider="add")
    public void A4_AddQueues(HashMap<String, String> data)  {
        Reporter.infoExec("添加队列" + data.get("Name") + "：" + data.get("Number"));
        queue.add.shouldBe(Condition.exist);
        queue.add.click();
        ys_waitingMask();
        add_queue_basic.number.clear();
        add_queue_basic.number.setValue(data.get("Number"));
        add_queue_basic.name.clear();
        add_queue_basic.name.setValue(data.get("Name"));
        add_queue_basic.password.setValue(data.get("Password"));
        comboboxSelect(add_queue_basic.ringStrategy,data.get("RingStrategy"));
        comboboxSelect(add_queue_basic.failoverDestination,data.get("FailoverDes1"));
        ys_waitingTime(1000);
        if(!data.get("FailoverDes2").equals("")){
            if(data.get("FailoverDes1").equals("e") || data.get("FailoverDes1").equals("v")){
                comboboxSet(add_queue_basic.failoverDestinationdst,extensionList,data.get("FailoverDes2"));
            }else{
                comboboxSet(add_queue_basic.failoverDestinationdst,"name",data.get("FailoverDes2"));
            }
        }
        if(!data.get("StaticAgents").equals("")){
            ArrayList<String> memberList = new ArrayList<>();
            String[] List1 = data.get("StaticAgents").split(",");
            for(int i=0;i< List1.length;i++){
                System.out.println(String.valueOf(List1[i]));
                memberList.add(String.valueOf(List1[i]));
            }
            listSelect(add_queue_basic.list_AddQueue,extensionList,memberList);
        }
        add_queue_basic.agentTimeout.setValue(data.get("AgentTimeout"));
        comboboxSelect(add_queue_basic.agentAnnouncement,data.get("AgentAnnouncement"));
        add_queue_basic.retry.setValue(data.get("Retry"));
        add_queue_basic.wrap_upTime.setValue(data.get("WrapupTime"));
        add_queue_callerExperienceSettings.callerExperienceSettings.click();
        ys_waitingTime(1000);
        comboboxSelect(add_queue_callerExperienceSettings.musicOnHold,data.get("MusicOnHold"));
        add_queue_callerExperienceSettings.callerMaxWaitTime.setValue(data.get("MaxWaitTime"));
        setCheckBox(add_queue_callerExperienceSettings.leaveWhenEmpty,Boolean.valueOf(data.get("LeaveWhenEmpty")));
        setCheckBox(add_queue_callerExperienceSettings.joinEmpty,Boolean.valueOf(data.get("JoinEmpty")));
        comboboxSelect(add_queue_callerExperienceSettings.joinAnnouncement,data.get("JoinAnnouncement"));
        comboboxSetbyValue(add_queue_callerExperienceSettings.agentidAnnouncement,nameList,data.get("AgentID"));
        comboboxSetbyValue(add_queue_callerExperienceSettings.satisfactionSurveyPrompt,nameList,data.get("Satisfaction"));
        setCheckBox(add_queue_callerExperienceSettings.announcePosition,Boolean.valueOf(data.get("Position")));
        setCheckBox(add_queue_callerExperienceSettings.announceHoldTime,Boolean.valueOf(data.get("HoldTime")));
        comboboxSelect(add_queue_callerExperienceSettings.frequency,data.get("Frequency"));
        comboboxSelect(add_queue_callerExperienceSettings.prompt,data.get("Prompt"));
        comboboxSelect(add_queue_callerExperienceSettings.frequencys,data.get("PeriodicFrequency"));
        comboboxSelect(add_queue_callerExperienceSettings.key,data.get("Key"));
        comboboxSelect(add_queue_callerExperienceSettings.keydest,data.get("KeyDes1"));
        if(!data.get("KeyDes2").equals("")){
            if(data.get("KeyDes1").equals("e") || data.get("KeyDes1").equals("v")){
                comboboxSet(add_queue_callerExperienceSettings.keydest2,extensionList,data.get("KeyDes2"));
            }else{
                comboboxSet(add_queue_callerExperienceSettings.keydest2,"name",data.get("KeyDes2"));
            }
        }
        add_queue_callerExperienceSettings.save.click();
        ys_waitingTime(1000);
    }

    @Test
    public void B1_AddInbound()  {
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        inboundRoutes.add.shouldBe(Condition.exist);
        deletes(" 删除所有呼入路由",inboundRoutes.grid,inboundRoutes.delete,inboundRoutes.delete_yes,inboundRoutes.grid_Mask);
    }

    @Test(dataProvider = "add")
    public void B2_AddInbound(HashMap<String, String> data)  {
        Reporter.infoExec("添加呼入路由："+data.get("Name")+"DID："+data.get("DID")+"，Destination："+data.get("Name"));
        ArrayList<String> memberList = new ArrayList<>();
        memberList.add("all");
        m_callcontrol.addInboundRoutes(data.get("Name"),data.get("DID"),"",s_queue,data.get("Name"),memberList);
    }

    @Test
    public void B3_Apply()  {
        ys_apply();
    }


    //    通话测试
    @Test
    public void B5_Register() {
        pjsip.Pj_Init();
        Reporter.infoExec("注册分机1000/1100/1101/1102/1103/1105/3001/2000/2001");
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(1100,EXTENSION_PASSWORD,"UDP",UDP_PORT,2);
        pjsip.Pj_CreateAccount(1101,EXTENSION_PASSWORD,"UDP",UDP_PORT,3);
        pjsip.Pj_CreateAccount(1102,EXTENSION_PASSWORD,"UDP",UDP_PORT,4);
        pjsip.Pj_CreateAccount(1103,EXTENSION_PASSWORD,"UDP",UDP_PORT,5);
        pjsip.Pj_CreateAccount(1105,EXTENSION_PASSWORD,"UDP",UDP_PORT,7);
        pjsip.Pj_CreateAccount(3001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1100,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1102,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1103,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1105,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2);
    }
//    各种外线呼入到队列
    @Test
    public void C1_sps_6701()  {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入，预期呼入到队列6701，分机1000、1100、1101、1105、1103同时响铃，1100接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100,RING,1),RING,"预期1100会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1101,RING,1),RING,"预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105,RING,1),RING,"预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103,RING,1),RING,"预期1103会同时Ring");
        pjsip.Pj_Answer_Call(1100,true);
        ys_waitingTime(15000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <6701(1100)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void C2_sip_6701()  {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入，预期呼入到队列6701，分机1000、1100、1101、1105、1103同时响铃，1000接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100,RING,1),RING,"预期1100会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1101,RING,1),RING,"预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105,RING,1),RING,"预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103,RING,1),RING,"预期1103会同时Ring");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(15000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000 <6701(1000)>","Answered",SIPTrunk,"",communication_inbound);
    }

    @Test
    public void C3_iax_6701()  {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 3001拨打3100通过iax外线呼入，预期呼入到队列6701，分机1000、1100、1101、1105、1103同时响铃，1103接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3100",DEVICE_ASSIST_1,false);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100,RING,1),RING,"预期1100会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1101,RING,1),RING,"预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105,RING,1),RING,"预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103,RING,1),RING,"预期1103会同时Ring");
        pjsip.Pj_Answer_Call(1103,true);
        ys_waitingTime(15000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","xlq <6701(1103)>","Answered",IAXTrunk,"",communication_inbound);
    }

    @Test
    public void C4_spx_6701()  {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 2000拨打88888通过spx外线呼入，预期呼入到队列6701，分机1000、1100、1101、1105、1103同时响铃，1105接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"88888",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100,RING,1),RING,"预期1100会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1101,RING,1),RING,"预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105,RING,1),RING,"预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103,RING,1),RING,"预期1103会同时Ring");
        pjsip.Pj_Answer_Call(1105,true);
        ys_waitingTime(15000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1105 <6701(1105)>","Answered",SPX,"",communication_inbound);
    }

    @Test
    public void C5_fxo_6701()  {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(FXO_1.equals("null") || FXO_1.equals("")){
            return;
        }
        Reporter.infoExec(" 2000拨打2010通过pstn外线呼入，预期呼入到队列6701，分机1000、1100、1101、1105、1103同时响铃，1000接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"2010",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100,RING,1),RING,"预期1100会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1101,RING,1),RING,"预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105,RING,1),RING,"预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103,RING,1),RING,"预期1103会同时Ring");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(15000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1000 <6701(1000)>","Answered",FXO_1,"",communication_inbound);
    }

    @Test
    public void C6_bri_6701()  {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(BRI_1.equals("null") || BRI_1.equals("")){
            return;
        }
        Reporter.infoExec(" 2000拨打66666通过bri外线呼入，预期呼入到队列6701，分机1000、1100、1101、1105、1103同时响铃，1103接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"66666",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100,RING,1),RING,"预期1100会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1101,RING,1),RING,"预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105,RING,1),RING,"预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103,RING,1),RING,"预期1103会同时Ring");
        pjsip.Pj_Answer_Call(1103,true);
        ys_waitingTime(15000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","xlq <6701(1103)>","Answered",BRI_1,"",communication_inbound);
    }

    @Test
    public void C7_e1_6701()  {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(E1.equals("null") || E1.equals("")){
            return;
        }
        Reporter.infoExec(" 2000拨打77777通过e1外线呼入，预期呼入到队列6701，分机1000、1100、1101、1105、1103同时响铃，1100接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"77777",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100,RING,1),RING,"预期1100会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1101,RING,1),RING,"预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105,RING,1),RING,"预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103,RING,1),RING,"预期1103会同时Ring");
        pjsip.Pj_Answer_Call(1100,true);
        ys_waitingTime(15000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <6701(1100)>","Answered",E1,"",communication_inbound);
    }

    @Test
    public void C8_gsm_6701()  {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(GSM.equals("null") || GSM.equals("")){
            return;
        }
        Reporter.infoExec(" 2000拨打被测设备的GSM号码呼入，预期到队列6701，分机1000、1100、1101、1105、1103同时响铃，1101接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,DEVICE_TEST_GSM,DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100,RING,1),RING,"预期1100会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1101,RING,1),RING,"预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105,RING,1),RING,"预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103,RING,1),RING,"预期1103会同时Ring");
        pjsip.Pj_Answer_Call(1100,true);
        ys_waitingTime(15000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR(DEVICE_ASSIST_GSM+" <"+DEVICE_ASSIST_GSM+">","1100 <6701(1100)>","Answered",GSM,"",communication_inbound);
    }

//    Failover 到各种目的地
    @Test
    public void D1_fail_hangup()  {
        Reporter.infoExec(" 2000拨打995503301通过sps外线呼入到队列6702，预期到Failover：Hangup"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503301",DEVICE_ASSIST_2,false);
        ys_waitingTime(10000);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,20),HUNGUP,"预期2000会HangUp");
    }

    @Test
    public void D2_fail_extension()  {
        Reporter.infoExec(" 2000拨打995503302通过sps外线呼入到队列6703，预期到Failover：分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503302",DEVICE_ASSIST_2,false);
        ys_waitingTime(10000);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1000 <6703(1000)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void D3_fail_voicemail1()  {

        Reporter.infoExec(" 2000拨打995503303通过sps外线呼入到队列6704，预期到Failover：分机1105的语音留言"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503303",DEVICE_ASSIST_2,false);
        ys_waitingTime(10000);
        ys_waitingTime(30000);
        screenshot("D3_fail_voicemail1 ing");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","6704(1105)","Voicemail",SPS,"",communication_inbound);
        screenshot("D3_fail_voicemail1 end");
    }

//    查看语音留言
    @Test
    public void D3_fail_voicemail2_check()  {
        Reporter.infoExec(" 分机1105登录，查看存在2000留下的语音留言"); //执行操作
        logout();
        if(PRODUCT.equals(CLOUD_PBX)) {
            login("autotest@yeastar.com", EXTENSION_PASSWORD);
        }else{
            login("1105",EXTENSION_PASSWORD);
        }
        me.taskBar_Main.click();
        me.mesettingShortcut.click();
        me.me_Voicemail.click();
        ys_waitingLoading(me_voicemail.grid_Mask);
        setPageShowNum(me_voicemail.grid,100);
        ys_waitingLoading(me_voicemail.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(me_voicemail.grid))) !=0) {
            YsAssert.assertEquals((gridContent(me_voicemail.grid, Integer.parseInt(String.valueOf(gridLineNum(me_voicemail.grid))), me_voicemail.gridColumn_Callerid)),
                    "2000(2000)", "语音留言检查:预期第"+String.valueOf(gridLineNum(me_voicemail.grid))+"行的CallerID为2000(2000)");
        }else{
            screenshot("语音留言检查:预期最后一行CallerID失败");
            YsAssert.fail("语音留言检查:预期第"+String.valueOf(gridLineNum(me_voicemail.grid))+"行的CallerID为2000(2000)");
        }
    }

    @Test
    public void D3_fail_voicemail3_login(){
        sleep(10000);
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
//            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }

    @Test
    public void D4_fail_ivr()  {
        Reporter.infoExec(" 2000拨打995503304通过sps外线呼入到队列6705，预期到Failover：IVR1，按1到分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503304",DEVICE_ASSIST_2,false);
        ys_waitingTime(15000);
        pjsip.Pj_Send_Dtmf(2000,"1");
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1000 <6500(1000)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void D5_fail_ringgroup()  {
        Reporter.infoExec(" 2000拨打995503305通过sps外线呼入到队列6706，预期到Failover：RingGroup，所有成员1000、1100、1105同时响铃，1100接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503305",DEVICE_ASSIST_2,false);
        ys_waitingTime(10000);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100,RING,1),RING,"预期1100会Ring");
        ysAssertWithHangup(getExtensionStatus(1105,RING,1),RING,"预期1105会Ring");
        pjsip.Pj_Answer_Call(1100,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <6200(1100)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void D6_fail_queue()  {
        Reporter.infoExec(" 2000拨打995503306通过sps外线呼入到队列6707，预期到Failover：Queue6701，所有成员都同时响铃，1103接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503306",DEVICE_ASSIST_2,false);
        ys_waitingTime(10000);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100,RING,1),RING,"预期1100会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1101,RING,1),RING,"预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105,RING,1),RING,"预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103,RING,1),RING,"预期1103会同时Ring");
        pjsip.Pj_Answer_Call(1103,true);
        ys_waitingTime(20000);
        pjsip.Pj_Hangup_All();
        ys_waitingTime(5000);
        m_extension.checkCDR("2000 <2000>","xlq <6701(1103)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void D7_fail_conference()  {
        Reporter.infoExec(" 2000拨打995503307通过sps外线呼入到队列6708，预期Failover：Conference1"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503307",DEVICE_ASSIST_2,false);
        ys_waitingTime(10000);
        ys_waitingTime(20000);
        ysAssertWithHangup(getExtensionStatus(2000,TALKING,20),TALKING,"预期2000会Talking");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","6708(6400)","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void D8_fail_DialbyName()  {
        Reporter.infoExec(" 2000拨打995503308通过sps外线呼入到队列6709，预期Failover：DialByName"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503308",DEVICE_ASSIST_2,false);
        ys_waitingTime(15000);
        pjsip.Pj_Send_Dtmf(2000,"9","5","7");
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2000,"1");
        ysAssertWithHangup(getExtensionStatus(1103,RING,10),RING,"预期1103为Ring状态");
        pjsip.Pj_Answer_Call(1103,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","xlq <6709(1103)>","Answered",SPS,"",communication_inbound);
    }

//    Key值到不同目的地
    @Test
    public void E1_key0_hangup()  {
        Reporter.infoExec(" 2000拨打999999通过sps外线呼入到队列6701，按0，预期被挂断通话"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"999999",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2000,"0");
        ysAssertWithHangup(getExtensionStatus(2000,HUNGUP,20),HUNGUP,"预期2000会HangUp");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","6701","No Answer",SPS,"",communication_inbound);
    }

    @Test
    public void E2_key1_extension()  {
        Reporter.infoExec(" 2000拨打995503302通过sps外线呼入到队列6703，按1，预期分机1000响铃，接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503302",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2000,"1");
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1000 <6703(1000)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void E3_key2_voicemail1()  {
        Reporter.infoExec(" 2001拨打995503303通过sps外线呼入到队列6704，按2，预期到分机1105的语音留言"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"995503303",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2001,"2");
        ys_waitingTime(30000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","6704(1105)","Voicemail",SPS,"",communication_inbound);
    }

//    查看语音留言
    @Test
    public void E3_key2_voicemail2_check()  {
        Reporter.infoExec(" 分机1105登录，查看存在2001留下的语音留言"); //执行操作
        logout();
        if (PRODUCT.equals(CLOUD_PBX)) {
            login("autotest@yeastar.com", EXTENSION_PASSWORD);
        } else {
            login("1105", EXTENSION_PASSWORD);
        }
        me.taskBar_Main.click();
        me.mesettingShortcut.click();
        me.me_Voicemail.click();
        ys_waitingLoading(me_voicemail.grid_Mask);
        setPageShowNum(me_voicemail.grid,100);
        ys_waitingLoading(me_voicemail.grid_Mask);
        ys_waitingTime(5000);
        screenshot("E3_key2_voicemail2_check 检查语音留言");
        if (Integer.parseInt(String.valueOf(gridLineNum(me_voicemail.grid))) != 0) {
            YsAssert.assertEquals((gridContent(me_voicemail.grid, Integer.parseInt(String.valueOf(gridLineNum(me_voicemail.grid))), me_voicemail.gridColumn_Callerid)),
                    "2001(2001)", "语音留言检查:预期第"+Integer.parseInt(String.valueOf(gridLineNum(me_voicemail.grid)))+"行的CallerID为2001(2001)");
        } else {
            YsAssert.fail("语音留言检查:预期第"+Integer.parseInt(String.valueOf(gridLineNum(me_voicemail.grid)))+"行的CallerID为2001(2001)");
        }
    }

    @Test
    public void E3_key2_voicemail3_login(){
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
//            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }

    @Test
    public void E4_key3_ivr()  {
        Reporter.infoExec(" 2000拨打995503304通过sps外线呼入到队列6705，按3，预期到IVR1，按1到分机1000，接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503304",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2000,"3");
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2000,"1");
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1000 <6500(1000)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void E5_key4_ringgroup()  {
        Reporter.infoExec(" 2000拨打995503305通过sps外线呼入到队列6706，按4，预期到RingGroup1，分机1000、1100、1105同时响铃，1100接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503305",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2000,"4");
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100,RING,1),RING,"预期1100会Ring");
        ysAssertWithHangup(getExtensionStatus(1105,RING,1),RING,"预期1105会Ring");
        pjsip.Pj_Answer_Call(1100,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <6200(1100)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void E6_key5_queue()  {
        Reporter.infoExec(" 2000拨打995503306通过sps外线呼入到队列6707，按5，预期到Queue：a，所有成员同时响铃，分机1101接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503306",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2000,"5");
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100,RING,1),RING,"预期1100会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1101,RING,1),RING,"预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105,RING,1),RING,"预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103,RING,1),RING,"预期1103会同时Ring");
        pjsip.Pj_Answer_Call(1101,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        ys_waitingTime(5000);
        m_extension.checkCDR("2000 <2000>","1101 <6701(1101)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void E7_key6_conference()  {
        Reporter.infoExec(" 2000拨打995503307通过sps外线呼入到队列6708，按6，预期到Conference1"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503307",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2000,"6");
        ys_waitingTime(20000);
        ysAssertWithHangup(getExtensionStatus(2000,TALKING,1),TALKING,"预期2000为Talking");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","6708(6400)","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void E8_key7_DialbyName()  {
        Reporter.infoExec(" 2000拨打995503308通过sps外线呼入到队列6709，按7，预期到DialbyName，按957，按1确认，分机1103接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503308",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2000,"7");
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2000,"9","5","7");
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2000,"1");
        ysAssertWithHangup(getExtensionStatus(1103,RING,10),RING,"预期1103为Ring状态");
        pjsip.Pj_Answer_Call(1103,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","xlq <6709(1103)>","Answered",SPS,"",communication_inbound);
    }

//    动态坐席
    @Test
    public void F1_dynamic_login6701()  {
        Reporter.infoExec(" 1102拨打6701*加入队列6701，密码：空"); //执行操作
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_Make_Call_Auto_Answer(1102, "6701*", DEVICE_IP_LAN);
        boolean showKeyWord2 = tcpSocket.getAsteriskInfo("Playback(agent-loginok)");
        System.out.println("Q_QueueLoginOk TcpSocket return: " + showKeyWord2);
        YsAssert.assertEquals(showKeyWord2, true, "动态坐席1102");
        tcpSocket.closeTcpSocket();
        ys_waitingTime(10000);
    }

    @Test
    public void F2_dynamic_call6701()  {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到队列6701，预期所有坐席同时响铃，1102接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(5000);
//        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
//        YsAssert.assertEquals(getExtensionStatus(1100,RING,1),RING,"预期1100会同时Ring");
//        YsAssert.assertEquals(getExtensionStatus(1101,RING,1),RING,"预期1101会同时Ring");
//        YsAssert.assertEquals(getExtensionStatus(1105,RING,1),RING,"预期1105会同时Ring");
//        YsAssert.assertEquals(getExtensionStatus(1103,RING,1),RING,"预期1103会同时Ring");
//        YsAssert.assertEquals(getExtensionStatus(1102,RING,1),RING,"预期1102会同时Ring");
        int s1000 = getExtensionStatus(1000,RING,20);
        int s1100 = getExtensionStatus(1100,RING,1);
        int s1101 = getExtensionStatus(1101,RING,1);
        int s1105 = getExtensionStatus(1105,RING,1);
        int s1103 = getExtensionStatus(1103,RING,1);
        int s1102 = getExtensionStatus(1102,RING,1);
        Reporter.infoExec("F2_dynamic_call6701 分机通话状态 预期1000 1100 1101 1102 1103 1105会同时Ring "+s1000+s1100+s1101+s1102+s1103+s1105);
        pjsip.Pj_Answer_Call(1102,true);
        ys_waitingTime(20000);
        pjsip.Pj_Hangup_All();
        ys_waitingTime(5000);
        m_extension.checkCDR("3001 <3001>","1102 <6701(1102)>","Answered",SIPTrunk,"",communication_inbound);
    }

    @Test
    public void F3_dynamic_logoff6701()  {
        Reporter.infoExec(" 1102拨打6701**退出队列6701"); //执行操作
        tcpSocket.connectToDevice(40000);
        pjsip.Pj_Make_Call_Auto_Answer(1102,"6701**",DEVICE_IP_LAN);
        boolean showKeyWord= tcpSocket.getAsteriskInfo("agent-loggedoff");
        System.out.println("Q_QueueLoginOff TcpSocket return: "+showKeyWord);
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(showKeyWord,true,"动态坐席1102退出队列6701");
    }

    @Test
    public void F4_dynamic_call6701()  {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到队列6701，预期1102不会响铃，其它坐席同时响铃，1103接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
//        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
//        YsAssert.assertEquals(getExtensionStatus(1100,RING,1),RING,"预期1100会同时Ring");
//        YsAssert.assertEquals(getExtensionStatus(1101,RING,1),RING,"预期1101会同时Ring");
//        YsAssert.assertEquals(getExtensionStatus(1105,RING,1),RING,"预期1105会同时Ring");
//        YsAssert.assertEquals(getExtensionStatus(1103,RING,1),RING,"预期1103会同时Ring");
//        YsAssert.assertEquals(getExtensionStatus(1102,RING,1),HUNGUP,"预期1102不会同时Ring");
        int s1000 = getExtensionStatus(1000,RING,20);
        int s1100 = getExtensionStatus(1100,RING,1);
        int s1101 = getExtensionStatus(1101,RING,1);
        int s1105 = getExtensionStatus(1105,RING,1);
        int s1103 = getExtensionStatus(1103,RING,1);
        int s1102 = getExtensionStatus(1102,HUNGUP,1);
        Reporter.infoExec("F4_dynamic_call6701 分机通话状态 预期1000 1100 1101 1103 1105会同时Ring "+s1000+s1100+s1101+s1103+s1105);
        Reporter.infoExec("F4_dynamic_call6701 分机通话状态 预期1102不会同时Ring "+s1102);
        pjsip.Pj_Answer_Call(1103,true);
        ys_waitingTime(20000);
        pjsip.Pj_Hangup_All();
        tcpSocket.closeTcpSocket();
        m_extension.checkCDR("3001 <3001>","xlq <6701(1103)>","Answered",SIPTrunk,"",communication_inbound);
    }

    @Test
    public void F5_dynamic_login6710()  {
        Reporter.infoExec(" 1103拨打6710*加入队列6710，密码123"); //执行操作
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_Make_Call_Auto_Answer(1103,"6710*",DEVICE_IP_LAN);
        boolean showKeyWord= tcpSocket.getAsteriskInfo("macro-queue-login");
        System.out.println("Q_QueueLogin6710 TcpSocket return: "+showKeyWord);
        if (showKeyWord){
            ys_waitingTime(2000);
            pjsip.Pj_Send_Dtmf(1103,"1","2","3","#");
            boolean showKeyWord2= tcpSocket.getAsteriskInfo("Playback(agent-loginok)");
            System.out.println("Q_QueueLoginOk TcpSocket return: "+showKeyWord2);
            tcpSocket.closeTcpSocket();
            YsAssert.assertEquals(showKeyWord2,true,"动态坐席1103输入密码123");
        }else {
            tcpSocket.closeTcpSocket();
            YsAssert.fail("动态坐席1103加入队列6710失败");
        }

    }

    @Test
    public void F6_dynamic_login6711()  {
        Reporter.infoExec(" 1102拨打6711*加入队列6711，密码123"); //执行操作
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_Make_Call_Auto_Answer(1102,"6711*",DEVICE_IP_LAN);
        boolean showKeyWord= tcpSocket.getAsteriskInfo("macro-queue-login");
        System.out.println("Q_QueueLogin6711 TcpSocket return: "+showKeyWord);
        if (showKeyWord){
            ys_waitingTime(2000);
            pjsip.Pj_Send_Dtmf(1102,"1","2","3","#");
            boolean showKeyWord2= tcpSocket.getAsteriskInfo("Playback(agent-loginok)");
            System.out.println("Q_QueueLoginOk TcpSocket return: "+showKeyWord2);
            tcpSocket.closeTcpSocket();
            YsAssert.assertEquals(showKeyWord2,true,"动态坐席1102输入密码123");
        }else {
            tcpSocket.closeTcpSocket();
            YsAssert.fail("动态坐席1102加入队列6711失败");
        }

    }

    @Test
    public void F7_dynamic_login6712()  {
        Reporter.infoExec(" 1101拨打6712*加入队列6712，密码1234567890"); //执行操作
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_Make_Call_Auto_Answer(1101,"6712*",DEVICE_IP_LAN);
        boolean showKeyWord= tcpSocket.getAsteriskInfo("macro-queue-login");
        System.out.println("Q_QueueLogin6712 TcpSocket return: "+showKeyWord);
        if (showKeyWord){
            ys_waitingTime(2000);
            pjsip.Pj_Send_Dtmf(1101,"1","2","3","4","5","6","7","8","9","0","#");
            boolean showKeyWord2= tcpSocket.getAsteriskInfo("Playback(agent-loginok)");
            System.out.println("Q_QueueLoginOk TcpSocket return: "+showKeyWord2);
            tcpSocket.closeTcpSocket();
            YsAssert.assertEquals(showKeyWord2,true,"动态坐席1101输入密码1234567890");
        }else {
            tcpSocket.closeTcpSocket();
            YsAssert.fail("动态坐席1101加入队列6712失败");
        }

    }
    @Test
    public void F8_dynamic_login6713()  {
        sleep(5000);
        Reporter.infoExec(" 1101拨打6713*加入队列6713，密码1--1101已经是该队列的静态坐席"); //执行操作
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_Make_Call_Auto_Answer(1101,"6713*",DEVICE_IP_LAN);
        boolean showKeyWord= tcpSocket.getAsteriskInfo("macro-queue-login");
        System.out.println("Q_QueueLogin6713 TcpSocket return: "+showKeyWord);
        if (showKeyWord){
            ys_waitingTime(3000);
            pjsip.Pj_Send_Dtmf(1101,"1","#");
            boolean showKeyWord2= tcpSocket.getAsteriskInfo("Playback(agent-loginok)");
            System.out.println("Q_QueueLoginOk TcpSocket return: "+showKeyWord2);
            tcpSocket.closeTcpSocket();
            YsAssert.assertEquals(showKeyWord2,true,"动态坐席1101输入密码1");
        }else{
            tcpSocket.closeTcpSocket();
        }
    }

    @Test
    public void F9_dynamic_login6714()  {
        Reporter.infoExec(" 1101拨打6714*加入队列6714，密码0"); //执行操作
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_Make_Call_Auto_Answer(1101,"6714*",DEVICE_IP_LAN);
        boolean showKeyWord= tcpSocket.getAsteriskInfo("macro-queue-login");
        System.out.println("Q_QueueLogin6714 TcpSocket return: "+showKeyWord);
        if (showKeyWord){
            ys_waitingTime(2000);
            pjsip.Pj_Send_Dtmf(1101,"0","#");
            boolean showKeyWord2= tcpSocket.getAsteriskInfo("Playback(agent-loginok)");
            System.out.println("Q_QueueLoginOk TcpSocket return: "+showKeyWord2);
            tcpSocket.closeTcpSocket();
            YsAssert.assertEquals(showKeyWord2,true,"动态坐席1101输入密码0");
        }else {
            tcpSocket.closeTcpSocket();
            YsAssert.fail("动态坐席1101加入队列6714失败");
        }


    }

    @Test
    public void Fa_dynamic_login6714()  {
        Reporter.infoExec(" 1103拨打6714*加入队列6714，密码0"); //执行操作
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_Make_Call_Auto_Answer(1103,"6714*",DEVICE_IP_LAN);
        boolean showKeyWord= tcpSocket.getAsteriskInfo("macro-queue-login");
        System.out.println("Q_QueueLogin6714 TcpSocket return: "+showKeyWord);
        if (showKeyWord){
            ys_waitingTime(2000);
            pjsip.Pj_Send_Dtmf(1103,"0","#");
            boolean showKeyWord2= tcpSocket.getAsteriskInfo("Playback(agent-loginok)");
            System.out.println("Q_QueueLoginOk TcpSocket return: "+showKeyWord2);
            tcpSocket.closeTcpSocket();
            YsAssert.assertEquals(showKeyWord2,true,"动态坐席1103输入密码0");
        }else {
            tcpSocket.closeTcpSocket();
            YsAssert.fail("动态坐席1103加入队列6714失败");
        }

    }

//    坐席应答提示音---AMI没有对应的打印信息---暂不测试
//    @Test
    public void G1_AgentAnnouncement()  {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入，预期呼入到队列6701，1100应答时会播放提示音AgentAnnouncement"); //执行操作
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_Make_Call_No_Answer(2000, "99999", DEVICE_ASSIST_2, false);
        ysAssertWithHangup(getExtensionStatus(1000, RING, 20), RING, "预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100, RING, 1), RING, "预期1100会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1101, RING, 1), RING, "预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105, RING, 1), RING, "预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103, RING, 1), RING, "预期1103会同时Ring");
        pjsip.Pj_Answer_Call(1100, true);
        boolean showKeyWord = tcpSocket.getAsteriskInfo("AgentAnnouncement");
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(showKeyWord, true, "1100应答时播放提示音“AgentAnnouncement”");
        m_extension.checkCDR("2000 <2000>", "1100 <6701(1100)>", "Answered", SPS, "", communication_inbound);

    }

//    进入队列提示音
    @Test
    public void G2_JoinAnnouncement()  {
        sleep(3000);
        Reporter.infoExec(" 2000拨打995503313通过sps外线呼入，预期呼入到队列6714,2000会听到进入队列的提示音Join，无坐席应答"); //执行操作
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_Make_Call_No_Answer(2000,"995503313",DEVICE_ASSIST_2,false);
        boolean showKeyWord = tcpSocket.getAsteriskInfo("Join");
        tcpSocket.closeTcpSocket();
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(showKeyWord, true, "2000呼入时播放提示音“Join”");
//        m_extension.checkCDR("2000 <2000>","6714(1101)","Answered",SPS,"",communication_inbound);
    }

//    等待音乐
    @Test
    public void G3_MusicOnHold1()  {
        sleep(3000);
        Reporter.infoExec(" 2000拨打999999通过sps外线呼入，预期呼入到队列6701，预期播放Music on Hold：Autotest"); //执行操作
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_Make_Call_No_Answer(2000,"999999",DEVICE_ASSIST_2,false);
        boolean showKeyWord = tcpSocket.getAsteriskInfo("Autotest");
        pjsip.Pj_Hangup_All();
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(showKeyWord, true, "2000呼入时播放Music on hold ：Autotest");
    }

    @Test
    public void G3_MusicOnHold2()  {
        sleep(3000);
        Reporter.infoExec(" 1103拨打6710呼入队列6710，预期播放Music on Hold：default"); //执行操作
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_Make_Call_No_Answer(1103,"6710",DEVICE_IP_LAN,false);
        boolean showKeyWord = tcpSocket.getAsteriskInfo("default");
        pjsip.Pj_Hangup_All();
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(showKeyWord, true, "1103呼入6710时播放Music on hold ：default");
    }


//    坐席播报工号提示音----AMI未打印相关信息------暂不测试
    @Test
    public void G4_AgentID()  {
        Reporter.infoExec(" 坐席播报工号提示音----AMI未打印相关信息------请手动测试"); //执行操作
    }

//    满意度调查提示音-------record/satisfaction,
    @Test
    public void G5_Satisfaction()  {
        sleep(3000);
        Reporter.infoExec(" 2000拨打99999呼入到队列6701，坐席1100应答后，1100先挂断，预期2000会听到满意度调查提示音：satisfaction"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2,false);
//        YsAssert.assertEquals(getExtensionStatus(1000, RING, 20), RING, "预期1000会Ring");
//        YsAssert.assertEquals(getExtensionStatus(1100, RING, 1), RING, "预期1100会同时Ring");
//        YsAssert.assertEquals(getExtensionStatus(1101, RING, 1), RING, "预期1101会同时Ring");
//        YsAssert.assertEquals(getExtensionStatus(1105, RING, 1), RING, "预期1105会同时Ring");
//        YsAssert.assertEquals(getExtensionStatus(1103, RING, 1), RING, "预期1103会同时Ring");
        int s1000 = getExtensionStatus(1000,RING,20);
        int s1100 = getExtensionStatus(1100,RING,1);
        int s1101 = getExtensionStatus(1101,RING,1);
        int s1105 = getExtensionStatus(1105,RING,1);
        int s1103 = getExtensionStatus(1103,RING,1);
        Reporter.infoExec("G5_Satisfaction 预期1000 1100 1101 1105 1103会同时Ring "+s1000+s1100+s1101+s1105+s1103 );
        pjsip.Pj_Answer_Call(1100,true);
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_hangupCall(1100,1100);
        boolean showKeyWord = tcpSocket.getAsteriskInfo("satisfaction");
        pjsip.Pj_Hangup_All();
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(showKeyWord, true, "坐席挂断时播放满意度调查提示音satisfaction");
    }

//    用户公告----AMI未打印相关信息------暂不测试
    @Test
    public void G6_CallerPosition()  {
        Reporter.infoExec(" 用户公告----AMI未打印相关信息---请手动测试"); //执行操作

    }

//    系统公告----AMI未打印相关信息------暂不测试
    @Test
    public void G7_PeriodicAnnouncements()  {
        Reporter.infoExec(" 系统公告----AMI未打印相关信息---请手动测试 "); //执行操作

    }

//    坐席响铃超时 & 重试间隔
    @Test
    public void G8_AgentTimeout_Retry()  {
        Reporter.infoExec(" 2000拨打999999通过sps外线呼入到队列6701，坐席响铃15s后挂断，20s左右会再次响铃,坐席循环第3次响铃时，分机1105接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"999999",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1000, RING, 20), RING, "预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100, RING, 1), RING, "预期1100会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1101, RING, 1), RING, "预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105, RING, 1), RING, "预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103, RING, 1), RING, "预期1103会同时Ring");
//        预期坐席会停止响铃
        ysAssertWithHangup(getExtensionStatus(1000, HUNGUP, 20), HUNGUP, "预期1000响铃结束");
        ysAssertWithHangup(getExtensionStatus(1100, HUNGUP, 1), HUNGUP, "预期1100响铃结束");
        ysAssertWithHangup(getExtensionStatus(1101, HUNGUP, 1), HUNGUP, "预期1101响铃结束");
        ysAssertWithHangup(getExtensionStatus(1105, HUNGUP, 1), HUNGUP, "预期1105响铃结束");
        ysAssertWithHangup(getExtensionStatus(1103, HUNGUP, 1), HUNGUP, "预期1103响铃结束");
//        预期坐席会再次响铃
        ysAssertWithHangup(getExtensionStatus(1000, RING, 25), RING, "预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100, RING, 1), RING, "预期1100会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1101, RING, 1), RING, "预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105, RING, 1), RING, "预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103, RING, 1), RING, "预期1103会同时Ring");
//      预期坐席会停止响铃
        ysAssertWithHangup(getExtensionStatus(1000, HUNGUP, 20), HUNGUP, "预期1000响铃结束");
        ysAssertWithHangup(getExtensionStatus(1100, HUNGUP, 1), HUNGUP, "预期1100响铃结束");
        ysAssertWithHangup(getExtensionStatus(1101, HUNGUP, 1), HUNGUP, "预期1101响铃结束");
        ysAssertWithHangup(getExtensionStatus(1105, HUNGUP, 1), HUNGUP, "预期1105响铃结束");
        ysAssertWithHangup(getExtensionStatus(1103, HUNGUP, 1), HUNGUP, "预期1103响铃结束");
//        预期坐席会再次响铃
        ysAssertWithHangup(getExtensionStatus(1000, RING, 30), RING, "预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100, RING, 1), RING, "预期1100会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1101, RING, 1), RING, "预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105, RING, 1), RING, "预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103, RING, 1), RING, "预期1103会同时Ring");
//        坐席1105应答
        pjsip.Pj_Answer_Call(1105,true);
        ys_waitingTime(20000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1105 <6701(1105)>","Answered",SPS,"",communication_inbound);
    }

//    休息时间
    @Test
    public void G9_warp_time0()  {
        Reporter.infoExec(" 2000拨打99999呼入到队列6701，坐席1100应答后，挂断；再次呼入队列，预期1100会马上响铃（warp time：0）"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1000, RING, 20), RING, "预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100, RING, 1), RING, "预期1100会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1101, RING, 1), RING, "预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105, RING, 1), RING, "预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103, RING, 1), RING, "预期1103会同时Ring");
        pjsip.Pj_Answer_Call(1100,true);
        ys_waitingTime(15000);
        pjsip.Pj_Hangup_All();
        ys_waitingTime(2000);
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1000, RING, 20), RING, "预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100, RING, 1), RING, "预期1100会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1101, RING, 1), RING, "预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105, RING, 1), RING, "预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103, RING, 1), RING, "预期1103会同时Ring");
        pjsip.Pj_Hangup_All();
//        m_extension.checkCDR("2000 <2000>","xlq <6701(1103)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void G9_warp_time20()  {
        Reporter.infoExec(" 2000拨打995503311呼入到队列6712，动态坐席1101应答后，挂断；再次呼入队列，预期1101不会马上响铃"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503311",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1101, RING, 20), RING, "预期1101会Ring");
        pjsip.Pj_Answer_Call(1101,true);
        ys_waitingTime(20000);
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Make_Call_No_Answer(2000,"995503311",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1101, HUNGUP, 10), HUNGUP, "预期1101不会Ring");
        ysAssertWithHangup(getExtensionStatus(1101, RING, 40), RING, "预期1101会再次Ring");
        pjsip.Pj_Answer_Call(1101,true);
        ys_waitingTime(20000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1101 <6712(1101)>","Answered",SPS,"",communication_inbound);
    }

//    响铃策略：Least Recent---应答后，对应的“last was xxx secs ago”时间从0开始计算，新来电时从下一个坐席开始振铃直到应答；
//如果分机有启用callwaiting 对应的分机在通话过程中还是会振铃
//(振铃最近被这个队列呼叫的最少的分机)
    @Test
    public void H1_leastRecent()  {
        Reporter.infoExec(" 2000拨打995503309呼入到队列6710，预期1103第1个响铃，无应答，1103会再次响铃，应答"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503309",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1103, RING, 20), RING, "预期1103会Ring");
        ysAssertWithHangup(getExtensionStatus(1103, HUNGUP, 15), HUNGUP, "预期1103结束Ring，约响铃10s");
        ysAssertWithHangup(getExtensionStatus(1103, RING, 20), RING, "预期1103会再次Ring");
        pjsip.Pj_Answer_Call(1103,true);
        ys_waitingTime(20000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","xlq <6710(1103)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void H2_leastRecent()  {
        Reporter.infoExec(" 2000再次拨打995503309呼入到队列6710，预期1105第1个响铃，接听并保持通话"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503309",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1105, RING, 20), RING, "预期1105会Ring");
        pjsip.Pj_Answer_Call(1105,true);
        ys_waitingTime(10000);
//        Reporter.infoExec(" 2001再次拨打995503309呼入到队列6710，预期1100会响铃，接听并保持通话");
//        pjsip.Pj_Make_Call_No_Answer(2001,"995503309",DEVICE_ASSIST_2,false);
//        YsAssert.assertEquals(getExtensionStatus(1100, RING, 40), RING, "预期1100会Ring");
//        pjsip.Pj_Answer_Call(1100,true);
//        ys_waitingTime(20000);
//        Reporter.infoExec(" 2001(1100)先挂断通话，1105(2000)再挂断通话");
//        pjsip.Pj_hangupCall(2001,1100);
//        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1105 <6710(1105)>","Answered",SPS,"",communication_inbound,1);
//        m_extension.checkCDR("2001 <2001>","1100 <6710(1100)>","Answered",SPS,"",communication_inbound,1,2);
    }

    @Test
    public void H3_leastRecent()  {
        Reporter.infoExec(" 2000再次拨打995503309呼入到队列6710，预期1100第1个响铃，接听并保持通话"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503309",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1100, RING, 20), RING, "预期1100会Ring");
        pjsip.Pj_Answer_Call(1100,true);
        ys_waitingTime(10000);
//        Reporter.infoExec(" 2001再次拨打995503309呼入到队列6710，预期1103会响铃，接听并保持通话");
//        pjsip.Pj_Make_Call_No_Answer(2001,"995503309",DEVICE_ASSIST_2,false);
//        YsAssert.assertEquals(getExtensionStatus(1103, RING, 20), RING, "预期1103会Ring");
//        pjsip.Pj_Answer_Call(1103,true);
//        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
//        m_extension.checkCDR("2000 <2000>","1105 <6710(1105)>","Answered",SPS,"",communication_inbound,2,1);
        m_extension.checkCDR("2000 <2000>","1100 <6710(1100)>","Answered",SPS,"",communication_inbound,1);
    }


//    响铃策略：fewestcalls---振铃这个队列完成电话数最少的分机
    @Test
    public void I1_fewestcalls()  {
        Reporter.infoExec(" 2000拨打995503310呼入到队列6711，预期1102响铃，无应答，1102会再次响铃，应答"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503310",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1102, RING, 20), RING, "预期1102会Ring");
        ysAssertWithHangup(getExtensionStatus(1102, HUNGUP, 15), HUNGUP, "预期1102结束Ring，约响铃10s");
        ysAssertWithHangup(getExtensionStatus(1102, RING, 20), RING, "预期1102会再次Ring");
        pjsip.Pj_Answer_Call(1102,true);
        ys_waitingTime(20000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1102 <6711(1102)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void I2_fewestcalls()  {
        Reporter.infoExec(" 2000拨打995503310再次呼入到队列6711，预期1105响铃，接听保持通话"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503310",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1105, RING, 20), RING, "预期1105会Ring");
        pjsip.Pj_Answer_Call(1105,true);
        ys_waitingTime(20000);
//        Reporter.infoExec(" 2001拨打995503310再次呼入到队列6711，预期1102响铃，不接");
//        pjsip.Pj_Make_Call_No_Answer(2001,"995503310",DEVICE_ASSIST_2,false);
//        YsAssert.assertEquals(getExtensionStatus(1102, RING, 20), RING, "预期1102会Ring");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1105 <6711(1105)>","Answered",SPS,"",communication_inbound,1);
//        m_extension.checkCDR("2001 <2001>","1102 <6711(1102)>","No Answer",SPS,"",communication_inbound,1,2);
    }

    @Test
    public void I3_fewestcalls()  {
        Reporter.infoExec(" 2000拨打995503310再次呼入队列6711，预期1000响铃，接听保持通话"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503310",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1000, RING, 20), RING, "预期1000会Ring");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(20000);
//        Reporter.infoExec(" 2001拨打995503310呼入到队列6711，预期1105会响铃，未接");
//        pjsip.Pj_Make_Call_No_Answer(2001,"995503310",DEVICE_ASSIST_2,false);
//        YsAssert.assertEquals(getExtensionStatus(1105, RING, 20), RING, "预期1105会Ring");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1000 <6711(1000)>","Answered",SPS,"",communication_inbound,1);
//        m_extension.checkCDR("2001 <2001>","1105 <6711(1105)>","No Answer",SPS,"",communication_inbound,1,2);
    }

//    响铃策略：random---没有规律，无法验证
    @Test
    public void J1_random()  {
        Reporter.infoExec(" 响铃策略：random没有规律，无法自动化测试--请手动验证"); //执行操作
    }

//    响铃策略：rrmemory----记住上次振铃的分机，每次从下一个分机开始振铃（与接不接听无关）
    @Test
    public void K1_rrmemory()  {
        Reporter.infoExec(" 2000拨打995503312呼入到队列6713，预期1000第1个响铃,轮到1101响铃时接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503312",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1000, RING, 20), RING, "预期1000会第1个Ring");
        ysAssertWithHangup(getExtensionStatus(1000, HUNGUP, 15), HUNGUP, "预期1000结束Ring，约响铃10s");
        ysAssertWithHangup(getExtensionStatus(1100, RING, 15), RING, "预期1100会第2个Ring");
        ysAssertWithHangup(getExtensionStatus(1100, HUNGUP, 15), HUNGUP, "预期1100结束Ring，约响铃10s");
        ysAssertWithHangup(getExtensionStatus(1101, RING, 15), RING, "预期1101会第3个Ring，应答");
        pjsip.Pj_Answer_Call(1101,true);
        ys_waitingTime(20000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1101 <6713(1101)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void K2_rrmemory()  {
        Reporter.infoExec(" 2000拨打995503312呼入到队列6713，预期1105第一个响铃"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503312",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1105, RING, 20), RING, "预期1105会Ring");
        ys_waitingTime(5000);
        pjsip.Pj_hangupCall(2000,2000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1105 <6713(1105)>","No Answer",SPS,"",communication_inbound);
    }

//    响铃策略：linear
    @Test
    public void L1_linear()  {
        Reporter.infoExec(" 2000拨打995503313通过sps外线呼入到队列6714，预期按线性响铃，1100第2次响铃时接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503313",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1000, RING, 25), RING, "预期1000第1个Ring");
        ysAssertWithHangup(getExtensionStatus(1000, HUNGUP, 15), HUNGUP, "预期1000结束响铃，约响铃10s");
        ysAssertWithHangup(getExtensionStatus(1100, RING, 25), RING, "预期1100第2个Ring");
        ysAssertWithHangup(getExtensionStatus(1100, HUNGUP, 15), HUNGUP, "预期1100结束响铃，约响铃10s");
        ysAssertWithHangup(getExtensionStatus(1105, RING, 25), RING, "预期1105第3个Ring");
        ysAssertWithHangup(getExtensionStatus(1105, HUNGUP, 15), HUNGUP, "预期1105结束响铃，约响铃10s");
        ysAssertWithHangup(getExtensionStatus(1101, RING, 25), RING, "预期1101第4个Ring");
        ysAssertWithHangup(getExtensionStatus(1101, HUNGUP, 15), HUNGUP, "预期1101结束响铃，约响铃10s");
        ysAssertWithHangup(getExtensionStatus(1103, RING, 25), RING, "预期1103第5个Ring");
        ysAssertWithHangup(getExtensionStatus(1103, HUNGUP, 15), HUNGUP, "预期1103结束响铃，约响铃10s");
        ysAssertWithHangup(getExtensionStatus(1000, RING, 25), RING, "预期1000第6个Ring");
        ysAssertWithHangup(getExtensionStatus(1000, HUNGUP, 15), HUNGUP, "预期1000结束响铃，约响铃10s");
        ysAssertWithHangup(getExtensionStatus(1100, RING, 25), RING, "预期1100第7个Ring");
        pjsip.Pj_Answer_Call(1100,true);
        ys_waitingTime(15000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <6714(1100)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void L2_linear()  {
        Reporter.infoExec(" 2000拨打995503313通过sps外线再次呼入到队列6714，预期按线性响铃，1103接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503313",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1000, RING, 25), RING, "预期1000第1个Ring");
        ysAssertWithHangup(getExtensionStatus(1000, HUNGUP, 15), HUNGUP, "预期1000结束响铃，约响铃10s");
        ysAssertWithHangup(getExtensionStatus(1100, RING, 25), RING, "预期1100第2个Ring");
        ysAssertWithHangup(getExtensionStatus(1100, HUNGUP, 15), HUNGUP, "预期1100结束响铃，约响铃10s");
        ysAssertWithHangup(getExtensionStatus(1105, RING, 25), RING, "预期1105第3个Ring");
        ysAssertWithHangup(getExtensionStatus(1105, HUNGUP, 15), HUNGUP, "预期1105结束响铃，约响铃10s");
        ysAssertWithHangup(getExtensionStatus(1101, RING, 25), RING, "预期1101第4个Ring");
        ysAssertWithHangup(getExtensionStatus(1101, HUNGUP, 15), HUNGUP, "预期1101结束响铃，约响铃10s");
        ysAssertWithHangup(getExtensionStatus(1103, RING, 25), RING, "预期1103第5个Ring");
        pjsip.Pj_Answer_Call(1103,true);
        ys_waitingTime(20000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","xlq <6714(1103)>","Answered",SPS,"",communication_inbound);
    }

//    线性响铃--多方呼入
//    @Test
    public void L3_linear()  {
        Reporter.infoExec(" 2000拨打995503313通过sps外线呼入到队列6714，预期1101第一个Ring，接听并保持通话；"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503313",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1101, RING, 20), RING, "预期1101第1个Ring");
        pjsip.Pj_Answer_Call(1101,true);
        ys_waitingTime(10000);
        Reporter.infoExec(" 2001拨打995503313通过sps外线再次呼入到队列6714，预期1103第1个Ring，1000第2个Ring，并接听");
        pjsip.Pj_Make_Call_No_Answer(2001,"995503313",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1103, RING, 25), RING, "预期1103第1个Ring");
        ysAssertWithHangup(getExtensionStatus(1103, HUNGUP, 15), HUNGUP, "预期1103结束响铃，约响铃10s");
        ys_waitingTime(10000);
        ysAssertWithHangup(getExtensionStatus(1000, RING, 5), RING, "预期1000第2个Ring");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <6714(1000)>","Answered",SPS,"",communication_inbound,1,2);
        m_extension.checkCDR("2000 <2000>","1101 <6714(1101)>","Answered",SPS,"",communication_inbound,2,1);
    }

//    多方同时呼入
    @Test
    public void M1_ringall()  {
        Reporter.infoExec(" 2000拨打999999通过sps外线呼入到队列6701,1100接听并保持通话"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"999999",DEVICE_ASSIST_2,false);
        ysAssertWithHangup(getExtensionStatus(1000, RING, 20), RING, "预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1100, RING, 1), RING, "预期1100会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1101, RING, 1), RING, "预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105, RING, 1), RING, "预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103, RING, 1), RING, "预期1103会同时Ring");
        pjsip.Pj_Answer_Call(1100,true);
        ysAssertWithHangup(getExtensionStatus(1100, TALKING, 10), TALKING, "预期1100在Talking");
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到队列6701,1103接听并保持通话"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ysAssertWithHangup(getExtensionStatus(1000, RING, 20), RING, "预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1101, RING, 1), RING, "预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105, RING, 1), RING, "预期1105会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1103, RING, 1), RING, "预期1103会同时Ring");
        pjsip.Pj_Answer_Call(1103,true);
        Reporter.infoExec(" 1102拨打6701呼入到队列6701,1105接听并保持通话"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1102,"6701",DEVICE_IP_LAN,false);
        ysAssertWithHangup(getExtensionStatus(1000, RING, 20), RING, "预期1000会Ring");
        ysAssertWithHangup(getExtensionStatus(1101, RING, 1), RING, "预期1101会同时Ring");
        ysAssertWithHangup(getExtensionStatus(1105, RING, 1), RING, "预期1105会同时Ring");
        pjsip.Pj_Answer_Call(1105,true);
        ys_waitingTime(5000);
//        主叫先挂断
        pjsip.Pj_hangupCall(2000,1100);
        ys_waitingTime(5000);
//        被叫先挂断
        pjsip.Pj_hangupCall(1102,1105);
//        同时挂断所有通话
        pjsip.Pj_Hangup_All();
//        检查cdr
        m_extension.checkCDR("2000 <2000>","1100 <6701(1100)>","Answered",SPS,"",communication_inbound,3);
        m_extension.checkCDR("3001 <3001>","xlq <6701(1103)>","Answered",SIPTrunk,"",communication_inbound,2);
        m_extension.checkCDR("1102 <1102>","1105 <6701(1105)>","Answered","","",communication_internal,1);
    }

    @Test
    public void N0_backup() {
        backupEnviroment(this.getClass().getSimpleName());
    }

    //    Leave When Empty、Join Empty
    @Test
    public void N1_leave_join_empty()  {
        Reporter.infoExec(" 编辑队列6702，MaxWaitTime：1800，LeaveWhenEmpty：启用，JoinEmpty：启用"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
        }else{
            ys_waitingTime(5000);
        }
        queue.queue.click();
        queue.add.shouldBe(Condition.exist);
        gridClick(queue.grid,gridFindRowByColumn(queue.grid,queue.gridcolumn_Number,"6702",sort_ascendingOrder),queue.gridEdit);
        ys_waitingMask();
        add_queue_callerExperienceSettings.callerExperienceSettings.click();
        ys_waitingTime(1000);
        add_queue_callerExperienceSettings.callerMaxWaitTime.setValue("1800");
        setCheckBox(add_queue_callerExperienceSettings.leaveWhenEmpty,true);
        setCheckBox(add_queue_callerExperienceSettings.joinEmpty,true);
        add_queue_callerExperienceSettings.save.click();
        ys_waitingTime(3000);
        queue.add.shouldBe(Condition.exist);
        ys_apply();
    }

    @Test
    public void N2_leave_join_empty()  {
        Reporter.infoExec(" 1102拨打6702，预期：1102被直接挂断"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1102,"6702",DEVICE_IP_LAN,false);
        YsAssert.assertEquals(getExtensionStatus(1102, HUNGUP, 20), HUNGUP, "预期1102会被直接挂断");
    }

    @Test
    public void N3_leave_join_empty()  {
        Reporter.infoExec(" 编辑队列6702，LeaveWhenEmpty：启用，JoinEmpty：不启用"); //执行操作


//        pageDeskTop.taskBar_Main.click();
//        pageDeskTop.settingShortcut.click();
//        settings.callFeatures_panel.click();
//        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
//            ys_waitingMask();
//        }else{
//            ys_waitingTime(5000);
//        }
//        queue.queue.click();


        queue.add.shouldBe(Condition.exist);
        gridClick(queue.grid,gridFindRowByColumn(queue.grid,queue.gridcolumn_Number,"6702",sort_ascendingOrder),queue.gridEdit);
        ys_waitingMask();
        add_queue_callerExperienceSettings.callerExperienceSettings.click();
        ys_waitingTime(1000);
        setCheckBox(add_queue_callerExperienceSettings.leaveWhenEmpty,true);
        setCheckBox(add_queue_callerExperienceSettings.joinEmpty,false);
        add_queue_callerExperienceSettings.save.click();
        ys_waitingTime(3000);
        queue.add.shouldBe(Condition.exist);
        ys_apply();
    }

    @Test
    public void N4_leave_join_empty()  {
        Reporter.infoExec(" 1102拨打6702，预期：1102被直接挂断"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1102,"6702",DEVICE_IP_LAN,false);
        YsAssert.assertEquals(getExtensionStatus(1102, HUNGUP, 20), HUNGUP, "预期1102会被直接挂断");
    }

    @Test
    public void N5_leave_join_empty()  {
        Reporter.infoExec(" 编辑队列6702，LeaveWhenEmpty：不启用，JoinEmpty：不启用"); //执行操作

//        pageDeskTop.taskBar_Main.click();
//        pageDeskTop.settingShortcut.click();
//        settings.callFeatures_panel.click();
//        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
//            ys_waitingMask();
//        }else{
//            ys_waitingTime(5000);
//        }
//        queue.queue.click();



        queue.add.shouldBe(Condition.exist);
        gridClick(queue.grid,gridFindRowByColumn(queue.grid,queue.gridcolumn_Number,"6702",sort_ascendingOrder),queue.gridEdit);
        ys_waitingMask();
        add_queue_callerExperienceSettings.callerExperienceSettings.click();
        ys_waitingTime(1000);
        setCheckBox(add_queue_callerExperienceSettings.leaveWhenEmpty,false);
        setCheckBox(add_queue_callerExperienceSettings.joinEmpty,false);
        add_queue_callerExperienceSettings.save.click();
        ys_waitingTime(3000);
        queue.add.shouldBe(Condition.exist);
        ys_apply();
    }

    @Test
    public void N6_leave_join_empty()  {
        Reporter.infoExec(" 1102拨打6702，预期：1102被直接挂断"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1102,"6702",DEVICE_IP_LAN,false);
        YsAssert.assertEquals(getExtensionStatus(1102, HUNGUP, 20), HUNGUP, "预期1102会被直接挂断");
    }

    @Test
    public void N7_leave_join_empty()  {
        Reporter.infoExec(" 编辑队列6702，LeaveWhenEmpty：不启用，JoinEmpty：启用"); //执行操作

//        pageDeskTop.taskBar_Main.click();
//        pageDeskTop.settingShortcut.click();
//        settings.callFeatures_panel.click();
//        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
//            ys_waitingMask();
//        }else{
//            ys_waitingTime(5000);
//        }
//        queue.queue.click();


        queue.add.shouldBe(Condition.exist);
        gridClick(queue.grid,gridFindRowByColumn(queue.grid,queue.gridcolumn_Number,"6702",sort_ascendingOrder),queue.gridEdit);
        ys_waitingMask();
        add_queue_callerExperienceSettings.callerExperienceSettings.click();
        ys_waitingTime(1000);
        setCheckBox(add_queue_callerExperienceSettings.leaveWhenEmpty,false);
        setCheckBox(add_queue_callerExperienceSettings.joinEmpty,true);
        add_queue_callerExperienceSettings.save.click();
        ys_waitingTime(3000);
        queue.add.shouldBe(Condition.exist);
        ys_apply();
    }

    @Test
    public void N8_leave_join_empty()  {
        Reporter.infoExec(" 1102拨打6702，预期：1102不会被直接挂断"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1102,"6702",DEVICE_IP_LAN,false);
        ysAssertWithHangup(getExtensionStatus(1102, HUNGUP, 20), TALKING, "预期1102不会被直接挂断");
        pjsip.Pj_Hangup_All();
    }

    @Test
    public void O1_delete()  {
        queue.queue.click();
        queue.add.shouldBe(Condition.exist);
        setPageShowNum(queue.grid, 50);
        Reporter.infoExec(" 表格删除：队列6702-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(queue.grid,queue.gridcolumn_Name,"队列6702",sort_ascendingOrder)));
        int rows=Integer.parseInt(String.valueOf(gridLineNum(queue.grid)));
        gridClick(queue.grid,row,queue.gridDelete);
        queue.delete_no.click();
        ys_waitingLoading(queue.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(queue.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：队列6702-取消删除");

        Reporter.infoExec(" 表格删除：队列6702-确定删除"); //执行操作
        gridClick(queue.grid,row,queue.gridDelete);
        queue.delete_yes.click();
        ys_waitingLoading(queue.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(queue.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：queue6201-确定删除");

        Reporter.infoExec(" 删除：Досвидания-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(queue.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(queue.grid,queue.gridcolumn_Name,"Досвидания",sort_ascendingOrder)));
        gridCheck(queue.grid,row4,queue.gridcolumn_Check);
        queue.delete.click();
        queue.delete_no.click();
        ys_waitingLoading(queue.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(queue.grid)));
        System.out.println("row5:"+row5);
        YsAssert.assertEquals(row8,row5,"删除：Досвидания-取消删除");

        Reporter.infoExec(" 删除：Досвидания-确定删除"); //执行操作
        queue.delete.click();
        queue.delete_yes.click();
        ys_waitingLoading(queue.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(queue.grid)));
        System.out.println("row6:"+row6);
        int row7=row6+1;
        System.out.println("row7:"+row7);
        YsAssert.assertEquals(row5,row7,"删除：Досвидания-确定删除");
        ys_apply();
    }

    @Test
    public void P1_recovery()  {
        Reporter.infoExec(" 恢复初始化环境"); //执行操作
        queue.queue.click();
        deletes(" 删除所有Queue",queue.grid,queue.delete,queue.delete_yes,queue.grid_Mask);
        Reporter.infoExec(" 添加Queue1：6700，选择分机1000、1100、1105，其它默认 "); //执行操作
        m_callFeature.addQueue("Queue1","6700",1000,1100,1105);
    }

    @Test
    public void P2_recovery()  {
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        inboundRoutes.add.shouldBe(Condition.exist);
        deletes(" 删除所有呼入路由",inboundRoutes.grid,inboundRoutes.delete,inboundRoutes.delete_yes,inboundRoutes.grid_Mask);
        Reporter.infoExec(" 添加呼入路由InRoute1"); //执行操作
        ArrayList<String> arraytrunk1 = new ArrayList<>();
        arraytrunk1.add("all");
        m_callcontrol.addInboundRoutes("InRoute1","","",add_inbound_route.s_extensin,"1000",arraytrunk1);
        ys_apply();
    }

    @AfterMethod
    public void AfterMethod(){

        if(cdRandRecordings.deleteCDR.isDisplayed()){
            closeCDRRecord();
        }
    }

    @AfterClass
    public void AfterClass()  {
        Reporter.infoAfterClass("执行完毕：=======  Queue  ======="); //执行操作
        quitDriver();
        pjsip.Pj_Destory();

        ys_waitingTime(10000);
        killChromePid();
    }
}
