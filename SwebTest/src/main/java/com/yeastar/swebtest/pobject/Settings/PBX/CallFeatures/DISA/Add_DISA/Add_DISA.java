package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.DISA.Add_DISA;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_DISA {
    public SelenideElement name = $(By.id("st-disa-name-inputEl"));
    public SelenideElement password = $(By.id("st-disa-pintype-trigger-picker"));
    public SelenideElement responseTimeout = $(By.id("st-disa-responsetimeout-inputEl"));
    public SelenideElement dightTimeout = $(By.id("st-disa-digittimeout-inputEl"));



    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
