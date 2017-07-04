package com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Add_VoIP_Trunk.Add_VoIP_Trunk_Advanced;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_VoIP_Trunk_Advanced {
    public SelenideElement advanced = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Advanced\"]"));

    public SelenideElement qualify = $(By.id("qualify-displayEl"));
    public SelenideElement enableSRTP = $(By.id("enablesrtp-displayEl"));
    public SelenideElement TSupport = $(By.id("t38support-displayEl"));
    public SelenideElement DTMFMode = $(By.id("dtmfmode-inputEl"));
    public SelenideElement realm = $(By.id("realm-inputEl"));
    public SelenideElement sendPrivacyID = $(By.id("privacyid-displayEl"));
    public SelenideElement maximumChannels = $(By.id("calllimit-inputEl"));
    public SelenideElement getDIDFrom = $(By.id("st-trunk-getto-inputEl"));
    public SelenideElement getCallerIDFrom = $(By.id("st-trunk-getfrom-inputEl"));
    public SelenideElement enableDNIS = $(By.id("enablednis-displayEl"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
