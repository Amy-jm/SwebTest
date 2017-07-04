package com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Extension.Add_Extension_Features;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Extension_Features {
    /**
     * 进入Features
     */
    public SelenideElement features = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Features\"]"));

    /**
     * Voicemail
     */
    public SelenideElement enableVoicemail = $(By.id("hasvoicemail-displayEl"));
    public SelenideElement sendVoicemailtoEmail = $(By.id("enablevmtoemail-displayEl"));
    public SelenideElement VoicemailAccessPIN = $(By.id("vmsecret-inputEl"));

    /**
     * Call Forwarding
     */
    public SelenideElement always = $(By.id("alwaysforward-displayEl"));
    public SelenideElement noAnswer = $(By.id("noanswerforward-displayEl"));
    public SelenideElement noAnswer_button = $(By.id("ntransferto-trigger-picker"));
    public SelenideElement whenBusy = $(By.id("busyforward-displayEl"));
    public SelenideElement whenBusy_button = $(By.id("btransferto-trigger-picker"));

    /**
     * Mobility Extension
     */
    public SelenideElement ringSimultaneously = $(By.id("ringsimultaneous-displayEl"));
    public SelenideElement mobilityExtension = $(By.id("mobileprefix-inputEl"));
    public SelenideElement setMobileNumber = $(By.xpath(".//span[starts-with(@class,\"cp-link\") and text()=\"Set Mobile Number\"]"));
    public SelenideElement enableMobilityExtension = $(By.id("enablemobile-displayEl"));

    /**
     * Monitor Settings
     */
    public SelenideElement allowBeingMonitored = $(By.id("allowbeingspy-displayEl"));
    public SelenideElement monitorMode_button = $(By.id("spymode-trigger-picker"));

    /**
     * Other Settings
     */
    public SelenideElement ringTimeout = $(By.id("ringtimeout-inputEl"));
    public SelenideElement maxCallDuration = $(By.id("maxduration-inputEl"));
    public SelenideElement DND = $(By.id("dnd-displayEl"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));


}
