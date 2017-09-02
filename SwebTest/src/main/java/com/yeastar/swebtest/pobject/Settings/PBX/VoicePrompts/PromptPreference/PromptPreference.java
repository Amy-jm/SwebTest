package com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.PromptPreference;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class PromptPreference {
    public SelenideElement promptPreference = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Prompt Preference\"]"));

    public SelenideElement musicOnHold = $(By.id("st-pp-musiconhold-trigger-picker"));
    public SelenideElement playCallForwardingPrompt = $(By.id("st-pp-followmepromptenable-displayEl"));
    public SelenideElement musiconHoldforCallForwarding = $(By.id("st-pp-followmeprompttone-trigger-picker"));
    public SelenideElement invalidPhoneNumberPrompt = $(By.id("st-pp-trunkunvail-trigger-picker"));
    public SelenideElement busyLinePrompt = $(By.id("st-pp-trunkbusy-trigger-picker"));
    public SelenideElement dialFailurePrompt = $(By.id("st-pp-trunkcongestion-trigger-picker"));
    public SelenideElement meventCenterPrompt = $(By.id("st-pp-eventcenter-trigger-picker"));
    public SelenideElement oneTouchRecordingStartPrompt = $(By.id("st-pp-monitorstart-trigger-picker"));
    public SelenideElement oneTouchRecordingEndPrompt = $(By.id("st-pp-monitorend-trigger-picker"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'promptpreference-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'promptpreference-')]//span[text()='Cancel']"));

    public String MusicOnHoldId = "st-pp-musiconhold";

}
