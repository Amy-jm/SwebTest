package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.AutoCLIPRoutes;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.swebtest.driver.SwebDriver.executeJs;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class AutoCLIPRoutes {

    public String grid = "Ext.getCmp('control-panel').down('cliplist').down('tableview')";
    public String grid_Mask = "Ext.getCmp('control-panel').down('cliplist').down('loadmask')";

    public int gridColumn_Checked = 0;
    public int gridColumn_ExtNumber = 1;
    public int gridColumn_CallerNumber = 2;
    public int gridColumn_Trunk = 3;
    public int gridColumn_ExpirationsTime = 4;

    public String list= "st-clip-trunkinfo";
    public SelenideElement autoCLIPRoutes = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"AutoCLIP Routes\"]"));

    public SelenideElement viewAutoCLIPList = $(By.xpath(".//div[starts-with(@id,'callcontrol-')]//span[text()='View AutoCLIP List']"));

    public SelenideElement deleteUsedRecords = $(By.name("useonce"));
    public SelenideElement onlyKeepMissedCallRecords = $(By.name("onlynoanswer"));
    public SelenideElement matchOutgoingTrunk = $(By.id("st-clip-checkport-displayEl"));
    public SelenideElement recordKeepTime_button = $(By.id("st-clip-keeptime-trigger-picker"));
    public SelenideElement digitsMatch = $(By.id("st-clip-matchdigit-inputEl"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

    public SelenideElement mt_RemoveAllFromSelect = $(By.xpath(".//div[@id='st-clip-trunkinfo']//a[@data-qtip='Remove All from Selected']"));

    //AutoClipRoutes
    public SelenideElement delete = $(By.xpath(".//div[starts-with(@id,'cliplist-')]//span[text()='Delete']"));

    public SelenideElement delete_yes = $(By.xpath(".//div[starts-with(@id,'messagebox-')]//span[text()='Yes']"));
    public SelenideElement delete_no = $(By.xpath(".//div[starts-with(@id,'messagebox-')]//span[text()='No']"));

    public void closeAutoClIP_List(){
        executeJs("Ext.get(Ext.getCmp('control-panel').down('cliplist').down('tool').id).dom.click()");
    }
}
