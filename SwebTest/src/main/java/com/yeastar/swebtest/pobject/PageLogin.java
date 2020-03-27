package com.yeastar.swebtest.pobject;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by GaGa on 2017-03-29.
 */
public class PageLogin {

    public SelenideElement username = $(By.id("login-username-inputEl"));
    public SelenideElement language = $(By.id("login-language-inputEl"));
    public SelenideElement password = $(By.id("login-password-inputEl"));
    public SelenideElement login = $(By.id("login-btn-btnEl"));
    public SelenideElement english = $(By.xpath(".//*[@data-recordindex='1']"));
    public SelenideElement chineseSimpleFied = $(By.xpath(".//*[@data-recordindex='2']"));
    public SelenideElement forgot_password = $(By.className("css-login-forgetpass"));

}
