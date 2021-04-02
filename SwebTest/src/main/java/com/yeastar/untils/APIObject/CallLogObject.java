package com.yeastar.untils.APIObject;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import top.jfunc.json.impl.JSONObject;

@Log4j2
@Getter
public class CallLogObject {
    private JSONObject obj;
    public String errorcode;
    public String errmsg;
    public String perCallType;
    public String perCallerId;
    public String perClid;
    public String perContactName;
    public String perDate;
    public String perDcontext;
    public String perDisposition;
    public String perId;
    public String perNumber;
    public String perNumberType;
    public String perStatus;
    public String perTalkDuration;
    public String perTime;

    public CallLogObject(String respondJson, int index){
        JSONObject object = new JSONObject(respondJson);
        errorcode = object.getString("errcode").trim();
        errmsg  = object.getString("errmsg");

        if(!errorcode.trim().equals("0")){
            log.error("[CDR API] errorcode: "+errorcode+" errmsg:"+errmsg);
            return;
        }

        obj = (JSONObject) object.getJsonArray("personal_cdr_list").getJsonObject(index);
        perCallType = getValue("call_type");
        perCallerId = getValue("caller_id");
        perClid = getValue("clid");
        perContactName = getValue("contact_name");
        perDate = getValue("date");
        perDcontext = getValue("dcontext");
        perDisposition = getValue("disposition");
        perId = getValue("id");
        perNumber = getValue("number");
        perNumberType = getValue("number_type");
        perStatus = getValue("status");
        perTalkDuration = getValue("talk_duration");
        perTime = getValue("time");
    }

    private String getValue(String key){
        if(obj.containsKey(key)){
            return obj.getString(key);
        }
        return "";
    }

    /**
     * call log的number type
     */
    public enum NUMBER_TYPE{
        Mobile("mobile"),
        Personal("personal"),
        External("external"),
        Extension("extension");

        private final String alias;
        NUMBER_TYPE(String alias) {
            this.alias = alias;
        }
        @Override
        public String toString() {
            return this.alias;
        }
    }
}
