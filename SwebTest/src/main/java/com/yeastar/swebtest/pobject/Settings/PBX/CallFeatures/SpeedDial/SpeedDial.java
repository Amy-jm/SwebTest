package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.SpeedDial;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class SpeedDial {

    public int gridcolumn_SpeedDialCode = 1;
    public int gridcolumn_PhoneNumber = 2;

    public int gridEdit = 0;
    public int gridDelete = 1;
    public int gridcheck =0;


    public String grid = "Ext.getCmp('control-panel').down('speeddial')";
    public String grid_Mask = "Ext.getCmp('control-panel').down('speeddial').down('loadmask')";


    public SelenideElement speedDial = $(By.xpath(".//div[starts-with(@id,'callfeature')]//span[ text()='Speed Dial']"));

    /**
     * 功能按钮
     */

    public SelenideElement add = $(By.xpath(".//div[starts-with(@id,'speeddial-')]//span[text()='Add']"));
    public SelenideElement delete = $(By.xpath(".//div[starts-with(@id,'speeddial-')]//span[text()='Delete']"));
    public SelenideElement Import = $(By.xpath(".//div[starts-with(@id,'speeddial-')]//span[text()='Import']"));
    public SelenideElement Import_import = $(By.xpath(".//div[starts-with(@id,'speeddial-import-')]//span[starts-with(@id,'button-')]//span[text()='Import']"));
    public SelenideElement Import_browse = $(By.id("st-speeddial-filename-button"));
    public SelenideElement import_OK = $(By.xpath(".//div[starts-with(@id,'messagebox-')]//a[starts-with(@id,'button-')]//span[text()='OK']"));
    public SelenideElement export = $(By.xpath(".//div[starts-with(@id,'speeddial-')]//span[text()='Export']"));

    public SelenideElement speedDialPrefix = $(By.id("st-speeddial-speeddial-inputEl"));
    public SelenideElement speedDialPrefix_button = $(By.xpath(".//div[starts-with(@id,'speeddial')]//span[ text()='Save']"));
    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));

    /**
     * 页码功能
     */
    public SelenideElement firstPage = $(By.xpath(".//div[starts-with(@id,'speeddial')]//a[@data-qtip='First Page']"));
    public SelenideElement previousPage = $(By.xpath(".//div[starts-with(@id,'speeddial')]//a[@data-qtip='Previous Page']"));
    public SelenideElement nextPage = $(By.xpath(".//div[starts-with(@id,'speeddial')]//a[@data-qtip='Next Page']"));
    public SelenideElement lastPage = $(By.xpath(".//div[starts-with(@id,'speeddial')]//a[@data-qtip='Last Page']"));
    public SelenideElement refresh = $(By.xpath(".//div[starts-with(@id,'speeddial')]//a[@data-qtip='Refresh']"));
    public SelenideElement gotoinput = $(By.xpath(".//div[starts-with(@id,'speeddial')]//input[@name='gotoinput']"));
    public SelenideElement go = $(By.xpath(".//div[starts-with(@id,'speeddial')]//span[text()='Go']"));
    public SelenideElement selectPage = $(By.id(".//*[(@data-ref='bodyEl') and (@role='presentation')]"));

}
