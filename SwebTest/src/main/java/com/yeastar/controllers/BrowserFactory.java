package com.yeastar.controllers;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.codeborne.selenide.Configuration;
import com.yeastar.swebtest.driver.ConfigP;
import com.yeastar.untils.DataUtils;
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

	public WebDriver initialDriver(String browser, String relativeOrAbsoluteUrl, Method method) {
		if (IS_RUN_REMOTE_SERVER.trim().equalsIgnoreCase("true")) {
			log.debug("[IS_RUN_REMOTE_SERVER] " + IS_RUN_REMOTE_SERVER);
			return initialDriver(browser, relativeOrAbsoluteUrl, "http://" + GRID_HUB_IP + ":" + GRID_HUB_PORT + "/wd/hub", method);
		} else {
			return initialDriver(browser, relativeOrAbsoluteUrl, "");
		}
	}

	public WebDriver initialDriver(String browser, String relativeOrAbsoluteUrl, String hubUrl){
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
			ChromeOptions options = new ChromeOptions();
			options.addArguments("disable-infobars");
			options.addArguments("--no-sandbox");// 以最高权限运行
			options.addArguments("--disable-gpu");//硬件加速，谷歌文档提到需要加上这个属性来规避bug
			options.addArguments("--start-maximized");//默认启动最大化，避免最大化过程失败
			options.addArguments("--lang=en");
			options.addArguments("--ignore-certificate-errors");
			return webDriver = new ChromeDriver(options);

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
			String testFileNameTemplate = DataUtils.getCurrentTime()+"_{browser}_{testStatus}";
			log.debug("[testFileNameTemplate]{}",testFileNameTemplate);
			desiredCapabilities.setCapability("testFileNameTemplate", testFileNameTemplate);
			desiredCapabilities.setCapability("network", true);
			log.debug("[idleTimeout] 20s");
			desiredCapabilities.setCapability("idleTimeout", 240);//150-100
			log.debug("[ZALENIUM_PROXY_CLEANUP_TIMEOUT]  90s");
			desiredCapabilities.setCapability("ZALENIUM_PROXY_CLEANUP_TIMEOUT", 90);//180-90


			try {
				webDriver = new RemoteWebDriver(new URL(hubUrl), desiredCapabilities);
			} catch (MalformedURLException e) {
				log.error("[init remote webdriver error]{}",e.getMessage()+e.getStackTrace());
			}

			webDriver.get(relativeOrAbsoluteUrl);
			webDriver.manage().window().maximize();
			setWebDriver(webDriver);
			}else {
				open(relativeOrAbsoluteUrl);
				webDriver = getWebDriver();
			}
		return webDriver;
	}

	public WebDriver initialDriver(String browser, String relativeOrAbsoluteUrl, String hubUrl, Method method){
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
			log.debug("[test name]{}", getTestName(method));
			desiredCapabilities.setCapability("name", getTestName(method));
			desiredCapabilities.setCapability(CapabilityType.BROWSER_NAME, BrowserType.CHROME);
			desiredCapabilities.setCapability("network", true);
			String testFileNameTemplate = DataUtils.getCurrentTime()+"_{browser}_{testStatus}";
			log.debug("[testFileNameTemplate]{}",testFileNameTemplate);
			desiredCapabilities.setCapability("testFileNameTemplate", testFileNameTemplate);
			desiredCapabilities.setCapability("build", System.getProperty("serviceBuildName"));
			//Idle TimeOut
			desiredCapabilities.setCapability("idleTimeout", 300);//150-100
			log.debug("[set idleTimeout]{}",300);
			desiredCapabilities.setCapability("ZALENIUM_PROXY_CLEANUP_TIMEOUT", 720);//180-90
			log.debug("[set ZALENIUM_PROXY_CLEANUP_TIMEOUT]{}",720);
			desiredCapabilities.setCapability("WAIT_FOR_AVAILABLE_NODES",false);//不等待回收node，直接创建
			log.debug("[set WAIT_FOR_AVAILABLE_NODES]{}",false);
			if (RECORD_VIDEO.trim().equalsIgnoreCase("false")) {
				log.debug("[set recordVideo]{}",RECORD_VIDEO.trim());
				desiredCapabilities.setCapability("recordVideo", false);
			}
			//Screen Resolution
//			desiredCapabilities.setCapability("screenResolution", "1920x1080");
			desiredCapabilities.setCapability("network", true);
			//unknown error: DevToolsActivePort file doesn't exist
			log.debug("[add config for DevToolsActivePort issue start...]");

			ChromeOptions options = new ChromeOptions();
			options.addArguments("disable-infobars");
			options.addArguments("--no-sandbox");// 以最高权限运行
			options.addArguments("--disable-gpu");//硬件加速，谷歌文档提到需要加上这个属性来规避bug
			options.addArguments("--start-maximized");//默认启动最大化，避免最大化过程失败
			options.addArguments("--lang=en");
			options.addArguments("--ignore-certificate-errors");

			options.addArguments("blink-settings=imagesEnabled=false");//不加载图片, 提升速度
			Map<String, Object> prefs = new HashMap<String, Object>();
			prefs.put("credentials_enable_service", false);
			prefs.put("profile.password_manager_enabled", false);
			prefs.put("profile.default_content_settings.popups", 0);
			prefs.put("download.prompt_for_download", false);
			prefs.put("--user-data-dir","/home/seluser/chrome-user-data-dir");
			options.setExperimentalOption("prefs", prefs);
			desiredCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			desiredCapabilities.setCapability("goog:"+ChromeOptions.CAPABILITY, options);
			log.debug("[add config for DevToolsActivePort issue end...]");
			try {
				webDriver = new RemoteWebDriver(new URL(hubUrl), desiredCapabilities);
			} catch (MalformedURLException e) {
				log.error("[init remote webdriver error]{}",e.getMessage()+e.getStackTrace());
			}
		}
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
