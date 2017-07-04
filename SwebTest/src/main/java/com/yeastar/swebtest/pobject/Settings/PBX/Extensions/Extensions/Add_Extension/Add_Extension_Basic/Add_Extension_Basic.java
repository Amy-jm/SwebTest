package com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Extension.Add_Extension_Basic;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Extension_Basic {
    /**
     * 进入Basic
     */
    public SelenideElement basic = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Basic\"]"));

    /**
     * General
     */
    public SelenideElement SIP = $(By.id("type-sip-displayEl"));
    public SelenideElement IAX = $(By.id("type-iax-displayEl"));
    public SelenideElement FXS = $(By.id("type-fxs-displayEl"));
    public SelenideElement extensions = $(By.id("username-inputEl"));
    public SelenideElement callerID = $(By.id("callerid-inputEl"));
    public SelenideElement registrationName = $(By.id("registername-inputEl"));
    public SelenideElement registrationPassword = $(By.id("registerpassword-inputEl"));
    public SelenideElement concurrentRegistrations = $(By.id("maxregistrations-inputEl"));

    /**
     * User Information
     */
    public SelenideElement name = $(By.id("fullname-inputEl"));
    public SelenideElement userPassword = $(By.id("loginpassword-inputEl"));
    public SelenideElement email = $(By.id("email-inputEl"));
    public SelenideElement mobileNumber = $(By.id("mobile-inputEl"));
    public SelenideElement promptLanguage_button = $(By.id("language-trigger-picker"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));



}
