package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.TimeConditions;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Time_Conditions {

    public SelenideElement timeConditions = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Time Conditions\"]"));


}
