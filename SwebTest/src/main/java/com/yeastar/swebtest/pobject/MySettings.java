package com.yeastar.swebtest.pobject;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/7/7.
 */
public class MySettings {
    public SelenideElement close = $(By.xpath(".//div[starts-with(@class,\"x-tool-img x-tool-close\")]"));

}
