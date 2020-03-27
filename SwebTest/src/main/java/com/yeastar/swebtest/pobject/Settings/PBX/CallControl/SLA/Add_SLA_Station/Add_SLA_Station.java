package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.SLA.Add_SLA_Station;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_SLA_Station {
    public SelenideElement stationName = $(By.id("st-sla-name-inputEl"));
    public String station = "st-sla-extensionid";

    public String ringTimeout = "st-sla-ringtimeout";
    public String ringDelay = "st-sla-ringdelay";
    public SelenideElement holdAccess_open = $(By.xpath(".//div[starts-with(@id,\"radiogroup\")]//label[ text()=\"Open\"]"));
    public SelenideElement holdAccess_private = $(By.xpath(".//div[starts-with(@id,\"radiogroup\")]//label[ text()=\"Private\"]"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'sla-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'sla-edit-')]//span[text()='Cancel']"));

}
