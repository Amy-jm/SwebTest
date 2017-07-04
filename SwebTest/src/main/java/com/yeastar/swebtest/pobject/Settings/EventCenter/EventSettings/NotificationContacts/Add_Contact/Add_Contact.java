package com.yeastar.swebtest.pobject.Settings.EventCenter.EventSettings.NotificationContacts.Add_Contact;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Add_Contact {
    public SelenideElement chooseContact = $(By.id("st-ec-extensionid-inputEl"));
    /**
     * Notification Method
     */
    public SelenideElement email = $(By.id("st-ec-enableemail-displayEl"));
    public SelenideElement SMS = $(By.id("st-ec-enablesms-displayEl"));
    public SelenideElement callExtension = $(By.id("st-ec-enableextension-displayEl"));
    public SelenideElement callMobile = $(By.id("st-ec-enablemobile-displayEl"));

    public SelenideElement setEmail = $(By.xpath(".//a[starts-with(@class,\"cp-link\") and text()=\"Set Email\"]"));

    public SelenideElement mobileNumber = $(By.id("st-ec-mobileprefix-inputEl"));
    public SelenideElement setMobileNumber = $(By.xpath(".//a[starts-with(@class,\"cp-link\") and text()=\"Set Mobile Number\"]"));


    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
