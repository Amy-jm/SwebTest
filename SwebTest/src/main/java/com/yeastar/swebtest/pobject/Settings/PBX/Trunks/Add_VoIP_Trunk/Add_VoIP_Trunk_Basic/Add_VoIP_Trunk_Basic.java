package com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Add_VoIP_Trunk.Add_VoIP_Trunk_Basic;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_VoIP_Trunk_Basic {

    public SelenideElement basic = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Basic\"]"));

    public SelenideElement trunkStatus = $(By.id("trunk-status-inputEl"));
    public SelenideElement protocol = $(By.id("type-inputEl"));
    public SelenideElement providerName = $(By.id("trunkname-inputEl"));
    public SelenideElement hostname = $(By.id("hostname-inputEl"));
    public SelenideElement hostnameIP = $(By.id("hostport-inputEl"));
    public SelenideElement domain = $(By.id("fromdomain-inputEl"));
    public SelenideElement username = $(By.id("username-inputEl"));
    public SelenideElement authenticationName = $(By.id("ysauth-inputEl"));
    public SelenideElement calledIDNumber = $(By.id("globaldod-inputEl"));
    public SelenideElement trunkType = $(By.id("trunktype-inputEl"));
    public SelenideElement transport = $(By.id("transport-inputEl"));
    public SelenideElement password = $(By.id("secret-inputEl"));
    public SelenideElement fromUser = $(By.id("fromuser-inputEl"));
    public SelenideElement callerIDName = $(By.id("calleridname-inputEl"));
    public SelenideElement enableProxyServer = $(By.id("enableproxy-displayEl"));
    public SelenideElement outboundProxyServer = $(By.id("proxyserver-inputEl"));
    public SelenideElement outboundProxyServerPort = $(By.id("proxyport-inputEl"));
    public SelenideElement enableSLA = $(By.id("enablesla-displayEl"));


    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
