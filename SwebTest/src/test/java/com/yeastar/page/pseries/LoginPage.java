package com.yeastar.page.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.html5.Location;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.*;
import static com.yeastar.untils.WaitUntils.SHORT_WAIT;

@Log4j2
public class LoginPage extends BasePage{

    public SelenideElement login_username = $(By.id("login_username"));
    public SelenideElement login_password = $(By.id("login_password"));
    public SelenideElement loginBtn = $(By.xpath("//button[@type='submit']"));
    public SelenideElement user_avatars = $(By.xpath("//i[contains(@aria-label,'user')]"));
    public SelenideElement header_box_name = $(By.xpath("//span[contains(@class,'header-box')]"));
    public SelenideElement china_title = $(By.xpath("//div[contains(@title,'简体中文')]"));
    public SelenideElement english_title = $(By.xpath("//div[contains(@title,'English')]"));
    //div[contains(@aria-expanded,'false')]

    //分机 Privacy Policy Agreement
    public SelenideElement privacy_policy_confirmgdpr = $(By.id("privacy_policy_confirmgdpr"));
    public SelenideElement change_password_new_password = $(By.id("change_password_new_password"));
    public SelenideElement change_password_confirm_password = $(By.id("change_password_confirm_password"));
    public SelenideElement dail_save_btn = $(By.xpath("//button[@class='ant-btn modal-footer-btn ant-btn-primary']"));


    public Boolean isLoginSuccess = true;
    /**
     * login PBX P
     * @param userName
     * @param passWord
     */
    @Step("login with userName:{0} , password:{1}")
    public LoginPage login(String userName,String passWord){
        login_username.shouldBe(Condition.visible).setValue(userName);
        setElementValue(login_password,passWord);
        loginBtn.click();
        try {
            user_avatars.shouldBe(Condition.exist);
            sleep(3000);
        }catch (com.codeborne.selenide.ex.ElementNotFound e){
            isLoginSuccess = false;
        }
        return this;
    }

    /**
     *  切换到英文
     */
    public void switchToEnglish(){
        if(china_title.isDisplayed()){
            china_title.click();
            sleep(WaitUntils.RETRY_WAIT*2);
            actions().moveToElement(china_title,5,80).click().perform();

        }
    }





    /**
     * extension login PBX P
     * @param userName
     * @param passWord
     */
    @Step("login with userName:{0} , password:{1},changePassword:{2}")
    public LoginPage loginWithExtension(String userName,String passWord,String changePassword){
        login_username.shouldBe(Condition.visible).setValue(userName);
        setElementValue(login_password,passWord);
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
        }
        sleep(SHORT_WAIT);
        return this;
    }

    /**
     * extension login PBX P
     * @param userName
     * @param passWord
     */
    @Step("login with userName:{0} , password:{1},changePassword:{2}")
    public LoginPage loginWithExtensionNewPassword(String userName,String passWord,String changePassword){
        Boolean isChangePassword = false ;
        login_username.shouldBe(Condition.visible).setValue(userName);
        setElementValue(login_password,passWord);
        loginBtn.click();
        if(waitElementDisplay(privacy_policy_confirmgdpr,SHORT_WAIT)){
            Selenide.actions().click(privacy_policy_confirmgdpr).perform();
            sleep(1000);
            ConfrimAlertBtn.click();
        }
        sleep(SHORT_WAIT*2);//todo 29版本引入问题，修改密码界面会加载两次，修复后删除sleep
        if(waitElementDisplay(change_password_new_password,SHORT_WAIT) && waitElementDisplay(change_password_confirm_password,SHORT_WAIT)){
            change_password_new_password.setValue(changePassword);
            change_password_confirm_password.setValue(changePassword);
            dail_save_btn.click();
            isChangePassword = true ;
        }
        if(isChangePassword){
            login(userName,changePassword);
        }
        sleep(SHORT_WAIT);
        return this;
    }
    public LoginPage loginWithExtension(String userName,String passWord){
        loginWithExtension(userName, passWord,passWord);
        return this;
    }

}
