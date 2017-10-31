package com.yeastar.swebtest.pobject.Settings.PBX.EmergencyNumber.Add_Emergency_Number;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_Emergency_Number {

    public String trunkSelect = "trunk0";
    public String selectExtension = "admin0";

    public SelenideElement emergencyNumber = $(By.id("st-emergency-number-inputEl"));
    public SelenideElement trunk = $(By.id("prefix0-inputEl"));
    public SelenideElement trunkPrepend = $(By.id("prefix0-inputEl"));
    public String notification = "admin0";

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'emergency-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'emergency-edit-')]//span[text()='Cancel']"));

}
