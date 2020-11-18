package com.yeastar.untils.APIObject;

import top.jfunc.json.impl.JSONObject;

/**
 * @program: SwebTest
 * @description: office time object
 * @author: huangjx@yeastar.com
 * @create: 2020/11/17
 */
public class OfficeTimeObject {
    public int id;
    public String daysOfWeek;

    public OfficeTimeObject(JSONObject jsonObject){
        this.id = Integer.parseInt(getValueByString(jsonObject,"id"));
        this.daysOfWeek = getValueByString(jsonObject,"days_of_week");
    }


    private String getValueByString(JSONObject jsonObject,String key){
        if(jsonObject.containsKey(key)){
            return jsonObject.getString(key);
        }else{
            return "";
        }
    }
}
