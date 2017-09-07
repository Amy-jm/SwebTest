package com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.SystemPrompt;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class SystemPrompt {
    public String grid = "Ext.getCmp('control-panel').down('systemprompt').down('grid')";
    public String gridLoading = "Ext.getCmp('control-panel').down('systemprompt').down('grid').down('loadmask')";

    public int Default = 0;
    public int gridColumn_Language = 1;
    public int gridDelete = 0;

    public SelenideElement systemPrompt = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"System Prompt\"]"));

    public SelenideElement file = $(By.id("st-sp-promptfile-inputEl"));
    public SelenideElement browse = $(By.id("st-sp-promptfile-trigger-filebutton"));
    public SelenideElement upload = $(By.id("st-sp-upload-btnInnerEl"));
    public SelenideElement downloadOnlinePrompt = $(By.id("st-sp-packagemanage-btnInnerEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'systemprompt-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'systemprompt-')]//span[text()='Cancel']"));

    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));

}
