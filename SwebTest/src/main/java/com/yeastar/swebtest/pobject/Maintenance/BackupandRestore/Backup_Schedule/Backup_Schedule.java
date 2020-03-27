package com.yeastar.swebtest.pobject.Maintenance.BackupandRestore.Backup_Schedule;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Backup_Schedule {

    public String SDTF = "tf/sd-1";
    public String  local  = "local-1";

    public SelenideElement enableScheduleBackup = $(By.id("mt-br-enable-displayEl"));
    /**
     * Schedule
     */
    public SelenideElement timeType = $(By.id("mt-br-backuptimetype-trigger-picker"));
    public SelenideElement timeTime = $(By.id("mt-br-backuptimetime-trigger-picker"));
    public String timeTime_id = "mt-br-backuptimetime";
    public SelenideElement locationType = $(By.id("mt-br-storagetype-trigger-picker"));
    public String locationType_id = "mt-br-storagetype";
    public SelenideElement backupRotation = $(By.id("mt-br-backupmaxnumber-inputEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'backupandrestore-schedule')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'backupandrestore-schedule')]//span[text()='Cancel']"));

}
