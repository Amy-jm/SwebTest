package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.IVR.Add_IVR_KeyPressEvent;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_IVR_KeyPressEvent {
    public SelenideElement press0 = $(By.id("key0action-trigger-picker"));
    public SelenideElement press1 = $(By.id("key1action-trigger-picker"));
    public SelenideElement press2 = $(By.id("key2action-trigger-picker"));
    public SelenideElement press3 = $(By.id("key3action-trigger-picker"));
    public SelenideElement press4 = $(By.id("key4action-trigger-picker"));
    public SelenideElement press5 = $(By.id("key5action-trigger-picker"));
    public SelenideElement press6 = $(By.id("key6action-trigger-picker"));
    public SelenideElement press7 = $(By.id("key7action-trigger-picker"));
    public SelenideElement press8 = $(By.id("key8action-trigger-picker"));
    public SelenideElement press9 = $(By.id("key9action-trigger-picker"));
    public SelenideElement pressj = $(By.id("keysharpaction-trigger-picker"));
    public SelenideElement pressx = $(By.id("keystaraction-trigger-picker"));
    public SelenideElement timeout = $(By.id("timeoutaction-trigger-picker"));
    public SelenideElement invaild = $(By.id("invalidaction-trigger-picker"));


    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
