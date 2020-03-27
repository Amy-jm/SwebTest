package com.yeastar.swebtest.pobject.Settings.PBX.General.API;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/10/10.
 */
public class API {

    public SelenideElement IAX = $(By.xpath(".//div[starts-with(@id,'general')]//span[ text()='API']"));

    public String gridExtension = "general-api-ext";

    public String gridTurnk = "general-api-trunk";

    public String enabel = "st-api-enable";
    public SelenideElement username = $(By.id("st-api-username-inputEl"));
    public SelenideElement password = $(By.id("st-api-password-inputEl"));

    public SelenideElement extension = $(By.xpath(".//div[starts-with(@id,'general-api-ext')]//span[text()='Extension']"));
    public SelenideElement trunk = $(By.xpath(".//div[starts-with(@id,'general-api-ext')]//span[text()='trunk']"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'st-api')]//span[text()='Save']"));
}
