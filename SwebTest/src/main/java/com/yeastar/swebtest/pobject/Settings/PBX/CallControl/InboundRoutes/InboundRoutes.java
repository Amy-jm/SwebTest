package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.InboundRoutes;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class InboundRoutes {

    public String grid = "Ext.getCmp('control-panel').down('inrouter')";
    public String grid_Mask = "Ext.getCmp('control-panel').down('inrouter').down('loadmask')";

    public int gridcolumn_Check = 0; //仅搜索.x-grid-row-checker
    public int gridcolumn_Name = 1;
    public int gridcolumn_DID = 2;
    public int gridcolumn_CallIdPatten = 2;
    public int gridEdit = 0; //仅搜索img
    public int gridDelete = 1; //仅搜索img
    public int gridTop = 2;
    public int gridUp = 3;
    public int gridDown = 4;
    public int gridButtom = 5;


    public SelenideElement inboundRoutes = $(By.xpath(".//div[starts-with(@id,\"callcontrol\")]//span[ text()=\"Inbound Routes\"]"));


    /**
     * 功能按钮
     */

    public SelenideElement add = $(By.xpath(".//div[starts-with(@id,'inrouter-')]//span[text()='Add']"));
    public SelenideElement Import = $(By.xpath(".//div[starts-with(@id,'inrouter-')]//span[text()='Import']"));
    public SelenideElement Import_import = $(By.xpath(".//div[starts-with(@id,'inrouter-import')]//.//div[starts-with(@id,'toolbar-')]//span[text()='Import']"));
    public SelenideElement browse = $(By.id("st-inrouter-filename-trigger-filebutton"));
    public SelenideElement import_OK = $(By.xpath(".//div[starts-with(@id,'messagebox-')]//a[starts-with(@id,'button-')]//span[text()='OK']"));
    public SelenideElement delete = $(By.xpath(".//div[starts-with(@id,'inrouter-')]//span[text()='Delete']"));
    public SelenideElement addEnableTimeCondition = $(By.id("st-ir-addtimecondition"));
    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));

    /**
     * 页码功能
     */
    public SelenideElement firstPage = $(By.xpath(".//div[starts-with(@id,'inrouter')]//a[@data-qtip='First Page']"));
    public SelenideElement previousPage = $(By.xpath(".//div[starts-with(@id,'inrouter')]//a[@data-qtip='Previous Page']"));
    public SelenideElement nextPage = $(By.xpath(".//div[starts-with(@id,'inrouter')]//a[@data-qtip='Next Page']"));
    public SelenideElement lastPage = $(By.xpath(".//div[starts-with(@id,'inrouter')]//a[@data-qtip='Last Page']"));
    public SelenideElement refresh = $(By.xpath(".//div[starts-with(@id,'inrouter')]//a[@data-qtip='Refresh']"));
    public SelenideElement gotoinput = $(By.xpath(".//div[starts-with(@id,'inrouter')]//input[@name='gotoinput']"));
    public SelenideElement go = $(By.xpath(".//div[starts-with(@id,'inrouter')]//span[text()='Go']"));
    public SelenideElement selectPage = $(By.id(".//*[(@data-ref='bodyEl') and (@role='presentation')]"));

}
