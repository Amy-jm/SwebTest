package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.SpeedDial.Import_Speed_Dial_Number;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Import_Speed_Dial_Number {
    public SelenideElement extensionFile = $(By.id("st-speeddial-filename-inputEl"));
    public SelenideElement browse = $(By.id("st-speeddial-filename-button-fileInputEl"));
    public SelenideElement Import = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Import\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Cancel\"]"));

}
