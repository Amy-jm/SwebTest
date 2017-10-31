package com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Add_VoIP_Trunk.Add_VoIP_Trunk_Codec;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_VoIP_Trunk_Codec {

    public SelenideElement codec = $(By.xpath(".//div[starts-with(@id,\"editvoip\")]//span[text()=\"Codec\"]"));

//    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
//    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));
    public SelenideElement save = $(By.xpath(" .//div[starts-with(@id,'editvoip')]//span[ text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'editvoip')]//span[text()='Cancel']"));
}
