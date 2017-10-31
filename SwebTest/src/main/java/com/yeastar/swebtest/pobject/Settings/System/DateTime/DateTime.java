package com.yeastar.swebtest.pobject.Settings.System.DateTime;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class DateTime {

    public String chinaTime = "UTC+8 Asia/Shanghai";

    public String timeZone = "st-time-timezone";
//    public SelenideElement timeZone = $(By.id("st-time-timezone-trigger-picker"));
    public String daylightSavingTime = ("st-time-enabledst");
//    public SelenideElement daylightSavingTime = $(By.id("st-time-enabledst-trigger-picker"));
    public SelenideElement synchronizeWithNTPServer = $(By.id("st-time-enablentp-boxLabelEl"));
    public SelenideElement NTPServer = $(By.id("st-time-ntpserver-inputEl"));
    public SelenideElement setUpManually = $(By.id("st-time-enablemanually-boxLabelEl"));
    public SelenideElement date = $(By.id("st-time-date-inputEl"));
    public SelenideElement time_hour = $(By.id("st-time-hour-inputEl"));
    public SelenideElement time_minute = $(By.id("st-time-minute-inputEl"));
    public SelenideElement time_second = $(By.id("st-time-second-inputEl"));
    public SelenideElement current_time = $(By.id("st-time-currenttime-inputEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'timesetting-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'timesetting-')]//span[text()='Cancel']"));







}
