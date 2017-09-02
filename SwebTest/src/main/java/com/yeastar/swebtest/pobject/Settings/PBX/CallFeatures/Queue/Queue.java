package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Queue;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Queue {
    public int gridcolumn_Check = 0; //仅搜索.x-grid-row-checker
    public int gridcolumn_Number = 1;
    public int gridcolumn_Name = 2;
    public int gridcolumn_Member = 3;
    public int gridcolumn_RingStrategy = 4;

    public int gridEdit = 0; //仅搜索img
    public int gridDelete = 1; //仅搜索img

    public String grid = "Ext.getCmp('control-panel').down('queue')";
    public String grid_Mask = "Ext.getCmp('control-panel').down('queue').down('loadmask')";

    public SelenideElement queue = $(By.xpath(".//span[starts-with(@class,\"toolbartip\") and text()=\"Queue\"]"));

    /**
     * 功能按钮
     */

    public SelenideElement add = $(By.xpath(".//div[starts-with(@id,'queue-')]//span[text()='Add']"));
    public SelenideElement delete = $(By.xpath(".//div[starts-with(@id,'queue-')]//span[text()='Delete']"));

    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));

    /**
     * 页码功能
     */
    public SelenideElement firstPage = $(By.xpath(".//div[starts-with(@id,'queue')]//a[@data-qtip='First Page']"));
    public SelenideElement previousPage = $(By.xpath(".//div[starts-with(@id,'queue')]//a[@data-qtip='Previous Page']"));
    public SelenideElement nextPage = $(By.xpath(".//div[starts-with(@id,'queue')]//a[@data-qtip='Next Page']"));
    public SelenideElement lastPage = $(By.xpath(".//div[starts-with(@id,'queue')]//a[@data-qtip='Last Page']"));
    public SelenideElement refresh = $(By.xpath(".//div[starts-with(@id,'queue')]//a[@data-qtip='Refresh']"));
    public SelenideElement gotoinput = $(By.xpath(".//div[starts-with(@id,'queue')]//input[@name='gotoinput']"));
    public SelenideElement go = $(By.xpath(".//div[starts-with(@id,'queue')]//span[text()='Go']"));
    public SelenideElement selectPage = $(By.id(".//*[(@data-ref='bodyEl') and (@role='presentation')]"));

}
