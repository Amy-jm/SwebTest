package com.yeastar.swebtest.pobject.Settings.System.Security.IPAutoDefense.AutoDefenseRules.Add_IP_Auto_Defense_Rule;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Add_IP_Auto_Defense_Rule {
    public SelenideElement port_start = $(By.id("st-defense-port-inputEl"));
    public SelenideElement port_end = $(By.id("st-defense-portend-inputEl"));
    public String protocol = "st-defense-protocol";
    public SelenideElement numberofIPPackets = $(By.id("st-defense-packets-inputEl"));
    public SelenideElement timeInterval = $(By.id("st-defense-fwinterval-inputEl"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'defense-edit')]//span[ text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'defense-edit')]//span[ text()='Cancel']"));

}
