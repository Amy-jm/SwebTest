package com.yeastar.swebtest.pobject.Settings.System.UserPermission.Grant_Privilege_Settings;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Grant_Privilege_Settings {
    public SelenideElement grant_Privilege_Settings = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Settings\"]"));

    public SelenideElement user = $(By.id("st-up-id-inputEl"));
    public SelenideElement setPrivilegeAs = $(By.id("st-up-type-trigger-picker"));

    public SelenideElement all = $(By.xpath(".//label[starts-with(@class,\"x-form-cb-label x-form-cb-label-default x-form-cb-label-after  \") and text()=\"All\"]"));
    public SelenideElement settings = $(By.id("st-up-controlplanel-boxLabelEl"));
    /**
     * PBX
     */
    public SelenideElement PBX = $(By.id("st-up-pbx-boxLabelEl"));

    public SelenideElement extensions = $(By.id("st-up-extensions-boxLabelEl"));
    public SelenideElement trunks = $(By.id("st-up-trunks-boxLabelEl"));
    public SelenideElement callControl = $(By.id("st-up-callcontrol-boxLabelEl"));
    public SelenideElement callFeatures = $(By.id("st-up-callfeatures-boxLabelEl"));
    public SelenideElement voicePrompts = $(By.id("st-up-promptsettings-boxLabelEl"));
    public SelenideElement general = $(By.id("st-up-generalsettings-boxLabelEl"));
    public SelenideElement recording = $(By.id("st-up-recordingsettings-boxLabelEl"));
    public SelenideElement emergencyNumber = $(By.id("st-up-emergency-boxLabelEl"));

    /**
     * System
     */
    public SelenideElement system = $(By.id("st-up-system-boxLabelEl"));

    public SelenideElement network = $(By.id("st-up-networksettings-boxLabelEl"));
    public SelenideElement security = $(By.id("st-up-securitycenter-boxLabelEl"));
    public SelenideElement dateTime = $(By.id("st-up-timesettings-boxLabelEl"));
    public SelenideElement email = $(By.id("st-up-emailsettings-boxLabelEl"));
    public SelenideElement storage = $(By.id("st-up-storage-boxLabelEl"));
    public SelenideElement hotStandby = $(By.id("st-up-hotstandby-boxLabelEl"));

    /**
     * Event Center
     */
    public SelenideElement eventCenter = $(By.id("st-up-eventcenter-boxLabelEl"));

    public SelenideElement eventSettings = $(By.id("st-up-eventsettings-boxLabelEl"));
    public SelenideElement eventLog = $(By.id("st-up-eventlog-boxLabelEl"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));






















}
