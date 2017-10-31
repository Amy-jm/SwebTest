package com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.SessionTimer;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class SessionTimer {
    public SelenideElement sessionTimer = $(By.xpath(".//div[starts-with(@id,\"general\")]//div[starts-with(@id,\"sip\")]//span[text()='Session Timer']"));


    public String session_timers ="st-sip-stmode";
    public SelenideElement session_Expires = $(By.id("st-sip-stexpires-inputEl"));
    public SelenideElement min_SE = $(By.id("st-sip-stminse-inputEl"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'sip-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'sip-')]//span[text()='Cancel']"));

}
