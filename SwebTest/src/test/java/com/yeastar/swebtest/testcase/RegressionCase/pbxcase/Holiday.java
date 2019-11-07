package com.yeastar.swebtest.testcase.RegressionCase.pbxcase;

import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;
/**
 * Created by AutoTest on 2017/9/28.
 */
public class Holiday extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======  时间条件—Holiday  ======="); //执行操作
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

//    添加Holiday
    @Test
    public void A_addHoliday1() throws InterruptedException {
        settings.callControl_panel.click();
        time_Conditions.timeConditions.click();
        holiday.holiday.click();
        Reporter.infoExec(" 删除所有Holiday"); //执行操作
        deletes("删除所有Holiday",holiday.grid,holiday.delete,holiday.delete_yes,holiday.grid_Mask);

        Reporter.infoExec(" 新建HolidayByDay：2017-10-01~2025-12-31"); //执行操作
        m_callcontrol.addHolidayByDay("HolidayByDay","2017-10-01","2025-12-31");
        Reporter.infoExec(" 新建HolidayByMonth：1月1号-12月31号"); //执行操作
        m_callcontrol.addHolidayByMonth("HolidayByMonth",add_holiday.january,"1",add_holiday.december,"31");
        Reporter.infoExec(" 新建HolidayByWeek：5月第3个周四"); //执行操作
        m_callcontrol.addHolidayByWeek("HolidayByWeek",add_holiday.may,add_holiday.third,add_holiday.thursday);
        Reporter.infoExec(" 新建HolidayByWeek2：12月最后1个周日"); //执行操作
        m_callcontrol.addHolidayByWeek("HolidayByWeek2",add_holiday.december,add_holiday.last,add_holiday.sunday);

    }


//    编辑Holiday
    @Test
    public void B_editHoliday() throws InterruptedException {
        Reporter.infoExec(" 编辑HolidayByWeek：8月第4个周六"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(holiday.grid,holiday.gridcolumn_Name,"HolidayByWeek",sort_ascendingOrder)));
        gridClick(holiday.grid,row,holiday.gridEdit);
        comboboxSelect(add_holiday.weekMonth,add_holiday.august);
        comboboxSelect(add_holiday.weeknum,add_holiday.fourth);
        comboboxSelect(add_holiday.weekday,add_holiday.saturday);
        add_holiday.save.click();
        YsAssert.assertEquals(gridContent(holiday.grid,row,holiday.gridcolumn_Date),"4th Sat in Aug","编辑Holiday");
    }

//    删除Holiday
    @Test
    public void C_deleteHoliday() throws InterruptedException {
        Reporter.infoExec(" 表格删除：HolidayByWeek-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(holiday.grid,holiday.gridcolumn_Name,"HolidayByWeek",sort_ascendingOrder)));
        int rows=Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        gridClick(holiday.grid,row,holiday.gridDelete);
        holiday.delete_no.click();
        ys_waitingLoading(holiday.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：HolidayByWeek-取消删除");

        Reporter.infoExec(" 表格删除：HolidayByWeek-确定删除"); //执行操作
        gridClick(holiday.grid,row,holiday.gridDelete);
        holiday.delete_yes.click();
        ys_waitingLoading(holiday.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：HolidayByWeek-确定删除");

        Reporter.infoExec(" 删除：HolidayByWeek2-取消删除"); //执行操作
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(holiday.grid,holiday.gridcolumn_Name,"HolidayByWeek2",sort_ascendingOrder)));
        gridCheck(holiday.grid,row4,holiday.gridcolumn_Check);
        holiday.delete.click();
        holiday.delete_no.click();
        ys_waitingLoading(holiday.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        System.out.println("row5:"+row5);
        YsAssert.assertEquals(row2,row5,"删除：HolidayByWeek2-取消删除");

        Reporter.infoExec(" 删除：HolidayByWeek2-确定删除"); //执行操作
//        gridCheck(holiday.grid,Integer.parseInt(String.valueOf(gridFindRowByColumn(holiday.grid,holiday.gridcolumn_Name,"HolidayByWeek2",sort_ascendingOrder))),holiday.gridcolumn_Check);
        holiday.delete.click();
        holiday.delete_yes.click();
        ys_waitingLoading(holiday.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        System.out.println("row6:"+row6);
        int row7=row6+1;
        System.out.println("row7:"+row7);
        YsAssert.assertEquals(row5,row7,"删除：HolidayByWeek2-确定删除");
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
        Reporter.infoAfterClass("执行完毕：======  时间条件-Holiday  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);

    }
}
