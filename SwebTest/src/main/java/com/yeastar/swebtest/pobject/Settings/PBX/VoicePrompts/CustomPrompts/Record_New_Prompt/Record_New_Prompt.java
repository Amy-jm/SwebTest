package com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.CustomPrompts.Record_New_Prompt;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Record_New_Prompt {
    public SelenideElement name = $(By.id("st-cp-name-inputEl"));
    public SelenideElement extension = $(By.id("st-cp-extension-inputEl"));

    public SelenideElement record = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Record\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
