package com.yeastar.page.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import static com.codeborne.selenide.Selenide.$;

public interface IExtensionPageElement {
    /**查询输入框*/
    SelenideElement searchIpt = $(By.xpath("//input[@placeholder=\"Search\"]"));


    SelenideElement ele_add_DropDown_bulk_add_Btn = $(By.xpath("//li[text()='Bulk Add']"));
    SelenideElement ele_add_DropDown_add_Btn = $(By.xpath("//li[text()='Add']"));
    SelenideElement ele_delete_all_checkbox = $(By.xpath("//table//thead//input[1]"));

    //Tab标签
    String TAB_COMM_XPATH = "//div[contains(@role,'tab') and contains(text(),\"%s\")]";
    SelenideElement ele_tab_user = $(By.xpath(String.format(TAB_COMM_XPATH, "User")));
    SelenideElement ele_tab_presence = $(By.xpath(String.format(TAB_COMM_XPATH, "Presence")));
    SelenideElement ele_tab_voicemail = $(By.xpath(String.format(TAB_COMM_XPATH, "Voicemail")));
    SelenideElement ele_tab_features = $(By.xpath(String.format(TAB_COMM_XPATH, "Features")));
    SelenideElement ele_tab_advanced = $(By.xpath(String.format(TAB_COMM_XPATH, "Advanced")));
    SelenideElement ele_tab_security = $(By.xpath(String.format(TAB_COMM_XPATH, "Security")));
    SelenideElement ele_tab_linkus_clients = $(By.xpath(String.format(TAB_COMM_XPATH, "Linkus Clients")));

    //Tab User basic
    SelenideElement ele_sip_extension_selection = $(By.xpath("//div[contains(text(),\"SIP Extension\")]"));
    SelenideElement ele_fxs_extension_selection = $(By.xpath("//div[contains(text(),\"FXS Extension\")]"));

    //User
    SelenideElement ele_extension_user_caller_id = $(By.id("extension_user_caller_id"));
    SelenideElement ele_extension_user_user_password = $(By.id("extension_user_user_password"));
    SelenideElement ele_extension_user_first_name = $(By.id("extension_user_first_name"));
    SelenideElement ele_extension_user_last_name = $(By.id("extension_user_last_name"));
    SelenideElement ele_extension_user_number = $(By.id("extension_user_number"));
    SelenideElement ele_extension_user_reg_name = $(By.id("extension_user_reg_name"));
    SelenideElement ele_extension_user_reg_password = $(By.id("extension_user_reg_password"));

    //密码强度不够弹框
     SelenideElement ele_registration_password_not_strong_alert = $(By.xpath("//div[contains(text(),\"Registration Password is not strong, continue to save\")]"));
     SelenideElement ele_extension_list_warning_registration_warning_img = $(By.xpath("//i[contains(@aria-label,\"icon: warning\") and contains(@title,\"注册密码强度弱\")]"));


     /********advance ***/
     //下拉列表 DTMF_MODE
     SelenideElement  ele_extension_advanced_dtmf_mode = $(By.id("extension_advanced_dtmf_mode"));
     //下拉列表 Transport
     SelenideElement  ele_extension_advanced_transport = $(By.id("extension_advanced_transport"));
     //Qualify check_box
     SelenideElement ele_extension_advanced_enb_qualify_checkbox =$(By.id("extension_advanced_enb_qualify"));
    //T.38 Support check_box
    SelenideElement ele_extension_advanced_enb_t38_support_checkbox =$(By.id("extension_advanced_enb_t38_support"));
    //NAT check_box
    SelenideElement ele_extension_advanced_enb_nat_checkbox =$(By.id("extension_advanced_enb_nat"));
    //Enable SRTP check_box
    SelenideElement ele_extension_advanced_enb_srtp_checkbox =$(By.id("extension_advanced_enb_srtp"));

    /** 下拉列表 用户角色 **/
    enum TABLE_MENU {
        USER("User"),
        PRESENCE("Presence"),
        VOICEMAIL("Voicemail"),
        FEATURES("Features"),
        ADVANCED("Advanced"),
        SECURITY("Security"),
        LINKUS_CLIENTS("Linkus Clients");

        private final String alias;

        TABLE_MENU(String alias) {
            this.alias = alias;
        }


        public String getAlias() {
            return alias;
        }
    }

    /** 下拉列表 用户角色 **/
    enum USER_ROLE {
        None("None"),
        Administrator("Administrator"),
        Supervisor("Supervisor"),
        Operator("Operator"),
        Employee("Employee"),
        Human_Resource("Human Resource"),
        Accounting("Accounting");

        private final String alias;

        USER_ROLE(String alias) {
            this.alias = alias;
        }


        public String getAlias() {
            return alias;
        }
    }

    /** 下拉列表 DTMF_MODE **/
    enum DTMF_MODE {
        RFC4733RFC2833("RFC4733(RFC2833)"),
        INFO("Info"),
        INBAND("Inband"),
        AUTO("Auto");

        private final String alias;

        DTMF_MODE(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            ele_extension_advanced_dtmf_mode.shouldBe(Condition.enabled).click();
            return alias;
        }
    }
    /** 下拉列表 TRANSPORT **/
    enum TRANSPORT{
        UDP("UDP"),
        TCP("TCP"),
        TLS("TLS");

        private final String alias;

        TRANSPORT(String alias) {
            this.alias = alias;
        }
        public String getAlias() {
            ele_extension_advanced_transport.shouldBe(Condition.enabled).click();
            return alias;
        }
    }

    /** 下拉列表 Voicemail PIN Authentication **/
    enum VOICEMAIL_PIN_AUTH{
        ENABLED("Enabled"),
        DISABLED("Disabled");

        private final String alias;

        VOICEMAIL_PIN_AUTH(String alias){
            this.alias = alias;
        }

        public String getAlias() {
            ele_extension_voicemail_pin_auth_combobox.shouldBe(Condition.enabled).click();
            return alias;
        }
    }

    /** 下拉列表 New Voicemail Notification **/
    enum NEW_VOICEMAIL_NOTIFICATION{
        SEND_EMAIL_NOTIFICATIONS_WITH_ATTACHMENT("Send Email Notifications with Attachment"),
        DO_NOT_SEND_EMAIL_NOTIFICATIONS("Do Not Send Email Notifications"),
        SEND_EMAIL_NOTIFICATIONS_WITHOUT_ATTACHMENT("Send Email Notifications without Attachment");
        private final String alias;

        NEW_VOICEMAIL_NOTIFICATION(String alias){
            this.alias = alias;
        }
        public String getAlias() {
            ele_extension_voicemail_new_notification_combobox.shouldBe(Condition.enabled).click();
            return alias;
        }
    }
    /** 下拉列表 After Notification **/
    enum AFTER_NOTIFICATION{
        MARK_AS_READ("Mark as Read"),
        DELETE_VOICEMAIL("Delete Voicemail"),
        DO_NOTHING("Do Nothing");
        private final String alias;

        AFTER_NOTIFICATION(String alias){
            this.alias = alias;
        }

        public String getAlias() {
            ele_extension_voicemail_after_notification_combobox.shouldBe(Condition.enabled).click();
            return alias;
        }
    }

    enum DEFAULT_GREETING{
        FOLLOW_SYSTEM("[Follow System]");
        private final String alias;

        DEFAULT_GREETING(String alias){
            this.alias = alias;
        }

        public String getAlias() {
            ele_extension_voicemail_default_greeting_combobox.shouldBe(Condition.enabled).click();
            return alias;
        }
    }


    //Tab Presence
    SelenideElement extension_presence_information_input = $(By.id("extension_presence_information"));
    //Tab Presence->Internal Calls
    SelenideElement ele_extension_presence_forward_enb_in_always_forward_checkBox = $(By.id("extension_presence_forward_enb_in_always_forward"));
    SelenideElement ele_extension_presence_forward_enb_in_no_answer_forward_checkBox = $(By.id("extension_presence_forward_enb_in_no_answer_forward"));
    SelenideElement ele_extension_presence_forward_enb_in_busy_forward_checkBox = $(By.id("extension_presence_forward_enb_in_busy_forward"));
    //Tab Presence->External Calls
    SelenideElement ele_extension_presence_forward_enb_ex_always_forward_checkBox = $(By.id("extension_presence_forward_enb_ex_always_forward"));
    SelenideElement ele_extension_presence_forward_enb_ex_no_answer_forward_checkBox = $(By.id("extension_presence_forward_enb_ex_no_answer_forward"));
    SelenideElement ele_extension_presence_forward_enb_ex_busy_forward_checkBox = $(By.id("extension_presence_forward_enb_ex_busy_forward"));
    //Tab Presence->Ring Strategy
    SelenideElement ele_extension_presence_enb_ring1_endpoints_checkBox = $(By.id("extension_presence_enb_ring1_endpoints"));
    SelenideElement ele_extension_presence_enb_ring1_mobile_client_checkBox = $(By.id("extension_presence_enb_ring1_mobile_client"));
    SelenideElement ele_extension_presence_enb_ring1_desktop_client_checkBox = $(By.id("extension_presence_enb_ring1_desktop_client_forward"));

    /** 下拉列表 目的地********/
    enum DESTINATION {
        END_CALL("End Call"),
        EXTENSION("Extension"),
        Voicemail("Voicemail"),
        IVR("IVR"),
        RING_GROUP("Ring Group"),
        QUEUE("Queue"),
        MOBILE_NUMBER("Mobile Number"),
        EXTERNAL_NUMBER("External Number"),
        PLAY_GREETING_THEN_EXIT("Play Greeting then Exit");

        private final String alias;

        DESTINATION(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            return alias;
        }
    }

    //Tab Vociemail
    SelenideElement ele_extension_voicemail_enable = $(By.id("extension_voicemail_enb_vm"));
    //Tab Voicemail -> Enable Voicemail
    SelenideElement ele_extension_voicemail_pin_auth_combobox = $(By.id("extension_voicemail_enb_vm_pin"));
    SelenideElement ele_extension_voicemail_access_pin = $(By.id("extension_voicemail_vm_pin"));
    SelenideElement ele_extension_voicemail_new_notification_combobox = $(By.id("extension_voicemail_new_vm_notify"));
    SelenideElement ele_extension_voicemail_after_notification_combobox = $(By.id("extension_voicemail_after_vm_notify"));
    SelenideElement ele_extension_voicemail_play_date_time_checkbox = $(By.id("extension_voicemail_enb_vm_play_datetime"));
    SelenideElement ele_extension_voicemail_play_caller_id_checkbox = $(By.id("extension_voicemail_enb_vm_play_caller_id"));
    SelenideElement ele_extension_voicemail_play_message_duration_checkbox = $(By.id("extension_voicemail_enb_vm_play_duration"));
    //Tab Voicemail -> Voicemail Greeting
    SelenideElement ele_extension_voicemail_default_greeting_combobox = $(By.id("extension_voicemail_vm_greeting"));
    SelenideElement ele_extension_voicemail_available = $(By.id("extension_voicemail_voicemail_available"));
    SelenideElement ele_extension_voicemail_away = $(By.id("extension_voicemail_voicemail_away"));
    SelenideElement ele_extension_voicemail_doNotDisturb = $(By.id("extension_voicemail_voicemail_do_not_disturb"));
    SelenideElement ele_extension_voicemail_lunchBreak = $(By.id("extension_voicemail_voicemail_launch"));
    SelenideElement ele_extension_voicemail_businessTrip = $(By.id("extension_voicemail_voicemail_business_trip"));
    SelenideElement ele_extension_voicemail_offWork = $(By.id("extension_voicemail_voicemail_off_work"));
}
