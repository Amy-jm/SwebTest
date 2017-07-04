package com.yeastar.swebtest.pobject.Maintenance.Reset;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Reset {
    public SelenideElement reset = $(By.id("mt-rr-reset-btnInnerEl"));
}
