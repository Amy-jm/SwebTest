package com.yeastar.untils.APIObject;

import top.jfunc.json.JsonArray;
import top.jfunc.json.impl.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuOptionObject {

    public List<MemberList> extensionOptions;
    public List<MemberList> extGroupOptions;
    public List<MemberList> ivrOpenions;
    public List<MemberList> ringGroupOption;
    public List<MemberList> queueOption;

    public MenuOptionObject(JSONObject jsonObject){

        this.extensionOptions = new ArrayList<>();
        this.extGroupOptions = new ArrayList<>();
        this.ivrOpenions = new ArrayList<>();
        this.ringGroupOption = new ArrayList<>();
        this.queueOption = new ArrayList<>();

        if (jsonObject.containsKey("extension_options")){
            JsonArray m_memberist = jsonObject.getJsonArray("extension_options");
            for (int i=0; i<m_memberist.size(); i++){
                this.extensionOptions.add(new MenuOptionObject.MemberList((JSONObject) m_memberist.getJsonObject(i)));
            }
        }
        if (jsonObject.containsKey("ext_group_options")){
            JsonArray m_memberist = jsonObject.getJsonArray("ext_group_options");
            for (int i=0; i<m_memberist.size(); i++){
                System.out.println("!!!!!!!!!!!!!!");
                System.out.println(m_memberist.getJsonObject(i).toString());
                this.extGroupOptions.add(new MenuOptionObject.MemberList((JSONObject) m_memberist.getJsonObject(i)));
            }
        }
        if (jsonObject.containsKey("ivr_options")){
            JsonArray m_memberist = jsonObject.getJsonArray("ivr_options");
            for (int i=0; i<m_memberist.size(); i++){
                this.ivrOpenions.add(new MenuOptionObject.MemberList((JSONObject) m_memberist.getJsonObject(i)));
            }
        }
        if (jsonObject.containsKey("ring_group_options")){
            JsonArray m_memberist = jsonObject.getJsonArray("ring_group_options");
            for (int i=0; i<m_memberist.size(); i++){
                this.ringGroupOption.add(new MenuOptionObject.MemberList((JSONObject) m_memberist.getJsonObject(i)));
            }
        }
        if (jsonObject.containsKey("queue_options")){
            JsonArray m_memberist = jsonObject.getJsonArray("queue_options");
            for (int i=0; i<m_memberist.size(); i++){
                this.queueOption.add(new MenuOptionObject.MemberList((JSONObject) m_memberist.getJsonObject(i)));
            }
        }
    }

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

    private String getValueByString(JSONObject jsonObject,String key){
        if(jsonObject.containsKey(key)){
            return jsonObject.getString(key);
        }else{
            return "";
        }
    }
}
