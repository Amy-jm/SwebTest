package com.yeastar.linkustest.pobject.Me;

import org.openqa.selenium.By;

public class Voicemail {
    public By back = By.id("com.yeastar.linkus:id/actionbar_left_iv");
    public By unread = By.id("com.yeastar.linkus:id/switch_left_tv");
    public By all = By.id("com.yeastar.linkus:id/switch_right_tv");

    public By nameExtension1 =By.xpath("//*[@resource-id='com.yeastar.linkus:id/voicemail_pullableListView']/android.view.ViewGroup[1]/android.widget.RelativeLayout[1]/android.widget.LinearLayout");
    public By time1 =By.xpath("//*[@resource-id='com.yeastar.linkus:id/voicemail_pullableListView']/android.view.ViewGroup[1]/android.widget.RelativeLayout[1]/android.widget.TextView[1]");
    public By delay1 =By.xpath("//*[@resource-id='com.yeastar.linkus:id/voicemail_pullableListView']/android.view.ViewGroup[1]/android.widget.RelativeLayout[1]/android.widget.TextView[2]");
    public By isRead1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/voicemail_pullableListView']/android.view.ViewGroup[1]/android.widget.RelativeLayout[1]/android.widget.ImageView");
}
