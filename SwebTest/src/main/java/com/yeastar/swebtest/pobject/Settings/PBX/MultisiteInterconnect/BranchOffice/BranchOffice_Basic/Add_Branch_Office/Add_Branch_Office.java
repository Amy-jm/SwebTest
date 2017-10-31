package com.yeastar.swebtest.pobject.Settings.PBX.MultisiteInterconnect.BranchOffice.BranchOffice_Basic.Add_Branch_Office;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/10/10.
 */
public class Add_Branch_Office {

    public SelenideElement hostnameIp = $(By.id("st-multisite-hostname-inputEl"));
    public SelenideElement port = $(By.id("st-multisite-port-inputEl"));
    public SelenideElement account = $(By.xpath("st-multisite-username-inputEl"));
    public SelenideElement password = $(By.xpath("st-multisite-secret-inputEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,\"multisitebbasic-edit\")]//span[ text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,\"multisitebbasic-edit\")]//span[ text()=\"Cancel\"]"));
}
