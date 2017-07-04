package com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.JitterBuffer;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class JitterBuffer {
    public SelenideElement jitterBuffer = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Jitter Buffer\"]"));


    public SelenideElement enableJitterBuffer = $(By.id("st-sip-jbenable-displayEl"));

    public SelenideElement fixed = $(By.xpath(".//label[starts-with(@class,\"x-form-cb-label x-form-cb-label-default x-form-cb-label-after  \") and text()=\"Fixed\"]"));
    public SelenideElement adaptive = $(By.xpath(".//label[starts-with(@class,\"x-form-cb-label x-form-cb-label-default x-form-cb-label-after  \") and text()=\"Adaptive\"]"));

    public SelenideElement jitterBufferSize = $(By.id("st-sip-jbbuffersize-inputEl"));


    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));





}
