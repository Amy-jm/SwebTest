package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.RingGroup.Add_Ring_Group;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_Ring_Group {
    public String list_RingGroup = "st-rg-groupextens";

    public SelenideElement number = $(By.id("st-rg-groupnum-inputEl"));
    public SelenideElement name = $(By.id("st-rg-groupname-inputEl"));
//    public SelenideElement ringStrategy = $(By.id("st-rg-ringtype"));
    public String ringStrategy= "st-rg-ringtype";
    public SelenideElement secondstoringeachmenmber = $(By.id("st-rg-timeout-inputEl"));

    //    响铃策略的选择
    public String rs_ringall = "ringall";
    public String rs_sequentially="ringinorder";

    public String failoverDestinationtype = "st-rg-noansweraction";
    public String failoverDestination = "st-rg-noanswerdest";

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'ringgroup-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'ringgroup-edit-')]//span[text()='Cancel']"));


}
