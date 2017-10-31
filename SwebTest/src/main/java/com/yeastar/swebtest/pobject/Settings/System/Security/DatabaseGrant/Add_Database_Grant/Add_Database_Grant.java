package com.yeastar.swebtest.pobject.Settings.System.Security.DatabaseGrant.Add_Database_Grant;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_Database_Grant {
    public SelenideElement username = $(By.id("st-database-username-inputEl"));
    public SelenideElement password = $(By.id("st-database-password-inputEl"));
    public SelenideElement permitterIP = $(By.id("host0-inputEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'databasegrant-edit')]//span[ text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'databasegrant-edit')]//span[ text()='Cancel']"));



}
