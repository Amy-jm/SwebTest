package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.TimeConditions.Holiday.Add_Holiday;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Holiday {

    public SelenideElement name = $(By.id("cc-hd-name-inputEl"));
    public SelenideElement byDay = $(By.id("cc-hd-date-boxLabelEl"));
    public SelenideElement byMouth = $(By.id("cc-hd-month-boxLabelEl"));
    public SelenideElement byWeek = $(By.id("cc-hd-week-boxLabelEl"));

    public SelenideElement startDate = $(By.id("cc-hd-date-startdate-inputEl"));
    public SelenideElement endDate = $(By.id("cc-hd-date-enddate-inputEl"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
