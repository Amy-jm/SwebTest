package com.yeastar.swebtest.pobject.Me.Me_Blacklist_Whitelist.Me_Whitelist.Me_Add_Whitelist;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/7/21.
 */
public class Me_Add_Whitelist {



    public String routeTypeId = "me-bw-type";

    public String routeType_Both = "both";
    public String routeType_Inbound = "inbound";
    public String routeType_Outbound = "outbound";

    public SelenideElement name = $(By.id("me-bw-name-inputEl"));
    public SelenideElement number = $(By.id("me-bw-numbers-inputEl"));
    public SelenideElement type = $(By.id("me-bw-type-trigger-picker"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'meblacklist-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'meblacklist-edit-')]//span[text()='Cancel']"));

}
