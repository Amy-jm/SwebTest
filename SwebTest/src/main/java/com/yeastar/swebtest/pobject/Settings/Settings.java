package com.yeastar.swebtest.pobject.Settings;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by GaGa on 2017-05-19.
 */
public class Settings {
    /**
     * Panel PBX
     */
    public SelenideElement extensions_panel = $(By.id("menuextensions"));
    public SelenideElement trunks_panel = $(By.id("menutrunks"));
    public SelenideElement callControl_panel = $(By.id("menucallcontrol"));
    public SelenideElement callFeatures_panel = $(By.id("menucallfeatures"));
    public SelenideElement voicePrompts_panel = $(By.id("menuprompt"));
    public SelenideElement general_panel = $(By.id("menugeneral"));
    public SelenideElement recording_panel = $(By.id("menurecording"));
    public SelenideElement emergencyNumber_panel = $(By.id("menuemergency"));

    /**
     * Panel System
     */
    public SelenideElement network_panel = $(By.id("menunetwork"));
    public SelenideElement security_panel = $(By.id("menusecurity"));
    public SelenideElement userPermission_panel = $(By.id("menuuserpermission"));
    public SelenideElement menumultisite_panel = $(By.id("menumultisite"));
    public SelenideElement dateTime_panel = $(By.id("menutime"));
    public SelenideElement email_panel = $(By.id("menuemail"));
    public SelenideElement storage_panel = $(By.id("menustorage"));
    public SelenideElement hotStandby_panel = $(By.id("menuhotstandby"));

    /**
     * Panel EventCenter
     */
    public SelenideElement eventSettings_panel = $(By.id("menueventseting"));
    public SelenideElement eventLog_panel = $(By.id("menueventlog"));

    /**
     * TreePBX，PBX如需使用需双击
     */
    public SelenideElement PBX_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"PBX\"]"));
    public SelenideElement extensions_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()='Extensions']"));
    public SelenideElement trunks_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()='Trunks']"));
    public SelenideElement multisiteInterconnect_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()='Multisite Interconnect']"));
    public SelenideElement callControl_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"Call Control\"]"));
    public SelenideElement callFeatures_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"Call Features\"]"));
    public SelenideElement voicePrompts_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"Voice Prompts\"]"));
    public SelenideElement general_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"General\"]"));
    public SelenideElement recording_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"Recording\"]"));
    public SelenideElement emergencyNumber_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"Emergency Number\"]"));

    /**
     * TreeSystem，System如需使用需双击
     */
    public SelenideElement system_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"System\"]"));
    public SelenideElement network_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"Network\"]"));
    public SelenideElement security_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"Security\"]"));
    public SelenideElement userPermission_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"User Permission\"]"));
    public SelenideElement dateTime_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"Date & Time\"]"));
    public SelenideElement email_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"Email\"]"));
    public SelenideElement storage_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"Storage\"]"));
    public SelenideElement hotStandby_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"Hot Standby\"]"));

    /**
     * TreeEventCenter，EventCenter如需使用需双击
     */
    public SelenideElement eventCenter_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"Event Center\"]"));
    public SelenideElement eventSettings_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"Event Settings\"]"));
    public SelenideElement eventLog_tree = $(By.xpath(".//div[starts-with(@id,\"control-panel\")]//div[starts-with(@id,\"set-lefttree\")]//span[text()=\"Event Log\"]"));

    /**
     * 最小化，最大化，关闭
     */
    public SelenideElement minimize = $(By.xpath(".//div[starts-with(@class,\"x-tool-img x-tool-minimize\")]"));
    public SelenideElement maximize = $(By.xpath(".//div[starts-with(@class,\"x-tool-img x-tool-maximize\")]"));
    public SelenideElement close = $(By.xpath(".//div[starts-with(@class,\"x-tool-img x-tool-close\")]"));
}
