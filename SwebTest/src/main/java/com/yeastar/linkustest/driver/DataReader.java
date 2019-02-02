
package com.yeastar.linkustest.driver;

import java.io.*;
import java.util.Properties;

/* 测试数据 */

public class DataReader {


//	被测设备的产品型号
	public static String product = readFromfile("product");

//	被测手机、辅助手机相关信息
	// 基本信息
	public static String linkus_ver = readFromfile("linkus_ver");  // linkus客户端版本号
	public static String androidVersionCode = readFromfile("androidVersionCode");//用于aapt dump badging检查versionCode与上个发布版本不一致（一样则后期无法上架）
	public static String androidApkPath = readFromfile("androidApkPath");
	public static String linkusA_udid = readFromfile("linkusA_udid"); //adb devices 被测手机
	public static String linkusB_udid = readFromfile("linkusB_udid"); //adb devices 辅助手机
	public static String linkusA_androidVer = readFromfile("linkusA_androidVer");  //安卓版本 被测手机
	public static String linkusB_androidVer = readFromfile("linkusB_androidVer");  //安卓版本 辅助手机
	public static String linkusA_appiumPort = readFromfile("linkusA_appiumPort");  //appium设置界面的地址和端口 被测手机
	public static String linkusB_appiumPort = readFromfile("linkusB_appiumPort");	//appium设置界面的地址和端口 辅助手机
	// 被测手机联系人信息
	public static String linkusA_contactName = readFromfile("linkusA_contactName");
	public static String linkusA_contactMobileNum = readFromfile("linkusA_contactMobileNum");
	public static String linkusA_contactWorkNum = readFromfile("linkusA_contactWorkNum");
	public static String linkusA_contactHomeNum = readFromfile("linkusA_contactHomeNum");
	public static String linkusA_contactWorkFax = readFromfile("linkusA_contactWorkFax");
	public static String linkusA_contactHomeFax = readFromfile("linkusA_contactHomeFax");
	public static String linkusA_contactOtherNum = readFromfile("linkusA_contactOtherNum");


//	被测设备相关信息
	// 基本信息
	public static String localAddress = readFromfile("localAddress");
	public static String localPort = readFromfile("localPort");
	public static String externalAddress = readFromfile("externalAddress");
	public static String externalPort = readFromfile("externalPort");
	public static String assistAddress = readFromfile("assistAddress");
	public static String assistPort = readFromfile("assistPort");
	public static int udpPort = Integer.parseInt(readFromfile("udpPort"));
	public static int tcpPort = Integer.parseInt(readFromfile("tcpPort"));
	public static int tlsPort = Integer.parseInt(readFromfile("tlsPort"));
	public static String SSH_PORT = readFromfile("sshPort");
	public static int AMI_PORT = Integer.parseInt(readFromfile("amiPort"));
	//	截图保存地址
	public static String SCREENSHOT_PATH = System.getProperty("user.dir")+"\\"+ readFromfile("screenShotPath");
	// 特殊目的地
	public static String ringGroupNum = readFromfile("ringGroupNum");
	public static String intercomNum = readFromfile("intercomNum");
	public static String conferenceNum = readFromfile("conferenceNum");
	public static String conferencePassword = readFromfile("conferencePassword");
	public static String ivrNum = readFromfile("ivrNum");
	public static String ivrKeyPressEvent = readFromfile("ivrKeyPressEvent");
	public static String queueNum = readFromfile("queueNum");

//  分机通用信息
	public static String udpProtocol = readFromfile("udpProtocol");
	public static String tcpProtocol = readFromfile("tcpProtocol");
	public static String tlsProtocol = readFromfile("tlsProtocol");
	public static String registPassword = readFromfile("registPassword");
	public static String loginPassword = readFromfile("loginPassword");

//	被测linkus A信息
	public static String linkusA_name = readFromfile("linkusA_name");
	public static String linkusA_num = readFromfile("linkusA_num");
	public static String linkusA_phoneNum = readFromfile("linkusA_phoneNum");
	public static String linkusA_emailAddress = readFromfile("linkusA_emailAddress");

//	辅助linkus分机相关信息
	public static String linkusB_num = readFromfile("linkusB_num");
	public static String linkusB_name = readFromfile("linkusB_name");
	public static String linkusB_phoneNum = readFromfile("linkusB_phoneNum");
	public static String linkusB_emailAddress = readFromfile("linkusB_emailAddress");

	public static String linkusC_num = readFromfile("linkusC_num");
	public static String linkusC_name = readFromfile("linkusC_name");
	public static String linkusC_Password = readFromfile("linkusC_Password");

	public static String linkusD_num = readFromfile("linkusD_num");
	public static String linkusD_name = readFromfile("linkusD_name");
	public static String linkusD_Password = readFromfile("linkusD_Password");

	//	被测设备其他辅助分机号码
	public static String extensionS02_num = readFromfile("extensionS02_num");
	public static String extensionS03_num = readFromfile("extensionS03_num");
	public static String extensionS04_num = readFromfile("extensionS04_num");
	public static String extensionS05_num = readFromfile("extensionS05_num");
	public static String extensionS06_num = readFromfile("extensionS06_num");
	public static String extensionS07_num = readFromfile("extensionS07_num");
	public static String extensionS08_num = readFromfile("extensionS08_num");
	public static String extensionS09_num = readFromfile("extensionS09_num");

//	被测设备中继名称
	public static String sipTrunk = readFromfile("sipTrunk");
	public static String sipTrunkPrefix = readFromfile("sipTrunkPrefix");
	public static String iaxTrunk = readFromfile("iaxTrunk");
	public static String iaxTrunkPrefix = readFromfile("iaxTrunkPrefix");
	public static String gsmTrunk = readFromfile("gsmTrunk");
	public static String gsmSimNum = readFromfile("gsmSimNum");
	public static String gsmTrunkPrefix = readFromfile("gsmTrunkPrefix");
	public static String pstnTrunk = readFromfile("pstnTrunk");
	public static String pstnTrunkPrefix = readFromfile("pstnTrunkPrefix");
	public static String e1Trunk = readFromfile("e1Trunk");
	public static String e1TrunkPrefix = readFromfile("e1TrunkPrefix");
	public static String briTrunk = readFromfile("briTrunk");
	public static String briTrunkPrefix = readFromfile("briTrunkPrefix");
	public static String spsTrunk = readFromfile("spsTrunk");
	public static String spsTrunkPrefix = readFromfile("spsTrunkPrefix");
	public static String spxTrunk = readFromfile("spxTrunk");
	public static String spxTrunkPrefix = readFromfile("spxTrunkPrefix");

//	辅助设备S1相关信息
	public static String localAddressS1 = readFromfile("localAddressS1");
	public static String externalAddressS1 = readFromfile("externalAddressS1");
	public static String externalPortS1 = readFromfile("externalPortS1");
	public static String gsmSimNumS1 = readFromfile("gsmSimNumS1");
	public static String extensionS10_num = readFromfile("extensionS10_num");
	public static String extensionS11_num = readFromfile("extensionS11_num");
	public static String extensionS12_num = readFromfile("extensionS12_num");

//	辅助设备S2相关信息
	public static String localAddressS2 = readFromfile("localAddressS2");
	public static String externalAddressS2 = readFromfile("externalAddressS2");
	public static String externalPortS2 = readFromfile("externalPortS2");
	public static String extensionS20 = readFromfile("extensionS20");




	/**
	 * 根据key返回key值
	 * @param key
	 * @return keyvalue
	 */
	public static String readFromfile(String key) {
		Properties properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new BufferedInputStream(new FileInputStream("src/main/resources/data.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (properties.getProperty(key).equals("null")){
			return "null";
		}else{
			return properties.getProperty(key);
		}

	}
}
