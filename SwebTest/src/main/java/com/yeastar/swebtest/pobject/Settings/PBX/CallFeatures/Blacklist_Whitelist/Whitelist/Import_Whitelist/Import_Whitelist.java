package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Blacklist_Whitelist.Whitelist.Import_Whitelist;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Import_Whitelist {
    public SelenideElement extensionFile = $(By.id("st-blacklist-filename-inputEl"));
    public SelenideElement browse = $(By.id("st-blacklist-filename-button-fileInputEl"));
    public SelenideElement Import = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Import\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Cancel\"]"));

}
