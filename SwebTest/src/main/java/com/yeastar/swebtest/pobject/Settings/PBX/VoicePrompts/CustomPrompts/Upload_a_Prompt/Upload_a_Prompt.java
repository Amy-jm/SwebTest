package com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.CustomPrompts.Upload_a_Prompt;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Upload_a_Prompt {
    public SelenideElement file = $(By.id("st-cp-choosefile-inputEl"));
    public SelenideElement broese = $(By.id("st-cp-choosefile-trigger-filebutton"));
    public SelenideElement upload = $(By.xpath(".//div[starts-with(@id,'customprompt-upload-')]//span[text()='Upload']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'customprompt-upload-')]//span[text()='Cancel']"));

}
