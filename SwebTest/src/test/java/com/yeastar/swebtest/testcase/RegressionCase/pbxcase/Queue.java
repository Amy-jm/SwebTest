package com.yeastar.swebtest.testcase.RegressionCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import com.yeastar.untils.AllureReporterListener;
import com.yeastar.untils.RetryListener;
import com.yeastar.untils.TestNGListener;
import org.testng.annotations.*;

import java.lang.reflect.Method;

/**
 * Created by AutoTest on 2017/10/20.
 */
@Listeners({AllureReporterListener.class, RetryListener.class, TestNGListener.class})
public class Queue extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {

        Reporter.infoBeforeClass("开始执行：======  Queue  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);

        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }

    @BeforeClass
    public void InitQueue() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
        }
        queue.queue.click();
        queue.add.shouldBe(Condition.exist);
        deletes(" 删除所有Queue",queue.grid,queue.delete,queue.delete_yes,queue.grid_Mask);
        Reporter.infoExec(" 添加Queue1：6700，选择分机1000、1100、1105，其它默认 "); //执行操作
        m_callFeature.addQueue("Queue1","6700",1000,1100,1105);
    }

    @Test(priority = 0)
    public void A0_Register() throws InterruptedException {
        pjsip.Pj_Init();
        //        被测设备注册分机1000、1100、1101、1102、1105，辅助1：分机3001，辅助2：分机2000、2001
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(1100,EXTENSION_PASSWORD,"UDP",UDP_PORT,2);
        pjsip.Pj_CreateAccount(1101,EXTENSION_PASSWORD,"UDP",UDP_PORT,3);
        pjsip.Pj_CreateAccount(1102,EXTENSION_PASSWORD,"UDP",UDP_PORT,3);
        pjsip.Pj_CreateAccount(1105,EXTENSION_PASSWORD,"UDP",UDP_PORT,7);
        pjsip.Pj_CreateAccount(3001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1100,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1102,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1105,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2);
    }

    @Test(priority = 1)
    public void A1_add_queue6701() throws InterruptedException {
        Reporter.infoExec(" 新建Queue6701,Password:123，FailoverDestination：分机1000，Static Agents：空"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        queue.add.shouldBe(Condition.exist);
        queue.add.click();
        ys_waitingMask();
        add_queue_basic.number.clear();
        add_queue_basic.number.setValue("6701");
        add_queue_basic.name.clear();
        add_queue_basic.name.setValue("Queue6701");
        add_queue_basic.password.clear();
        add_queue_basic.password.setValue("123");
        comboboxSelect(add_queue_basic.failoverDestination,s_extensin);
        comboboxSet(add_queue_basic.failoverDestinationdst,extensionList,"1000");
        add_queue_basic.save.click();
        ys_waitingLoading(queue.grid_Mask);
    }

    @Test(priority = 2)
    public void A_add_queue6702() throws InterruptedException {
        Reporter.infoExec(" 新建Queue6702,Ring Strategy：RingAll，Static Agents：ExtensionGroup1，Agent Announcement：prompt1，" +
                "Caller Max Wait Time:100,按9到分机1000"); //执行操作
        queue.add.shouldBe(Condition.exist);
        queue.add.click();
        ys_waitingMask();
        add_queue_basic.number.clear();
        add_queue_basic.number.setValue("6702");
        add_queue_basic.name.clear();
        add_queue_basic.name.setValue("Queue6702");
        listSelect(add_queue_basic.list_AddQueue,extensionList,"ExtensionGroup1");
        comboboxSelect(add_queue_basic.agentAnnouncement,"prompt1");
        add_queue_callerExperienceSettings.callerExperienceSettings.click();
        add_queue_callerExperienceSettings.callerMaxWaitTime.setValue("100");
        comboboxSelect(add_queue_callerExperienceSettings.key,"9");
        comboboxSelect(add_queue_callerExperienceSettings.keydest,s_extensin);
        comboboxSet(add_queue_callerExperienceSettings.keydest2,extensionList,"1000");
        add_queue_callerExperienceSettings.save.click();
        ys_waitingLoading(queue.grid_Mask);
        ys_apply();
    }

    @Test(priority = 3)
    public void A_editInbound() throws InterruptedException {
        Reporter.infoExec(" 编辑呼入路由Inbound1到Queue6702"); //执行操作
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.shouldBe(Condition.exist);
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_inbound_route.destinationType,s_queue);
        comboboxSet(add_inbound_route.destination,"name","Queue6702");
        add_inbound_route.save.click();
        ys_waitingTime(1000);
        ys_apply();
    }

//    Failover Destination /leave when empty /Join Empty
   @Test(priority = 4)
    public void B_failtoextension() throws InterruptedException {
        Reporter.infoExec(" 1105拨打6701，预期到FailDestination--1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1105,"6701",DEVICE_IP_LAN,false);
        ys_waitingTime(10000);
       YsAssert.assertEquals(getExtensionStatus(1000,TALKING,10),TALKING,"预期1000为Talking状态");
       pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1105 <1105>","1000 <6701(1000)>","Answered","","",communication_internal);
    }

//    动态坐席 & Password
    @Test(priority = 5)
    public void C_agent1(Method method) throws InterruptedException {
        Reporter.infoExec(" 1100拨打6701*加入队列6701，密码：123"); //执行操作
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_Make_Call_Auto_Answer(1100,"6701*",DEVICE_IP_LAN);
        boolean showKeyWord= tcpSocket.getAsteriskInfo("macro-queue-login");
        System.out.println("Q_QueueLoginCall TcpSocket return: "+showKeyWord);
        if (showKeyWord){
            ys_waitingTime(2000);
            pjsip.Pj_Send_Dtmf(1100,"1","2","3","#");
            boolean showKeyWord2= tcpSocket.getAsteriskInfo("Playback(agent-loginok)");
            System.out.println("Q_QueueLoginOk TcpSocket return: "+showKeyWord2);
            tcpSocket.closeTcpSocket();
            YsAssert.assertEquals(showKeyWord2,true,"动态坐席1100输入密码123");
        }else {
            tcpSocket.closeTcpSocket();
            YsAssert.fail("动态坐席1100加入队列6701失败");
        }

        ys_waitingTime(10000);
    }

    @Test(priority = 6)
    public void C_agent2() throws InterruptedException {
        Reporter.infoExec(" 1105拨打6701，预期动态坐席1100接听"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1105,"6701",DEVICE_IP_LAN,false);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
//        cloud cdr
        m_extension.checkCDR("1105 <1105>","1100 <6701(1100)>","Answered","","",communication_internal);
    }

    //    退出动态坐席
    @Test(priority = 7)
    public void C_agent3() throws InterruptedException {
        Reporter.infoExec(" 1100拨打6701**退出队列6701"); //执行操作
        tcpSocket.connectToDevice(40000);
        pjsip.Pj_Make_Call_Auto_Answer(1100,"6701**",DEVICE_IP_LAN);
        boolean showKeyWord= tcpSocket.getAsteriskInfo("agent-loggedoff");
        System.out.println("Q_QueueLoginCall TcpSocket return: "+showKeyWord);
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(showKeyWord,true,"动态坐席1100退出队列6701");
    }

    @Test(priority = 8)
    public void C_agent4() throws InterruptedException {
        Reporter.infoExec(" 1105拨打6701，预期FailDestina-1000接听"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1105,"6701",DEVICE_IP_LAN,false);
        ys_waitingTime(10000);
        YsAssert.assertEquals(getExtensionStatus(1000,TALKING,10),TALKING,"预期1000为Talking状态");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1105 <1105>","1000 <6701(1000)>","Answered","","",communication_internal);
    }

//    Ring All & Agent Announcement & ExtentionGroup
    @Test(priority = 9)
    public void D_RingAll1() throws InterruptedException {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到Queue6702,1100接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,10),RING,"1000预期响铃");
        YsAssert.assertEquals(getExtensionStatus(1100,RING,10),RING,"1100预期响铃");
        YsAssert.assertEquals(getExtensionStatus(1101,RING,10),RING,"1101预期响铃");
        YsAssert.assertEquals(getExtensionStatus(1105,RING,10),RING,"1105预期响铃");
        ys_waitingTime(5000);
        pjsip.Pj_Answer_Call(1100,true);

//        检查Agent应答提示音=====检测方法？

        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1100 <6702(1100)>","Answered",SIPTrunk,"",communication_inbound);
    }

//    Caller Max Wait Time
    @Test(priority = 10)
    public void E_maxWaitTime() throws InterruptedException {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到Queue6702,100s内无人接就挂断通话"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,100),RING,"1000预期响铃");
        YsAssert.assertEquals(getExtensionStatus(1100,RING,100),RING,"1100预期响铃");
        YsAssert.assertEquals(getExtensionStatus(1101,RING,100),RING,"1101预期响铃");
        YsAssert.assertEquals(getExtensionStatus(1105,RING,100),RING,"1105预期响铃");
        ys_waitingTime(110000);
        YsAssert.assertEquals(getExtensionStatus(2001,HUNGUP,20),HUNGUP,"2001预期HangUp");
    }

//    key to 1000
    @Test(priority = 11)
    public void F_key() throws InterruptedException {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到Queue6702,按9到分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,100),RING,"1000预期响铃");
        YsAssert.assertEquals(getExtensionStatus(1100,RING,100),RING,"1100预期响铃");
        YsAssert.assertEquals(getExtensionStatus(1101,RING,100),RING,"1101预期响铃");
        YsAssert.assertEquals(getExtensionStatus(1105,RING,100),RING,"1105预期响铃");
        pjsip.Pj_Send_Dtmf(2001,"9");
        YsAssert.assertEquals(getExtensionStatus(1100,HUNGUP,100),HUNGUP,"1100预期挂断");
        YsAssert.assertEquals(getExtensionStatus(1101,HUNGUP,100),HUNGUP,"1101预期挂断");
        YsAssert.assertEquals(getExtensionStatus(1105,HUNGUP,100),HUNGUP,"1105预期挂断");
        YsAssert.assertEquals(getExtensionStatus(1000,RING,100),RING,"1000预期响铃");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <6702(1000)>","Answered",SPS,"",communication_inbound);
    }

//    Join Empty & Leave When Empty
    @Test(priority =12)
    public void G1_edit6701_1() throws InterruptedException {
        Reporter.infoExec(" 编辑Queue6701,启用Empty，不启用Leave When Empty"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_tree.click();
        queue.add.shouldBe(Condition.exist);
        gridClick(queue.grid,gridFindRowByColumn(queue.grid,queue.gridcolumn_Name,"Queue6701",sort_ascendingOrder),queue.gridEdit);
        ys_waitingMask();
        add_queue_callerExperienceSettings.callerExperienceSettings.click();
        setCheckBox(add_queue_callerExperienceSettings.leaveWhenEmpty,false);
        setCheckBox(add_queue_callerExperienceSettings.joinEmpty,true);
        add_queue_callerExperienceSettings.save.click();
        ys_waitingTime(3000);
        ys_apply();
    }

    @Test(expectedExceptions = AssertionError.class,priority = 13)
    public void G2_joinEmpty() throws InterruptedException {
        Reporter.infoExec(" 1100拨打6701--预期：队列无有效坐席，仍可成功呼入队列"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"6701",DEVICE_IP_LAN);
        ys_waitingTime(5000);
        YsAssert.assertEquals(getExtensionStatus(1100,TALKING,10),TALKING,"1100预期Talking状态");
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","6701","Answered","","",communication_internal);
    }

//    Leave When Empty
    @Test(priority = 14)
    public void G3_edit6701_2() throws InterruptedException {
        Reporter.infoExec(" 编辑Queue6701，启用Empty，启用Leave When Empty"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_tree.click();
        queue.add.shouldBe(Condition.exist);
        gridClick(queue.grid,gridFindRowByColumn(queue.grid,queue.gridcolumn_Name,"Queue6701",sort_ascendingOrder),queue.gridEdit);
        ys_waitingMask();
        add_queue_callerExperienceSettings.callerExperienceSettings.click();
        setCheckBox(add_queue_callerExperienceSettings.leaveWhenEmpty,true);
        setCheckBox(add_queue_callerExperienceSettings.joinEmpty,true);
        add_queue_callerExperienceSettings.save.click();
        ys_waitingTime(3000);
        ys_apply();
    }

    @Test(priority = 15)
    public void G4_leaveWhenEmpty() throws InterruptedException {
        Reporter.infoExec(" 1100拨打6701--预期：队列无有效坐席--failover 1000分机响铃"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"6701",DEVICE_IP_LAN);
        ys_waitingTime(5000);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,10),RING,"1000预期响铃");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","1000 <6701(1000)>","Answered","","",communication_internal);
    }

//    编辑6701，成员选择1105，1000,1101，响铃策略线性响铃
    @Test(priority = 16)
    public void H1_Linear() throws InterruptedException {
        Reporter.infoExec(" 编辑6701，成员选择1105，1101,1102，响铃策略线性响铃"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_tree.click();
        queue.add.shouldBe(Condition.exist);
        gridClick(queue.grid,gridFindRowByColumn(queue.grid,queue.gridcolumn_Name,"Queue6701",sort_ascendingOrder),queue.gridEdit);
        ys_waitingMask();
        listSelect(add_queue_basic.list_AddQueue,extensionList,"1105","1101","1102");
        comboboxSelect(add_queue_basic.ringStrategy,"linear");
        add_queue_basic.agentTimeout.setValue("10");
        add_queue_basic.retry.setValue("10");
        add_queue_basic.wrap_upTime.setValue("0");
        add_queue_basic.save.click();
        ys_waitingTime(3000);
        ys_apply();
    }

    @Test(priority = 17)
    public void H2_Linear() throws InterruptedException {
        Reporter.infoExec(" 1000拨打6701，轮到1102响铃时接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"6701",DEVICE_IP_LAN);
        YsAssert.assertEquals(getExtensionStatus(1105,RING,10),RING,"1105预期响铃");
        YsAssert.assertEquals(getExtensionStatus(1101,RING,25),RING,"1101预期响铃");
        YsAssert.assertEquals(getExtensionStatus(1105,HUNGUP,1),HUNGUP,"1101响铃时，1105不会响铃");
        YsAssert.assertEquals(getExtensionStatus(1102,RING,25),RING,"1102预期响铃");
        YsAssert.assertEquals(getExtensionStatus(1105,HUNGUP,1),HUNGUP,"1102响铃时，1105不会响铃");
        YsAssert.assertEquals(getExtensionStatus(1101,HUNGUP,1),HUNGUP,"1102响铃时，1101不会响铃");
        pjsip.Pj_Answer_Call(1102,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1102 <6701(1102)>","Answered","","",communication_internal);
    }

//    删除
    @Test(priority =18 )
    public void I_delete() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_tree.click();
        queue.queue.click();
        queue.add.shouldBe(Condition.exist);
        Reporter.infoExec(" 表格删除：Queue6701-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(queue.grid,queue.gridcolumn_Name,"Queue6701",sort_ascendingOrder)));
        int rows=Integer.parseInt(String.valueOf(gridLineNum(queue.grid)));
        gridClick(queue.grid,row,queue.gridDelete);
        queue.delete_no.click();
        ys_waitingLoading(queue.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(queue.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：Queue6701-取消删除");

        Reporter.infoExec(" 表格删除：Queue6701-确定删除"); //执行操作
        gridClick(queue.grid,row,queue.gridDelete);
        queue.delete_yes.click();
        ys_waitingLoading(queue.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(queue.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：Queue6701-确定删除");

        Reporter.infoExec(" 删除：Queue6702-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(queue.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(queue.grid,queue.gridcolumn_Name,"Queue6702",sort_ascendingOrder)));
        gridCheck(queue.grid,row4,queue.gridcolumn_Check);
        queue.delete.click();
        queue.delete_no.click();
        ys_waitingLoading(queue.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(queue.grid)));
        System.out.println("row5:"+row5);
        YsAssert.assertEquals(row8,row5,"删除：Queue6702-取消删除");

        Reporter.infoExec(" 删除：Queue6702-确定删除"); //执行操作
        queue.delete.click();
        queue.delete_yes.click();
        ys_waitingLoading(queue.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(queue.grid)));
        System.out.println("row6:"+row6);
        int row7=row6+1;
        System.out.println("row7:"+row7);
        YsAssert.assertEquals(row5,row7,"删除：Queue6702-确定删除");
        ys_apply();
    }

    @Test(priority =19 )
    public void J_Recovery() throws InterruptedException {
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
        Reporter.infoAfterClass("执行完毕：======  Queue  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        ys_waitingTime(10000);
        killChromePid();

    }
}
