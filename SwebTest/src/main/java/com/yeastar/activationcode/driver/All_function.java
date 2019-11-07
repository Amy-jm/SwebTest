package com.yeastar.activationcode.driver;

import com.codeborne.selenide.WebDriverRunner;
import com.yeastar.swebtest.driver.Config;
import com.yeastar.swebtest.driver.SwebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.getWebDriverLogs;
import static com.codeborne.selenide.Selenide.refresh;
import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.swebtest.driver.SwebDriver.webDriver;

/**
 * Created by Caroline on 2019/9/17.
 */
public class All_function extends Config {

    public static WebDriver driver = null;

}
