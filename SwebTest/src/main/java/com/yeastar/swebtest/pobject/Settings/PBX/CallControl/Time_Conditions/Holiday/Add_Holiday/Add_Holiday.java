package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.Time_Conditions.Holiday.Add_Holiday;

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

    //    byDay
    public SelenideElement startDate = $(By.id("cc-hd-date-startdate-inputEl"));
    public SelenideElement endDate = $(By.id("cc-hd-date-enddate-inputEl"));

    //    byMonth
    public String startMonth= "cc-hd-month-start-m";
    public  String endMonth = "cc-hd-month-end-m";
    public  String startDay = "cc-hd-month-start-d";
    public  String endDay ="cc-hd-month-end-d";

    //    byWeek
//    public SelenideElement weekMonth=$(By.id("cc-hd-week-start-m"));
//    public SelenideElement weeknum = $(By.id("cc-hd-week-start-n"));
//    public SelenideElement weekday =$(By.id("cc-hd-week-start-w"));
    public  String weekMonth="cc-hd-week-start-m";
    public  String weeknum = "cc-hd-week-start-n";
    public  String weekday ="cc-hd-week-start-w";

    // 月份定义
    public String january="1";
    public String february="2";
    public String march="3";
    public String april="4";
    public String may ="5";
    public String june ="6";
    public String july ="7";
    public String august ="8";
    public String september="9";
    public String october="10";
    public String november="11";
    public String december="12";

    //    第几周定义
    public String first = "1";
    public String second ="2";
    public String third ="3";
    public String fourth ="4";
    public String last ="5";

    //    星期几定义
    public String sunday ="0";
    public String monday="1";
    public String tuesday="2";
    public String wednesday="3";
    public String thursday="4";
    public String friday="5";
    public String saturday="6";


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'holiday-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'holiday-edit-')]//span[text()='Cancel']"));

}
