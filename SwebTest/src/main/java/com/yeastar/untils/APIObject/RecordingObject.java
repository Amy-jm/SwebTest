package com.yeastar.untils.APIObject;

import cn.hutool.core.date.DateUtil;
import com.yeastar.untils.UIMapUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import top.jfunc.json.impl.JSONObject;

import java.io.IOException;

@Log4j2
@Getter
public class RecordingObject {
    private String prefix="personal_recording_list.";
    public String errorcode;
    public String errmsg;
    private JSONObject obj;

    public int id;
    public String time;
    public String timestamp;
    public String number;
    public String callType;
    public String duration;
    public String size;
    public String recordFile;
    public String callFrom;
    public String callTo;
    public String callDuration;


    public RecordingObject(String respondJson, int index) throws IOException {

        JSONObject object = new JSONObject(respondJson);
        errorcode = object.getString("errcode").trim();
        errmsg  = object.getString("errmsg");

        if(!errorcode.trim().equals("0")){
            log.error("[CDR API] errorcode: "+errorcode+" errmsg:"+errmsg);
            return;
        }

        obj = (JSONObject) object.getJsonArray("recording_list").getJsonObject(index);
        id = Integer.parseInt(getValue("id").replace(prefix,""));
        time = getValue("time").replace(prefix,"");
        timestamp = getValue("timestamp").replace(prefix,"");
        number = getValue("number").replace(prefix,"");
        callType = getValue("call_type").replace(prefix,"");
        duration = DateUtil.secondToTime(Integer.parseInt(getValue("duration").replace(prefix,"")));
        size = getValue("size").replace(prefix,"");
        recordFile = getValue("record_file").replace(prefix,"");

        callFrom = getValue("call_from").replace(prefix,"");
        callTo = getValue("call_to").replace(prefix,"");
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

    /**
     * cdr field call to
     */
    public enum CDRNAME {
        Extension_1000("test A<1000>"),
        Extension_1001("test2 B<1001>"),
        Extension_1002("testta C<1002>"),
        Extension_1003("testa D<1003>"),
        Extension_1004("t estX<1004>"),
        Extension_1005("First Last<1005>"),
        Extension_1020("1020 1020<1020>"),
        Extension_3001("3001<3001>"),
        Extension_2000("2000<2000>"),
        Extension_2001("2001<2001>"),
        Extension_4000("4000<4000>"),

        IVR0_6200("IVR IVR0<6200>"),
        IVR1_6201("IVR IVR1<6201>"),

        RINGGROUP0_6300("RingGroup RingGroup0<6300>"),
        RINGGROUP1_6301("RingGroup RingGroup1<6301>"),

        QUEUE0_6400("Queue Queue0<6400>"),
        QUEUE1_6401("Queue Queue1<6401>"),
        QUEUE2_6402("Queue Queue2<6402>"),
        QUEUE3_6403("Queue Queue3<6403>"),

        Extension_1000_VOICEMAIL("Voicemail test A<1000>"),
        Extension_1001_VOICEMAIL("Voicemail test2 B<1001>"),
        Extension_1004_VOICEMAIL("Voicemail t estX<1004>"),


        Conference0_6500("Conference Conference0<6500>");


        private final String alias;
        CDRNAME(String alias) {
            this.alias = alias;
        }
        @Override
        public String toString() {

            return this.alias;
        }
    }


    /**
     * cdr field status
     */
    public enum STATUS {
        ANSWER("ANSWERED"),
        NO_ANSWER("NO ANSWER"),
        BUSY("BUSY"),
        VOICEMAIL("VOICEMAIL");


        private final String alias;
        STATUS(String alias) {
            this.alias = alias;
        }
        @Override
        public String toString() {
            return this.alias;
        }
    }

    /**
     * cdr field COMMUNICATION TYPE
     */
    public enum COMMUNICATION_TYPE {
        INBOUND("Inbound"),
        OUTBOUND("Outbound"),
        INTERNAL("Internal");

        private final String alias;
        COMMUNICATION_TYPE(String alias) {
            this.alias = alias;
        }
        @Override
        public String toString() {
            return this.alias;
        }
    }
}
