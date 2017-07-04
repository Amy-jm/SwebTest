package com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.General;

import com.codeborne.selenide.SelenideElement;
import org.apache.tools.ant.taskdefs.Sleep;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class General {
    public SelenideElement general = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"General\"]"));

    public SelenideElement UDPPort = $(By.id("st-sip-udpport-inputEl"));

    public SelenideElement TCPPort_check = $(By.id("sst-sip-enabletcp-displayEl"));
    public SelenideElement TCPPort = $(By.id("st-sip-tcpport-inputEl"));

    public SelenideElement RTPPort_start = $(By.id("st-sip-rtpportstart-inputEl"));
    public SelenideElement RTPPort_end = $(By.id("st-sip-rtpportend-inputEl"));

    public SelenideElement localSIPPort_check = $(By.id("st-sip-enablerandomport-displayEl"));
    public SelenideElement localSIPPort_start = $(By.id("st-sip-randomportstart-inputEl"));
    public SelenideElement localSIPPort_end = $(By.id("st-sip-randomportend-inputEl"));

    /**
     * Registration Timers
     */

    public SelenideElement maxRegistrationTime = $(By.id("st-sip-maxregtime-inputEl"));
    public SelenideElement minRegistrationTime = $(By.id("st-sip-minregtime-inputEl"));
    public SelenideElement quailfyFrequency = $(By.id("st-sip-qualifyfrequency-inputEl"));

    /**
     * Outbound SIP Registrations
     */
    public SelenideElement registrationAttempts = $(By.id("st-sip-regattempts-inputEl"));
    public SelenideElement defaultIncoming_outgoingRegistrationTime = $(By.id("st-sip-defaultregtime-inputEl"));

    /**
     * Subscription Timers
     */
    public SelenideElement maxSubscriptionTime = $(By.id("st-sip-submaxexpiry-inputEl"));
    public SelenideElement minSubscriptionTime = $(By.id("st-sip-subminexpiry-inputEl"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));



}
