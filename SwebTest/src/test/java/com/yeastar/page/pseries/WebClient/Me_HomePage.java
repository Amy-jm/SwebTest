package com.yeastar.page.pseries.WebClient;

import com.codeborne.selenide.SelenideElement;
import com.yeastar.untils.WaitUntils;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

public class Me_HomePage {

    /**
     * 左侧菜单  //a[contains(@id,"m_w_preferences")]/..
     **/
    public SelenideElement left_menu_first_level_extension = $(By.id("m_w_extension"));//m_w_extension
    public SelenideElement left_menu_first_level_visual_control_panel = $(By.id("m_w_visual_control_panel"));//m_w_visual_control_panel
    public SelenideElement left_menu_first_level_call_log = $(By.id("m_w_call_log"));//m_w_call_log
    public SelenideElement left_menu_first_level_voicemails = $(By.xpath("//a[contains(@id,\"m_w_voicemail\")]/.."));//m_w_voicemail
    public SelenideElement left_menu_first_level_recordings = $(By.id("m_w_recordings"));//m_w_recordings
    public SelenideElement left_menu_first_level_preferences = $(By.id("m_w_preferences"));//m_w_preferences
    public SelenideElement left_menu_first_level_queue_live = $(By.id("m_w_queue_live"));//m_w_queue_live
    public SelenideElement queue_live_wallboard = $(By.id("m_w_wallBoard"));
    public SelenideElement queue_live_queue_panel = $(By.id("m_w_queue_calls"));

    /**
     * Vociemail
     */
    public SelenideElement voicemail_mark_read = $(By.xpath("//span[contains(text(),'Mark read')]"));
    public SelenideElement voicemail_delete = $(By.xpath("//span[contains(text(),'Delete')]"));



    /**
     * 左侧 一 二级菜单
     **/
    public enum Menu_Level_1 {
        extension,
        visual_control_panel,
        call_log,
        voicemails,
        recordings,
        preferences,
        queue_live;
    }
    public enum Menu_Level_2 {
        none,
        wallboard,
        queue_panel;
    }
    /**
     * 系统提示信息
     */
    public SelenideElement system_alert_message = $(By.xpath("//a[contains(@class,'close')]"));
    /**
     * 菜单选择
     */
    public void intoPage(Me_HomePage.Menu_Level_1 level_1) {
        intoPage(level_1,Menu_Level_2.none);
    }
    public void intoPage(Me_HomePage.Menu_Level_1 level_1, Me_HomePage.Menu_Level_2 level_2) {
        sleep(WaitUntils.SHORT_WAIT*2);
        //close alert
        // todo need to delete sleep
        while(system_alert_message.exists()){
            system_alert_message.click();
            sleep(2000);
        }
        //左侧一级菜单
        switch (level_1) {
            case extension:
                left_menu_first_level_extension.click();
                break;
            case visual_control_panel:
                left_menu_first_level_visual_control_panel.click();
                break;
            case call_log:
                left_menu_first_level_call_log.click();
                break;
            case voicemails:
                left_menu_first_level_voicemails.click();
                break;
            case recordings:
                left_menu_first_level_recordings.click();
                break;
            case preferences:
                left_menu_first_level_preferences.click();
                break;
            case queue_live:
                left_menu_first_level_queue_live.click();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + level_1);
        }

        //左侧二级菜单
        switch (level_2) {
            case none:
                break;
            case wallboard:
                queue_live_wallboard.click();
                break;
            case queue_panel:
                queue_live_queue_panel.click();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + level_2);
        }


    }



}
