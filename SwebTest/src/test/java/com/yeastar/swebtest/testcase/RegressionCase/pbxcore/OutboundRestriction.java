package com.yeastar.swebtest.testcase.RegressionCase.pbxcore;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import java.util.ArrayList;

import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;

/**
 * Created by AutoTest on 2017/10/12.
 */
public class OutboundRestriction extends SwebDriver {
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======  OutboundRestriction  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();

        //        被测设备注册分机1000，辅助1：分机3001，辅助2：分机2000
        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",UDP_PORT,2);
        pjsip.Pj_CreateAccount(1102,"Yeastar202","UDP",UDP_PORT,4);
        pjsip.Pj_CreateAccount(3001,"Yeastar202","UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_Register_Account(1100,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1102,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        outboundRestriction.outboundRestriction.click();
        outboundRestriction.add.should(Condition.exist);
    }

//    新建呼出限制
    @Test
    public void A_add_1() throws InterruptedException {
        deletes("  删除所有Outbound Restriction",outboundRestriction.grid,outboundRestriction.delete,outboundRestriction.delete_yes,outboundRestriction.grid_Mask);
        Reporter.infoExec(" 新建呼出限制OutRestriction1，2分钟不能超过5通");
        outboundRestriction.add.click();
        ys_waitingMask();
        add_outbound_restriction.name.setValue("OutRestriction1");
        add_outbound_restriction.timeLimit.setValue("2");
        add_outbound_restriction.save.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
        ys_apply();
        ys_waitingTime(5000);

//      通话测试
        for (int i=1;i<=8;i++) {
            pjsip.Pj_Make_Call_Auto_Answer(1100, "13001", DEVICE_IP_LAN, false);
            ys_waitingTime(10000);
            pjsip.Pj_Hangup_All();
            System.out.println(i);
//            呼出限制无法实时生效，大概要1分钟
            if(i==7) {
                ys_waitingTime(80000);
                YsAssert.assertEquals(getExtensionStatus(1100,HUNGUP,10),HUNGUP,"预期1100呼出失败");
            }
        }
//        m_extension.checkCDR("1100 <1100>","13001","Failed"," "," ",communication_internal);
    }

//    检查分机1100被限制呼出
    @Test
    public void A_add_2() throws InterruptedException {
        Reporter.infoExec(" 检查分机1100被限制呼出，取消呼出限制"); //执行操作
//        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ys_waitingTime(1000);
//        settings.extensions_panel.click();
        settings.extensions_tree.click();
        ys_waitingTime(5000);
        gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Name,"1100",sort_ascendingOrder),0);
        if(extensions.delete_yes.exists()){
            extensions.delete_yes.click();
            System.out.println("取消呼出限制");
            Reporter.pass(" 查看分机1100状态已被限制，并取消限制");
        }else {
            addExtensionBasic.cancel.click();
        }
        ys_waitingTime(5000);
    }

    @Test
    public void A_add_3() throws InterruptedException {
        Reporter.infoExec(" 分机1100取消呼出限制后能正常呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100, "13001", DEVICE_IP_LAN, false);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();

        m_extension.checkCDR("1100 <1100>","13001","Answered"," ",SIPTrunk,communication_outRoute);
    }

//    新建呼出限制，自定义分机
    @Test
    public void B_add_1() throws InterruptedException {
        Reporter.infoExec(" 新建呼出限制：OutRestriction2，分机1102，2分钟呼出不能超过2通"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_tree.click();
        outboundRestriction.outboundRestriction.click();
        outboundRestriction.add.shouldBe(Condition.exist);
        outboundRestriction.add.click();
        ys_waitingMask();
        add_outbound_restriction.name.setValue("OutRestriction2");
        add_outbound_restriction.timeLimit.setValue("2");
        add_outbound_restriction.numberofCallsLimit.setValue("2");
        add_outbound_restriction.selectExtensions.click();
        listSelect(add_outbound_restriction.list,extensionList,"1102");
        add_outbound_restriction.save.click();
        ys_apply();
        ys_waitingTime(5000);

//        通话测试
        for (int i=1;i<=5;i++) {
            pjsip.Pj_Make_Call_Auto_Answer(1102, "13001", DEVICE_IP_LAN, false);
            ys_waitingTime(10000);
            pjsip.Pj_Hangup_All();
            System.out.println(i);
//            呼出限制无法实时生效，大概要1分钟
            if(i==4) {
                ys_waitingTime(80000);
                YsAssert.assertEquals(getExtensionStatus(1102,HUNGUP,10),HUNGUP,"预期1102呼出失败");

            }
        }
//        m_extension.checkCDR("1102 <1102>","13001","Failed"," "," ",communication_internal);
    }

//    检查分机1102被限制呼出
    @Test
    public void B_add_2() throws InterruptedException {
        Reporter.infoExec(" 检查分机1102被限制呼出，取消呼出限制"); //执行操作
        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ys_waitingTime(1000);
        settings.extensions_panel.click();
        ys_waitingTime(5000);
        gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Name,"1102",sort_ascendingOrder),0);
        if(extensions.delete_yes.exists()){
            extensions.delete_yes.click();
            System.out.println("取消呼出限制");
            Reporter.pass(" 查看分机1102状态已被限制，并取消限制");
        }else {
            addExtensionBasic.cancel.click();
        }
        ys_waitingTime(5000);
    }

    @Test
    public void B_add_3() throws InterruptedException {
        Reporter.infoExec(" 分机1102取消呼出限制后能正常呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1102, "13001", DEVICE_IP_LAN, false);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();

        m_extension.checkCDR("1102 <1102>","13001","Answered"," ",SIPTrunk,communication_outRoute);

    }

//    删除
    @Test
    public void C_delete() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_tree.click();
        outboundRestriction.outboundRestriction.click();
        outboundRestriction.add.shouldBe(Condition.exist);
        setPageShowNum(outboundRestriction.grid,25);
        Reporter.infoExec(" 表格删除：OutRestriction1-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRestriction.grid,outboundRestriction.gridcolumn_Name,"OutRestriction1",sort_ascendingOrder)));
        int rows=Integer.parseInt(String.valueOf(gridLineNum(outboundRestriction.grid)));
        gridClick(outboundRestriction.grid,row,outboundRestriction.gridDelete);
        outboundRestriction.delete_no.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(outboundRestriction.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：OutRestriction1-取消删除");

        Reporter.infoExec(" 表格删除：OutRestriction1-确定删除"); //执行操作
        gridClick(outboundRestriction.grid,row,outboundRestriction.gridDelete);
        outboundRestriction.delete_yes.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(outboundRestriction.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：OutRestriction1-确定删除");

        Reporter.infoExec(" 删除：OutRestriction2-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(outboundRestriction.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRestriction.grid,outboundRestriction.gridcolumn_Name,"OutRestriction2",sort_ascendingOrder)));
        gridCheck(outboundRestriction.grid,row4,outboundRestriction.gridcolumn_Check);
        outboundRestriction.delete.click();
        outboundRestriction.delete_no.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(outboundRestriction.grid)));
        System.out.println("row5:"+row5);
        YsAssert.assertEquals(row8,row5,"删除：OutRestriction2-取消删除");

        Reporter.infoExec(" 删除：OutRestriction2-确定删除"); //执行操作
        outboundRestriction.delete.click();
        outboundRestriction.delete_yes.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(outboundRestriction.grid)));
        System.out.println("row6:"+row6);
        int row7=row6+1;
        System.out.println("row7:"+row7);
        YsAssert.assertEquals(row5,row7,"删除：OutRestriction2-确定删除");
        ys_apply();
    }
    
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：======  OutboundRestriction  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);

    }
}
