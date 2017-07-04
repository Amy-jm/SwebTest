package com.yeastar.swebtest.pobject.Settings.System.DateTime;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class DateTime {
    public SelenideElement timeZone = $(By.id("st-time-timezone-trigger-picker"));
    public SelenideElement daylightSavingTime = $(By.id("st-time-enabledst-trigger-picker"));
    public SelenideElement synchronizeWithNTPServer = $(By.id("st-time-enablentp-boxLabelEl"));
    public SelenideElement NTPServer = $(By.id("st-time-ntpserver-inputEl"));
    public SelenideElement setUpManually = $(By.id("st-time-enablemanually-boxLabelEl"));
    public SelenideElement date = $(By.id("st-time-date-inputEl"));
    public SelenideElement time_hour = $(By.id("st-time-hour-inputEl"));
    public SelenideElement time_minute = $(By.id("st-time-minute-inputEl"));
    public SelenideElement time_second = $(By.id("st-time-second-inputEl"));


    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));








}
