package com.yeastar.swebtest.pobject;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/8/15.
 */
public class PbxMonitor {

    public String grid_Trunks = "Ext.getCmp('pbxmonitor').down('b-trunkstatus')";
    public int gridTrunks_Status = 0;
    public int gridTrunks_Name = 1;
    public int gridTrunks_Type = 2;
    public int gridTrunks_Hostname = 3;

    public String Status_UP = "Up";







    public SelenideElement extension = $(By.xpath(".//div[starts-with(@id,'pbxmonitor')]//span[text()='Extension']"));
    public SelenideElement trunks = $(By.xpath(".//div[starts-with(@id,'pbxmonitor')]//span[text()='Trunks']"));
    public SelenideElement concurrent_Call = $(By.xpath(".//div[starts-with(@id,'pbxmonitor')]//span[text()='Concurrent Call']"));
    public SelenideElement conference = $(By.xpath(".//div[starts-with(@id,'pbxmonitor')]//span[text()='Conference']"));
}
