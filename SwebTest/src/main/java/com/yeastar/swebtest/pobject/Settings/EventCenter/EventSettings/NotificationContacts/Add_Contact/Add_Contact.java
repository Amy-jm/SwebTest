package com.yeastar.swebtest.pobject.Settings.EventCenter.EventSettings.NotificationContacts.Add_Contact;

import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.cs.A;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Add_Contact {
    public SelenideElement chooseContact = $(By.id("st-ec-extensionid-inputEl"));
    public String chooseContact_id = "st-ec-extensionid";
    /**
     * Notification Method
     */
    public String email = ("st-ec-enableemail");
    public String SMS = ("st-ec-enablesms");
    public String callExtension = "st-ec-enableextension";
    public String callMobile = "st-ec-enablemobile-displayEl";

    public SelenideElement setEmail = $(By.xpath(".//a[starts-with(@class,\"cp-link\") and text()=\"Set Email\"]"));

    public SelenideElement mobileNumber = $(By.id("st-ec-mobileprefix-inputEl"));
    public SelenideElement setMobileNumber = $(By.xpath(".//a[starts-with(@class,\"cp-link\") and text()=\"Set Mobile Number\"]"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'contact-edit')]//span[ text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'contact-edit')]//span[ text()='Cancel']"));

}
