/**
 * 
 */
package com.yeastar.controllers;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import java.lang.reflect.Method;

import static com.codeborne.selenide.WebDriverRunner.setWebDriver;


public class WebDriverFactory extends BrowserFactory
{
	public static ThreadLocal<WebDriver> wd = new ThreadLocal<WebDriver>();

	public void setDriver(WebDriver driver)
	{
		wd.set(driver);
		setWebDriver(driver);
	}

	public static WebDriver getDriver()
	{
		return wd.get();
	}



}
