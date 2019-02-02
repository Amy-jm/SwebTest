package com.yeastar.linkustest.pobject.Me.Settings;

import org.openqa.selenium.By;

public class Settings {

    public By back = By.id("com.yeastar.linkus:id/actionbar_left_iv");
    public By title = By.id("com.yeastar.linkus:id/actionbar_center_tv");

    public By presence = By.name("Presence");
    public By audioOptions = By.id("com.yeastar.linkus:id/setting_AudioOptions_tv");
    public By advancedOptions = By.id("com.yeastar.linkus:id/setting_AdvanceOptions_tv");
    public By messageNotification = By.id("com.yeastar.linkus:id/setting_msg_notification_tv");
    public By reportProblem = By.id("com.yeastar.linkus:id/setting_bug_report_tv");
    public By passwordManagement = By.id("com.yeastar.linkus:id/setting_password_tv");
    public By about = By.id("com.yeastar.linkus:id/setting_about_rl");



    public By logout = By.id("com.yeastar.linkus:id/setting_logout_btn");

}
