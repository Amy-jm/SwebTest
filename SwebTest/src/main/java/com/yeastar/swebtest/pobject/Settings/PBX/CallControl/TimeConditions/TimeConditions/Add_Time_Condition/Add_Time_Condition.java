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

    public SelenideElement all  = $(By.name("cc-tc-weekall-displayEl"));
    public SelenideElement sunday  = $(By.name("cc-tc-sun-displayEl"));
    public SelenideElement monday  = $(By.name("cc-tc-mon-displayEl"));
    public SelenideElement tuesday  = $(By.name("cc-tc-tue-displayEl"));
    public SelenideElement wednesday  = $(By.name("cc-tc-wen-displayEl"));
    public SelenideElement thursday  = $(By.name("cc-tc-thu-displayEl"));
    public SelenideElement friday  = $(By.name("cc-tc-fri-displayEl"));
    public SelenideElement saturday  = $(By.name("cc-tc-sat-displayEl"));

    public SelenideElement advancedOptions = $(By.id("cc-tc-enablemonth-displayEl"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
