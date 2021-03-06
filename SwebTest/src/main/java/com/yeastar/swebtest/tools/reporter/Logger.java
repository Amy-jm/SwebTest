package com.yeastar.swebtest.tools.reporter;


import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;

import java.lang.reflect.*;

/**
 * Created by GaGa on 2017-05-23.
 */
public class Logger {
    public static org.slf4j.Logger logger;
    private static final String FQCN = Logger.class.getName();

    static {
        try {
            Enhancer eh = new Enhancer();
            eh.setSuperclass(org.apache.log4j.Logger.class);
            eh.setCallbackType(LogInterceptor.class);
            Class c = eh.createClass();
            Enhancer.registerCallbacks(c, (Callback[]) new LogInterceptor[]{new LogInterceptor() {
                @Override
                public Object invoke(MethodInvocation invocation) throws Throwable {
                    return null;
                }
            }});

            Constructor<org.apache.log4j.Logger> constructor = c.getConstructor(String.class);
            org.apache.log4j.Logger loggerProxy = constructor.newInstance(Logger.class.getName());

            LoggerRepository loggerRepository = LogManager.getLoggerRepository();
            org.apache.log4j.spi.LoggerFactory lf = ReflectionUtil.getFieldValue(loggerRepository, "defaultFactory");
            Object loggerFactoryProxy = Proxy.newProxyInstance(
                    LoggerFactory.class.getClassLoader(),
                    new Class[]{LoggerFactory.class},
                    new NewLoggerHandler(loggerProxy)
            );

            ReflectionUtil.setFieldValue(loggerRepository, "defaultFactory", loggerFactoryProxy);
            logger = org.slf4j.LoggerFactory.getLogger(Logger.class.getName());
            ReflectionUtil.setFieldValue(loggerRepository, "defaultFactory", lf);
        } catch (
                IllegalAccessException |
                        NoSuchMethodException |
                        InvocationTargetException |
                        InstantiationException e) {
            throw new RuntimeException("?????????Logger??????", e);
        }
    }

    private static abstract class LogInterceptor implements MethodInterceptor {
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            // ?????????log?????????
            if (objects.length != 4 || !method.getName().equals("log"))
                return methodProxy.invokeSuper(o, objects);
            objects[0] = FQCN;
            return methodProxy.invokeSuper(o, objects);
        }
    }

    private static class NewLoggerHandler implements InvocationHandler {
        private final org.apache.log4j.Logger proxyLogger;

        public NewLoggerHandler(org.apache.log4j.Logger proxyLogger) {
            this.proxyLogger = proxyLogger;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return proxyLogger;
        }
    }

    public static void setProperty(String path) {
        PropertyConfigurator.configure(path);
    }
    // ?????????Logger?????????????????????????????????????????????????????????
    // ???????????????slf4j???api????????????????????????????????????????????????????????????????????????
    public static void info(String msg) {
        logger.info(msg);
    }

    public static void debug(String message) {
        logger.debug(message);
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

}

