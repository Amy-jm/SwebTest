package com.yeastar.pageObject.pSeries;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public interface ExtensionPageElementImpl {
    SelenideElement searchInput = $(By.xpath("//input[@placeholder=\"Search\"]"));
    SelenideElement add_DropDown_bulk_add_Btn = $(By.xpath("//li[text()='Bulk Add']"));
    SelenideElement add_DropDown_add_Btn = $(By.xpath("//li[text()='Add']"));
    SelenideElement delete_all_checkbox = $(By.xpath("//table//thead//input[1]"));

    //Tab标签
    String TAB_COMM_XPATH = "//div[contains(@role,'tab') and contains(text(),\"%s\")]";
    SelenideElement tab_user = $(By.xpath(String.format(TAB_COMM_XPATH, "User")));
    SelenideElement tab_presence = $(By.xpath(String.format(TAB_COMM_XPATH, "Presence")));
    SelenideElement tab_voicemail = $(By.xpath(String.format(TAB_COMM_XPATH, "Voicemail")));
    SelenideElement tab_features = $(By.xpath(String.format(TAB_COMM_XPATH, "Features")));
    SelenideElement tab_advanced = $(By.xpath(String.format(TAB_COMM_XPATH, "Advanced")));
    SelenideElement tab_security = $(By.xpath(String.format(TAB_COMM_XPATH, "Security")));
    SelenideElement tab_linkus_clients = $(By.xpath(String.format(TAB_COMM_XPATH, "Linkus Clients")));

    //Tab User basic
    SelenideElement sip_extension_selection = $(By.xpath("//div[contains(text(),\"SIP Extension\")]"));
    SelenideElement fxs_extension_selection = $(By.xpath("//div[contains(text(),\"FXS Extension\")]"));


    //密码强度不够弹框
     SelenideElement registration_password_not_strong_alert = $(By.xpath("//div[contains(text(),\"Registration Password is not strong, continue to save\")]"));
     SelenideElement extension_list_warning_registration_warning_img = $(By.xpath("//i[contains(@aria-label,\"icon: warning\") and contains(@title,\"注册密码强度弱\")]"));


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
    SelenideElement extension_presence_forward_enb_in_always_forward_checkBox = $(By.id("extension_presence_forward_enb_in_always_forward"));
    SelenideElement extension_presence_forward_enb_in_no_answer_forward_checkBox = $(By.id("extension_presence_forward_enb_in_no_answer_forward"));
    SelenideElement extension_presence_forward_enb_in_busy_forward_checkBox = $(By.id("extension_presence_forward_enb_in_busy_forward"));
    //Tab Presence->External Calls
    SelenideElement extension_presence_forward_enb_ex_always_forward_checkBox = $(By.id("extension_presence_forward_enb_ex_always_forward"));
    SelenideElement extension_presence_forward_enb_ex_no_answer_forward_checkBox = $(By.id("extension_presence_forward_enb_ex_no_answer_forward"));
    SelenideElement extension_presence_forward_enb_ex_busy_forward_checkBox = $(By.id("extension_presence_forward_enb_ex_busy_forward"));
    //Tab Presence->Ring Strategy
    SelenideElement extension_presence_enb_ring1_endpoints_checkBox = $(By.id("extension_presence_enb_ring1_endpoints"));
    SelenideElement extension_presence_enb_ring1_mobile_client_checkBox = $(By.id("extension_presence_enb_ring1_mobile_client"));
    SelenideElement extension_presence_enb_ring1_desktop_client_checkBox = $(By.id("extension_presence_enb_ring1_desktop_client_forward"));

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
