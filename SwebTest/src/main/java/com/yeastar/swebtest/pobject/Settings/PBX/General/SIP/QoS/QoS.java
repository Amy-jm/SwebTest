package com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.QoS;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class QoS {
    public SelenideElement QoS = $(By.xpath(".//div[starts-with(@id,\"general\")]//div[starts-with(@id,\"sip\")]//span[text()='Qos']"));

    public String ToSSIP = "st-sip-tossip-trigger-picker";
    public String  ToSAudio = "st-sip-tosaudio";
    public String ToSVideo = "st-sip-tosvideo";
    public String CoSSIP = "st-sip-cossip";
    public String CoSAudio = "st-sip-cosaudio";
    public String CoSVideo = "st-sip-cosvideo";

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'sip-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'sip-')]//span[text()='Cancel']"));

}
