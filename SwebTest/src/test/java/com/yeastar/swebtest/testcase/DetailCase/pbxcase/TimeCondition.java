package com.yeastar.swebtest.testcase.DetailCase.pbxcase;

import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.ScreenShot;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;


import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.sleep;

/**
 * Created by Caroline on 2018/1/16.
 * 呼入路由中勾选了Time Condition后，Time Condition中呼入到不同目的地部分在“呼入路由”编码中已经涉及，所以这里不再重复编码
 * 需特别注意：测试时间条件前，先把cdr全部都清空---前后日期选长一点清空，如2000年到2020年的cdr记录，以防后面去获取不同时间的cdr时会获取错误
 */
public class TimeCondition extends SwebDriver {
    //    分割版本号字符
    String[] version = DEVICE_VERSION.split("\\.");
    //    要创建的呼出路由是否已存在的标志变量
    boolean isout1Exist = false;
    boolean isout2Exist = false;
    boolean isout3Exist = false;
    boolean isout4Exist = false;
    boolean isout5Exist = false;
    boolean isout6Exist = false;
    boolean isout7Exist = false;

    @BeforeClass
    public void BeforeClass() {

        Reporter.infoBeforeClass("开始执行：====== TimeCondition ======"); //执行操作
        initialDriver(BROWSER, "https://" + DEVICE_IP_LAN + ":" + DEVICE_PORT + "/");
        login(LOGIN_USERNAME, LOGIN_PASSWORD);
        if (!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes") && Integer.valueOf(VERSION_SPLIT[1]) <= 9) {
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }

//    @Test
    public void A0_restore(){
        //初始化beforetest
        resetoreBeforetest("BeforeTest_Local.bak");

    }
    @Test
    public void A1_addExtensions() {
        pjsip.Pj_Init();
        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, 1);
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);
        closePbxMonitor();
    }
    @Test
    public void A2_init0() {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        cdRandRecordings.time_end.setValue("2030-12-08 23:59");
        ys_waitingTime(1000);
        cdRandRecordings.time_start.setValue("2000-01-01 00:00");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        Object count = gridLineNum(cdRandRecordings.grid);
        if(Integer.valueOf(count.toString()) > 0){
            Reporter.infoExec("TimeContition CDR count = " + count);
//            deletes("删除所有CDR",cdRandRecordings.grid,cdRandRecordings.deleteCDR,cdRandRecordings.delete_yes,cdRandRecordings.grid_Mask);
            setPageShowNum(cdRandRecordings.grid, 100);
            ys_waitingLoading(cdRandRecordings.grid_Mask);
            while (Integer.parseInt(String.valueOf(gridLineNum(cdRandRecordings.grid))) > 0) {
                gridSeleteAll(cdRandRecordings.grid);
                cdRandRecordings.deleteCDR.click();
                if(pageDeskTop.loginout_OK.isDisplayed()){
                    Reporter.infoExec("deleting No Item defined click "+pageDeskTop.loginout_OK.isDisplayed());
                    pageDeskTop.loginout_OK.click();
                    break;
                }else{
                    Reporter.infoExec("deleting yes click "+cdRandRecordings.delete_yes.isDisplayed());
                    cdRandRecordings.delete_yes.click();
                }
                ys_waitingLoading(cdRandRecordings.grid_Mask);
                cdRandRecordings.search.click();
                Reporter.infoExec("deleting "+": "+Integer.parseInt(String.valueOf(gridLineNum(cdRandRecordings.grid))));
                ys_waitingTime(3000);
            }
        }
        closeCDRRecord();
    }
    @Test
    public void A2_init1() {


        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.synchronizeWithNTPServer.click();
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            pageDeskTop.reboot_Yes.click();
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
        login(LOGIN_USERNAME, LOGIN_PASSWORD);
    }

    @Test
    public void B1_addTimeCondition1()  {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        time_Conditions.timeConditions.click();
        timeConditions.timeConditions.click();
        Reporter.infoExec(" 删除所有时间条件——Workday");
        deletes("删除所有时间条件——Workday", timeConditions.grid, timeConditions.delete, timeConditions.delete_yes, timeConditions.grid_Mask);

        Reporter.infoExec(" 添加时间条件---时间条件Q_ABC: 00:00~01:00"); //执行操作
        m_callcontrol.addTimeContion("时间条件Q_ABC", "00:00", "01:00", false, "all");
        Reporter.infoExec(" 编辑时间条件Q_ABC，新增10个Time，勾选高级设置，All Month,All Day");
        gridClick(timeConditions.grid, Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid, timeConditions.gridcolumn_Name, "时间条件Q_ABC", sort_ascendingOrder))), timeConditions.gridEdit);
        for (int i = 1; i < 11; i++) {
            add_time_condition.getAddTime(i).click();
            ys_waitingTime(1000);
        }
        String[] Start = {"starthour", "startminu", "endhour", "endminu"};
        String[][] time = {{"00", "30", "02", "30"}, {"03", "00", "03", "01"}, {"05", "05", "12", "00"}, {"12", "00", "15", "30"}, {"15", "40", "16", "30"}, {"18", "50", "20", "30"}, {"20", "40", "21", "15"}, {"21", "25", "22", "00"}, {"23", "50", "23", "51"}, {"23", "52", "23", "59"}};
        add_time_condition.setTime_More(2, Start, time);
        add_time_condition.advancedOptions.click();
        add_time_condition.all_month.click();
        add_time_condition.all_day.click();
        add_time_condition.save.click();
    }

    @Test
    public void B1_addTimeCondition2()  {
        Reporter.infoExec(" 添加时间条件*:2月2号周一，00:59~23:59"); //执行操作
        m_callcontrol.addTimeContion("*", "00:59", "23:59", false, "mon");
        Reporter.infoExec(" 编辑*，勾选高级设置，february,day2");
        gridClick(timeConditions.grid, Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid, timeConditions.gridcolumn_Name, "*", sort_ascendingOrder))), timeConditions.gridEdit);
        add_time_condition.advancedOptions.click();
        add_time_condition.february.click();
        add_time_condition.day2.click();
        add_time_condition.save.click();
    }

    @Test
    public void B1_addTimeCondition3()  {
        Reporter.infoExec(" 添加时间条件1234567890123456789012345678901:7月7号周六，17:00~20:00"); //执行操作
        m_callcontrol.addTimeContion("1234567890123456789012345678901", "17:00", "20:00", false, "sat");
        Reporter.infoExec(" 编辑1234567890123456789012345678901，勾选高级设置，july,day7");
        gridClick(timeConditions.grid, Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid, timeConditions.gridcolumn_Name, "1234567890123456789012345678901", sort_ascendingOrder))), timeConditions.gridEdit);
        add_time_condition.advancedOptions.click();
        add_time_condition.july.click();
        add_time_condition.day7.click();
        add_time_condition.save.click();
    }

    @Test
    public void B1_addTimeCondition4()  {
        Reporter.infoExec(" 添加时间条件あリがとゥ:8月8号周日，20:10~22:30"); //执行操作
        m_callcontrol.addTimeContion("あリがとゥ", "20:10", "22:30", false, "sun");
        Reporter.infoExec(" 编辑あリがとゥ，勾选高级设置，august,september,day8,day9,day10,day11");
        gridClick(timeConditions.grid, Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid, timeConditions.gridcolumn_Name, "あリがとゥ", sort_ascendingOrder))), timeConditions.gridEdit);
        add_time_condition.advancedOptions.click();
        add_time_condition.august.click();
        add_time_condition.september.click();
        add_time_condition.day8.click();
        add_time_condition.day9.click();
        add_time_condition.day10.click();
        add_time_condition.day11.click();
        add_time_condition.save.click();
    }

    @Test
    public void B1_addTimeCondition5()  {
        Reporter.infoExec(" 添加时间条件العراللغة:" + "10月29号周一到周五，00:00~23:59"); //执行操作
        m_callcontrol.addTimeContion("العراللغة", "00:00", "23:59", false, "mon", "tue", "wed", "thu", "fri");
        Reporter.infoExec(" 编辑العراللغة，勾选高级设置，october,day29");
        gridClick(timeConditions.grid, Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid, timeConditions.gridcolumn_Name, "العراللغة", sort_ascendingOrder))), timeConditions.gridEdit);
        add_time_condition.advancedOptions.click();
        add_time_condition.october.click();
        add_time_condition.day29.click();
        add_time_condition.save.click();
    }

    @Test
    public void B1_addTimeCondition6()  {
        Reporter.infoExec(" 添加时间条件Спасибо：12月31号周二、四、六，00:00~23:59"); //执行操作
        m_callcontrol.addTimeContion("Спасибо", "00:00", "23:59", false, "tue", "thu", "sat");
        Reporter.infoExec(" 编辑Спасибо，勾选高级设置，december,day31");
        gridClick(timeConditions.grid, Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid, timeConditions.gridcolumn_Name, "Спасибо", sort_ascendingOrder))), timeConditions.gridEdit);
        add_time_condition.advancedOptions.click();
        add_time_condition.december.click();
        add_time_condition.day31.click();
        add_time_condition.save.click();
    }

    @Test
    public void B1_addTimeCondition7()  {
        Reporter.infoExec(" 添加时间条件 时间条件7：1月1号周一到周日，00:00~23:59"); //执行操作
        m_callcontrol.addTimeContion("时间条件7", "00:00", "23:59", false, "all");
        Reporter.infoExec(" 编辑时间条件7，勾选高级设置，january,day1");
        gridClick(timeConditions.grid, Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid, timeConditions.gridcolumn_Name, "时间条件7", sort_ascendingOrder))), timeConditions.gridEdit);
        add_time_condition.advancedOptions.click();
        add_time_condition.january.click();
        add_time_condition.day1.click();
        ys_waitingTime(1000);
        add_time_condition.save.click();
    }

    @Test
    public void B2_addHoliday()  {
        holiday.holiday.click();
        Reporter.infoExec(" 删除所有Holiday"); //执行操作
        deletes("删除所有Holiday", holiday.grid, holiday.delete, holiday.delete_yes, holiday.grid_Mask);

        m_callcontrol.addHolidayByDay("あリがとゥ_date", "2018-11-11", "2018-11-11");
        m_callcontrol.addHolidayByMonth("~！￥……*（）“”、【】，。《》？+—···...month", add_holiday.july, "1", add_holiday.august, "31");
        m_callcontrol.addHolidayByWeek("Спасибо_week1", add_holiday.february, add_holiday.first, add_holiday.wednesday);
        m_callcontrol.addHolidayByWeek("العراللغة_week2", add_holiday.september, add_holiday.second, add_holiday.thursday);
        m_callcontrol.addHolidayByWeek("今天天气不错_week3", add_holiday.december, add_holiday.last, add_holiday.sunday);
        ys_apply();
    }

    @Test
    public void B3_addOutRoute1()  {
        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        outboundRoutes.outboundRoutes.click();
        setPageShowNum(outboundRoutes.grid, 100);


        String[] name = new String[50];
        for (int i = 0; i < Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid))); i++) {
            name[i] = String.valueOf(gridContent(outboundRoutes.grid, i + 1, outboundRoutes.gridcolumn_Name));
            if (name[i].equals("OutRoute_test1")) {
//                如果name[i]就是要找的那条呼出路由，将标志变量 isout1Exist …… isout7Exist 赋值为true
                isout1Exist = true;
                gridCheck(outboundRoutes.grid, i + 1, outboundRoutes.gridcolumn_Check);
            } else if (name[i].equals("OutRoute_test2")) {
                isout2Exist = true;
                gridCheck(outboundRoutes.grid, i + 1, outboundRoutes.gridcolumn_Check);
            } else if (name[i].equals("OutRoute_test3")) {
                isout3Exist = true;
                gridCheck(outboundRoutes.grid, i + 1, outboundRoutes.gridcolumn_Check);
            } else if (name[i].equals("OutRoute_test4")) {
                isout4Exist = true;
                gridCheck(outboundRoutes.grid, i + 1, outboundRoutes.gridcolumn_Check);
            } else if (name[i].equals("OutRoute_test5")) {
                isout5Exist = true;
                gridCheck(outboundRoutes.grid, i + 1, outboundRoutes.gridcolumn_Check);
            } else if (name[i].equals("OutRoute_test6")) {
                isout6Exist = true;
                gridCheck(outboundRoutes.grid, i + 1, outboundRoutes.gridcolumn_Check);
            } else if (name[i].equals("OutRoute_test7")) {
                isout7Exist = true;
                gridCheck(outboundRoutes.grid, i + 1, outboundRoutes.gridcolumn_Check);
            }
        }
        if (isout1Exist||isout2Exist||isout3Exist||isout4Exist||isout5Exist||isout6Exist||isout7Exist) {
            outboundRoutes.delete.click();
            outboundRoutes.delete_yes.click();
            ys_waitingLoading(outboundRoutes.grid_Mask);
        }
        Reporter.infoExec(" 添加呼出路由OutRoute_test1"); //执行操作
        outboundRoutes.add.click();
        ys_waitingMask();
        add_outbound_routes.name.setValue("OutRoute_test1");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('patterns','92.')");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('strip','2')");
        listSelect(add_outbound_routes.list_Trunk, trunkList, SPS);
        add_outbound_routes.me_AddAllToSelect.click();
        ys_waitingTime(3000);
//        勾选time condition
        $$("#st-or-timecondition span").findBy(text("时间条件Q_ABC")).click();
//        操作一下cdr，防止勾选的timecondition小气泡挡住了save按钮
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        closeCDRRecord();
//        保存编辑页面
        add_outbound_routes.save.click();
    }

    @Test
    public void B3_addOutRoute2()  {
        Reporter.infoExec(" 添加呼出路由OutRoute_test2"); //执行操作
        outboundRoutes.add.click();
        ys_waitingMask();
        add_outbound_routes.name.setValue("OutRoute_test2");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('patterns','93.')");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('strip','2')");
        listSelect(add_outbound_routes.list_Trunk, trunkList, SPS);
        add_outbound_routes.me_AddAllToSelect.click();
//        勾选time condition
        $$("#st-or-timecondition span").findBy(text("*")).click();
        ys_waitingTime(1000);
//        操作一下cdr，防止勾选的timecondition小气泡挡住了save按钮
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        closeCDRRecord();
//        保存编辑页面
        add_outbound_routes.save.click();
    }

    @Test
    public void B3_addOutRoute3()  {
        Reporter.infoExec(" 添加呼出路由OutRoute_test3"); //执行操作
        outboundRoutes.add.click();
        ys_waitingMask();
        add_outbound_routes.name.setValue("OutRoute_test3");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('patterns','94.')");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('strip','2')");
        listSelect(add_outbound_routes.list_Trunk, trunkList, SPS);
        add_outbound_routes.me_AddAllToSelect.click();
//        勾选time condition
        $$("#st-or-timecondition span").findBy(text("1234567890123456789012345678901")).click();
        ys_waitingTime(1000);
//        操作一下cdr，防止勾选的timecondition小气泡挡住了save按钮
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        closeCDRRecord();
//        保存编辑页面
        add_outbound_routes.save.click();
    }

    @Test
    public void B3_addOutRoute4()  {
        Reporter.infoExec(" 添加呼出路由OutRoute_test4"); //执行操作
        outboundRoutes.add.click();
        ys_waitingMask();
        add_outbound_routes.name.setValue("OutRoute_test4");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('patterns','95.')");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('strip','2')");
        listSelect(add_outbound_routes.list_Trunk, trunkList, SPS);
        add_outbound_routes.me_AddAllToSelect.click();
//        勾选time condition
        $$("#st-or-timecondition span").findBy(text("あリがとゥ")).click();
        ys_waitingTime(1000);
        //        操作一下cdr，防止勾选的timecondition小气泡挡住了save按钮
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        closeCDRRecord();
//        保存编辑页面
        add_outbound_routes.save.click();
    }

    @Test
    public void B3_addOutRoute5()  {
        Reporter.infoExec(" 添加呼出路由OutRoute_test5"); //执行操作
        outboundRoutes.add.click();
        ys_waitingMask();
        add_outbound_routes.name.setValue("OutRoute_test5");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('patterns','96.')");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('strip','2')");
        listSelect(add_outbound_routes.list_Trunk, trunkList, SPS);
        add_outbound_routes.me_AddAllToSelect.click();
//        勾选time condition
        $$("#st-or-timecondition span").findBy(text("العراللغة")).click();
        ys_waitingTime(1000);
        //        操作一下cdr，防止勾选的timecondition小气泡挡住了save按钮
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        closeCDRRecord();
//        保存编辑页面
        add_outbound_routes.save.click();
    }

    @Test
    public void B3_addOutRoute6()  {
        Reporter.infoExec(" 添加呼出路由OutRoute_test6"); //执行操作outboundRoutes.add.click();
        outboundRoutes.add.click();
        ys_waitingMask();
        add_outbound_routes.name.setValue("OutRoute_test6");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('patterns','97.')");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('strip','2')");
        listSelect(add_outbound_routes.list_Trunk, trunkList, SPS);
        add_outbound_routes.me_AddAllToSelect.click();
//        勾选time condition
        $$("#st-or-timecondition span").findBy(text("Спасибо")).click();
        ys_waitingTime(1000);
        //        操作一下cdr，防止勾选的timecondition小气泡挡住了save按钮
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        closeCDRRecord();
//        保存编辑页面
        add_outbound_routes.save.click();
    }

    @Test
    public void B3_addOutRoute7()  {
        Reporter.infoExec(" 添加呼出路由OutRoute_test7"); //执行操作outboundRoutes.add.click();
        outboundRoutes.add.click();
        ys_waitingMask();
        add_outbound_routes.name.setValue("OutRoute_test7");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('patterns','98.')");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('strip','2')");
        listSelect(add_outbound_routes.list_Trunk, trunkList, SPS);
        add_outbound_routes.me_AddAllToSelect.click();
//        勾选time condition
        $$("#st-or-timecondition span").findBy(text("时间条件7")).click();
        ys_waitingTime(1000);
//        操作一下cdr，防止勾选的timecondition小气泡挡住了save按钮
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        closeCDRRecord();
//        保存编辑页面
        add_outbound_routes.save.click();
    }

    @Test
    public void B4_editInRoute() {
        inboundRoutes.inboundRoutes.click();
        deletes(" 删除所有呼入路由", inboundRoutes.grid, inboundRoutes.delete, inboundRoutes.delete_yes, inboundRoutes.grid_Mask);
        Reporter.infoExec(" 添加呼入路由InRoute1");
        ArrayList<String> arraytrunk10 = new ArrayList<>();
        arraytrunk10.add("all");
        m_callcontrol.addInboundRoutes("InRoute1", "", "", add_inbound_route.s_extensin, "1000", arraytrunk10);

        Reporter.infoExec("修改呼入路由InRoute1，启用时间条件，并且新增八个Time Condition");
        gridClick(inboundRoutes.grid, gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute1", sort_ascendingOrder), inboundRoutes.gridEdit);
        ys_waitingMask();
        setCheckBox(add_inbound_route.enableTimeCondition, true);
        add_inbound_route.addTimeCondition.click();
        ys_waitingTime(1000);
        add_inbound_route.addTimeCondition.click();
        ys_waitingTime(1000);
        add_inbound_route.addTimeCondition.click();
        ys_waitingTime(1000);
        add_inbound_route.addTimeCondition.click();
        ys_waitingTime(1000);
        add_inbound_route.addTimeCondition.click();
        ys_waitingTime(1000);
        add_inbound_route.addTimeCondition.click();
        ys_waitingTime(1000);
        add_inbound_route.addTimeCondition.click();
        ys_waitingTime(1000);
        add_inbound_route.addTimeCondition.click();
        ys_waitingTime(1000);
        add_inbound_route.SetTimeConditionTableviewDestition(1, 1, "时间条件Q_ABC");
        add_inbound_route.SetTimeConditionTableviewDestination(1, 2, add_inbound_route.s_extensin);
        add_inbound_route.SetTimeConditionTableviewDestitionEx(1, 3, "1000");
        add_inbound_route.SetTimeConditionTableviewDestition(2, 1, "*");
        add_inbound_route.SetTimeConditionTableviewDestination(2, 2, add_inbound_route.s_extensin);
        add_inbound_route.SetTimeConditionTableviewDestitionEx(2, 3, "1100");
        add_inbound_route.SetTimeConditionTableviewDestition(3, 1, "1234567890123456789012345678901");
        add_inbound_route.SetTimeConditionTableviewDestination(3, 2, add_inbound_route.s_extensin);
        add_inbound_route.SetTimeConditionTableviewDestitionEx(3, 3, "1101");
        add_inbound_route.SetTimeConditionTableviewDestition(4, 1, "あリがとゥ");
        add_inbound_route.SetTimeConditionTableviewDestination(4, 2, add_inbound_route.s_extensin);
        add_inbound_route.SetTimeConditionTableviewDestitionEx(4, 3, "1102");
        add_inbound_route.SetTimeConditionTableviewDestition(5, 1, "العراللغة");
        add_inbound_route.SetTimeConditionTableviewDestination(5, 2, add_inbound_route.s_extensin);
        add_inbound_route.SetTimeConditionTableviewDestitionEx(5, 3, "1103");
        add_inbound_route.SetTimeConditionTableviewDestition(6, 1, "Спасибо");
        add_inbound_route.SetTimeConditionTableviewDestination(6, 2, add_inbound_route.s_extensin);
        add_inbound_route.SetTimeConditionTableviewDestitionEx(6, 3, "1104");
        add_inbound_route.SetTimeConditionTableviewDestition(7, 1, "时间条件7");
        add_inbound_route.SetTimeConditionTableviewDestination(7, 2, add_inbound_route.s_extensin);
        add_inbound_route.SetTimeConditionTableviewDestitionEx(7, 3, "1105");
        add_inbound_route.SetTimeConditionTableviewDestition(8, 1, "[Holiday]");
        add_inbound_route.SetTimeConditionTableviewDestination(8, 2, add_inbound_route.s_conference);
        //        保存编辑页面
        add_inbound_route.save.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
    }

    @Test
    public void B5_ExtensionPermission() {
        Reporter.infoExec(" 设置分机1000具有拨打时间特征码的权限"); //执行操作
        settings.general_tree.click();
        m_general.setExtensionPermission(true, "*8", "1000");
        ys_apply();
    }

    //    验证第一个TimeCondition——时间条件Q_ABC
    @Test
    public void C1_FeatureCode() {
//        30.6.0.X
        if (Integer.valueOf(version[1]) <= 6) {
            Reporter.infoExec(" 分机1000拨打特征码*801强制启用工作时间_时间条件Q_ABC"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*801", DEVICE_IP_LAN, false);
        } else {
//        30.7.0.X
            Reporter.infoExec(" 分机1000拨打特征码*802强制启用工作时间_时间条件Q_ABC"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*802", DEVICE_IP_LAN, false);
        }
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
    }

    @Test
    public void C2_setSystemTime() {
        pjsip.Pj_Unregister_Account(1000);
        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2020-09-08");
        dateTime.time_hour.setValue("10");
        dateTime.time_minute.setValue("00");
        dateTime.time_second.setValue("00");
        ys_waitingTime(1000);
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_CreateAccount(1100, EXTENSION_PASSWORD, "UDP", UDP_PORT, 2);
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_CreateAccount(1101, EXTENSION_PASSWORD, "UDP", UDP_PORT, 3);
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_CreateAccount(1102, EXTENSION_PASSWORD, "UDP", UDP_PORT, 4);
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_CreateAccount(1103, EXTENSION_PASSWORD, "UDP", UDP_PORT, 5);
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_CreateAccount(1104, EXTENSION_PASSWORD, "UDP", UDP_PORT, 6);
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_CreateAccount(1105, EXTENSION_PASSWORD, "UDP", UDP_PORT, 7);
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2000, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2001, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void C3_makeCall_Out() {
        Reporter.infoExec(" 1000拨打922000通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000, "922000", DEVICE_IP_LAN, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
//
        sleep(1000);
        //注意在查询CDR的时候，因为输入time_start或time_end，就会刷新查询结果，所以要保证第一次刷新的时候star的时间要在end之前，否则会有弹窗提示
//        那这里就加一个cdr的star和end的提示，如果有弹窗，则点击弹窗后继续
        cdRandRecordings.time_end.setValue("2020-09-08 23:59");
        ys_waitingTime(2000);
        cdRandRecordings.time_start.setValue("2020-09-08 00:00");
        cdRandRecordings.search.click();
        ys_waitingTime(1000);
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "1000 <1000>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "922000", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_DestinationTrunk)).trim(), SPS, "CDR目的中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_outRoute, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void C4_makeCall_In() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_end.setValue("2020-09-08 23:59");
        ys_waitingTime(1000);
        cdRandRecordings.time_start.setValue("2020-09-08 00:00");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "1000 <1000>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void C5_setSystemTime1() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1101);
        pjsip.Pj_Unregister_Account(1102);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(1105);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(2001);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2016-09-08");
        dateTime.time_hour.setValue("04");
        dateTime.time_minute.setValue("00");
        dateTime.time_second.setValue("00");
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
    }

    @Test
    public void C5_setSystemTime2() {
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void C6_makeCall_Out() {
        Reporter.infoExec(" 1000拨打922000通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000, "922000", DEVICE_IP_LAN, false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1000, HUNGUP, 8);
        if (state == HUNGUP) {
            Reporter.infoExec(" 1000拨打922000,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        } else {
            Reporter.infoExec(" 1000拨打922000,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打922000,预期呼出失败,实际呼出成功");
        }
    }

    @Test
    public void C7_makeCall_In() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2016-09-08 04:00");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2016-09-08 05:00");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "1000 <1000>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    //    验证holiday的week2
    @Test
    public void C8_FeatureCode() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        Reporter.infoExec(" 分机1000拨打特征码*800重置"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000, "*800", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
    }

    @Test
    public void C9_makeCall_In() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到会议室"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(6000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为" + getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2016-09-08 04:00");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2016-09-08 23:59");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "6400", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void D1_setSystemTime1_FeatureCode() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
//        30.6.0.X
        if (Integer.valueOf(version[1]) <= 6) {
            Reporter.infoExec(" 分机1000拨打特征码*801"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*801", DEVICE_IP_LAN, false);
        } else {
//        30.7.0.X
            Reporter.infoExec(" 分机1000拨打特征码*802"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*802", DEVICE_IP_LAN, false);
        }
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
    }

    @Test
    public void D1_setSystemTime2() {
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1101);
        pjsip.Pj_Unregister_Account(1102);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(1105);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(2001);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2016-09-08");
        dateTime.time_hour.setValue("00");
        dateTime.time_minute.setValue("00");
        dateTime.time_second.setValue("05");
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
    }

    @Test
    public void D1_setSystemTime3() {
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void D2_makeCall_Out() {
        Reporter.infoExec(" 1000拨打922000通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000, "922000", DEVICE_IP_LAN, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2016-09-08 00:00");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2016-09-08 01:00");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "1000 <1000>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "922000", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_DestinationTrunk)).trim(), SPS, "CDR目的中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_outRoute, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void D3_makeCall_In() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2016-09-08 00:00");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2016-09-08 01:00");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "1000 <1000>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void D4_deleteTime() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        time_Conditions.timeConditions.click();
        timeConditions.timeConditions.click();
        Reporter.infoExec("删除TimeCondition_时间条件Q_ABC的Time");
        ys_waitingTime(3000);
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid, timeConditions.gridcolumn_Name, "时间条件Q_ABC", sort_ascendingOrder)));
        gridClick(timeConditions.grid, row, timeConditions.gridEdit);
        ys_waitingTime(1000);
        for (int i = 0; i < 10; i++) {
            add_time_condition.getDeleteTime(2).click();
            ys_waitingTime(1000);
        }
        add_time_condition.save.click();
        ys_apply();
    }

    @Test
    public void D5_makeCall_Out() {
        Reporter.infoExec(" 1000拨打922000通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000, "922000", DEVICE_IP_LAN, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2016-09-08 00:00");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2016-09-08 01:00");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "1000 <1000>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "922000", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_DestinationTrunk)).trim(), SPS, "CDR目的中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_outRoute, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void D6_makeCall_In() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2016-09-08 00:00");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2016-09-08 01:00");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "1000 <1000>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void D7_setSystemTime1() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1101);
        pjsip.Pj_Unregister_Account(1102);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(1105);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(2001);

        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2016-09-08");
        dateTime.time_hour.setValue("10");
        dateTime.time_minute.setValue("00");
        dateTime.time_second.setValue("00");
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
    }

    @Test
    public void D7_setSystemTime2() {
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void D8_makeCall_Out() {
        Reporter.infoExec(" 1000拨打922000通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000, "922000", DEVICE_IP_LAN, false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1000, HUNGUP, 8);
        if (state == HUNGUP) {
            Reporter.infoExec(" 1000拨打922000,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        } else {
            Reporter.infoExec(" 1000拨打922000,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1000拨打922000,预期呼出失败,实际呼出成功");
        }
    }

    @Test
    public void D9_makeCall_In() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1000，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(6000);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2016-09-08 10:00");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2016-09-08 11:00");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "1000 <1000>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    //    验证第二个TimeCondition——*
    @Test
    public void E1_FeatureCode() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
//        30.6.0.X
        if (Integer.valueOf(version[1]) <= 6) {
            Reporter.infoExec(" 分机1000拨打特征码*802强制启用工作时间_*"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*802", DEVICE_IP_LAN, false);
        } else {
//        30.7.0.X
            Reporter.infoExec(" 分机1000拨打特征码*803强制启用工作时间_*"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*803", DEVICE_IP_LAN, false);
        }
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
    }

    @Test
    public void E2_setSystemTime1() {
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1101);
        pjsip.Pj_Unregister_Account(1102);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(1105);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(2001);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2015-02-02");
        dateTime.time_hour.setValue("01");
        dateTime.time_minute.setValue("30");
        dateTime.time_second.setValue("00");
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
    }

    @Test
    public void E2_setSystemTime2() {
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void E3_makeCall_Out() {
        Reporter.infoExec(" 1100拨打932000通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100, "932000", DEVICE_IP_LAN, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1100, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1100状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1100状态为TALKING，实际状态为" + getExtensionStatus(1100, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2015-02-02 01:30");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2015-02-02 23:59");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "1100 <1100>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "932000", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_DestinationTrunk)).trim(), SPS, "CDR目的中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_outRoute, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void E4_makeCall_In() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1100，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1100, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1100状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1100, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2015-02-02 01:30");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2015-02-02 23:59");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "1100 <1100>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void E5_setSystemTime1() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1101);
        pjsip.Pj_Unregister_Account(1102);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(1105);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(2001);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2017-08-02");
        dateTime.time_hour.setValue("01");
        dateTime.time_minute.setValue("30");
        dateTime.time_second.setValue("00");
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
    }

    @Test
    public void E5_setSystemTime2() {
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void E6_makeCall_Out() {
        Reporter.infoExec(" 1100拨打932000通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1100, "932000", DEVICE_IP_LAN, false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1100, HUNGUP, 8);
        if (state == HUNGUP) {
            Reporter.infoExec(" 1100拨打932000,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        } else {
            Reporter.infoExec(" 1100拨打932000,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1100拨打932000,预期呼出失败,实际呼出成功");
        }
    }

    @Test
    public void E7_makeCall_In() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1100，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(6000);
        if (getExtensionStatus(1100, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1100状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1100状态为TALKING，实际状态为" + getExtensionStatus(1100, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2017-08-02 01:30");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2017-08-02 23:59");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "1100 <1100>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    //    验证holiday的month
    @Test
    public void E8_FeatureCode() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        Reporter.infoExec(" 分机1000拨打特征码*800"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000, "*800", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
    }

    @Test
    public void E9_makeCall_In() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到会议室6400"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(6000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为" + getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2017-08-02 01:30");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2017-08-02 02:30");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "6400", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    //    验证第3个TimeCondition——1234567890123456789012345678901
    @Test
    public void F1_setSystemTime1() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1101);
        pjsip.Pj_Unregister_Account(1102);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(1105);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(2001);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2018-07-07");
        dateTime.time_hour.setValue("19");
        dateTime.time_minute.setValue("30");
        dateTime.time_second.setValue("00");
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
    }

    @Test
    public void F1_setSystemTime2() {
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void F2_FeatureCode() {
//        30.6.0.X
        if (Integer.valueOf(version[1]) <= 6) {
            Reporter.infoExec(" 分机1000拨打特征码*803强制启用工作时间_1234567890123456789012345678901"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*803", DEVICE_IP_LAN, false);
        } else {
//        30.7.0.X
            Reporter.infoExec(" 分机1000拨打特征码*804强制启用工作时间_1234567890123456789012345678901"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*804", DEVICE_IP_LAN, false);
        }
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
    }

    @Test
    public void F3_makeCall_Out() {
        Reporter.infoExec(" 1101拨打942000通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1101, "942000", DEVICE_IP_LAN, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1101, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1101状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1101状态为TALKING，实际状态为" + getExtensionStatus(1101, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_end.setValue("2018-07-07 23:59");
        ys_waitingTime(1000);
        cdRandRecordings.time_start.setValue("2018-07-07 19:30");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "1101 <1101>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "942000", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_DestinationTrunk)).trim(), SPS, "CDR目的中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_outRoute, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void F4_makeCall_In() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1101，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为" + getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_end.setValue("2018-07-07 23:59");
        ys_waitingTime(1000);
        cdRandRecordings.time_start.setValue("2018-07-07 19:30");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "1101 <1101>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void F5_setSystemTime1() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1101);
        pjsip.Pj_Unregister_Account(1102);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(1105);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(2001);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2018-07-14");
        dateTime.time_hour.setValue("19");
        dateTime.time_minute.setValue("30");
        dateTime.time_second.setValue("00");
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
    }

    @Test
    public void F5_setSystemTime2() {
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void F6_makeCall_Out() {
        Reporter.infoExec(" 1101拨打932000通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1101, "932000", DEVICE_IP_LAN, false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1101, HUNGUP, 8);
        if (state == HUNGUP) {
            Reporter.infoExec(" 1101拨打932000,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        } else {
            Reporter.infoExec(" 1101拨打932000,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1101拨打932000,预期呼出失败,实际呼出成功");
        }
    }

    @Test
    public void F7_makeCall_In() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1101，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(6000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为" + getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_end.setValue("2018-07-14 23:59");
        ys_waitingTime(1000);
        cdRandRecordings.time_start.setValue("2018-07-14 19:30");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "1101 <1101>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    //    验证第4个TimeCondition——あリがとゥ
    @Test
    public void G1_FeatureCode() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
//        30.6.0.X
        if (Integer.valueOf(version[1]) <= 6) {
            Reporter.infoExec(" 分机1000拨打特征码*804强制启用工作时间_あリがとゥ"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*804", DEVICE_IP_LAN, false);
        } else {
//        30.7.0.X
            Reporter.infoExec(" 分机1000拨打特征码*805强制启用工作时间_あリがとゥ"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*805", DEVICE_IP_LAN, false);
        }
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
    }

    @Test
    public void G2_setSystemTime1() {
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1101);
        pjsip.Pj_Unregister_Account(1102);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(1105);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(2001);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2021-08-08");
        dateTime.time_hour.setValue("21");
        dateTime.time_minute.setValue("00");
        dateTime.time_second.setValue("00");
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
    }

    @Test
    public void G2_setSystemTime2() {
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void G3_makeCall_Out() {
        Reporter.infoExec(" 1102拨打952000通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1102, "952000", DEVICE_IP_LAN, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1102, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1102状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1102状态为TALKING，实际状态为" + getExtensionStatus(1102, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_end.setValue("2021-08-08 23:59");
        ys_waitingTime(1000);
        cdRandRecordings.time_start.setValue("2021-08-08 21:00");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "1102 <1102>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "952000", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_DestinationTrunk)).trim(), SPS, "CDR目的中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_outRoute, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void G4_makeCall_In() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1102，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为" + getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_end.setValue("2021-08-08 23:59");
        ys_waitingTime(1000);
        cdRandRecordings.time_start.setValue("2021-08-08 21:00");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "1102 <1102>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void G5_setSystemTime1() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1101);
        pjsip.Pj_Unregister_Account(1102);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(1105);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(2001);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2018-08-08");
        dateTime.time_hour.setValue("21");
        dateTime.time_minute.setValue("00");
        dateTime.time_second.setValue("00");
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
    }

    @Test
    public void G5_setSystemTime2() {
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void G6_makeCall_Out() {
        Reporter.infoExec(" 1102拨打952000通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1102, "952000", DEVICE_IP_LAN, false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1102, HUNGUP, 8);
        if (state == HUNGUP) {
            Reporter.infoExec(" 1102拨打952000,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        } else {
            Reporter.infoExec(" 1102拨打952000,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1102拨打952000,预期呼出失败,实际呼出成功");
        }
    }

    @Test
    public void G7_makeCall_In() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1102，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(6000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为" + getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_end.setValue("2018-08-08 23:59");
        ys_waitingTime(1000);
        cdRandRecordings.time_start.setValue("2018-08-08 21:00");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "1102 <1102>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void H1_setSystemTime1() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1101);
        pjsip.Pj_Unregister_Account(1102);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(1105);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(2001);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2018-09-09");
        dateTime.time_hour.setValue("21");
        dateTime.time_minute.setValue("00");
        dateTime.time_second.setValue("00");
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
    }

    @Test
    public void H1_setSystemTime2() {
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void H2_makeCall_Out() {
        Reporter.infoExec(" 1102拨打952000通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1102, "952000", DEVICE_IP_LAN, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1102, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1102状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1102状态为TALKING，实际状态为" + getExtensionStatus(1102, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_end.setValue("2018-09-09 23:59");
        ys_waitingTime(1000);
        cdRandRecordings.time_start.setValue("2018-09-09 21:00");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "1102 <1102>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "952000", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_DestinationTrunk)).trim(), SPS, "CDR目的中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_outRoute, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void H3_makeCall_In() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1102，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为" + getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_end.setValue("2018-09-09 23:59");
        ys_waitingTime(1000);
        cdRandRecordings.time_start.setValue("2018-09-09 21:00");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "1102 <1102>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void H4_setSystemTime1() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1101);
        pjsip.Pj_Unregister_Account(1102);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(1105);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(2001);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2018-11-11");
        dateTime.time_hour.setValue("21");
        dateTime.time_minute.setValue("00");
        dateTime.time_second.setValue("00");
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
    }

    @Test
    public void H4_setSystemTime2() {
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void H5_makeCall_Out() {
        Reporter.infoExec(" 1102拨打952000通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1102, "952000", DEVICE_IP_LAN, false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1102, HUNGUP, 8);
        if (state == HUNGUP) {
            Reporter.infoExec(" 1102拨打952000,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        } else {
            Reporter.infoExec(" 1102拨打952000,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1102拨打952000,预期呼出失败,实际呼出成功");
        }
    }

    @Test
    public void H6_makeCall_In() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1102，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(6000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为" + getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_end.setValue("2018-11-11 23:59");
        ys_waitingTime(1000);
        cdRandRecordings.time_start.setValue("2018-11-11 21:00");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "1102 <1102>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    //    验证holiday——date
    @Test
    public void H7_FeatureCode() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        Reporter.infoExec(" 分机1000拨打特征码*800"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000, "*800", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
    }

    @Test
    public void H8_makeCall_In() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到会议室6400"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(6000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为" + getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_end.setValue("2018-11-11 23:59");
        ys_waitingTime(1000);
        cdRandRecordings.time_start.setValue("2018-11-11 21:00");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "6400", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    //    验证第5个TimeCondition——العراللغة
    @Test
    public void I1_FeatureCode() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
//    30.6.0.X
        if (Integer.valueOf(version[1]) <= 6) {
            Reporter.infoExec(" 分机1000拨打特征码*805强制启用工作时间_العراللغة"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*805", DEVICE_IP_LAN, false);
        } else {
//    30.7.0.X
            Reporter.infoExec(" 分机1000拨打特征码*806强制启用工作时间_العراللغة"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*806", DEVICE_IP_LAN, false);
        }
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
    }

    @Test
    public void I2_setSystemTime1() {
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1101);
        pjsip.Pj_Unregister_Account(1102);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(1105);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(2001);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2018-10-29");
        dateTime.time_hour.setValue("10");
        dateTime.time_minute.setValue("10");
        dateTime.time_second.setValue("10");
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
    }

    @Test
    public void I2_setSystemTime2() {
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void I3_makeCall_Out() {
        Reporter.infoExec(" 1103拨打962000通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1103, "962000", DEVICE_IP_LAN, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1103, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1103状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1103状态为TALKING，实际状态为" + getExtensionStatus(1103, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_end.setValue("2018-10-29 23:59");
        ys_waitingTime(1000);
        cdRandRecordings.time_start.setValue("2018-10-29 10:10");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "xlq <1103>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "962000", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_DestinationTrunk)).trim(), SPS, "CDR目的中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_outRoute, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void I4_makeCall_In() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1103，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为" + getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_end.setValue("2018-10-29 23:59");
        ys_waitingTime(1000);
        cdRandRecordings.time_start.setValue("2018-10-29 10:10");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "xlq <1103>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void I5_setSystemTime1() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1101);
        pjsip.Pj_Unregister_Account(1102);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(1105);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(2001);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2017-10-29");
        dateTime.time_hour.setValue("21");
        dateTime.time_minute.setValue("00");
        dateTime.time_second.setValue("00");
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
    }

    @Test
    public void I5_setSystemTime2() {
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void I6_makeCall_Out() {
        Reporter.infoExec(" 1103拨打962000通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1103, "962000", DEVICE_IP_LAN, false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1103, HUNGUP, 8);
        if (state == HUNGUP) {
            Reporter.infoExec(" 1103拨打962000,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        } else {
            Reporter.infoExec(" 1103拨打962000,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1103拨打962000,预期呼出失败,实际呼出成功");
        }
    }

    @Test
    public void I7_makeCall_In() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1103，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(6000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为" + getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2017-10-29 21:00");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2017-10-29 23:59");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "xlq <1103>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    //    验证第6个TimeCondition——Спасибо
    @Test
    public void J1_FeatureCode() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
//        30.6.0.X
        if (Integer.valueOf(version[1]) <= 6) {
            Reporter.infoExec(" 分机1000拨打特征码*806强制启用工作时间_Спасибо"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*806", DEVICE_IP_LAN, false);
        } else {
//        30.7.0.X
            Reporter.infoExec(" 分机1000拨打特征码*807强制启用工作时间_Спасибо"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*807", DEVICE_IP_LAN, false);
        }
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
    }

    @Test
    public void J2_setSystemTime1() {
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1101);
        pjsip.Pj_Unregister_Account(1102);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(1105);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(2001);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2016-12-31");
        dateTime.time_hour.setValue("00");
        dateTime.time_minute.setValue("00");
        dateTime.time_second.setValue("01");
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
    }

    @Test
    public void J2_setSystemTime2() {
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void J3_makeCall_Out() {
        Reporter.infoExec(" 1104拨打972000通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1104, "972000", DEVICE_IP_LAN, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1104, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1104状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1104状态为TALKING，实际状态为" + getExtensionStatus(1104, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2016-12-31 00:00");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2016-12-31 23:59");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "xll <1104>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "972000", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_DestinationTrunk)).trim(), SPS, "CDR目的中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_outRoute, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void J4_makeCall_In() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1104，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为" + getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2016-12-31 00:00");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2016-12-31 23:59");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "xll <1104>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void J5_setSystemTime1() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1101);
        pjsip.Pj_Unregister_Account(1102);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(1105);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(2001);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2017-12-31");
        dateTime.time_hour.setValue("00");
        dateTime.time_minute.setValue("00");
        dateTime.time_second.setValue("01");
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
    }

    @Test
    public void J5_setSystemTime2() {
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void J6_makeCall_Out() {
        Reporter.infoExec(" 1104拨打972000通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1104, "972000", DEVICE_IP_LAN, false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1104, HUNGUP, 8);
        if (state == HUNGUP) {
            Reporter.infoExec(" 1104拨打972000,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        } else {
            Reporter.infoExec(" 1104拨打972000,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1104拨打972000,预期呼出失败,实际呼出成功");
        }
    }

    @Test
    public void J7_makeCall_In() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1104，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(6000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为" + getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2017-12-31 00:00");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2017-12-31 23:59");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "xll <1104>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    //    验证holiday——week3
    @Test
    public void J8_FeatureCode() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        Reporter.infoExec(" 分机1000拨打特征码*800"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000, "*800", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
    }

    @Test
    public void J9_makeCall_In() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到会议室6400"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(6000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为" + getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2017-12-31 00:00");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2017-12-31 23:59");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "6400", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    //    验证第7个TimeCondition——时间条件7
    @Test
    public void K1_FeatureCode() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
//        30.6.0.X
        if (Integer.valueOf(version[1]) <= 6) {
            Reporter.infoExec(" 分机1000拨打特征码*807强制启用工作时间_时间条件7"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*807", DEVICE_IP_LAN, false);
        } else {
//        30.7.0.X
            Reporter.infoExec(" 分机1000拨打特征码*808强制启用工作时间_时间条件7"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "*808", DEVICE_IP_LAN, false);
        }
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
    }

    @Test
    public void K2_setSystemTime1() {
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1101);
        pjsip.Pj_Unregister_Account(1102);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(1105);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(2001);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2017-01-01");
        dateTime.time_hour.setValue("23");
        dateTime.time_minute.setValue("00");
        dateTime.time_second.setValue("01");
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
    }

    @Test
    public void K2_setSystemTime2() {
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void K3_makeCall_Out() {
        Reporter.infoExec(" 1105拨打982000通过sps外线呼出，预期呼出成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1105, "982000", DEVICE_IP_LAN, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(1105, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1105状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1105状态为TALKING，实际状态为" + getExtensionStatus(1105, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2017-01-01 23:00");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2017-01-01 23:59");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "1105 <1105>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "982000", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_DestinationTrunk)).trim(), SPS, "CDR目的中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_outRoute, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void K4_makeCall_In() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1105，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(8000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为" + getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2017-01-01 23:00");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2017-01-01 23:59");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "1105 <1105>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void K5_setSystemTime1() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1101);
        pjsip.Pj_Unregister_Account(1102);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(1105);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(2001);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.setUpManually.click();
        dateTime.date.setValue("2017-02-01");
        dateTime.time_hour.setValue("12");
        dateTime.time_minute.setValue("00");
        dateTime.time_second.setValue("00");
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
    }

    @Test
    public void K5_setSystemTime2() {
        login(LOGIN_USERNAME, LOGIN_PASSWORD);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1101"); //执行操作
        pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1102"); //执行操作
        pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1105"); //执行操作
        pjsip.Pj_Register_Account(1105, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }

    @Test
    public void K6_makeCall_Out() {
        Reporter.infoExec(" 1105拨打982000通过sps外线呼出，预期呼出失败"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1105, "982000", DEVICE_IP_LAN, false);
        ys_waitingTime(6000);
        int state = getExtensionStatus(1105, HUNGUP, 8);
        if (state == HUNGUP) {
            Reporter.infoExec(" 1105拨打982000,预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        } else {
            Reporter.infoExec(" 1105拨打982000,预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail(" 1105拨打982000,预期呼出失败,实际呼出成功");
        }
    }

    @Test
    public void K7_makeCall_In() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1105，预期呼入成功"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(6000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为" + getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2017-02-01 12:00");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2017-02-01 23:59");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "1105 <1105>", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    //    验证holiday——week1
    @Test
    public void K8_FeatureCode() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        Reporter.infoExec(" 分机1000拨打特征码*800"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000, "*800", DEVICE_IP_LAN, false);
        if (getExtensionStatus(1000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机1000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机1000状态为TALKING，实际状态为" + getExtensionStatus(1000, TALKING, 8));
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
    }

    @Test
    public void K9_makeCall_In() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到会议室6400"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001, "99999", DEVICE_ASSIST_2, false);
        ys_waitingTime(6000);
        if (getExtensionStatus(2001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2001状态为TALKING，实际状态为" + getExtensionStatus(2001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();

        cdRandRecordings.time_start.setValue("2017-02-01 12:00");
        ys_waitingTime(1000);
        cdRandRecordings.time_end.setValue("2017-02-01 23:59");
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        cdRandRecordings.maxWindows.click();
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 1)).trim(), "2001 <2001>", "CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 2)).trim(), "6400", "CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, 5)).trim(), "Answered", "CDR_Status检测");

        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_SourceTrunk)).trim(), SPS, "CDR源中继检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR, 1, cdRandRecordings.gridColumn_CommunicationTrunk)).trim(), communication_inbound, "CDR通讯类型检测");
        closeCDRRecord();
    }

    @Test
    public void L1_deleteOne_no() {
        if (cdRandRecordings.time_ok.isDisplayed()) {
            cdRandRecordings.time_ok.click();
        }
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        ys_waitingTime(2000);
        time_Conditions.timeConditions.click();
        ys_waitingTime(1000);
        holiday.holiday.click();
//        删除holiday
        Reporter.infoExec(" 删除单个holiday——选择no"); //执行操作
        ys_waitingLoading(holiday.grid_Mask);
        ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH + "L1_deleteOne_no.jpg");
        Reporter.sendReport("link", "Error: " + "L1_deleteOne_no", SCREENSHOT_PATH + "L1_deleteOne_no.jpg");
        Reporter.error(" L1_deleteOne_no 查找あリがとゥ_date");

//       定位要删除的那条呼入路由
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(holiday.grid, holiday.gridcolumn_Name, "あリがとゥ_date", sort_ascendingOrder)));
        //还未操作前呼出路由的所有条数
        int rows = Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        Reporter.infoExec("预期值:" + rows);
        gridClick(holiday.grid, row, holiday.gridDelete);
        holiday.delete_no.click();
        ys_waitingLoading(holiday.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        Reporter.infoExec("实际值:" + row1);
        YsAssert.assertEquals(row1, rows, "删除holiday-取消删除");
    }

    @Test
    public void L2_deleteOne_yes() {
        Reporter.infoExec(" 删除单个holiday——选择yes"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        //       定位要删除的那条呼入路由
        int row1 = Integer.parseInt(String.valueOf(gridFindRowByColumn(holiday.grid, holiday.gridcolumn_Name, "あリがとゥ_date", sort_ascendingOrder)));
        gridClick(holiday.grid, row1, holiday.gridDelete);
        holiday.delete_yes.click();
        ys_waitingLoading(holiday.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        Reporter.infoExec("实际值row2:" + row2);
        int row3 = row - 1;
        Reporter.infoExec("期望值row3:" + row3);
        ys_apply();
        YsAssert.assertEquals(row2, row3, "删除单个holiday——确定删除");
    }

    @Test
    public void L3_deletePart_no() {
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        Reporter.infoExec("期望值row1:" + row1);
        int row2 = Integer.parseInt(String.valueOf(gridFindRowByColumn(holiday.grid, holiday.gridcolumn_Name, "Спасибо_week1", sort_ascendingOrder)));
        int row3 = Integer.parseInt(String.valueOf(gridFindRowByColumn(holiday.grid, holiday.gridcolumn_Name, "今天天气不错_week3", sort_ascendingOrder)));
//        全部勾选
        gridSeleteAll(holiday.grid);
//        取消勾选OutboundRoute2和OutboundRoute3
        gridCheck(holiday.grid, row2, holiday.gridcolumn_Check);
        gridCheck(holiday.grid, row3, holiday.gridcolumn_Check);
//        点击删除按钮
        holiday.delete.click();
        holiday.delete_no.click();
        ys_waitingLoading(holiday.grid_Mask);
        int row4 = Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        Reporter.infoExec("实际值row4:" + row4);
        YsAssert.assertEquals(row4, row1, "全部勾选，再取消某条的勾选后-取消删除");
    }

    @Test
    public void L4_deletePart_yes() {
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-确定删除"); //执行操作
        holiday.delete.click();
        holiday.delete_yes.click();
        ys_waitingLoading(holiday.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        Reporter.infoExec("实际row6:" + row1);
        ys_apply();
        YsAssert.assertEquals(row1, 2, "全部勾选，再取消某条的勾选后-确定删除");
    }

    @Test
    public void L5_deleteAll_no() {
        Reporter.infoExec(" 全部勾选-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
//        全部勾选
        gridSeleteAll(holiday.grid);
//        点击删除按钮
        holiday.delete.click();
        holiday.delete_no.click();
        ys_waitingLoading(holiday.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        Reporter.infoExec("实际row2:" + row2);
        YsAssert.assertEquals(row1, row2, "全部勾选-取消删除");
    }

    @Test
    public void L6_deleteAll_yes() {
        Reporter.infoExec(" 全部勾选-确定删除"); //执行操作
        holiday.delete.click();
        holiday.delete_yes.click();
        ys_waitingLoading(holiday.grid_Mask);
        int row = Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        Reporter.infoExec("实际row:" + row);
        ys_apply();
        YsAssert.assertEquals(row, 0, "全部勾选-确定删除");
    }

    @Test
    public void M1_deleteOne_no() {
        time_Conditions.timeConditions.click();
        timeConditions.timeConditions.click();
//        删除holiday
        Reporter.infoExec(" 删除单个timeConditions——选择no"); //执行操作
//       定位要删除的那条呼入路由
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid, timeConditions.gridcolumn_Name, "时间条件7", sort_ascendingOrder)));
        //还未操作前呼出路由的所有条数
        int rows = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        Reporter.infoExec("预期值:" + rows);
        gridClick(timeConditions.grid, row, timeConditions.gridDelete);
        timeConditions.delete_no.click();
        ys_waitingLoading(timeConditions.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        Reporter.infoExec("实际值:" + row1);
        YsAssert.assertEquals(row1, rows, "删除单个timeConditions-取消删除");
    }

    @Test
    public void M2_deleteOne_yes() {
        Reporter.infoExec(" 删除单个timeConditions——选择yes"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        //       定位要删除的那条呼入路由
        int row1 = Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid, timeConditions.gridcolumn_Name, "时间条件7", sort_ascendingOrder)));
        gridClick(timeConditions.grid, row1, timeConditions.gridDelete);
        timeConditions.delete_yes.click();
        ys_waitingLoading(timeConditions.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        Reporter.infoExec("实际值row2:" + row2);
        int row3 = row - 1;
        Reporter.infoExec("期望值row3:" + row3);
        ys_apply();
        YsAssert.assertEquals(row2, row3, "删除单个timeConditions——确定删除");
    }

    @Test
    public void M3_deletePart_no() {
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        Reporter.infoExec("期望值row1:" + row1);
        int row2 = Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid, timeConditions.gridcolumn_Name, "Спасибо", sort_ascendingOrder)));
        int row3 = Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid, timeConditions.gridcolumn_Name, "*", sort_ascendingOrder)));
//        全部勾选
        gridSeleteAll(timeConditions.grid);
//        取消勾选OutboundRoute2和OutboundRoute3
        gridCheck(timeConditions.grid, row2, timeConditions.gridcolumn_Check);
        gridCheck(timeConditions.grid, row3, timeConditions.gridcolumn_Check);
//        点击删除按钮
        timeConditions.delete.click();
        timeConditions.delete_no.click();
        ys_waitingLoading(timeConditions.grid_Mask);
        int row4 = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        Reporter.infoExec("实际值row4:" + row4);
        YsAssert.assertEquals(row4, row1, "全部勾选，再取消某条的勾选后-取消删除");
    }

    @Test
    public void M4_deletePart_yes() {
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-确定删除"); //执行操作
        timeConditions.delete.click();
        timeConditions.delete_yes.click();
        ys_waitingLoading(timeConditions.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        Reporter.infoExec("实际row6:" + row1);
        ys_apply();
        YsAssert.assertEquals(row1, 2, "全部勾选，再取消某条的勾选后-确定删除");
    }

    @Test
    public void M5_deleteAll_no() {
        Reporter.infoExec(" 全部勾选-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
//        全部勾选
        gridSeleteAll(timeConditions.grid);
//        点击删除按钮
        timeConditions.delete.click();
        timeConditions.delete_no.click();
        ys_waitingLoading(timeConditions.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        Reporter.infoExec("实际row2:" + row2);
        YsAssert.assertEquals(row1, row2, "全部勾选-取消删除");
    }

    @Test
    public void M6_deleteAll_yes() {
        Reporter.infoExec(" 全部勾选-确定删除"); //执行操作
        timeConditions.delete.click();
        timeConditions.delete_yes.click();
        ys_waitingLoading(timeConditions.grid_Mask);
        int row = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        Reporter.infoExec("实际row:" + row);
        ys_apply();
        YsAssert.assertEquals(row, 0, "全部勾选-确定删除");
    }

    @Test
    public void N_recovery1()  {
//        新增默认的workday
//        添加默认的时间条件
        Reporter.infoExec(" 添加时间条件Workday:默认的工作时间"); //执行操作
        m_callcontrol.addTimeContion("Workday", "08:30", "12:00", false, "mon", "tue", "wed", "thu", "fri");
        Reporter.infoExec(" 编辑Workday");
        gridClick(timeConditions.grid, gridFindRowByColumn(timeConditions.grid, timeConditions.gridcolumn_Name, "Workday", sort_ascendingOrder), timeConditions.gridEdit);
        ys_waitingTime(3000);
        add_time_condition.getAddTime(1).click();
//        add_time_condition.addTime.click();
        ys_waitingTime(1000);
        ArrayList<String> arrayStartTime = new ArrayList<>();
        arrayStartTime.add("starthour");
        arrayStartTime.add("endhour");
        ArrayList<String> arrayTime = new ArrayList<>();
        arrayTime.add("14");
        arrayTime.add("18");
        add_time_condition.setTime_One(2, arrayStartTime, arrayTime);
        add_time_condition.save.click();
    }

    @Test
    public void N_recovery2() {
//        删除呼出路由
        outboundRoutes.outboundRoutes.click();
        setPageShowNum(outboundRoutes.grid, 100);
        int row1 = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutRoute_test1", sort_ascendingOrder)));
        int row2 = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutRoute_test2", sort_ascendingOrder)));
        int row3 = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutRoute_test3", sort_ascendingOrder)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutRoute_test4", sort_ascendingOrder)));
        int row5 = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutRoute_test5", sort_ascendingOrder)));
        int row6 = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutRoute_test6", sort_ascendingOrder)));
        int row7 = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutRoute_test7", sort_ascendingOrder)));
//      勾选以上定位的几行
        gridCheck(outboundRoutes.grid, row1, outboundRoutes.gridcolumn_Check);
        gridCheck(outboundRoutes.grid, row2, outboundRoutes.gridcolumn_Check);
        gridCheck(outboundRoutes.grid, row3, outboundRoutes.gridcolumn_Check);
        gridCheck(outboundRoutes.grid, row4, outboundRoutes.gridcolumn_Check);
        gridCheck(outboundRoutes.grid, row5, outboundRoutes.gridcolumn_Check);
        gridCheck(outboundRoutes.grid, row6, outboundRoutes.gridcolumn_Check);
        gridCheck(outboundRoutes.grid, row7, outboundRoutes.gridcolumn_Check);
        //        点击删除按钮
        outboundRoutes.delete.click();
        outboundRoutes.delete_yes.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
    }

    @Test
    public void N_recovery3() {
//       恢复最开始的呼入路由——先删除再新增
        inboundRoutes.inboundRoutes.click();
        deletes(" 删除所有呼入路由", inboundRoutes.grid, inboundRoutes.delete, inboundRoutes.delete_yes, inboundRoutes.grid_Mask);
        Reporter.infoExec(" 添加呼入路由InRoute1"); //执行操作
        ArrayList<String> arraytrunk1 = new ArrayList<>();
        arraytrunk1.add("all");
        m_callcontrol.addInboundRoutes("InRoute1", "", "", add_inbound_route.s_extensin, "1000", arraytrunk1);
    }

    @Test
    public void N_recovery4() {
        Reporter.infoExec(" 取消分机1000具有拨打时间特征码的权限"); //执行操作
        settings.general_tree.click();
        featureCode.featureCode.click();
        ys_waitingTime(5000);
        featureCode.setExtensionPermission.click();
        ys_waitingTime(5000);
        featureCode.extension_RemoveAllFromSelect.click();
        ys_waitingTime(1000);
        featureCode.list_save.click();
        featureCode.save.click();
        ys_apply();
    }

    @Test
    public void N_recovery5() {
        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        dateTime.synchronizeWithNTPServer.click();
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            pageDeskTop.reboot_Yes.click();
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            pageDeskTop.loginout_OK.click();
            ys_waitingTime(8000);
            login(LOGIN_USERNAME, LOGIN_PASSWORD);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        } else {
            System.out.println("没有检测到reboot_Yes和reboot_No");
            if (pageDeskTop.taskBar_User.isDisplayed()) {
                System.out.println("logout");
                logout();
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            } else if (pageLogin.username.isDisplayed()) {
                System.out.println("login");
                login(LOGIN_USERNAME, LOGIN_PASSWORD);
            }
            ys_waitingTime(3000);
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.maintanceShortcut.click();
            maintenance.reboot.click();
            reboot.reboot.click();
            ys_waitingTime(1000);
            reboot.reboot_Yes.click();
            System.out.println("datetime wait reboot——maintenance ");
            waitReboot();
        }
        login(LOGIN_USERNAME, LOGIN_PASSWORD);
    }

    //    AfterMethod是在每个Test执行后都要来执行的方法
    @AfterMethod
    public void AfterMethod() {
        if (cdRandRecordings.deleteCDR.isDisplayed()) {
            closeCDRRecord();
        }
    }

    @AfterClass
    public void AfterClass()  {
        Reporter.infoAfterClass("执行完毕：====== TimeCondition ======"); //执行操作
        quitDriver();
        pjsip.Pj_Destory();

        ys_waitingTime(10000);
        killChromePid();
    }
}
