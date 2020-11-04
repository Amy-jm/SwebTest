package com.yeastar.page.pseries.CallFeatures;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public interface IConference {

    SelenideElement ele_conference_partic_password = $(By.id("conference_partic_password"));
    SelenideElement ele_conference_moderator_password = $(By.id("conference_moderator_password"));

    SelenideElement ele_conference_enb_wait_moderator_checkbox = $(By.id("conference_enb_wait_moderator"));
    SelenideElement ele_conference_allow_partic_invite_checkbox = $(By.id("conference_allow_partic_invite"));
}
