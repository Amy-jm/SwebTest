package com.yeastar.swebtest.pobject.Settings.EventCenter.EventSettings.EventSetting;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class EventSetting {
    public SelenideElement eventSetting = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Event Settings\"]"));

}
