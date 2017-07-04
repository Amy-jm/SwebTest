package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Queue.Add_Queue_CallerExperienceSettings;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_Queue_CallerExperienceSettings {
    /**
     * Call Settings
     */
    public SelenideElement musicOnHold = $(By.id("st-queue-musiconhold-trigger-picker"));
    public SelenideElement callerMaxWaitTime = $(By.id("st-queue-maxwaittime-inputEl"));
    public SelenideElement leaveWhenEmpty = $(By.id("st-queue-leavewhenempty-displayEl"));
    public SelenideElement joinEmpty = $(By.id("st-queue-joinempty-displayEl"));
    public SelenideElement joinAnnouncement = $(By.id("st-queue-joinannounce-trigger-picker"));
    /**
     * Caller Position Announcements
     */
    public SelenideElement announcePosition = $(By.id("st-queue-announcepos-displayEl"));
    public SelenideElement announceHoldTime = $(By.id("st-queue-announceholdtime-displayEl"));
    public SelenideElement frequency = $(By.id("st-queue-announcefreq-trigger-picker"));
    /**
     * Periodic Announcements
     */
    public SelenideElement prompt = $(By.id("st-queue-userannounce-trigger-picker"));
    public SelenideElement frequencys = $(By.id("st-queue-userannouncefreq-trigger-picker"));
    /**
     * Events
     */
    public SelenideElement key = $(By.id("st-queue-breakoutkey-trigger-picker"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
