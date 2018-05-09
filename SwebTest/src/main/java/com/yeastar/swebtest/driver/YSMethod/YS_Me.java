package com.yeastar.swebtest.driver.YSMethod;

import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.ysassert.YsAssert;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.SwebDriver.executeJs;
import static com.yeastar.swebtest.driver.SwebDriver.ys_waitingLoading;

/**
 * Created by Yeastar on 2017/8/1.
 */
public class YS_Me extends SwebDriver {

    public void addMeBlacklist(String name,String type ,int... number) throws InterruptedException {
        me_blacklist.add.click();
        me_add_blacklist.name.setValue(name);
        String numberlist="";
        for(int index:number){
            numberlist = numberlist+String.valueOf(index);
            numberlist = numberlist+"\n";
        }

        me_add_blacklist.number.setValue(numberlist);

        executeJs("Ext.getCmp('"+me_add_blacklist.routeTypeId+"').setValue('"+type+"')");
        me_add_blacklist.save.click();

        ys_waitingLoading(me_blacklist.grid_Mask);
    }


    public void addMeWhitelist(String name,String type ,int... number) throws InterruptedException {
        me_whitelist.add.click();
        me_add_whitelist.name.setValue(name);
        String numberlist="";
        for(int index:number){
            numberlist = numberlist+String.valueOf(index);
            numberlist = numberlist+"\n";
        }

        me_add_whitelist.number.setValue(numberlist);

        executeJs("Ext.getCmp('"+me_add_whitelist.routeTypeId+"').setValue('"+type+"')");
        me_add_whitelist.save.click();

        ys_waitingLoading(me_whitelist.grid_Mask);
    }

//    Caroline新增
    public void addMeBlacklist_String(String name,String type ,String... number) throws InterruptedException {
        me_blacklist.add.click();
        me_add_blacklist.name.setValue(name);
        String numberlist="";
        for(String index:number){
            numberlist = numberlist+String.valueOf(index);
            numberlist = numberlist+"\n";
        }

        me_add_blacklist.number.setValue(numberlist);

        executeJs("Ext.getCmp('"+me_add_blacklist.routeTypeId+"').setValue('"+type+"')");
        me_add_blacklist.save.click();

        ys_waitingLoading(me_blacklist.grid_Mask);
    }

//    Caroline新增
    public void addMeWhitelist_String(String name,String type ,String... number) throws InterruptedException {
        me_whitelist.add.click();
        me_add_whitelist.name.setValue(name);
        String numberlist="";
        for(String index:number){
            numberlist = numberlist+String.valueOf(index);
            numberlist = numberlist+"\n";
        }

        me_add_whitelist.number.setValue(numberlist);

        executeJs("Ext.getCmp('"+me_add_whitelist.routeTypeId+"').setValue('"+type+"')");
        me_add_whitelist.save.click();

        ys_waitingLoading(me_whitelist.grid_Mask);
    }

//    Caroline新增
public void checkCDR(String caller, String callee, String status)  {
    checkCDR(caller,callee,status,"",1);
}
    public void checkCDR(String caller, String callee, String status,String communition ){
        checkCDR(caller,callee,status,communition,1);
    }
    public void checkCDR(String caller, String callee, String status,String communition ,int row)  {
        me.me.click();
        me.me_CDRandRecording.click();
        me_cdRandRecording.search.click();
        ys_waitingLoading(me_cdRandRecording.grid_Mask);
        YsAssert.assertEquals(String.valueOf(gridContent(me_cdRandRecording.grid,row,1)).trim(),caller,"CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(me_cdRandRecording.grid,row,2)).trim(),callee,"CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(me_cdRandRecording.grid,row,5)).trim(),status,"CDR_Status检测");
        if(!communition.isEmpty()){
            YsAssert.assertEquals(String.valueOf(gridContent(me_cdRandRecording.grid,row,me_cdRandRecording.gridColumn_CommunicationType)).trim(),communition,"CDR通讯类型检测");
        }
//        me_cdRandRecording.me_closeCDRRecord();
    }
}
