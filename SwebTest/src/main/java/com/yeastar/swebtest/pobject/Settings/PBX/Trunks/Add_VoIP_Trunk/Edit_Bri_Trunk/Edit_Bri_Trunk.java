package com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Add_VoIP_Trunk.Edit_Bri_Trunk;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/8/15.
 */
public class Edit_Bri_Trunk {
    public String trunkName = "trunkname";
    public String signalling = "signalling";
    public String switchside = "switchside";
    public String switchtype = "switchtype";



    public SelenideElement singal_Role = $(By.id("switchside-inputEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'editbri')]//span[text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'editbri')]//span[text()=\"Cancel\"]"));
}
