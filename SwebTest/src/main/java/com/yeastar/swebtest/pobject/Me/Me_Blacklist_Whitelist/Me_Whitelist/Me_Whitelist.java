package com.yeastar.swebtest.pobject.Me.Me_Blacklist_Whitelist.Me_Whitelist;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.swebtest.driver.SwebDriver.executeJs;

/**
 * Created by Yeastar on 2017/7/21.
 */
public class Me_Whitelist {

    public String grid = "Ext.getCmp('mesetting').down('meblackwhitelist').down('mewhitelist').down('tableview')";
    public String grid_Mask = "Ext.getCmp('mesetting').down('meblackwhitelist').down('mewhitelist').down('loadmask')";

    public int gridColumn_Check = 0;
    public int gridColumn_Name = 1;
    public int gridColumn_Number =2;
    public int gridColumn_Type = 3;

    public int gridEdit = 0;
    public int gridDelete =1;


    public SelenideElement me_Whitelist = $(By.xpath(".//div[starts-with(@id,'meblackwhitelist-')]//span[text()='Whitelist']"));

    /**
     * 功能按钮
     */

    public SelenideElement add = $(By.xpath(".//div[starts-with(@id,'mewhitelist-')]//span[text()='Add']"));
    public SelenideElement delete = $(By.xpath(".//div[starts-with(@id,'mewhitelist-')]//span[text()='Delete']"));
    public SelenideElement Import = $(By.xpath(".//div[starts-with(@id,'mewhitelist-')]//span[text()='Import']"));
    public SelenideElement export = $(By.xpath(".//div[starts-with(@id,'mewhitelist-')]//span[text()='Export']"));

    /*
    * 导入文件
    * */
    public SelenideElement browse = $(By.id("me-blacklist-filename-trigger-filebutton"));
    public SelenideElement Import_OK = $(By.xpath(".//div[starts-with(@id,\"messagebox\")]//span[text()=\"OK\"]"));
    public SelenideElement Import_import = $(By.xpath(".//div[starts-with(@id,'meblacklist-import-')]//span[text()='Import']"));

    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));

    public SelenideElement whitelistOnly = $(By.id("me-bw-onlywhitelist-boxLabelEl"));
//    public SelenideElement whitelistOnly = $(By.id("me-bw-onlywhitelist-inputEl"));

    public String getOnlyWhiteSelect(){
        return String.valueOf(executeJs("return Ext.getCmp(\"me-bw-onlywhitelist\").value"));
    }

    public SelenideElement ok = $(By.xpath(".//div[starts-with(@id,'messagebox-')]//span[text()='OK']"));
}
