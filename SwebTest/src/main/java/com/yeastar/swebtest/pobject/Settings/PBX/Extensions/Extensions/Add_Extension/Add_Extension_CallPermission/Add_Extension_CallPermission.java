package com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Extension.Add_Extension_CallPermission;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Extension_CallPermission {
    /**
     * 进入CallPermission
     */
    public SelenideElement callPermission = $(By.xpath(".//div[starts-with(@id,'extension-edit')]//span[ text()='Call Permission']"));

    /**
     * Outbound Routes
     */

    public String outboundRestriction = "st-extension-callconstraint";

    public SelenideElement save = $(By.xpath(" .//div[starts-with(@id,'extension-edit')]//span[ text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'extension-edit')]//span[text()='Cancel']"));

}
