package com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.CustomPrompts;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class CustomPrompts {

    public int gridcolumn_Name = 1;
    public int gridRecord = 0;
    public int gridPlay = 1;
    public int gridDownload = 2;
    public int gridDelete = 3 ;
    public String grid = "Ext.getCmp('control-panel').down('customprompt').down('tableview')";
    public String grid_Mask = "Ext.getCmp('control-panel').down('customprompt').down('loadmask')";


    public SelenideElement customPrompts = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Custom Prompts\"]"));

    public SelenideElement recordNew = $(By.xpath(".//div[starts-with(@id,'customprompt-')]//span[text()='Record New']"));
    public SelenideElement upload = $(By.xpath(".//div[starts-with(@id,'customprompt-')]//span[text()='Upload']"));
    public SelenideElement delete = $(By.xpath(".//div[starts-with(@id,'customprompt-')]//span[text()='Delete']"));

    /**
     * play
     */
    public SelenideElement name = $(By.id("st-cp-name-inputEl"));
    public SelenideElement palyToExtension = $(By.id("st-moh-extension-trigger-picker"));
    public SelenideElement play = $(By.xpath(".//div[starts-with(@id,'customprompt-play-')]//span[text()='Play']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'customprompt-play-')]//span[text()='Cancel']"));
    public String  plauToExtension = "st-cp-extension";


    public SelenideElement record_play = $(By.xpath(".//div[starts-with(@id,'customprompt-record-')]//span[text()='Record']"));
    public SelenideElement record_cancel = $(By.xpath(".//div[starts-with(@id,'customprompt-record-')]//span[text()='Cancel']"));
    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));

    /**
     * 页码功能
     */
    public SelenideElement firstPage = $(By.xpath(".//div[starts-with(@id,'customprompt')]//a[@data-qtip='First Page']"));
    public SelenideElement previousPage = $(By.xpath(".//div[starts-with(@id,'customprompt')]//a[@data-qtip='Previous Page']"));
    public SelenideElement nextPage = $(By.xpath(".//div[starts-with(@id,'customprompt')]//a[@data-qtip='Next Page']"));
    public SelenideElement lastPage = $(By.xpath(".//div[starts-with(@id,'customprompt')]//a[@data-qtip='Last Page']"));
    public SelenideElement refresh = $(By.xpath(".//div[starts-with(@id,'customprompt')]//a[@data-qtip='Refresh']"));
    public SelenideElement gotoinput = $(By.xpath(".//div[starts-with(@id,'customprompt')]//input[@name='gotoinput']"));
    public SelenideElement go = $(By.xpath(".//div[starts-with(@id,'customprompt')]//span[text()='Go']"));
    public SelenideElement selectPage = $(By.id(".//*[(@data-ref='bodyEl') and (@role='presentation')]"));

}
