package com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Bulk_Extensions.Add_Bulk_Extensions_CallPermission;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Bulk_Extensions_CallPermission {
    /**
     * 进入Call Permission
     */
    public SelenideElement callPermission = $(By.xpath(".//div[starts-with(@id,\"extension-edit\")]//span[ text()=\"Call Permission\"]"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,\"extension-edit\")]//span[ text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,\"extension-edit\")]//span[ text()=\"Cancel\"]"));
}
