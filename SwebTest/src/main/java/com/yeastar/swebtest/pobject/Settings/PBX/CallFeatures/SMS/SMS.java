package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.SMS;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class SMS {

    public SelenideElement SMS = $(By.xpath(".//div[starts-with(@id,'callfeature')]//span[ text()='SMS']"));

    /**
     * Enable Email To SMS
     */
    public String countryCode ="st-sms-countrycode";
    public String emailCheckingInterval = "st-sms-pop3interval";
    public SelenideElement accessCode = $(By.id("st-sms-pincode-inputEl"));
    /**
     * Enable SMS To Email
     */
    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'sms')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'sms')]//span[text()='Cancel']"));

}
