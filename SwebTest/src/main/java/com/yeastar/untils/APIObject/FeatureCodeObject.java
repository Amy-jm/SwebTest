package com.yeastar.untils.APIObject;

import top.jfunc.json.JsonArray;
import top.jfunc.json.impl.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: SwebTest
 * @description: feature code object
 * @author: huangjx@yeastar.com
 * @create: 2020/11/19
 */
public class FeatureCodeObject {
    public List<FeatureCodeObject.OfficeTimePermitList> officeTimePermitLists;
    public class OfficeTimePermitList{
        public String text;
        public String value;
        public String type;
        public OfficeTimePermitList(JSONObject jsonObject){
            this.text = getValueByString(jsonObject,"text");
            this.value = getValueByString(jsonObject,"value");
            this.type = getValueByString(jsonObject,"type");
        }
    }

    public FeatureCodeObject(JSONObject jsonObject){
        this.officeTimePermitLists = new ArrayList<>();
        if (jsonObject.containsKey("office_time_permit_list")){
            JsonArray m_memberist = jsonObject.getJsonArray("office_time_permit_list");
            for (int i=0; i<m_memberist.size(); i++){
                this.officeTimePermitLists.add(new FeatureCodeObject.OfficeTimePermitList((JSONObject) m_memberist.getJsonObject(i)));
            }
        }
    }
    private String getValueByString(JSONObject jsonObject,String key){
        if(jsonObject.containsKey(key)){
            return jsonObject.getString(key);
        }else{
            return "";
        }
    }

}
