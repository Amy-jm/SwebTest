package com.yeastar.swebtest.pobject.Settings.System.Network.OpenVPN;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class OpenVPN {
    public SelenideElement openVPN = $(By.xpath(".//div[starts-with(@id,'network')]//span[ text()='OpenVPN']"));

    public SelenideElement enableOpenVPN = $(By.id("st-vpn-enable-displayEl"));

    public String type = ("st-vpn-type");
    public SelenideElement serverAddress = $(By.id("st-vpn-serveraddr-inputEl"));
    public SelenideElement serverPort = $(By.id("st-vpn-serverport-inputEl"));
    public String protocol = ("st-vpn-protocol");
    public String deviceMode = ("st-vpn-device");
    public SelenideElement username = $(By.id("st-vpn-username-inputEl"));
    public SelenideElement password = $(By.id("st-vpn-password-inputEl"));
    public String encryption = ("st-vpn-encryption");
    public SelenideElement compression = $(By.id("st-vpn-compression-displayEl"));
    public SelenideElement proxyServer = $(By.id("st-vpn-proxyserver-inputEl"));
    public SelenideElement proxyPort = $(By.id("st-vpn-proxyport-inputEl"));

    public SelenideElement CACert = $(By.id("st-vpn-cacertfile-inputEl"));
    public SelenideElement CACert_browse = $(By.id("st-vpn-cacertfile-button-fileInputEl"));

    public SelenideElement cert = $(By.id("st-vpn-certfile-inputEl"));
    public SelenideElement cert_browse = $(By.id("st-vpn-certfile-button-fileInputEl"));

    public SelenideElement key = $(By.id("st-vpn-keyfile-inputEl"));
    public SelenideElement key_browse = $(By.id("st-vpn-keyfile-button-fileInputEl"));

    public SelenideElement TLSAuthentination = $(By.id("st-vpn-tlsauth-displayEl"));

    public SelenideElement TAKey = $(By.id("st-vpn-takeyfile-inputEl"));
    public SelenideElement TAKey_browse = $(By.id("st-vpn-takeyfile-button-fileInputEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,\"network\")]//span[ text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,\"network\")]//span[ text()=\"Cancel\"]"));


}
