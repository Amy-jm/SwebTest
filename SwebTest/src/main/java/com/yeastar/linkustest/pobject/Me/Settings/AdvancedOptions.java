package com.yeastar.linkustest.pobject.Me.Settings;

import org.openqa.selenium.By;

public class AdvancedOptions {
    public By back = By.id("com.yeastar.linkus:id/actionbar_left_iv");
    public By title = By.id("com.yeastar.linkus:id/actionbar_center_tv");

    public By ringTimeout = By.id("com.yeastar.linkus:id/et_ring_out");
    public By callWaiting = By.id("com.yeastar.linkus:id/cb_call_waiting");
    public By clearCache = By.id("com.yeastar.linkus:id/setting_cache_tv");
    public By outboundPrefix = By.id("com.yeastar.linkus:id/setting_outbound_tv");
    public By backgroudMode = By.id("com.yeastar.linkus:id/setting_background_mode_cb");
    public By backgroudAppSetting = By.id("com.yeastar.linkus:id/layout_setting_background_permission");
    public By audioDebug = By.id("com.yeastar.linkus:id/cb_setting_sound_quality");

    public By outPrefix_add = By.id("com.yeastar.linkus:id/actionbar_right_iv");
    public By outPrefix_name1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/prefix_listview']/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.TextView[1]");
    public By outPrefix_prefix1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/prefix_listview']/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.TextView[2]");
    public By outPrefix_edit1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/prefix_listview']/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.ImageView");
    public By outPrefix_name2 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/prefix_listview']/android.widget.FrameLayout[2]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.TextView[1]");
    public By outPrefix_prefix2 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/prefix_listview']/android.widget.FrameLayout[2]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.TextView[2]");
    public By outPrefix_edit2 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/prefix_listview']/android.widget.FrameLayout[2]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.ImageView");
    public By outPrefix_name3 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/prefix_listview']/android.widget.FrameLayout[3]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.TextView[1]");
    public By outPrefix_prefix3 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/prefix_listview']/android.widget.FrameLayout[3]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.TextView[2]");
    public By outPrefix_edit3 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/prefix_listview']/android.widget.FrameLayout[3]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.ImageView");

    public By outPrefixEdit_ok = By.id("com.yeastar.linkus:id/actionbar_right_iv");
    public By outPrefixEdit_name = By.id("com.yeastar.linkus:id/name_et");
    public By outPrefixEdit_num = By.id("com.yeastar.linkus:id/prefix_et");

    public By clearCache_ok = By.name("OK");
    public By clearCache_cancel = By.name("CANCEL");

}
