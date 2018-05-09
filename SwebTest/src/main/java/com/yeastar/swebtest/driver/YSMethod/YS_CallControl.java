package com.yeastar.swebtest.driver.YSMethod;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.swebtest.driver.Config;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;

import java.util.ArrayList;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2017/7/20.
 */
public class YS_CallControl {
    /**
     * Created by lhr
     * 功能描述：添加时间条件，未进行高级设置
     * 参数说明：name,startTime：00:05,endTime：00：80，daysOfWeek: sun,mon,tue,wed,thu,fri,sat
     */
    public void addTimeContion(String name,String startTime,String endTime, boolean advanceOptions,String... daysOfWeek ) throws InterruptedException {
        timeConditions.add.click();
        add_time_condition.name.setValue(name);
//        add_time_condition.starthour.setValue()
        String[] startTimeList = startTime.split(":");
        String[] endTimeList = endTime.split(":");
        add_time_condition.starthour.setValue(startTimeList[0]);
        add_time_condition.startminu.setValue(startTimeList[1]);
        add_time_condition.endhour.setValue(endTimeList[0]);
        add_time_condition.endminu.setValue(endTimeList[1]);
        ArrayList<String> memberList = new ArrayList<>();
        for(String item:daysOfWeek){
            System.out.println((item));
            memberList.add((item));
        }
        if(memberList.get(0).equals("all")){
            add_time_condition.all.click();
        }else {
            for(int i=0; i<memberList.size(); i++){
                if(memberList.get(i).contains("sun"))
                    add_time_condition.sunday.click();
                else if(memberList.get(i).contains("mon"))
                    add_time_condition.monday.click();
                else if (memberList.get(i).contains("tue"))
                    add_time_condition.tuesday.click();
                else if (memberList.get(i).contains("wed"))
                    add_time_condition.wednesday.click();
                else if (memberList.get(i).contains("thu"))
                    add_time_condition.thursday.click();
                else if (memberList.get(i).contains("fri"))
                    add_time_condition.friday.click();
                else if (memberList.get(i).contains("sat"))
                    add_time_condition.saturday.click();
            }
        }
        if(advanceOptions)
            add_time_condition.advancedOptions.click();
        add_time_condition.save.click();
        pageDeskTop.apply.click();
        ys_waitingLoading(Config.timeConditions.grid_Mask);
        YsAssert.assertEquals(String.valueOf(gridContent(Config.timeConditions.grid,1, Config.timeConditions.gridcolumn_Name)),name,"添加TimeConditions");
    }

    /**
     * 添加HolidayByDay
     * @param name
     * @param startDate   格式为 yyyy-mm-dd   eg:2017-07-19
     * @param endDate     格式为 yyyy-mm-dd   eg:2017-07-19
     */
    public void addHolidayByDay(String name, String startDate, String endDate) throws InterruptedException {
        holiday.add.click();
        add_holiday.name.setValue(name);
        add_holiday.byDay.click();
        add_holiday.startDate.setValue(startDate);
        add_holiday.endDate.setValue(endDate);
        add_holiday.save.click();
//        pageDeskTop.apply.click();
        YsAssert.assertEquals(String.valueOf(gridContent(holiday.grid,1,holiday.gridcolumn_Name)), name, "添加Holiday");
    }

    /**
     * 按月添加Holiday
     * @param name
     * @param startMonth
     * @param startDay
     * @param endMonth
     * @param endDay
     */
    public void addHolidayByMonth(String name, String startMonth, String startDay, String endMonth, String endDay){
        holiday.add.click();
        add_holiday.name.setValue(name);
        add_holiday.byMouth.click();
        comboboxSelect(add_holiday.startMonth,startMonth);
        comboboxSelect(add_holiday.startDay,startDay);
        comboboxSelect(add_holiday.endMonth,endMonth);
        comboboxSelect(add_holiday.endDay,endDay);
        add_holiday.save.click();
        YsAssert.assertEquals(String.valueOf(gridContent(holiday.grid,1,holiday.gridcolumn_Name)), name, "添加Holiday");
    }

    public void addHolidayByWeek(String name,String month,String weekNum,String weekday){
        holiday.add.click();
        add_holiday.name.setValue(name);
        add_holiday.byWeek.click();
        comboboxSelect(add_holiday.weekMonth,month);
        comboboxSelect(add_holiday.weeknum,weekNum);
        comboboxSelect(add_holiday.weekday,weekday);
        add_holiday.save.click();
        YsAssert.assertEquals(String.valueOf(gridContent(holiday.grid,1,holiday.gridcolumn_Name)), name, "添加Holiday");
    }
    /**
     *
     * @param name
     * @param patterns  可为空
     * @param strip      可为空
     * @param prepend    可为空
     * @param extList
     * @param trunksList
     * @throws InterruptedException
     */
    public void addOutboundRoute(String name,String patterns,String strip,String prepend,ArrayList extList,ArrayList trunksList) throws InterruptedException {
        outboundRoutes.add.click();
        ys_waitingMask();
        add_outbound_routes.name.setValue(name);
        if(!patterns.isEmpty())
            executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('patterns','"+patterns+"')");
        if(!strip.isEmpty())
            executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('strip','"+strip+"')");
        if(!prepend.isEmpty()){
            executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('prepend','"+prepend+"')");
        }
        Thread.sleep(1000);
        if(trunksList.get(0).equals("all")){
            add_outbound_routes.mt_AddAllToSelect.click();
        }else
            listSelect(add_outbound_routes.list_Trunk,trunkList,trunksList);

        if(extList.get(0).equals("all")){
            add_outbound_routes.me_AddAllToSelect.click();
        }else
            listSelect(add_outbound_routes.list_Extension, extensionList,extList);
        Thread.sleep(1000);
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
    }



    /**
     *
     * @param name
     * @param DID    可为空
     * @param callerID    可为空
     */


//    public void addInboundRoutes(String name, String DID, String callerID, String des1, String des2,String... memberTrunk)  {
//        ArrayList<String> memberList = new ArrayList<>();
//        for(String item:memberTrunk){
//            memberList.add(item);
//        }
//        addInboundRoutes(name,DID,callerID,"","",memberList);
//    }

    public void addInboundRoutes(String name, String DID, String callerID, String des1, String des2, ArrayList<String> memberList){
        inboundRoutes.add.click();
        ys_waitingMask();
        add_inbound_route.name.setValue(name);
        if(!DID.isEmpty()){
            add_inbound_route.DIDPatem.setValue(DID);
        }
        if(!callerID.isEmpty()){
            add_inbound_route.callIDPattem.setValue(callerID);
        }
        System.out.println(" First trunk member: "+memberList.get(0));
        if(memberList.get(0).equals("all")){;
            listSelectAll(add_inbound_route.list);
        }else {
            listSelect(add_inbound_route.list,trunkList,memberList);
        }
        if(!des1.isEmpty()){
            comboboxSelect(add_inbound_route.destinationType,des1);
        }
        if(!des2.isEmpty()){
            if(String.valueOf(des1)==add_inbound_route.s_extension_range){
                add_inbound_route.destinationInput.shouldBe(Condition.exist).setValue(des2);
            }else {
                if(des1.equals("e") || des1.equals("v")) {
                    comboboxSet(add_inbound_route.destination, extensionList, des2);
                }else{
                    comboboxSet(add_inbound_route.destination,nameList, des2);
                }
            }
        }
        ys_waitingTime(1000);
        add_inbound_route.save.click();

        ys_waitingLoading(inboundRoutes.grid_Mask);
    }

    public void addInboundRoutes(String name, String DID, String callerID, String... memberTrunk)  {
        ArrayList<String> memberList = new ArrayList<>();
        for(String item:memberTrunk){
            memberList.add(item);
        }
        addInboundRoutes(name,DID,callerID,"","",memberList);
    }





}
