package com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Add_VoIP_Trunk.Add_VoIP_Trunk_DOD;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_VoIP_Trunk_DOD {
    public SelenideElement DOD = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"DOD\"]"));

    /**
     * 功能按钮
     */

    public SelenideElement add = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Add\"]"));
    public SelenideElement delete = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Delete\"]"));
    public SelenideElement Import = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Import\"]"));
    public SelenideElement export = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Export\"]"));

    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));

//    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
//    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));
    public SelenideElement save = $(By.xpath(" .//div[starts-with(@id,'editvoip')]//span[ text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'editvoip')]//span[text()='Cancel']"));
}
