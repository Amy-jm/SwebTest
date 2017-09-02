package com.yeastar.swebtest.pobject.Settings.EventCenter.EventLog;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class EventLog {

    public String grid = "Ext.getCmp('control-panel').down('eventlog')";
    public String gridLoading = "Ext.getCmp('control-panel').down('eventlog').down('loadmask')";
    public int gridColumn_Time = 0;
    public int gridColumn_Type = 1;
    public int gridColumn_EventName = 2;
    public int gridColumn_EventMessage = 3;

    public SelenideElement eventType = $(By.id("st-el-eventtype-trigger-picker"));
    public SelenideElement eventName = $(By.id("st-el-eventname-trigger-picker"));
    public SelenideElement time_start = $(By.id("st-el-startdate-inputEl"));
    public SelenideElement time_end = $(By.id("st-el-enddate-inputEl"));
    public SelenideElement search = $(By.id("st-el-search-btnInnerEl"));

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
