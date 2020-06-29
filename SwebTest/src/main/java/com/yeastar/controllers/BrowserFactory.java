package com.yeastar.controllers;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import com.codeborne.selenide.Configuration;
import com.yeastar.swebtest.driver.ConfigP;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;


@Log4j2
public class BrowserFactory extends ConfigP {
	private WebDriver webDriver;
	public WebDriver initialDriver(String browser, String relativeOrAbsoluteUrl) throws MalformedURLException {
		if (IS_RUN_REMOTE_SERVER.trim().equalsIgnoreCase("true")) {
			log.debug("[IS_RUN_REMOTE_SERVER] " + IS_RUN_REMOTE_SERVER);
			return initialDriver(browser, relativeOrAbsoluteUrl, "http://" + GRID_HUB_IP + ":" + GRID_HUB_PORT + "/wd/hub");
		} else {
			return initialDriver(browser, relativeOrAbsoluteUrl, "");
		}
	}

	public WebDriver initialDriver(String browser, String relativeOrAbsoluteUrl, Method method) throws MalformedURLException {
		if (IS_RUN_REMOTE_SERVER.trim().equalsIgnoreCase("true")) {
			log.debug("[IS_RUN_REMOTE_SERVER] " + IS_RUN_REMOTE_SERVER);
			return initialDriver(browser, relativeOrAbsoluteUrl, "http://" + GRID_HUB_IP + ":" + GRID_HUB_PORT + "/wd/hub", method);
		} else {
			return initialDriver(browser, relativeOrAbsoluteUrl, "");
		}
	}

	public WebDriver initialDriver(String browser, String relativeOrAbsoluteUrl, String hubUrl) throws MalformedURLException {
		//Selenide的配置信息
		Configuration.timeout = FINDELEMENT_TIMEOUT;
		Configuration.collectionsTimeout = FINDELEMENT_TIMEOUT;
		Configuration.startMaximized = true;
		Configuration.captureJavascriptErrors = false;//捕获js错误
		Configuration.savePageSource = false;
		DesiredCapabilities desiredCapabilities = null;
		//选择测试浏览器
		//这里保留IE、EDGE等浏览器的判断，以及比较重要的一个：PhantomJS
		if (browser.equals("chrome")) {
			Configuration.browser = CHROME;
			System.setProperty("webdriver.chrome.driver", CHROME_PATH);
			desiredCapabilities = DesiredCapabilities.chrome();
		} else if (browser.equals("firefox")) {
			desiredCapabilities = DesiredCapabilities.firefox();
		} else {
			log.error("浏览器参数有误：" + browser);
		}

		if (GRID_HUB_IP != "" && hubUrl != "") {
			log.debug("[GRID_HUB_IP] " + hubUrl);
			desiredCapabilities.setCapability("name", Thread.currentThread().getStackTrace()[3].getMethodName());
			desiredCapabilities.setCapability("build", System.getProperty("serviceBuildName"));
			desiredCapabilities.setCapability(CapabilityType.BROWSER_NAME, BrowserType.CHROME);
			desiredCapabilities.setCapability("testFileNameTemplate", "myID_{browser}_{testStatus}");
			desiredCapabilities.setCapability("network", true);
			desiredCapabilities.setCapability("idleTimeout", 240);//150-100
			desiredCapabilities.setCapability("ZALENIUM_PROXY_CLEANUP_TIMEOUT", 90);//180-90


			webDriver = new RemoteWebDriver(new URL(hubUrl), desiredCapabilities);

			webDriver.get(relativeOrAbsoluteUrl);
			webDriver.manage().window().maximize();
			setWebDriver(webDriver);
			}else {
				open(relativeOrAbsoluteUrl);
				webDriver = getWebDriver();
			}
		return webDriver;
	}
	
	public WebDriver initialDriver(String browser, String relativeOrAbsoluteUrl, String hubUrl, Method method) throws MalformedURLException {
		//Selenide的配置信息
		Configuration.timeout = FINDELEMENT_TIMEOUT;
		Configuration.collectionsTimeout = FINDELEMENT_TIMEOUT;
		Configuration.startMaximized = true;
		Configuration.reportsFolder = SCREENSHOT_PATH;
//        ScreenShooter.captureSuccessfulTests = true; //每个test后都截图，不论失败或成功
		Configuration.captureJavascriptErrors = false;//捕获js错误
		Configuration.savePageSource = false;
		DesiredCapabilities desiredCapabilities = null;
		//选择测试浏览器
		//这里保留IE、EDGE等浏览器的判断，以及比较重要的一个：PhantomJS
		if (browser.equals("chrome")) {
			Configuration.browser = CHROME;
			System.setProperty("webdriver.chrome.driver", CHROME_PATH);
			desiredCapabilities = DesiredCapabilities.chrome();
		} else if (browser.equals("firefox")) {
			System.setProperty("webdriver.firefox.bin", FIREFOX_PATH);
			desiredCapabilities = DesiredCapabilities.firefox();
		} else {
			log.error("浏览器参数有误：" + browser);
		}
		if (GRID_HUB_IP != null) {
			log.debug("[GRID_HUB_IP] " + hubUrl);
			desiredCapabilities.setCapability("name", getTestName(method));
			desiredCapabilities.setCapability(CapabilityType.BROWSER_NAME, BrowserType.CHROME);
			desiredCapabilities.setCapability("network", true);
			//Build Name    String serviceName = System.getProperty("serviceName");
//            desiredCapabilities.setCapability("build", "AutoTestBuild_Seven");
			desiredCapabilities.setCapability("testFileNameTemplate", "myID_{browser}_{testStatus}");
			desiredCapabilities.setCapability("build", System.getProperty("serviceBuildName"));
			//Idle TimeOut
			desiredCapabilities.setCapability("idleTimeout", 150);//150-100
			desiredCapabilities.setCapability("ZALENIUM_PROXY_CLEANUP_TIMEOUT", 30);//180-90
			//Screen Resolution
//			desiredCapabilities.setCapability("screenResolution", "1920x1080");
			desiredCapabilities.setCapability("network", true);

			webDriver = new RemoteWebDriver(new URL(hubUrl), desiredCapabilities);
		}
		webDriver.manage().window().maximize();
		return webDriver;
	}

	/**
	 * get test name
	 *
	 * @param method
	 * @return
	 */
	public String getTestName(Method method) {
		return this.getClass().getSimpleName() + "." + method.getName();
	}

}
