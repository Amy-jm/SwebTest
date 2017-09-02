package com.yeastar.swebtest.pobject.Settings.System.Security.FirewallRules;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class FirewallRules {
    public SelenideElement firewallRules = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Firewall Rules\"]"));

    public SelenideElement add = $(By.xpath(".//div[starts-with(@id,'security')]//span[text()='Add']"));
    public SelenideElement enableFirewall = $(By.id("st-fwrules-enable-displayEl"));
    public SelenideElement disablePing = $(By.id("st-fwrules-disableping-displayEl"));
    public SelenideElement dropAll = $(By.id("st-fwrules-dropall-displayEl"));
    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'security')]//span[text()='Save']"));


    public SelenideElement enableFirewall_Yes = $(By.xpath(".//div[starts-with(@id,'messagebox-')]//span[text()='Yes']"));
    public SelenideElement getEnableFirewall_No = $(By.xpath(".//div[starts-with(@id,'messagebox-')]//span[text()='No']"));
    /**
     * 页码功能
     */
    public SelenideElement firstPage = $(By.xpath(".//div[starts-with(@id,'security')]//a[@data-qtip='First Page']"));
    public SelenideElement previousPage = $(By.xpath(".//div[starts-with(@id,'security')]//a[@data-qtip='Previous Page']"));
    public SelenideElement nextPage = $(By.xpath(".//div[starts-with(@id,'security')]//a[@data-qtip='Next Page']"));
    public SelenideElement lastPage = $(By.xpath(".//div[starts-with(@id,'security')]//a[@data-qtip='Last Page']"));
    public SelenideElement refresh = $(By.xpath(".//div[starts-with(@id,'security')]//a[@data-qtip='Refresh']"));
    public SelenideElement gotoinput = $(By.xpath(".//div[starts-with(@id,'security')]//input[@name='gotoinput']"));
    public SelenideElement go = $(By.xpath(".//div[starts-with(@id,'security')]//span[text()='Go']"));
    public SelenideElement selectPage = $(By.id(".//*[(@data-ref='bodyEl') and (@role='presentation')]"));









}
