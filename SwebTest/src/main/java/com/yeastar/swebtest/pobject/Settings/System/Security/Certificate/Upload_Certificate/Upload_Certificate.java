package com.yeastar.swebtest.pobject.Settings.System.Security.Certificate.Upload_Certificate;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Upload_Certificate {
    public String type = ("st-cert-type-trigger");
    public SelenideElement file = $(By.id("st-cert-choosefile-inputEl"));
    public SelenideElement browse = $(By.id("st-cert-choosefile-button-fileInputEl"));
    public SelenideElement upload = $(By.xpath(".//div[starts-with(@id,'certificates-upload')]//span[ text()='Upload']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'certificates-upload')]//span[ text()='Cancel']"));

}
