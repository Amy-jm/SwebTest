package com.yeastar.swebtest.testcase.RegressionCase.pbxcore;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;

/**
 * Created by AutoTest on 2017/10/16.
 */
public class PickupGroup extends SwebDriver{

    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======  PickupGroup  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);

        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.general_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
        }
        m_general.setPickup(true,"*4",true,"*04");

        //        被测设备注册分机1000/1100/1105，辅助1：分机3001
        pjsip.Pj_CreateAccount(1000,"Yeastar202","UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",UDP_PORT,2);
        pjsip.Pj_CreateAccount(1105,"Yeastar202","UDP",UDP_PORT,7);
        pjsip.Pj_CreateAccount(3001,"Yeastar202","UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1100,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1105,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
    }


//内部分机互打截答、指定截答
    @Test
    public void A_add_pickupgroup1() throws InterruptedException {

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_tree.click();
        pickupGroup.pickupGroup.click();
        ys_waitingMask();
        deletes(" 删除所有PickupGroup",pickupGroup.grid,pickupGroup.delete,pickupGroup.delete_yes,pickupGroup.grid_Mask);

//        新建PickupGroup
        Reporter.infoExec(" 新建截答组PickupGroup1,成员分机1000、1105");
        m_callFeature.addPickupGroup("PickupGroup1",1000,1105);
        ys_apply();

//        通话验证
        Reporter.infoExec(" 1100拨打1000,1105按*4截答--预期截答成功");
        pjsip.Pj_Make_Call_No_Answer(1100,"1000",DEVICE_IP_LAN);
        System.out.println("截答状态：：："+getExtensionStatus(1000,RING,10));
        if (getExtensionStatus(1000,RING,10)==RING) {
            System.out.println(" -----------开始按*4---------");
            pjsip.Pj_Make_Call_Auto_Answer(1105, "*4", DEVICE_IP_LAN, false);
        }else {
            System.out.println("------------1000未响铃，未执行*4---------");
        }
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","1105 <1105(pickup 1000)>","Answered","","",communication_internal);

    }

    @Test
    public void A_add_pickupgroup2() throws InterruptedException {
        Reporter.infoExec(" 1000拨打1100,1105按*4截答--预期截答失败");
        pjsip.Pj_Make_Call_No_Answer(1000,"1100",DEVICE_IP_LAN);
        if (getExtensionStatus(1100,RING,10)==RING) {
            System.out.println(" -----------开始按*4---------");
            pjsip.Pj_Make_Call_Auto_Answer(1105, "*4", DEVICE_IP_LAN, false);
        }
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1100 <1100>","No Answer","","",communication_internal);

    }

    @Test
    public void A_add_pickupgroup3() throws InterruptedException {
        Reporter.infoExec(" 1000拨打1100,1105按*041100截答--预期截答成功");
        pjsip.Pj_Make_Call_No_Answer(1000,"1100",DEVICE_IP_LAN);
        if (getExtensionStatus(1100,RING,10)==RING) {
            System.out.println(" -----------开始按*04---------");
            pjsip.Pj_Make_Call_Auto_Answer(1105, "*041100", DEVICE_IP_LAN, false);
        }
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <1105(pickup 1100)>","Answered","","",communication_internal);

    }


//    分机组、外线呼入截答
    @Test
    public void B_add_pickupgroup2() throws InterruptedException {
        Reporter.infoExec(" 新建截答组PickupExGroup,成员：ExtensionGroup1"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        pickupGroup.add.shouldBe(Condition.exist);
        m_callFeature.addPickupGroup("PickupExGroup","ExtensionGroup1");
        ys_apply();

//        通话验证
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入--预期分机1000响铃");
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1);
        if (getExtensionStatus(1000,RING,10)==RING){
            Reporter.infoExec(" 1105按*4截答--预期截答成功");
            pjsip.Pj_Make_Call_Auto_Answer(1105, "*4", DEVICE_IP_LAN, false);
        }
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1105 <1105(pickup 1000)>","Answered",SIPTrunk,"",communication_inbound);
    }

//    删除
    @Test
    public void C_delete() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        pickupGroup.pickupGroup.click();
        pickupGroup.add.shouldBe(Condition.exist);
        Reporter.infoExec(" 表格删除：PickupGroup1-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(pickupGroup.grid,pickupGroup.gridcolumn_Name,"PickupGroup1",sort_ascendingOrder)));
        int rows=Integer.parseInt(String.valueOf(gridLineNum(pickupGroup.grid)));
        gridClick(pickupGroup.grid,row,pickupGroup.gridDelete);
        pickupGroup.delete_no.click();
        ys_waitingLoading(pickupGroup.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(pickupGroup.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：PickupGroup1-取消删除");

        Reporter.infoExec(" 表格删除：PickupGroup1-确定删除"); //执行操作
        gridClick(pickupGroup.grid,row,pickupGroup.gridDelete);
        pickupGroup.delete_yes.click();
        ys_waitingLoading(pickupGroup.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(pickupGroup.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：PickupGroup1-确定删除");

        Reporter.infoExec(" 删除：PickupExGroup-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(pickupGroup.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(pickupGroup.grid,pickupGroup.gridcolumn_Name,"PickupExGroup",sort_ascendingOrder)));
        gridCheck(pickupGroup.grid,row4,pickupGroup.gridcolumn_Check);
        pickupGroup.delete.click();
        pickupGroup.delete_no.click();
        ys_waitingLoading(pickupGroup.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(pickupGroup.grid)));
        System.out.println("row5:"+row5);
        YsAssert.assertEquals(row8,row5,"删除：PickupExGroup-取消删除");

        Reporter.infoExec(" 删除：PickupExGroup-确定删除"); //执行操作
        pickupGroup.delete.click();
        pickupGroup.delete_yes.click();
        ys_waitingLoading(pickupGroup.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(pickupGroup.grid)));
        System.out.println("row6:"+row6);
        int row7=row6+1;
        System.out.println("row7:"+row7);
        YsAssert.assertEquals(row5,row7,"删除：PickupExGroup-确定删除");
        ys_apply();
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：======  PickupGroup  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);

    }
}
