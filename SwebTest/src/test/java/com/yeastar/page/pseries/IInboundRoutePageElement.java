package com.yeastar.page.pseries;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public interface IInboundRoutePageElement {

    /**查询输入框*/
    SelenideElement searchIpt = $(By.xpath("//input[@placeholder='Name/DID/Caller ID...']"));

    SelenideElement ele_import_btn = $(By.xpath("//span[contains(text(),'Import')]"));
    SelenideElement ele_add_btn = $(By.xpath("//span[contains(text(),'Add')]"));
    SelenideElement ele_delete_btn = $(By.xpath("//span[contains(text(),'Delete')]"));
    SelenideElement ele_delete_all_checkbox = $(By.xpath("//table//thead//input[1]"));


    SelenideElement ele_edit_name = $(By.id("inbound_routes_name"));
    SelenideElement ele_edit_did_match_mode = $(By.id("inbound_routes_did_option"));
//    SelenideElement ele_
}
