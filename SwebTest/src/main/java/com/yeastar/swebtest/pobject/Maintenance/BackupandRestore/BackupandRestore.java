package com.yeastar.swebtest.pobject.Maintenance.BackupandRestore;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class BackupandRestore {

    public String grid = "Ext.getCmp('maintance').down('backupandrestore').down('tableview')";
    public String gridLoading = "Ext.getCmp('maintance').down('backupandrestore').down('loadmask')";

    public int gridColumn_Checked = 0;
    public int gridColumn_Name = 1;
    public int gridColumn_BackUpTime = 2;
    public int gridColumn_Memo = 3;
    public int gridDownload = 0;
    public int gridRestroe = 1;
    public int gridDelete = 2;
    /**
     * 功能按钮
     */

    public SelenideElement backup = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Backup\"]"));
    public SelenideElement upload = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Upload\"]"));
    public SelenideElement delete = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Delete\"]"));
    public SelenideElement backupSchedule = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Backup Schedule\"]"));

    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));

    /**
     * 页码功能
     */
    public SelenideElement firstPage = $(By.xpath(".//*[@data-qtip='First Page']"));
    public SelenideElement previousPage = $(By.xpath(".//*[@data-qtip='Previous Page']"));
    public SelenideElement nextPage = $(By.xpath(".//*[@data-qtip='Next Page']"));
    public SelenideElement lastPage = $(By.xpath(".//*[@data-qtip='Last Page']"));
    public SelenideElement refresh = $(By.xpath(".//*[@data-qtip='Refresh']"));
    public SelenideElement gotoinput = $(By.name("gotoinput"));
    public SelenideElement go = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-toolbar-small\") and text()=\"Go\"]"));
    public SelenideElement selectPage = $(By.id(".//*[(@data-ref='bodyEl') and (@role='presentation')]"));

}
