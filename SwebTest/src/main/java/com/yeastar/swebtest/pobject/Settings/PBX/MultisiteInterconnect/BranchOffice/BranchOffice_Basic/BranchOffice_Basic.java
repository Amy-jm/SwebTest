package com.yeastar.swebtest.pobject.Settings.PBX.MultisiteInterconnect.BranchOffice.BranchOffice_Basic;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/10/10.
 */
public class BranchOffice_Basic {

    public SelenideElement basic = $(By.xpath(".//div[starts-with(@id,'multisitembasic')]//span[text()=\"Basic\"]"));

    public String grid = "Ext.getCmp('control-panel').down('multisitembasic')";
    public int gridcolumn_Check = 0;
    public int gridcolumn_Status = 1;
    public int gridcolumn_Role = 2;
    public int gridcolumn_Account = 3;
    public int gridcolumn_Ip = 4;
    public int gridEdit = 0;

    public SelenideElement add = $(By.id("multisitebbasic-btn-add-btnEl"));
    public SelenideElement WithdrawFromTheNetworking = $(By.xpath(".//div[starts-with(@id,'multisitembasic')]//span[text()=\"Withdraw from the Networking\"]"));


    /**
     * 页码功能
     */
    public SelenideElement firstPage = $(By.xpath(".//div[starts-with(@id,'multisitembasic')]//a[@data-qtip='First Page']"));
    public SelenideElement previousPage = $(By.xpath(".//div[starts-with(@id,'multisitembasic')]//a[@data-qtip='Previous Page']"));
    public SelenideElement nextPage = $(By.xpath(".//div[starts-with(@id,'multisitembasic')]//a[@data-qtip='Next Page']"));
    public SelenideElement lastPage = $(By.xpath(".//div[starts-with(@id,'multisitembasic')]//a[@data-qtip='Last Page']"));
    public SelenideElement refresh = $(By.xpath(".//div[starts-with(@id,'multisitembasic')]//a[@data-qtip='Refresh']"));
    public SelenideElement gotoinput = $(By.xpath(".//div[starts-with(@id,'multisitembasic')]//input[@name='gotoinput']"));
    public SelenideElement go = $(By.xpath(".//div[starts-with(@id,'multisitembasic')]//span[text()='Go']"));
    public SelenideElement selectPage = $(By.id(".//*[(@data-ref='bodyEl') and (@role='presentation')]"));
}
