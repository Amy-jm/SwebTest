package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.TimeConditions.TimeConditions.Add_Time_Condition;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Time_Condition {
    public SelenideElement name  = $(By.id("cc-tc-name-inputEl"));

    public SelenideElement starthour  = $(By.name("starthour"));
    public SelenideElement startminu  = $(By.name("startminu"));
    public SelenideElement endhour  = $(By.name("endhour"));
    public SelenideElement endminu  = $(By.name("endminu"));

    public SelenideElement all  = $(By.id("cc-tc-weekall-displayEl"));
    public SelenideElement sunday  = $(By.id("cc-tc-sun-displayEl"));
    public SelenideElement monday  = $(By.id("cc-tc-mon-displayEl"));
    public SelenideElement tuesday  = $(By.id("cc-tc-tue-displayEl"));
    public SelenideElement wednesday  = $(By.id("cc-tc-wen-displayEl"));
    public SelenideElement thursday  = $(By.id("cc-tc-thu-displayEl"));
    public SelenideElement friday  = $(By.id("cc-tc-fri-displayEl"));
    public SelenideElement saturday  = $(By.id("cc-tc-sat-displayEl"));

    public SelenideElement advancedOptions = $(By.id("cc-tc-enablemonth-displayEl"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'timecondition-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'timecondition-edit-')]//span[text()='Cancel']"));

}
