package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.Time_Conditions.TimeConditions;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class TimeConditions {
    public String grid = "Ext.getCmp('control-panel').down('timecondition')";
    public String grid_Mask = "Ext.getCmp('control-panel').down('timecondition').down('loadmask')";

    public int gridcolumn_Check = 0; //仅搜索.x-grid-row-checker
    public int gridcolumn_Name =1;
    public int gridcolumn_Time = 2;
    public int gridcolumn_Dayofweek = 3;
    public int gridcolumn_Month =4;
    public int gridcolumn_Day = 5;
    public int gridEdit = 0; //仅搜索img
    public int gridDelete = 1; //仅搜索img

    public SelenideElement timeConditions = $(By.xpath(".//div[starts-with(@id,\"timecondition\")]//span[ text()=\"Time Conditions\"]"));

    /**
     * 功能按钮
     */

    public SelenideElement add = $(By.xpath(".//div[starts-with(@id,'timecondition-')]//span[text()='Add']"));
    public SelenideElement delete = $(By.xpath(".//div[starts-with(@id,'timecondition-')]//span[text()='Delete']"));

    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));

    /**
     * 页码功能
     */
    public SelenideElement firstPage = $(By.xpath(".//div[starts-with(@id,'timecondition')]//a[@data-qtip='First Page']"));
    public SelenideElement previousPage = $(By.xpath(".//div[starts-with(@id,'timecondition')]//a[@data-qtip='Previous Page']"));
    public SelenideElement nextPage = $(By.xpath(".//div[starts-with(@id,'timecondition')]//a[@data-qtip='Next Page']"));
    public SelenideElement lastPage = $(By.xpath(".//div[starts-with(@id,'timecondition')]//a[@data-qtip='Last Page']"));
    public SelenideElement refresh = $(By.xpath(".//div[starts-with(@id,'timecondition')]//a[@data-qtip='Refresh']"));
    public SelenideElement gotoinput = $(By.xpath(".//div[starts-with(@id,'timecondition')]//input[@name='gotoinput']"));
    public SelenideElement go = $(By.xpath(".//div[starts-with(@id,'timecondition')]//span[text()='Go']"));
    public SelenideElement selectPage = $(By.id(".//*[(@data-ref='bodyEl') and (@role='presentation')]"));

}
