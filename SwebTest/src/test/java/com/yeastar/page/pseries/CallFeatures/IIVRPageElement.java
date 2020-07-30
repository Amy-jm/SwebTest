package com.yeastar.page.pseries.CallFeatures;

import com.codeborne.selenide.SelenideElement;
import org.apache.tools.ant.taskdefs.Sleep;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.swebtest.driver.DataReader2.UI_MAP;

public interface IIVRPageElement {

    /** IVR编辑页面Tab选项 **/
    enum IVR_TAB {
        BASIC("Basic"),
        KEY_PRESS_EVENT(UI_MAP.getString("call_features.ivr.key_press_event"));

        private final String alias;

        IVR_TAB(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            return alias;
        }
    }

    /** IVR Key Press Event选项**/
    enum IVR_KEY_PRESS_EVENT{
        NONE("[None]"),
        HANGUP(UI_MAP.getString("call_features.ivr.end_call")),
        EXTENSION(UI_MAP.getString("call_features.ivr.extension")),
        EXTENSION_VOICEMAIL(UI_MAP.getString("call_features.ivr.ext_vm")),
        IVR(UI_MAP.getString("call_features.ivr.ivr")),
        RING_GROUP("Ring Group"),
        QUEUE("Queue"),
        DIAL_BY_NAME(UI_MAP.getString("call_features.ivr.dial_by_name")),
        EXTERNAL_NUMBER(UI_MAP.getString("call_features.ivr.external_num")),
        PLAY_PROMPT_AND_EXIT(UI_MAP.getString("call_features.ivr.play_greeting"));

        private final String alias;

        IVR_KEY_PRESS_EVENT(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            return alias;
        }
    }

    SelenideElement searchIpt = $(By.xpath("//input[@placeholder="+UI_MAP.getString("header.search_placeholder")+"]"));
    SelenideElement ele_delete_all_checkbox = $(By.xpath("//table//thead//input[1]"));

    SelenideElement ele_ivr_basic_number = $(By.id("ivr_basic_number"));
    SelenideElement ele_ivr_basic_name = $(By.id("ivr_basic_name"));

    SelenideElement ele_ivr_key_press_event_press0_dest = $(By.id("ivr_key_press_event_press0_dest"));
    SelenideElement ele_ivr_key_press_event_press0_dest_value = $(By.id("ivr_key_press_event_press0_dest_value"));
}
