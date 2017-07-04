package com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.CustomPrompts.Upload_a_Prompt;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Upload_a_Prompt {
    public SelenideElement file = $(By.id("st-cp-choosefile-inputEl"));
    public SelenideElement broese = $(By.id("st-cp-choosefile-button-fileInputEl"));
    public SelenideElement upload = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Upload\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
