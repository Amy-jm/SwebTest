package com.yeastar.swebtest.pobject.PageSettings;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by GaGa on 2017-05-19.
 */
public class PageSettings {
    public SelenideElement Extensions = $(By.xpath(".//span[starts-with(@class,\"x-tree-node-text\") and text()=\"Extensions\"]"));
    public SelenideElement Trunks = $(By.xpath(".//span[starts-with(@class,\"x-tree-node-text\") and text()=\"Trunks\"]"));
    public SelenideElement CallControl = $(By.xpath(".//span[starts-with(@class,\"x-tree-node-text\") and text()=\"Call Control\"]"));
}
