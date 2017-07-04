package com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.QoS;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class QoS {
    public SelenideElement QoS = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"QoS\"]"));

    public SelenideElement ToSSIP = $(By.id("st-sip-tossip-trigger-picker"));
    public SelenideElement ToSAudio = $(By.id("st-sip-tosaudio-trigger-picker"));
    public SelenideElement ToSVideo = $(By.id("st-sip-tosvideo-trigger-picker"));
    public SelenideElement CoSSIP = $(By.id("st-sip-cossip-trigger-picker"));
    public SelenideElement CoSAudio = $(By.id("st-sip-cosaudio-trigger-picker"));
    public SelenideElement CoSVideo = $(By.id("st-sip-cosvideo-trigger-picker"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));


}
