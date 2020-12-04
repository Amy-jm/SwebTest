package com.yeastar.untils.APIObject;

import top.jfunc.json.impl.JSONObject;

/**
 * @program: SwebTest
 * @description: speed dial object
 * @author: huangjx@yeastar.com
 * @create: 2020/11/19
 */
public class SpeedDialObject {
    public int id;
    public String code;
    public String phoneNumber;

    public SpeedDialObject(JSONObject jsonObject) {
        this.id = Integer.parseInt(getValueByString(jsonObject, "id"));
        this.code = getValueByString(jsonObject, "code");
        this.phoneNumber = getValueByString(jsonObject, "phoneNumber");
    }

    private String getValueByString(JSONObject jsonObject, String key) {
        if (jsonObject.containsKey(key)) {
            return jsonObject.getString(key);
        } else {
            return "";
        }
    }

}
