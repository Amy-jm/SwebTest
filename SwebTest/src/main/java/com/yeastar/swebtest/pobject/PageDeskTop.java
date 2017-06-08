package com.yeastar.swebtest.pobject;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.*;

/**
 * Created by GaGa on 2017-03-29.
 */
public class PageDeskTop {
    public SelenideElement taskBarUser = $(By.xpath(".//*[@src='images/taskbar-user.png']"));
    public SelenideElement taskBarUser_Logout = $(By.xpath(".//span[text()='Logout']"));
    public SelenideElement messageBox_Yes = $(By.xpath(".//span[text()=\"Yes\" and @data-ref=\"btnInnerEl\"]"));
    public SelenideElement messageBox_No = $(By.xpath(".//span[text()=\"No\" and @data-ref=\"btnInnerEl\"]"));
    public SelenideElement mesetting = $(By.name("mesetting"));
}
