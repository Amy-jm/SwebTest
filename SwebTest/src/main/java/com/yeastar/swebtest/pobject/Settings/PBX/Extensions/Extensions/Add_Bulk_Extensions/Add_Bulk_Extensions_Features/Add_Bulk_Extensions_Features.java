package com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Bulk_Extensions.Add_Bulk_Extensions_Features;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Bulk_Extensions_Features {
    /**
     * 进入Features
     */
    public SelenideElement features = $(By.xpath(".//div[starts-with(@id,\"extension-edit\")]//span[ text()=\"Features\"]"));

    /**
     * Voicemail
     */
    public String enableVoicemail = "hasvoicemail";
    public String sengVoicemailtoEmail = ("enablevmtoemail");
    public SelenideElement useExtensionasPIN = $(By.xpath(".//div[starts-with(@id,\"extension-features\")]//div[starts-with(@id,\"vmsecrettype\")]//label[ text()=\"Use Extension as PIN\"]"));
    public SelenideElement useasPIN = $(By.xpath(".//div[starts-with(@id,\"extension-features\")]//div[starts-with(@id,\"vmsecrettype\")]//label[ text()=\"Use\"]"));
    public SelenideElement voicemailAccessPIN = $(By.id("fixedvmsecret-inputEl"));

    /**
     * Monitor Settings
     */
    public String allowBeingMonitored = ("allowbeingspy");
    public String monitorMode_button = ("spymode");

    /**
     * Other Settings
     */

    public String ringTimeout = ("ringtimeout-inputEl");
    public String maxCallDuration =("maxduration-inputEl");
    public String DND = ("dnd-displayEl");

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,\"extension-edit\")]//span[ text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,\"extension-edit\")]//span[ text()=\"Cancel\"]"));


}
