package com.yeastar.untils;

import co.boorse.seleniumtable.SeleniumTable;
import co.boorse.seleniumtable.SeleniumTableCell;
import com.codeborne.selenide.SelenideElement;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

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
    public static String getCDRForHeader(WebDriver driver,String strHeader, int row){
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
     * @param row
     * @return
     */
    public static String getCDRForHeader(WebDriver driver,WebElement tableElement, String strHeader, int row){
        tableElement = driver.findElement(By.xpath(strTableXPATH));
        SeleniumTable table = SeleniumTable.getInstance(tableElement);
        String strReturn = "";
        if (table.hasColumn(strHeader)) {
            table = SeleniumTable.getInstance(tableElement);
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
     * 【自定义表格对象】通过行列 获取表格数据
     * @param tableElement 表格对象
     * @param row 行
     * @param column 列
     * @return
     */
    public String getCDR(SelenideElement tableElement, int row, int column){
        String strReturn = "";
        SeleniumTable table = SeleniumTable.getInstance(tableElement);
        SeleniumTableCell cell = table.get(row, column);
        strReturn = cell.getText();
        log.debug("[getTableData]"+strReturn);
        return strReturn;
    }
}
