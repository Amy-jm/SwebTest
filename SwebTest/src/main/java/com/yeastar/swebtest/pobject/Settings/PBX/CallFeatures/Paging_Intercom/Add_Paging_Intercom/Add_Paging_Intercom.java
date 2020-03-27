package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Paging_Intercom.Add_Paging_Intercom;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_Paging_Intercom {

    public String list_PageingIntercom = "st-paginggroup-allowexten";

    public SelenideElement number = $(By.id("st-paginggroup-number-inputEl"));
    public SelenideElement name = $(By.id("st-paginggroup-name-inputEl"));
    public String type = "st-paginggroup-duplex";
    public String paging1way="paging";
    public String paging2way = "intercom";
    public String answers = "st-paginggroup-enablekeyhanup";
//    public SelenideElement answers = $(By.id("st-paginggroup-enablekeyhanup-displayEl"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'paginggroup-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'paginggroup-edit-')]//span[text()='Cancel']"));

}
