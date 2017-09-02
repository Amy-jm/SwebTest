package com.yeastar.swebtest.pobject.Settings.System.Security.Service;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Service {
    public SelenideElement service = $(By.xpath(".//span[starts-with(@class,\"x-tab-inner x-tab-inner-default\") and text()=\"Service\"]"));

    public SelenideElement autoLogoutTime = $(By.name("webexpiretime"));
    public SelenideElement loginMode_extension = $(By.id("loginmode-ext-displayEl"));
    public SelenideElement loginMode_email = $(By.id("loginmode-eml-displayEl"));
    public SelenideElement protocol_picker = $(By.id("st-service-webprotocol-trigger-picker"));
    public String protocol_id = "st-service-webprotocol";
    public String HTTPS = "https";
    public String HTTP= "http";
    public SelenideElement port = $(By.id("st-service-webport-inputEl"));
    public SelenideElement redirectfromport80 = $(By.id("st-service-redirecturl-displayEl"));
    public SelenideElement certificate = $(By.id("st-service-httpscert-trigger-picker"));
    public SelenideElement enableSSH_check = $(By.id("st-service-enablessh-displayEl"));
    public String enableSSH_check_id = "st-service-enablessh";

    public SelenideElement enableSSH = $(By.id("st-service-sshport-inputEl"));
    public SelenideElement enableFTP_check = $(By.id("st-service-enableftp-displayEl"));
    public SelenideElement enableFTP = $(By.id("st-service-ftpport-inputEl"));
    public SelenideElement enableTFTP_check = $(By.id("st-service-enabletftp-displayEl"));
    public SelenideElement IAXPort = $(By.id("st-service-bindport-inputEl"));
    public SelenideElement SIPUDPPort = $(By.id("st-service-udpport-inputEl"));
    public SelenideElement enableSIPTCP_check = $(By.id("st-service-enabletcp-displayEl"));
    public SelenideElement enableSIPTCP = $(By.id("st-service-tcpport-inputEl"));
    public SelenideElement enableSIPTLS_check = $(By.id("st-service-enabletls-displayEl"));
    public SelenideElement enableSIPTLS = $(By.id("st-service-tlsport-inputEl"));
    public SelenideElement enableDHCPServer = $(By.id("st-service-enable-displayEl"));
    public String enableAMI_id = "st-service-enableami";
    public SelenideElement enableAMI_Name = $(By.id("st-service-username-inputEl"));
    public SelenideElement enableAMI_Password = $(By.id("st-service-password-inputEl"));
    public SelenideElement enableAMI_Permitted = $(By.xpath(".//div[starts-with(@id,'panel')]//input[starts-with(@id,'ipaddrpermit')]"));
    public SelenideElement enableAMI_Subnet = $(By.xpath(".//div[starts-with(@id,'panel')]//input[starts-with(@id,'maskpermit')]"));

    public SelenideElement secure_OK = $(By.xpath(".//div[starts-with(@id,'messagebox')]//span[text()='OK']"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'service')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'service')]//span[text()='Cancel']"));
















}
