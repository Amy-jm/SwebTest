package com.yeastar.swebtest.pobject.CDRandRecordings;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;


import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class CDRandRecordings {

    public String grid = "Ext.getCmp('cdr-record').down('grid')";
    public String grid_Mask = "Ext.getCmp('cdr-record').down('loadmask')";
    public int gridColumn_Time = 0;
    public int gridColumn_CallFrom = 1;
    public int gridColumn_CallTo = 2;
    public int gridColumn_CallDuration = 3;
    public int gridColumn_TalkFuration = 4;

//    30.6.0.x
    public int gridColumn_Status = 5;
    public int gridColumn_SourceTrunk = 6;
    public int gridColumn_DestinationTrunk = 7;
    public int gridColumn_CommunicationTrunk = 8;
    public int gridColumn_PinCode = 9;

    public int gridPlay = 0; //仅搜索img
    public int gridDownload = 1; //仅搜索img
    public int gridDeleteRecord = 2; //仅搜索img
    public int gridDeleteCDR = 3; //仅搜索img

    public String gridColumnColor_Gray = "gray";

    public String playToExtension_id = "cdr-play-ext";
    public SelenideElement recordingInfo_CallFrom = $(By.id("src-inputEl"));
    public SelenideElement recordingInfo_CallTo =  $(By.id("dst-inputEl"));
    public SelenideElement recordingInfo_Durantion =  $(By.id("duration-inputEl"));
    public SelenideElement recordingInfo_Play = $(By.xpath(".//div[starts-with(@id,'cdr-play-block-outerCt')]//span[text()=\"Play\"]"));

    public SelenideElement time_start = $(By.id("cr-startdate-inputEl"));
    public SelenideElement time_end = $(By.id("cr-enddate-inputEl"));
    public SelenideElement time_ok = $(By.xpath(".//span[starts-with(@id,\"button-\") and text()=\"OK\"]"));
    public SelenideElement callFrom = $(By.id("cr-src-inputEl"));
    public SelenideElement callTo = $(By.id("cr-dst-inputEl"));
    public SelenideElement callDuration = $(By.id("cr-duration-inputEl"));
    public SelenideElement talkDuration = $(By.id("cr-billable-inputEl"));
    public String status = "cr-disposition";
    public SelenideElement includeRecordingFiles = $(By.id("cr-isrecord-displayEl"));
//    public SelenideElement search = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Search\"]"));
    public SelenideElement search = $(By.id("cr-search"));

    public SelenideElement advancedOptions = $(By.xpath(".//a[starts-with(@class,\"cp-link-before\") and text()=\"Advanced Options\"]"));
                                                            // .//a[starts-with(@id,\"cdr-play-block-innerCt\") and text()=\"Paly\"]
    public SelenideElement trunk = $(By.id("cr-trunk-trigger-picker"));
    public SelenideElement communicationType = $(By.id("cr-calltype-trigger-picker"));
    public SelenideElement PINCode = $(By.id("cr-accountcode-inputEl"));
    public SelenideElement numberFuzzySearch = $(By.id("cr-fuzzymatch-displayEl"));

    public SelenideElement downloadCDR = $(By.xpath(".//div[starts-with(@id,'cdr-record')]//span[text()='Download CDR']"));
    public SelenideElement downloadRecordings = $(By.xpath(".//div[starts-with(@id,'cdr-record')]//span[text()='Download Recordings']"));
    public SelenideElement deleteCDR = $(By.xpath(".//div[starts-with(@id,'cdr-record')]//span[text()='Delete CDR']"));

    public SelenideElement CDR_set = $(By.xpath(".//*[@src='images/cdr-set.png']"));

    public SelenideElement delete_yes = $(By.xpath(".//div[starts-with(@id,'messagebox-')]//span[text()='Yes']"));
    public SelenideElement delete_no = $(By.xpath(".//div[starts-with(@id,'messagebox-')]//span[text()='No']"));

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
    public SelenideElement maxWindows = $(By.xpath(".//div[@id='cdr-record_header-innerCt']//div[@class='x-tool-img x-tool-maximize']"));
    public SelenideElement minWindows = $(By.xpath(".//div[@id='cdr-record_header-innerCt']//div[@class='x-tool-img x-tool-restore']"));
    public SelenideElement closeWindows = $(By.xpath(".//div[@id='cdr-record_header-innerCt']//div[@class='x-tool-img x-tool-close']"));









}
