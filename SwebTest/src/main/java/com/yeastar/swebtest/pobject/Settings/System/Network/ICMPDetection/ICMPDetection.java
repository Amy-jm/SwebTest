package com.yeastar.swebtest.pobject.Settings.System.Network.ICMPDetection;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class ICMPDetection {
    public SelenideElement ICMPDetection = $(By.xpath(".//div[starts-with(@id,'network')]//span[ text()='ICMP Detection']"));

    public SelenideElement enableICMPDetection = $(By.id("st-net-icmp-enable-displayEl"));
    public SelenideElement ICMPDetectionSerer = $(By.id("st-net-icmp-server-trigger-picker"));
    public SelenideElement test = $(By.id("st-net-icmp-test-btnInnerEl"));
    public SelenideElement ICMPDetectionInterval = $(By.id("st-net-icmp-detectinterval-inputEl"));
    public SelenideElement ICMPDetectionTimeout = $(By.id("st-net-icmp-timeout-inputEl"));
    public SelenideElement ICMPDetectionRetries = $(By.id("st-net-icmp-trycount-inputEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,\"network\")]//span[ text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,\"network\")]//span[ text()=\"Cancel\"]"));

}
