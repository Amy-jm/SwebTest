package com.yeastar.untils;

import co.boorse.seleniumtable.SeleniumTable;
import co.boorse.seleniumtable.SeleniumTableCell;
import com.codeborne.selenide.SelenideElement;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.controllers.WebDriverFactory.getDriver;

/**
 * @program: SwebTest
 * @description: 表格工具类
 * @author: huangjx@yeastar.com
 * @create: 2020/06/16
 */
@Log4j2
public class TableUtils {
    final public static String strTableXPATH = "//table";


    /**
     * 【默认表格对象】通过行列 获取表格数据
     * @param row 行
     * @param column 列
     * @return
     */
    public String getCDR(WebDriver driver,int row,int column){
        WebElement tableElement = driver.findElement(By.xpath(strTableXPATH));
        SeleniumTable table = SeleniumTable.getInstance(tableElement);
        String strReturn = "";
        SeleniumTableCell cell = table.get(row, column);
        strReturn = cell.getText();
        log.debug("[getTableData]"+strReturn);
        return strReturn;
    }

    /**
     * 【默认表格对象】 通过标题 及行号 获取表格数据
     * @param strHeader
     * @param row
     * @return
     */
    public static String getTableForHeader(WebDriver driver,String strHeader, int row){
        WebElement tableElement = driver.findElement(By.xpath(strTableXPATH));
        SeleniumTable table = SeleniumTable.getInstance(tableElement);
        String strReturn = "";
        if (table.hasColumn(strHeader)) {
            List<SeleniumTableCell> header1Cells = table.getColumn(strHeader);
            log.debug("[headerCell Size]"+header1Cells.size());
            try {
                strReturn = header1Cells.get(row).getText();
            }catch(java.lang.IndexOutOfBoundsException ex){
                log.debug("[表格查无数据]"+ex.getMessage());
            }
            log.debug("[getTableData]"+strReturn);
        }
        return strReturn;
    }

    /**
     * 【自定义表格对象】 通过标题 及行号 获取表格数据
     * @param strHeader
     * @param column
     * @return
     */
    public static String getTableForHeader(WebDriver driver,WebElement tableElement, String strHeader, int column){
        tableElement = driver.findElement(By.xpath(strTableXPATH));
        SeleniumTable table = SeleniumTable.getInstance(tableElement);
        String strReturn = "";
        if (table.hasColumn(strHeader)) {
            table = SeleniumTable.getInstance(tableElement);
            List<SeleniumTableCell> header1Cells = table.getColumn(strHeader);
            log.debug("[headerCell Size]"+header1Cells.size());
            try {
                strReturn = header1Cells.get(column).getText();
            }catch(java.lang.IndexOutOfBoundsException ex){
                log.debug("[表格查无数据]"+ex.getMessage());
            }
            log.debug("[getTableData]"+strReturn);
        }
        return strReturn;
    }

    /**
     * 获取表格数量
     * @param driver
     * @return
     */
    public static int getTableDataNum(WebDriver driver){
        WebElement tableElement = driver.findElement(By.xpath(strTableXPATH));
        SeleniumTable table = SeleniumTable.getInstance(tableElement);
        return table.rowCount();
    }

    /**
     * 【自定义表格对象】通过表格中的某个数据（tagName），定位到那一行，点击那一行的编辑按钮
     * @param driver
     * @param strHeader
     * @param tagName
     */
    public static boolean clickTableEidtBtn(WebDriver driver, String strHeader, String tagName){
        sleep(2000);
        WebElement tableElement = driver.findElement(By.xpath(strTableXPATH));
        SeleniumTable table = SeleniumTable.getInstance(tableElement);
        System.out.println("table has col"+ strHeader+ table.hasColumn(strHeader));
        if (table.hasColumn(strHeader)) {
            List<SeleniumTableCell> header1Cells = table.getColumn(strHeader);
            for(int row=0; row<header1Cells.size(); row++){
                if(header1Cells.get(row).getText().equals(tagName)){
                    log.debug("[clickTableEidtBtn:find table data,row=] "+row);
                    $(By.xpath("//table/tbody/tr["+(row+1)+"]//i[contains(@class,'edit')]")).click();
                    return true;
                }
            }
        }
        return false ;
    }

    /**
     * 【自定义表格对象】通过表格中的表头（strHeader）某个数据（tagName），定位到那一行，点击那一行的删除按钮
     * @param driver
     * @param strHeader
     * @param tagName
     * @return
     */
    public static boolean clickTableDeletBtn(WebDriver driver, String strHeader, String tagName){
        sleep(2000);//todo 判断表格显现

        WebElement tableElement = driver.findElement(By.xpath(strTableXPATH));
        SeleniumTable table = SeleniumTable.getInstance(tableElement);

        if (table.hasColumn(strHeader)) {
            List<SeleniumTableCell> header1Cells = table.getColumn(strHeader);
            for(int row=0; row<header1Cells.size(); row++){
                if(header1Cells.get(row).getText().equals(tagName)){
                    log.debug("[clickTableDeletBtn:find table data,row=] "+row);
                    $(By.xpath("//table/tbody/tr["+(row+1)+"]//i[contains(@class,'delete')]")).click();
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 【自定义表格对象】通过行列 获取表格数据
     * @param tableElement 表格对象
     * @param row 行
     * @param column 列
     * @return
     */
    public String getTable(SelenideElement tableElement, int row, int column){
        String strReturn = "";
        SeleniumTable table = SeleniumTable.getInstance(tableElement);
        SeleniumTableCell cell = table.get(row, column);
        strReturn = cell.getText();
        log.debug("[getTableData]"+strReturn);
        return strReturn;
    }

}
