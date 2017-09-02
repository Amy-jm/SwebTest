package com.yeastar.swebtest.pobject.Settings.System.Storage.FileShare;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class FileShare {
    public SelenideElement fileShare = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"File Share\"]"));

    public SelenideElement enableFileSharing = $(By.id("st-share-enable-displayEl"));
    public SelenideElement enableFTPFileSharing = $(By.id("st-share-enableftp-displayEl"));
    public SelenideElement allowtochangesharedfiles = $(By.id("st-share-enabledelete-displayEl"));
    public SelenideElement sharedFileName = $(By.id("st-share-sharename-inputEl"));
    public SelenideElement password = $(By.id("st-share-password-inputEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'fileshare-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'fileshare-')]//span[text()='Cancel']"));


}
