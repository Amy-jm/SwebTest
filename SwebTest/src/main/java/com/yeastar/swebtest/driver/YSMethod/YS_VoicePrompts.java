package com.yeastar.swebtest.driver.YSMethod;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.SwebDriver.executeJs;
import static com.yeastar.swebtest.driver.SwebDriver.ys_waitingLoading;
import static com.yeastar.swebtest.driver.SwebDriver.ys_waitingTime;


/**
 * Created by Yeastar on 2017/7/25.
 */
public class YS_VoicePrompts {

    /**
     * 添加 MOH playlist
     * @param name
     * @param type
     */
    public void addMOHPlaylist(String name,int type){
        add_moh_playlist.name.setValue(name);
        if(type == add_moh_playlist.playlistOrder_Random){
            executeJs("Ext.getCmp('st-moh-playsort').setValue('random')");
        }else if(type == add_moh_playlist.playlistOrder_alphabetical){
            executeJs("Ext.getCmp('st-moh-playsort').setValue('alpha')");
        }

        add_moh_playlist.save.click();
        ys_waitingTime(5000);
//        ys_waitingLoading(musicOnHold.grid_Mask);

    }
}
