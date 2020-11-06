package com.yeastar.untils.APIObject;

import top.jfunc.json.JsonArray;
import top.jfunc.json.impl.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RingGroupObject {

    public class MemberList{
        public String text;
        public String text2;
        public String value;
        public String type;
        public MemberList(JSONObject jsonObject){
            this.text = getValueByString(jsonObject,"text");
            this.text2 = getValueByString(jsonObject,"text2");
            this.value = getValueByString(jsonObject,"value");
            this.type = getValueByString(jsonObject,"type");
        }
    }

    public int id;
    public String number;
    public String name;
    public String ringStrategy;
    public List<MemberList> memberList;

    public RingGroupObject(JSONObject jsonObject){
        this.id = Integer.parseInt(getValueByString(jsonObject,"id"));
        this.number = getValueByString(jsonObject,"number");
        this.name = getValueByString(jsonObject,"name");
        this.ringStrategy = getValueByString(jsonObject,"ringStrategy");
        this.memberList = new ArrayList<>();

        if (jsonObject.containsKey("member_list")){
            JsonArray m_memberist = jsonObject.getJsonArray("member_list");
            for (int i=0; i<m_memberist.size(); i++){
                this.memberList.add(new RingGroupObject.MemberList((JSONObject) m_memberist.getJsonObject(i)));

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
