package com.yeastar.swebtest.driver.YSMethod;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.SwebDriver.executeJs;
import static com.yeastar.swebtest.driver.SwebDriver.ys_waitingLoading;

/**
 * Created by Yeastar on 2017/8/1.
 */
public class YS_Me {

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
}
