package com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.Advanced;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Advanced {
    public SelenideElement advanced = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Advanced\"]"));

    public SelenideElement allowRTPRe_invite = $(By.id("st-sip-canreinvite-trigger-picker"));
    public SelenideElement getCallerIDFrom = $(By.id("st-sip-getfrom-trigger-picker"));
    public SelenideElement userAgent = $(By.id("st-sip-useragent-inputEl"));
    public SelenideElement getDIDFrom = $(By.id("st-sip-getto-trigger-picker"));
    public SelenideElement sendRemotePartyID = $(By.id("st-sip-sendrpid-displayEl"));
    public SelenideElement _100rel = $(By.id("st-sip-sip100rel-displayEl"));
    public SelenideElement sendPAssertedIdentify = $(By.id("st-sip-sendpai-displayEl"));
    public SelenideElement allowGuest = $(By.id("st-sip-allowguest-displayEl"));
    public SelenideElement sendDiversionID = $(By.id("st-sip-diversionid-displayEl"));
    public SelenideElement supportMessageRequest = $(By.id("st-sip-supportmessage-displayEl"));
    public SelenideElement allBusyModeforSIPForking = $(By.id("st-sip-sipforking-displayEl"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
