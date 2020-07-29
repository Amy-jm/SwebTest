package com.yeastar.untils;

import lombok.extern.log4j.Log4j2;
import top.jfunc.json.impl.JSONObject;

import java.io.IOException;

@Log4j2
public class CDRObject {
    private String prefix="cdr_recording.cdr.";
    private JSONObject obj;
    public String errorcode;
    public String errmsg;
    public String uid;
    public String time;
    public String callFrom;
    public String callTo;
    public String callDuration;
    public String ringDuration;
    public String talkDuration;
    public String status;
    public String reason;
    public String sourceTrunk;
    public String destinationTrunk;
    public String communicatonType;
    public String did;
    public String dod;
    public String callerIpAddr;
    public String recordingFile;

    public CDRObject(String respondJson, int index) throws IOException {

        JSONObject object = new JSONObject(respondJson);
        errorcode = object.getString("errcode").trim();
        errmsg  = object.getString("errmsg");

        if(!errorcode.trim().equals("0")){
            log.error("[CDR API] errorcode: "+errorcode+" errmsg:"+errmsg);
            return;
        }

        obj = (JSONObject) object.getJsonArray("cdr_list").getJsonObject(index);
        uid = getValue("uid");
        time = getValue("time");
        callFrom = getValue("call_from");
        callTo = getValue("call_to");
        callDuration = getValue("duration"); //todo 时间需要转化成00:00:00
        ringDuration = getValue("ring_duration");
        talkDuration = getValue("talk_duration");
        status = UIMapUtils.getValueByKey(getValue("disposition").toLowerCase().replace(" ","_"));
        sourceTrunk = getValue("src_trunk");
        destinationTrunk = getValue("dst_trunk");
        communicatonType = getValue("Outbound");
        did = getValue("did_number");
        dod = getValue("dod_number");
        callerIpAddr = getValue("src_addr");
        reason = getReason();

    }

    /**
     * 读取键值对的value，先判断是否存在此key，存在返回value,不存在返回空
     * @param key
     * @return
     */
    private String getValue(String key){
        if(obj.containsKey(key)){
            return prefix+obj.getString(key);
        }
        return "";
    }

    /**
     * 针对Reasn的键值对读取，如果读到src返回call_from的值，dst返回call_to的值
     * @param key
     * @return
     */
    private String getReasonValue(String key){
        if(obj.containsKey(key)){
            if(obj.getString(key).equals("src")) {
                return obj.getString("call_from");
            }else if(obj.getString(key).equals("dst")){
                return obj.getString("call_to");
            }else{
                return prefix+obj.getString(key);
            }
        }
        return "";
    }
    /**
     * 组合Reason
     * @return
     */
    private String getReason() throws IOException {

        String str = UIMapUtils.getValueByKey(getReasonValue("reason_partya")) +" " +
                UIMapUtils.getValueByKey(getReasonValue("reason_partyb")) +" " +
                UIMapUtils.getValueByKey(getReasonValue("reason_partyc")) +" " +
                UIMapUtils.getValueByKey(getReasonValue("reason_partyd")) +" " +
                UIMapUtils.getValueByKey(getReasonValue("reason_partye")) +" " +
                UIMapUtils.getValueByKey(getReasonValue("reason_partyf")) ;

        return str.trim();
    }
}
