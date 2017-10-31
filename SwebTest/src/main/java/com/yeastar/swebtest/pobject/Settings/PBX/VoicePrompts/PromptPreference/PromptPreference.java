package com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.PromptPreference;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class PromptPreference {

    public SelenideElement promptPreference = $(By.xpath(".//div[starts-with(@id,'prompt')]//span[ text()='Prompt Preference']"));

    public String MusicOnHold = "st-pp-musiconhold";
    public String playCallForwardingPrompt = "st-pp-followmepromptenable";
    public String musiconHoldforCallForwarding = "st-pp-followmeprompttone";
    public String invalidPhoneNumberPrompt = "st-pp-trunkunvail";
    public String busyLinePrompt = "st-pp-trunkbusy";
    public String dialFailurePrompt = "st-pp-trunkcongestion";
    public String meventCenterPrompt = "st-pp-eventcenter";
    public String oneTouchRecordingStartPrompt = "st-pp-monitorstart";
    public String oneTouchRecordingEndPrompt = "st-pp-monitorend";

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'promptpreference-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'promptpreference-')]//span[text()='Cancel']"));



}
