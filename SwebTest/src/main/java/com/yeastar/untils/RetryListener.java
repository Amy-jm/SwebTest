package com.yeastar.untils;

import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author huangjx
 **/
public class RetryListener implements IAnnotationTransformer {


    /**
     * This method will be invoked by TestNG to give you a chance
     * to modify a TestNG annotation read from your test classes.
     * You can change the values you need by calling any of the
     * setters on the ITest interface.
     * <p>
     * Note that only one of the three parameters testClass,
     * testConstructor and testMethod will be non-null.
     *
     * @param annotation      The annotation that was read from your
     *                        test class.
     * @param testClass       If the annotation was found on a class, this
     *                        parameter represents this class (null otherwise).
     * @param testConstructor If the annotation was found on a constructor,
     *                        this parameter represents this constructor (null otherwise).
     * @param testMethod      If the annotation was found on a method,
     */
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        IRetryAnalyzer retry = annotation.getRetryAnalyzer();
        if (retry == null) {
            annotation.setRetryAnalyzer(TestNGRetry.class);
        }
    }
}
