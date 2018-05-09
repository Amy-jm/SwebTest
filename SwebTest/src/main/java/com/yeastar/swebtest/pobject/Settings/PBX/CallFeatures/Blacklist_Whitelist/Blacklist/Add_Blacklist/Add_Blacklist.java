package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Blacklist_Whitelist.Blacklist.Add_Blacklist;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_Blacklist {

    public int type_Inbound = 1;
    public int type_Outbound =2;
    public int type_Both =3;

    public SelenideElement name = $(By.id("st-bw-name-inputEl"));
    public SelenideElement number = $(By.id("st-bw-numbers-inputEl"));
    public String type = "st-bw-type";
    public SelenideElement allExtensions = $(By.xpath(".//div[starts-with(@id,'radiofield-')]//label[text()='All Extensions']"));
    public SelenideElement selectExtensions = $(By.xpath(".//div[starts-with(@id,'radiofield-')]//label[text()='Selected Extensions']"));
    public SelenideElement addAllToSelected = $(By.xpath(".//div[@id='st-bw-outlist']//a[@data-qtip='Add All to Selected']"));
    public String list = "st-bw-outlist";

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'blacklist-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'blacklist-edit-')]//span[text()='Cancel']"));

}
