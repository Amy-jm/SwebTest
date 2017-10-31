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
    public SelenideElement advanced = $(By.xpath(".//div[starts-with(@id,\"extension-edit\")]//span[ text()=\"Advanced\"]"));

    /**
     * VoIP Settings
     */
    public String NAT = "nat-displayEl";
    public String qualify = "qualify-displayEl";
    public String registerRemotely = "remoteregister-displayEl";
    public String enableSRTP = "enablesrtp-displayEl";
    public String transport_button = "transport-trigger-picker";
    public String DTMFMode_button = "dtmfmode-trigger-picker";
    public String enableUserAgentRegistrationAuthorization = "enableuseragent-displayEl";
    public SelenideElement userAgent = $(By.id("limituseragent0-inputEl"));
//    public SelenideElement userAgent_add = $(By.xpath(""));

    /**
     * IP Restriction
     */
    public String enableIPRestriction = ("enableiprestrict-displayEl");
    public SelenideElement permittedIP = $(By.id("permitip0-inputEl"));
    public SelenideElement subnetmask = $(By.id("permitmask0-inputEl"));
    public SelenideElement enableIPRestriction_add = $(By.xpath(""));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,\"extension-edit\")]//span[ text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,\"extension-edit\")]//span[ text()=\"Cancel\"]"));


}
