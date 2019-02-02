package com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Add_VoIP_Trunk.Add_VoIP_Trunk_Basic;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_VoIP_Trunk_Basic {
    public int  VoipTrunk = 1;
    public int  PeerToPeer = 2;
//    public int  AccountTrunk = 3;

    public SelenideElement basic = $(By.xpath(".//div[starts-with(@id,\"editvoip\")]//span[text()=\"Basic\"]"));

    public String type ="trunk-itsptype";
    public String trunkStatus = "trunk-status";
    public String protocol = "type";
    public SelenideElement providerName = $(By.id("trunkname-inputEl"));
    public SelenideElement hostname = $(By.id("hostname-inputEl"));
    public SelenideElement hostnamePort = $(By.id("hostport-inputEl"));
    public SelenideElement domain = $(By.id("fromdomain-inputEl"));
    public SelenideElement username = $(By.id("username-inputEl"));
    public SelenideElement authenticationName = $(By.id("ysauth-inputEl"));
    public SelenideElement calledIDNumber = $(By.id("globaldod-inputEl"));
    public String trunkType = "trunktype";
    public String transport = "transport";
    public SelenideElement password = $(By.id("secret-inputEl"));
    public SelenideElement fromUser = $(By.id("fromuser-inputEl"));
    public SelenideElement callerIDName = $(By.id("calleridname-inputEl"));
    public String enableProxyServer = "enableproxy-displayEl";
    public SelenideElement outboundProxyServer = $(By.id("proxyserver-inputEl"));
    public SelenideElement outboundProxyServerPort = $(By.id("proxyport-inputEl"));
    public String enableSLA = "enablesla-displayEl";
    public SelenideElement didNumber =$(By.id("dnisnumber0-inputEl"));


    public SelenideElement save = $(By.xpath(" .//div[starts-with(@id,'editvoip')]//span[ text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'editvoip')]//span[text()='Cancel']"));

}
