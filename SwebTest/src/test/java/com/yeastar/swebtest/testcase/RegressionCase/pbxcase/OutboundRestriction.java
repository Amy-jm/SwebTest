package com.yeastar.swebtest.testcase.RegressionCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.jcraft.jsch.JSchException;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;

import com.yeastar.swebtest.tools.ysassert.YsAssert;
import com.yeastar.untils.AllureReporterListener;
import com.yeastar.untils.RetryListener;
import com.yeastar.untils.SSHLinuxUntils;
import com.yeastar.untils.TestNGListener;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.*;

import java.io.IOException;

/**
 * Created by AutoTest on 2017/10/12.
 */
@Listeners({AllureReporterListener.class, RetryListener.class, TestNGListener.class})
@Log4j2
public class OutboundRestriction extends SwebDriver {
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        Reporter.infoBeforeClass("开始执行：======  OutboundRestriction  ======"); //执行操作
        //取消分机注册并重启设备
        try {
            if (DEVICE_ASSIST_1 != null) {
                log.debug("start unregistrar and reboot device 1 :"+DEVICE_ASSIST_1);
                SSHLinuxUntils.exeCommand(DEVICE_ASSIST_1, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, PJSIP_COMMAND_DELTREE_REGISTRAR);
                ys_waitingTime(3000);
                SSHLinuxUntils.exeCommand(DEVICE_ASSIST_1, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, PJSIP_COMMAND_reboot);
            }

            if (DEVICE_ASSIST_2 != null) {
                log.debug("start unregistrar and reboot device 2 :"+DEVICE_ASSIST_2);
                SSHLinuxUntils.exeCommand(DEVICE_ASSIST_2, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, PJSIP_COMMAND_DELTREE_REGISTRAR);
                ys_waitingTime(3000);
                SSHLinuxUntils.exeCommand(DEVICE_ASSIST_2, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, PJSIP_COMMAND_reboot);
            }
        } catch (JSchException e) {
            log.error("SSH error" + e.getMessage()+e.getStackTrace());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ys_waitingTime(90000);
        log.debug("END-[取消分机注册并重启设备]");

        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC)&& Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }
    @Test(priority = 0)
    public void A0_init(){
        pjsip.Pj_Init();
        //        被测设备注册分机1000，辅助1：分机3001，辅助2：分机2000
        pjsip.Pj_CreateAccount(1100,EXTENSION_PASSWORD,"UDP",UDP_PORT,2);
        pjsip.Pj_CreateAccount(1102,EXTENSION_PASSWORD,"UDP",UDP_PORT,4);
        pjsip.Pj_CreateAccount(3001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
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
    @Test(priority =1 )
    public void A1_add_1_all() throws InterruptedException {
        deletes("  删除所有Outbound Restriction",outboundRestriction.grid,outboundRestriction.delete,outboundRestriction.delete_yes,outboundRestriction.grid_Mask);
        Reporter.infoExec(" 新建呼出限制OutRestriction1，2分钟不能超过5通");
        outboundRestriction.add.click();
        ys_waitingMask();
        add_outbound_restriction.name.setValue("OutRestriction1");
        add_outbound_restriction.timeLimit.setValue("2");
        add_outbound_restriction.numberofCallsLimit.setValue("5");
        ys_waitingTime(1000);
        add_outbound_restriction.save.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
        ys_apply();
        ys_waitingTime(5000);

//      通话测试
        for (int i=1;i<=7;i++) {
            if(i==7) {
                ys_waitingTime(80000);
            }
            pjsip.Pj_Make_Call_Auto_Answer(1100, "13001", DEVICE_IP_LAN, false);
            ys_waitingTime(8000);
            System.out.println("=============================第"+i+"次循环打电话========================");
            System.out.println("1100的通话状态："+getExtensionStatus(1100,TALKING,1));
//            呼出限制无法实时生效，大概要1分钟
            if(i==7) {
                YsAssert.assertEquals(getExtensionStatus(1100,HUNGUP,10),HUNGUP,"预期第"+i+"通电话1100呼出失败");
            }else {
                YsAssert.assertEquals(getExtensionStatus(1100,TALKING,10),TALKING,"预期第"+i+"通电话1100呼出成功");
            }
            pjsip.Pj_Hangup_All();
        }
//        m_extension.checkCDR("1100 <1100>","13001","Failed"," "," ",communication_internal);
    }

//    检查分机1100被限制呼出
    @Test(priority = 2)
    public void A_add_2_cancel() throws InterruptedException {
        Reporter.infoExec(" 检查分机1100被限制呼出，取消呼出限制"); //执行操作
//        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ys_waitingTime(1000);
//        settings.extensions_panel.click();
        settings.extensions_tree.click();
        ys_waitingTime(5000);
        gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Name,"1100",sort_ascendingOrder),0);
        if(extensions.delete_yes.isDisplayed()){
            extensions.delete_yes.click();
            System.out.println("取消呼出限制");
            Reporter.pass(" 查看分机1100状态已被限制，并取消限制");
        }else {
            addExtensionBasic.cancel.click();
            YsAssert.fail("预期分机1100状态被限制");
        }
        ys_waitingTime(5000);
    }

    @Test(priority = 3)
    public void A_add_3_cancelCall() throws InterruptedException {
        Reporter.infoExec(" 分机1100取消呼出限制后能正常呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100, "13001", DEVICE_IP_LAN, false);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","13001","Answered"," ",SIPTrunk,communication_outRoute);
    }

//    新建呼出限制，自定义分机
    @Test(priority = 4)
    public void B_add_1_exten1102() throws InterruptedException {
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
        for (int i=1;i<=4;i++) {
            if(i==4) {
                ys_waitingTime(80000);
            }
            pjsip.Pj_Make_Call_Auto_Answer(1102, "13001", DEVICE_IP_LAN, false);
            ys_waitingTime(10000);
            System.out.println("=============================第"+i+"次循环打电话========================");
            System.out.println("1100的通话状态："+getExtensionStatus(1102,TALKING,1));
//            呼出限制无法实时生效，大概要1分钟
            if(i==4) {
                YsAssert.assertEquals(getExtensionStatus(1102,HUNGUP,10),HUNGUP,"预期第"+i+"通电话1102呼出失败");
            }else {
                YsAssert.assertEquals(getExtensionStatus(1102,TALKING,10),TALKING,"预期第"+i+"通电话1102呼出成功");
            }
            pjsip.Pj_Hangup_All();
        }
        m_extension.checkCDR("1102 <1102>","13001","Failed"," "," ",communication_internal);
    }

//    检查分机1102被限制呼出
    @Test(priority = 5)
    public void B_add_2_exten1102_cancel() throws InterruptedException {
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

    @Test(priority = 6)
    public void B_add_3_exten1102_cancelCall() throws InterruptedException {
        Reporter.infoExec(" 分机1102取消呼出限制后能正常呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1102, "13001", DEVICE_IP_LAN, false);
        ys_waitingTime(10000);
        YsAssert.assertEquals(getExtensionStatus(1102,TALKING,10),TALKING,"预期1102为Talking状态");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1102 <1102>","13001","Answered"," ",SIPTrunk,communication_outRoute);
    }

//    删除
    @Test(priority = 7)
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

    @AfterMethod
    public void AfterMethod(){
        if(cdRandRecordings.deleteCDR.isDisplayed()){
            closeCDRRecord();
        }
    }
    
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：======  OutboundRestriction  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        ys_waitingTime(10000);
        killChromePid();

    }
}
