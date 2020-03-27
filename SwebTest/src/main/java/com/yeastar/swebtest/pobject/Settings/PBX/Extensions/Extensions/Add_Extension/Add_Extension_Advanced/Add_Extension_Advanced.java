package com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Extension.Add_Extension_Advanced;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Extension_Advanced {
    /**
     * 进入Advanced
     */
    public SelenideElement advanced = $(By.xpath(".//div[starts-with(@id,\"extension-edit\")]//span[ text()=\"Advanced\"]"));

    /**
     * VoIP Settings
     */
    public String NAT = "nat";
    public String qualify = "qualify";
    public String registerRemotely = "remoteregister";
    public String enableSRTP = "enablesrtp";
    public String transport_button = "transport";
    public String DTMFMode_button = "dtmfmode";
    public String enableUserAgentRegistrationAuthorization = "enableuseragent";
    public SelenideElement userAgent = $(By.id("limituseragent0-inputEl"));
    public SelenideElement userAgent_add = $(By.xpath(".//div[starts-with(@id,\"extension-advanced\")]//div[ @class=\"cp-icon\"]"));

    /**
     * IP Restriction
     */
    public String enableIPRestriction = "enableiprestrict";
    public SelenideElement permittedIP = $(By.id("permitip0-inputEl"));
    public SelenideElement subnetmask = $(By.id("permitmask0-inputEl"));
    public SelenideElement enableIPRestriction_add = $(By.xpath(""));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,\"extension-edit\")]//span[ text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,\"extension-edit\")]//span[ text()=\"Cancel\"]"));


}
