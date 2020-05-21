package com.yeastar.swebtest.pseries.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    private SelenideElement login_username = $(By.id("login_username"));
    private SelenideElement login_password = $(By.id("login_password"));
    private SelenideElement loginBtn = $(By.xpath("//button[@type='submit']"));
    private SelenideElement user_avatars = $(By.xpath("//i[contains(@aria-label,'user')]"));

    /**
     * 登录PBX P
     * @param userName
     * @param passWord
     */
    public void login(String userName,String passWord){
        login_username.shouldBe(Condition.visible).setValue(userName);
        login_password.setValue(passWord);
        loginBtn.click();
        user_avatars.shouldBe(Condition.exist);
    }
}
