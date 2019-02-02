package com.yeastar.linkustest.pobject.Me.Settings;

import org.openqa.selenium.By;

public class PersonalInformation  {

    public By back = By.id("com.yeastar.linkus:id/actionbar_left_iv");
    public By edit = By.id("com.yeastar.linkus:id/actionbar_right_iv");

    public By extension = By.id("com.yeastar.linkus:id/user_info_profile_tv");
    public By avatar = By.id("com.yeastar.linkus:id/user_info_profile_civ");
    public By name = By.xpath("//*[@resource-id='com.yeastar.linkus:id/name_tv'][@text='Name']/../android.widget.RelativeLayout/android.widget.EditText");
    public By mobile = By.xpath("//*[@resource-id='com.yeastar.linkus:id/name_tv'][@text='Mobile']/../android.widget.RelativeLayout/android.widget.EditText");
    public By email = By.xpath("//*[@resource-id='com.yeastar.linkus:id/name_tv'][@text='Email']/../android.widget.RelativeLayout/android.widget.EditText");
}
