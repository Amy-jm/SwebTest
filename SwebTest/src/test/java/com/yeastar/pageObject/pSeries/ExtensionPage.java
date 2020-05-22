package com.yeastar.pageObject.pSeries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

@Log4j2
public class ExtensionPage {

    private SelenideElement extension_user_first_name = $(By.id("extension_user_first_name"));
    private SelenideElement extension_user_number = $(By.id("extension_user_number"));
    private SelenideElement extension_user_caller_id = $(By.id("extension_user_caller_id"));
    private SelenideElement extension_user_user_password = $(By.xpath("extension_user_user_password"));



}
