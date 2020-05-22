package com.yeastar.pageObject.pSeries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

@Log4j2
public class HomePage {

    /** 左侧菜单 -- Dashboard **/
    public SelenideElement left_menu_dashboard = $(By.linkText("Dashboard"));
    /** 左侧菜单 -- Extension/Trunk **/
    public SelenideElement left_menu_first_level_extension_trunk = $(By.xpath("//span[contains(text(),'Extension/Trunk')]"));
    public SelenideElement extension_trunk_tree_extensions = $(By.linkText("Extensions"));
    public SelenideElement extension_trunk_tree_extension_group = $(By.linkText("Extension Group"));
    public SelenideElement extension_trunk_tree_trunks = $(By.linkText("Trunks"));
    public SelenideElement extension_trunk_tree_management = $(By.linkText("Role Management"));

    /** 左侧菜单 -- Call Control **/
    public SelenideElement left_menu_first_level_call_control = $(By.xpath("//span[contains(text(),'Call Control')]"));
    public SelenideElement call_control_tree_inbound_routes = $(By.linkText("Inbound Routes"));
    public SelenideElement call_control_tree_outbound_routes = $(By.linkText("Outbound Routes"));
    public SelenideElement call_control_tree_office_time_and_holidays = $(By.linkText("Office Time and Holidays"));
    public SelenideElement call_control_tree_emergency_number = $(By.linkText("Emergency Number"));

    /** 左侧菜单 -- Call Feature **/
    public SelenideElement left_menu_first_level_call_feature = $(By.xpath("//span[contains(text(),'Call Feature')]"));
    public SelenideElement call_feature_tree_voicemail = $(By.linkText("Voicemail"));
    public SelenideElement call_feature_tree_feature_codes = $(By.linkText("Feature Codes"));
    public SelenideElement call_feature_tree_ivr = $(By.linkText("IVR"));
    public SelenideElement call_feature_tree_ring_group = $(By.linkText("Ring Group"));
    public SelenideElement call_feature_tree_queue = $(By.linkText("Queue"));
    public SelenideElement call_feature_tree_conference = $(By.linkText("Conference"));
    public SelenideElement call_feature_tree_speed_dial = $(By.linkText("Speed Dial"));
    public SelenideElement call_feature_tree_recording = $(By.linkText("Recording"));


    /** 左侧菜单 -- PBX Settings **/
    public SelenideElement left_menu_first_level_pbx_settings = $(By.xpath("//span[contains(text(),'PBX Settings')]"));
    public SelenideElement pbx_settings_tree_preferences = $(By.linkText("Preferences"));
    public SelenideElement pbx_settings_tree_voice_prompt = $(By.linkText("Voice Prompt"));
    public SelenideElement pbx_settings_tree_sip_settings = $(By.linkText("SIP Settings"));
    public SelenideElement pbx_settings_tree_jitter_buffer = $(By.linkText("Jitter Buffer"));

    /** 左侧菜单 -- System **/
    public SelenideElement left_menu_first_level_system = $(By.xpath("//span[contains(text(),'System')]"));
    public SelenideElement system_tree_network = $(By.linkText("Network"));
    public SelenideElement system_tree_date_and_time = $(By.linkText("Date and Time"));
    public SelenideElement system_tree_email = $(By.linkText("Email"));
    public SelenideElement system_tree_storage = $(By.linkText("Storage"));
    public SelenideElement system_tree_event_notification = $(By.linkText("Event Notification"));
    public SelenideElement system_tree_host_standby = $(By.linkText("Hot Standby"));
    public SelenideElement system_tree_remote_management = $(By.linkText("Remote Management"));
    public SelenideElement system_tree_integration = $(By.linkText("Integration"));

    /** 左侧菜单 -- Security **/
    public SelenideElement left_menu_first_level_security = $(By.xpath("//span[contains(text(),'Security')]"));
    public SelenideElement security_tree_security_rules = $(By.linkText("Security Rules"));
    public SelenideElement security_tree_security_settings = $(By.linkText("Security Settings"));


    /** 左侧菜单 -- Maintenance **/
    public SelenideElement left_menu_first_level_maintenance = $(By.xpath("//span[contains(text(),'Maintenance')]"));
    public SelenideElement maintenance_tree_upgrade = $(By.linkText("Upgrade"));
    public SelenideElement maintenance_tree_backup_and_restore = $(By.linkText("Backup And Restore"));
    public SelenideElement maintenance_tree_reboot = $(By.linkText("Reboot"));
    public SelenideElement maintenance_tree_factory_reset = $(By.linkText("Factory Reset"));
    public SelenideElement maintenance_tree_operation_logs = $(By.linkText("Operation Logs"));
    public SelenideElement maintenance_tree_trouble_shooting = $(By.linkText("Trouble Shooting"));


    /** 左侧菜单 -- CDR/Recording **/
    public SelenideElement left_menu_first_level_cdr_recording = $(By.xpath("//span[contains(text(),'CDR/Recording')]"));
    public SelenideElement cdr_recording_tree_cdr = $(By.linkText("CDR"));
    public SelenideElement cdr_recording_tree_recording = $(By.linkText("Recording"));
    public SelenideElement cdr_recording_tree_call_report = $(By.linkText("Call Report"));


    public SelenideElement search = $(By.id("search"));



}
