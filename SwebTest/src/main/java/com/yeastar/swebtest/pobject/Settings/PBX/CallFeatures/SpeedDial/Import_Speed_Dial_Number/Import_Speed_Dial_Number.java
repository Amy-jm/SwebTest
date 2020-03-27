package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.SpeedDial.Import_Speed_Dial_Number;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Import_Speed_Dial_Number {
    public SelenideElement extensionFile = $(By.id("st-speeddial-filename-inputEl"));
    public SelenideElement browse = $(By.id("st-speeddial-filename-trigger-filebutton"));
    public SelenideElement Import = $(By.xpath(".//div[starts-with(@id,'speeddial-import')]//span[text()='Import']"));
    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'speeddial-import-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'speeddial-import-')]//span[text()='Cancel']"));
    public SelenideElement ImportOK = $(By.xpath(".//div[starts-with(@id,\"messagebox\")]//span[text()=\"OK\"]"));

}
