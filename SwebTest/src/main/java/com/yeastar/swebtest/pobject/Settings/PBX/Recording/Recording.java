package com.yeastar.swebtest.pobject.Settings.PBX.Recording;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Recording {
    public SelenideElement storageLocations = $(By.xpath(".//span[starts-with(@class,\"cp-link-before\") and text()=\"Storage Locations\"]"));

    public SelenideElement internalCallBeingRecordedPrompt = $(By.id("st-recording-internalprompt-trigger-picker"));
    public SelenideElement callsBeingRecordedPrompt = $(By.id("st-recording-externalprompt-trigger-picker"));

    /**
     * Record Trunks
     */
    public SelenideElement recordTrunks = $(By.id("st-recording-allowtrunks-containerEl"));

//    public SelenideElement rt_AddAllToSelect = $(By.xpath(".//div[starts-with(@id,'st-recording-allowtrunks-bodyEl')]//span[data-qtip()='Add All to Selected']"));
    public SelenideElement rt_AddAllToSelect = $(By.xpath(".//div[starts-with(@id,'st-recording-allowtrunks-innerCt')]//a[@data-qtip='Add All to Selected']"));

    /**
     * Record Extensions
     */
    public SelenideElement recordExtensions = $(By.id("st-recording-allowextensions-containerEl"));
    public SelenideElement re_AddAllToSelect = $(By.xpath(".//div[starts-with(@id,'st-recording-allowextensions-innerCt')]//a[@data-qtip='Add All to Selected']"));

    /**
     * Record Conferences
     */
    public SelenideElement recordConferences = $(By.id("st-recording-allowconferences-containerEl"));
    public SelenideElement rc_AddAllToSelect = $(By.xpath(".//div[starts-with(@id,'st-recording-allowconferences-innerCt')]//span[data-qtip()='Add All to Selected']"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'recordinglist-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'recordinglist-')]//span[text()='Cancel']"));

}
