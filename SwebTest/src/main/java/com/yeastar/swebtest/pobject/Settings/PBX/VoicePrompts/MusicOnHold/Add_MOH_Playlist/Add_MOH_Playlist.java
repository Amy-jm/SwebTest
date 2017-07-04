package com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.MusicOnHold.Add_MOH_Playlist;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;


import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_MOH_Playlist {
    public SelenideElement name = $(By.id("st-moh-name-inputEl"));
    public SelenideElement playlistOrder = $(By.id("st-moh-playsort-trigger-picker"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
