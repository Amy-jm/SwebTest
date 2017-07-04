package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.RingGroup.Add_Ring_Group;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_Ring_Group {
    public SelenideElement number = $(By.id("st-rg-groupnum-inputEl"));
    public SelenideElement name = $(By.id("st-rg-groupname-inputEl"));
    public SelenideElement ringStrategy = $(By.id("st-rg-ringtype-trigger-picker"));
    public SelenideElement secondstoringeachmenmber = $(By.id("st-rg-timeout-inputEl"));



    public SelenideElement failoverDestination = $(By.id("st-rg-noansweraction-trigger-picker"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
