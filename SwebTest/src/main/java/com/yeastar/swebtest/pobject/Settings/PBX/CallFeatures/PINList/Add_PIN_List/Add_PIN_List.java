package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.PINList.Add_PIN_List;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_PIN_List {
    public SelenideElement name = $(By.id("st-pinset-name-inputEl"));
    public SelenideElement recordInCDR = $(By.id("st-pinset-recordincdr-displayEl"));
    public SelenideElement PINList = $(By.id("st-pinset-pinlist-inputEl"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
