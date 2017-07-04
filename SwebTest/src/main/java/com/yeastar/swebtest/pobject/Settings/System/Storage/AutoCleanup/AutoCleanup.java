package com.yeastar.swebtest.pobject.Settings.System.Storage.AutoCleanup;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class AutoCleanup {
    public SelenideElement autoCleanup = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Auto Cleanup\"]"));

    /**
     * CDR Auto Cleanup
     */
    public SelenideElement maxNumberOfCDR = $(By.id("st-storage-maxnumbercdr-inputEl"));
    public SelenideElement CDRPreservervationDuration = $(By.id("st-storage-keepcdrday-inputEl"));
    /**
     * Voicemail and One Touch Recording Auto Cleanup
     */
    public SelenideElement maxNumberOfFiles = $(By.id("st-storage-maxnumbervm-inputEl"));
    public SelenideElement filesPreservervationDuration = $(By.id("st-storage-keepvmminute-inputEl"));
    /**
     * Recording Auto Cleanup
     */
    public SelenideElement maxUsageOfDevice = $(By.id("st-storage-maxusagedevice-inputEl"));
    public SelenideElement recordingsPreservationDuration = $(By.id("st-storage-keeprecordday-inputEl"));
    /**
     * Logs Auto Cleanup
     */
    public SelenideElement maxSizeofTotalLogs = $(By.id("st-storage-maxpbxlogsize-inputEl"));
    public SelenideElement logsPreservationDuration = $(By.id("st-storage-keeplogday-inputEl"));
    public SelenideElement maxNumberOfLogs = $(By.id("st-storage-maxnumberlog-inputEl"));


    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

















}
