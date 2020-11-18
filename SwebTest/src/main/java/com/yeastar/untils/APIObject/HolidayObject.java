package com.yeastar.untils.APIObject;

import top.jfunc.json.impl.JSONObject;

/**
 * @program: SwebTest
 * @description: Holiday object
 * @author: huangjx@yeastar.com
 * @create: 2020/11/17
 */
public class HolidayObject {
    public int id;
    public String name;
    public String type;
    public String date;
    public String month;
    public String howWeek;
    public String weekday;

    public HolidayObject(JSONObject jsonObject){
        this.id = Integer.parseInt(getValueByString(jsonObject,"id"));
        this.name = getValueByString(jsonObject,"name");
        this.type = getValueByString(jsonObject,"type");
        this.date = getValueByString(jsonObject,"date");
        this.howWeek = getValueByString(jsonObject,"howWeek");
        this.weekday = getValueByString(jsonObject,"weekday");
    }


    private String getValueByString(JSONObject jsonObject,String key){
        if(jsonObject.containsKey(key)){
            return jsonObject.getString(key);
        }else{
            return "";
        }
    }
}
