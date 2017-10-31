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
    public SelenideElement basic = $(By.xpath(".//div[starts-with(@id,'extension-edit')]//span[ text()='Basic']"));

    /**
     * General
     */
    public SelenideElement SIP = $(By.id("type-sip-displayEl"));
    public SelenideElement IAX = $(By.id("type-iax-displayEl"));
    public SelenideElement FXS = $(By.id("type-fxs-displayEl"));
    public SelenideElement FXS_port_picker = $(By.id("pbxport-trigger-picker"));
    public String FXS_port = "pbxport";
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
    public String promptLanguage_button = "language";

    public SelenideElement save = $(By.xpath(" .//div[starts-with(@id,'extension-edit')]//span[ text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'extension-edit')]//span[text()='Cancel']"));




}
