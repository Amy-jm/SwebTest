package com.yeastar.untils;

import lombok.Getter;
import lombok.Setter;
import top.jfunc.json.impl.JSONObject;

/**
 * @program: SwebTest
 * @description: asterisk log object
 * @author: huangjx@yeastar.com
 * @create: 2020/10/23
 */
@Getter
@Setter
public class AsteriskObject {
    private String time;
    private String name;
    private String tag;
    private String keyword;
    public AsteriskObject(){}

    public AsteriskObject(String name,String time,String tag,String keyword){
        this.name = name;
        this.time = time;
        this.tag = tag;
        this.keyword = keyword;
    }
}
