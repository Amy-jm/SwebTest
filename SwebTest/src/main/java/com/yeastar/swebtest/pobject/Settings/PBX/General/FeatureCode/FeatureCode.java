package com.yeastar.swebtest.pobject.Settings.PBX.General.FeatureCode;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class FeatureCode {
    public SelenideElement featureCode = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Feature Code\"]"));

    public SelenideElement featureCodeDigitsTimeout = $(By.id("st-feature-digittimeout-inputEl"));
    /**
     * Recording
     */
    public SelenideElement oneTouchRecord_check = $(By.id("st-feature-enbmonitor-displayEl"));
    public SelenideElement oneTouchRecord = $(By.id("st-feature-touchmonitor-inputEl"));
    /**
     * Voicemail
     */
    public SelenideElement checkVoicemail_check = $(By.id("st-feature-enbmonitor-displayEl"));
    public SelenideElement checkVoicemail = $(By.id("st-feature-extensionvm-inputEl"));

    public SelenideElement voicemailforExtension_check = $(By.id("st-feature-enbvmtoextension-displayEl"));
    public SelenideElement voicemailforExtension = $(By.id("st-feature-vmtoextension-inputEl"));

    public SelenideElement voicemailMainMenu_check = $(By.id("st-feature-enbvmmenu-displayEl"));
    public SelenideElement voicemailMainMenu = $(By.id("st-feature-vmmenu-inputEl"));
    /**
     * Transfer
     */
    public SelenideElement blindTransfer_check = $(By.id("st-feature-enbblindtransfer-displayEl"));
    public SelenideElement blindTransfer = $(By.id("st-feature-blindtransfer-inputEl"));

    public SelenideElement attendedTransfer_check = $(By.id("st-feature-enbattendtransfer-displayEl"));
    public SelenideElement attendedTransfer = $(By.id("st-feature-attendtransfer-inputEl"));
    public SelenideElement attendedTransferTimeout = $(By.id("st-feature-attendtransfertimeout-inputEl"));

    /**
     * Call Pickup
     */
    public SelenideElement callPickup_check = $(By.id("st-feature-enbcallpickup-displayEl"));
    public SelenideElement callPickup = $(By.id("st-feature-callpickup-inputEl"));

    public SelenideElement extensionPickup_check = $(By.id("st-feature-enbextensionpickup-displayEl"));
    public SelenideElement extensionPickup = $(By.id("st-feature-extensionpickup-inputEl"));
    /**
     * Intercom
     */
    public SelenideElement intercom_check = $(By.id("st-feature-enbintercom-displayEl"));
    public SelenideElement intercom = $(By.id("st-feature-intercom-inputEl"));
    /**
     * Call Parking
     */
    public SelenideElement callParking_check = $(By.id("st-feature-enbcallpark-displayEl"));
    public SelenideElement callParking = $(By.id("st-feature-callpark-inputEl"));

    public SelenideElement directedCallParking_check = $(By.id("st-feature-enbdirectedparkcall-displayEl"));
    public SelenideElement directedCallParking = $(By.id("st-feature-directedparkcall-inputEl"));

    public SelenideElement parkingExtensionRange = $(By.id("st-feature-callparkpos-inputEl"));
    public SelenideElement parkingTimeout = $(By.id("st-feature-callparktime-inputEl"));

    public SelenideElement resettoDefaults_check = $(By.id("st-feature-enbresetfollowme-displayEl"));
    public SelenideElement resettoDefaults = $(By.id("st-feature-resetfollowme-inputEl"));

    /**
     * Call Forwarding
     */
    public SelenideElement enableForwardAllCalls_check = $(By.id("st-feature-enbenablealways-displayEl"));
    public SelenideElement enableForwardAllCalls = $(By.id("st-feature-enablealways-inputEl"));

    public SelenideElement disableForwardAllCalls_check = $(By.id("st-feature-enbdisablealways-displayEl"));
    public SelenideElement disableForwardAllCalls = $(By.id("st-feature-disablealways-inputEl"));

    public SelenideElement enableForwardWhenBusy_check = $(By.id("st-feature-enbenablebusy-displayEl"));
    public SelenideElement enableForwardWhenBusy = $(By.id("st-feature-enablebusy-inputEl"));

    public SelenideElement disableForwardWhenBusy_check = $(By.id("st-feature-enbdisablebusy-displayEl"));
    public SelenideElement disableForwardWhenBusy = $(By.id("st-feature-disablebusy-inputEl"));

    public SelenideElement enableForwardNoAnswer_check = $(By.id("st-feature-enbenablenoanswer-displayEl"));
    public SelenideElement enableForwardNoAnswer = $(By.id("st-feature-enablenoanswer-inputEl"));

    public SelenideElement disableForwardNoAnswer_check = $(By.id("st-feature-enbdisablenoanswer-displayEl"));
    public SelenideElement disableForwardNoAnswer = $(By.id("st-feature-disablenoanswer-inputEl"));

    /**
     * DND
     */
    public SelenideElement enableDoNotDisturb_check = $(By.id("st-feature-enbenablednd-displayEl"));
    public SelenideElement enableDoNotDisturb = $(By.id("st-feature-enablednd-inputEl"));

    public SelenideElement disableDoNotDisturb_check = $(By.id("st-feature-enbdisablednd-displayEl"));
    public SelenideElement disableDoNotDisturb = $(By.id("st-feature-disablednd-inputEl"));

    /**
     * Time Condition
     */
    public SelenideElement timeConditionOverride_check = $(By.id("st-feature-enbforcetime-displayEl"));
    public SelenideElement timeConditionOverride = $(By.id("st-feature-forcetime-inputEl"));

    public SelenideElement setExtensionPermission = $(By.xpath(".//span[starts-with(@class,\"cp-link-before\") and text()=\"Set Extension Permission\"]"));
    /**
     * Call Monitor
     */
    public SelenideElement listen_check = $(By.id("st-feature-enbnormalspy-displayEl"));
    public SelenideElement listen = $(By.id("st-feature-normalspy-inputEl"));

    public SelenideElement whisper_check = $(By.id("st-feature-enbwhisperspy-displayEl"));
    public SelenideElement whisper = $(By.id("st-feature-whisperspy-inputEl"));

    public SelenideElement barge_in_check = $(By.id("st-feature-enbbargespy-displayEl"));
    public SelenideElement barge_in = $(By.id("st-feature-bargespy-inputEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'featurecode-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'featurecode-')]//span[text()='Cancel']"));

}
