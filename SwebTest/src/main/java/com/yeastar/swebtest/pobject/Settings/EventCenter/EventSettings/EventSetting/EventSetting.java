package com.yeastar.swebtest.pobject.Settings.EventCenter.EventSettings.EventSetting;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class EventSetting {
    public SelenideElement eventSetting = $(By.xpath(".//div[starts-with(@id,\"eventsetting\")]//span[ text()=\"Event Settings\"]"));

    public String EventSetting_Record  = "record";
    public String EventSetting_Noticication = "notification";
//    String EventSetting

    public  int Record_ModifyAdministratorPassword = 0;
    public  int Record_UserLoginSuccess = 1;
    public int Record_UserLoginFailed = 2;
    public int Rescord_Lockout = 3;
//    30.6.0.X
    public  int Record_EmergencyCall_306 = 8;
//    30.7.0.X
    public  int Record_EmergencyCall_307 = 9;//设备未装linkus的情况下，如果有装linkus，要往后移两行
//    CloudPBX
    public  int Record_EmergencyCall_CloudPBX = 11;

    public int Notification_ModifyAdministratorPassword = 0;
    public int Notification_UserLoginSuccess = 1;
    public int Notification_UserLoginFailed = 2;
    public int Notification_Lockout = 3;
//    30.6.0.X
//    public int Notification_EmergencyCall = 8;
//    30.7.0.X
    public int Notification_EmergencyCall = 9;
//    CloudPBX
    public int Notification_EmergencyCall_CloudPBX = 11;



}
