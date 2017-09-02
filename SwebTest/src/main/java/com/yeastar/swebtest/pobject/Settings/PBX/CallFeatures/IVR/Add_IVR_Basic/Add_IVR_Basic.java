package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.IVR.Add_IVR_Basic;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_IVR_Basic {

    public SelenideElement basic = $(By.xpath(".//div[starts-with(@id,'ivr-edit-')]//span[text()='Basic']"));

    public SelenideElement number = $(By.id("st-ivr-number-inputEl"));
    public SelenideElement name = $(By.id("st-ivr-name-inputEl"));
    public SelenideElement prompt = $(By.id("st-ivr-prompt0-trigger-picker"));
    public SelenideElement promptRepeatCount = $(By.id("st-ivr-playtimes-trigger-picker"));
    public SelenideElement responseTimeout = $(By.id("st-ivr-waitexten-inputEl"));
    public SelenideElement dightTimeout = $(By.id("st-ivr-digittimeout-inputEl"));
    public SelenideElement dialExtensions = $(By.id("st-ivr-enablenumber-displayEl"));
    public SelenideElement dialOutboundRoutes = $(By.id("st-ivr-enableoutrouter-displayEl"));
    public SelenideElement dialtoCheckVoicemail = $(By.name("enablecheckvoicemail"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'ivr-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'ivr-edit-')]//span[text()='Cancel']"));

}
