package com.yeastar.linkustest.pobject;

import org.openqa.selenium.By;

public class Calling {
    public By caller_extensionName = By.id("com.yeastar.linkus:id/incall_name_tv");
    public By caller_callTime = By.id("com.yeastar.linkus:id/incall_time_tv");
    public By mute=By.xpath("//*[@resource-id=\"com.yeastar.linkus:id/incall_btn_gv\"]/android.widget.RelativeLayout[1]");;
    public By disaplad=By.xpath("//*[@resource-id=\"com.yeastar.linkus:id/incall_btn_gv\"]/android.widget.RelativeLayout[2]");;
    public By speaker=By.xpath("//*[@resource-id=\"com.yeastar.linkus:id/incall_btn_gv\"]/android.widget.RelativeLayout[3]");;
    public By history=By.xpath("//*[@resource-id=\"com.yeastar.linkus:id/incall_btn_gv\"]/android.widget.RelativeLayout[4]");;
    public By hold=By.xpath("//*[@resource-id=\"com.yeastar.linkus:id/incall_btn_gv\"]/android.widget.RelativeLayout[5]");;
    public By contacts=By.xpath("//*[@resource-id=\"com.yeastar.linkus:id/incall_btn_gv\"]/android.widget.RelativeLayout[6]");;
    public By transfer=By.xpath("//*[@resource-id=\"com.yeastar.linkus:id/incall_btn_gv\"]/android.widget.RelativeLayout[7]");;
    public By record =By.xpath("//*[@resource-id=\"com.yeastar.linkus:id/incall_btn_gv\"]/android.widget.RelativeLayout[8]");
    public By call_hangup = By.id("com.yeastar.linkus:id/incall_refuse_iv");


    //被叫方
    public By calling_extensionName = By.id("com.yeastar.linkus:id/incall_name_tv");
    public By calling_callTime = By.id("com.yeastar.linkus:id/incall_time_tv");
    public By calling_hangup = By.id("com.yeastar.linkus:id/img_ring_decline");
    public By calling_answer = By.id("com.yeastar.linkus:id/img_ring_accept");
    public By calling_trunk = By.id("com.yeastar.linkus:id/incall_trunk_tv");

    public By reaportCallQuality = By.id("com.yeastar.linkus:id/img_report_call_quality");
    /* 通话中的键盘按钮 */
    public By back = By.id("com.yeastar.linkus:id/actionbar_left_iv");
    public By num0 = By.name("0");
    public By num1 = By.name("1");
    public By num2 = By.name("2");
    public By num3 = By.name("3");
    public By num4 = By.name("4");
    public By num5 = By.name("5");
    public By num6 = By.name("6");
    public By num7 = By.name("7");
    public By num8 = By.name("8");
    public By num9 = By.name("9");
    public By numStar = By.name("*");
    public By numPound = By.name("#");
    public By dial = By.id("com.yeastar.linkus:id/incall_dial_iv");
    public By inputDelete = By.id("com.yeastar.linkus:id/tab_dial_delete_iv");
    public By dialPadBackDown = By.id("com.yeastar.linkus:id/incall_history_container");

    //转移时出现
    public By transfer_history = By.id("com.yeastar.linkus:id/incall_history_container");
    public By transfer_contacts = By.id("com.yeastar.linkus:id/incall_contact_container");
    public By transfer_confirm = By.id("com.yeastar.linkus:id/incall_transfer_confirm_tv");
    public By transfer_cancel = By.id("com.yeastar.linkus:id/incall_transfer_cancel_tv");
    public By transfer_attendedBlind = By.id("com.yeastar.linkus:id/actionbar_transfer_tv");

    public By transfer_name1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/incall_left_contact']/android.widget.LinearLayout/android.widget.TextView");
    public By transfer_status1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/incall_left_contact']/android.widget.LinearLayout/android.widget.Chronometer");

    public By transfer_name2 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/incall_center_contact']/android.widget.LinearLayout/android.widget.TextView");
    public By transfer_status2 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/incall_center_contact']/android.widget.LinearLayout/android.widget.Chronometer");

    public By transfer_name3 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/incall_right_contact']/android.widget.LinearLayout/android.widget.TextView");
    public By transfer_status3 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/incall_right_contact']/android.widget.LinearLayout/android.widget.Chronometer");

    //呼叫等待页面
    public By callWait_name = By.xpath("//*[@resource-id='com.yeastar.linkus:id/incall_hold_contact']/android.widget.LinearLayout[1]/android.widget.TextView");
    public By callWait_time = By.xpath("//*[@resource-id='com.yeastar.linkus:id/incall_hold_contact']/android.widget.LinearLayout[1]/android.widget.Chronometer");//Hold
    public By callWait_accpet = By.id("com.yeastar.linkus:id/layout_hold_answer");
    public By callWait_reject = By.id("com.yeastar.linkus:id/layout_reject");
    public By callWait_hangupCurrentCall = By.id("com.yeastar.linkus:id/layout_hangup_answer");

    //会议室界面---尚未获取成功
    public By conference_exit = By.id("com.yeastar.linkus:id/conference_exit_btn");
    public By conference_delMember = By.name("Delete");
    public By conference_muteBtn = By.name("Mute");
    public By conference_unmuteBtn = By.name("Unmute");

    public By conference_tip = By.id("com.yeastar.linkus:id/conference_tip_tv");
    public By conference_name = By.id("com.yeastar.linkus:id/conference_name_tv");
    public By conference_time = By.id("com.yeastar.linkus:id/conference_time_tv");

    public By conferece_avatar1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[1]/android.widget.RelativeLayout/android.widget.ImageView");
    public By conference_name1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[1]/android.widget.TextView[1]");
    public By conference_num1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[1]/android.widget.TextView[2]");
    public By conference_mute1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[1]/android.widget.ImageView[1]");
    public By conference_status1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[1]/android.widget.ImageView[2]");

    public By conferece_avatar2 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[2]/android.widget.RelativeLayout/android.widget.ImageView");
    public By conference_name2 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[2]/android.widget.TextView[1]");
    public By conference_num2 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[2]/android.widget.TextView[2]");
    public By conference_mute2 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[2]/android.widget.ImageView[1]");
    public By conference_status2 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[2]/android.widget.ImageView[2]");

    public By conferece_avatar3 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[3]/android.widget.RelativeLayout/android.widget.ImageView");
    public By conference_name3 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[3]/android.widget.TextView[1]");
    public By conference_num3 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[3]/android.widget.TextView[2]");
    public By conference_mute3 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[3]/android.widget.ImageView[1]");
    public By conference_status3 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[3]/android.widget.ImageView[2]");

    public By conference_name4 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[4]/android.widget.TextView[1]");
    public By conference_num4 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[4]/android.widget.TextView[2]");
    public By conference_mute4 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[4]/android.widget.ImageView[1]");
    public By conference_status4 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/conference_member_gv']/android.widget.RelativeLayout[4]/android.widget.ImageView[2]");
}
