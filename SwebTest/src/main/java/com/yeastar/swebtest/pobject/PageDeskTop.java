package com.yeastar.swebtest.pobject;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.*;

/**
 * Created by GaGa on 2017-03-29.
 */
public class PageDeskTop {

    public SelenideElement taskBar_User = $(By.xpath(".//*[@src='images/taskbar-user.png']"));
    public SelenideElement taskBar_User_Logout = $(By.xpath(".//span[text()='Logout']"));
    public SelenideElement messageBox_Yes = $(By.xpath(".//span[text()=\"Yes\" and @data-ref=\"btnInnerEl\"]"));
    public SelenideElement messageBox_No = $(By.xpath(".//span[text()=\"No\" and @data-ref=\"btnInnerEl\"]"));

    public SelenideElement taskBar_Main = $(By.xpath(".//*[@src='images/taskbar-main.png']"));

    public SelenideElement apply = $(By.xpath(".//a[starts-with(@class,'css_apply') and text()='Apply']"));


    public SelenideElement settings = $(By.name("control-panel"));
    public SelenideElement CDRandRecording = $(By.name("cdr-record"));
    public SelenideElement maintenance = $(By.name("maintance"));

}
