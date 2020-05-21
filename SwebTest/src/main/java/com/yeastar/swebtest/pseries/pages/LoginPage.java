package com.yeastar.swebtest.pseries.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
@Log4j2
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
        log.info("input user name");
        login_username.shouldBe(Condition.visible).setValue(userName);
        login_password.setValue(passWord);
        loginBtn.click();
        log.info("should be");
        user_avatars.shouldBe(Condition.exist);
        log.info("sheep````");
        sleep(3000);
        log.info("sheep end");
    }
}
