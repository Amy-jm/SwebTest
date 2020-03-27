package com.yeastar.swebtest.pobject.Settings.System.Security.IPAutoDefense;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class IPAutoDefense {
    public SelenideElement IPAutoDefense = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"IP Auto Defense\"]"));




}
