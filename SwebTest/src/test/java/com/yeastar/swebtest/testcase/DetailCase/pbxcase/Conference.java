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

import static com.codeborne.selenide.Selenide.sleep;

/**
 * Created by AutoTest on 2017/12/20.
 */
public class Conference extends SwebDriver {
     public int num = 1;

    @BeforeClass
    public void BeforeClass()  {

        Reporter.infoBeforeClass("开始执行：=======  Conference  ======="); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && LOGIN_ADMIN.equals("yes") && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }

    @Test
    public void A0_Register() {
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(1100,EXTENSION_PASSWORD,"UDP",UDP_PORT,2);
        pjsip.Pj_CreateAccount(3001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(3003,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2003,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2004,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2005,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2006,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1100,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3003,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2003,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2004,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2005,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2006,DEVICE_ASSIST_2);
    }

     @Test
     public void A1_Init()  {
         pageDeskTop.taskBar_Main.click();
         pageDeskTop.settingShortcut.click();
         settings.callFeatures_panel.click();
         if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
             ys_waitingMask();
         }else{
             ys_waitingTime(5000);
         }
         conference.conference.click();
         deletes(" 删除所有Conference",conference.grid,conference.delete,conference.delete_yes,conference.grid_Mask);
         ys_apply();
     }

    @DataProvider(name="add")
    public Object[][] Numbers() throws BiffException, IOException{
        ExcelUnit e=new ExcelUnit("database", "conference");
        return e.getExcelData();
    }

    @Test(dataProvider="add")
    public void A2_AddConferences(HashMap<String, String> data)  {
        Reporter.infoExec("添加会议室" + data.get("Name") + "：" + data.get("Number"));
        conference.add.shouldBe(Condition.exist);
        conference.add.click();
        ys_waitingMask();
        add_conference.number.clear();
        add_conference.number.setValue(data.get("Number"));
        add_conference.name.clear();
        add_conference.name.setValue(data.get("Name"));
        add_conference.participantPassword.setValue(data.get("ParticipantPassword"));
        setCheckBox(add_conference.waitforModeretor,Boolean.valueOf(data.get("waitModerator")));
        comboboxSelect(add_conference.soundPrompt,data.get("SoundPrompt"));
        setCheckBox(add_conference.allowParticipanttoInvite,Boolean.valueOf(data.get("AllowInvite")));
        add_conference.moderatorPassword.setValue(data.get("ModeratorPassword"));
        if(!data.get("Moderators").equals("")){
            ArrayList<String> memberList = new ArrayList<>();
            String[] List1 = data.get("Moderators").split(",");
            for(int i=0;i< List1.length;i++){
                System.out.println(String.valueOf(List1[i]));
                memberList.add(String.valueOf(List1[i]));
            }
            listSelect(add_conference.list_moderator,extensionList,memberList);
        }
        add_conference.save.click();
        ys_waitingTime(5000);
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
        Reporter.infoExec("添加呼入路由："+data.get("Name")+"，DID："+data.get("DID")+"，Destination："+data.get("Name"));
        ArrayList<String> memberList = new ArrayList<>();
        memberList.add("all");
        m_callcontrol.addInboundRoutes(data.get("Name"),data.get("DID"),"",s_conference,data.get("Name"),memberList);
    }

    @Test
    public void B3_Apply()  {
        ys_apply();
    }

    @Test
    public void B4_backup(){
        backupEnviroment(this.getClass().getSimpleName());
    }
//    通话测试
    @Test
    public void C1_extension()  {
        Reporter.infoExec(" 分机1000呼入会议室6400"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"6400",DEVICE_IP_LAN);
        ys_waitingTime(20000);
        if(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400"))==num) {
            YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400")), num, "预期会议室6400有" + num + "个成员");
            num = num + 1;
            Reporter.infoExec("会议室成员数：" + num);
        }else {
            YsAssert.fail("分机1000呼入会议室6400失败");
        }
    }

    @Test
    public void C2_sps()  {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到会议室6400"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(20000);
        if(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400"))==num) {
            YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400")), num, "预期会议室6400有" + num + "个成员");
            num = num + 1;
            Reporter.infoExec("会议室成员数：" + num);
        }else {
            YsAssert.fail("从sps外线呼入会议室6400失败");
        }
    }

    @Test
    public void C3_sip()  {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到会议室6400"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(20000);
        if(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400"))==num) {
            YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400")), num, "预期会议室6400有" + num + "个成员");
            if(!PRODUCT.equals(CLOUD_PBX)) {
                num = num + 1;
            }
            Reporter.infoExec("会议室成员数：" + num);
        }else {
            YsAssert.fail("从sip外线呼入会议室6400失败");
        }
    }

    @Test
    public void C4_spx()  {
        if (PRODUCT.equals(CLOUD_PBX)) {
            return;
        }
        Reporter.infoExec(" 2001拨打88888通过spx外线呼入到会议室6400"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001, "88888", DEVICE_ASSIST_2, false);
        ys_waitingTime(20000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400")), num, "预期会议室6400有" + num + "个成员");
        num = num + 1;
        Reporter.infoExec("会议室成员数："+num);
    }

    @Test
    public void C5_iax1()  {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 3003拨打3100通过iax外线呼入到会议室6400"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3003,"3100",DEVICE_ASSIST_1,false);
        ys_waitingTime(20000);
        if(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400"))==num) {
            YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400")), num, "预期会议室6400有" + num + "个成员");
            num=num+1;
            Reporter.infoExec("会议室成员数：" + num);
        }else {
            YsAssert.fail("iax外线呼入会议室6400失败");
        }
    }

    @Test
    public void C5_iax2()  {
        if(PRODUCT.equals(PC)) {
            num = num - 1;
        }
    }

    @Test
    public void C6_fxo()  {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(FXO_1.equals("null")){
            return;
        }
        Reporter.infoExec(" 2003拨打2010通过pstn外线呼入到会议室6400"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2003,"2010",DEVICE_ASSIST_2,false);
        ys_waitingTime(20000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400")), num, "预期会议室6400有" + num + "个成员");
        num = num + 1;
        Reporter.infoExec("会议室成员数："+num);
    }

    @Test
    public void C7_bri()  {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(BRI_1.equals("null")){
            return;
        }
        Reporter.infoExec(" 2004拨打66666通过BRI外线呼入到会议室6400"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2004,"66666",DEVICE_ASSIST_2,false);
        ys_waitingTime(20000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400")), num, "预期会议室6400有" + num + "个成员");
        num = num + 1;
        Reporter.infoExec("会议室成员数："+num);
    }

    @Test
    public void C8_e1()  {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(E1.equals("null")){
            return;
        }
        Reporter.infoExec(" 2005拨打77777通过E1外线呼入到会议室6400"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2005,"77777",DEVICE_ASSIST_2,false);
        ys_waitingTime(20000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400")), num, "预期会议室6400有" + num + "个成员");
        num = num + 1;
        Reporter.infoExec("会议室成员数："+num);
    }

    @Test
    public void C9_gsm()  {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(GSM.equals("null")){
            num=num -1;
            return;
        }
        Reporter.infoExec(" 2006拨打GSM号码呼入到会议室6400"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2006,DEVICE_TEST_GSM,DEVICE_ASSIST_2,false);
        ys_waitingTime(35000);
        if(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400"))== num){
            YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400")), num, "预期会议室6400有" + num + "个成员");
        }else{
            num=num-1;
            YsAssert.fail("GSM呼入会议室6400失败");
        }
    }

//    会议室中对应的分机状态为0（IDLE），无法通过判断对应的分机状态为Talking
    @Test
    public void Ca1_hangup_extension()  {
        Reporter.infoExec(" 1000退出会议室"); //执行操作
        pjsip.Pj_hangupCall(1000,1000);
        ys_waitingTime(2000);
        num=num-1;
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400")), num, "预期会议室6400有" + num + "个成员");
        m_extension.checkCDR("1000 <1000>","6400","Answered","","",communication_internal);
    }

    @Test
    public void Ca2_hangup_sps()  {
        Reporter.infoExec(" 2000退出会议室"); //执行操作
        pjsip.Pj_hangupCall(2000,2000);
        ys_waitingTime(2000);
        num=num-1;
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400")), num, "预期会议室6400有" + num + "个成员");
        m_extension.checkCDR("2000 <2000>","6400","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void Ca3_hangup_sip()  {
        Reporter.infoExec(" 3001退出会议室"); //执行操作
        pjsip.Pj_hangupCall(3001,3001);
        num=num-1;
        ys_waitingTime(2000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400")), num, "预期会议室6400有" + num + "个成员");
        m_extension.checkCDR("3001 <3001>","6400","Answered",SIPTrunk,"",communication_inbound);
    }

    @Test
    public void Ca4_hangup_spx()  {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 2001退出会议室"); //执行操作
        pjsip.Pj_hangupCall(2001,2001);
        m_extension.checkCDR("2001 <2001>","6400","Answered",SPX,"",communication_inbound);
    }

    @Test
    public void Ca5_hangup_iax()  {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 3003退出会议室"); //执行操作
        pjsip.Pj_hangupCall(3003,3003);
        m_extension.checkCDR("3003 <3003>","6400","Answered",IAXTrunk,"",communication_inbound);
    }

    @Test
    public void Ca6_hangup_fxo()  {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC) || FXO_1.equals("null")){
            return;
        }
        Reporter.infoExec(" 2003退出会议室"); //执行操作
        pjsip.Pj_hangupCall(2003,2003);
        m_extension.checkCDR("2003 <2003>","6400","Answered",FXO_1,"",communication_inbound);
    }

    @Test
    public void Ca7_hangup_bri()  {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC) || BRI_1.equals("null")){
            return;
        }
        Reporter.infoExec(" 2004退出会议室"); //执行操作
        pjsip.Pj_hangupCall(2004,2004);
        m_extension.checkCDR("2004 <2004>","6400","Answered",BRI_1,"",communication_inbound);
    }

    @Test
    public void Ca8_hangup_e1()  {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC) || E1.equals("null")){
            return;
        }
        Reporter.infoExec(" 2005退出会议室"); //执行操作
        pjsip.Pj_hangupCall(2005,2005);
        m_extension.checkCDR("2005 <2X612>","6400","Answered",E1,"",communication_inbound);
    }

    @Test
    public void Ca9_hangup_gsm()  {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC) || GSM.equals("null")){
            return;
        }
        Reporter.infoExec(" 2006退出会议室"); //执行操作
        pjsip.Pj_hangupCall(2006,2006);
        Reporter.infoExec("Pj_hangupCall 3");
        m_extension.checkCDR(DEVICE_ASSIST_GSM+" <"+DEVICE_ASSIST_GSM+">","6400","Answered",GSM,"",communication_inbound);
        Reporter.infoExec("Pj_hangupCall 4");
    }

//    与会者密码ParticipantPassword
    @Test
    public void D1_Participant1()  {
        pjsip.Pj_Hangup_All();
        Reporter.infoExec(" 2000拨打995503301通过sps外线呼入到会议室6401，输入与会者密码：789"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503301",DEVICE_ASSIST_2,false);
        ys_waitingTime(8000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("6401")), 0, "输入与会者密码前，预期会议室6401有" + 0 + "个成员");
        pjsip.Pj_Send_Dtmf(2000,"7","8","9","#");
        ys_waitingTime(5000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("6401")), 1, "输入与会者密码后，预期会议室6401有" + 1 + "个成员");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","6401","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void D2_Participant2()  {
        Reporter.infoExec(" 1000拨打6401呼入到会议室6401，管理员不需要输入密码直接进入会议室"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"6401",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("6401")), 1, "预期会议室6401有" + 1 + "个成员");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","6401","Answered","","",communication_internal);
    }

    @Test
    public void D3_Participant3()  {
        Reporter.infoExec(" 2000拨打995503301通过sps外线呼入到会议室6401，输入错误的与会密码：123，再次输入正确的与会者密码：789"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503301",DEVICE_ASSIST_2,false);
        ys_waitingTime(3000);
//        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("6401")), 0, "预期会议室6401有" + 0 + "个成员");
        pjsip.Pj_Send_Dtmf(2000,"1","2","3","#");
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("6401")), 0, "输入错误的与会密码123后，预期会议室6401有" + 0 + "个成员");
        pjsip.Pj_Send_Dtmf(2000,"7","8","9","#");
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("6401")), 1, "输入正确的与会密码789后，预期会议室6401有" + 1 + "个成员");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","6401","Answered",SPS,"",communication_inbound);
    }

//    通话过程中修改与会者密码
    @Test
    public void D4_Participant4()  {
        Reporter.infoExec(" 1000拨打6401呼入到会议室6401，管理员不需要输入密码直接进入会议室，保持通话"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"6401",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        Reporter.infoExec(" 修改会议室的与会密码为空,立即生效"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_tree.click();

//        settings.callFeatures_panel.click();

        conference.conference.click();
        conference.add.shouldBe(Condition.exist);
        gridClick(conference.grid,gridFindRowByColumn(conference.grid,conference.gridcolumn_Name,"6401",sort_ascendingOrder),conference.gridEdit);
        ys_waitingMask();
        add_conference.participantPassword.setValue("");
        add_conference.save.click();
        ys_waitingTime(5000);
        ys_apply();
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("6401")), 1, "1000进入会议室，预期会议室6401有" + 1 + "个成员");
        Reporter.infoExec(" 1100拨打6401呼入到会议室6401，不输入密码，呼入成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"6401",DEVICE_IP_LAN,false);
        ys_waitingTime(40000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("6401")), 2, "1100加入会议室，预期会议室6401有" + 2 + "个成员");
        pjsip.Pj_Hangup_All();
        ys_waitingTime(3000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("6401")), 0, "所有成员退出会议室，预期会议室6401有" + 0 + "个成员");
        Reporter.infoExec("所有成员退出会议室6401， 1100再次拨打6401呼入到会议室6401，不输入密码，呼入成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"6401",DEVICE_IP_LAN,false);
        ys_waitingTime(10000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("6401")), 1, "1100进入会议室，预期会议室6401有" + 1 + "个成员");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","6401","Answered","","",communication_internal);
    }

//    提示音
    @Test
    public void E1_Prompt()  {
        Reporter.infoExec(" AMI未打印提示音相关信息--请手动验证"); //执行操作
    }

//    等候管理员--AMI---Status: off
    @Test
    public void F1_waitModerator()  {
        pjsip.Pj_Hangup_All();
        Reporter.infoExec(" 2000拨打995503302通过sps外线呼入到会议室6402，需等候管理员---判断方法：AMI打印Status: off"); //执行操作
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_Make_Call_No_Answer(2000,"995503302",DEVICE_ASSIST_2,false);
        boolean showKeyWord2 = tcpSocket.getAsteriskInfo("Status: off");
        System.out.println("Conference 等候管理员 TcpSocket return: " + showKeyWord2);
        YsAssert.assertEquals(showKeyWord2, true, "会议室6402需等候管理员才能通话");
        Reporter.infoExec(" 1100拨打6402呼入到会议室6402，需等候管理员---判断方法：AMI打印Status: off");
        pjsip.Pj_Make_Call_No_Answer(1100,"6402",DEVICE_IP_LAN,false);
        boolean showKeyWord1 = tcpSocket.getAsteriskInfo("Status: off");
        System.out.println("Conference 等候管理员 TcpSocket return: " + showKeyWord1);
        YsAssert.assertEquals(showKeyWord1, true, "会议室6402需等候管理员才能通话");
        Reporter.infoExec(" 1000拨打6402呼入到会议室6402，管理员进入会议室---判断方法：AMI不会打印Status: off");
        pjsip.Pj_Make_Call_No_Answer(1000,"6402",DEVICE_IP_LAN,false);
        boolean showKeyWord0 = tcpSocket.getAsteriskInfo("Status: off");
        YsAssert.assertEquals(showKeyWord0, false, "会议室6402等到管理员，进行通话");
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
    }

//    管理员密码
    @Test
    public void G1_ModeratorPassword()  {
        pjsip.Pj_Hangup_All();
        Reporter.infoExec(" 2000拨打995503302通过sps外线呼入到会议室6402，需等候管理员---判断方法：AMI打印Status: off"); //执行操作
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_Make_Call_No_Answer(2000,"995503302",DEVICE_ASSIST_2,false);
        boolean showKeyWord2 = tcpSocket.getAsteriskInfo("Status: off");
        System.out.println("Conference 等候管理员 TcpSocket return: " + showKeyWord2);

        Reporter.infoExec(" 1100拨打6402呼入到会议室6402，输入管理员密码：456---判断方法：AMI不会打印Status: off");
        pjsip.Pj_Make_Call_No_Answer(1100,"6402",DEVICE_IP_LAN,false);
//        ys_waitingTime(5000);
        getExtensionStatus(1100,TALKING,10);
        pjsip.Pj_Send_Dtmf(1100,"4","5","6","#");
        boolean showKeyWord1 = tcpSocket.getAsteriskInfo("Status: off");
        System.out.println("Conference 等候管理员 TcpSocket return: " + showKeyWord1);
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(showKeyWord2, true, "会议室6402需等候管理员才能通话");
        YsAssert.assertEquals(showKeyWord1, false, "1100成为会议室6402的管理员");
    }

//    允许邀请其它号码
    @Test
    public void H1_AllowInvite()  {
        pjsip.Pj_Hangup_All();
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到会议室6400，按#邀请成员1100加入会议室"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        getExtensionStatus(2000,TALKING,10);
        ys_waitingTime(10000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400")), 1, "2000进入会议室，预期会议室6400有" + 1 + "个成员");
        pjsip.Pj_Send_Dtmf(2000,"#");
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2000,"1","1","0","0","#");
        getExtensionStatus(1100,RING,20);
        pjsip.Pj_Answer_Call(1100,false);
        ys_waitingTime(8000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400")), 2, "1100被2000邀请进入会议室，预期会议室6400有" + 2 + "个成员");
        Reporter.infoExec(" 1100按#13001邀请外线号码加入会议室"); //执行操作
        pjsip.Pj_Send_Dtmf(1100,"#");
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(1100,"1","3","0","0","1","#");
        getExtensionStatus(3001,RING,20);
        pjsip.Pj_Answer_Call(3001,false);
        ys_waitingTime(8000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400")), 3, "3001被1100邀请进入会议室，预期会议室6400有" + 3 + "个成员");
        Reporter.infoExec(" 1100按#1000邀请管理员加入会议室"); //执行操作
        pjsip.Pj_Send_Dtmf(1100,"#");
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(1100,"1","0","0","0","#");
        getExtensionStatus(1000,RING,20);
        pjsip.Pj_Answer_Call(1000,false);
        ys_waitingTime(8000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6400")), 4, "管理员1000被1100邀请进入会议室，预期会议室6400有" + 4 + "个成员");
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","6400(from 1100)","Answered","","",communication_internal,2,1);
        m_extension.checkCDR("13001","6400(from 1100)","Answered","",SIPTrunk,communication_outRoute,4,2,3);
        m_extension.checkCDR("1100 <1100>","6400(from 2000)","Answered","","",communication_internal,1,3);
        m_extension.checkCDR("2000 <2000>","6400","Answered",SPS,"",communication_inbound,5,4);
    }

//    语音菜单
    @Test
    public void I1_menu()  {
        Reporter.infoExec(" 语音菜单--AMI未打印相关信息--请手动测试"); //执行操作
    }

//    删除
    @Test
    public void J1_delete()  {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_tree.click();
        conference.conference.click();
        conference.add.shouldBe(Condition.exist);
        setPageShowNum(conference.grid, 50);
        Reporter.infoExec(" 表格删除：Conference6400-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(conference.grid, conference.gridcolumn_Name, "Conference6400", sort_ascendingOrder)));
        int rows = Integer.parseInt(String.valueOf(gridLineNum(conference.grid)));
        gridClick(conference.grid, row, conference.gridDelete);
        conference.delete_no.click();
        ys_waitingLoading(conference.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(conference.grid)));
        System.out.println("row1:" + row1);
        YsAssert.assertEquals(rows, row1, "表格删除：Conference6400-取消删除");

        Reporter.infoExec(" 表格删除：Conference6400-确定删除"); //执行操作
        gridClick(conference.grid, row, conference.gridDelete);
        conference.delete_yes.click();
        ys_waitingLoading(conference.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(conference.grid)));
        System.out.println("row2:" + row2);
        int row3 = row2 + 1;
        System.out.println("row3:" + row3);
        YsAssert.assertEquals(row3, row1, "表格删除：conference6201-确定删除");

        Reporter.infoExec(" 删除：a-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(conference.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(conference.grid, conference.gridcolumn_Name, "a", sort_ascendingOrder)));
        gridCheck(conference.grid, row4, conference.gridcolumn_Check);
        conference.delete.click();
        conference.delete_no.click();
        ys_waitingLoading(conference.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(conference.grid)));
        System.out.println("row5:" + row5);
        YsAssert.assertEquals(row8, row5, "删除：a-取消删除");

        Reporter.infoExec(" 删除：a-确定删除"); //执行操作
        conference.delete.click();
        conference.delete_yes.click();
        ys_waitingLoading(conference.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(conference.grid)));
        System.out.println("row6:" + row6);
        int row7 = row6 + 1;
        System.out.println("row7:" + row7);
        YsAssert.assertEquals(row5, row7, "删除：a-确定删除");
        ys_apply();
    }

    @Test
    public void K1_recovery()  {
        Reporter.infoExec(" 恢复初始化环境"); //执行操作
        conference.conference.click();
        deletes(" 删除所有Conference",conference.grid,conference.delete,conference.delete_yes,conference.grid_Mask);
        Reporter.infoExec(" 添加Conference1:6400"); //执行操作
        m_callFeature.addConference("6400","Conference1");
    }

    @Test
    public void K2_recovery()  {
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
        Reporter.infoAfterClass("执行完毕：=======  Conference  ======="); //执行操作
        quitDriver();
        pjsip.Pj_Destory();

        sleep(10000);
        killChromePid();
    }
}
