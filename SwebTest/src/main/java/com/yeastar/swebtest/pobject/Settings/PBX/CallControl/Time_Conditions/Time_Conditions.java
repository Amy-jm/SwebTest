package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.Time_Conditions;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Time_Conditions {

    public SelenideElement timeConditions = $(By.xpath(".//div[starts-with(@id,\"callcontrol\")]//span[ text()=\"Time Conditions\"]"));


}
