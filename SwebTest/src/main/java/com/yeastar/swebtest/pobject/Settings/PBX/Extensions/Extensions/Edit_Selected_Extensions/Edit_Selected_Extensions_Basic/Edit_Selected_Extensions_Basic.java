package com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Edit_Selected_Extensions.Edit_Selected_Extensions_Basic;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Edit_Selected_Extensions_Basic {
    /**
     * 进入Basic
     */
    public SelenideElement basic = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Basic\"]"));

    public SelenideElement registrationPaaword_checkbox = $(By.xpath(""));
    public SelenideElement registrationPaaword = $(By.id("regpasstype-trigger-picker"));

    public SelenideElement userPassword_checkbox = $(By.xpath(""));
    public SelenideElement userPassword = $(By.id("loginpasstype-trigger-picker"));
    public SelenideElement prefixPassword = $(By.id("loginpassprefix-inputEl"));

    public SelenideElement concurrentRegistrations_checkbox = $(By.xpath(""));
    public SelenideElement concurrentRegistrations = $(By.id("mulmaxregistrations-inputEl"));

    public SelenideElement promptLanguage_checkbox = $(By.xpath(""));
    public SelenideElement promptLanguage = $(By.id("mullanguage-trigger-picker"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
