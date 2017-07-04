package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.AutoCLIPRoutes;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class AutoCLIPRoutes {

    public SelenideElement autoCLIPRoutes = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"AutoCLIP Routes\"]"));

    public SelenideElement deleteUsedRecords = $(By.name("useonce"));
    public SelenideElement onlyKeepMissedCallRecords = $(By.name("onlynoanswer"));
    public SelenideElement matchOutgoingTrunk = $(By.id("st-clip-checkport-displayEl"));
    public SelenideElement recordKeepTime_button = $(By.id("st-clip-keeptime-trigger-picker"));
    public SelenideElement digitsMatch = $(By.id("st-clip-matchdigit-inputEl"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
