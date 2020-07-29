package com.yeastar.page.pseries.CdrRecording;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.swebtest.driver.DataReader2.UI_MAP;

public interface ICdrPageElement {

    SelenideElement ele_download_cdr_btn = $(By.xpath("//span[contains(text(),'"+UI_MAP.getString("cdr_recording.cdr.download_cdr")+"')]"));
    SelenideElement ele_delete_cdr_btn =$(By.xpath("//span[contains(text(),'"+UI_MAP.getString("common.delete")+"')]"));
    SelenideElement ele_refresh_cdr_btn = $(By.xpath("//body//button[3]"));
    SelenideElement ele_delete_all_checkbox = $(By.xpath("//table//thead//input[1]"));
    enum  CDR_HEADER{
        ID("ID"),
        Time("Time"),
        Call_From("Call From"),
        Call_To("Call To"),
        Call_Duration("Call Duration(s)"),
        Ring_Durations("Ring Duration(s)"),
        Talk_Durations("Talk Duration(s)"),
        Status("Status"),
        Reason("Reason"),
        Source_Trunk("Source Trunk"),
        Destination_Trunk("Destination Trunk"),
        Communication_Type("Communication Type"),
        DID("DID"),
        DOD("DOD"),
        Caller_IP_Address("Caller IP Address"),
        Recording_File("Recording File"),
        Delete("Delete");

        private final String alias;

        CDR_HEADER(String alias) {
            this.alias = alias;
        }
        public String getAlias() {
            return alias;
        }
    }
}
