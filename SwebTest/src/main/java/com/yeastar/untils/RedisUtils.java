package com.yeastar.untils;

import com.sun.jna.Library;
import com.sun.jna.Native;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import top.jfunc.json.JsonObject;
import top.jfunc.json.impl.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import static com.yeastar.swebtest.driver.DataReader2.DEVICE_IP_LAN;

/**
 * P系列开启redis
 * 1、cd /etc/redis
 * 2、mount -o remount,rw /
 * 3.vi redis.conf
 *    (1)修改redis.conf 文件，把bind 127.0.0.1 ::1这一行注释掉。
 *    (2)修改redis.conf 文件，protected-mode 要设置成no
 * 4. kiil redis
 * 5. redis-server redis.conf
 */
@Log4j2
public class RedisUtils {



    public enum REDIS_KEY{
        RingGroupCache("RingGroupCache"),
        TrunkStatusCache("TrunkStatusCache"),
        AccIpFailTimes("AccIpFailTimes"),
        ExtCallStatusCache("ExtCallStatusCache"),
        QueuePanelWeekCache("QueuePanelWeekCache"),
        UserCache("UserCache"),
        LinkusAccessToken("LinkusAccessToken"),
        AccIpBlockTimes("AccIpBlockTimes"),
        RoleCache("RoleCache"),
        ConferenceCache("ConferenceCache"),
        AccIpMaskWhite("AccIpMaskWhite"),
        ExtOnlineStatusCache("ExtOnlineStatusCache"),
        SipIpWhite("SipIpWhite"),
        IvrCache("IvrCache"),
        AdminAccessToken("AdminAccessToken"),
        QueuePanelMonthCache("QueuePanelMonthCache"),
        AccIpWhite("AccIpWhite"),
        ExtensionCache("ExtensionCache"),
        NumberRecordCache("NumberRecordCache"),
        ExtensionGroupCache("ExtensionGroupCache"),
        QueueCache("QueueCache"),
        QueuePanelCache("QueuePanelCache"),
        UserAccessToken("UserAccessToken");

        private String alias=null;
        REDIS_KEY(String alias) {
            this.alias = alias;
        }
    }

    public enum QUEUE_KEY{
        ID("id"),
        Name("name"),
        Number("number"),
        RingInUse("enb_ring_in_use");

        private final String alias;
        QUEUE_KEY(String alias) {
            this.alias = alias;
        }
        @Override
        public String toString() {
            return this.alias;
        }
    }


    public interface redisutils extends Library {
        redisutils instance = (redisutils) Native.loadLibrary("libAutoTestTool",redisutils.class);

//        redisutils instance = (redisutils) Native.loadLibrary("libAutoTestTool.dll",redisutils.class);
//        int Add(int a, int b);

        int add(int a, int b, String ae);
        int qRedisHget(String ip, String filed, String key1, String key2);
    }
    @Test
    public void mytest() {
        System.out.println("resp111111111111111 "+ System.getProperty("java.library.path"));
        System.out.println("2222222222222222");
        int resp=redisutils.instance.add(8,3,"ae2");
        System.out.println("resp "+ resp);
//
        int q = redisutils.instance.qRedisHget("192.168.11.180","QueueCache","6400","name");
        System.out.println("q "+ q);
//        System.out.println("mytest ; "+ hget(REDIS_KEY.QueueCache.toString(),"6401",QUEUE_KEY.Name.toString()));

    }
    public static void main(String[] args) {
        //new一个结构体对象，赋值
//        RedisUtils.redisutils.UserLoginField.ByReference userLoginFiled=new RedisUtils.redisutils.UserLoginField.ByReference();
//        userLoginFiled.UserName="admin";
//        userLoginFiled.UserName="admin123";
//        int resp=redisutils.instance.Add(8,4);
//        System.out.println("resp111111111111111 "+ System.getProperty("java.library.path"));
//        System.out.println("2222222222222222");
//        int resp=redisutils.instance.add(8,4);
//        System.out.println("resp "+ resp);
//
//        String q = redisutils.instance.qRedisHget("192.168.11.180","QueueCache","6400","name");
//        System.out.println("q "+ q);

        hget(REDIS_KEY.QueueCache.toString(),"6400",QUEUE_KEY.Name.toString());
    }

    public static String hget(String key, String filed){
        return hget(key,filed,"");
    }

    /**
     * 连接redis库，用hget方法，根据key，filed查询获得Json格式数据，再根据key2，查找对应的value值
     * @param key redis中定义的key，可用keys *查询设备中可用的key
     * @param filed 具体查询的字段，如队列号：6401
     * @param key2 查询字段中的子字段，如队列6401中的id，
     * @return key2对应的value
     */
    public static String hget(String key, String filed, String key2) {

        log.debug("[hget args:]"+key + " "+ filed + " "+key2);

        Jedis jedis = new Jedis(DEVICE_IP_LAN,6379) ;

        Base64.Decoder decoder = Base64.getDecoder();

        try {
            jedis.auth(new String(decoder.decode("WHphcHAjcHJlZGlzQDIwMjA/NjMtMg=="), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String ret = jedis.hget(key,filed);

        if (!key2.isEmpty()){
            JsonObject jsonObject = new JSONObject(ret);
            ret = jsonObject.get(key2).toString();
        }

        log.debug("[Redis result] "+ret);
        return ret;
    }


}