package com.yeastar.swebtest.testcase.RegressionCase.pbxcore;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;

/**
 * Created by AutoTest on 2017/10/17.
 */
public class SpeedDial extends SwebDriver {
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======  SpeedDial  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();

        //        被测设备注册分机1000，辅助1：分机3001，辅助2：分机2000
        pjsip.Pj_CreateAccount(1000,"Yeastar202","UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(3001,"Yeastar202","UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

    }

    @Test
    public void A_add_1() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        ys_waitingMask();
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
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","13001","Answered"," ",SIPTrunk,communication_outRoute);

    }

    @Test
    public void A_add_2() throws InterruptedException {
        Reporter.infoExec(" 1000拨打*9991234567通过sps外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*9991234567",DEVICE_IP_LAN,false);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","31234567","Answered"," ",SPS,communication_outRoute);
    }

//    导出、导入
    @Test
    public void B_export() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        Reporter.infoExec(" 导出速拨码"); //执行操作
        speedDial.export.click();
    }

    @Test
    public void C_delete() throws InterruptedException {
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

    @Test
    public void D_import() throws InterruptedException {
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


    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：======  SpeedDial  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);

    }
}
