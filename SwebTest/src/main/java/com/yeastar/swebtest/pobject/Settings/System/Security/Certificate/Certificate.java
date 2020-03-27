package com.yeastar.swebtest.pobject.Settings.System.Security.Certificate;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Certificate {
   public SelenideElement certificate = $(By.xpath(".//div[starts-with(@id,'security')]//span[ text()='Certificate']"));

    /**
     * 功能按钮
     */
    public SelenideElement upload = $(By.xpath(".//div[starts-with(@id,'certificates')]//span[ text()='Upload']"));
    public SelenideElement delete = $(By.xpath(".//div[starts-with(@id,'certificates')]//span[ text()='Delete']"));

    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));

}
