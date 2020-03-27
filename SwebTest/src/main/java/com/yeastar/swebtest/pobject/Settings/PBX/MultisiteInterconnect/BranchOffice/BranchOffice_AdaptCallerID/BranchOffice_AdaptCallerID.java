package com.yeastar.swebtest.pobject.Settings.PBX.MultisiteInterconnect.BranchOffice.BranchOffice_AdaptCallerID;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/10/10.
 */
public class BranchOffice_AdaptCallerID {

    public SelenideElement AdaptCallerID = $(By.xpath(".//div[starts-with(@id,'multisitembasic')]//span[text()=\"Adapt Caller ID\"]"));


    public SelenideElement addAdaptationPatterns = $(By.xpath("st-multisite-adddialpattern"));
}
