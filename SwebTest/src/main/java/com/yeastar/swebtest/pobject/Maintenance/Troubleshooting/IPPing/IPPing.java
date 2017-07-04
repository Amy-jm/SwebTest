package com.yeastar.swebtest.pobject.Maintenance.Troubleshooting.IPPing;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class IPPing {
    public SelenideElement host = $(By.id("mt-ip-host-inputEl"));
    public SelenideElement start = $(By.id("mt-ip-start-btnInnerEl"));
    public SelenideElement stop = $(By.id("mt-ip-stop-btnInnerEl"));

}
