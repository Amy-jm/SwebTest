package com.yeastar.swebtest.pobject.Settings.System.UserPermission.Grant_Privilege_CDRandRecordings;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Grant_Privilege_CDRandRecordings {
    public SelenideElement grant_Privilege_CDRandRecordings = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"CDR and Recordings\"]"));

    public SelenideElement user = $(By.id("st-up-id-inputEl"));
    public SelenideElement setPrivilegeAs = $(By.id("st-up-type-trigger-picker"));

    public SelenideElement CDRandRecordings = $(By.id("st-up-cdrandrecord-boxLabelEl"));
    public SelenideElement downloadCDR = $(By.id("cdrpermit-downloadcdr-boxLabelEl"));
    public SelenideElement deleteCDR = $(By.id("cdrpermit-deletecdr-boxLabelEl"));
    public SelenideElement playRecordings = $(By.id("recordpermit-playrecord-boxLabelEl"));
    public SelenideElement downloadRecordings = $(By.id("recordpermit-downloadrecord-boxLabelEl"));
    public SelenideElement deleteRecordings = $(By.id("recordpermit-deleterecord-boxLabelEl"));
    public SelenideElement allExtensions = $(By.xpath(".//label[starts-with(@class,\"x-form-cb-label x-form-cb-label-default x-form-cb-label-after  \") and text()=\"All Extensions\"]"));
    public SelenideElement selectedExtensions = $(By.xpath(".//label[starts-with(@class,\"x-form-cb-label x-form-cb-label-default x-form-cb-label-after  \") and text()=\"Selected Extensions\"]"));


    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));




}
