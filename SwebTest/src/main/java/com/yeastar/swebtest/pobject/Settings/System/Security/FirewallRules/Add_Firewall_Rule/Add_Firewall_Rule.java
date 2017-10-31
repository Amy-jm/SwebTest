package com.yeastar.swebtest.pobject.Settings.System.Security.FirewallRules.Add_Firewall_Rule;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_Firewall_Rule {
    public SelenideElement name = $(By.id("st-fwrules-rulename-inputEl"));
    public SelenideElement description = $(By.id("st-fwrules-fwdescribe-inputEl"));
//    public SelenideElement action = $(By.id("st-fwrules-action-trigger-picker"));
    public String action = ("st-fwrules-action");
//    public SelenideElement protocol = $(By.id("st-fwrules-protocol-trigger-picker"));
    public String protocol = "st-fwrules-protocol";
    public SelenideElement MACAddress = $(By.id("st-fwrules-mac-inputEl"));
    public SelenideElement IP = $(By.id("st-fwrules-typeip-boxLabelEl"));
    public SelenideElement domainNme = $(By.id("st-fwrules-typedomain-boxLabelEl"));
    public SelenideElement sourceIPAddress = $(By.id("st-fwrules-ipaddress-inputEl"));
    public SelenideElement subnetMask = $(By.id("st-fwrules-subnetmask-inputEl"));
    public SelenideElement port_start = $(By.id("st-fwrules-portstart-inputEl"));
    public SelenideElement port_end = $(By.id("st-fwrules-portend-inputEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'firewallrules-edit')]//span[ text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'firewallrules-edit')]//span[ text()='Cancel']"));


}
