package com.yeastar.swebtest.pobject.Settings.System.UserPermission.Grant_Privilege_Others;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Grant_Privilege_Others {
    public String privilegeAs_Administrator = "admin";
    public String privilegeAs_Custom = "custom";

    public String setPrivilegeAs_id = "st-up-type";
    public String user_id = "st-up-id";
    public SelenideElement grant_Privilege_Others = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Others\"]"));

    public SelenideElement user = $(By.id("st-up-id-inputEl"));
    public SelenideElement setPrivilegeAs = $(By.id("st-up-type-trigger-picker"));

    public SelenideElement all = $(By.xpath(".//label[starts-with(@class,\"x-form-cb-label x-form-cb-label-default x-form-cb-label-after  \") and text()=\"All\"]"));

    public SelenideElement appCenter = $(By.id("st-up-applicationcenter-boxLabelEl"));

    public SelenideElement maintenance = $(By.id("st-up-maintenance-boxLabelEl"));

    public SelenideElement upgrade = $(By.id("st-up-upgrade-boxLabelEl"));
    public SelenideElement backupandRestore = $(By.id("st-up-backupandrestore-boxLabelEl"));
    public SelenideElement reboot = $(By.id("st-up-rebootandreset-boxLabelEl"));
    public SelenideElement systemLog = $(By.id("st-up-syslog-boxLabelEl"));
    public SelenideElement operationLog = $(By.id("st-up-operatelog-boxLabelEl"));
    public SelenideElement troubleshooting = $(By.id("st-up-systemtools-boxLabelEl"));
    public SelenideElement reset = $(By.id("st-up-webreset-boxLabelEl"));


    public SelenideElement help = $(By.id("st-up-help-boxLabelEl"));


//    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
//    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));
public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'userpermission')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'userpermission')]//span[text()='Cancel']"));
















}
