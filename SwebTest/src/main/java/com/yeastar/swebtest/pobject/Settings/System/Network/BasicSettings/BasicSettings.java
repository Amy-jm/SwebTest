package com.yeastar.swebtest.pobject.Settings.System.Network.BasicSettings;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class BasicSettings {

    public String mode_Dual = "dual";
    public String mode_Signal = "signal";
    public String mode_Bridge = "bridge";

    public String mode = "st-net-mode";
    public SelenideElement mode_input = $(By.id("st-net-mode-inputEl"));
    public SelenideElement basicSettings = $(By.xpath(".//div[starts-with(@id,'network')]//span[ text()='Basic Settings']"));

    public SelenideElement hostname = $(By.id("st-net-hostname-inputEl"));
    public SelenideElement mode_picker = $(By.id("st-net-mode-trigger-picker"));
//    public SelenideElement defaultInterface_picker = $(By.id("st-net-gwnic-trigger-picker"));
    public String defaultInterface = "st-net-gwnic";
    public String cellularNetwork_picker = ("st-net-lteprority");
    /**
     * LAN
     */

    /**
     * DHCP
     */

    public SelenideElement l_DHCP = $(By.xpath(".//input[starts-with(@name='lan1type') ]/*/*/label[text()='DHCP']"));
    public String l_DHCP_id = "Ext.query('#'+Ext.getCmp('control-panel').down('network').down('basic').lookupReference('dhcp1').id+ ' span')[1].id";


    /**
     * Static Address
     */
    public SelenideElement l_staticIPAddress = $(By.xpath(".//input[starts-with(@name='lan1type') ]/*/*/label[text()='Static IP Address']"));
    public String l_staticIPAddress_id ="Ext.query('#'+Ext.getCmp('control-panel').down('network').down('basic').lookupReference('staticip1').id+ ' span')[1].id";

    public SelenideElement l_IPAddress = $(By.id("st-net-ipaddress11-inputEl"));
    public SelenideElement l_subnetMask = $(By.id("st-net-netmask11-inputEl"));
    public SelenideElement l_gateway = $(By.id("st-net-gateway1-inputEl"));
    public SelenideElement l_preferredDNSServer = $(By.id("st-net-dns11-inputEl"));
    public SelenideElement l_alternateDNSServer = $(By.id("st-net-dns21-inputEl"));
    public SelenideElement l_IPAddress2 = $(By.id("st-net-ipaddress21-inputEl"));
    public SelenideElement l_subnetMask2 = $(By.id("st-net-netmask21-inputEl"));
    public SelenideElement l_enableVLAN = $(By.id("st-net-enablevlan1-displayEl"));
    public SelenideElement l_enableVLANsubinterface1 = $(By.id("st-net-enablevlan11-displayEl"));
    public SelenideElement l_enableVLANsubinterface2 = $(By.id("st-net-enablevlan21-displayEl"));


    /**
     * PPPoE
     */

    public SelenideElement l_PPPoE = $(By.xpath(".//input[starts-with(@name='lan1type') ]/*/*/label[text()='PPPoE']"));
    public String l_PPPoE_id = "Ext.query('#'+Ext.getCmp('control-panel').down('network').down('basic').lookupReference('pppoe1').id+ ' span')[1].id";


    public SelenideElement l_username = $(By.id("st-net-pppoeusername1-inputEl"));
    public SelenideElement l_password = $(By.id("st-net-pppoepassword1-inputEl"));


    /**
     * WAN
     */
    /**
     * DHCP
     */

//    public SelenideElement w_DHCP = $(By.xpath(".//input[starts-with(@name='lan2type') ]/*/*/label[text()='DHCP']"));
    public String w_DHCP_id = "Ext.query('#'+Ext.getCmp('control-panel').down('network').down('basic').lookupReference('dhcp2').id+ ' span')[1].id";

    /**
     * Static Address
     */
    public SelenideElement w_staticIPAddress = $(By.xpath(".//input[starts-with(@name='lan2type') ]/*/*/label[text()='Static IP Address']"));
    public String w_staticIPAddress_id ="Ext.query('#'+Ext.getCmp('control-panel').down('network').down('basic').lookupReference('staticip2').id+ ' span')[1].id";

    public SelenideElement w_IPAddress = $(By.id("st-net-ipaddress12-inputEl"));
    public SelenideElement w_subnetMask = $(By.id("st-net-netmask12-inputEl"));
    public SelenideElement w_gateway = $(By.id("st-net-gateway2-inputEl"));
    public SelenideElement w_preferredDNSServer = $(By.id("st-net-dns12-inputEl"));
    public SelenideElement w_alternateDNSServer = $(By.id("st-net-dns22-inputEl"));
    public SelenideElement w_enableVLAN = $(By.id("st-net-enablevlan2-displayEl"));
    public SelenideElement w_enableVLANsubinterface1 = $(By.id("st-net-enablevlan12-displayEl"));
    public SelenideElement w_enableVLANsubinterface2 = $(By.id("st-net-enablevlan22-displayEl"));


    /**
     * PPPoE
     */

    public SelenideElement w_PPPoE = $(By.xpath(".//input[starts-with(@name='lan2type') ]/*/*/label[text()='PPPoE']"));
    public String w_PPPoE_id = "Ext.query('#'+Ext.getCmp('control-panel').down('network').down('basic').lookupReference('pppoe2').id+ ' span')[1].id";

    public SelenideElement w_username = $(By.id("st-net-pppoeusername2-inputEl"));
    public SelenideElement w_password = $(By.id("st-net-pppoepassword2-inputEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,\"network\")]//span[ text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,\"network\")]//span[ text()=\"Cancel\"]"));

}
