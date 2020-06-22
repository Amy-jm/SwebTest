package com.yeastar.page.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public interface ICdrPageElement {

    SelenideElement ele_download_cdr_btn = $(By.xpath("//span[contains(text(),'Download CDR')]"));
    SelenideElement ele_delete_cdr_btn =$(By.xpath("//span[contains(text(),'Delete')]"));
    SelenideElement ele_refresh_cdr_btn = $(By.xpath("//span[contains(text(),'Refresh')]"));

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
