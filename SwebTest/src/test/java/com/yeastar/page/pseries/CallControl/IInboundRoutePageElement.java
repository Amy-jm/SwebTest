package com.yeastar.page.pseries.CallControl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.swebtest.driver.DataReader2;
import com.yeastar.untils.UIMapUtils;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.yeastar.swebtest.driver.DataReader2.UI_MAP;

public interface IInboundRoutePageElement {

    String buttonLocationXpath = "//button//span[contains(text(),'%s')]/..";
    /**查询输入框*/

//    SelenideElement searchIpt = $(By.xpath("//input[@placeholder='Name/DID/Caller ID...']"));
    SelenideElement searchIpt = $(By.xpath("//input[@placeholder='"+UI_MAP.getString("call_control.inbound_routes.search_placeholder")+"']"));

    SelenideElement ele_import_btn = $(By.xpath(String.format(buttonLocationXpath,"Import")));
    SelenideElement ele_add_btn = $(By.xpath(String.format(buttonLocationXpath,"Add")));
    SelenideElement ele_delete_btn = $(By.xpath(String.format(buttonLocationXpath,"Delete")));
    SelenideElement ele_delete_all_checkbox = $(By.xpath("//table//thead//input[1]"));

    //DID PATTERN
    SelenideElement ele_inbound_routes_dial_pattern_add_btn = $(By.xpath("//div[@class='add-footer']//span[contains(text(),'Add')]"));
    ElementsCollection ele_inbound_routes_dial_pattern_input = $$(By.id("did_pattern"));


    SelenideElement ele_edit_name = $(By.id("inbound_routes_name"));
    SelenideElement ele_edit_did_match_mode = $(By.id("inbound_routes_did_option"));
    SelenideElement ele_edit_trunk_toRight_btn = $(By.xpath("//div[@class='ant-col ant-col-sm-24 ant-col-md-12 ant-col-lg-20']//button[1]"));
    SelenideElement ele_edit_trunk_toLeft_btn = $(By.xpath("//div[@class='ant-col ant-col-sm-24 ant-col-md-12 ant-col-lg-20']//button[2]"));

    /**Default Destination**/
    SelenideElement ele_edit_default_destination = $(By.id("call_control_destination_def_dest"));
    SelenideElement ele_edit_default_destination2 =$(By.id("inbound_forward_def_dest_value"));
    SelenideElement ele_edit_time_condition = $(By.id("inbound_routes_enb_time_condition"));


    enum DEFAULT_DESTIONATON{
        NONE("[None]"),
        HANG_UP("Hang up"),
        EXTENSION("Extension"),
        EXTENSION_VOICEMAIL("Extension Voicemail"),
        IVR("IVR"),
        RING_GROUP("Ring Group"),
        QUEUE("Queue"),
        CONFERENCE("Conference"),
        EXTERNAL_ROUTE("External Number"),
        OUTBOUND_ROUTE("Outbound Route"),
        PLAY_GREETING_THEN_HANG_UP("Play Greeting then Hang up"),
        FAX_TO_EMAIL("Fax To Email");

        private final String alias;

        DEFAULT_DESTIONATON(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            ele_edit_default_destination.shouldBe(Condition.enabled).click();
            return alias;
        }

    }
}
