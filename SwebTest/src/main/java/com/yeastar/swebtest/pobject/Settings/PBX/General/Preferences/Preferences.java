package com.yeastar.swebtest.pobject.Settings.PBX.General.Preferences;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Preferences {
    public SelenideElement preferences = $(By.xpath(".//div[starts-with(@id,'general')]//span[ text()='Preferences']"));

    public String maxCallDuration = "st-general-maxduration";
    public String attendedTransferCalledID = "st-general-transfercid";
    public String flashEvent = "st-general-flashfunction";

    public String virtualRingBackTone = "st-general-gsmringtone";
    public String distinctiveCallerID = "st-general-distinctivecid";

    public String FXOMode = "st-general-fxomode";
    public String toneRegion = "st-general-toneregion";
    public SelenideElement DTMFDuration = $(By.id("st-general-tonelength-inputEl"));
    public SelenideElement DTMFGap = $(By.id("st-general-toneinterval-inputEl"));

    /**
     * Extension Preferences
     */
    public SelenideElement userExtensions_start = $(By.id("st-general-extenstart-inputEl"));
    public SelenideElement userExtensions_end = $(By.id("st-general-extenend-inputEl"));

    public SelenideElement ringGroupExtensions_start = $(By.id("st-general-ringgroupstart-inputEl"));
    public SelenideElement ringGroupExtensions_end = $(By.id("st-general-ringgroupend-inputEl"));

    public SelenideElement pagingGroupExtensions_start = $(By.id("st-general-paginggroupstart-inputEl"));
    public SelenideElement pagingGroupExtensions_end = $(By.id("st-general-paginggroupend-inputEl"));

    public SelenideElement conferenceExtensions_start = $(By.id("st-general-conferencestart-inputEl"));
    public SelenideElement conferenceExtensions_end = $(By.id("st-general-conferenceend-inputEl"));

    public SelenideElement IVRExtensions_start = $(By.id("st-general-ivrstart-inputEl"));
    public SelenideElement IVRExtensions_end = $(By.id("st-general-ivrend-inputEl"));

    public SelenideElement queueExtensions_start = $(By.id("st-general-queuestart-inputEl"));
    public SelenideElement queueExtensions_end = $(By.id("st-general-queueend-inputEl"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'preference-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'preference-')]//span[text()='Cancel']"));


}
