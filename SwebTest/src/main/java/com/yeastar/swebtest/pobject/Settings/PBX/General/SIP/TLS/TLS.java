package com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.TLS;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class TLS {
    public SelenideElement TLS = $(By.xpath(".//div[starts-with(@id,\"general\")]//div[starts-with(@id,\"sip\")]//span[text()='TLS']"));

//    public SelenideElement enableTLS = $(By.id("st-sip-enabletls-displayEl"));
    public String enableTLS = "st-sip-enabletls";

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'sip-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'sip-')]//span[text()='Cancel']"));

}
