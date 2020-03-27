package com.yeastar.swebtest.pobject.Maintenance.Reset;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Reset {
    public SelenideElement reset = $(By.id("mt-rr-reset-btnInnerEl"));

    public SelenideElement resetCode = $(By.id("mt-rr-resetcode"));
    public SelenideElement resetInputCode = $(By.id("mt-rr-code-inputEl"));

    public SelenideElement startReset = $(By.xpath(".//div[starts-with(@id,'webreset')]//span[text()='Reset']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'webreset')]//span[text()='Cancel']"));
}
