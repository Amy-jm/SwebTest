package com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Bulk_Extensions.Add_Bulk_Extensions_Advanced;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Bulk_Extensions_Advanced {
    /**
     * 进入Advanced
     */
    public SelenideElement advanced = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Advanced\"]"));

    /**
     * VoIP Settings
     */
    public SelenideElement NAT = $(By.id("nat-displayEl"));
    public SelenideElement qualify = $(By.id("qualify-displayEl"));
    public SelenideElement registerRemotely = $(By.id("remoteregister-displayEl"));
    public SelenideElement enableSRTP = $(By.id("enablesrtp-displayEl"));
    public SelenideElement transport_button = $(By.id("transport-trigger-picker"));
    public SelenideElement DTMFMode_button = $(By.id("dtmfmode-trigger-picker"));
    public SelenideElement enableUserAgentRegistrationAuthorization = $(By.id("enableuseragent-displayEl"));
    public SelenideElement userAgent = $(By.id("limituseragent0-inputEl"));
    public SelenideElement userAgent_add = $(By.xpath(""));

    /**
     * IP Restriction
     */
    public SelenideElement enableIPRestriction = $(By.id("enableiprestrict-displayEl"));
    public SelenideElement permittedIP = $(By.id("permitip0-inputEl"));
    public SelenideElement subnetmask = $(By.id("permitmask0-inputEl"));
    public SelenideElement enableIPRestriction_add = $(By.xpath(""));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));


}
