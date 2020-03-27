package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class CallFeatures {
    public SelenideElement more = $(By.xpath(".//div[starts-with(@id,'callfeature')]//span[text()='More']"));
    public SelenideElement back = $(By.xpath(".//div[starts-with(@id,'callfeature')]//span[text()='Back']"));
}
