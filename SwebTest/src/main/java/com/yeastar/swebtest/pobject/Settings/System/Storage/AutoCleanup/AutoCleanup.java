package com.yeastar.swebtest.pobject.Settings.System.Storage.AutoCleanup;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class AutoCleanup {
    public SelenideElement autoCleanup = $(By.xpath(".//div[starts-with(@id,\"storage\")]//span[ text()=\"Auto Cleanup\"]"));

    /**
     * CDR Auto Cleanup
     */
    public String maxNumberOfCDR = ("st-storage-maxnumbercdr");
    public SelenideElement CDRPreservervationDuration = $(By.id("st-storage-keepcdrday-inputEl"));
    /**
     * Voicemail and One Touch Recording Auto Cleanup
     */
    public String maxNumberOfFiles = "st-storage-maxnumbervm";
    public SelenideElement filesPreservervationDuration = $(By.id("st-storage-keepvmminute-inputEl"));
    /**
     * Recording Auto Cleanup
     */
    public String maxUsageOfDevice = "st-storage-maxusagedevice";
    public SelenideElement recordingsPreservationDuration = $(By.id("st-storage-keeprecordday-inputEl"));
    /**
     * Logs Auto Cleanup
     */
    public SelenideElement maxSizeofTotalLogs = $(By.id("st-storage-maxpbxlogsize-inputEl"));
    public SelenideElement logsPreservationDuration = $(By.id("st-storage-keeplogday-inputEl"));
    public String maxNumberOfLogs = "st-storage-maxnumberlog-inputEl";


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,\"storage\")]//span[ text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,\"storage\")]//span[ text()=\"Cancel\"]"));

















}
