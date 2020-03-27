package com.yeastar.swebtest.pobject.Maintenance;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by GaGa on 2017-05-19.
 */
public class Maintenance {
    public SelenideElement upgrade = $(By.xpath(".//span[starts-with(@class,\"x-tree-node-text \") and text()=\"Upgrade\"]"));
    public SelenideElement backupandRestore = $(By.xpath(".//span[starts-with(@class,\"x-tree-node-text \") and text()=\"Backup and Restore\"]"));
    public SelenideElement reboot = $(By.xpath(".//span[starts-with(@class,\"x-tree-node-text \") and text()=\"Reboot\"]"));
    public SelenideElement reset = $(By.xpath(".//span[starts-with(@class,\"x-tree-node-text \") and text()=\"Reset\"]"));
    public SelenideElement systemLog = $(By.xpath(".//span[starts-with(@class,\"x-tree-node-text \") and text()=\"System Log\"]"));
    public SelenideElement operationLog = $(By.xpath(".//span[starts-with(@class,\"x-tree-node-text \") and text()=\"Operation Log\"]"));
    public SelenideElement troubleshooting = $(By.xpath(".//span[starts-with(@class,\"x-tree-node-text \") and text()=\"Troubleshooting\"]"));






}
