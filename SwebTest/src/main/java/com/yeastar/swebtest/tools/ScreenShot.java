package com.yeastar.swebtest.tools;

import com.yeastar.swebtest.driver.SwebDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

/**
 * Created by GaGa on 2017-05-12.
 */
public class ScreenShot extends SwebDriver {
    public static WebDriver driver;

    //要注意不同截图来源
    public static void takeScreenshot(String screenPath) {
        driver = getWebDriver();
        try {
            File scrFile = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(screenPath));
        } catch (IOException e) {
            System.out.println("Screen shot error: " + screenPath);
        }
    }

    public static void takeScreenshotByAll(String filename) {
//        screenShotAsFile(0, 0, 1920, 1080,filename);
        takeScreenshot(filename);
    }

    public static void screenShotAsFile(int x, int y, int width, int height, String fileName) {
        try {
            Robot robot = new Robot();
            BufferedImage bfImage = robot.createScreenCapture(new Rectangle(x, y, width, height));
            //File path = new File("D:\\eclipseFile\\baiduFile");
            //File file = new File(fileName, fileName+ "." + "jpg");
            File file = new File(fileName);
            ImageIO.write(bfImage, "jpg", file);
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
