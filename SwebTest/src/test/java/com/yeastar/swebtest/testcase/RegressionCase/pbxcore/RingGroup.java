package com.yeastar.swebtest.testcase.RegressionCase.pbxcore;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;

/**
 * 回归测试--响铃组功能
 * Created by AutoTest on 2017/10/16.
 */
public class RingGroup extends SwebDriver {
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======  RingGroup  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        ys_waitingMask();
        mySettings.close.click();
        m_extension.showCDRClounm();

    }

    @BeforeClass
    public void InitRingGroup() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        ys_waitingMask();
        ringGroup.ringGroup.click();
        ringGroup.add.shouldBe(Condition.exist);
        deletes(" 删除所有RingGroup",ringGroup.grid,ringGroup.delete,ringGroup.delete_yes,ringGroup.grid_Mask);
        Reporter.infoExec(" 添加RingGroup1：6200，选择分机1000,1100,1105，其它默认"); //执行操作
        m_callFeature.addRingGroup("RingGroup1","6200",add_ring_group.rs_ringall,1000,1100,1105);
    }

    @BeforeClass
    public void Register() throws InterruptedException {
        //        注册分机
//        被测设备注册分机1000、1100、1101、1102、1105，辅助1：分机3001，辅助2：分机2000
        pjsip.Pj_CreateAccount(1000,"Yeastar202","UDP",1);
        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",2);
        pjsip.Pj_CreateAccount(1101,"Yeastar202","UDP",3);
        pjsip.Pj_CreateAccount(1102,"Yeastar202","UDP",4);
        pjsip.Pj_CreateAccount(1105,"Yeastar202","UDP",7);
        pjsip.Pj_CreateAccount(3001,"Yeastar202","UDP",-1);
        pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1100,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1102,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1105,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
    }

//    新建响铃组
    @Test
    public void A_add_RingGroup1 () throws InterruptedException {
        Reporter.infoExec(" 新建RingGroup6201,Mem:ExtensionGroup1,其它默认"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ringGroup.add.shouldBe(Condition.exist);
        m_callFeature.addRingGroup("RingGroup6201","6201","","ExtensionGroup1");
        ys_apply();

//        通话验证
        Reporter.infoExec(" 1000拨打6201，分机1105接听");
        pjsip.Pj_Make_Call_No_Answer(1000,"6201",DEVICE_IP_LAN);
        if(getExtensionStatus(1100,RING,10)==RING &&
                getExtensionStatus(1101,RING,10)==RING &&
                getExtensionStatus(1105,RING,10)==RING){
            Reporter.pass(" 分机1100、1101、1105同时响铃");
            pjsip.Pj_Answer_Call(1105,true);
            ys_waitingTime(6000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000 <1000>","1105 <6201(1105)>","Answered","","",communication_internal);

        }else{
            YsAssert.fail(" 呼入响铃组6201失败");
        }
    }

    @Test
    public void B_edit_RingGroup1() throws InterruptedException {
        Reporter.infoExec(" 编辑RingGroup6201，成员响铃时间：15s，Failover：分机1102"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ringGroup.add.shouldBe(Condition.exist);
        gridClick(ringGroup.grid,gridFindRowByColumn(ringGroup.grid,ringGroup.gridcolumn_Name,"RingGroup6201",sort_ascendingOrder),ringGroup.gridEdit);
        add_ring_group.secondstoringeachmenmber.clear();
        add_ring_group.secondstoringeachmenmber.setValue("15");
        comboboxSelect(add_ring_group.failoverDestinationtype,s_extensin);
        comboboxSet(add_ring_group.failoverDestination,extensionList,"1102");
        add_ring_group.save.click();
        ys_waitingLoading(ringGroup.grid_Mask);
        ys_apply();

//        通话测试
        Reporter.infoExec(" 1000拨打6201，预期1100、1101、1105响铃15s后挂断，1102开始响铃");
        pjsip.Pj_Make_Call_No_Answer(1000,"6201",DEVICE_IP_LAN);
        ys_waitingTime(9000);
        if(getExtensionStatus(1100,RING,1)==RING){
            Reporter.pass(" 1100--Ring");
        }else{
            YsAssert.fail(" 预期1100状态为Ring");
        }
        if(getExtensionStatus(1101,RING,1)==RING){
            Reporter.pass(" 1101--Ring");
        }else{
            YsAssert.fail(" 预期1101状态为Ring");
        }
        if(getExtensionStatus(1105,RING,1)==RING){
            Reporter.pass(" 1105--Ring");
        }else{
            YsAssert.fail(" 预期1105状态为Ring");
        }
        if(getExtensionStatus(1102,IDLE,1)==IDLE){
            Reporter.pass(" 1102--Idle");
        }else{
            YsAssert.fail(" 预期1102状态为Idle");
        }
        ys_waitingTime(6000);
        if(getExtensionStatus(1102,RING,1)==RING){
            Reporter.pass(" 1102--Ring");
            Reporter.infoExec(" 1102接听");
            pjsip.Pj_Answer_Call(1102,true);
            ys_waitingTime(3000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000 <1000>","1102 <6201(1102)>","Answered","","",communication_internal);
        }else{
            YsAssert.fail(" 预期1105状态为Ring");
        }
    }

//    呼入到响铃组测试
    @Test
    public void C_add_RingGroup2() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ringGroup.add.shouldBe(Condition.exist);
        Reporter.infoExec(" 新建响铃组RingSequentially6202，Sequentially,每个成员20s,成员：1000、1100、1101、1105"); //执行操作
        m_callFeature.addRingGroup("RingSequentially6202","6202",add_ring_group.rs_sequentially,1000,1100,1101,1105);

//        编辑呼入路由Inroute1到RingSequentially6202
        Reporter.infoExec(" 编辑呼入路由Inroute1到RingSequentially6202");
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        inboundRoutes.add.shouldBe(Condition.exist);
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_inbound_route.destinationType,s_ringGroup);
        comboboxSet(add_inbound_route.destination,"name","RingSequentially6202");
        add_inbound_route.save.click();
        ys_waitingTime(1000);
        ys_apply();

//        通话测试
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到RingSequentially6202");
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        if(getExtensionStatus(1000,RING,10)==RING && getExtensionStatus(1000,HUNGUP,22)==HUNGUP){
            Reporter.pass(" 1000--Ring20s--Hungup");
            if (getExtensionStatus(1100,RING,10)==RING && getExtensionStatus(1100,HUNGUP,22)==HUNGUP){
                Reporter.pass(" 1100--Ring20s--Hungup");
                if (getExtensionStatus(1101,RING,10)==RING && getExtensionStatus(1101,HUNGUP,22)==HUNGUP){
                    Reporter.pass(" 1101--Ring20s--Hungup");
                    if (getExtensionStatus(1105,RING,10)==RING ){
                        Reporter.pass(" 1105--Ring--Answer");
                        Reporter.infoExec(" 轮到1105响铃的时候，1105接听");
                        pjsip.Pj_Answer_Call(1105,false);
                        ys_waitingTime(5000);
                        pjsip.Pj_Hangup_All();
                        m_extension.checkCDR("2000 <2000>","1105 <6202(1105)>","Answered",SPS," ",communication_inbound);
                    }else {
                        YsAssert.fail(" 预期：1105响铃20s后挂断");
                    }
                }else {
                    YsAssert.fail(" 预期：1101响铃20s后挂断");
                }
            }else {
                YsAssert.fail(" 预期：1100响铃20s后挂断");
            }
        }else{
            YsAssert.fail(" 预期：1000响铃20s后挂断");
        }

    }

//    删除测试
    @Test
    public void D_delete() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_tree.click();
        ringGroup.ringGroup.click();
        ringGroup.add.shouldBe(Condition.exist);
        Reporter.infoExec(" 表格删除：RingGroup6201-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(ringGroup.grid,ringGroup.gridcolumn_Name,"RingGroup6201",sort_ascendingOrder)));
        int rows=Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        gridClick(ringGroup.grid,row,ringGroup.gridDelete);
        ringGroup.delete_no.click();
        ys_waitingLoading(ringGroup.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：RingGroup6201-取消删除");

        Reporter.infoExec(" 表格删除：RingGroup6201-确定删除"); //执行操作
        gridClick(ringGroup.grid,row,ringGroup.gridDelete);
        ringGroup.delete_yes.click();
        ys_waitingLoading(ringGroup.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：RingGroup6201-确定删除");

        Reporter.infoExec(" 删除：RingSequentially6202-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(ringGroup.grid,ringGroup.gridcolumn_Name,"RingSequentially6202",sort_ascendingOrder)));
        gridCheck(ringGroup.grid,row4,ringGroup.gridcolumn_Check);
        ringGroup.delete.click();
        ringGroup.delete_no.click();
        ys_waitingLoading(ringGroup.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        System.out.println("row5:"+row5);
        YsAssert.assertEquals(row8,row5,"删除：RingSequentially6202-取消删除");

        Reporter.infoExec(" 删除：RingSequentially6202-确定删除"); //执行操作
        ringGroup.delete.click();
        ringGroup.delete_yes.click();
        ys_waitingLoading(ringGroup.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        System.out.println("row6:"+row6);
        int row7=row6+1;
        System.out.println("row7:"+row7);
        YsAssert.assertEquals(row5,row7,"删除：RingSequentially6202-确定删除");
        ys_apply();

    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
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
        Reporter.infoAfterClass("执行完毕：======  RingGroup  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);

    }
}
