package com.yeastar.swebtest.pobject.Settings.System.Email;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Email {
    public SelenideElement emailAddress = $(By.id("st-email-email-inputEl"));
    public SelenideElement password = $(By.id("st-email-secret-inputEl"));
    public SelenideElement SMTP = $(By.id("st-email-smtpserver-inputEl"));
    public SelenideElement SMTP_Port = $(By.id("st-email-port-inputEl"));
    public SelenideElement POP3 = $(By.id("st-email-pop3server-inputEl"));
    public SelenideElement POP3_Port = $(By.id("st-email-pop3port-inputEl"));
    public SelenideElement SMTPAuthentication = $(By.id("st-email-needauth-trigger-picker"));
    public SelenideElement enableTLS = $(By.id("st-email-usessl-displayEl"));
    public SelenideElement test = $(By.xpath(".//div[starts-with(@id,'emailsettings')]//span[ text()='Test']"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'emailsettings')]//span[ text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'emailsettings')]//span[ text()='Cancel']"));

}
