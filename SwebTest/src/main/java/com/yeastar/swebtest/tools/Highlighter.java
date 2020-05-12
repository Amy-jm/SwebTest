package com.yeastar.swebtest.tools;

import com.yeastar.untils.PropertiesUntils;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

import java.util.Properties;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.sleep;

@Log4j2
public class Highlighter extends AbstractWebDriverEventListener {
    private static boolean isHighlighter = true;
    static {
        Properties p = null;
        try {
            p = new PropertiesUntils().getInstance().getPropertie("/config.properties");
        } catch (Exception e) {
            log.error("can not find config properties file " +e);
        }
        try {
            isHighlighter = Boolean.valueOf(p.getProperty("isHighlighter"));
        } catch (java.lang.NumberFormatException e) {
            log.error("can not find retry key maxRunCount on " +e);
        }
        log.debug("[is open highlight element ] " + (isHighlighter) + "\n");
    }
    public static <T extends WebElement> T highlight(T element) {
        return highlight(element, "orange");
    }

    public static <T extends WebElement> T highlight(T element, final String color) {
        if (element != null && element.getAttribute("__selenideHighlighting") == null && isHighlighter) {
            for (int i = 1; i < 4; i++) {
                transform(element, color, i);
                sleep(50);
            }
            for (int i = 4; i > 0; i--) {
                transform(element, color, i);
                sleep(50);
            }
        }
        return element;
    }

    private static void transform(WebElement element, String color, int i) {
        executeJavaScript(
                "arguments[0].setAttribute('__selenideHighlighting', 'done'); " +
                        "arguments[0].setAttribute(arguments[1], arguments[2])",
                element,
                "style",
                "border: " + i + "px solid " + color + "; border-style: dotted; " +
                        "transform: scale(1, 1." + i + ");");
    }

    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        highlight(element, "orange");
    }

    @Override
    public void beforeFindBy(By by, WebElement element, WebDriver driver) {
        highlight(element, "red");
    }

    @Override
    public void afterFindBy(By by, WebElement element, WebDriver driver) {
        highlight(element, "green");
    }

//    @Override
    public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
        highlight(element, "orange");
    }
}