package com.yeastar.swebtest.pobject.Me.Me_CDRandRecording;

import com.codeborne.selenide.SelenideElement;
import com.yeastar.swebtest.driver.SwebDriver;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/7/21.
 */
public class Me_CDRandRecording extends SwebDriver{
    public  String grid = "Ext.getCmp('mesetting').down('mecdr').down('tableview')";
    public  String grid_Mask = "Ext.getCmp('mesetting').down('mecdr').down('loadmask')";

    public int gridColumn_Time = 0;
    public int gridColumn_CallFrom =1;
    public int gridColumn_CallTo = 2;
    public int gridColumn_CallDuration =3;
    public int gridColumn_TalkDuration = 4;
    public int gridColumn_Status = 5;
    public int gridColumn_CommunicationType =6;
    public int     gridColumn_CallIp =7;

    public String gridColumnColor_Gray = "gray";
    public int gridPlay = 0;
    public int gridDownload = 1;
    public int gridDelete = 2;


    public SelenideElement search = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Search\"]"));

    /**
     *关闭CDR页面
     */
    public static void me_closeCDRRecord(){
        try {
            Thread.sleep(500);
            executeJs("Ext.get(Ext.query('#mesetting_header-targetEl'+ ' .x-tool-close')[0].id).dom.click()");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
