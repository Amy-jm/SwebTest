package com.yeastar.page.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.untils.WaitUntils;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

@Log4j2
public class HomePage {

    String LI_XPATH = "//li[@title=\"%s\"]";
    /**
     * 左侧菜单 -- Dashboard
     **/
    public SelenideElement left_menu_dashboard = $(By.id("m_dashboard"));
    /**
     * 左侧菜单 -- Extension/Trunk
     **/
    public SelenideElement left_menu_first_level_extension_trunk = $(By.id("m_extension_trunk"));//m_extension_trunk
    public SelenideElement extension_trunk_tree_extensions = $(By.id("m_extensions"));//m_extensions
    public SelenideElement extension_trunk_tree_extension_group = $(By.id("m_extension_groups"));
    public SelenideElement extension_trunk_tree_trunks = $(By.id("m_trunks"));
    public SelenideElement extension_trunk_tree_management = $(By.id("m_role_management"));
    /**
     * 左侧菜单 -- Call Control
     **/
    public SelenideElement left_menu_first_level_call_control = $(By.id("m_call_control"));
    public SelenideElement call_control_tree_inbound_routes = $(By.id("m_inbound_routes"));
    public SelenideElement call_control_tree_outbound_routes = $(By.id("m_outbound_routes"));
    public SelenideElement call_control_tree_office_time_and_holidays = $(By.id("m_office_time_and_holidays"));
    public SelenideElement call_control_tree_emergency_number = $(By.id("m_emergency_number"));

    /**
     * 左侧菜单 -- Call Feature
     **/
    public SelenideElement left_menu_first_level_call_feature = $(By.id("m_call_features"));
    public SelenideElement call_feature_tree_voicemail = $(By.id("m_voicemail"));
    public SelenideElement call_feature_tree_feature_codes = $(By.id("m_feature_code"));
    public SelenideElement call_feature_tree_ivr = $(By.id("m_ivr"));
    public SelenideElement call_feature_tree_ring_group = $(By.id("m_ring_group"));
    public SelenideElement call_feature_tree_queue = $(By.id("m_queue"));
    public SelenideElement call_feature_tree_conference = $(By.id("m_conference"));
    public SelenideElement call_feature_tree_speed_dial = $(By.id("m_speed_dial"));
    public SelenideElement call_feature_tree_recording = $(By.id("m_auto_record"));


    /**
     * 左侧菜单 -- PBX Settings
     **/
    public SelenideElement left_menu_first_level_pbx_settings = $(By.id("m_pbx_settings"));
    public SelenideElement pbx_settings_tree_preferences = $(By.id("m_preference"));
    public SelenideElement pbx_settings_tree_voice_prompt = $(By.id("m_voice_prompt"));
    public SelenideElement pbx_settings_tree_sip_settings = $(By.id("m_sip_settings"));
    public SelenideElement pbx_settings_tree_jitter_buffer = $(By.id("m_jitter_buffer"));

    /**
     * 左侧菜单 -- System
     **/
    public SelenideElement left_menu_first_level_system = $(By.id("m_system"));
    public SelenideElement system_tree_network = $(By.id("m_network"));
    public SelenideElement system_tree_date_and_time = $(By.id("m_date_and_time"));
    public SelenideElement system_tree_email = $(By.id("m_email"));
    public SelenideElement system_tree_storage = $(By.id("m_storage"));
    public SelenideElement system_tree_event_notification = $(By.id("m_event_notification"));
    public SelenideElement system_tree_host_standby = $(By.id("m_hot_standby"));
    public SelenideElement system_tree_remote_management = $(By.id("m_remote_management"));
    public SelenideElement system_tree_integration = $(By.id("m_integration"));

    /**
     * 左侧菜单 -- Security
     **/
    public SelenideElement left_menu_first_level_security = $(By.id("m_security"));
    public SelenideElement security_tree_security_rules = $(By.id("m_security_rules"));
    public SelenideElement security_tree_security_settings = $(By.id("m_security_settings"));


    /**
     * 左侧菜单 -- Maintenance
     **/
    public SelenideElement left_menu_first_level_maintenance = $(By.id("m_maintenance"));
    public SelenideElement maintenance_tree_upgrade = $(By.id("m_upgrade"));
    public SelenideElement maintenance_tree_backup_and_restore = $(By.id("m_backup_and_restore"));
    public SelenideElement maintenance_tree_reboot = $(By.id("m_reboot"));
    public SelenideElement maintenance_tree_factory_reset = $(By.id("m_factory_reset"));
    public SelenideElement maintenance_tree_operation_logs = $(By.id("m_operation_logs"));
    public SelenideElement maintenance_tree_trouble_shooting = $(By.id("m_trouble_shooting"));


    /**
     * 左侧菜单 -- CDR/Recording
     **/
    public SelenideElement left_menu_first_level_cdr_recording = $(By.id("m_cdr_recording"));
    public SelenideElement cdr_recording_tree_cdr = $(By.id("m_cdr"));
    public SelenideElement cdr_recording_tree_recording = $(By.id("Recording"));
    public SelenideElement cdr_recording_tree_call_report = $(By.id("Call Report"));


    public SelenideElement search = $(By.id("search"));

    /**
     * 菜单 login user --
     **/

    public String link_comm_xpath = "//li[contains(text(),'%s')]";
//    public SelenideElement header_box_name = $(By.xpath("//span[contains(@class,'header-box')]"));//login username
     public SelenideElement header_box_name = $(By.xpath("//span[contains(@class,'ant-avatar')]/following-sibling::span"));//login username   //span[contains(@class,'ant-avatar')]/following-sibling::span


    public SelenideElement menu_first_level_user_dropdown = $(By.xpath("//div[contains(@class,'ant-dropdown-link ant-dropdown-trigger')]//i[contains(@class,\"anticon anticon-down\")]"));
    public SelenideElement user_tree_setting = $(By.xpath("//div[@class=\"ant-dropdown ant-dropdown-placement-bottomRight\"]//li[contains(text(),'Setting')]"));
    public SelenideElement user_tree_change_password = $(By.xpath(String.format(link_comm_xpath,"Change Password")));
    public SelenideElement user_tree_privacy_policy_agreement =  $(By.xpath(String.format(link_comm_xpath,"Privacy Policy Agreement")));
    public SelenideElement user_tree_language = $(By.xpath(String.format(link_comm_xpath,"Language")));
    public SelenideElement user_tree_language_english =  $(By.xpath(String.format(link_comm_xpath,"English")));
    public SelenideElement user_tree_language_china =  $(By.xpath(String.format(link_comm_xpath,"简体中文")));
    public SelenideElement user_tree_logout = $(By.xpath(String.format(link_comm_xpath,"Logout")));

    /**
     * logout pbx
     */
    public void logout() {
        menu_first_level_user_dropdown.click();
        user_tree_logout.waitUntil(Condition.visible,3*1000).click();
    }


    /**
     * 系统提示信息
     */
    public SelenideElement system_alert_message = $(By.xpath("//a[contains(@class,'close')]"));

    /**
     * 菜单选择
     */
    public void intoPage(Menu_Level_1 level_1, Menu_Level_2 level_2) {
        sleep(WaitUntils.SHORT_WAIT*2);
        //close alert
        // todo need to delete sleep
        while(system_alert_message.exists()){
            system_alert_message.click();
            sleep(2000);
        }
        //左侧一级菜单
        switch (level_1) {
            case extension_trunk:
                left_menu_first_level_extension_trunk.click();
                break;
            case call_control:
                left_menu_first_level_call_control.click();
                break;
            case call_feature:
                left_menu_first_level_call_feature.click();
                break;
            case pbx_settings:
                left_menu_first_level_pbx_settings.click();
                break;
            case security:
                left_menu_first_level_security.click();
                break;
            case maintenance:
                left_menu_first_level_maintenance.click();
                break;
            case cdr_recording:
                left_menu_first_level_cdr_recording.click();
                break;
        }

        //左侧二级菜单
        switch (level_2) {
            case extension_trunk_tree_extensions:
                extension_trunk_tree_extensions.click();
                break;
            case extension_trunk_tree_extension_group:
                extension_trunk_tree_extension_group.click();
                break;
            case extension_trunk_tree_trunks:
                extension_trunk_tree_trunks.click();
                break;
            case extension_trunk_tree_management:
                extension_trunk_tree_management.click();
                break;
            case call_control_tree_inbound_routes:
                call_control_tree_inbound_routes.click();
                break;
            case call_control_tree_outbound_routes:
                call_control_tree_outbound_routes.click();
                break;
            case call_control_tree_office_time_and_holidays:
                call_control_tree_office_time_and_holidays.click();
                break;
            case call_control_tree_emergency_number:
                call_control_tree_emergency_number.click();
                break;
            case call_feature_tree_voicemail:
                call_feature_tree_voicemail.click();
                break;
            case call_feature_tree_feature_codes:
                call_feature_tree_feature_codes.click();
                break;
            case call_feature_tree_ivr:
                call_feature_tree_ivr.click();
                break;
            case call_feature_tree_ring_group:
                call_feature_tree_ring_group.click();
                break;
            case call_feature_tree_queue:
                call_feature_tree_queue.click();
                break;
            case call_feature_tree_conference:
                call_feature_tree_conference.click();
                break;
            case pbx_settings_tree_preferences:
                pbx_settings_tree_preferences.click();
                break;
            case pbx_settings_tree_voice_prompt:
                pbx_settings_tree_voice_prompt.click();
                break;
            case pbx_settings_tree_sip_settings:
                pbx_settings_tree_sip_settings.click();
                break;
            case pbx_settings_tree_jitter_buffer:
                pbx_settings_tree_jitter_buffer.click();
                break;
            case system_tree_network:
                system_tree_network.click();
                break;
            case system_tree_date_and_time:
                system_tree_date_and_time.click();
                break;
            case system_tree_email:
                system_tree_email.click();
                break;
            case system_tree_storage:
                system_tree_storage.click();
                break;
            case system_tree_event_notification:
                system_tree_event_notification.click();
                break;
            case system_tree_host_standby:
                system_tree_host_standby.click();
                break;
            case system_tree_remote_management:
                system_tree_remote_management.click();
                break;
            case system_tree_integration:
                system_tree_integration.click();
                break;
            case security_tree_security_rules:
                security_tree_security_rules.click();
                break;
            case security_tree_security_settings:
                security_tree_security_settings.click();
                break;
            case maintenance_tree_upgrade:
                maintenance_tree_upgrade.click();
                break;
            case maintenance_tree_backup_and_restore:
                maintenance_tree_backup_and_restore.click();
                break;
            case maintenance_tree_reboot:
                maintenance_tree_reboot.click();
                break;
            case maintenance_tree_factory_reset:
                maintenance_tree_factory_reset.click();
                break;
            case maintenance_tree_operation_logs:
                maintenance_tree_operation_logs.click();
                break;
            case maintenance_tree_trouble_shooting:
                maintenance_tree_trouble_shooting.click();
                break;
            case cdr_recording_tree_cdr:
                cdr_recording_tree_cdr.click();
                break;
            case cdr_recording_tree_recording:
                cdr_recording_tree_recording.click();
                break;
            case cdr_recording_tree_call_report:
                cdr_recording_tree_call_report.click();
                break;
        }
//        sleep(3000);
//        //close alert
//        while(system_alert_message.exists()){
//            system_alert_message.click();
//            sleep(2000);
//        }

    }


    /**
     * 左侧 一 二级菜单
     **/
    public enum Menu_Level_1 {
        extension_trunk,
        call_control,
        call_feature,
        pbx_settings,
        security,
        maintenance,
        cdr_recording;
    }

    public enum Menu_Level_2 {
        extension_trunk_tree_extensions,
        extension_trunk_tree_extension_group,
        extension_trunk_tree_trunks,
        extension_trunk_tree_management,

        call_control_tree_inbound_routes,
        call_control_tree_outbound_routes,
        call_control_tree_office_time_and_holidays,
        call_control_tree_emergency_number,

        call_feature_tree_voicemail,
        call_feature_tree_feature_codes,
        call_feature_tree_ivr,
        call_feature_tree_ring_group,
        call_feature_tree_queue,
        call_feature_tree_conference,

        pbx_settings_tree_preferences,
        pbx_settings_tree_voice_prompt,
        pbx_settings_tree_sip_settings,
        pbx_settings_tree_jitter_buffer,

        system_tree_network,
        system_tree_date_and_time,
        system_tree_email,
        system_tree_storage,
        system_tree_event_notification,
        system_tree_host_standby,
        system_tree_remote_management,
        system_tree_integration,


        security_tree_security_rules,
        security_tree_security_settings,

        maintenance_tree_upgrade,
        maintenance_tree_backup_and_restore,
        maintenance_tree_reboot,
        maintenance_tree_factory_reset,
        maintenance_tree_operation_logs,
        maintenance_tree_trouble_shooting,

        cdr_recording_tree_cdr,
        cdr_recording_tree_recording,
        cdr_recording_tree_call_report;
    }

}
