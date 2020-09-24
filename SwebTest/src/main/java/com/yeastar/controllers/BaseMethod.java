package com.yeastar.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.yeastar.swebtest.tools.pjsip.UserAccount;
import com.yeastar.untils.SSHLinuxUntils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.asserts.SoftAssert;

import static com.codeborne.selenide.Selenide.sleep;
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
		}catch(Exception ex){
			log.error("[BaseMethod on step ] "+ex);
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
		}catch(Exception ex){
			log.error("[BaseMethod on assertStep ] "+ex);
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

	/**
	 * Base64加密字符串
	 * @param str
	 * @return
	 */
	public String enBase64(String str) {
		byte[] bytes = str.getBytes();

		String encoded = Base64.getEncoder().encodeToString(bytes);

		return encoded;
	}

	/**
	 * ssh操作执行静态命令查看
	 * @param asteriskCommand
	 * @return
	 * @throws IOException
	 * @throws JSchException
	 */
	public  String execAsterisk(String asteriskCommand)  {
		String asterisk_commond = String.format(ASTERISK_CLI,asteriskCommand);
		log.debug("[asterisk_command]"+asterisk_commond);
		String str = null;
		try {
			str = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, asterisk_commond);
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.debug("[asterisk_command_return_string] "+str);
		return str;
	}

	@Step("...清空/ysdisk/syslog/pbxlog.0文件")
	public String clearasteriskLog()  {
		log.debug("[CLEAR_CLI_LOG_command]"+CLEAR_CLI_LOG);
		String str = null;
		try {
			str = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, CLEAR_CLI_LOG);
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.debug("[asterisk_command_return_string] "+str);
		return str;
	}

	/**
	 * 获取分机通话状态
	 *
	 * @param username
	 */
	@Step("获取分机通话状态")
	public  int getExtensionStatus(int username, int expectStatus, int timeout) {
		UserAccount account;
		int time = 0;
		int status = -1;
		while (time <= timeout) {
			sleep(1000);
			account = pjsip.getUserAccountInfo(username);
			if (account == null) {
				status = -1;
			}
			if (account.status == expectStatus) {
				status = account.status;
				return status;
			}
			if (time == timeout) {
				status = account.status;
			}
			time++;
		}
		System.out.println("分机通话状态：" + status);
		return status;
	}

	/**
	 * 界面刷新
	 */
	public void refresh(){
		getWebDriver().navigate().refresh();
	}

}
