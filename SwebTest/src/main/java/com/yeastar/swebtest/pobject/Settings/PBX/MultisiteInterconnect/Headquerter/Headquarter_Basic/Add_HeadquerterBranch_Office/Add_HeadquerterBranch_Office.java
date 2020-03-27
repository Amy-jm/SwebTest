package com.yeastar.swebtest.pobject.Settings.PBX.MultisiteInterconnect.Headquerter.Headquarter_Basic.Add_HeadquerterBranch_Office;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/10/10.
 */
public class Add_HeadquerterBranch_Office {

    public SelenideElement name = $(By.id("st-multisite-name-inputEl"));
    public SelenideElement branchID = $(By.id("st-multisite-stationid-inputEl"));
    public SelenideElement account = $(By.xpath("st-multisite-username-inputEl"));
    public SelenideElement password = $(By.xpath("st-multisite-secret-inputEl"));

    public String enableIpRestrict = "st-multisite-enableiprestrict";

    public SelenideElement permitted = $(By.id("permitip0-inputEl"));
    public SelenideElement submask = $(By.id("permitmask0-inputEl"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,\"multisitebbasic-edit\")]//span[ text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,\"multisitembasic-edit\")]//span[ text()=\"Cancel\"]"));
}
