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
    public SelenideElement features = $(By.xpath(".//div[starts-with(@id,'extension-edit')]//span[ text()='Features']"));

    /**
     * Voicemail
     */
    public String enableVoicemail = "hasvoicemail-displayEl";
    public String sendVoicemailtoEmail = "enablevmtoemail-displayEl";
    public SelenideElement VoicemailAccessPIN = $(By.id("vmsecret-inputEl"));

    /**
     * Call Forwarding
     */
    public String always = "alwaysforward";
    public String noAnswer = "noanswerforward";
    public String noAnswer_button = "ntransferto";
    public String whenBusy = "busyforward";
    public String whenBusy_button = "btransferto";

    /**
     * Mobility Extension
     */
    public String ringSimultaneously ="ringsimultaneous";
    public SelenideElement mobilityExtension = $(By.id("mobileprefix-inputEl"));
    public SelenideElement setMobileNumber = $(By.xpath(".//div[starts-with(@id,'extension-features')]//a[text()='Set Mobile Number']"));
    public String enableMobilityExtension = "enablemobile";

    /**
     * Monitor Settings
     */
    public String allowBeingMonitored = "allowbeingspy";
    public String monitorMode_button = "spymode";

    /**
     * Other Settings
     */
    public String ringTimeout = "ringtimeout-inputEl";
    public String maxCallDuration = "maxduration-inputEl";
    public String DND = "dnd-displayEl";

    public SelenideElement save = $(By.xpath(" .//div[starts-with(@id,'extension-edit')]//span[ text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'extension-edit')]//span[text()='Cancel']"));


}
