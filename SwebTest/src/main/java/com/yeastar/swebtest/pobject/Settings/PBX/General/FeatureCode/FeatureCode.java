package com.yeastar.swebtest.pobject.Settings.PBX.General.FeatureCode;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class FeatureCode {
    public SelenideElement featureCode = $(By.xpath(".//div[starts-with(@id,'general')]//span[ text()='Feature Code']"));

    public SelenideElement featureCodeDigitsTimeout = $(By.id("st-feature-digittimeout-inputEl"));

    /**
     * Reset to Defaults
     */
    public String resetToDefaults_check = "st-feature-enbresetfollowme";
    public SelenideElement resetToDefaults = $(By.id("st-feature-resetfollowme-inputEl"));
    /**
     * Recording
     */
    public String oneTouchRecord_check = "st-feature-enbmonitor";
    public SelenideElement oneTouchRecord = $(By.id("st-feature-touchmonitor-inputEl"));
    /**
     * Voicemail
     */
    public String checkVoicemail_check = "st-feature-enbmonitor";
    public SelenideElement checkVoicemail = $(By.id("st-feature-extensionvm-inputEl"));

    public String voicemailforExtension_check = "st-feature-enbvmtoextension";
    public SelenideElement voicemailforExtension = $(By.id("st-feature-vmtoextension-inputEl"));

    public String voicemailMainMenu_check = "st-feature-enbvmmenu";
    public SelenideElement voicemailMainMenu = $(By.id("st-feature-vmmenu-inputEl"));
    /**
     * Transfer
     */
    public String blindTransfer_check = "st-feature-enbblindtransfer";
    public SelenideElement blindTransfer = $(By.id("st-feature-blindtransfer-inputEl"));

    public String attendedTransfer_check = "st-feature-enbattendtransfer";
    public SelenideElement attendedTransfer = $(By.id("st-feature-attendtransfer-inputEl"));
    public SelenideElement attendedTransferTimeout = $(By.id("st-feature-attendtransfertimeout-inputEl"));

    /**
     * Call Pickup
     */
    public String callPickup_check = "st-feature-enbcallpickup";
    public SelenideElement callPickup = $(By.id("st-feature-callpickup-inputEl"));

    public String extensionPickup_check = "st-feature-enbextensionpickup";
    public SelenideElement extensionPickup = $(By.id("st-feature-extensionpickup-inputEl"));
    /**
     * Intercom
     */
    public String intercom_check = "st-feature-enbintercom";
    public SelenideElement intercom = $(By.id("st-feature-intercom-inputEl"));
    /**
     * Call Parking
     */
    public String callParking_check = "st-feature-enbcallpark";
    public SelenideElement callParking = $(By.id("st-feature-callpark-inputEl"));

    public String directedCallParking_check = "st-feature-enbdirectedparkcall";
    public SelenideElement directedCallParking = $(By.id("st-feature-directedparkcall-inputEl"));

    public SelenideElement parkingExtensionRange = $(By.id("st-feature-callparkpos-inputEl"));
    public SelenideElement parkingTimeout = $(By.id("st-feature-callparktime-inputEl"));

    public String resettoDefaults_check = "st-feature-enbresetfollowme";
    public SelenideElement resettoDefaults = $(By.id("st-feature-resetfollowme-inputEl"));

    /**
     * Call Forwarding
     */
    public String enableForwardAllCalls_check = "st-feature-enbenablealways";
    public SelenideElement enableForwardAllCalls = $(By.id("st-feature-enablealways-inputEl"));

    public String disableForwardAllCalls_check = "st-feature-enbdisablealways";
    public SelenideElement disableForwardAllCalls = $(By.id("st-feature-disablealways-inputEl"));

    public String enableForwardWhenBusy_check = "st-feature-enbenablebusy";
    public SelenideElement enableForwardWhenBusy = $(By.id("st-feature-enablebusy-inputEl"));

    public String disableForwardWhenBusy_check = "st-feature-enbdisablebusy";
    public SelenideElement disableForwardWhenBusy = $(By.id("st-feature-disablebusy-inputEl"));

    public String enableForwardNoAnswer_check = "st-feature-enbenablenoanswer";
    public SelenideElement enableForwardNoAnswer = $(By.id("st-feature-enablenoanswer-inputEl"));

    public String disableForwardNoAnswer_check = "st-feature-enbdisablenoanswer";
    public SelenideElement disableForwardNoAnswer = $(By.id("st-feature-disablenoanswer-inputEl"));

    /**
     * DND
     */
    public String enableDoNotDisturb_check = "st-feature-enbenablednd";
    public SelenideElement enableDoNotDisturb = $(By.id("st-feature-enablednd-inputEl"));

    public String disableDoNotDisturb_check = "st-feature-enbdisablednd";
    public SelenideElement disableDoNotDisturb = $(By.id("st-feature-disablednd-inputEl"));

    /**
     * Time Condition
     */
    public String timeConditionOverride_check = "st-feature-enbforcetime";
    public SelenideElement timeConditionOverride = $(By.id("st-feature-forcetime-inputEl"));
    public SelenideElement setExtensionPermission = $(By.xpath(".//div[starts-with(@id,'panel')]//a[text()='Set Extension Permission']"));
    /**
     * Set Extension Permission
     */
    public String list_setExtionsionPermission = "st-feature-extenspermission";
    public SelenideElement list_save =$(By.xpath(".//div[starts-with(@id,'featurecode-edit')]//span[text()='Save']"));
    public SelenideElement list_cancel =$(By.xpath(".//div[starts-with(@id,'featurecode-edit')]//span[text()='Cancel']"));
    /**
     * Call Monitor
     */
    public String listen_check = "st-feature-enbnormalspy";
    public SelenideElement listen = $(By.id("st-feature-normalspy-inputEl"));

    public String whisper_check = "st-feature-enbwhisperspy";
    public SelenideElement whisper = $(By.id("st-feature-whisperspy-inputEl"));

    public String barge_in_check = "st-feature-enbbargespy";
    public SelenideElement barge_in = $(By.id("st-feature-bargespy-inputEl"));

    /**
     * Multisite Interconnect
     */
    public String dialPrefix_check = "st-feature-multisite";
    public SelenideElement diaPrefix = $(By.id("st-feature-multisite-inputEl"));



    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'general')]//div[starts-with(@id,'featurecode')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'general')]//div[starts-with(@id,'featurecode')]//span[text()='Cancel']"));


}
