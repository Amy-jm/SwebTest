package com.yeastar.swebtest.pobject.Settings.System.Network.StaticRoutes.StaticRoute;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class StaticRoute {
    public SelenideElement staticRoutes = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Static Routes\"]"));

    public SelenideElement add = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Add\"]"));

    public SelenideElement destination = $(By.name("destination"));
    public SelenideElement subnetMask = $(By.name("destination"));
    public SelenideElement gateway = $(By.name("destination"));
    public SelenideElement metric = $(By.name("destination"));
    public SelenideElement Interface = $(By.name("destination"));

    public SelenideElement edit = $(By.xpath(".//span[starts-with(@class,\"x-action-col-icon x-action-col-0  \") and text()=\"Edit\"]"));

    public SelenideElement delete = $(By.xpath(".//span[starts-with(@class,\"x-action-col-icon x-action-col-0  \") and text()=\"Delete\"]"));

}
