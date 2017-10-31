package com.yeastar.swebtest.pobject.Settings.PBX.MultisiteInterconnect.Headquerter.Headquarter_Basic;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/10/10.
 */
public class Headquerter_Basic {

    public SelenideElement basic = $(By.xpath(".//div[starts-with(@id,'multisite')]//span[text()=\"Basic\"]"));

    public String grid = "Ext.getCmp('control-panel').down('multisite')";
    public int gridcolumn_Check = 0;
    public int gridcolumn_Status = 1;
    public int gridcolumn_BranchID = 2;
    public int gridcolumn_Role = 3;
    public int gridcolumn_Name = 4;
    public int gridcolumn_Account = 4;
    public int gridcolumn_IpAddr = 4;
    public int gridEdit = 0;
    public int gridDelete = 1;

    public SelenideElement add = $(By.xpath(".//div[starts-with(@id,'multisitembasic')]//a[starts-with(@id,'button')]//span[text()=\"Add\"]"));
    public SelenideElement delete = $(By.xpath(".//div[starts-with(@id,'multisitembasic')]//a[starts-with(@id,'button')]//span[text()=\"Delete\"]"));
    public SelenideElement WithdrawFromTheNetworking = $(By.xpath(".//div[starts-with(@id,'multisitembasic')]//span[text()=\"Withdraw from the Networking\"]"));


    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));

    /**
     * Withdraw from the Networking  YES NO
     */
    public SelenideElement withdraw_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement withdraw_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));


}
