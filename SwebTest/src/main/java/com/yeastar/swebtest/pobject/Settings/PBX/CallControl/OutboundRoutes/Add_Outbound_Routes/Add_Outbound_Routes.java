package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.OutboundRoutes.Add_Outbound_Routes;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Outbound_Routes {
    public SelenideElement name = $(By.id("st-or-name-inputEl"));
    public SelenideElement dialPattems = $(By.id("st-ir-adddialpattern"));


    public SelenideElement password_button = $(By.id("st-or-pintype-trigger-picker"));
    public SelenideElement rrmemoryHunt = $(By.id("st-or-adjusttrunk-displayEl"));
    public SelenideElement Workday = $(By.id("st-or-1-displayEl"));


    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
