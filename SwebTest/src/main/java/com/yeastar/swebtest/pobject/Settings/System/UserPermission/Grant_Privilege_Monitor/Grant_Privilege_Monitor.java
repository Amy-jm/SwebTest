package com.yeastar.swebtest.pobject.Settings.System.UserPermission.Grant_Privilege_Monitor;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Grant_Privilege_Monitor {
    public String privilegeAs_Administrator = "admin";
    public String privilegeAs_Custom = "custom";

    public String setPrivilegeAs_id = "st-up-type";
    public String user_id = "st-up-id";

    public SelenideElement grant_Privilege_Monitor = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Monitor\"]"));

    public SelenideElement user = $(By.id("st-up-id-inputEl"));
    public SelenideElement setPrivilegeAs = $(By.id("st-up-type-trigger-picker"));

    public SelenideElement all = $(By.xpath(".//label[starts-with(@class,\"x-form-cb-label x-form-cb-label-default x-form-cb-label-after  \") and text()=\"All\"]"));

    public SelenideElement PBXMonitor = $(By.id("st-up-pbxmonitor-boxLabelEl"));

    public SelenideElement resourceMonitor = $(By.id("st-up-resourcemonitor-boxLabelEl"));

    public SelenideElement information = $(By.id("st-up-information-boxLabelEl"));
    public SelenideElement network = $(By.id("st-up-networkstatus-boxLabelEl"));
    public SelenideElement performance = $(By.id("st-up-performance-boxLabelEl"));
    public SelenideElement storageUsage = $(By.id("st-up-storageusage-boxLabelEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'userpermission')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'userpermission')]//span[text()='Cancel']"));







}
