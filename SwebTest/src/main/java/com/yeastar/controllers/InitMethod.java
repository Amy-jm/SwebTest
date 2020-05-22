/**
 * 
 */
package com.yeastar.controllers;

import java.awt.Robot;
import java.io.File;
import java.net.URI;

import com.yeastar.untils.PropertiesUntils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.ITestResult;
import org.testng.asserts.SoftAssert;

/**
 * @Author Gladson Antony
 * @Date 28-Jan-2017
 */
public class InitMethod 
{


	final static String DATA_PROPERTIES_FILE_PATH ="/data.properties";
	public static String WebsiteURL;
	public static String Browser;
	
	public static String FS = File.separator;

	public static String OSName = System.getProperty("os.name");
	public static String OSArchitecture = System.getProperty("os.arch");
	public static String OSVersion = System.getProperty("os.version");
	public static String OSBit = System.getProperty("sun.arch.data.model");

	public static String ProjectWorkingDirectory = System.getProperty("user.dir");

	
	public static Robot re;
	public static Alert al;
	public static String robotImageName;
	public static Select se;
	public static String FileToUpload;
	public static Actions ac;
	public static String VideoName;
	public static ITestResult testResult;
	public static SoftAssert softAssert;
	public static WebDriver augmentedDriver;
	public static ITestResult result;
	public static URI uri;


	/**
	 * selenide
	 */
	public static long FINDELEMENT_TIMEOUT = 10000;  //元素查找的时间差


	/**
	 * gridhub
	 */
	public  static String IS_RUN_REMOTE_SERVER = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"IS_RUN_REMOTE_SERVER");
	public  static String GRID_HUB_IP = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"GRID_HUB_IP");
	public  static String GRID_HUB_PORT = PropertiesUntils.getInstance().getPropertiesValue(DATA_PROPERTIES_FILE_PATH,"GRID_HUB_PORT");

	
}
