package com.yeastar.untils;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

/**
 * @program: SwebTest
 * @description: for influxdb send message
 * @author: huangjx@yeastar.com
 * @create: 2020/06/23
 */
public class ResultSenderUtils {
    private static final InfluxDB INFLXUDB = InfluxDBFactory.connect("http://192.168.3.252:8086", "root", "root");
    private static final String DATABASE = "selenium";

    static{
        INFLXUDB.setDatabase(DATABASE);
    }

    public static void send(final Point point){
        INFLXUDB.write(point);
    }
}
