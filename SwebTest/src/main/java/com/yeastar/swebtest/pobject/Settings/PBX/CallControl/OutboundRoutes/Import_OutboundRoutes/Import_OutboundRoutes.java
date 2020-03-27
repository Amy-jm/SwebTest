package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.OutboundRoutes.Import_OutboundRoutes;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/10/10.
 */
public class Import_OutboundRoutes {
    public SelenideElement outboundRoutesFile = $(By.id("st-outrouter-filename-inputEl"));
    public SelenideElement browse = $(By.id("st-outrouter-filename-button-fileInputEl"));
    public SelenideElement downloadTemp = $(By.id(".//div[starts-with(@id,'outrouter-import')]//span[ text()='Download the Template']"));
    public SelenideElement Import = $(By.xpath(".//div[starts-with(@id,'outrouter-import')]//span[text()='Import']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'outrouter-import')]//span[text()='Cancel']"));
}
