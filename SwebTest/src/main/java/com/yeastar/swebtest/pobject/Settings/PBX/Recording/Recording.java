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

    /**
     * Record Extensions
     */

    /**
     * Record Conferences
     */

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
