package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Callback.Add_Callback;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_Callback {
    public SelenideElement name = $(By.id("st-callback-name-inputEl"));
    public SelenideElement callbackThrough = $(By.id("st-callback-callbacktrunk-trigger-picker"));
    public SelenideElement delayBeforeCallback = $(By.id("st-callback-delay-inputEl"));
    public SelenideElement strip = $(By.id("st-callback-strip-inputEl"));
    public SelenideElement prepend = $(By.id("st-callback-prepend-inputEl"));
    public SelenideElement destination = $(By.id("st-callback-desttype-trigger-picker"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
