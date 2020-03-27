package com.yeastar.swebtest.pobject.Maintenance.BackupandRestore.Upload_a_Backup_File;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Upload_a_Backup_File {
    public SelenideElement file = $(By.id("mt-br-choosefile-inputEl"));
    public SelenideElement browse = $(By.id("mt-br-choosefile-button-fileInputEl"));
    public SelenideElement memo = $(By.id("mt-br-memo-inputEl"));
    public SelenideElement upload = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Upload\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
