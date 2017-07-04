package com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Bulk_Extensions.Add_Bulk_Extensions_Features;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Bulk_Extensions_Features {
    /**
     * 进入Features
     */
    public SelenideElement features = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Features\"]"));

    /**
     * Voicemail
     */
    public SelenideElement enableVoicemail = $(By.id("hasvoicemail-displayEl"));
    public SelenideElement sengVoicemailtoEmail = $(By.id("enablevmtoemail-displayEl"));
    public SelenideElement useExtensionasPIN = $(By.xpath(".//label[starts-with(@class,\"x-form-cb-label x-form-cb-label-default x-form-cb-label-after  \") and text()=\"Use Extension as PIN\"]"));
    public SelenideElement useasPIN = $(By.xpath(".//label[starts-with(@class,\"x-form-cb-label x-form-cb-label-default x-form-cb-label-after  \") and text()=\"Use\"]"));
    public SelenideElement voicemailAccessPIN = $(By.id("fixedvmsecret-inputEl"));

    /**
     * Monitor Settings
     */
    public SelenideElement allowBeingMonitored = $(By.id("allowbeingspy-displayEl"));
    public SelenideElement monitorMode_button = $(By.id("spymode-trigger-picker"));

    /**
     * Other Settings
     */

    public SelenideElement ringTimeout = $(By.id("ringtimeout-inputEl"));
    public SelenideElement maxCallDuration = $(By.id("maxduration-inputEl"));
    public SelenideElement DND = $(By.id("dnd-displayEl"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));



}
