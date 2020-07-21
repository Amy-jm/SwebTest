package com.yeastar.page.pseries.PbxSettings;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public interface IPreferencesPageElement {
    SelenideElement ele_name_display_format_combobox = $(By.id("pbx_settings_preferences_name_disp_fmt"));
    //Max Call Duration(s)
    SelenideElement ele_pbx_settings_preferences_max_call_duration_select = $(By.xpath("//*[@id=\"pbx_settings_preferences_max_call_duration\"]//input"));
    enum NAME_DISPLAY_FORMAT{
        FIRST_LAST_WITH_SPACE("First Name Last Name with Space Inbetween"),
        FIRST_LAST_WITHOUT_SPACE("Last Name First Name with Space Inbetween"),
        LAST_FIRST_WITHOUT_SPACE("Last Name First Name without Space Inbetween");

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
