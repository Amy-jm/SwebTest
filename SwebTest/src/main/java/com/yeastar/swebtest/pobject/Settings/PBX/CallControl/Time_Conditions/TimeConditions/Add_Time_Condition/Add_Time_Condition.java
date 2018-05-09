package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.Time_Conditions.TimeConditions.Add_Time_Condition;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import java.util.ArrayList;

import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.swebtest.driver.Config.add_time_condition;
import static com.yeastar.swebtest.driver.SwebDriver.executeJs;
import static com.yeastar.swebtest.driver.SwebDriver.ys_waitingTime;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Time_Condition {
    public SelenideElement name  = $(By.id("cc-tc-name-inputEl"));

    public SelenideElement starthour  = $(By.name("starthour"));
    public SelenideElement startminu  = $(By.name("startminu"));
    public SelenideElement endhour  = $(By.name("endhour"));
    public SelenideElement endminu  = $(By.name("endminu"));
    public SelenideElement addTime  = $(By.xpath(".//div[starts-with(@id,'timecondition-edit-')]//div[starts-with(@id,'panel-')]//div[9]//img"));

    public SelenideElement getAddTime(int row){
//        row表示第几行Time的加号+
        String path =  ".//div[starts-with(@id,'timecondition-edit-')]//div[starts-with(@id,'panel-')]//div["+ row +"]//div[9]//img";
        return $(By.xpath(path));
    }
    public SelenideElement getDeleteTime(int row){
        //        row表示第几行Time的删除按钮
        String path =  ".//div[starts-with(@id,'timecondition-edit-')]//div[starts-with(@id,'panel-')]//div["+ row +"]//div[8]//img";
        return $(By.xpath(path));
    }
    public SelenideElement all  = $(By.id("cc-tc-weekall-displayEl"));
    public SelenideElement sunday  = $(By.id("cc-tc-sun-displayEl"));
    public SelenideElement monday  = $(By.id("cc-tc-mon-displayEl"));
    public SelenideElement tuesday  = $(By.id("cc-tc-tue-displayEl"));
    public SelenideElement wednesday  = $(By.id("cc-tc-wen-displayEl"));
    public SelenideElement thursday  = $(By.id("cc-tc-thu-displayEl"));
    public SelenideElement friday  = $(By.id("cc-tc-fri-displayEl"));
    public SelenideElement saturday  = $(By.id("cc-tc-sat-displayEl"));

    public SelenideElement advancedOptions = $(By.id("cc-tc-enablemonth-displayEl"));

    //    Month:
    public SelenideElement all_month = $(By.id("cc-tc-monthall-displayEl"));
    public SelenideElement january = $(By.id("cc-tc-jan-displayEl"));
    public SelenideElement february = $(By.id("cc-tc-feb-displayEl"));
    public SelenideElement march = $(By.id("cc-tc-mar-displayEl"));
    public SelenideElement april = $(By.id("cc-tc-apr-displayEl"));
    public SelenideElement may = $(By.id("cc-tc-may-displayEl"));
    public SelenideElement june =$(By.id("cc-tc-jun-displayEl"));
    public SelenideElement july =$(By.id("cc-tc-jul-displayEl"));
    public SelenideElement august =$(By.id("cc-tc-aug-displayEl"));
    public SelenideElement september=$(By.id("cc-tc-sep-displayEl"));
    public SelenideElement october = $(By.id("cc-tc-oct-displayEl"));
    public SelenideElement november = $(By.id("cc-tc-nov-displayEl"));
    public SelenideElement december = $(By.id("cc-tc-dec-displayEl"));
    // Day:
    public SelenideElement all_day =$(By.id("cc-tc-dayall-displayEl"));
    public SelenideElement day1 =$(By.id("cp-day0"));
    public SelenideElement day2 =$(By.id("cp-day1"));
    public SelenideElement day3 =$(By.id("cp-day2"));
    public SelenideElement day4 =$(By.id("cp-day3"));
    public SelenideElement day5 =$(By.id("cp-day4"));
    public SelenideElement day6 =$(By.id("cp-day5"));
    public SelenideElement day7 =$(By.id("cp-day6"));
    public SelenideElement day8 =$(By.id("cp-day7"));
    public SelenideElement day9 =$(By.id("cp-day8"));
    public SelenideElement day10 =$(By.id("cp-day9"));
    public SelenideElement day11 =$(By.id("cp-day10"));
    public SelenideElement day12 =$(By.id("cp-day11"));
    public SelenideElement day13 =$(By.id("cp-day12"));
    public SelenideElement day14 =$(By.id("cp-day13"));
    public SelenideElement day15 =$(By.id("cp-day14"));
    public SelenideElement day16 =$(By.id("cp-day15"));
    public SelenideElement day17 =$(By.id("cp-day16"));
    public SelenideElement day18 =$(By.id("cp-day17"));
    public SelenideElement day19 =$(By.id("cp-day18"));
    public SelenideElement day20 =$(By.id("cp-day19"));
    public SelenideElement day21 =$(By.id("cp-day20"));
    public SelenideElement day22 =$(By.id("cp-day21"));
    public SelenideElement day23 =$(By.id("cp-day22"));
    public SelenideElement day24 =$(By.id("cp-day23"));
    public SelenideElement day25 =$(By.id("cp-day24"));
    public SelenideElement day26 =$(By.id("cp-day25"));
    public SelenideElement day27 =$(By.id("cp-day26"));
    public SelenideElement day28 =$(By.id("cp-day27"));
    public SelenideElement day29 =$(By.id("cp-day28"));
    public SelenideElement day30 =$(By.id("cp-day29"));
    public SelenideElement day31 =$(By.id("cp-day30"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'timecondition-edit')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'timecondition-edit')]//span[text()='Cancel']"));

//    StartTime:starthour/startminu/endhour/endminu其中一个
//    row表示是第几行的addtime
//    time表示要设置的时间,StartTime和time要一一对应
    public void setTime_One(int row,ArrayList<String> StartTime,ArrayList<String> time){
        for (int i = 0;i<StartTime.size();i++) {
            String.valueOf(executeJs("return  Ext.query(\"input[name='" + StartTime.get(i) + "']\")[" + (row-1) + "].value = '" + time.get(i) + "'"));
            ys_waitingTime(1000);
        }
    }
//    以下方法用于设置多行time的时间时
//    inti表示要从第几行开始设置time；
//    StartTime数组：starthour/startminu/endhour/endminu；用于存放要设置的是哪个空格
//    time数组是用于存放每行的时间设置；StartTime和time要一一对应
    public void setTime_More(int init,String[] StartTime,String[][] time){
            for (int j=0;j<time.length;j++){
                for (int k=0;k<time[j].length;k++){
                    String.valueOf(executeJs("return  Ext.query(\"input[name='" + StartTime[k] + "']\")[" + (init-1) + "].value = '" + time[j][k] + "'"));
                    ys_waitingTime(1000);
                }
                init ++;
            }
        }
}
