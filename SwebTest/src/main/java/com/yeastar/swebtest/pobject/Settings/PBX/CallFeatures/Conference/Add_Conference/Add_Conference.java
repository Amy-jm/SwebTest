package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Conference.Add_Conference;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_Conference {
    

    public SelenideElement number = $(By.id("st-conference-roomnumber-inputEl"));
    public SelenideElement name = $(By.id("st-conference-roomname-inputEl"));
    public SelenideElement participantPassword = $(By.id("st-conference-pin-inputEl"));
    public SelenideElement waitforModeretor = $(By.id("st-conference-waitforadmin-displayEl"));
    public SelenideElement soundPrompt = $(By.id("st-conference-prompt-trigger-picker"));
    public SelenideElement allowParticipanttoInvite = $(By.id("st-conference-enableinvite-displayEl"));
    public SelenideElement moderatorPassword = $(By.id("st-conference-pinadmin-inputEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'conference-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'conference-edit-')]//span[text()='Cancel']"));



}
