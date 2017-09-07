package com.yeastar.swebtest.driver.YSMethod;

import com.yeastar.swebtest.tools.ysassert.YsAssert;

import java.util.ArrayList;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2017/7/20.
 */
public class YS_CallControl {

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
        ys_waitingLoading(time_conditions.gridLoading);
        YsAssert.assertEquals(String.valueOf(gridContent(time_conditions.grid,1,time_conditions.gridcolumn_Name)),name,"TimeConditions名称错误");
    }

    /**
     * 添加Holiday
     * @param name
     * @param type      1表示点击By Data    2：By Month  3:By Week
     * @param startDate   格式为 yyyy-mm-dd   eg:2017-07-19
     * @param endDate     格式为 yyyy-mm-dd   eg:2017-07-19
     */
    public void addHoliday(String name, int type, String startDate, String endDate) throws InterruptedException {
        holiday.add.click();
        add_holiday.name.setValue(name);
        if(type == 1){
            add_holiday.byDay.click();
        }else if (type ==2){
            add_holiday.byMouth.click();
        }else if (type == 3){
            add_holiday.byWeek.click();
        }
        add_holiday.startDate.setValue(startDate);
        add_holiday.endDate.setValue(endDate);
        add_holiday.save.click();
        pageDeskTop.apply.click();


        YsAssert.assertEquals(String.valueOf(gridContent(holiday.grid,1,holiday.gridcolumn_Name)),
                name,
                "TimeConditions名称错误");
    }

    /**
     *
     * @param name
     * @param patterns  可为空
     * @param strip      可为空
     * @param prepend    可为空
     * @param extList
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
        if(extList.get(0).equals("all")){
            add_outbound_routes.me_AddAllToSelect.click();
        }else
            listSelect(add_outbound_routes.list_Extension, extensionList,extList);
        Thread.sleep(1000);
        if(trunksList.get(0).equals("all")){
            add_outbound_routes.mt_AddAllToSelect.click();
        }else
            listSelect(add_outbound_routes.list_Trunk,trunkList,trunksList);

        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);

    }

    /**
     *
     * @param name
     * @param DID    可为空
     * @param callerID    可为空
     * @param memberTrunk  eg:"BRI2-4,BRI-3-1, "从页面看是除括号内的内容
     * @throws InterruptedException
     */
    public void addInboundRoutes(String name, String DID,String callerID, String... memberTrunk) throws InterruptedException {
        inboundRoutes.add.click();
        ys_waitingMask();
        add_inbound_route.name.setValue(name);
        if(!DID.isEmpty()){
            add_inbound_route.DIDPatem.setValue(DID);
        }
        if(!callerID.isEmpty()){
            add_inbound_route.callIDPattem.setValue(callerID);
        }
        ArrayList<String> memberList = new ArrayList<>();
        for(String item:memberTrunk){
            memberList.add(item);
        }
        if(memberList.get(0).equals("all")){
            String Id = "";
             String  num = getListCount(add_inbound_route.list);
             for(int i=1; i<=Integer.parseInt(num); i++){
                 String listId = (String)getListId(add_inbound_route.list,i);
                 Id = listId + "," + Id;
             }
            Id = Id.substring(0,Id.length()-1);
            listSetValue(add_inbound_route.list, Id);
        }
        Thread.sleep(1000);
        add_inbound_route.save.click();

        ys_waitingLoading(inboundRoutes.gridLoading);
    }
}
