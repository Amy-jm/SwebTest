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
    public String callLog_Checkbox = "mt-br-cdr";

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'backupandrestore')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'backupandrestore')]//span[text()='Cancel']"));



}
