package com.yeastar.page.pseries.CallControl;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.page.pseries.IButton.buttonLocationXpath;

public interface IOutBoundRoutePageElement {
    //Extension-Extension Group
    /**查询输入框*/
    SelenideElement searchIpt = $(By.xpath("//input[@placeholder='Name/DID/Caller ID...']"));

    SelenideElement ele_import_btn = $(By.xpath(String.format(buttonLocationXpath,"Import")));
    SelenideElement ele_add_btn = $(By.xpath(String.format(buttonLocationXpath,"Add")));
    SelenideElement ele_delete_btn = $(By.xpath(String.format(buttonLocationXpath,"Delete")));
    SelenideElement ele_delete_all_checkbox = $(By.xpath("//table//thead//input[1]"));

    SelenideElement ele_outbound_routes_name_input = $(By.id("outbound_routes_name"));
    SelenideElement ele_outbound_routes_outb_cid_input = $(By.id("outbound_routes_outb_cid"));
    SelenideElement ele_outbound_routes_role_list_combobox = $(By.id("outbound_routes_role_list"));
    SelenideElement ele_outbound_routes_dial_pattern_add_btn = $(By.xpath("//div[@class='add-footer']//span[contains(text(),'Add')]"));
    SelenideElement ele_outbound_routes_dial_pattern_input = $(By.id("dial_pattern"));
    SelenideElement ele_outbound_routes_strip_input = $(By.id("strip"));
    SelenideElement ele_outbound_routes_prepend_input = $(By.id("prepend"));
    SelenideElement ele_outbound_routes_add_trunk_btn = $(By.xpath("//div[3]//div[2]//div[1]//div[1]//div[1]//div[1]//div[1]//div[1]//div[2]//button[1]"));
    SelenideElement ele_outbound_routes_del_trunk_btn = $(By.xpath("//div[3]//div[2]//div[1]//div[1]//div[1]//div[1]//div[1]//div[1]//div[2]//button[2]"));
    SelenideElement ele_outbound_routes_pin_protect_combobox = $(By.id("outbound_routes_pin_protect"));
    SelenideElement ele_outbound_routes_pin_input = $(By.id("outbound_routes_pin"));
    SelenideElement ele_outbound_routes_enb_rrmemory_hunt_checkbox = $(By.id("outbound_routes_enb_rrmemory_hunt"));
    SelenideElement ele_outbound_routes_add_extension_btn = $(By.xpath("//div[4]//div[2]//div[1]//div[1]//div[1]//div[1]//div[1]//div[1]//div[2]//button[1]"));
    SelenideElement ele_outbound_routes_del_extension_btn = $(By.xpath("//div[4]//div[2]//div[1]//div[1]//div[1]//div[1]//div[1]//div[1]//div[2]//button[2]"));
    SelenideElement ele_outbound_routes_available_time_combobox = $(By.id("outbound_routes_available_time"));

}
