package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.SMS;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class SMS {
    public SelenideElement SMS = $(By.xpath(".//span[starts-with(@class,\"toolbartip\") and text()=\"SMS\"]"));

    /**
     * Enable Email To SMS
     */
    public SelenideElement countryCode = $(By.id("st-sms-countrycode-trigger-picker"));
    public SelenideElement emailCheckingInterval = $(By.id("st-sms-pop3interval-inputEl"));
    public SelenideElement accessCode = $(By.id("st-sms-pincode-inputEl"));
    /**
     * Enable SMS To Email
     */


    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
