package com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.T_38;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class T_38 {
    public SelenideElement T_38 = $(By.xpath(".//div[starts-with(@id,\"general\")]//div[starts-with(@id,\"sip\")]//span[text()='T.38']"));

    public String NoT_38Attributes = "st-sip-t38reinvitesdpnotaddattr";
    public String errorCorrection = "st-sip-t38faxudpec";
    public String T_38MaxBitRate = "st-sip-t38maxbitrate";

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'sip-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'sip-')]//span[text()='Cancel']"));


}
