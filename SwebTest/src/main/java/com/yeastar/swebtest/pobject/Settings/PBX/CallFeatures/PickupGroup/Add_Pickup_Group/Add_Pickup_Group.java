package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.PickupGroup.Add_Pickup_Group;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_Pickup_Group {
    public String list_PickupGroup = "st-pg-member";

    public SelenideElement name = $(By.id("st-pg-name-inputEl"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'pickupgroup-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'pickupgroup-edit-')]//span[text()='Cancel']"));

}
