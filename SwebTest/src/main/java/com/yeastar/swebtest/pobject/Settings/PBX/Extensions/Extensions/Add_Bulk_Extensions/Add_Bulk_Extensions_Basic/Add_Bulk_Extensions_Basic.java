package com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Bulk_Extensions.Add_Bulk_Extensions_Basic;

import com.codeborne.selenide.SelenideElement;
import com.yeastar.swebtest.driver.SwebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Bulk_Extensions_Basic {
    /**
     * 进入Basic
     */
    public SelenideElement basic = $(By.xpath(".//div[starts-with(@id,\"extension-edit\")]//span[ text()=\"Basic\"]"));

    /**
     * General
     */
    public SelenideElement SIP = $(By.xpath(".//div[starts-with(@id,\"extension-edit\")]//label[text()='SIP']"));
    public SelenideElement IAX = $(By.xpath(".//div[starts-with(@id,\"extension-edit\")]//label[text()='IAX']"));



    public SelenideElement startExtension = $(By.id("startextension-inputEl"));
    public SelenideElement createNumber = $(By.id("createnumber-inputEl"));
    public String registrationPassword ="regpasstype";
    public SelenideElement registerationFixPassword = $(By.id("regpassprefix-inputEl"));
    public SelenideElement registrationPassword_input =  $(By.id("regpasstype-inputEl"));
    public String userPasswrod = "loginpasstype";
    public SelenideElement prefixPassword = $(By.id("loginpassprefix-inputEl"));
    public SelenideElement concurrentRegistrations = $(By.id("mulmaxregistrations-inputEl"));
    public String promptLanguage= "mullanguage";

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,\"extension-edit\")]//span[ text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,\"extension-edit\")]//span[ text()=\"Cancel\"]"));



}
