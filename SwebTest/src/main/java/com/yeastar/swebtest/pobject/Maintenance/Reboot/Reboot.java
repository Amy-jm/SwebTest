package com.yeastar.swebtest.pobject.Maintenance.Reboot;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Reboot {
    public SelenideElement reboot = $(By.id("mt-rr-reboot-btnInnerEl"));
    public SelenideElement enableAutoReboot = $(By.id("mt-rr-enable-displayEl"));
    public SelenideElement timeType = $(By.id("mt-rr-timetype-trigger-picker"));
    public SelenideElement timeTime = $(By.id("mt-rr-timetime-trigger-picker"));

}
