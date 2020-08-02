package com.yeastar.untils.APIObject;

import top.jfunc.json.impl.JSONObject;

public class InboundRouteObject {

    public int id;
    public int pos;
    public String name;
    public String didOption;
    public String cidOption; //caller ID类型选项
    public String defDest;
    public String defDestValue;
    public String timeCondition;
    public String officeTimeDest;
    public String holidayDest;

    public InboundRouteObject(JSONObject jsonObject){
        this.id = Integer.parseInt(getValueByString(jsonObject,"id"));
        this.pos = jsonObject.getInteger("pos");
        this.name = getValueByString(jsonObject,"name");
        this.didOption = getValueByString(jsonObject,"did_option");
        this.cidOption = getValueByString(jsonObject,"cid_option");
        this.defDest = getValueByString(jsonObject,"def_dest");
        this.defDestValue = getValueByString(jsonObject,"def_dest_value");
        this.timeCondition = getValueByString(jsonObject,"time_condition");
        this.officeTimeDest = getValueByString(jsonObject,"office_time_dest");
        this.holidayDest = getValueByString(jsonObject,"holiday_dest");

    }

    private String getValueByString(JSONObject jsonObject,String key){
        if(jsonObject.containsKey(key)){
            return jsonObject.getString(key);
        }else{
            return "";
        }
    }
}
