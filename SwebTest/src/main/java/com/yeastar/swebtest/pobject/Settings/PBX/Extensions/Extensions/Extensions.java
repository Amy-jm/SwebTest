package com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.swebtest.driver.SwebDriver.executeJs;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Extensions {
    /**
     * 进入Extensions
     */

    public SelenideElement Extensions = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Extensions\"]"));

    /**
     * Extensions表格
     */
    public String grid_extensions = "Ext.getCmp('control-panel').down('extension')";
    public int gridCheck = 0; //仅搜索.x-grid-row-checker
    public int gridcolumn_Extensions = 3;
    public int gridcolumn_Name = 4;
    public int gridcolumn_Type = 5;
    public int gridcolumn_Port = 6;
    public int gridEdit = 0; //仅搜索img
    public int gridDelete = 1; //仅搜索img

    public void GridExtensions(int row, int column) {
        String two = executeJs("return Ext.getCmp('control-panel').down('extension').down(\"actioncolumn\").id");
        //String three = executeJs("return Ext.query('#'+Ext.query('#'+Ext.getCmp('control-panel').down('extension').id + ' [data-recordindex]')[0].id + ' tr td')[6].textContent");
    }

    /**
     * 功能按钮
     */

    public SelenideElement add = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Add\"]"));
    public SelenideElement bulkAdd = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Bulk Add\"]"));
    public SelenideElement edit = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Edit\"]"));
    public SelenideElement delete = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Delete\"]"));
    public SelenideElement Import = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Import\"]"));
    public SelenideElement export = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Export\"]"));

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
    public SelenideElement firstPage = $(By.xpath(".//*[@data-qtip='First Page']"));
    public SelenideElement previousPage = $(By.xpath(".//*[@data-qtip='Previous Page']"));
    public SelenideElement nextPage = $(By.xpath(".//*[@data-qtip='Next Page']"));
    public SelenideElement lastPage = $(By.xpath(".//*[@data-qtip='Last Page']"));
    public SelenideElement refresh = $(By.xpath(".//*[@data-qtip='Refresh']"));
    public SelenideElement gotoinput = $(By.name("gotoinput"));
    public SelenideElement go = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-toolbar-small\") and text()=\"Go\"]"));
    public SelenideElement selectPage = $(By.id(".//*[(@data-ref='bodyEl') and (@role='presentation')]"));



}
