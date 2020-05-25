package com.yeastar.pageObject.pSeries;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public interface IButton {
    String buttonLocationXpath = "//button//span[contains(text(),'%s')]";
    SelenideElement addBtn = $(By.xpath(String.format(buttonLocationXpath,"Add")));
    SelenideElement importBtn = $(By.xpath(String.format(buttonLocationXpath,"Import')]")));
    SelenideElement exportBtn = $(By.xpath(String.format(buttonLocationXpath,"Export')]")));
    SelenideElement deleteBtn = $(By.xpath(String.format(buttonLocationXpath,"Delete')]")));
    SelenideElement linkusServerBtn = $(By.xpath(String.format(buttonLocationXpath,"LinkusServer')]")));
    SelenideElement copyRoleBtn = $(By.xpath(String.format(buttonLocationXpath,"Copy Role')]")));
    SelenideElement saveBtn = $(By.xpath(String.format(buttonLocationXpath,"Save')]")));
    SelenideElement cancelBtn = $(By.xpath(String.format(buttonLocationXpath,"Cancel')]")));
    SelenideElement prefixBtn = $(By.xpath(String.format(buttonLocationXpath,"Prefix')]")));
    SelenideElement checkForNewFirmwareBtn = $(By.xpath(String.format(buttonLocationXpath,"Check for new Firmware')]")));
    SelenideElement browserBtn = $(By.xpath(String.format(buttonLocationXpath,"Browser')]")));
    SelenideElement upgradeBtn = $(By.xpath(String.format(buttonLocationXpath,"Upgrade')]")));
    SelenideElement backUpBtn = $(By.xpath(String.format(buttonLocationXpath,"Backup')]")));
    SelenideElement uploadBtn = $(By.xpath(String.format(buttonLocationXpath,"Upload')]")));
    SelenideElement backScheduleBtn = $(By.xpath(String.format(buttonLocationXpath,"Backup Schedule')]")));
    SelenideElement rebootNowBtn = $(By.xpath(String.format(buttonLocationXpath,"Reboot Now')]")));
    SelenideElement factoryResetBtn = $(By.xpath(String.format(buttonLocationXpath,"Factory Reset')]")));



    SelenideElement downloadBtn = $(By.xpath(String.format(buttonLocationXpath,"Download')]")));
    SelenideElement starttn = $(By.xpath(String.format(buttonLocationXpath,"Start')]")));
    SelenideElement stopBtn = $(By.xpath(String.format(buttonLocationXpath,"Strop')]")));
    SelenideElement downloadCDRBtn = $(By.xpath(String.format(buttonLocationXpath,"Download CDR')]")));
    SelenideElement refreshBtn = $(By.xpath(String.format(buttonLocationXpath,"Refresh')]")));
    SelenideElement downloadRecordingsBtn = $(By.xpath(String.format(buttonLocationXpath,"Download Recordings')]")));




}
