package com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.SessionTimer;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class SessionTimer {
    public SelenideElement sessionTimer = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Session Timer\"]"));


    public SelenideElement session_timers = $(By.id("st-sip-stmode-trigger-picker"));
    public SelenideElement session_Expires = $(By.id("st-sip-stexpires-inputEl"));
    public SelenideElement min_SE = $(By.id("st-sip-stminse-inputEl"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
