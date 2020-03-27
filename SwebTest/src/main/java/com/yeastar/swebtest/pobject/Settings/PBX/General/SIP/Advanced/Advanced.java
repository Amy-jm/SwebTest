package com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.Advanced;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Advanced {
    public SelenideElement advanced = $(By.xpath(".//div[starts-with(@id,\"general\")]//div[starts-with(@id,\"sip\")]//span[text()='Advanced']"));

    public String allowRTPRe_invite = "st-sip-canreinvite";
    public String getCallerIDFrom = "st-sip-getfrom";
    public SelenideElement userAgent = $(By.id("st-sip-useragent-inputEl"));
    public String getDIDFrom ="st-sip-getto";
    public String sendRemotePartyID = "st-sip-sendrpid";
    public String _100rel ="st-sip-sip100rel";
    public String sendPAssertedIdentify ="st-sip-sendpai";
    public String allowGuest ="st-sip-allowguest";
    public String sendDiversionID ="st-sip-diversionid";
    public String supportMessageRequest ="st-sip-supportmessage";
    public String allBusyModeforSIPForking ="st-sip-sipforking";

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'sip-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'sip-')]//span[text()='Cancel']"));

}
