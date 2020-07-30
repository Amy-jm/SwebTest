package com.yeastar.page.pseries.PbxSettings;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.swebtest.driver.DataReader2.UI_MAP;

public interface IPreferencesPageElement {
    SelenideElement ele_name_display_format_combobox = $(By.id("pbx_settings_preferences_name_disp_fmt"));
    //Max Call Duration(s)
    SelenideElement ele_pbx_settings_preferences_max_call_duration_select = $(By.xpath("//*[@id=\"pbx_settings_preferences_max_call_duration\"]//input"));
    enum NAME_DISPLAY_FORMAT{
        FIRST_LAST_WITH_SPACE(UI_MAP.getString("pbx_settings.preferences.first_last")),
        FIRST_LAST_WITHOUT_SPACE(UI_MAP.getString("pbx_settings.preferences.last_first")),
        LAST_FIRST_WITHOUT_SPACE(UI_MAP.getString("pbx_settings.preferences.lastfirst"));

        private final String alias;

        NAME_DISPLAY_FORMAT(String alias){
            this.alias = alias;
        }

        public String getAlias() {
            ele_name_display_format_combobox.shouldBe(Condition.enabled).click();
            return alias;
        }
    }
}
