package com.yeastar.swebtest.pobject.Maintenance.Troubleshooting.EthernetCaptureTool;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class EthernetCaptureTool {
    public SelenideElement ethernetInterface = $(By.id("mt-ec-iface-trigger-picker"));
    public SelenideElement IPAddress = $(By.id("mt-ec-ip-inputEl"));
    public SelenideElement port = $(By.id("mt-ec-port-inputEl"));

    public SelenideElement start = $(By.id("mt-ec-start-btnInnerEl"));
    public SelenideElement stop = $(By.id("mt-ec-stop-btnInnerEl"));
    public SelenideElement download = $(By.id("mt-ec-download-btnInnerEl"));



}
