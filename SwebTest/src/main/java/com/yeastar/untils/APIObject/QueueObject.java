package com.yeastar.untils.APIObject;

import com.alibaba.fastjson.JSON;
import top.jfunc.json.JsonArray;
import top.jfunc.json.impl.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QueueObject {

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
    public String number ;
    public String name;
    public String ringStrategy;
    public List<MemberList> dynamicAgentList;
    public List<MemberList> staticAgentList;
    public List<MemberList> managerList;

    public QueueObject(JSONObject jsonObject){
        this.id = Integer.parseInt(getValueByString(jsonObject,"id"));
        this.number = getValueByString(jsonObject,"number");
        this.name = getValueByString(jsonObject,"name");
        this.ringStrategy = getValueByString(jsonObject,"ringStrategy");
        this.dynamicAgentList = new ArrayList<>();
        this.staticAgentList = new ArrayList<>();
        this.managerList = new ArrayList<>();

        if (jsonObject.containsKey("dynamic_agent_list")){
            JsonArray m_memberist = jsonObject.getJsonArray("dynamic_agent_list");
            for (int i=0; i<m_memberist.size(); i++){
                this.dynamicAgentList.add(new QueueObject.MemberList((JSONObject) m_memberist.getJsonObject(i)));
            }
        }
        if (jsonObject.containsKey("static_agent_list")){
            JsonArray m_memberist = jsonObject.getJsonArray("static_agent_list");
            for (int i=0; i<m_memberist.size(); i++){
                this.staticAgentList.add(new QueueObject.MemberList((JSONObject) m_memberist.getJsonObject(i)));
            }
        }
        if (jsonObject.containsKey("manager_list")){
            JsonArray m_memberist = jsonObject.getJsonArray("manager_list");
            for (int i=0; i<m_memberist.size(); i++){
                this.managerList.add(new QueueObject.MemberList((JSONObject) m_memberist.getJsonObject(i)));
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
