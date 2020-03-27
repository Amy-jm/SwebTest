package com.yeastar.swebtest.pobject.Me.Me_Blacklist_Whitelist.Me_Whitelist.Me_Import_Whitelist;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/7/21.
 */
public class Me_Import_Whitelist {
    public SelenideElement extensionFile = $(By.id("me-blacklist-filename-inputEl"));
    public SelenideElement browse = $(By.id("me-blacklist-filename-trigger-filebutton"));

    public SelenideElement Import = $(By.xpath(".//div[starts-with(@id,'meblacklist-import-')]//span[text()='Import']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'meblacklist-import-')]//span[text()='Cancel']"));
    public SelenideElement ImportOK = $(By.xpath(".//div[starts-with(@id,\"messagebox\")]//span[text()=\"OK\"]"));

}
