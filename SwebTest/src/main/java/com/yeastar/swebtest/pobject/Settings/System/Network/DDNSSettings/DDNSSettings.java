package com.yeastar.swebtest.pobject.Settings.System.Network.DDNSSettings;

import com.codeborne.selenide.SelenideElement;

import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class DDNSSettings {
    public SelenideElement DDNSSettings = $(By.xpath(".//div[starts-with(@id,'network')]//span[ text()='DDNS Settings']"));

    public SelenideElement enableDDNS = $(By.id("st-net-enableddns-boxLabelEl"));
    public String DDNSServer = ("st-net-ddnsserver");
    public SelenideElement username = $(By.id("st-net-username-inputEl"));
    public SelenideElement password = $(By.id("st-net-password-inputEl"));
    public SelenideElement domian = $(By.id("st-net-domain-inputEl"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'network')]//span[ text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'network')]//span[ text()='Cancel']"));

}
