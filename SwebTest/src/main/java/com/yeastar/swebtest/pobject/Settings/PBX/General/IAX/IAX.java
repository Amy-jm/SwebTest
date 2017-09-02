package com.yeastar.swebtest.pobject.Settings.PBX.General.IAX;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class IAX {
    public SelenideElement IAX = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"IAX\"]"));

    public SelenideElement UDPPort = $(By.id("st-iax-bindport-inputEl"));
    public SelenideElement bandwidth = $(By.id("st-iax-bandwidth-trigger-picker"));
    public SelenideElement maximumRegistration = $(By.id("st-iax-maxreg-inputEl"));
    public SelenideElement minimumRegistration = $(By.id("st-iax-minreg-inputEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'iax-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'iax-')]//span[text()='Cancel']"));

}
