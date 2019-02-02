package com.yeastar.linkustest.pobject.Me.Settings;

import org.openqa.selenium.By;

public class ReportProblem {

    public By back = By.id("com.yeastar.linkus:id/actionbar_left_iv");
    public By title = By.id("com.yeastar.linkus:id/actionbar_center_tv");

    public By appCrash = By.id("com.yeastar.linkus:id/bug_report_crash_no_response");
    public By voiceCall = By.id("com.yeastar.linkus:id/bug_report_voice_communication");
    public By callAnswering = By.id("com.yeastar.linkus:id/bug_report_telephone_answering");
    public By serverConnect = By.id("com.yeastar.linkus:id/bug_report_server_connection");
    public By others = By.id("com.yeastar.linkus:id/bug_report_other");

    public By appCrash_appCrash = By.xpath("//*[@resource-id='com.yeastar.linkus:id/lv_option']/android.widget.LinearLayout[1]/android.widget.ImageView");
    public By appCrash_appNotRespond = By.xpath("//*[@resource-id='com.yeastar.linkus:id/lv_option']/android.widget.LinearLayout[2]/android.widget.ImageView");
    public By appCrash_others= By.xpath("//*[@resource-id='com.yeastar.linkus:id/lv_option']/android.widget.LinearLayout[3]/android.widget.ImageView");
    public By appCrash_crashTime= By.name("//*[@text='Crash time']/../android.widget.TextView[2]");
    public By appCrash_addComment= By.xpath("//*[@text='Add comment']/../android.widget.TextView[2]");
    public By appCrash_email = By.id("com.yeastar.linkus:id/et_email");
    public By appCrash_submit = By.id("com.yeastar.linkus:id/btn_commit_bug_report");

    public By others_crashTime = By.id("com.yeastar.linkus:id/tv_problem_time");
    public By others_des = By.id("com.yeastar.linkus:id/et_problem_desc2");
    public By others_num = By.id("com.yeastar.linkus:id/tv_problem_desc_num");
    public By others_emial = By.id("com.yeastar.linkus:id/et_email");
    public By others_submit = By.id("com.yeastar.linkus:id/btn_commit_bug_report");
}
