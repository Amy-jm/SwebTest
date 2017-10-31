package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Queue.Add_Queue_CallerExperienceSettings;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_Queue_CallerExperienceSettings {

    public SelenideElement callerExperienceSettings=$(By.xpath(".//div[starts-with(@id,\"queue-edit\")]//span[ text()=\"Caller Experience Settings\"]"));
    /**
     * Call Settings
     */
    public String musicOnHold ="st-queue-musiconhold";
    public SelenideElement callerMaxWaitTime =$(By.id("st-queue-maxwaittime-inputEl"));
    public String leaveWhenEmpty ="st-queue-leavewhenempty";
    public String joinEmpty ="st-queue-joinempty";
    public String joinAnnouncement ="st-queue-joinannounce";
    /**
     * Caller Position Announcements
     */
    public String announcePosition ="st-queue-announcepos";
    public String announceHoldTime ="st-queue-announceholdtime";
    public String frequency ="st-queue-announcefreq";
    /**
     * Periodic Announcements
     */
    public String prompt ="st-queue-userannounce";
    public String frequencys ="st-queue-userannouncefreq";
    /**
     * Events
     */
    public String key ="st-queue-breakoutkey";
    public String keydest="st-queue-breakoutaction";
    public String keydest2="st-queue-breakoutdest";

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'queue-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'queue-edit-')]//span[text()='Cancel']"));

}
