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
    public String s_extension_range ="E";
    public String s_hangup = "h";
    public String s_voicemail = "v";
    public String s_ringGroup = "r";
    public String s_queue = "q";
    public String s_conference = "c";
    public String s_disa = "d";
    public String s_callback = "C";
    public String s_outboundRoute ="o";
    public String s_faxToMail = "F";
    public String s_customPrompt = "P";
    public String s_ivr = "i";

    public String add_Inbound_Route_Interface = "Ext.getCmp('st-ir-name').up('inrouter-edit')";

    public String list = "st-ir-trunkinfo";

    public SelenideElement name = $(By.id("st-ir-name-inputEl"));
    public SelenideElement DIDPatem = $(By.id("st-ir-didprefix-inputEl"));
    public SelenideElement callIDPattem = $(By.id("st-ir-calleridprefix-inputEl"));

    public String enableTimeCondition ="st-ir-enabletimecondition";
    public SelenideElement addTimeCondition = $(By.id("st-ir-addtimecondition"));
    public String destinationType = "st-ir-desttype";
    public String destination = "st-ir-dest";
    public String enableFaxDetection ="st-ir-faxdetect";
    public String faxDestination_button = "st-ir-faxto";
    public SelenideElement faxDestinationNumber = $(By.id("st-ir-faxdest-inputEl"));
    public SelenideElement destinationInput = $(By.id("st-ir-extensionrange-inputEl"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'inrouter-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'inrouter-edit-')]//span[text()='Cancel']"));



    public void SetTimeConditionTableviewDestination(int row, int column, String des)  {
        ys_waitingTime(2000);
        System.out.println("1231 "+ "Ext.query('#'+Ext.query('#'+ Ext.query('#'+"+add_Inbound_Route_Interface+".down('tableview').id + ' [data-recordindex]')["+row+"].id + ' tr td')["+column+"].id + ' div div')[0].id");
        String cellId = String.valueOf(return_executeJs("Ext.query('#'+Ext.query('#'+ Ext.query('#'+"+add_Inbound_Route_Interface+".down('tableview').id + ' [data-recordindex]')["+--row+"].id + ' tr td')["+column+"].id + ' div div')[0].id"));
        System.out.println("1231 Ext.getCmp('"+cellId+"').setValue('"+des+"')");
        executeJs("Ext.getCmp('"+cellId+"').setValue('"+des+"')");

    }

    public void SetTimeConditionTableviewDestition(int row, int column, String extName) {
        ys_waitingTime(2000);
        System.out.println("456  "+ "Ext.query('#'+Ext.query('#'+ Ext.query('#'+"+add_Inbound_Route_Interface+".down('tableview').id + ' [data-recordindex]')["+row+"].id + ' tr td')["+column+"].id + ' div div')[0].id");
        String cellId = String.valueOf(return_executeJs("Ext.query('#'+Ext.query('#'+ Ext.query('#'+"+add_Inbound_Route_Interface+".down('tableview').id + ' [data-recordindex]')["+--row+"].id + ' tr td')["+column+"].id + ' div div')[0].id"));
        String id =  String.valueOf(listGetId(cellId,"name",extName)) ;
        System.out.println(id);
        System.out.println("Ext.getCmp('"+cellId+"').setValue('"+id+"')");
        executeJs("Ext.getCmp('"+cellId+"').setValue('"+id+"')");
    }

//    目的地到分机时用改函数
    public void SetTimeConditionTableviewDestitionEx(int row, int column, String extName) {
        ys_waitingTime(2000);
        System.out.println("...  "+ "Ext.query('#'+Ext.query('#'+ Ext.query('#'+"+add_Inbound_Route_Interface+".down('tableview').id + ' [data-recordindex]')["+row+"].id + ' tr td')["+column+"].id + ' div div')[0].id");
        String cellId = String.valueOf(return_executeJs("Ext.query('#'+Ext.query('#'+ Ext.query('#'+"+add_Inbound_Route_Interface+".down('tableview').id + ' [data-recordindex]')["+--row+"].id + ' tr td')["+column+"].id + ' div div')[0].id"));
        String id =  String.valueOf(listGetId(cellId,extensionList,extName)) ;
        System.out.println(id);
        System.out.println("Ext.getCmp('"+cellId+"').setValue('"+id+"')");
        executeJs("Ext.getCmp('"+cellId+"').setValue('"+id+"')");
    }

    public void SetDestination(String type,String recordname,String m_destination) throws InterruptedException {
        executeJs("Ext.getCmp('"+destinationType+"').setValue('"+type+"')");

        String id = String.valueOf(listGetId(destination,recordname,m_destination));
        listSetValue(destination,id);

    }
}