package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.InboundRoutes.Add_Inbound_Route;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import java.util.ArrayList;

import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Inbound_Route {

    public String s_extensin = "e";
    public String s_voicemail = "v";
    public String s_ringGroup = "r";
    public String s_queue = "q";
    public String s_conference = "c";
    public String s_disa = "d";
    public String s_callback = "C";
    public String s_faxToMail = "F";
    public String s_customPrompt = "P";
    public String s_ivr = "i";

    public String add_Inbound_Route_Interface = "Ext.getCmp('st-ir-name').up('inrouter-edit')";
    public String destinationType = "st-ir-desttype";
    public String destination = "st-ir-dest";
    public String list = "st-ir-trunkinfo";

    public SelenideElement name = $(By.id("st-ir-name-inputEl"));
    public SelenideElement DIDPatem = $(By.id("st-ir-didprefix-inputEl"));
    public SelenideElement callIDPattem = $(By.xpath("st-ir-calleridprefix-inputEl"));

    public SelenideElement enableTimeCondition = $(By.id("st-ir-enabletimecondition-displayEl"));
    public SelenideElement addTimeCondition = $(By.id("st-ir-addtimecondition"));
    public SelenideElement destination_button = $(By.id("st-ir-desttype-trigger-picker"));
    public SelenideElement distinctiveRingtone = $(By.id("st-ir-ringtone-inputEl"));
    public SelenideElement enableFaxDetection = $(By.id("st-ir-faxdetect-displayEl"));
    public SelenideElement faxDestination_button = $(By.id("st-ir-faxto-trigger-picker"));
    public SelenideElement faxDestinationNumber = $(By.id("st-ir-faxdest-inputEl"));
    public SelenideElement destinationInput = $(By.id("st-ir-extensionrange-inputEl"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'inrouter-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'inrouter-edit-')]//span[text()='Cancel']"));



    public void SetTimeConditionTableviewDestination(int row, int column, String des) throws InterruptedException {
        Thread.sleep(2000);
        System.out.println("1231"+ "Ext.query('#'+Ext.query('#'+ Ext.query('#'+"+add_Inbound_Route_Interface+".down('tableview').id + ' [data-recordindex]')["+row+"].id + ' tr td')["+column+"].id + ' div div')[0].id");
        String cellId = String.valueOf(return_executeJs("Ext.query('#'+Ext.query('#'+ Ext.query('#'+"+add_Inbound_Route_Interface+".down('tableview').id + ' [data-recordindex]')["+--row+"].id + ' tr td')["+column+"].id + ' div div')[0].id"));
        executeJs("Ext.getCmp('"+cellId+"').setValue('"+des+"')");
    }

    public void SetTimeConditionTableviewDestition(int row, int column, int extIndex, String extName) throws InterruptedException {
        Thread.sleep(2000);
        System.out.println("456  "+ "Ext.query('#'+Ext.query('#'+ Ext.query('#'+"+add_Inbound_Route_Interface+".down('tableview').id + ' [data-recordindex]')["+row+"].id + ' tr td')["+column+"].id + ' div div')[0].id");
        String cellId = String.valueOf(return_executeJs("Ext.query('#'+Ext.query('#'+ Ext.query('#'+"+add_Inbound_Route_Interface+".down('tableview').id + ' [data-recordindex]')["+--row+"].id + ' tr td')["+column+"].id + ' div div')[0].id"));
        String id =  String.valueOf(listGetId(cellId,"name",extName)) ;
        System.out.println("Ext.getCmp('"+cellId+"').setValue('"+id+"')");
        executeJs("Ext.getCmp('"+cellId+"').setValue('"+id+"')");
    }

    public void SetDestination(String type,String recordname,String m_destination) throws InterruptedException {
        executeJs("Ext.getCmp('"+destinationType+"').setValue('"+type+"')");

        String id = String.valueOf(listGetId(destination,recordname,m_destination));
        listSetValue(destination,id);

    }
}