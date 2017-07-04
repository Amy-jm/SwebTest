package com.yeastar.swebtest.pobject.Settings.PBX.EmergencyNumber.Add_Emergency_Number;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_Emergency_Number {
    public SelenideElement emergencyNumber = $(By.id("st-emergency-number-inputEl"));
    public SelenideElement trunk = $(By.id("prefix0-inputEl"));
    public SelenideElement trunk_button = $(By.id("trunk0-trigger-picker"));
    public SelenideElement notification = $(By.id("admin0-trigger-picker"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
