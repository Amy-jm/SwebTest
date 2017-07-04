package com.yeastar.swebtest.pobject.Maintenance.BackupandRestore.Create_New_Backup_File;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;


/**
 * Created by Yeastar on 2017/6/30.
 */
public class Create_New_Backup_File {
    public SelenideElement fileName = $(By.id("mt-br-file-inputEl"));
    public SelenideElement memo = $(By.id("mt-br-memo-inputEl"));
    public SelenideElement locationType = $(By.id("mt-br-storagetype-trigger-picker"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));



}
