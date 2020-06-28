package com.yeastar.page.pseries;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public interface IPreferencesPage {

    //Max Call Duration(s)
    SelenideElement ele_pbx_settings_preferences_max_call_duration_select = $(By.xpath("//*[@id=\"pbx_settings_preferences_max_call_duration\"]//input"));
}
