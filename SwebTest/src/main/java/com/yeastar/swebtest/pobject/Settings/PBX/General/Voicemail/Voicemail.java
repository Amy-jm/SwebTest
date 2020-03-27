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
    public String maxMessagesperFolder = "st-voicemail-maxmsgcount";
    public String maxMessagesTime = "st-voicemail-maxmsgduration";
    public String minMessagesTime = "st-voicemail-minmsgduration";

    public String deleteVoicemail = "st-voicemail-deletevm";
    public String askCallertoDial5 = "st-voicemail-press5";
    public String opetatorBreakoutfromVoicemail = "st-voicemail-press0to";
    public String destination = "st-voicemail-press0dest";

    /**
     * Greeting Options
     */

    public String busyPrompt = "st-voicemail-busygreet";
    public String unavailablePrompt = "st-voicemail-unavgreet";
    public String leaveaMeaagePrompt = "st-voicemail-vmprompt";
    public String dial5Prompt = "st-voicemail-normalgreet";

    /**
     * Playback Options
     */

    public String announceMessageCalledID = "st-voicemail-saycallerid";
    public String announceMessageDuration = "st-voicemail-sayduration";
    public String announceMessageArrivalTime = "st-voicemail-envelope";
    public String allowUserstoReviewMessages = "st-voicemail-review";


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'voicemail-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'voicemail-')]//span[text()='Cancel']"));

}
