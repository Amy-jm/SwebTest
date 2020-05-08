package com.yeastar.swebtest.pobject;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.*;

/**
 * Created by GaGa on 2017-03-29.
 */
public class PageDeskTop {

    public SelenideElement taskBar_User = $(By.xpath(".//*[@src='images/taskbar-user.png']"));
    public SelenideElement taskBar_User_Logout = $(By.xpath(".//span[text()='Logout']"));
    public SelenideElement messageBox_Yes = $(By.xpath(".//span[text()=\"Yes\" and @data-ref=\"btnInnerEl\"]"));
    public SelenideElement messageBox_No = $(By.xpath(".//span[text()=\"No\" and @data-ref=\"btnInnerEl\"]"));



    public SelenideElement apply = $(By.xpath(".//a[starts-with(@class,'css_apply') and text()='Apply']"));
	public SelenideElement apply_new = $(By.id("taskbarapply"));
    public SelenideElement boxSettings = $(By.xpath(".//span[starts-with(@class,'x-btn-inner x-btn-inner-default-toolbar-small') and text()='Settings']"));
    public SelenideElement boxCDRandRecordings = $(By.xpath(".//span[starts-with(@class,'x-btn-inner x-btn-inner-default-toolbar-small') and text()='CDR and Recordings']"));
    public SelenideElement boxMaintenance = $(By.xpath(".//span[starts-with(@class,'x-btn-inner x-btn-inner-default-toolbar-small') and text()='Maintenance']"));

    public SelenideElement settings = $(By.name("control-panel"));
    public SelenideElement CDRandRecording = $(By.name("cdr-record"));
    public SelenideElement maintenance = $(By.name("maintance"));

    public SelenideElement taskBar_Main = $(By.xpath(".//*[@src='images/taskbar-main.png']"));
    public SelenideElement settingShortcut = $(By.id("control-panel-shortcut"));
    public SelenideElement CDRandRecordShortcut = $(By.id("cdr-record-shortcut"));
    public SelenideElement pbxmonitorShortcut = $(By.id("pbxmonitor-shortcut"));
    public SelenideElement resourcemonitorShortcut=$(By.id("resourcemonitor-shortcut"));
    public SelenideElement applicationShortcut = $(By.id("application-center-shortcut"));
    public SelenideElement maintanceShortcut = $(By.id("maintance-shortcut"));
    public SelenideElement autopShortcut = $(By.id("autop-shortcut"));
    public SelenideElement guideShortcut = $(By.id("guide-specification-shortcut"));
    public SelenideElement linkusShortcut = $(By.id("linkus-shortcut"));
    public SelenideElement call_Control = $(By.id("menucallcontrol"));

    //reboot
    public SelenideElement reboot_Yes = $(By.xpath(".//div[starts-with(@id,'notifaction')]//span[text()=\"Yes\"]"));
    public SelenideElement reboot_No = $(By.xpath(".//div[starts-with(@id,'notifaction')]//span[text()=\"No\"]"));

    //login out
    public SelenideElement loginout_OK = $(By.xpath(".//div[starts-with(@id,'messagebox')]//span[text()=\"OK\"]"));

    //Privacy Policy Agreement
    public String pp_agreement_checkBox = "pp-nevernotify";
    public SelenideElement pp_comfirm = $(By.id("pp-save"));
    //restore notification
    public SelenideElement restore_alert_yes = $(By.xpath("//span[text()=\"Yes\"]"));

}
