package com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.NAT;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class NAT {
    public SelenideElement NAT = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"NAT\"]"));

    public SelenideElement NATType = $(By.id("st-sip-nattype-trigger-picker"));
    public SelenideElement NATMode = $(By.id("st-sip-natmode-trigger-picker"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'sip-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'sip-')]//span[text()='Cancel']"));

}
