package com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Import_Extension;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Import_Extension {
    public SelenideElement extensionFile = $(By.id("st-exten-filename-inputEl"));
    public SelenideElement browse = $(By.id("st-exten-filename-button-fileInputEl"));
    public SelenideElement Import = $(By.xpath(".//div[starts-with(@id,'extension-import')]//span[ text()='Import']"));
    public SelenideElement ImportOK = $(By.xpath(".//div[starts-with(@id,\"messagebox\")]//span[text()=\"OK\"]"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'extension-import')]//span[ text()='Cancel']"));

}
