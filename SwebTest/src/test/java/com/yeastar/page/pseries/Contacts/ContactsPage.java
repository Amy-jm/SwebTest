package com.yeastar.page.pseries.Contacts;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.page.pseries.BasePage;
import org.openqa.selenium.By;

import java.util.ArrayList;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

public class ContactsPage extends BasePage{

    SelenideElement lastNameLabel = $(By.id("contact_add_last_name"));
    SelenideElement firstNameLabel = $(By.id("contact_add_first_name"));
    SelenideElement companyLabel = $(By.id("contact_add_company"));
    SelenideElement emailLabel = $(By.xpath("//div//input[@class=\"ant-input ant-select-search__field\"]"));
    SelenideElement addNumberBtn = $(By.className("add-footer"));
    String numberType = "contact_table_num_type%s";
    String number = "contact_table_number%s";
    SelenideElement numberTypeSelect = $(By.id(String.format(numberType,"0")));
    SelenideElement zipCodeLabel = $(By.id("contact_add_zip_code"));
    SelenideElement streetLabel = $(By.id("contact_add_street"));
    SelenideElement cityLabel = $(By.id("contact_add_city"));
    SelenideElement stateLabel = $(By.id("contact_add_city"));

    public ContactsPage createCompanyContacts(String lastName, String firstName, String company, String email, ArrayList<String> phoneList){
        addBtn.shouldBe(Condition.enabled).click();
        lastNameLabel.setValue(lastName);
        firstNameLabel.setValue(firstName);
        companyLabel.setValue(company);
        emailLabel.setValue(email);
        for(int i=0; i < phoneList.size(); i++){
            addNumberBtn.shouldBe(Condition.enabled).click();
            SelenideElement phoneNumberLabel = $(By.id(String.format(number, i)));
            phoneNumberLabel.setValue(phoneList.get(i));
        }
        saveBtn.shouldBe(Condition.enabled).click();
        return this;
    }

    public ContactsPage createPersonalContacts(String lastName, String firstName, String company, String email, ArrayList<String> phoneList){
        sleep(10000);
        if(addPerCont.exists()){
            addPerCont.shouldBe(Condition.enabled).click();
        }else
            addBtn.shouldBe(Condition.enabled).click();
        lastNameLabel.setValue(lastName);
        firstNameLabel.setValue(firstName);
        companyLabel.setValue(company);
        emailLabel.setValue(email);
        for(int i=0; i < phoneList.size(); i++){
            addNumberBtn.shouldBe(Condition.enabled).click();
            SelenideElement phoneNumberLabel = $(By.id(String.format(number, i)));
            phoneNumberLabel.setValue(phoneList.get(i));
        }
        saveBtn.shouldBe(Condition.enabled).click();
        return this;
    }
}
