package com.yeastar.swebtest.pobject.CDRandRecordings.Edit_List_Options;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Edit_List_Options {
    public SelenideElement time = $(By.xpath("//div[starts-with(@id,\"checkboxgroup\")]//label[ text()=\"Time\"]/../../.."));
    public SelenideElement callFrom = $(By.xpath("//div[starts-with(@id,\"checkboxgroup\")]//label[ text()=\"Call From\"]/../../.."));
    public SelenideElement callTo = $(By.xpath("//div[starts-with(@id,\"checkboxgroup\")]//label[ text()=\"Call To\"]/../../.."));
    public SelenideElement callDuration = $(By.xpath("//div[starts-with(@id,\"checkboxgroup\")]//label[ text()=\"Call Duration (s)\"]/../../.."));
    public SelenideElement talkDuration = $(By.xpath("//div[starts-with(@id,\"checkboxgroup\")]//label[ text()=\"Talk Duration (s)\"]/../../.."));
    public SelenideElement status = $(By.xpath("//div[starts-with(@id,\"checkboxgroup\")]//label[ text()=\"Status\"]/../../.."));
    public SelenideElement sourceTrunk = $(By.xpath("//div[starts-with(@id,\"checkboxgroup\")]//label[ text()=\"Source Trunk\"]/../../.."));
    public SelenideElement destinationTrunk = $(By.xpath("//div[starts-with(@id,\"checkboxgroup\")]//label[ text()=\"Destination Trunk\"]/../../.."));
    public SelenideElement communicationType = $(By.xpath("//div[starts-with(@id,\"checkboxgroup\")]//label[ text()=\"Communication Trunk\"]/../../.."));
    public SelenideElement PINCode = $(By.xpath("//div[starts-with(@id,\"checkboxgroup\")]//label[ text()=\"PIN Code\"]/../../.."));
    public SelenideElement DOD = $(By.xpath("//div[starts-with(@id,\"checkboxgroup\")]//label[ text()=\"DOD\"]/../../.."));
    public SelenideElement calledrIPAddress = $(By.xpath("//div[starts-with(@id,\"checkboxgroup\")]//label[ text()=\"Caller IP Address\"]/../../.."));
    public SelenideElement cost =$(By.xpath("//div[starts-with(@id,\"checkboxgroup\")]//label[ text()=\"Cost\"]/../../.."));
    public SelenideElement restoreDefaults = $(By.xpath("//div[starts-with(@id,\"cdrandrecord-edi\")]//span[ text()=\"Restore Defaults\"]"));

    public int r_sourceTrunk =2;
    public int r_destinationTrunk =2;
    public int r_communicationType=2;
    public int r_pincode = 3;


    public int c_sourceTrunk = 0;
    public int c_destinationTrunk =1;
    public int c_communicationType=2;
    public int c_pincode = 0;


////div[starts-with(@id,"checkboxgroup")]//label[ text()="Source Trunk"]























}
