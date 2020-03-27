package com.yeastar.swebtest.pobject.Settings.System.Network.StaticRoutes.RoutingTable;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class RoutingTable {
    public SelenideElement staticRoutes = $(By.xpath(".//div[starts-with(@id,'staticroutes')]//span[ text()='Routing Table']"));

}
