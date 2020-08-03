package com.yeastar.page.pseries.ExtensionTrunk;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.untils.UIMapUtils;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.yeastar.swebtest.driver.DataReader2.UI_MAP;

public interface IExtensionPageElement {
    /**查询输入框*/
    SelenideElement searchIpt = $(By.xpath("//input[@placeholder="+UI_MAP.getString("header.search_placeholder")+"]"));


    SelenideElement ele_add_DropDown_bulk_add_Btn = $(By.xpath("//li[text()='"+UI_MAP.getString("common.bulk_add")+"']"));
    SelenideElement ele_add_DropDown_add_Btn = $(By.xpath("//li[text()='Add']"));
    SelenideElement ele_delete_all_checkbox = $(By.xpath("//table//thead//input[1]"));

    /** 下拉列表 分机页面，分机列表中Presence可选项 **/
    enum TABLE_PRESENCE_LIST {
        AVAILABLE("Available"),
        AWAY("Away"),
        BUSINESSTRIP("Business Trip"),
        DONotDISTURB("Do Not Disturb"),
        LUNCHBREAK("Lunch Break"),
        OFFWORK("Off Work");

        private final String alias;

        TABLE_PRESENCE_LIST(String alias) {
            this.alias = alias;
        }


        public String getAlias() {
            return alias;
        }
    }

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
    SelenideElement ele_sip_extension_selection = $(By.xpath("//div[contains(text(),"+UI_MAP.getString("extension_trunk.extensions.SIP")+")]"));
    SelenideElement ele_fxs_extension_selection = $(By.xpath("//div[contains(text(),"+UI_MAP.getString("extension_trunk.extensions.FXS")+")]"));

    //User
    SelenideElement ele_extension_user_caller_id = $(By.id("extension_user_caller_id"));
    SelenideElement ele_extension_user_user_password = $(By.id("extension_user_user_password"));
    SelenideElement ele_extension_user_first_name = $(By.id("extension_user_first_name"));
    SelenideElement ele_extension_user_last_name = $(By.id("extension_user_last_name"));
    SelenideElement ele_extension_user_number = $(By.id("extension_user_number"));
    SelenideElement ele_extension_user_email_addr = $(By.xpath("//div[@id='extension_user_email_addr']//input"));
    SelenideElement ele_extension_user_mobile_number = $(By.id("extension_user_mobile_number"));
    SelenideElement ele_extension_user_reg_name = $(By.id("extension_user_reg_name"));
    SelenideElement ele_extension_user_reg_password = $(By.id("extension_user_reg_password"));
    SelenideElement ele_extension_user_role_id = $(By.id("extension_user_role_id"));
//    SelenideElement ele_extension_user_first_name = $(By.id("extension_user_first_name"));

    //密码强度不够弹框
    SelenideElement ele_registration_password_not_strong_alert = $(By.xpath("//div[contains(text(),\""+UI_MAP.getString("extension_trunk.extensions.pwd_weak_continue")+"\")]"));
    SelenideElement ele_extension_list_warning_registration_warning_img = $(By.xpath("//span[contains(@title,\""+UI_MAP.getString("extension_trunk.extensions.weak_reg_password")+"\")]"));


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

    /*********Security *****/
    //Allow Register
    SelenideElement ele_extension_security_allow_reg_remotely_checkbox = $(By.id("extension_security_allow_reg_remotely"));
    //Enable User Agent Registration Authorization
    SelenideElement ele_extension_security_enb_user_agent_ident_checkbox = $(By.id("extension_security_enb_user_agent_ident"));
    //Enable IP Restriction
    SelenideElement ele_extension_security_enb_ip_rstr_checkbox = $(By.id("extension_security_enb_ip_rstr"));
    //Disable Outbound Calls
    SelenideElement ele_extension_security_disable_outb_call_checkbox = $(By.id("extension_security_disable_outb_call"));
    //Disable Outbound Calls outside Business
    SelenideElement ele_extension_security_disable_office_time_outb_call_checkbox = $(By.id("extension_security_disable_office_time_outb_call"));
    //Max Outbound Call Durations
    SelenideElement ele_extension_security_max_outb_call_duration_select = $(By.xpath("//*[@id=\"extension_security_max_outb_call_duration\"]//input"));
    //-SIP User Agent Identification
    ElementsCollection ele_list_user_agent_input = $$(By.id("user_agent"));
    //--Add User Agent btn
    SelenideElement ele_add_user_agent_btn =$(By.xpath("//span[contains(text(),\""+UI_MAP.getString("extension_trunk.extensions.add_user_agent")+"\")]"));

    //-SIP User Agent Identification
    ElementsCollection ele_list_permitted_ip_input = $$(By.id("permitted_ip"));
    ElementsCollection ele_list_netmask_input = $$(By.id("netmask"));
    //--Add User Agent btn
    SelenideElement ele_add_ip_btn =$(By.xpath("//span[contains(text(),\""+UI_MAP.getString("extension_trunk.extensions.add_ip")+"\")]"));

    /** Features **/
    SelenideElement ele_extension_feature_enb_email_pwd_chg = $(By.id("extension_feature_enb_email_pwd_chg"));
    SelenideElement ele_extension_feature_enb_email_miss_call = $(By.id("extension_feature_enb_email_miss_call"));
    //Add Call Handing Rule--Caller ID
    SelenideElement ele_call_blocking_list_caller_id = $(By.id("call_blocking_list_caller_id"));
    //Add Call Handing Rule--Action
    SelenideElement ele_call_blocking_list_action_dest  = $(By.id("call_blocking_list_action_dest"));


    /** Role ****/
    SelenideElement ele_role_management_name = $(By.id("role_management_name"));
    SelenideElement ele_role_management_enable_extension_input =  $(By.id("role_management_enable_extension"));

    /** Linkus Server ****/
    SelenideElement ele_extension_server_enable_extension_login_checkbox  = $(By.id("extension_server_enable_extension_login"));// Extension Number
    SelenideElement ele_extension_server_enable_email_login_checkbox =$(By.id("extension_server_enable_email_login"));// Email Address

    /** 下拉列表 Max Outbound Call Duration(s) **/
   enum MAX_OUTBOUND_CALL_DURATIONS{
       FOLLOWSYSTEM(UI_MAP.getString("extension_trunk.extensions.follow_system")),
       UNLIMITED(UI_MAP.getString("extension_trunk.extensions.unlimited")),
       S_60("60"),
       S_300("300"),
       S_600("600"),
       S_900("900");

       private final String alias;
       MAX_OUTBOUND_CALL_DURATIONS(String alias) {
           this.alias = alias;
       }
       public String getAlias() {
           ele_extension_security_max_outb_call_duration_select.shouldBe(Condition.enabled).click();
           return alias;
       }
   }

    /** 下拉列表 用户角色 **/
    enum TABLE_MENU {
        USER(UI_MAP.getString("extension_trunk.extensions.user")),
        PRESENCE(UI_MAP.getString("extension_trunk.extensions.presence")),
        VOICEMAIL(UI_MAP.getString("extension_trunk.extensions.voicemail")),
        FEATURES(UI_MAP.getString("extension_trunk.extensions.features")),
        ADVANCED(UI_MAP.getString("extension_trunk.extensions.advanced")),
        SECURITY(UI_MAP.getString("extension_trunk.extensions.security")),
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
        RFC4733RFC2833("RFC4733 (RFC2833)"),
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



    //Tab Presence
    SelenideElement ele_extension_presence_information_input = $(By.id("extension_presence_information"));
    //Tab Presence
    SelenideElement ele_extension_presence_available_tab = $(By.xpath("//input[contains(@value,'available')]/../.."));
    SelenideElement ele_extension_presence_away_tab = $(By.xpath("//input[contains(@value,'away')]/../.."));
    SelenideElement ele_extension_presence_doNotDisturb_tab = $(By.xpath("//input[contains(@value,'do_not_disturb')]/../.."));
    SelenideElement ele_extension_presence_launch_tab = $(By.xpath("//input[contains(@value,'launch')]/../.."));
    SelenideElement ele_extension_presence_businessTrip_tab = $(By.xpath("//input[contains(@value,'business_trip')]/../.."));
    SelenideElement ele_extension_presence_off_work_tab = $(By.xpath("//input[contains(@value,'off_work')]/../.."));

    //Tab Presence->Internal Calls
    SelenideElement ele_extension_presence_forward_enb_in_always_forward_checkBox = $(By.id("extension_presence_forward_enb_in_always_forward"));
    SelenideElement ele_extension_presence_forward_in_always_forward_dest_combobox = $(By.id("extension_presence_forward_in_always_forward_dest"));
    SelenideElement ele_extension_presence_forward_in_always_forward_value_combobox = $(By.id("extension_presence_forward_in_always_forward_value"));
    SelenideElement ele_extension_presence_forward_play_in_always_forward_prefix_combobox = $(By.id("extension_presence_forward_play_in_always_forward_prefix"));
    SelenideElement ele_extension_presence_forward_in_always_forward_prefix_input = $(By.id("extension_presence_forward_in_always_forward_prefix"));
    SelenideElement ele_extension_presence_forward_in_always_forward_num_input = $(By.id("extension_presence_forward_in_always_forward_num"));

    SelenideElement ele_extension_presence_forward_enb_in_no_answer_forward_checkBox = $(By.id("extension_presence_forward_enb_in_no_answer_forward"));
    SelenideElement ele_extension_presence_forward_in_no_answer_forward_dest_combobox = $(By.id("extension_presence_forward_in_no_answer_forward_dest"));
    SelenideElement ele_extension_presence_forward_in_no_answer_forward_value_combobox = $(By.id("extension_presence_forward_in_no_answer_forward_value"));
    SelenideElement ele_extension_presence_forward_play_in_no_answer_forward_prefix_combobox = $(By.id("extension_presence_forward_play_in_no_answer_forward_prefix"));
    SelenideElement ele_extension_presence_forward_in_no_answer_forward_prefix_input = $(By.id("extension_presence_forward_in_no_answer_forward_prefix"));
    SelenideElement ele_extension_presence_forward_in_no_answer_forward_num_input = $(By.id("extension_presence_forward_in_no_answer_forward_num"));

    SelenideElement ele_extension_presence_forward_enb_in_busy_forward_checkBox = $(By.id("extension_presence_forward_enb_in_busy_forward"));
    SelenideElement ele_extension_presence_forward_in_busy_forward_dest_combobox = $(By.id("extension_presence_forward_in_busy_forward_dest"));
    SelenideElement ele_extension_presence_forward_in_busy_forward_value_combobox = $(By.id("extension_presence_forward_in_busy_forward_value"));
    SelenideElement ele_extension_presence_forward_play_in_busy_forward_prefix_combobox = $(By.id("extension_presence_forward_play_in_busy_forward_prefix"));
    SelenideElement ele_extension_presence_forward_in_busy_forward_prefix_input = $(By.id("extension_presence_forward_in_busy_forward_prefix"));
    SelenideElement ele_extension_presence_forward_in_busy_forward_num_input = $(By.id("extension_presence_forward_in_busy_forward_num"));

    //Tab Presence->External Calls
    SelenideElement ele_extension_presence_forward_enb_ex_always_forward_checkBox = $(By.id("extension_presence_forward_enb_ex_always_forward"));
    SelenideElement ele_extension_presence_forward_ex_always_forward_dest_combobox = $(By.id("extension_presence_forward_ex_always_forward_dest"));
    SelenideElement ele_extension_presence_forward_ex_always_forward_value_combobox = $(By.id("extension_presence_forward_ex_always_forward_value"));
    SelenideElement ele_extension_presence_forward_play_ex_always_forward_prefix_combobox = $(By.id("extension_presence_forward_play_ex_always_forward_prefix"));
    SelenideElement ele_extension_presence_forward_ex_always_forward_prefix_input = $(By.id("extension_presence_forward_ex_always_forward_prefix"));
    SelenideElement ele_extension_presence_forward_ex_always_forward_num_input = $(By.id("extension_presence_forward_ex_always_forward_num"));


    SelenideElement ele_extension_presence_forward_enb_ex_no_answer_forward_checkBox = $(By.id("extension_presence_forward_enb_ex_no_answer_forward"));
    SelenideElement ele_extension_presence_forward_ex_no_answer_forward_dest_combobox = $(By.id("extension_presence_forward_ex_no_answer_forward_dest"));
    SelenideElement ele_extension_presence_forward_ex_no_answer_forward_value_combobox = $(By.id("extension_presence_forward_ex_no_answer_forward_value"));
    SelenideElement ele_extension_presence_forward_play_ex_no_answer_forward_prefix_combobox = $(By.id("extension_presence_forward_play_ex_no_answer_forward_prefix"));
    SelenideElement ele_extension_presence_forward_ex_no_answer_forward_prefix_input = $(By.id("extension_presence_forward_ex_no_answer_forward_prefix"));
    SelenideElement ele_extension_presence_forward_ex_no_answer_forward_num_input = $(By.id("extension_presence_forward_ex_no_answer_forward_num"));

    SelenideElement ele_extension_presence_forward_enb_ex_busy_forward_checkBox = $(By.id("extension_presence_forward_enb_ex_busy_forward"));
    SelenideElement ele_extension_presence_forward_ex_busy_forward_dest_combobox = $(By.id("extension_presence_forward_ex_busy_forward_dest"));
    SelenideElement ele_extension_presence_forward_ex_busy_forward_value_combobox = $(By.id("extension_presence_forward_ex_busy_forward_value"));
    SelenideElement ele_extension_presence_forward_play_ex_busy_forward_prefix_combobox = $(By.id("extension_presence_forward_play_ex_busy_forward_prefix"));
    SelenideElement ele_extension_presence_forward_ex_busy_forward_prefix_input = $(By.id("extension_presence_forward_ex_busy_forward_prefix"));
    SelenideElement ele_extension_presence_forward_ex_busy_forward_num_input = $(By.id("extension_presence_forward_ex_busy_forward_num"));

    //Tab Presence->Ring Strategy
    SelenideElement ele_extension_presence_enb_ring1_endpoints_checkBox = $(By.id("extension_presence_enb_ring1_endpoints"));
    SelenideElement ele_extension_presence_enb_ring1_mobile_client_checkBox = $(By.id("extension_presence_enb_ring1_mobile_client"));
    SelenideElement ele_extension_presence_enb_ring1_desktop_client_checkBox = $(By.id("extension_presence_enb_ring1_desktop_client_forward"));
    SelenideElement ele_extension_presence_enb_ring2_endpoints_checkBox = $(By.id("extension_presence_enb_ring2_endpoints"));
    SelenideElement ele_extension_presence_enb_ring2_mobile_client_checkBox = $(By.id("extension_presence_enb_ring2_mobile_client"));
    SelenideElement ele_extension_presence_enb_ring2_desktop_client_checkBox = $(By.id("extension_presence_enb_ring2_desktop_client_forward"));
    //Tab Presence->Ring Timeout
    SelenideElement ele_extension_presence_ring_timeout_combobox = $(By.id("extension_presence_ring_timeout"));
    SelenideElement ele_extension_presence_ring_timeout_input = $(By.xpath("//div[@id='extension_presence_ring_timeout']//input[@type='text']"));
    //Tab Presence->Options->Ring the Mobile Number Simultaneously
    SelenideElement ele_extension_presence_ring_simultaneously_checkBox = $(By.id("extension_presence_enb_ring_mobile"));
    SelenideElement ele_extension_presence_ring_simultaneously_prefix_input = $(By.id("extension_presence_mobile_prefix"));
    //Tab Presence->Options->Agent Status Auto Switch
    SelenideElement ele_extension_presence_dynamic_agent_action = $(By.id("extension_presence_dynamic_agent_action"));


    /** 下拉列表 目的地********/
    enum CALL_FORWARDING_DESTINATION {
        HANG_UP("Hang Up"),
        EXTENSION("Extension"),
        VOICEMAIL("Voicemail"),
        IVR("IVR"),
        RING_GROUP("Ring Group"),
        QUEUE("Queue"),
        MOBILE_NUMBER("Mobile Number"),
        EXTERNAL_NUMBER("External Number"),
        PLAY_GREETING_THEN_HANG_UP("Play Greeting then Hang up");

        private final String alias;

        CALL_FORWARDING_DESTINATION(String alias) {
            this.alias = alias;
        }

        public String getAlias() {

            return alias;
        }
    }

    enum AGENT_STATUS_AUTO_SWITCH{
        DO_NOTHING(UI_MAP.getString("extension_trunk.extensions.no_action")),
        LOGIN(UI_MAP.getString("extension_trunk.extensions.login")),
        LOGOUT(UI_MAP.getString("extension_trunk.extensions.logout")),
        PAUSE(UI_MAP.getString("extension_trunk.extensions.pause"));

        private final String alias;

        AGENT_STATUS_AUTO_SWITCH(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            ele_extension_presence_dynamic_agent_action.shouldBe(Condition.enabled).click();
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
    SelenideElement ele_extension_voicemail_available_combobox = $(By.id("extension_voicemail_voicemail_available"));
    SelenideElement ele_extension_voicemail_away_combobox = $(By.id("extension_voicemail_voicemail_away"));
    SelenideElement ele_extension_voicemail_doNotDisturb_combobox = $(By.id("extension_voicemail_voicemail_do_not_disturb"));
    SelenideElement ele_extension_voicemail_lunchBreak_combobox = $(By.id("extension_voicemail_voicemail_launch"));
    SelenideElement ele_extension_voicemail_businessTrip_combobox = $(By.id("extension_voicemail_voicemail_business_trip"));
    SelenideElement ele_extension_voicemail_offWork_combobox = $(By.id("extension_voicemail_voicemail_off_work"));
    SelenideElement ele_greeting_management_btn = $(By.xpath("//a[contains(text(),'"+UI_MAP.getString("extension_trunk.extensions.greeting_management")+"')]"));
    SelenideElement ele_extension_voicemail_management_filename_input = $(By.id("extension_voicemail_management_filename"));
    SelenideElement ele_extension_voicemail_management_record_phone_extension_combobox = $(By.id("extension_voicemail_management_record_phone_extension"));
    SelenideElement ele_voicemail_management_save_btn = $(By.xpath("//button[@class='ant-btn modal-footer-btn ant-btn-primary']"));
    /** 下拉列表 Greeting Management管理 **/
    enum VOICEMAIL_GREETING_MANAGEMENT{
        GREETING_MANAGEMENT(UI_MAP.getString("extension_trunk.extensions.greeting_management")),
        RECORD_NEW_GREETING(UI_MAP.getString("extension_trunk.extensions.record_phone"));

        private final String alias;

        VOICEMAIL_GREETING_MANAGEMENT(String alias){
            this.alias = alias;
        }

        public String getAlias() {
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
        SEND_EMAIL_NOTIFICATIONS_WITH_ATTACHMENT(UI_MAP.getString("extension_trunk.extensions.with_attach")),
        DO_NOT_SEND_EMAIL_NOTIFICATIONS(UI_MAP.getString("extension_trunk.extensions.no")),
        SEND_EMAIL_NOTIFICATIONS_WITHOUT_ATTACHMENT(UI_MAP.getString("extension_trunk.extensions.without_attach"));
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
        MARK_AS_READ(UI_MAP.getString("extension_trunk.extensions.mark_read")),
        DELETE_VOICEMAIL(UI_MAP.getString("extension_trunk.extensions.delete")),
        DO_NOTHING(UI_MAP.getString("extension_trunk.extensions.no_action"));
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

}
