package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Blacklist_Whitelist.Whitelist;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Whitelist {

    public int gridcolumn_Name = 1;
    public int gridcolumn_Number = 2;
    public int gridcolumn_Type = 3;
    public int gridEdit = 0; //仅搜索img
    public int gridDelete = 1; //仅搜索img

    public String grid = "Ext.getCmp('control-panel').down('whitelist').down('tableview')";
    public String grid_loadMask = "Ext.getCmp('control-panel').down('whitelist').down('loadmask')";


    public SelenideElement whitelist = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Whitelist\"]"));

    /**
     * 功能按钮
     */

    public SelenideElement add = $(By.xpath(".//div[starts-with(@id,'whitelist-')]//span[text()='Add']"));
    public SelenideElement delete = $(By.xpath(".//div[starts-with(@id,'whitelist-')]//span[text()='Delete']"));
    public SelenideElement Import = $(By.xpath(".//div[starts-with(@id,'whitelist-')]//span[text()='Import']"));
    public SelenideElement export = $(By.xpath(".//div[starts-with(@id,'whitelist-')]//span[text()='Export']"));

    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));

    /**
     * 页码功能
     */
    public SelenideElement firstPage = $(By.xpath(".//div[starts-with(@id,'whitelist')]//a[@data-qtip='First Page']"));
    public SelenideElement previousPage = $(By.xpath(".//div[starts-with(@id,'whitelist')]//a[@data-qtip='Previous Page']"));
    public SelenideElement nextPage = $(By.xpath(".//div[starts-with(@id,'whitelist')]//a[@data-qtip='Next Page']"));
    public SelenideElement lastPage = $(By.xpath(".//div[starts-with(@id,'whitelist')]//a[@data-qtip='Last Page']"));
    public SelenideElement refresh = $(By.xpath(".//div[starts-with(@id,'whitelist')]//a[@data-qtip='Refresh']"));
    public SelenideElement gotoinput = $(By.xpath(".//div[starts-with(@id,'whitelist')]//input[@name='gotoinput']"));
    public SelenideElement go = $(By.xpath(".//div[starts-with(@id,'whitelist')]//span[text()='Go']"));
    public SelenideElement selectPage = $(By.id(".//*[(@data-ref='bodyEl') and (@role='presentation')]"));

}
