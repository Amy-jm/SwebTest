package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Blacklist_Whitelist.Blacklist.Import_Blacklist;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Import_Blacklist {
    public SelenideElement extensionFile = $(By.id("st-blacklist-filename-inputEl"));
    public SelenideElement browse = $(By.id("st-blacklist-filename-trigger-filebutton"));
    public SelenideElement Import = $(By.xpath(".//div[starts-with(@id,'blacklist-import-')]//span[text()='Import']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'blacklist-import-')]//span[text()='Cancel']"));
    public SelenideElement ImportOK = $(By.xpath(".//div[starts-with(@id,\"messagebox\")]//span[text()=\"OK\"]"));

}
