package com.yeastar.swebtest.pobject.Maintenance.OperationLog;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class OperationLog {

    public String grid = "Ext.getCmp('maintance').down('operationlog').down('tableview')";
    public String gridLoading = "Ext.getCmp('maintance').down('operationlog').down('loadmask')";
    public int gridColumn_Time = 0;
    public int gridColumn_User = 1;
    public int gridColumn_IP = 2;
    public int gridColumn_Operation = 3;
    public int gridColumn_Details =4;
    public int gridDetails  =0;

    public SelenideElement user = $(By.id("mt-ol-username-trigger-picker"));
    public SelenideElement IPAddress = $(By.id("mt-ol-ipaddress-inputEl"));
    public SelenideElement time_start = $(By.id("mt-ol-startdate-inputEl"));
    public SelenideElement time_end = $(By.id("mt-ol-enddate-inputEl"));
    public SelenideElement search = $(By.id("mt-ol-search-btnInnerEl"));
    public SelenideElement download = $(By.id("mt-ol-download-btnInnerEl"));

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
