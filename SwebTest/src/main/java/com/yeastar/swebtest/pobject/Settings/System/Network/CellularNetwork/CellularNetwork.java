package com.yeastar.swebtest.pobject.Settings.System.Network.CellularNetwork;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class CellularNetwork {
    public SelenideElement cellularNetwork = $(By.xpath(".//div[starts-with(@id,'network')]//span[ text()='Cellular Network']"));

    /**
     * Dial-up Settings
     */


    /**
     * Data Control
     */
}
