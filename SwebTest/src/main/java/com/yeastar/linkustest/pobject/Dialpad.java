package com.yeastar.linkustest.pobject;

import org.openqa.selenium.By;

public class Dialpad {
    public By dialpad_tab = By.id("com.yeastar.linkus:id/dial_layout");
    public  By all_page = By.id("com.yeastar.linkus:id/switch_left_tv");
    public  By missed_page = By.id("com.yeastar.linkus:id/switch_right_tv");

    /* 拨号盘按钮*/
    public  By dial_num = By.id("com.yeastar.linkus:id/tab_dial_input_tv");
    public  By num1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dial_dialpad_gv']/android.widget.RelativeLayout[1]");
    public  By num2 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dial_dialpad_gv']/android.widget.RelativeLayout[2]");
    public  By num3 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dial_dialpad_gv']/android.widget.RelativeLayout[3]");
    public  By num4 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dial_dialpad_gv']/android.widget.RelativeLayout[4]");
    public  By num5 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dial_dialpad_gv']/android.widget.RelativeLayout[5]");
    public  By num6 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dial_dialpad_gv']/android.widget.RelativeLayout[6]");
    public  By num7 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dial_dialpad_gv']/android.widget.RelativeLayout[7]");
    public  By num8 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dial_dialpad_gv']/android.widget.RelativeLayout[8]");
    public  By num9 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dial_dialpad_gv']/android.widget.RelativeLayout[9]");
    public  By numStar = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dial_dialpad_gv']/android.widget.RelativeLayout[10]");
    public  By num0 =    By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dial_dialpad_gv']/android.widget.RelativeLayout[11]");
    public  By numPound = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dial_dialpad_gv']/android.widget.RelativeLayout[12]");
    public  By dial = By.id("com.yeastar.linkus:id/tab_dial_call_iv");
    public  By delete = By.id("com.yeastar.linkus:id/tab_dial_delete_iv");
    public  By addNum = By.id("com.yeastar.linkus:id/tab_dial_add_iv");

    //通话记录 前三条
    public  By historyName1  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[1]");
    public  By historyCount1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[2]");
    public  By historyType1  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[3]");
    public  By historyTime1  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[4]");
    public  By historyDetail1= By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.ImageView");

    public  By historyName2  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[2]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[1]");
    public  By historyCount2 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[2]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[2]");
    public  By historyType2  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[2]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[3]");
    public  By historyTime2  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[2]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[4]");
    public  By historyDetail2= By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[2]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.ImageView");

    public  By historyName3  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[3]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[1]");
    public  By historyCount3 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[3]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[2]");
    public  By historyType3  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[3]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[3]");
    public  By historyTime3  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[3]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[4]");
    public  By historyDetail3= By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[3]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.ImageView");

    //前三条未接记录
    public  By missCallName1  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[1]");
    public  By missCallCount1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[2]");
    public  By missCallType1  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[3]");
    public  By missCallTime1  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[4]");
    public  By missCallDetail1= By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.ImageView");

    public  By missCallName2  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[2]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[1]");
    public  By missCallCount2 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[2]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[2]");
    public  By missCallType2  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[2]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[3]");
    public  By missCallTime2  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[2]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[4]");
    public  By missCallDetail2= By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[2]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.ImageView");

    public  By missCallName3  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[3]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[1]");
    public  By missCallCount3 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[3]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[2]");
    public  By missCallType3  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[3]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[3]");
    public  By missCallTime3  = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[3]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.TextView[4]");
    public  By missCallDetail3= By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_dialfragment_calllog_lv']/android.widget.FrameLayout[3]/android.widget.LinearLayout[1]/android.view.ViewGroup/android.widget.ImageView");

    /* 通话详情 */
//    public  By historyDetail = By.id("com.yeastar.linkus:id/calllog_edit_iv");
    public  By historyDetailBack = By.id("com.yeastar.linkus:id/actionbar_left_iv");
    public  By historyDetailTitle = By.id("com.yeastar.linkus:id/actionbar_center_tv");
    public  By historyDetailHeadIcon = By.id("com.yeastar.linkus:id/calllog_avatar_civ");
    //    public  By getHistoryDetailHeadIconPicture = By.id("com.yeastar.linkus:id/imageview");
    public  By historyDetailName = By.id("com.yeastar.linkus:id/call_log_name_tv");
    public  By historyDetailCall = By.id("com.yeastar.linkus:id/call_tv");
    public  By historyDetailChat = By.id("com.yeastar.linkus:id/im_tv");
    public  By historyDetailEmail = By.id("com.yeastar.linkus:id/email_tv");
    public  By historyDetailAdd = By.id("com.yeastar.linkus:id/add_tv");
    public  By historyDetailDate1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/calllog_lv']/android.widget.LinearLayout[1]/android.widget.TextView[1]");
    public  By historyDetailType1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/calllog_lv']/android.widget.LinearLayout[1]/android.widget.TextView[2]");
    public  By historyDetailTime1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/calllog_lv']/android.widget.LinearLayout[1]/android.view.View");
}
