package com.yeastar.swebtest.pobject.Settings.EventCenter.EventSettings.NotificationContacts;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class NotificationContacts {
    public SelenideElement notificationContacts = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Notification Contacts\"]"));


    /**
     * 功能按钮
     */

    public SelenideElement add = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Add\"]"));
    public SelenideElement delete = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Delete\"]"));

    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));



}
