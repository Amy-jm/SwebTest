package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.SpeedDial.Add_Speed_Dial;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_Speed_Dial {



    public SelenideElement speedDialCode = $(By.id("st-speeddial-srcnumber-inputEl"));
    public SelenideElement phoneNumber = $(By.id("st-speeddial-destnumber-inputEl"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'speeddial-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'espeeddial-edit-')]//span[text()='Cancel']"));

}
