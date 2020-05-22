package com.yeastar.pageObject.pSeries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.*;

@Log4j2
public class LoginPage {

    public SelenideElement login_username = $(By.id("login_username"));
    public SelenideElement login_password = $(By.id("login_password"));
    public SelenideElement loginBtn = $(By.xpath("//button[@type='submit']"));
    public SelenideElement user_avatars = $(By.xpath("//i[contains(@aria-label,'user')]"));
    public SelenideElement header_box_name = $(By.xpath("//span[contains(@class,'header-box')]"));

    /**
     * login PBX P
     * @param userName
     * @param passWord
     */
    @Step("login with userName:{0} , password:{1}")
    public LoginPage login(String userName,String passWord){
        login_username.shouldBe(Condition.visible).setValue(userName);
        login_password.setValue(passWord);
        loginBtn.click();
        user_avatars.shouldBe(Condition.exist);
        sleep(3000);
        return this;
    }

}
