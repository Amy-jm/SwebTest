package com.yeastar.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.yeastar.untils.SSHLinuxUntils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.*;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

@Log4j2
public class BaseMethod extends WebDriverFactory {

	@Step("{0}")
	public void step(String desc){
		log.debug("[step] "+desc);
		sleep(5);
		try{
			Cookie cookie = new Cookie("zaleniumMessage", desc);
			getWebDriver().manage().addCookie(cookie);
		}catch (org.openqa.selenium.WebDriverException exception){
			log.error("[org.openqa.selenium.WebDriverException: unable to set cookie]");
		}
	}

	@Step("{0}")
	public void assertStep(String desc){
		log.debug("[Assert] "+desc);
		sleep(5);
		try{
			Cookie cookie = new Cookie("zaleniumMessage", desc);
			getWebDriver().manage().addCookie(cookie);
		}catch (org.openqa.selenium.WebDriverException exception){
			log.error("[org.openqa.selenium.WebDriverException: unable to set cookie]");
		}

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

	/* To ScrollUp using JavaScript Executor */
	public void scrollUp() throws Exception 
	{
		((JavascriptExecutor) getWebDriver()).executeScript("scroll(0, -100);");
	}


	/* To ScrollDown using JavaScript Executor */
	public void scrollDown() throws Exception 
	{
		((JavascriptExecutor) getWebDriver()).executeScript("scroll(0, 100);");
	}

	/*To Switch To Frame By Index */
	public void switchToFrameByIndex(int index) throws Exception
	{
		getWebDriver().switchTo().frame(index);
	}


	/*To Switch To Frame By Frame Name */
	public void switchToFrameByFrameName(String frameName) throws Exception
	{
		getWebDriver().switchTo().frame(frameName);
	}


	/*To Switch To Frame By Web Element */
	public void switchToFrameByWebElement(WebElement element) throws Exception
	{
		getWebDriver().switchTo().frame(element);
	}


	/*To Switch out of a Frame */
	public void switchOutOfFrame() throws Exception
	{
		getWebDriver().switchTo().defaultContent();
	}


	/*To Get Tooltip Text */
	public String getTooltipText(WebElement element)
	{
		String tooltipText = element.getAttribute("title").trim();
		return tooltipText;
	}


	/*To Close all Tabs/Windows except the First Tab */
	public void closeAllTabsExceptFirst() 
	{
		ArrayList<String> tabs = new ArrayList<String> (getWebDriver().getWindowHandles());
		for(int i=1;i<tabs.size();i++)
		{	
			getWebDriver().switchTo().window(tabs.get(i));
			getWebDriver().close();
		}
		getWebDriver().switchTo().window(tabs.get(0));
	}
	
	
	/*To Print all the Windows */
	public void printAllTheWindows() 
	{
		ArrayList<String> al = new ArrayList<String>(getWebDriver().getWindowHandles());
		for(String window : al)
		{
			System.out.println(window);
		}
	}



	public  String execAsterisk(String asteriskCommand) throws IOException, JSchException {
		String asterisk_commond = String.format(ASTERISK_CLI,asteriskCommand);
		log.debug("[asterisk_command]"+asterisk_commond);
		String str = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, asterisk_commond);
		log.debug("[asterisk_command_return_string] "+str);
		return str;
	}
}
