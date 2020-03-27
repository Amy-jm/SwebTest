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

    public SelenideElement Extensions = $(By.xpath(".//div[starts-with(@id,'extension')]//span[ text()='Extensions']"));

    /**
     * Extensions表格
     */

    public String mask_added_id = "return Ext.query('#control-panel #control-panel-body .x-mask')[1].id";

    public int gridcolumn_Check = 0; //仅搜索.x-grid-row-checker
    public int gridcolumn_Extensions = 3;
    public int gridcolumn_Name = 4;
    public int gridcolumn_Type = 5;
    public int gridcolumn_Port = 6;

    public int gridEdit = 0; //仅搜索img
    public int gridDelete = 1; //仅搜索img

    public String grid = "Ext.getCmp('control-panel').down('extension')";
    public String grid_Mask = "Ext.getCmp('control-panel').down('extension').down('loadmask')";
    public String grid_status = "Ext.getCmp(Ext.getCmp('pbxmonitor').down('b-extenstatus').id).down('tableview')";
    public String grid_CDR = "Ext.getCmp('cdr-record').down('grid').down('tableview')";
    public String grid_CDR_Mask = "Ext.getCmp('cdr-record').down('grid').down('loadmask')";
    public String grid_extension_all_select = "Ext.query('#'+Ext.getCmp('control-panel').down('extension').down('headercontainer').id + ' div div div div')[0].click()";
    public String grid_extensionGroup_allSelect = "Ext.query('#'+Ext.getCmp('control-panel').down('extensiongroup').down('headercontainer').id + ' div div div div')[0].click()";
    /**
     * 功能按钮
     */

    public SelenideElement add = $(By.xpath(".//div[starts-with(@id,'extension')]//span[text()='Add']"));
    public SelenideElement bulkAdd = $(By.xpath(".//div[starts-with(@id,'extension')]//span[text()='Bulk Add']"));
    public SelenideElement edit = $(By.xpath(".//div[starts-with(@id,'extension')]//span[text()='Edit']"));
    public SelenideElement delete = $(By.xpath(".//div[starts-with(@id,'extension')]//span[text()='Delete']"));
    public SelenideElement Import = $(By.xpath(".//div[starts-with(@id,'extension')]//span[text()='Import']"));
    public SelenideElement export = $(By.xpath(".//div[starts-with(@id,'extension')]//span[text()='Export']"));
    public SelenideElement ImportExtension_Input = $(By.id("st-exten-filename-inputEl"));
    public SelenideElement ImportExtension_Browse = $(By.id("st-exten-filename-trigger-filebutton"));
    public SelenideElement ImortExtension_Import = $(By.xpath(".//div[starts-with(@id,'extension-import')]//span[text()='Import']"));
    public SelenideElement ImortExtension_Cancel = $(By.xpath(".//div[starts-with(@id,'extension-import')]//span[text()='Cancel']"));
    public SelenideElement ImportExtensionOK = $(By.xpath(".//div[starts-with(@id,\"messagebox\")]//span[text()=\"OK\"]"));

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
    public SelenideElement firstPage = $(By.xpath(".//div[starts-with(@id,'extension')]//a[@data-qtip='First Page']"));
    public SelenideElement previousPage = $(By.xpath(".//div[starts-with(@id,'extension')]//a[@data-qtip='Previous Page']"));
    public SelenideElement nextPage = $(By.xpath(".//div[starts-with(@id,'extension')]//a[@data-qtip='Next Page']"));
    public SelenideElement lastPage = $(By.xpath(".//div[starts-with(@id,'extension')]//a[@data-qtip='Last Page']"));
    public SelenideElement refresh = $(By.xpath(".//div[starts-with(@id,'extension')]//a[@data-qtip='Refresh']"));
    public SelenideElement gotoinput = $(By.xpath(".//div[starts-with(@id,'extension')]//input[@name='gotoinput']"));
    public SelenideElement go = $(By.xpath(".//div[starts-with(@id,'extension')]//span[text()='Go']"));
    public SelenideElement selectPage = $(By.id(".//*[(@data-ref='bodyEl') and (@role='presentation')]"));


}
