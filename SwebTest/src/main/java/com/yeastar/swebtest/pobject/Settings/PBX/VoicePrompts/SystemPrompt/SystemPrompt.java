package com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.SystemPrompt;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class SystemPrompt {
    public SelenideElement file = $(By.id("st-sp-promptfile-inputEl"));
    public SelenideElement browse = $(By.id("st-sp-promptfile-button-fileInputEl"));
    public SelenideElement upload = $(By.id("st-sp-upload-btnInnerEl"));
    public SelenideElement downloadOnlinePrompt = $(By.id("st-sp-packagemanage-btnInnerEl"));


    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
