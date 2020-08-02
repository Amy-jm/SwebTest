package com.yeastar.page.pseries.CallFeatures;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.swebtest.driver.DataReader2.UI_MAP;

public interface IQueuePageElement {

    /** 队列编辑页面Tab选项 **/
    enum QUEUE_TAB {
        BASIC("Basic"),
        QUEUE_MEMBERS(UI_MAP.getString("extension_trunk.extension_groups.members") ),
        QUEUE_PREFERENCE(UI_MAP.getString("call_features.queue.preference")),
        QUEUE_PANEL_PERMISSIONS(UI_MAP.getString("call_features.queue.permissions"));

        private final String alias;

        QUEUE_TAB(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            return alias;
        }
    }

    SelenideElement searchIpt = $(By.xpath("//input[@placeholder="+UI_MAP.getString("header.search_placeholder")+"]"));
    SelenideElement ele_delete_all_checkbox = $(By.xpath("//table//thead//input[1]"));

    SelenideElement ele_queue_basic_number_input = $(By.id("queue_basic_number"));
    SelenideElement ele_queue_basic_name_input = $(By.id("queue_basic_name"));
    SelenideElement ele_queue_basic_ring_strategy_combobox = $(By.id("queue_basic_ring_strategy"));

    SelenideElement ele_queue_dynamic_agents_left_table = $(By.id("transfer_left_queue_member_dynamic_extension"));
    SelenideElement ele_queue_dynamic_agents_right_table = $(By.id("transfer_right_queue_member_dynamic_extension"));
    SelenideElement ele_queue_add_dynamic_agents_btn = $(By.xpath("//body//div[@class='ant-card-body']//div//div[1]//div[2]//div[1]//div[1]//div[1]//div[1]//div[2]//button[1]"));
    SelenideElement ele_queue_del_dynamic_agents_btn = $(By.xpath("//body//div[@class='ant-card-body']//div//div[1]//div[2]//div[1]//div[1]//div[1]//div[1]//div[2]//button[2]"));
    SelenideElement ele_queue_top_dynamic_agents_btn = $(By.xpath("//body/div[@id='root']/div/section[@class='ant-layout ant-layout-has-sider']/section[@class='ant-layout']/div[@class='layout-content']/div/div[@class='ant-row']/div[@class='ant-col']/div[@class='ant-card ant-card-bordered ant-card-contain-tabs']/div[@class='ant-card-body']/div/div[1]/div[2]/div[1]/div[1]/div[2]/button[1]"));
    SelenideElement ele_queue_up_dynamic_agents_btn = $(By.xpath("//body/div[@id='root']/div/section[@class='ant-layout ant-layout-has-sider']/section[@class='ant-layout']/div[@class='layout-content']/div/div[@class='ant-row']/div[@class='ant-col']/div[@class='ant-card ant-card-bordered ant-card-contain-tabs']/div[@class='ant-card-body']/div/div[1]/div[2]/div[1]/div[1]/div[2]/button[2]"));
    SelenideElement ele_queue_down_dynamic_agents_btn = $(By.xpath("//body/div[@id='root']/div/section[@class='ant-layout ant-layout-has-sider']/section[@class='ant-layout']/div[@class='layout-content']/div/div[@class='ant-row']/div[@class='ant-col']/div[@class='ant-card ant-card-bordered ant-card-contain-tabs']/div[@class='ant-card-body']/div/div[1]/div[2]/div[1]/div[1]/div[2]/button[3]"));
    SelenideElement ele_queue_botton_dynamic_agents_btn = $(By.xpath("//body/div[@id='root']/div/section[@class='ant-layout ant-layout-has-sider']/section[@class='ant-layout']/div[@class='layout-content']/div/div[@class='ant-row']/div[@class='ant-col']/div[@class='ant-card ant-card-bordered ant-card-contain-tabs']/div[@class='ant-card-body']/div/div[1]/div[2]/div[1]/div[1]/div[2]/button[4]"));

    SelenideElement ele_queue_static_agents_left_table = $(By.id("transfer_left_queue_member_static_extension"));
    SelenideElement ele_queue_static_agents_right_table = $(By.id("transfer_right_queue_member_static_extension"));
    SelenideElement ele_queue_add_static_agents_btn = $(By.xpath("//div[@class='ant-card-body']//div[2]//div[2]//div[1]//div[1]//div[1]//div[1]//div[2]//button[1]"));
    SelenideElement ele_queue_del_static_agents_btn = $(By.xpath("//div[@class='ant-card-body']//div[2]//div[2]//div[1]//div[1]//div[1]//div[1]//div[2]//button[2]"));
}
