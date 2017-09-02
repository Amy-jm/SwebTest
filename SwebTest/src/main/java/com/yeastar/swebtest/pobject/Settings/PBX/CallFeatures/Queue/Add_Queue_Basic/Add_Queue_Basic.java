package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Queue.Add_Queue_Basic;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_Queue_Basic {

    public String list_AddQueue = "st-queue-agents";

    public SelenideElement number = $(By.id("st-queue-number-inputEl"));
    public SelenideElement name = $(By.id("st-queue-name-inputEl"));
    public SelenideElement password = $(By.id("st-queue-pass-inputEl"));
    public SelenideElement ringStrategy = $(By.id("st-queue-ringstrategy-trigger-picker"));
    public SelenideElement failoverDestination = $(By.id("st-queue-failoveraction-trigger-picker"));


    public SelenideElement agentTimeout = $(By.id("st-queue-agenttimeout-inputEl"));
    public SelenideElement ringInUse = $(By.id("st-queue-ringinuse-displayEl"));
    public SelenideElement agentAnnouncement = $(By.id("id=\"st-queue-agentannounce-trigger-picker\""));
    public SelenideElement retry = $(By.id("st-queue-retry-inputEl"));
    public SelenideElement wrap_upTime = $(By.id("st-queue-wrapuptime-inputEl"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'queue-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'queue-edit-')]//span[text()='Cancel']"));

}
