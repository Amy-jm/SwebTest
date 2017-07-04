package com.yeastar.swebtest.pobject.CDRandRecordings;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;


import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class CDRandRecordings {
    public SelenideElement time_start = $(By.id("cr-startdate-inputEl"));
    public SelenideElement time_end = $(By.id("cr-enddate-inputEl"));
    public SelenideElement callFrom = $(By.id("cr-src-inputEl"));
    public SelenideElement callTo = $(By.id("cr-dst-inputEl"));
    public SelenideElement callDuration = $(By.id("cr-duration-inputEl"));
    public SelenideElement talkDuration = $(By.id("cr-billable-inputEl"));
    public SelenideElement status = $(By.id("cr-disposition-trigger-picker"));
    public SelenideElement includeRecordingFiles = $(By.id("cr-isrecord-displayEl"));
    public SelenideElement search = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Search\"]"));

    public SelenideElement advancedOptions = $(By.xpath(".//a[starts-with(@class,\"cp-link-before\") and text()=\"Advanced Options\"]"));

    public SelenideElement trunk = $(By.id("cr-trunk-trigger-picker"));
    public SelenideElement communicationType = $(By.id("cr-calltype-trigger-picker"));
    public SelenideElement PINCode = $(By.id("cr-accountcode-inputEl"));
    public SelenideElement numberFuzzySearch = $(By.id("cr-fuzzymatch-displayEl"));

    public SelenideElement downloadCDR = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Download CDR\"]"));
    public SelenideElement downloadRecordings = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Download Recordings\"]"));
    public SelenideElement deleteCDR = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-ys-theme-small\") and text()=\"Delete CDR\"]"));

    public SelenideElement CDR_set = $(By.xpath(".//*[@src='images/cdr-set.png']"));


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
