package com.yeastar.swebtest.pobject.Settings.System.HotStandby;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class HotStandby {
    public SelenideElement enableHotStandby = $(By.id("st-hotstandby-enable-bodyEl"));
    public SelenideElement userManual = $(By.id(".//a[starts-with(@class,\"cp-link-before\") and text()=\"User Manual\"]"));
    public SelenideElement mode = $(By.id("st-hotstandby-hotstandbyserver-trigger-picker"));

    /**
     * Secondary Server Information
     */
    public SelenideElement secondaryServerHosetname = $(By.xpath(""));
    public SelenideElement secondaryServerIPAddress = $(By.xpath(""));
    public SelenideElement accessCode = $(By.xpath(""));

    /**
     * Advanced
     */
    public SelenideElement keepalive = $(By.name("keepalive"));
    public SelenideElement deadTime = $(By.name("deadtime"));
    public SelenideElement diskSynchronization = $(By.id("st-hotstandby-disksync-bodyEl"));

    /**
     * Virtual IP Address
     */
    public SelenideElement virtualIPAddress = $(By.name("vipaddress"));
    public SelenideElement subnetMask = $(By.name("vnetmask"));
    public SelenideElement networkConnectionDetection = $(By.name("pingnodes"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'hotstandby')]//span[ text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'hotstandby')]//span[ text()='Cancel']"));




















}
