package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Blacklist_Whitelist;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Blacklist_Whitelist {
    public SelenideElement blacklist_Whitelist = $(By.xpath(".//div[starts-with(@id,\"callfeature\")]//span[ text()=\"Blacklist/Whitelist\"]"));


}
