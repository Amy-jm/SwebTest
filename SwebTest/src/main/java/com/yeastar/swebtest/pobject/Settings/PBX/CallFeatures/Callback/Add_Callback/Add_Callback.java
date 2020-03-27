package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Callback.Add_Callback;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_Callback {
    public String destination_Extension = "e";
    public String destination_Voicemail = "v";
    public String destination_IVR = "i";
    public String destination_RingGroup = "r";
    public String destination_Queue = "q";
    public String destination_Conference = "c";
    public String destination_Disa = "d";

    public String id_destinationNumber = "st-callback-destination";
    public String id_destinationType = "st-callback-desttype";

    public SelenideElement name = $(By.id("st-callback-name-inputEl"));
    public String callbackThrough = "st-callback-callbacktrunk";
    public SelenideElement delayBeforeCallback = $(By.id("st-callback-delay-inputEl"));
    public SelenideElement strip = $(By.id("st-callback-strip-inputEl"));
    public SelenideElement prepend = $(By.id("st-callback-prepend-inputEl"));
    public String destination = "st-callback-desttype";
    public String destinationDest = "st-callback-destination";

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'callback-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'callback-edit-')]//span[text()='Cancel']"));

}
