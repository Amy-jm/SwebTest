package com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Edit_Selected_Extensions.Edit_Selected_Extensions_Advanced;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Edit_Selected_Extensions_Advanced {
    /**
     * 进入Advanced
     */
    public SelenideElement advanced = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Advanced\"]"));

    /**
     * VoIP Settings
     */
    public SelenideElement VoIPSettings_checkbox = $(By.xpath(".//label[starts-with(@class,\"x-form-cb-label x-form-cb-label-default x-form-cb-label-after  \") and text()=\"VoIP Settings\"]"));
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
    public SelenideElement IPRestriction_checkbox = $(By.xpath(".//label[starts-with(@class,\"x-form-cb-label x-form-cb-label-default x-form-cb-label-after  \") and text()=\"IP Restriction\"]"));
    public SelenideElement enableIPRestriction = $(By.id("enableiprestrict-displayEl"));
    public SelenideElement permittedIP = $(By.id("permitip0-inputEl"));
    public SelenideElement subnetmask = $(By.id("permitmask0-inputEl"));
    public SelenideElement enableIPRestriction_add = $(By.xpath(""));

    public SelenideElement save = $(By.xpath(" .//div[starts-with(@id,'extension-edit')]//span[ text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'extension-edit')]//span[text()='Cancel']"));


}
