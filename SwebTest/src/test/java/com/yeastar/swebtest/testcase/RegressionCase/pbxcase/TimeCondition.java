package com.yeastar.swebtest.testcase.RegressionCase.pbxcase;

import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;



/**
 * Created by AutoTest on 2017/9/28.
 */
public class TimeCondition extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======  时间条件—TimeCondition  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
    }

//    添加时间条件
    @Test
    public void addTimeCondition1() throws InterruptedException {
        settings.callControl_panel.click();
        time_Conditions.timeConditions.click();
        Reporter.infoExec(" 删除所有时间条件");
        deletes("删除所有时间条件",timeConditions.grid,timeConditions.delete,timeConditions.delete_yes,timeConditions.grid_Mask);

        Reporter.infoExec(" 添加时间条件workday_24hour:每天24小时都是工作时间"); //执行操作
        m_callcontrol.addTimeContion("workday_24hour","00:00","23:59",false,"all");
        Reporter.infoExec(" 添加时间条件workday_test：每天05:05~22:39"); //执行操作
        m_callcontrol.addTimeContion("workday_test","05:05","22:39",false,"all");
        Reporter.infoExec(" 添加时间条件Outbound：00:00-00:01,周日");
        m_callcontrol.addTimeContion("Outbound","00:00","00:01",false,"sun");
    }

    @Test
    public void addTimeCondition2() throws InterruptedException {
        Reporter.infoExec(" 添加时间条件workday_Advanced:周日/二/四，12:30~18:45"); //执行操作
        m_callcontrol.addTimeContion("workday_Advanced","12:30","18:45",false,"sun","tue","thu");
        Reporter.infoExec(" 编辑workday_Advanced，启用高级设置，1/6/12月的1/10/20/31号");
        gridClick(timeConditions.grid,Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"workday_Advanced",sort_ascendingOrder))),timeConditions.gridEdit);
        add_time_condition.advancedOptions.click();
        add_time_condition.january.click();
        add_time_condition.june.click();
        add_time_condition.december.click();
        add_time_condition.day1.click();
        add_time_condition.day10.click();
        add_time_condition.day20.click();
        add_time_condition.day31.click();
        add_time_condition.save.click();
        int row = gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"workday_Advanced",sort_ascendingOrder);
        YsAssert.assertEquals(gridContent(timeConditions.grid,row,timeConditions.gridcolumn_Month),"Jan,Jun,Dec","月份编辑");
        YsAssert.assertEquals(gridContent(timeConditions.grid,row,timeConditions.gridcolumn_Day),"1,10,20,31","日期编辑");
    }
    @Test
    public void addTimeCondition3() throws InterruptedException {
        Reporter.infoExec(" 添加时间条件CheckAll"); //执行操作
        m_callcontrol.addTimeContion("CheckAll","00:00","23:59",false,"all");
        Reporter.infoExec(" 编辑CheckAl，启用高级设置，Month:All，Day:All");
        gridClick(timeConditions.grid,Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"CheckAll",sort_ascendingOrder))),timeConditions.gridEdit);
        add_time_condition.advancedOptions.click();
        add_time_condition.all_month.click();
        add_time_condition.all_day.click();
        add_time_condition.save.click();
        int row = gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"CheckAll",sort_ascendingOrder);
        YsAssert.assertEquals(gridContent(timeConditions.grid,row,timeConditions.gridcolumn_Dayofweek),"All","Week编辑页面显示检查");
        YsAssert.assertEquals(gridContent(timeConditions.grid,row,timeConditions.gridcolumn_Month),"All","Month编辑页面显示检查");
        YsAssert.assertEquals(gridContent(timeConditions.grid,row,timeConditions.gridcolumn_Day),"All","Day编辑页面显示检查");
    }

//    测试删除按钮正常使用
    @Test
    public void addTimeCondition4() {
//        settings.callControl_panel.click();
//        time_Conditions.timeConditions.click();
        Reporter.infoExec(" 表格删除：workday_Advanced，取消删除"); //执行操作
        int rows = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        gridClick(timeConditions.grid,Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"workday_Advanced",sort_ascendingOrder))),timeConditions.gridDelete);
        timeConditions.delete_no.click();
        ys_waitingLoading(timeConditions.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        YsAssert.assertEquals(row1,rows,"表格删除：workday_Advanced，取消删除");

        Reporter.infoExec(" 表格删除：workday_Advanced，确定删除"); //执行操作
        gridClick(timeConditions.grid,Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"workday_Advanced",sort_ascendingOrder))),timeConditions.gridDelete);
        timeConditions.delete_yes.click();
        ys_waitingLoading(timeConditions.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        System.out.println(row2);
        int row3=row2+1;
        YsAssert.assertEquals(row3,rows,"表格删除：workday_Advanced，确定删除");

        Reporter.infoExec(" 删除：workday_test，取消删除"); //执行操作
        int xx=Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"workday_test",sort_ascendingOrder)));
        System.out.println(xx);
        gridCheck(timeConditions.grid,Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"workday_test",sort_ascendingOrder))),timeConditions.gridcolumn_Check);

        timeConditions.delete.click();
        timeConditions.delete_no.click();
        ys_waitingLoading(timeConditions.grid_Mask);
        int row4 =Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        YsAssert.assertEquals(row4,row2,"删除：workday_test，取消删除");

        Reporter.infoExec(" 删除：workday_test，确定删除"); //执行操作
//        gridCheck(timeConditions.grid,Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"workday_test",sort_ascendingOrder))),timeConditions.gridcolumn_Check);
        timeConditions.delete.click();
        timeConditions.delete_yes.click();
        ys_waitingLoading(timeConditions.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        int row6=row5+1;
        YsAssert.assertEquals(row6,row2,"表格删除：workday_Advanced，确定删除");
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
        Reporter.infoAfterClass("执行完毕：======  时间条件—TimeCondition  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);

    }
}
