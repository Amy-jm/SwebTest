package com.yeastar.swebtest.pobject.Settings.System.UserPermission.Grant_Privilege_Application;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Grant_Privilege_Application {
    public SelenideElement grant_Privilege_Application = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Application\"]"));

    public SelenideElement user = $(By.id("st-up-id-inputEl"));
    public SelenideElement setPrivilegeAs = $(By.id("st-up-type-trigger-picker"));

    public SelenideElement all = $(By.xpath(".//label[starts-with(@class,\"x-form-cb-label x-form-cb-label-default x-form-cb-label-after  \") and text()=\"All\"]"));

    public SelenideElement autoProvisioning = $(By.id("st-up-autop-boxLabelEl"));

}
