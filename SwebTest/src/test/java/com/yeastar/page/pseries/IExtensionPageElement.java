package com.yeastar.page.pseries;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

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
   public SelenideElement ele_extension_user_caller_id = $(By.id("extension_user_caller_id"));
   public SelenideElement ele_extension_user_user_password = $(By.id("extension_user_user_password"));
   public SelenideElement ele_extension_user_number = $(By.id("extension_user_number"));
   public SelenideElement ele_extension_user_reg_name = $(By.id("extension_user_reg_name"));
   public SelenideElement ele_extension_user_reg_password = $(By.id("extension_user_reg_password"));

    //密码强度不够弹框
     SelenideElement ele_registration_password_not_strong_alert = $(By.xpath("//div[contains(text(),\"Registration Password is not strong, continue to save\")]"));
     SelenideElement ele_extension_list_warning_registration_warning_img = $(By.xpath("//i[contains(@aria-label,\"icon: warning\") and contains(@title,\"注册密码强度弱\")]"));


    /** 用户角色 **/
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

    /** 用户角色 **/
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

    //目的地
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