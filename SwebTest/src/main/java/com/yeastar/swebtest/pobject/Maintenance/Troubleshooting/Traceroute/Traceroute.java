package com.yeastar.swebtest.pobject.Maintenance.Troubleshooting.Traceroute;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Traceroute {
    public SelenideElement host = $(By.id("mt-tr-host-inputEl"));
    public SelenideElement start = $(By.id("mt-tr-start-btnInnerEl"));
    public SelenideElement stop = $(By.id("mt-tr-stop-btnInnerEl"));

}
