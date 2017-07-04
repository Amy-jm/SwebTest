package com.yeastar.swebtest.pobject.Settings.System.Network.DDNSSettings;

import com.codeborne.selenide.SelenideElement;
import com.sun.jna.platform.win32.WinNT;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class DDNSSettings {
    public SelenideElement DDNSSettings = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"DDNS Settings\"]"));

    public SelenideElement enableDDNS = $(By.id("st-net-enableddns-boxLabelEl"));
    public SelenideElement DDNSServer = $(By.id("st-net-ddnsserver-trigger-picker"));
    public SelenideElement username = $(By.id("st-net-username-inputEl"));
    public SelenideElement password = $(By.id("st-net-password-inputEl"));
    public SelenideElement domian = $(By.id("st-net-domain-inputEl"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
