package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.OutboundRoutes.Add_Outbound_Routes;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Outbound_Routes {

    public SelenideElement mt_AddAllToSelect = $(By.xpath(".//div[starts-with(@id,'st-or-trunkinfo-innerCt')]//a[@data-qtip='Add All to Selected']"));
    public SelenideElement mt_RemoveAllFromSelected = $(By.xpath(".//div[starts-with(@id,'st-or-trunkinfo-innerCt')]//a[@data-qtip='Remove All from Selected']"));
    public SelenideElement me_AddAllToSelect = $(By.xpath(".//div[starts-with(@id,'st-or-exteninfo-innerCt')]//a[@data-qtip='Add All to Selected']"));
    public SelenideElement me_RemoveAllFromSelected = $(By.xpath(".//div[starts-with(@id,'st-or-exteninfo-innerCt')]//a[@data-qtip='Remove All from Selected']"));

    public String list_Trunk = "st-or-trunkinfo";
    public String list_Extension = "st-or-exteninfo";
    public String list_TimeContion1 = "Ext.query('#st-or-timecondition-innerCt'+ ' tr td div')[0].id";

    public String combobox_Password = "st-or-pintype";
    public String combobox_Password_None = "none";
    public String combobox_Password_Pinset = "pinset";
    public String combobox_Password_Singlepin = "singlepin";

    public String combobox_PinsetPassword = "st-or-pinset";

    public SelenideElement name = $(By.id("st-or-name-inputEl"));
    public SelenideElement dialPattems = $(By.id("st-ir-adddialpattern"));


    public SelenideElement password_button = $(By.id("st-or-pintype-trigger-picker"));
    public SelenideElement rrmemoryHunt = $(By.id("st-or-adjusttrunk-displayEl"));
    public SelenideElement Workday = $(By.id("st-or-1-displayEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'outrouter-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'outrouter-edit-')]//span[text()='Cancel']"));

}
