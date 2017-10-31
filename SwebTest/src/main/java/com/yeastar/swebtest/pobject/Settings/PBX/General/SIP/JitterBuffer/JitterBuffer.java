package com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.JitterBuffer;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class JitterBuffer {
    public SelenideElement jitterBuffer = $(By.xpath(".//div[starts-with(@id,\"general\")]//div[starts-with(@id,\"sip\")]//span[text()='Jitter Buffer']"));


    public String enableJitterBuffer ="st-sip-jbenable";

    public SelenideElement fixed = $(By.xpath(".//div[starts-with(@id,\"general\")]//div[starts-with(@id,\"sip\")]//label[text()='Fixed']"));
    public SelenideElement adaptive = $(By.xpath(".//div[starts-with(@id,\"general\")]//div[starts-with(@id,\"sip\")]//label[text()='Adaptive']"));

    public SelenideElement jitterBufferSize = $(By.id("st-sip-jbbuffersize-inputEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'sip-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'sip-')]//span[text()='Cancel']"));



}
