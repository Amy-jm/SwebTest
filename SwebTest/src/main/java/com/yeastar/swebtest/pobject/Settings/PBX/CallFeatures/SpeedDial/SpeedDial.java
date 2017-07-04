package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.SpeedDial;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class SpeedDial {
    public SelenideElement speedDial = $(By.xpath(".//span[starts-with(@class,\"toolbartip\") and text()=\"Speed Dial\"]"));

    /**
     * 功能按钮
     */

    public SelenideElement add = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Add\"]"));
    public SelenideElement delete = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Delete\"]"));
    public SelenideElement Import = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Import\"]"));
    public SelenideElement export = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Export\"]"));
    public SelenideElement speedDialPrefix = $(By.id("st-speeddial-speeddial-inputEl"));
    public SelenideElement speedDialPrefix_button = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));

    /**
     * 页码功能
     */
    public SelenideElement firstPage = $(By.xpath(".//*[@data-qtip='First Page']"));
    public SelenideElement previousPage = $(By.xpath(".//*[@data-qtip='Previous Page']"));
    public SelenideElement nextPage = $(By.xpath(".//*[@data-qtip='Next Page']"));
    public SelenideElement lastPage = $(By.xpath(".//*[@data-qtip='Last Page']"));
    public SelenideElement refresh = $(By.xpath(".//*[@data-qtip='Refresh']"));
    public SelenideElement gotoinput = $(By.name("gotoinput"));
    public SelenideElement go = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-toolbar-small\") and text()=\"Go\"]"));
    public SelenideElement selectPage = $(By.id(".//*[(@data-ref='bodyEl') and (@role='presentation')]"));

}
