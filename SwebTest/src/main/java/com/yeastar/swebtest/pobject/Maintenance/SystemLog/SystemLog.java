package com.yeastar.swebtest.pobject.Maintenance.SystemLog;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class SystemLog {

    public String grid = "Ext.getCmp('maintance').down('systemlog').down('tableview')";
    public String grid_Mask = "Ext.getCmp('maintance').down('systemlog').down('loadmask')";
    public int gridColumn_Checked = 0;
    public int gridColumn_Name = 1;
    public int gridDownload = 0;
    public int gridDelete = 1;
    /**
     * System Log Settings
     */
    public SelenideElement information = $(By.id("mt-sl-enableverbose-displayEl"));
    public String information_id = "mt-sl-enableverbose";
    public SelenideElement notice = $(By.id("mt-sl-enablenotice-displayEl"));
    public String notice_id = "mt-sl-enablenotice";
    public SelenideElement warning = $(By.id("mt-sl-enablewarning-displayEl"));
    public String warning_id = "mt-sl-enablewarning";
    public SelenideElement error = $(By.id("mt-sl-enableerror-displayEl"));
    public String error_id = "mt-sl-enableerror";
    public SelenideElement debug = $(By.id("mt-sl-enabledebug-displayEl"));
    public String debug_id = "mt-sl-enabledebug";
    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'systemlog')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'systemlog')]//span[text()='Cancel']"));

    /**
     * System Log
     */
    public SelenideElement download = $(By.xpath(".//div[starts-with(@id,'systemlog')]//span[text()='Download']"));
    public SelenideElement delete = $(By.xpath(".//div[starts-with(@id,'systemlog')]//span[text()='Delete']"));

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
