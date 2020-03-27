package com.yeastar.swebtest.pobject.Settings.PBX.MultisiteInterconnect.BranchOffice.BranchOffice_Advanced;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/10/10.
 */
public class BranchOffice_Advanced {

    public SelenideElement advanced = $(By.xpath(".//div[starts-with(@id,'multisitembasic')]//span[text()=\"Advanced\"]"));

    /**
     * VOIP Settiongs
     */
    public String qualify = "st-multisite-qualify";
    public String  NAT = "st-multisite-nat";
    public String EnableSRTP = "st-multisite-enablesrtp";
    public String DTMFmode = "st-multisite-dtmfmode";


    /**
     * Other Settings
     */
    public String getCallerIDFrom = "st-multisite-getfrom";
    public String getDIDFrom = "st-multisite-getto";



    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,\"multisiteadvanced\")]//span[ text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,\"multisiteadvanced\")]//span[ text()=\"Cancel\"]"));
}
