package com.yeastar.swebtest.pobject.Me;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/7/21.
 */
public class Me {
    public SelenideElement me = $(By.name("mesetting"));

    public String closeMeSetting = "Ext.getCmp('control-panel').close()";

    public SelenideElement taskBar_User = $(By.xpath(".//*[@src='images/taskbar-user.png']"));
    public SelenideElement taskBar_User_Logout = $(By.xpath(".//span[text()='Logout']"));

    public SelenideElement taskBar_Main = $(By.xpath(".//*[@src='images/taskbar-main.png']"));
    public SelenideElement settingShortcut = $(By.id("control-panel-shortcut"));
    public SelenideElement cdrShortcut = $(By.id("cdr-record-shortcut"));
    public SelenideElement pbxmonitorShortcut = $(By.id("pbxmonitor-shortcut"));
    public SelenideElement resourcemonitorShortcut= $(By.id("resourcemonitor-shortcut"));
    public SelenideElement applicationCenterShortcut= $(By.id("application-center-shortcut"));
    public SelenideElement maintanceShortcut = $(By.id("maintance-shortcut"));
    public SelenideElement mesettingShortcut = $(By.id("mesetting-shortcut"));
    public SelenideElement autopShortcut = $(By.id("autop-shortcut"));
    public SelenideElement helpShortcut = $(By.id("guide-specification-shortcut"));
    /**
     * Extension Settings
     */
    public SelenideElement me_ExtensionSettings = $(By.xpath(".//div[starts-with(@id,'mesetting-')]//span[text()='Extension Settings']"));


    /**
     * Blacklist/Whitelist
     */
    public SelenideElement me_Blacklist_Whitelist = $(By.xpath(".//div[starts-with(@id,'mesetting-')]//span[text()='Blacklist/Whitelist']"));


    /**
     * CDR And Recording
     */
    public SelenideElement me_CDRandRecording = $(By.xpath("//span[contains(text(),'One Touch Recording')]"));

    /**
     * Voicemail
     */
    public SelenideElement me_Voicemail = $(By.xpath(".//div[starts-with(@id,'mesetting-')]//span[text()='Voicemail']"));

    /**
     * Password Settings
     */
    public SelenideElement me_PasswordSettings = $(By.xpath(".//div[starts-with(@id,'mesetting-')]//span[text()='Password Settings']"));


    /**
     * Route Permission
     */
    public SelenideElement me_RoutePetmission = $(By.xpath(".//div[starts-with(@id,'mesetting-')]//span[text()='Route Permission']"));


    public SelenideElement me_apply = $(By.xpath(".//a[starts-with(@class,'css_apply') and text()='Apply']"));

}
