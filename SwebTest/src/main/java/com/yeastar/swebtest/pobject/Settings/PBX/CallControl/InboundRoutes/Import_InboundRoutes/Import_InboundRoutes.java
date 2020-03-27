package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.InboundRoutes.Import_InboundRoutes;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/10/10.
 */
public class Import_InboundRoutes {
    public SelenideElement inboundRoutesFile = $(By.id("st-inrouter-filename-inputEl"));
    public SelenideElement browse = $(By.id("st-inrouter-filename-button-fileInputEl"));
    public SelenideElement downloadTemp = $(By.id(".//div[starts-with(@id,'inrouter-import')]//span[ text()='Download the Template']"));
    public SelenideElement Import = $(By.xpath(".//div[starts-with(@id,'inrouter-import')]//span[ text()='Import']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'inrouter-import')]//span[ text()='Cancel']"));
}
