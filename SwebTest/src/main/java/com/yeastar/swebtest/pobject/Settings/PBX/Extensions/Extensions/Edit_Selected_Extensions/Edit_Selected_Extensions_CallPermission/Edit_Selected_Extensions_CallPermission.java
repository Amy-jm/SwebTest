package com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Edit_Selected_Extensions.Edit_Selected_Extensions_CallPermission;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Edit_Selected_Extensions_CallPermission {
    /**
     * 进入Call Permission
     */
    public SelenideElement callPermission = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Call Permission\"]"));

    public SelenideElement outboundRoutes_checkbox = $(By.xpath(".//label[starts-with(@class,\"x-form-cb-label x-form-cb-label-default x-form-cb-label-after  \") and text()=\"Outbound Routes\"]"));

    public SelenideElement save = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Cancel\"]"));

}
