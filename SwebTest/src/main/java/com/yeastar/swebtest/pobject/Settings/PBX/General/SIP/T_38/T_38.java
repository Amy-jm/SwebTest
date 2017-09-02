package com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.T_38;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class T_38 {
    public SelenideElement T_38 = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"T.38\"]"));

    public SelenideElement NoT_38Attributes = $(By.id("st-sip-t38reinvitesdpnotaddattr-displayEl"));
    public SelenideElement errorCorrection = $(By.id("st-sip-t38faxudpec-displayEl"));
    public SelenideElement T_38MaxBitRate = $(By.id("st-sip-t38maxbitrate-trigger-picker"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'sip-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'sip-')]//span[text()='Cancel']"));


}
