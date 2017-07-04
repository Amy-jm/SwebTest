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
    public SelenideElement protocol = $(By.id("st-defense-protocol-trigger-picker"));
    public SelenideElement numberofIPPackets = $(By.id("st-defense-packets-inputEl"));
    public SelenideElement timeInterval = $(By.id("st-defense-fwinterval-inputEl"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
