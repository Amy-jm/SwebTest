package com.yeastar.swebtest.pobject.Settings.PBX.General.SIP;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class SIP {
    public SelenideElement SIP = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"SIP\"]"));

}
