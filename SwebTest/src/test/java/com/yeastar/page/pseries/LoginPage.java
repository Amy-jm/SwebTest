package com.yeastar.page.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.*;
import static com.yeastar.untils.WaitUntils.SHORT_WAIT;

@Log4j2
public class LoginPage extends BasePage{

    public SelenideElement login_username = $(By.id("login_username"));
    public SelenideElement login_password = $(By.id("login_password"));
    public SelenideElement loginBtn = $(By.xpath("//button[@type='submit']"));
    public SelenideElement user_avatars = $(By.xpath("//i[contains(@aria-label,'user')]"));
    public SelenideElement header_box_name = $(By.xpath("//span[contains(@class,'header-box')]"));

    //分机 Privacy Policy Agreement
    public SelenideElement privacy_policy_confirmgdpr = $(By.id("privacy_policy_confirmgdpr"));
    public SelenideElement change_password_new_password = $(By.id("change_password_new_password"));
    public SelenideElement change_password_confirm_password = $(By.id("change_password_confirm_password"));
    public SelenideElement dail_save_btn = $(By.xpath("//*[@id=\"rcDialogTitle1\"]/../..//button//span[contains(text(),'Save')]/.."));

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

    /**
     * extension login PBX P
     * @param userName
     * @param passWord
     */
    @Step("login with userName:{0} , password:{1},changePassword:{2}")
    public LoginPage loginWithExtension(String userName,String passWord,String changePassword){
        login_username.shouldBe(Condition.visible).setValue(userName);
        login_password.setValue(passWord);
        loginBtn.click();
        if(waitElementDisplay(privacy_policy_confirmgdpr,SHORT_WAIT)){
            Selenide.actions().click(privacy_policy_confirmgdpr).perform();
            sleep(1000);
            ConfrimAlertBtn.click();

        }
        if(waitElementDisplay(change_password_new_password,SHORT_WAIT) && waitElementDisplay(change_password_confirm_password,SHORT_WAIT)){
            change_password_new_password.setValue(changePassword);
            change_password_confirm_password.setValue(changePassword);
            dail_save_btn.click();

            login_username.shouldBe(Condition.visible).setValue(userName);
            login_password.setValue(changePassword);
            loginBtn.click();
        }

        //change password
//        change_password_new_password.shouldBe(Condition.exist);
        sleep(SHORT_WAIT);
        return this;
    }

}
