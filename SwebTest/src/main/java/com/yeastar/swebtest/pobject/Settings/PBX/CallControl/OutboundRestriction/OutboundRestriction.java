package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.OutboundRestriction;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class OutboundRestriction {

    public String grid = "Ext.getCmp('control-panel').down('constraint')";
    public String grid_Mask = "Ext.getCmp('control-panel').down('constraint').down('loadmask')";

    public int gridcolumn_Check = 0; //仅搜索.x-grid-row-checker
    public int gridcolumn_Name = 1;
    public int gridcolumn_TimeLimit = 2;
    public int gridcolumn_NumofCall = 3;
    public int gridcolumn_MemberExtension = 4;

    public int gridEdit = 0;
    public int gridDelete  = 1;


    public SelenideElement outboundRestriction = $(By.xpath(".//div[starts-with(@id,\"callcontrol\")]//span[ text()=\"Outbound Restriction\"]"));

    /**
     * 功能按钮
     */

    public SelenideElement add = $(By.xpath(".//div[starts-with(@id,'constraint-')]//span[text()='Add']"));
    public SelenideElement Import = $(By.xpath(".//div[starts-with(@id,'constraint-')]//span[text()='Import']"));
    public SelenideElement delete = $(By.xpath(".//div[starts-with(@id,'constraint-')]//span[text()='Delete']"));

    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));

    /**
     * 页码功能
     */
    public SelenideElement firstPage = $(By.xpath(".//div[starts-with(@id,'constraint')]//a[@data-qtip='First Page']"));
    public SelenideElement previousPage = $(By.xpath(".//div[starts-with(@id,'constraint')]//a[@data-qtip='Previous Page']"));
    public SelenideElement nextPage = $(By.xpath(".//div[starts-with(@id,'constraint')]//a[@data-qtip='Next Page']"));
    public SelenideElement lastPage = $(By.xpath(".//div[starts-with(@id,'constraint')]//a[@data-qtip='Last Page']"));
    public SelenideElement refresh = $(By.xpath(".//div[starts-with(@id,'constraint')]//a[@data-qtip='Refresh']"));
    public SelenideElement gotoinput = $(By.xpath(".//div[starts-with(@id,'constraint')]//input[@name='gotoinput']"));
    public SelenideElement go = $(By.xpath(".//div[starts-with(@id,'constraint')]//span[text()='Go']"));
//    public SelenideElement selectPage = $(By.id(".//div[starts-with(@id,'constraint')]//input[(@name='gotoinput')]"));

}
