package com.yeastar.swebtest.pobject.Settings.System.Security.Service;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Service {
    public SelenideElement service = $(By.xpath(".//div[starts-with(@id,'security')]//span[text()='Service']"));

    public String weakPassword = "st-service-enableweakpwd";
    public String autoLogoutTime = "webexpiretime";
    public String loginMode_extension = ("loginmode-ext");
    public String loginMode_email = ("loginmode-eml");
//    public SelenideElement protocol_picker = $(By.id("st-service-webprotocol-trigger-picker"));
    public String protocol = "st-service-webprotocol";
    public String HTTPS = "https";
    public String HTTP= "http";
    public SelenideElement port = $(By.id("st-service-webport-inputEl"));
    public String redirectfromport80 = ("st-service-redirecturl");
    public String certificate = "st-service-httpscert";
//    public SelenideElement enableSSH_check = $(By.id("st-service-enablessh-displayEl"));
    public String enableSSH_check = "st-service-enablessh";

    public SelenideElement enableSSH = $(By.id("st-service-sshport-inputEl"));
    public String enableFTP_check = "st-service-enableftp-displayEl";
    public SelenideElement enableFTP = $(By.id("st-service-ftpport-inputEl"));
    public String enableTFTP_check = "st-service-enabletftp-displayEl";
    public SelenideElement IAXPort = $(By.id("st-service-bindport-inputEl"));
    public SelenideElement SIPUDPPort = $(By.id("st-service-udpport-inputEl"));
    public String enableSIPTCP_check ="st-service-enabletcp-displayEl";
    public SelenideElement enableSIPTCP = $(By.id("st-service-tcpport-inputEl"));
    public String enableSIPTLS_check = ("st-service-enabletls-displayEl");
    public SelenideElement enableSIPTLS = $(By.id("st-service-tlsport-inputEl"));
    public SelenideElement enableDHCPServer = $(By.id("st-service-enable-displayEl"));
    public String enableAMI = "st-service-enableami";
    public SelenideElement enableAMI_Name = $(By.id("st-service-username-inputEl"));
    public SelenideElement enableAMI_Password = $(By.id("st-service-password-inputEl"));
    public SelenideElement enableAMI_Permitted = $(By.xpath(".//div[starts-with(@id,'panel')]//input[starts-with(@id,'ipaddrpermit')]"));
    public SelenideElement enableAMI_Subnet = $(By.xpath(".//div[starts-with(@id,'panel')]//input[starts-with(@id,'maskpermit')]"));

    public SelenideElement secure_OK = $(By.xpath(".//div[starts-with(@id,'messagebox')]//span[text()='OK']"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'service')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'service')]//span[text()='Cancel']"));
















}
