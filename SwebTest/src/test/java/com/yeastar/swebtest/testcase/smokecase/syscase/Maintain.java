package com.yeastar.swebtest.testcase.smokecase.syscase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yeastar on 2017/8/18.
 */
public class Maintain extends SwebDriver {
    int backUpRow = 0;
    @BeforeClass
    public void BeforeClass() throws InterruptedException {

        Reporter.infoBeforeClass("打开游览器并登录设备_Maintain"); //执行操作
        initialDriver(BROWSER,"http://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.shouldBe(Condition.exist);
        mySettings.close.click();
        m_extension.showCDRClounm();
    }
    @Test
    public void A_BackupRestore() {
        Reporter.infoExec("添加备份文件"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.maintanceShortcut.click();
        maintenance.backupandRestore.click();
        backupandRestore.backup.click();
        ys_waitingTime(5000);
        create_new_backup_file.fileName.setValue("FileName");
        create_new_backup_file.save.click();

        backUpRow= gridFindRowByColumn(backupandRestore.grid,backupandRestore.gridColumn_Name,"FileName_Local.bak",sort_ascendingOrder);

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        System.out.println("当前时间：" + String.valueOf(sdf.format(d))+"   "+backUpRow);

        YsAssert.assertEquals(String.valueOf(gridContent(backupandRestore.grid,backUpRow,backupandRestore.gridColumn_Name)) ,"FileName_Local.bak");
        YsAssert.assertEquals(String.valueOf(gridContent(backupandRestore.grid,backUpRow,backupandRestore.gridColumn_BackUpTime)).substring(0,13) ,String.valueOf(sdf.format(d)).substring(0,13));
    }
    @Test
    public void B_DownloadBackUptar() {
        Reporter.infoExec("下载备份包"); //执行操作
        gridClick(backupandRestore.grid,backUpRow,backupandRestore.gridDownload);
    }

    public void C_Reset() {
        Reporter.infoExec("点击“Reset”，弹出的Reset页面的Verification Code输入图片的验证码，点击“Reset”"); //执行操作
//      1）设备重置，网页上出现重启进度页面。重启后，设备恢复出厂设置
        if(Single_Device_Test){
            pageDeskTop.taskBar_Main.click();     pageDeskTop.maintanceShortcut.click();
        }
        maintenance.reset.click();
        reset.reset.click();
        String resetcode = String.valueOf(reset.resetCode.getText());
        System.out.println("resetCode :"+resetcode);
        reset.resetInputCode.setValue(resetcode);
        reset.startReset.click();
    }

    public void D_Restore() {
        Reporter.infoExec("导入备份文件"); //执行操作
        if(Single_Device_Test){
            pageDeskTop.taskBar_Main.click();     pageDeskTop.maintanceShortcut.click();
        }
        maintenance.backupandRestore.click();
        gridClick(backupandRestore.grid,backUpRow,backupandRestore.gridRestroe);
        backupandRestore.delete_yes.click();
        pageDeskTop.reboot_Yes.click();
        waitReboot();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
    }
    @Test
    public void E_DeleteBackUp() {
        Reporter.infoExec("删除备份文件"); //执行操作
        pageDeskTop.taskBar_Main.click();     pageDeskTop.maintanceShortcut.click();
        maintenance.backupandRestore.click();
        ys_waitingLoading(backupandRestore.grid_Mask);
        gridClick(backupandRestore.grid,backUpRow,backupandRestore.gridDelete);
        backupandRestore.delete_yes.click();
        ys_apply();
    }
    @Test
    public void F_UploadBackUp() {
        Reporter.infoExec("点击“Upload”，选择刚下载的备份包，点击“Upload”"); //执行操作

    }
//    @Test
    public void G_DeleteAll() {
        Reporter.infoExec("点击“Delete”，弹出的提示页面，点击yes"); //执行操作
        gridSeleteAll(backupandRestore.grid);
        backupandRestore.delete.click();
        backupandRestore.delete_yes.click();
    }
    @Test
    public void H_BackupSchedule() {
        Reporter.infoExec("设置BackupSchedule"); //执行操作
        backupandRestore.backupSchedule.click();
        backup_schedule.enableScheduleBackup.click();
//        Ext.getCmp('mt-br-backuptimetime').setValue('11')
        ys_waitingTime(5000);
        executeJs("Ext.getCmp('"+backup_schedule.timeTime_id+"').setValue('12')");
        executeJs("Ext.getCmp('"+backup_schedule.locationType_id+"').setValue('"+backup_schedule.SDTF+"')");
        backup_schedule.save.click();
    }
//    @Test
    public void J_Reboot() {
        Reporter.infoExec("Reboot"); //执行操作
        maintenance.reboot.click();
        reboot.reboot.click();
        reboot.reboot_Yes.click();

        waitReboot();
    }
//    @Test
//    public void K_SystemLog() {
//        Reporter.infoExec("执行的操作"); //执行操作
////        1）Log Level勾选Informatica、Notice、Warning、Error和Debug，点击“Save”并点击“Apply”
////        2）System log列表中Name为当前日期的，点击“Download”图标，
//        maintenance.systemLog.click();
//        setCheckBox(systemLog.information_id,true);
//        setCheckBox(systemLog.notice_id,true);
//        setCheckBox(systemLog.warning_id,true);
//        setCheckBox(systemLog.error_id,true);
//        setCheckBox(systemLog.debug_id,true);
//
//        systemLog.save.click();
//        ys_apply();
//    }
    @Test
    public void L_DeleteSystemLog() {
        Reporter.infoExec("选择当前日期的系前一天的系统日志,删除"); //执行操作
        maintenance.systemLog.click();
        String line = String.valueOf(gridLineNum(systemLog.grid)) ;
        if(Integer.parseInt(line) <= 1){

        }else {
            String deleteName = String.valueOf(gridContent(systemLog.grid,Integer.parseInt(line),systemLog.gridColumn_Name));
            gridClick(systemLog.grid,Integer.parseInt(line),systemLog.gridDelete);
            systemLog.delete_yes.click();
            line = String.valueOf(gridLineNum(systemLog.grid));
            if(Integer.parseInt(line)>1){
                YsAssert.assertNotEquals(String.valueOf(gridContent(systemLog.grid,Integer.parseInt(line),systemLog.gridColumn_Name)),deleteName);
            }else {
                //删除成功
            }
        }
    }
    @Test
    public void M_DownloadSystemLog() {
        Reporter.infoExec("下载全部日志"); //执行操作
        gridSeleteAll(systemLog.grid);
        systemLog.download.click();

    }
    @Test
    public void N_DeleteAllSystemLog() {
        Reporter.infoExec("删除全部系统日志"); //执行操作
        gridSeleteAll(systemLog.grid);
        systemLog.delete.click();
        systemLog.delete_yes.click();
        YsAssert.assertEquals(String.valueOf(gridLineNum(systemLog.grid)),"1");
    }
    @Test
    public void O_OperateLog() {
        Reporter.infoExec("添加一个分机，检测操作日志"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.extensions_panel.click();
        m_extension.addSipExtension(4000,EXTENSION_PASSWORD);
        ys_apply();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.maintanceShortcut.click();
        maintenance.operationLog.click();
        ys_waitingTime(5000);
        String user = String.valueOf(gridContent(operationLog.grid,1,operationLog.gridColumn_User));
        String operation = String.valueOf(gridContent(operationLog.grid,1,operationLog.gridColumn_Operation));
        String detials = String.valueOf(gridContent(operationLog.grid,1,operationLog.gridColumn_Details));
        System.out.println("user "+user+" operation "+operation+ " detials "+detials);
        YsAssert.assertEquals(user,LOGIN_USERNAME,"操作日志User");
//        YsAssert.assertEquals(operation,"Extensions: Add");
        YsAssert.assertInclude(detials,"Extension: 4000");
    }
    @Test
    public void Z_RecoverHttps() {
        Reporter.infoExec("网页URL恢复成Https"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        if(Single_Device_Test){
            settings.extensions_panel.click();
        }
        ys_waitingLoading(extensions.grid_Mask);
        setPageShowNum(extensions.grid,100);
        ys_waitingLoading(extensions.grid_Mask);
        int row= gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Extensions,"4000",sort_descendingOrder);
        if(row!=0){
            gridClick(extensions.grid,row,extensions.gridDelete);
            extensions.delete_yes.click();
        }


        settings.system_tree.doubleClick();
//        settings.security_panel.click();
        settings.security_tree.click();
        service.service.click();
        ys_waitingTime(6666);
        comboboxSelect(service.protocol,service.HTTPS);
        service.save.click();

    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("关闭游览器"); //执行操作

        quitDriver();
        Thread.sleep(10000);
    }
}
