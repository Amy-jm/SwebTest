package com.yeastar.swebtest.pobject.Settings.EventCenter.EventSettings.NotificationContacts;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class NotificationContacts {
    public String grid = "Ext.getCmp('control-panel').down('contact').down('tableview')";
    public String grid_Mask = "Ext.getCmp('control-panel').down('contact').down('loadmask')";

    public int gridcolumn_Check = 0; //仅搜索.x-grid-row-checker
    public int gridColumn_Name = 1;
    public int gridColumn_NotificationMethod = 2;
    public int gridColumnEdit= 0;
    public int gridColumnDelete= 1;

    public SelenideElement notificationContacts = $(By.xpath(".//div[starts-with(@id,\"eventsetting\")]//span[ text()=\"Notification Contacts\"]"));


    /**
     * 功能按钮
     */

    public SelenideElement add = $(By.xpath(".//div[starts-with(@id,\"eventsetting\")]//span[ text()=\"Add\"]"));
    public SelenideElement delete = $(By.xpath(".//div[starts-with(@id,\"eventsetting\")]//span[ text()=\"Delete\"]"));

    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));



}
