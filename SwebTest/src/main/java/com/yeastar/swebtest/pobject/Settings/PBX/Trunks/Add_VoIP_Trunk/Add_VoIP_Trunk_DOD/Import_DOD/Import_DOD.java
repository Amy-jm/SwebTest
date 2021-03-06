package com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Add_VoIP_Trunk.Add_VoIP_Trunk_DOD.Import_DOD;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Import_DOD {
    public SelenideElement extensionFile = $(By.id("st-trunk-filename-inputEl"));
    public SelenideElement browse = $(By.id("st-trunk-filename-button-fileInputEl"));
    public SelenideElement Import = $(By.xpath(".//div[starts-with(@id,'trunkdod-impor')]//span[ text()='Import']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'trunkdod-impor')]//span[ text()='Cancel']"));

}
