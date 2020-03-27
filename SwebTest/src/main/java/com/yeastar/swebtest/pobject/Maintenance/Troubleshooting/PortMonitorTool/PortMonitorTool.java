package com.yeastar.swebtest.pobject.Maintenance.Troubleshooting.PortMonitorTool;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class PortMonitorTool {
    public SelenideElement port = $(By.id("mt-dm-channel-trigger-picker"));

    public SelenideElement start = $(By.id("mt-dm-start-btnInnerEl"));
    public SelenideElement stop = $(By.id("mt-dm-stop-btnInnerEl"));
    public SelenideElement download = $(By.id("mt-dm-download-btnInnerEl"));






}
