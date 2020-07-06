package com.yeastar.page.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

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
    SelenideElement ele_extension_user_role_id = $(By.id("extension_user_role_id"));
//    SelenideElement ele_extension_user_first_name = $(By.id("extension_user_first_name"));

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
    SelenideElement ele_add_user_agent_btn =$(By.xpath("//span[contains(text(),\"Add User Agent\")]"));

    //-SIP User Agent Identification
    ElementsCollection ele_list_permitted_ip_input = $$(By.id("permitted_ip"));
    ElementsCollection ele_list_netmask_input = $$(By.id("netmask"));
    //--Add User Agent btn
    SelenideElement ele_add_ip_btn =$(By.xpath("//span[contains(text(),\"Add IP\")]"));

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
       FOLLOWSYSTEM("[Follow System]"),
       UNLIMITED("Unlimited"),
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
        RFC4733RFC2833("RFC4733"),
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

}
