package com.yeastar.swebtest.driver.YSMethod;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.tools.ysassert.YsAssert;

import java.util.ArrayList;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2017/7/17.
 */
public class YS_CallFeature {
    /**
     * 关闭setting窗口
     */
    public void closeSettingWindow(){
        mySettings.close.click();
        pageDeskTop.boxSettings.shouldBe(Condition.disappear);
    }

    /**
     * 断言表格数据
     * @param line
     * @param number
     * @param name
     * @param memberList
     * @param strategy
     * @throws InterruptedException
     */
    public void assertRingGroup(int line, String number, String name ,ArrayList<String> memberList, String strategy) throws InterruptedException {
        String actualNumber = null;
        String actualName = null;
        String actualmember;
        String actualstrategy = null;
        if(!number.isEmpty()){
            actualNumber = (String) gridContent(ringGroup.grid,line,ringGroup.gridcolumn_Number);
            YsAssert.assertEquals(actualNumber,number);
        }

        if(!name.isEmpty()){
            actualName = (String) gridContent(ringGroup.grid,line,ringGroup.gridcolumn_Name);
            YsAssert.assertEquals(actualName,name);
        }

        if(!strategy.isEmpty()){
            actualName = (String) gridContent(ringGroup.grid,line,ringGroup.gridcolumn_RingStrategy);
            YsAssert.assertEquals(actualstrategy,strategy);
        }
    }

    /**
     * 添加响铃组  Ring Group
     * @param name    响铃组名称  为空则为默认值
     * @param number  响铃组 号码 为空则为默认值
     * @param ringStrategy
     * @param member  分机号
     */
    public void addRingGroup(String name,String number,int ringStrategy, int... member) throws InterruptedException {
        ArrayList<String> memberList = new ArrayList<>();
        for(int item:member){
            System.out.println(String.valueOf(item));
            memberList.add(String.valueOf(item));
        }
        ringGroup.add.click();
        ys_waitingMask();
        if(!name.isEmpty())
            add_ring_group.name.setValue(name);
        if(!number.isEmpty())
            add_ring_group.number.setValue(number);
        if(ringStrategy== 0){
//            if(ringStrategy.equals("Sequentially")){
            if(ringStrategy == 2){
                executeJs("Ext.getCmp('st-rg-ringtype').setValue('ringinorder')");
            }
        }
        //超时下拉框设置 Ext.getCmp("st-rg-timeout").setValue("20")
        listSelect(add_ring_group.list_RingGroup,extensionList,memberList);
        Thread.sleep(1000);
        add_ring_group.save.click();
        Thread.sleep(1000);


        Thread.sleep(1500);
        String lineNum = String.valueOf(gridLineNum(ringGroup.grid)) ;
        m_callFeature.assertRingGroup(Integer.parseInt(lineNum),"",name,memberList,"");
//        closeSettingWindow();
    }

    /**
     * 添加队列
     * @param name
     * @param number  可以为空
     * @param member
     */
    public void addQueue(String name ,String number, int... member) throws InterruptedException {
        ArrayList<String> memberList = new ArrayList<>();
        for(int item:member){
            System.out.println(String.valueOf(item));
            memberList.add(String.valueOf(item));
        }

        queue.add.click();
        ys_waitingMask();
        add_queue_basic.name.setValue(name);
        if(!number.isEmpty()){
            add_queue_basic.number.setValue(number);
        }
        listSelect(add_queue_basic.list_AddQueue,extensionList,memberList);
        add_queue_basic.save.click();
        Thread.sleep(1000);
        String lineNum = String.valueOf(gridLineNum(queue.grid)) ;
        assertQueue(Integer.parseInt(lineNum),number,name,memberList,"");

        ys_waitingLoading(queue.grid_Mask);

    }

    public void addConference(String name) throws InterruptedException {
        conference.add.click();
        ys_waitingMask();
        add_conference.name.setValue(name);
        add_conference.save.click();
        Thread.sleep(1000);

        //断言
        String actualName = null;
        actualName = (String) gridContent(conference.grid,Integer.parseInt(String.valueOf(gridLineNum(conference.grid))),conference.gridcolumn_Name);
        YsAssert.assertEquals(actualName,name);
    }

    public void addPickupGroup(String name, int... member) throws InterruptedException {
        pickupGroup.add.click();;
        Thread.sleep(9000);
        add_pickup_group.name.setValue(name);
        ArrayList<String> memberList = new ArrayList<>();
        for(int item:member){
            memberList.add(String.valueOf(item));
        }
        listSelect(add_pickup_group.list_PickupGroup,extensionList,memberList);
        add_pickup_group.save.click();
        Thread.sleep(1000);

    }

    public void addPagingIntercom(String name,int... member) throws InterruptedException {
        paging_intercom.add.click();
        ys_waitingMask();
        add_paging_intercom.name.setValue(name);
        ArrayList<String> memberList = new ArrayList<>();
        for(int item:member){
            memberList.add(String.valueOf(item));
        }
        listSelect(add_paging_intercom.list_PageingIntercom,extensionList,memberList);
        add_paging_intercom.save.click();

        ys_waitingLoading(paging_intercom.grid_Mask);
        String actualName = (String) gridContent(paging_intercom.grid,Integer.parseInt(String.valueOf(gridLineNum(paging_intercom.grid))),paging_intercom.gridcolumn_Name);
        YsAssert.assertEquals(actualName,name);
    }

    public void addCallBack(String name,String destension,String des) throws InterruptedException {
        callback.add.click();
        Thread.sleep(10000);
        add_callback.name.setValue(name);
        //Extension-->e  Voip-->v  以此类推
        if(!destension.isEmpty()){
            executeJs("Ext.getCmp('"+add_callback.id_destinationType+"').setValue('"+destension+"')");
        }


        if(!des.isEmpty()){
            ArrayList<String> memberList = new ArrayList<>();
            memberList.add(des);
            listSelect(add_callback.id_destinationNumber,extensionList,memberList);
        }
        Thread.sleep(1000);
        add_callback.save.click();
        Thread.sleep(1000);

        ys_waitingLoading(callback.grid_Mask);
        String actualName = null;
        actualName = (String) gridContent(callback.grid,Integer.parseInt(String.valueOf(gridLineNum(callback.grid))),callback.gridcolumn_Name);
        YsAssert.assertEquals(actualName,name);
    }
    /**
     * 断言queue表格
     * @param line
     * @param number
     * @param name
     * @param memberList
     * @param strategy
     * @throws InterruptedException
     */
    public void assertQueue(int line, String number, String name ,ArrayList<String> memberList, String strategy ) throws InterruptedException {
        String actualNumber = null;
        String actualName = null;
        String actualmember;
        String actualstrategy = null;
        if(!number.isEmpty()){
            actualNumber = (String) gridContent(queue.grid,line,queue.gridcolumn_Number);
            YsAssert.assertEquals(actualNumber,number);
        }
        if(!name.isEmpty()){
            actualName = (String) gridContent(queue.grid,line,queue.gridcolumn_Name);
            YsAssert.assertEquals(actualName,name);
        }
        if(!strategy.isEmpty()){
            actualName = (String) gridContent(queue.grid,line,queue.gridcolumn_RingStrategy);
            YsAssert.assertEquals(actualstrategy,strategy);
        }
    }

    public void addDISA(String name,String password, int responseTimeout, int digitTimeout, String... memberOutbound) throws InterruptedException {
        disa.add.click();
        ys_waitingMask();
        add_disa.name.setValue(name);
        ArrayList<String> outboundList = new ArrayList<>();
        for(String item:memberOutbound){
            outboundList.add(item);
        }
        Thread.sleep(10000);
        listSelect(add_disa.list, nameList,outboundList);
        add_disa.save.click();

        ys_waitingLoading(disa.grid_Mask);
    }

    public void addPinList(String name,String addpinlist) throws InterruptedException {

        pinList.add.click();
        add_pin_list.name.setValue(name);
        add_pin_list.recordInCDR.click();
        add_pin_list.PINList.setValue(addpinlist);
        add_pin_list.save.click();

        ys_waitingLoading(pinList.grid_Mask);
        int row= gridFindRowByColumn(pinList.grid,pinList.gridcolumn_Name,name,sort_ascendingOrder);
        String actualName = (String) gridContent(pinList.grid,row,pinList.gridcolumn_Name);
        YsAssert.assertEquals(actualName,name);
    }

    public void addIVR(String name) throws InterruptedException {

        ivr.add.click();
        ys_waitingTime(5000);
        add_ivr_basic.name.setValue(name);
        add_ivr_basic.save.click();

        ys_waitingLoading(ivr.grid_Mask);


    }

    /**
     * 添加黑名单
     * @param name
     * @param type  从blacklist表中获取，type_Inbound, type_Outbound ,type_Both
     * @param number
     */
    public void addBlacklist(String name,int type,int... number) throws InterruptedException {
        add_blacklist.name.setValue(name);
        switch (type){
            case 3: {
                executeJs("Ext.getCmp('st-bw-type').setValue('both')");
            }
            case 2:{
                executeJs("Ext.getCmp('st-bw-type').setValue('outbound')");
            }
            case 1:{
                executeJs("Ext.getCmp('st-bw-type').setValue('inbound')");
            }
        }
        String num = "";
        for(int index:number){
            System.out.println(String.valueOf(index));
            num = num + String.valueOf(index);
            num = num + "\n";
        }
        System.out.println(num);
        add_blacklist.number.setValue(num);
        add_blacklist.save.click();
        Thread.sleep(1000);

        ys_waitingLoading(blacklist.grid_loadMask);

        String actname =  String.valueOf(gridContent(blacklist.grid,Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid))),blacklist.gridcolumn_Name)) ;
        YsAssert.assertEquals(actname,name,"黑名单添加错误");
    }

    public void addWhitelist(String name,int type,int... number) throws InterruptedException {
        add_whitelist.name.setValue(name);
        switch (type){
            case 3: {
                executeJs("Ext.getCmp('st-bw-type').setValue('both')");
            }
            case 2:{
                executeJs("Ext.getCmp('st-bw-type').setValue('outbound')");
            }
            case 1:{
                executeJs("Ext.getCmp('st-bw-type').setValue('inbound')");
            }
        }
        String num = "";
        for(int index:number){
            System.out.println(String.valueOf(index));
            num = num + String.valueOf(index);
            num = num + "\n";
        }
        System.out.println(num);
        add_whitelist.number.setValue(num);

        add_whitelist.save.click();
        Thread.sleep(1000);

        ys_waitingLoading(whitelist.grid_loadMask);

        String actname =  String.valueOf(gridContent(whitelist.grid,Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid))),whitelist.gridcolumn_Name)) ;
        YsAssert.assertEquals(actname,name,"白名单添加错误");
    }

    public void addSpeedDial(String speedDialCode, int phoneNumber) throws InterruptedException {
        speedDial.add.click();
        add_speed_dial.speedDialCode.setValue(speedDialCode);
        add_speed_dial.phoneNumber.setValue(String.valueOf(phoneNumber));

        add_speed_dial.save.click();
        ys_waitingLoading(speedDial.grid_Mask);
        ys_waitingTime(8000);

        String actname =  String.valueOf(gridContent(speedDial.grid,Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid))),speedDial.gridcolumn_SpeedDialCode)) ;
        YsAssert.assertEquals(actname,speedDialCode,"添加speed_dial_name");
        String actname2 =  String.valueOf(gridContent(speedDial.grid,Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid))),speedDial.gridcolumn_PhoneNumber)) ;
        YsAssert.assertEquals(actname2,String.valueOf(phoneNumber),"添加speed_dial_phoneNumber");
    }
}
