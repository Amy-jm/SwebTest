package com.yeastar.page.pseries.OperatorPanel;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @program: SwebTest
 * @description: VCP实体类
 * @author: huangjx@yeastar.com
 * @create: 2020/07/29
 */
@Getter
@Setter
public class  Record implements Serializable {
    private static final long serialVersionUID =1L;
    private String recordStatus;
    private String caller;
    private String callee;
    private String status;
    private String strTime;
    private String details;
    public Record(){}
    public Record(String isRecord,String caller,String callee,String status,String time,String details){
        this.recordStatus = isRecord;
        this.caller = caller;
        this.callee = callee;
        this.status = status;
        this.strTime = time;
        this.details = details;
    }
    @Override
    public String toString() {
        return "Record: recordStatus=" + recordStatus + ", caller=" + caller + ", callee=" + callee + ", status=" + status + ", strTime=" + strTime + ", details=" + details;
    }
}