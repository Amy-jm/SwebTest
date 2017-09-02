package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.IVR.Add_IVR_KeyPressEvent;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_IVR_KeyPressEvent {
    public SelenideElement keyPressEvent = $(By.xpath(".//div[starts-with(@id,'ivr-edit-')]//span[text()='Key Press Event']"));

    public SelenideElement press0 = $(By.id("key0action-trigger-picker"));
    public SelenideElement press1 = $(By.id("key1action-trigger-picker"));
    public SelenideElement press2 = $(By.id("key2action-trigger-picker"));
    public SelenideElement press3 = $(By.id("key3action-trigger-picker"));
    public SelenideElement press4 = $(By.id("key4action-trigger-picker"));
    public SelenideElement press5 = $(By.id("key5action-trigger-picker"));
    public SelenideElement press6 = $(By.id("key6action-trigger-picker"));
    public SelenideElement press7 = $(By.id("key7action-trigger-picker"));
    public SelenideElement press8 = $(By.id("key8action-trigger-picker"));
    public SelenideElement press9 = $(By.id("key9action-trigger-picker"));
    public SelenideElement pressj = $(By.id("keysharpaction-trigger-picker"));
    public SelenideElement pressx = $(By.id("keystaraction-trigger-picker"));

    public String s_press0 = "key0action";
    public String s_press1 = "key1action";
    public String s_press2 = "key2action";
    public String s_press3 = "key3action";
    public String s_press4 = "key4action";
    public String s_press5 = "key5action";
    public String s_press6 = "key6action";
    public String s_press7 = "key7action";
    public String s_press8 = "key8action";
    public String s_press9=  "key9action";
    public String s_pressj = "keysharpaction";
    public String s_pressx = "keystaraction";

    public String d_press0 = "key0dest";
    public String d_press1 = "key1dest";
    public String d_press2 = "key2dest";
    public String d_press3 = "key3dest";
    public String d_press4 = "key4dest";
    public String d_press5 = "key5dest";
    public String d_press6 = "key6dest";
    public String d_press7 = "key7dest";
    public String d_press8 = "key8dest";
    public String d_press9=  "key9dest";
    public String d_pressj = "keysharpdest";
    public String d_pressx = "keystardest";

    public String s_extensin = "e";
    public String s_voicemail = "v";
    public String s_ringGroup = "r";
    public String s_queue = "q";
    public String s_conference = "c";
    public String s_disa = "d";
    public String s_callback = "C";
    public String s_faxToMail = "F";
    public String s_customPrompt = "P";


    public SelenideElement timeout = $(By.id("timeoutaction-trigger-picker"));
    public SelenideElement invaild = $(By.id("invalidaction-trigger-picker"));


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'ivr-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'ivr-edit-')]//span[text()='Cancel']"));

}
