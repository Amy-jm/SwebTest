package com.yeastar.page.pseries.CallFeatures;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.swebtest.driver.DataReader2.UI_MAP;

public interface IRingGroupPageElement {

    SelenideElement searchIpt = $(By.xpath("//input[@placeholder="+UI_MAP.getString("header.search_placeholder")+"]"));
    SelenideElement ele_delete_all_checkbox = $(By.xpath("//table//thead//input[1]"));

    SelenideElement ele_ring_group_number_input = $(By.id("ring_group_number"));
    SelenideElement ele_ring_group_name_input = $(By.id("ring_group_name"));
    SelenideElement ele_ring_group_ring_strategy_combobox = $(By.id("ring_group_ring_strategy"));
    SelenideElement ele_ring_group_ring_timeout_combobox = $(By.id("ring_group_ring_timeout"));
    SelenideElement ele_ring_group_ring_timeout_input = $(By.xpath("//div[@id='ring_group_ring_timeout']//input[@class='ant-input']"));

    String ele_ring_group_transfer_left_extension_str = "transfer_left_ring_group_extension";
    String ele_ring_group_transfer_right_extension_str = "transfer_right_ring_group_extension";
    SelenideElement ele_ring_group_transfer_left_search_input = $(By.xpath("//div[@id='transfer_left_ring_group_extension']/../..//input[@placeholder='Search here']"));
    SelenideElement ele_ring_group_transfer_right_search_input = $(By.xpath("//div[@id='transfer_right_ring_group_extension']/../..//input[@placeholder='Search here']"));
    SelenideElement ele_ring_group_add_extension_member = $(By.xpath("//div[@class='ant-col ant-col-sm-24 ant-col-md-12 ant-col-lg-20']//button[1]"));
    SelenideElement ele_ring_group_del_extension_member = $(By.xpath("//div[@class='ant-col ant-col-sm-24 ant-col-md-12 ant-col-lg-20']//button[2]"));
    SelenideElement ele_ring_group_top_extenson_member = $(By.xpath("//div[@class='ant-col ant-col-sm-24 ant-col-md-12 ant-col-lg-4']//button[1]"));
    SelenideElement ele_ring_group_up_extension_menber = $(By.xpath("//div[@class='ant-col ant-col-sm-24 ant-col-md-12 ant-col-lg-4']//button[2]"));
    SelenideElement ele_ring_group_down_extenson_member = $(By.xpath("//div[@class='ant-col ant-col-sm-24 ant-col-md-12 ant-col-lg-4']//button[3]"));
    SelenideElement ele_ring_group_bottom_extension_menber = $(By.xpath("//div[@class='ant-col ant-col-sm-24 ant-col-md-12 ant-col-lg-4']//button[4]"));

    SelenideElement ele_ring_group_destination_fail_dest_combobox = $(By.id("destination_fail_dest"));
    SelenideElement ele_ring_group_destination_presence_forward_fail_dest_value = $(By.id("destination_presence_forward_fail_dest_value"));
    SelenideElement ele_ring_group_destination_play_fail_dest_prefix = $(By.id("destination_play_fail_dest_prefix"));
}
