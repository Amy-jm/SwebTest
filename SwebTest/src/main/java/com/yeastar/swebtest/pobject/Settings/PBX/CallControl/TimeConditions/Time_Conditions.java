package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.TimeConditions;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Time_Conditions {
    public String grid = "Ext.getCmp('control-panel').down('timecondition')";
    public int gridcolumn_Name =1;
    public int gridcolumn_Time = 2;
    public int gridcolumn_Dayofweek = 3;
    public int gridcolumn_Month =4;
    public int gridcolumn_Day = 5;
    public int gridEdit = 0; //仅搜索img
    public int gridDelete = 1; //仅搜索img

    public SelenideElement timeConditions = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Time Conditions\"]"));


}
