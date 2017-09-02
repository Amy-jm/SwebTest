package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.OutboundRestriction.Add_Outbound_Restriction;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Outbound_Restriction {

    public SelenideElement name = $(By.id("st-constraint-name-inputEl"));
    public SelenideElement timeLimit = $(By.id("st-constraint-limittime-inputEl"));
    public SelenideElement numberofCallsLimit = $(By.id("st-constraint-callamount-inputEl"));



    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'constraint-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'constraint-edit-')]//span[text()='Cancel']"));

}
