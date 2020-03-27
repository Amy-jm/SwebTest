package com.yeastar.swebtest.pobject;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.swebtest.driver.Config.pageDeskTop;
import static com.yeastar.swebtest.driver.Config.sort_ascendingOrder;
import static com.yeastar.swebtest.driver.SwebDriver.gridContent;
import static com.yeastar.swebtest.driver.SwebDriver.gridFindRowByColumn;
import static com.yeastar.swebtest.driver.SwebDriver.ys_waitingTime;

/**
 * Created by Yeastar on 2017/8/15.
 */
public class PbxMonitor {

    public String grid_Trunks = "Ext.getCmp('pbxmonitor').down('b-trunkstatus')";
    public int gridTrunks_Status = 0;
    public int gridTrunks_Name = 1;
    public int gridTrunks_Type = 2;
    public int gridTrunks_Hostname = 3;

//    会议室页面
    public String grid_Conference = "Ext.getCmp('pbxmonitor').down('b-conferencestatus')";
    public int gridConference_number =0;
    public int gridConference_name = 1;
    public int gridModerator =2;
    public int gridInConference=3;
    public int gridStartTime=4;

    public String Status_UP = "Up";
    public SelenideElement extension = $(By.xpath(".//div[starts-with(@id,'pbxmonitor')]//span[text()='Extension']"));
    public SelenideElement trunks = $(By.xpath(".//div[starts-with(@id,'pbxmonitor')]//span[text()='Trunks']"));
    public SelenideElement concurrent_Call = $(By.xpath(".//div[starts-with(@id,'pbxmonitor')]//span[text()='Concurrent Call']"));
    public SelenideElement conference = $(By.xpath(".//div[starts-with(@id,'pbxmonitor')]//span[text()='Conference']"));

//获取到指定会议室的成员数量
    public String getInConference_num(String conferenceName){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.pbxmonitorShortcut.click();
        conference.click();
        ys_waitingTime(1000);
        int row= gridFindRowByColumn(grid_Conference,gridConference_name,conferenceName,sort_ascendingOrder);
        String num = gridContent(grid_Conference,row,gridInConference);
        return num;
    }

}
