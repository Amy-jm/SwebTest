package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.InboundRoutes.Add_Inbound_Route;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Inbound_Route {

    public SelenideElement name = $(By.id("st-ir-name-inputEl"));
    public SelenideElement DIDPatem = $(By.id("st-ir-didprefix-inputEl"));
    public SelenideElement callIDPattem = $(By.xpath("st-ir-calleridprefix-inputEl"));

    public SelenideElement enableTimeCondition = $(By.id("st-ir-enabletimecondition-displayEl"));
    public SelenideElement destination_button = $(By.id("st-ir-desttype-trigger-picker"));
    public SelenideElement distinctiveRingtone = $(By.id("st-ir-ringtone-inputEl"));
    public SelenideElement enableFaxDetection = $(By.id("st-ir-faxdetect-displayEl"));
    public SelenideElement faxDestination_button = $(By.id("st-ir-faxto-trigger-picker"));
    public SelenideElement faxDestinationNumber = $(By.id("st-ir-faxdest-inputEl"));


    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}