package com.yeastar.swebtest.testcase.RegressionCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;

/**
 * 会议室：提示音相关都未进行测试，无法判断
 * Created by AutoTest on 2017/10/16.
 */
public class Conference extends SwebDriver {
    public int num = 1;
    @BeforeClass
    public void BeforeClass() {

        Reporter.infoBeforeClass("开始执行：======  Conference  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);

        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }

    @Test
    public void AA0_Register() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoExec(" 被测设备注册分机1000、1100、1105，辅助1：分机3001，辅助2：分机2001"); //执行操作
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(1100,EXTENSION_PASSWORD,"UDP",UDP_PORT,2);
        pjsip.Pj_CreateAccount(1105,EXTENSION_PASSWORD,"UDP",UDP_PORT,7);
        pjsip.Pj_CreateAccount(3001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1100,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1105,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2);
    }

    @Test
    public void AA1_InitConference() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
        }else{
            ys_waitingTime(5000);
        }
        conference.conference.click();
        deletes(" 删除所有Conference",conference.grid,conference.delete,conference.delete_yes,conference.grid_Mask);
        ys_apply();
        Reporter.infoExec(" 添加Conference1:6400"); //执行操作
        m_callFeature.addConference("6400","Conference1");
    }

    @Test
    public void A_add_conference1() throws InterruptedException {
        Reporter.infoExec(" 新建Conference6401，默认设置"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        conference.add.shouldBe(Condition.exist);
        m_callFeature.addConference("6401","Conference6401");
    }

    @Test
    public void A_add_conference2() throws InterruptedException {
        Reporter.infoExec(" 新建Conference6402，与会者密码123，启用等候管理员，提示音：分机号码，Allow Participant to Invite：不启用，" +
                "管理员密码：456，管理员：1000、1100"); //执行操作
        conference.add.shouldBe(Condition.exist);
        conference.add.click();
        ys_waitingMask();
        add_conference.number.clear();
        add_conference.number.setValue("6402");
        add_conference.name.clear();
        add_conference.name.setValue("Conference6402");
        add_conference.participantPassword.setValue("123");
        setCheckBox(add_conference.waitforModeretor,true);
        comboboxSelect(add_conference.soundPrompt,"exten");
        setCheckBox(add_conference.allowParticipanttoInvite,false);
        add_conference.moderatorPassword.setValue("456");
        listSelect(add_conference.list_moderator,extensionList,"1000","1100");
        add_conference.save.click();
        ys_waitingTime(3000);
    }

    @Test
    public void B_editInRoute1() throws InterruptedException {
        Reporter.infoExec(" 编辑呼入路由InRoute1到Conference6402"); //执行操作
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        inboundRoutes.add.shouldBe(Condition.exist);
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_inbound_route.destinationType,s_conference);
        comboboxSet(add_inbound_route.destination,"name","Conference6402");
        add_inbound_route.save.click();
        ys_waitingTime(1000);
        ys_apply();
    }

    @Test
    public void C_CallIn1() throws InterruptedException {
        Reporter.infoExec(" 1000拨打6401呼入到会议室6401"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"6401",DEVICE_IP_LAN);
        ys_waitingTime(5000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6401")),num,"预期会议室6401有"+num+"个成员");
        num =num+1;
        System.out.println("会议室成员数："+num);
    }

    @Test
    public void C_CallIn2() throws InterruptedException {
        Reporter.infoExec(" 1100拨打6401呼入到会议室6401"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"6401",DEVICE_IP_LAN);
        ys_waitingTime(5000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6401")),num,"预期会议室6401有"+num+"个成员");
        num =num+1;
        System.out.println("会议室成员数："+num);
    }

    @Test
    public void C_CallIn3() throws InterruptedException {
        Reporter.infoExec(" 1105拨打6401呼入到会议室6401"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1105,"6401",DEVICE_IP_LAN);
        ys_waitingTime(5000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6401")),num,"预期会议室6401有"+num+"个成员");
        System.out.println("会议室成员数："+num);
    }


    @Test
    public void C_CallIn4() throws InterruptedException {
        Reporter.infoExec(" 1000退出会议室6401"); //执行操作
        pjsip.Pj_hangupCall(1000,1000);
        ys_waitingTime(1000);
        num =num-1;
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6401")),num,"预期会议室6401有"+num+"个成员");
        System.out.println("会议室成员数："+num);
    }

    @Test
    public void C_CallIn5() throws InterruptedException {
        num=1;
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(String.valueOf(pbxMonitor.getInConference_num("Conference6401")),"0","预期会议室6401有0个成员");
        m_extension.checkCDR("1105 <1105>","6401","Answered","","",communication_internal);
    }


    @Test
    public void D_callin1() throws InterruptedException {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入6402，输入与会密码123"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(3001,"1","2","3","#");
        ys_waitingTime(2000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6402")),num,"预期会议室6402有"+num+"个成员");
        num =num+1;
        System.out.println("会议室成员数："+num);
    }

    @Test
    public void D_callin2() throws InterruptedException {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入6402，输入管理员密码456"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2001,"4","5","6","#");
        ys_waitingTime(5000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6402")),num,"预期会议室6402有"+num+"个成员");
        num =num+1;
        System.out.println("会议室成员数："+num);
    }

    @Test
    public void D_callin3() throws InterruptedException {
        ys_waitingTime(2000);
        Reporter.infoExec(" 1000拨打6402呼入6402，无需密码"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"6402",DEVICE_IP_LAN,false);
        ys_waitingTime(2000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6402")),num,"预期会议室6402有"+num+"个成员");
        num =num+1;
        System.out.println("会议室成员数："+num);
    }

    @Test
    public void D_callin4() throws InterruptedException {
        Reporter.infoExec(" 1105拨打6402呼入6402，输入与会者密码123"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1105,"6402",DEVICE_IP_LAN,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(1105,"1","2","3","#");
        ys_waitingTime(2000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6402")),num,"预期会议室6402有"+num+"个成员");
        System.out.println("会议室成员数："+num);
        Reporter.infoExec(" 1105按#1100，邀请1100加入会议，预期：邀请失败"); //执行操作
        pjsip.Pj_Send_Dtmf(1105,"#","1","1","0","0","#");
        ys_waitingTime(2000);
        YsAssert.assertEquals(Integer.parseInt(pbxMonitor.getInConference_num("Conference6402")),num,"预期会议室6402有"+num+"个成员");
        System.out.println("会议室成员数："+num);
    }

    @Test
    public void D_callin5() throws InterruptedException {
        Reporter.infoExec(" 挂断所有通话,所有成员退出会议室6402"); //执行操作
        pjsip.Pj_Hangup_All();
        num=1;
        YsAssert.assertEquals(String.valueOf(pbxMonitor.getInConference_num("Conference6402")),"0","预期会议室6402有0个成员");
        m_extension.checkCDR("1105 <1105>","6402","Answered","","",communication_internal);
    }

//    按#邀请
    @Test
    public void E_invite() throws InterruptedException {
        Reporter.infoExec(" 1105呼入到会议室6401"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1105,"6401",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        Reporter.infoExec(" 1105按#...1100邀请分机1100加入会议室"); //执行操作
        pjsip.Pj_Send_Dtmf(1105,"#","1","1","0","0","#");
        YsAssert.assertEquals(getExtensionStatus(1100,RING,25),RING,"1100预期响铃");
        pjsip.Pj_Answer_Call(1100,false);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","6401(from 1105)","Answered","","",communication_internal,1,2);
    }
    
//    删除
    @Test
    public void F_delete() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_tree.click();
        conference.conference.click();
        conference.add.shouldBe(Condition.exist);
        Reporter.infoExec(" 表格删除：Conference6401-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(conference.grid,conference.gridcolumn_Name,"Conference6401",sort_ascendingOrder)));
        int rows=Integer.parseInt(String.valueOf(gridLineNum(conference.grid)));
        gridClick(conference.grid,row,conference.gridDelete);
        conference.delete_no.click();
        ys_waitingLoading(conference.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(conference.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：Conference6401-取消删除");

        Reporter.infoExec(" 表格删除：Conference6401-确定删除"); //执行操作
        gridClick(conference.grid,row,conference.gridDelete);
        conference.delete_yes.click();
        ys_waitingLoading(conference.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(conference.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：Conference6401-确定删除");

        Reporter.infoExec(" 删除：Conference6402-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(conference.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(conference.grid,conference.gridcolumn_Name,"Conference6402",sort_ascendingOrder)));
        gridCheck(conference.grid,row4,conference.gridcolumn_Check);
        conference.delete.click();
        conference.delete_no.click();
        ys_waitingLoading(conference.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(conference.grid)));
        System.out.println("row5:"+row5);
        YsAssert.assertEquals(row8,row5,"删除：Conference6402-取消删除");

        Reporter.infoExec(" 删除：Conference6402-确定删除"); //执行操作
        conference.delete.click();
        conference.delete_yes.click();
        ys_waitingLoading(conference.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(conference.grid)));
        System.out.println("row6:"+row6);
        int row7=row6+1;
        System.out.println("row7:"+row7);
        YsAssert.assertEquals(row5,row7,"删除：Conference6402-确定删除");
    }

    @Test
    public void G_recovery() throws InterruptedException {
        Reporter.infoExec(" 恢复呼入路由InRoute1到分机1000");
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        inboundRoutes.add.shouldBe(Condition.exist);
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_inbound_route.destinationType,s_extensin);
        comboboxSet(add_inbound_route.destination,extensionList,"1000");
        add_inbound_route.save.click();
        ys_waitingTime(1000);
        ys_apply();
    }

    @AfterMethod
    public void AfterMethod(){
        if(cdRandRecordings.deleteCDR.isDisplayed()){
            closeCDRRecord();
        }
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        pjsip.Pj_Hangup_All();
        Reporter.infoAfterClass("执行完毕：======  Conference  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        ys_waitingTime(10000);
        killChromePid();

    }

}
