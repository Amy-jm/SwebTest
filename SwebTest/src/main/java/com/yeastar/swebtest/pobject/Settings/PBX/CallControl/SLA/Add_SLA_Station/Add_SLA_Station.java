package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.SLA.Add_SLA_Station;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_SLA_Station {
    public SelenideElement stationName = $(By.id("st-sla-name-inputEl"));
    public SelenideElement station = $(By.id("st-sla-extensionid-inputEl"));

    public SelenideElement ringTimeout = $(By.id("st-sla-ringtimeout-inputEl"));
    public SelenideElement ringDelay = $(By.id("st-sla-ringdelay-inputEl"));
    public SelenideElement holdAccess_open = $(By.xpath(".//span[starts-with(@class,\"x-form-cb-label x-form-cb-label-default x-form-cb-label-after  \") and text()=\"Open\"]"));
    public SelenideElement holdAccess_private = $(By.xpath(".//span[starts-with(@class,\"x-form-cb-label x-form-cb-label-default x-form-cb-label-after  \") and text()=\"Private\"]"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'sla-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'sla-edit-')]//span[text()='Cancel']"));

}
