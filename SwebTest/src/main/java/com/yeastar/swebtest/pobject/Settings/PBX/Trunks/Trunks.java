package com.yeastar.swebtest.pobject.Settings.PBX.Trunks;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Trunks {

    /**
     * 表格
     */
    public int gridcolumn_Check = 0; //仅搜索.x-grid-row-checker
    public int gridcolumn_TrunkName = 1;
    public int gridcolumn_Type = 2;
    public int grid_column_Hostname = 3;
    public int grid_column_Username = 4;
    public int gridEdit = 0; //仅搜索img
    public int gridDelete = 1; //仅搜索img
    public String grid = "Ext.getCmp('control-panel').down('trunk').down('trunklist')";
    public String grid_Mask = "Ext.getCmp('control-panel').down('trunk').down('loadmask')";

    /**
     * 功能按钮
     */
    public SelenideElement add = $(By.xpath(".//div[starts-with(@id,'trunklist')]//span[text()=\"Add\"]"));
    public SelenideElement delete = $(By.xpath(".//div[starts-with(@id,'trunklist')]//span[text()=\"Delete\"]"));

    public SelenideElement search_Text = $(By.xpath(".//*[@placeholder='Extension,Name,Type']"));
    public SelenideElement search_Button = $(By.xpath(".//*[@src='images/search.png']"));

    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));

    /**
     * 页码功能
     */
    public SelenideElement firstPage = $(By.xpath(".//div[starts-with(@id,'trunklist')]//a[@data-qtip='First Page']"));
    public SelenideElement previousPage = $(By.xpath(".//div[starts-with(@id,'trunklist')]//a[@data-qtip='Previous Page']"));
    public SelenideElement nextPage = $(By.xpath(".//div[starts-with(@id,'trunklist')]//a[@data-qtip='Next Page']"));
    public SelenideElement lastPage = $(By.xpath(".//div[starts-with(@id,'trunklist')]//a[@data-qtip='Last Page']"));
    public SelenideElement refresh = $(By.xpath(".//div[starts-with(@id,'trunklist')]//a[@data-qtip='Refresh']"));
    public SelenideElement gotoinput = $(By.xpath(".//div[starts-with(@id,'trunklist')]//input[@name='gotoinput']"));
    public SelenideElement go = $(By.xpath(".//div[starts-with(@id,'trunklist')]//span[text()='Go']"));
    public SelenideElement selectPage = $(By.id(".//*[(@data-ref='bodyEl') and (@role='presentation')]"));

}
