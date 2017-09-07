package com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.MusicOnHold.Add_MOH_Playlist;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;


import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_MOH_Playlist {

    public int  playlistOrder_Random  = 1;
    public int playlistOrder_alphabetical =2;


    public SelenideElement name = $(By.id("st-moh-name-inputEl"));
    public SelenideElement playlistOrder = $(By.id("st-moh-playsort-trigger-picker"));

    public SelenideElement mohPlayList = $(By.id("st-moh-choosefolder-inputEl"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'moh-edi')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'moh-edi')]//span[text()='Cancel']"));

}
