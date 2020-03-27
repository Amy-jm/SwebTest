package com.yeastar.swebtest.pobject.Settings.PBX.General.SIP;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class SIP {
    public SelenideElement SIP = $(By.xpath(".//div[starts-with(@id,'general')]//span[ text()='SIP']"));

}
