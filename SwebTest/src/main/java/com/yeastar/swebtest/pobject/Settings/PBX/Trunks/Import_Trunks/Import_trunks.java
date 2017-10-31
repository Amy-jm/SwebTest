package com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Import_Trunks;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/10/10.
 */
public class Import_trunks {
    public SelenideElement outboundRoutesFile = $(By.id("st-trunk-filename-inputEl"));
    public SelenideElement browse = $(By.id("st-trunk-filename-button-fileInputEl"));
    public SelenideElement downloadTemp = $(By.id(".//div[starts-with(@id,'trunk-import')]//span[ text()='Download the Template']"));
    public SelenideElement Import = $(By.xpath(".//div[starts-with(@id,'trunk-import')]//span[text()='Import']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'trunk-import')]//span[text()='Cancel']"));
}
