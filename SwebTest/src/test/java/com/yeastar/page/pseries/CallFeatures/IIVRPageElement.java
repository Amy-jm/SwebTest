package com.yeastar.page.pseries.CallFeatures;

import com.codeborne.selenide.SelenideElement;
import org.apache.tools.ant.taskdefs.Sleep;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public interface IIVRPageElement {

    /** IVR编辑页面Tab选项 **/
    enum IVR_TAB {
        BASIC("Basic"),
        KEY_PRESS_EVENT("Key Press Event");

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
        HANGUP("Hang Up"),
        EXTENSION("Extension"),
        EXTENSION_VOICEMAIL("Extension Voicemail"),
        IVR("IVR"),
        RING_GROUP("Ring Group"),
        QUEUE("Queue"),
        DIAL_BY_NAME("Dial by Name"),
        EXTERNAL_NUMBER("External Number"),
        PLAY_PROMPT_AND_EXIT("Play Prompt and Exit");

        private final String alias;

        IVR_KEY_PRESS_EVENT(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            return alias;
        }
    }

    SelenideElement searchIpt = $(By.xpath("//input[@placeholder=\"Search\"]"));
    SelenideElement ele_delete_all_checkbox = $(By.xpath("//table//thead//input[1]"));

    SelenideElement ele_ivr_basic_number = $(By.id("ivr_basic_number"));
    SelenideElement ele_ivr_basic_name = $(By.id("ivr_basic_name"));

    SelenideElement ele_ivr_key_press_event_press0_dest = $(By.id("ivr_key_press_event_press0_dest"));
    SelenideElement ele_ivr_key_press_event_press0_dest_value = $(By.id("ivr_key_press_event_press0_dest_value"));
}
