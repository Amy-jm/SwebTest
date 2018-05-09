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
    public String prompt = "st-ivr-prompt0";
    public String promptRepeatCount ="st-ivr-playtimes";
//    public String responseTimeout ="st-ivr-waitexten";
//    public String dightTimeout ="st-ivr-digittimeout";
    public String dialExtensions ="st-ivr-enablenumber";
    public String dialOutboundRoutes ="st-ivr-enableoutrouter";
    public String listOutboundRoutes = "st-ivr-allowrouters";
    public String dialtoCheckVoicemail = "st-ivr-enablecheckvoicemail";
    public SelenideElement checkVoicemail_ok = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"OK\"]"));
    public SelenideElement responseTimeout =$(By.id("st-ivr-waitexten-inputEl"));
    public SelenideElement dightTimeout =$(By.id("st-ivr-digittimeout-inputEl"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'ivr-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'ivr-edit-')]//span[text()='Cancel']"));

}
