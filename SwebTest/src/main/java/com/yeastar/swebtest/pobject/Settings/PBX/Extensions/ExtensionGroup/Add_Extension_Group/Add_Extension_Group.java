package com.yeastar.swebtest.pobject.Settings.PBX.Extensions.ExtensionGroup.Add_Extension_Group;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Extension_Group {

    public String list_ExtensionGroup = "st-eg-groupmember";

    public SelenideElement name = $(By.id("st-eg-groupname-inputEl"));

    public SelenideElement save = $(By.xpath(" .//div[starts-with(@id,'extensiongroup-edit')]//span[ text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'extensiongroup-edit')]//span[text()='Cancel']"));

}
