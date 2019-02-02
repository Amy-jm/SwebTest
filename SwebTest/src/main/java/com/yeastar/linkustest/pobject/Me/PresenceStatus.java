package com.yeastar.linkustest.pobject.Me;

import org.openqa.selenium.By;

public class PresenceStatus {
    public By available = By.xpath("//*[@resource-id=\"com.yeastar.linkus:id/lv_presence_status\"]/android.widget.RelativeLayout[1]");
    public By away = By.xpath("//*[@resource-id=\"com.yeastar.linkus:id/lv_presence_status\"]/android.widget.RelativeLayout[2]");
    public By dnd  = By.xpath("//*[@resource-id=\"com.yeastar.linkus:id/lv_presence_status\"]/android.widget.RelativeLayout[3]");
    public By lunchBreak      = By.xpath("//*[@resource-id=\"com.yeastar.linkus:id/lv_presence_status\"]/android.widget.RelativeLayout[4]");
    public By businessTrip      = By.xpath("//*[@resource-id=\"com.yeastar.linkus:id/lv_presence_status\"]/android.widget.RelativeLayout[5]");

    public By back = By.id("com.yeastar.linkus:id/actionbar_left_iv");
}
