package com.yeastar.page.pseries.CallControl;

import com.codeborne.selenide.Condition;
import com.yeastar.page.pseries.BasePage;
import com.yeastar.untils.WaitUntils;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.*;

/**
 * @program: SwebTest
 * @description: business hours and holidays
 * @author: huangjx@yeastar.com
 * @create: 2020/06/30
 */
@Log4j2
public class BusinessHoursAndHolidaysPage extends BasePage implements IBusinessHoursAndHoildaysPageElement{
    public  String WEEKSDAY_XPATH = "//li[contains(text(),'%s')]";

    /**
     * 删除当前表格下所有数据
     * @return
     */
    public BusinessHoursAndHolidaysPage deleAllData(){
        deleAllTableData();
        return this;
    }


    /**
     * 设置 Business 开始时间
     * @param time
     * @return
     */
    public BusinessHoursAndHolidaysPage selectBusinessStartTime(String time){

        $(By.id("office_times_start")).shouldBe(Condition.visible).click();
        $(By.xpath("//input[contains(@placeholder,'Select time') and contains(@class,'ant-time-picker-panel-input')]")).
                shouldBe(Condition.visible).setValue(time);
//        actions().sendKeys(Keys.ENTER).build().perform();
        return this;
    }

    /**
     * 设置 Business 结束时间
     * @param time
     * @return
     */
    public BusinessHoursAndHolidaysPage selectBusinessEndTime(String time){

        $(By.id("office_times_end")).shouldBe(Condition.visible).click();
        $(By.xpath("//input[contains(@placeholder,'Select time') and contains(@class,'ant-time-picker-panel-input')]")).
                shouldBe(Condition.visible).setValue(time);
//        actions().sendKeys(Keys.ENTER).build().perform();
        return this;
    }

    /**
     * 设置 Break 结束时间
     * @param time
     * @return
     */
    public BusinessHoursAndHolidaysPage selectBreakEndTime(String time){
        $(By.id("reset_times_end")).shouldBe(Condition.visible).click();
        $(By.xpath("//input[contains(@placeholder,'Select time') and contains(@class,'ant-time-picker-panel-input')]")).
                shouldBe(Condition.visible).setValue(time);
//        actions().sendKeys(Keys.ENTER).build().perform();
        return this;
    }

    /**
     * 设置 break 开始时间
     * @param time
     * @return
     */
    public BusinessHoursAndHolidaysPage selectBreakStartTime(String time){

        $(By.id("reset_times_start")).shouldBe(Condition.visible).click();
        $(By.xpath("//input[contains(@placeholder,'Select time') and contains(@class,'ant-time-picker-panel-input')]")).
                shouldBe(Condition.visible).setValue(time);
//        actions().sendKeys(Keys.ENTER).build().perform();
        return this;
    }

    /**
     *  新增 business 时间
     * @param businessStartTime business 开始时间  eg."08:30"
     * @param businessEndTime   business 结束时间  eg."18:00"
     * @param breakStartTime    break 开始时间 eg. "12:00"
     * @param breakEndTime      break 结束时间 eg. "01:00"
     * @param days 选择天 eg[选择多天都过+号连接] BusinessHoursAndHolidaysPage.DAYS_ENUM.MON.getWeekday()+BusinessHoursAndHolidaysPage.DAYS_ENUM.FRI.getWeekday()
     * @return
     */
    public BusinessHoursAndHolidaysPage addBusinessHours(String businessStartTime,String businessEndTime,String breakStartTime,String breakEndTime,String days){

        addBtn.shouldBe(Condition.enabled).click();
        if(businessStartTime !="" && businessEndTime !=""){
           $(By.xpath("//label[contains(@title,\"Business Hours\")]//../..//span[contains(text(),'Add')]")).click();
           selectBusinessStartTime(businessStartTime);
           selectBusinessEndTime(businessEndTime);
       }
       if(breakStartTime != "" && breakEndTime != ""){
           $(By.xpath("//label[contains(@title,\"Break Hours\")]//../..//span[contains(text(),'Add')]")).click();
           selectBreakStartTime(breakStartTime);
           selectBreakEndTime(breakEndTime);
       }

        $(By.id("office_time_days_of_week")).click();//展开星期面板
       log.debug("[businessStartTime] "+businessEndTime+"[businessEndTime] "+businessEndTime+"[breakStartTime] "+breakStartTime+"[breakEndTime] "+breakEndTime+"[days string] "+days);
        sleep(WaitUntils.RETRY_WAIT);
       if(days.contains(DAYS_ENUM.MON.getWeekday())){
            $(By.xpath(String.format(WEEKSDAY_XPATH,DAYS_ENUM.MON.getWeekday()))).click();
       }

       if(days.contains(DAYS_ENUM.TUE.getWeekday())){
           $(By.xpath(String.format(WEEKSDAY_XPATH,DAYS_ENUM.TUE.getWeekday()))).click();
       }
       if(days.contains(DAYS_ENUM.WED.getWeekday())){
           $(By.xpath(String.format(WEEKSDAY_XPATH,DAYS_ENUM.WED.getWeekday()))).click();
       }
       if(days.contains(DAYS_ENUM.THU.getWeekday())){
           $(By.xpath(String.format(WEEKSDAY_XPATH,DAYS_ENUM.THU.getWeekday()))).click();
       }
       if(days.contains(DAYS_ENUM.FRI.getWeekday())){
           $(By.xpath(String.format(WEEKSDAY_XPATH,DAYS_ENUM.FRI.getWeekday()))).click();
       }
       if(days.contains(DAYS_ENUM.SAT.getWeekday())){
           $(By.xpath(String.format(WEEKSDAY_XPATH,DAYS_ENUM.SAT.getWeekday()))).click();
       }
       if(days.contains(DAYS_ENUM.SUN.getWeekday())){
           $(By.xpath(String.format(WEEKSDAY_XPATH,DAYS_ENUM.SUN.getWeekday()))).click();
       }
        sleep(WaitUntils.RETRY_WAIT);
        $(By.xpath("//div[@id=\"office_time_days_of_week\"]//i[contains(@aria-label,\"down\")]")).click();//关闭星期面板
        return this;
    }



    public enum DAYS_ENUM {
        SUN("Sunday"),
        MON("Monday"),
        TUE("Tuesday"),
        WED("Wednesday"),
        THU("Thursday"),
        FRI("Friday"),
        SAT("Saturday");

        private String weekday;

        DAYS_ENUM(String weekday) {
            this.weekday = weekday;
        }

        public String getWeekday() {
            return this.weekday;
        }
    }

}
