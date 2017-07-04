package com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Bulk_Extensions.Add_Bulk_Extensions_Basic;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Bulk_Extensions_Basic {
    /**
     * 进入Basic
     */
    public SelenideElement basic = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Basic\"]"));

    /**
     * General
     */
    public SelenideElement SIP = $(By.xpath(".//label[starts-with(@class,\"x-form-cb-label x-form-cb-label-default x-form-cb-label-after  \") and text()=\"SIP\"]"));
    public SelenideElement IAX = $(By.xpath(".//label[starts-with(@class,\"x-form-cb-label x-form-cb-label-default x-form-cb-label-after  \") and text()=\"IAX\"]"));

    public SelenideElement startExtension = $(By.id("startextension-inputEl"));
    public SelenideElement createNumber = $(By.id("createnumber-inputEl"));
    public SelenideElement registrationPassword_button = $(By.id("regpasstype-trigger-picker"));
    public SelenideElement userPasswrod_button = $(By.id("loginpasstype-trigger-picker"));
    public SelenideElement prefixPassword = $(By.id("loginpassprefix-inputEl"));
    public SelenideElement concurrentRegistrations = $(By.id("mulmaxregistrations-inputEl"));
    public SelenideElement promptLanguage_button = $(By.id("mullanguage-trigger-picker"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));



}
