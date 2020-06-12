package com.yeastar.page.pseries;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public interface IButton {
    String buttonLocationXpath = "//button//span[contains(text(),'%s')]/..";
    SelenideElement addBtn = $(By.xpath(String.format(buttonLocationXpath,"Add")));
    SelenideElement applyBtn = $(By.xpath(String.format(buttonLocationXpath,"Apply")));
    SelenideElement applyLoadingBtn = $(By.xpath(String.format(buttonLocationXpath,"Loading")));
    SelenideElement applyLoadedBtn = $(By.xpath(String.format(buttonLocationXpath,"Loaded")));

    SelenideElement importBtn = $(By.xpath(String.format(buttonLocationXpath,"Import")));
    SelenideElement exportBtn = $(By.xpath(String.format(buttonLocationXpath,"Export")));
    public static SelenideElement deleteBtn = $(By.xpath(String.format(buttonLocationXpath,"Delete")));
    SelenideElement linkusServerBtn = $(By.xpath(String.format(buttonLocationXpath,"LinkusServer")));
    SelenideElement copyRoleBtn = $(By.xpath(String.format(buttonLocationXpath,"Copy Role")));
    SelenideElement saveBtn = $(By.xpath(String.format(buttonLocationXpath,"Save")));
    SelenideElement cancelBtn = $(By.xpath(String.format(buttonLocationXpath,"Cancel")));
    SelenideElement prefixBtn = $(By.xpath(String.format(buttonLocationXpath,"Prefix")));
    SelenideElement checkForNewFirmwareBtn = $(By.xpath(String.format(buttonLocationXpath,"Check for new Firmware")));
    SelenideElement browserBtn = $(By.xpath(String.format(buttonLocationXpath,"Browser")));
    SelenideElement upgradeBtn = $(By.xpath(String.format(buttonLocationXpath,"Upgrade")));
    SelenideElement backUpBtn = $(By.xpath(String.format(buttonLocationXpath,"Backup")));
    SelenideElement uploadBtn = $(By.xpath(String.format(buttonLocationXpath,"Upload")));
    SelenideElement backScheduleBtn = $(By.xpath(String.format(buttonLocationXpath,"Backup Schedule")));
    SelenideElement rebootNowBtn = $(By.xpath(String.format(buttonLocationXpath,"Reboot Now")));
    SelenideElement factoryResetBtn = $(By.xpath(String.format(buttonLocationXpath,"Factory Reset")));



    SelenideElement downloadBtn = $(By.xpath(String.format(buttonLocationXpath,"Download")));
    SelenideElement startBtn = $(By.xpath(String.format(buttonLocationXpath,"Start")));
    SelenideElement stopBtn = $(By.xpath(String.format(buttonLocationXpath,"Strop")));
    SelenideElement downloadCDRBtn = $(By.xpath(String.format(buttonLocationXpath,"Download CDR")));
    SelenideElement refreshBtn = $(By.xpath(String.format(buttonLocationXpath,"Refresh")));
    SelenideElement downloadRecordingsBtn = $(By.xpath(String.format(buttonLocationXpath,"Download Recordings")));


    //alert
    SelenideElement OKAlertBtn = $(By.xpath("//button/span[contains(text(),'OK')]/.."));
    SelenideElement CancelAlertBtn = $(By.xpath("//button/span[contains(text(),'Cancel')]/.."));
    SelenideElement ConfrimAlertBtn = $(By.xpath("//button/span[contains(text(),'Confrim')]/.."));


    //表格第一行删除图标
    SelenideElement deleteImageForTableFirstTr = $(By.xpath("//table/tbody/tr[1]//i[contains(@class,'delete')]"));



}