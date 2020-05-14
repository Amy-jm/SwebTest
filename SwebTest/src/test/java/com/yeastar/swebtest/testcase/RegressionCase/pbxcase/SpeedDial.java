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
 * Created by AutoTest on 2017/10/17.
 */
@Listeners({AllureReporterListener.class, RetryListener.class, TestNGListener.class})
@Log4j2
public class SpeedDial extends SwebDriver {
    @BeforeClass
    public void BeforeClass()  {

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

        Reporter.infoBeforeClass("开始执行：======  SpeedDial  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();


    }
    @Test(priority = 0)
    public void A0_init(){
        pjsip.Pj_Init();
        //        被测设备注册分机1000，辅助1：分机3001，辅助2：分机2000
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(3001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
    }
    @Test(priority = 1)
    public void A_add_1()  {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
        }else{
            ys_waitingTime(5000);
        }
        callFeatures.more.click();
        speedDial.speedDial.click();
        ys_waitingLoading(speedDial.grid_Mask);
        deletes(" 删除所有速拨码",speedDial.grid,speedDial.delete,speedDial.delete_yes,speedDial.grid_Mask);

        Reporter.infoExec(" 设置速拨特征码为*999");
        speedDial.speedDialPrefix.clear();
        speedDial.speedDialPrefix.setValue("*999");
        speedDial.speedDialPrefix_button.click();
        Reporter.infoExec(" 新建速拨码：dialcode:1,PhoneNumber：13001");
        m_callFeature.addSpeedDial("1",13001);
        Reporter.infoExec(" 新建速拨码：dialcode:1234567,PhoneNumber：31234567"); //执行操作
        m_callFeature.addSpeedDial("1234567",31234567);
        ys_apply();

//        通话测试
        Reporter.infoExec(" 1000拨打*9991通过sip外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*9991",DEVICE_IP_LAN,false);
        ys_waitingTime(10000);
        YsAssert.assertEquals(getExtensionStatus(3001,TALKING,10),TALKING,"预期3001为Talking状态");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","13001","Answered"," ",SIPTrunk,communication_outRoute);

    }

    @Test(priority = 2)
    public void A_add_2()  {
        Reporter.infoExec(" 1000拨打*9991234567通过sps外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*9991234567",DEVICE_IP_LAN,false);
        ys_waitingTime(10000);
        YsAssert.assertEquals(getExtensionStatus(2000,TALKING,10),TALKING,"预期2000为Talking状态");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","31234567","Answered"," ",SPS,communication_outRoute);
    }

//    导出、导入
    @Test(priority = 3)
    public void B_export()  {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        Reporter.infoExec(" 导出速拨码"); //执行操作
        speedDial.export.click();
    }

    @Test(priority = 4)
    public void C_delete()  {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        speedDial.speedDial.click();
        speedDial.add.shouldBe(Condition.exist);
        Reporter.infoExec(" 表格删除：速拨码为1-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(speedDial.grid,speedDial.gridcolumn_SpeedDialCode,"1",sort_ascendingOrder)));
        int rows=Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid)));
        gridClick(speedDial.grid,row,speedDial.gridDelete);
        speedDial.delete_no.click();
        ys_waitingLoading(speedDial.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：速拨码为1-取消删除");

        Reporter.infoExec(" 表格删除：速拨码为1-确定删除"); //执行操作
        gridClick(speedDial.grid,row,speedDial.gridDelete);
        speedDial.delete_yes.click();
        ys_waitingLoading(speedDial.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：速拨码为1-确定删除");

        Reporter.infoExec(" 删除：速拨码为1234567-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(speedDial.grid,speedDial.gridcolumn_SpeedDialCode,"1234567",sort_ascendingOrder)));
        gridCheck(speedDial.grid,row4,speedDial.gridcheck);
        speedDial.delete.click();
        speedDial.delete_no.click();
        ys_waitingLoading(speedDial.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid)));
        System.out.println("row5:"+row5);
        YsAssert.assertEquals(row8,row5,"删除：速拨码为1234567-取消删除");

        Reporter.infoExec(" 删除：速拨码为1234567-确定删除"); //执行操作
        speedDial.delete.click();
        speedDial.delete_yes.click();
        ys_waitingLoading(speedDial.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid)));
        System.out.println("row6:"+row6);
        int row7=row6+1;
        System.out.println("row7:"+row7);
        YsAssert.assertEquals(row5,row7,"删除：速拨码为1234567-确定删除");
        ys_apply();

    }

    @Test(priority = 5)
    public void D_import()  {
        Reporter.infoExec(" 导入速拨码"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        speedDial.Import.click();
        import_speed_dial_number.browse.click();
        ys_waitingTime(2000);
        importFile(EXPORT_PATH +"RegressionSpeeddial.csv");
        ys_waitingTime(2000);
        import_speed_dial_number.Import.click();
        import_speed_dial_number.ImportOK.click();
        YsAssert.assertEquals(String.valueOf(gridLineNum(speedDial.grid)),"2","导入速拨码");

    }

    @AfterMethod
    public void AfterMethod(){
        if(cdRandRecordings.deleteCDR.isDisplayed()){
            closeCDRRecord();
        }
    }

    @AfterClass
    public void AfterClass()  {
        ys_waitingTime(5000);
        Reporter.infoAfterClass("执行完毕：======  SpeedDial  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        ys_waitingTime(10000);
        killChromePid();

    }
}
