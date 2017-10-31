package com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Add_VoIP_Trunk.Add_VoIP_Trunk_Advanced;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_VoIP_Trunk_Advanced {
    public SelenideElement advanced = $(By.xpath(".//div[starts-with(@id,\"editvoip\")]//span[text()=\"Advanced\"]"));

    public String qualify = "qualify";
    public String enableSRTP = "enablesrtp";
    public String TSupport = "t38support";
    public String DTMFMode = "dtmfmode";
    public SelenideElement realm = $(By.id("realm-inputEl"));
    public String sendPrivacyID = "privacyid";
    public String maximumChannels = "calllimit";
    public String getDIDFrom = "st-trunk-getto";
    public String getCallerIDFrom = "st-trunk-getfrom";
    public String enableDNIS = "enablednis";

    public SelenideElement save = $(By.xpath(" .//div[starts-with(@id,'editvoip')]//span[ text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'editvoip')]//span[text()='Cancel']"));
}
