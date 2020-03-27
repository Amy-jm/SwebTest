package com.yeastar.swebtest.pobject.Maintenance.Upgrade;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Upgrade {
    /**
     * Manual Upgrade
     */
    public SelenideElement resetConfigurationtoFactoryDefault = $(By.id("mt-ug-reset-displayEl"));

    public String type = "mt-ug-type";
    public SelenideElement file = $(By.id("mt-ug-file-inputEl"));
    public SelenideElement browse = $(By.id("mt-ug-file-button-fileInputEl"));
    public SelenideElement upload = $(By.id("mt-ug-upload-btnInnerEl"));
    public SelenideElement httpInput = $(By.id("mt-ug-httpurl-inputEl"));
    public SelenideElement httpDownload = $(By.id("mt-ug-download"));
    /**
     * Automatic Upgrade
     */
    public SelenideElement neverCheckFornewUpdates = $(By.id("mt-ug-mode-close-displayEl"));
    public SelenideElement letmeChooseWhetherToUpgrade= $(By.id("mt-ug-mode-autocheck-displayEl"));
    public SelenideElement check_week = $(By.id("mt-ug-autocheck-week-trigger-picker"));
    public SelenideElement check_hour = $(By.id("mt-ug-autocheck-hour-trigger-picker"));
    public SelenideElement automaticallyInstall = $(By.id("mt-ug-mode-autoinstall-displayEl"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
