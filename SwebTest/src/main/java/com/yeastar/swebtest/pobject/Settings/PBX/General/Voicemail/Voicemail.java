package com.yeastar.swebtest.pobject.Settings.PBX.General.Voicemail;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Voicemail {
    public SelenideElement voicemail = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Voicemail\"]"));

    /**
     * Message Options
     */
    public SelenideElement maxMessagesperFolder = $(By.id("st-voicemail-maxmsgcount-trigger-picker"));
    public SelenideElement maxMessagesTime = $(By.id("st-voicemail-maxmsgduration-trigger-picker"));
    public SelenideElement minMessagesTime = $(By.id("st-voicemail-minmsgduration-trigger-picker"));

    public SelenideElement deleteVoicemail = $(By.id("st-voicemail-deletevm-displayEl"));
    public SelenideElement askCallertoDial5 = $(By.id("st-voicemail-press5-displayEl"));
    public SelenideElement opetatorBreakoutfromVoicemail = $(By.id("st-voicemail-press0to-displayEl"));
    public SelenideElement destination = $(By.id("st-voicemail-press0dest-trigger-picker"));

    /**
     * Greeting Options
     */

    public SelenideElement busyPrompt = $(By.id("st-voicemail-busygreet-trigger-picker"));
    public SelenideElement unavailablePrompt = $(By.id("st-voicemail-unavgreet-trigger-picker"));
    public SelenideElement leaveaMeaagePrompt = $(By.id("st-voicemail-vmprompt-displayEl"));
    public SelenideElement dial5Prompt = $(By.id("st-voicemail-normalgreet-displayEl"));

    /**
     * Playback Options
     */

    public SelenideElement announceMessageCalledID = $(By.id("st-voicemail-saycallerid-displayEl"));
    public SelenideElement announceMessageDuration = $(By.id("st-voicemail-sayduration-displayEl"));
    public SelenideElement announceMessageArrivalTime = $(By.id("st-voicemail-envelope-displayEl"));
    public SelenideElement allowUserstoReviewMessages = $(By.id("st-voicemail-review-displayEl"));


    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
