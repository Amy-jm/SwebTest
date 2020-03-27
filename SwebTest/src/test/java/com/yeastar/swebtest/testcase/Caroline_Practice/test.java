package com.yeastar.swebtest.testcase.Caroline_Practice;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Caroline on 2018/1/3.
 */
public class test extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {
        System.out.println("BeforeClass");
    }

    @Test
    public void A(){
        System.out.println("Test1");
    }

    @Test
    public void B(){
        System.out.println("Test1");
    }


    @AfterMethod
    public void AfterMethod(){
        System.out.println("AfterMethod");
    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        System.out.println("AfterClass");
    }
}
