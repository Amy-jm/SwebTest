package com.yeastar.swebtest.pobject.Maintenance.BackupandRestore.Backup_Schedule;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Backup_Schedule {
    public SelenideElement enableScheduleBackup = $(By.id("mt-br-enable-displayEl"));
    /**
     * Schedule
     */
    public SelenideElement timeType = $(By.id("mt-br-backuptimetype-trigger-picker"));
    public SelenideElement timeTime = $(By.id("mt-br-backuptimetime-trigger-picker"));
    public SelenideElement locationType = $(By.id("mt-br-storagetype-trigger-picker"));
    public SelenideElement backupRotation = $(By.id("mt-br-backupmaxnumber-inputEl"));


    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
