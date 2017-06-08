package com.yeastar.swebtest.pobject;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by GAGA on 2017/4/26.
 */
public class PageMeSetting {
    public SelenideElement clicktochange = $(By.xpath(".//label[text()='Click To Change']"));
    public SelenideElement name = $(By.name("fullname"));
    public SelenideElement email = $(By.name("email"));
    public SelenideElement mobile = $(By.name("mobile"));
    public SelenideElement save = $(By.xpath("//div[starts-with(@id,'extensettings')]//span[text()='Save']"));

}
