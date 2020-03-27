package com.yeastar.swebtest.pobject.Settings.PBX.Recording;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Recording {
    public SelenideElement storageLocations = $(By.xpath(".//span[starts-with(@class,\"cp-link-before\") and text()=\"Storage Locations\"]"));

    public String enableInternslCallRecord = "st-recording-enableinternalrecord";
    public String internalCallBeingRecordedPrompt = "st-recording-internalprompt";
    public String callsBeingRecordedPrompt = "st-recording-externalprompt";

    /**
     * Record Trunks
     */
    public String recordTrunks = "st-recording-allowtrunks";

//    public SelenideElement rt_AddAllToSelect = $(By.xpath(".//div[starts-with(@id,'st-recording-allowtrunks-bodyEl')]//span[data-qtip()='Add All to Selected']"));
    public SelenideElement rt_AddAllToSelect = $(By.xpath(".//div[starts-with(@id,'st-recording-allowtrunks-innerCt')]//a[@data-qtip='Add All to Selected']"));

    /**
     * Record Extensions
     */
    public String recordExtensions = "st-recording-allowextensions";
    public SelenideElement re_AddAllToSelect = $(By.xpath(".//div[starts-with(@id,'st-recording-allowextensions-innerCt')]//a[@data-qtip='Add All to Selected']"));

    /**
     * Record Conferences
     */
    public String recordConferences = "st-recording-allowconferences";
    public SelenideElement rc_AddAllToSelect = $(By.xpath(".//div[starts-with(@id,'st-recording-allowconferences-innerCt')]//a[@data-qtip='Add All to Selected']"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'recordinglist-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'recordinglist-')]//span[text()='Cancel']"));

}
