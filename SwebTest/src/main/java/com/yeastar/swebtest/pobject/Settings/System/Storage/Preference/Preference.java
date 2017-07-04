package com.yeastar.swebtest.pobject.Settings.System.Storage.Preference;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Preference {
    public SelenideElement preference = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Preferences\"]"));

    public SelenideElement recordingSettings = $(By.xpath(".//a[starts-with(@class,\"cp-link-before\") and text()=\"Recording Settings\"]"));
    /**
     * Storage Locations
     */
    public SelenideElement CDR = $(By.id("st-storage-slotcdr-trigger-picker"));
    public SelenideElement voicemail_OneTouchRecordings = $(By.id("st-storage-slotvm-trigger-picker"));
    public SelenideElement recordings = $(By.id("st-storage-slotrecording-trigger-picker"));
    public SelenideElement logs = $(By.id("st-storage-slotlog-trigger-picker"));

    /**
     * Storage Devices
     */

    public SelenideElement addNetworkDrive = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Add Network Drive\"]"));








    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));


}
