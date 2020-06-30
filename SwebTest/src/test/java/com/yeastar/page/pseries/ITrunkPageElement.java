package com.yeastar.page.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import sun.management.Sensor;

import static com.codeborne.selenide.Selenide.$;

public interface ITrunkPageElement {

    SelenideElement ele_trunk_basic_name = $(By.id("trunk_voip_basic_name"));
    SelenideElement ele_trunk_basic_trunk_status_combobox =$(By.id("trunk_voip_basic_enable"));
    SelenideElement ele_trink_basic_itsp_template_combobox = $(By.id("trunk_voip_basic_country"));

    /**Trunk Type 选择框**/
    SelenideElement ele_trunk_basic_trunk_type_combobox = $(By.id("trunk_voip_basic_type"));

    /**Trunk Type选择内容**/
    SelenideElement ele_trunk_basic_transport_combobox = $(By.id("trunk_voip_basic_transport"));
    SelenideElement ele_trunk_basic_hostname = $(By.id("trunk_voip_basic_hostname"));
    SelenideElement ele_trunk_basic_port = $(By.id("trunk_voip_basic_port"));
    SelenideElement ele_trunk_basic_domain = $(By.id("trunk_voip_basic_domain"));
    SelenideElement ele_trunk_basic_username = $(By.id("trunk_voip_basic_username"));
    SelenideElement ele_trunk_basic_password = $(By.id("trunk_voip_basic_password"));
    SelenideElement ele_trunk_basic_authentication = $(By.id("trunk_voip_basic_auth_name"));
    SelenideElement ele_trunk_basic_outbound_proxy_checkbox = $(By.id("trunk_voip_basic_enb_outbound_proxy"));
    SelenideElement ele_trunk_basic_outbound_proxy_server = $(By.id("trunk_voip_basic_outbound_proxy_server"));
    SelenideElement ele_trunk_basic_outbound_proxy_prot = $(By.id("trunk_voip_basic_outbound_proxy_port"));

    /**Account Trunk 选择内容**/
    SelenideElement ele_trunk_basic_accountTrunk_number = $(By.id("trunk_voip_basic_number"));


    /** 下拉列表 用户角色 **/
    enum Trunk_Type {
        RegisterTrunk("Register Trunk"),
        PeerTrunk("Peer Trunk"),
        AccountTrunk("Account Trunk");

        private final String alias;

        Trunk_Type(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            ele_trunk_basic_trunk_type_combobox.shouldBe(Condition.enabled).click();
            return alias;
        }
    }
}
