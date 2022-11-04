package com.example.queue.fw.utils;

import com.example.queue.fw.bundle.UTF8ResourceBundle;
import com.example.queue.fw.concurrent.LanguageBundleUtil;
import com.example.queue.fw.dto.Locate;
import com.example.queue.fw.exception.UnsupportedEnvironmentException;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.*;

public class BundleUtil implements InitializingBean {
    private static org.apache.log4j.Logger logger = Logger.getLogger(BundleUtil.class);
    private static HashMap<Locate, ResourceBundle> resourceBundleMap = Maps.newHashMap();
    private String languageLocation;
    private List<Locate> supportedLanguages;
    private static String runEnvironment;
    private static String languageSort;
    private static Locate defaultLocate;

    public BundleUtil() {
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(runEnvironment, "runEnvironment of bean BundleUtil must be set in fw-beans.xml");
        Assert.notNull(this.languageLocation, "languageLocation of bean BundleUtil must be set in fw-beans.xml");
        Assert.notNull(this.supportedLanguages, "supportedLanguages of bean BundleUtil must be set in fw-beans.xml");
        Assert.notNull(defaultLocate, "defaultLocate of bean BundleUtil must be set in fw-beans.xml");
        this.init();
    }

    private void init() {
        Iterator var1 = this.supportedLanguages.iterator();

        while(var1.hasNext()) {
            Locate locale = (Locate)var1.next();
            Locale jLocale = new Locale(locale.getLanguage(), locale.getCountry());
            resourceBundleMap.put(locale, new UTF8ResourceBundle(this.languageLocation, jLocale));
        }

    }

    public static String getText(Locate locate, String key) {
        String result = key;

        try {
            ResourceBundle bundle = (ResourceBundle)resourceBundleMap.get(locate);
            if (key != null) {
                result = bundle.getString(key);
            }

            return result;
        } catch (Exception var4) {
            logger.error(var4.getMessage());
            return key;
        }
    }

    protected static String modifyMsg(String msg) {
        msg = DataUtil.safeToString(msg);
        if (msg.contains("SQLTimeoutException")) {
            msg = LanguageBundleUtil.getText("err.timeout.db");
        }

        msg = "[" + LanguageBundleUtil.getReqId() + "] " + msg;
        return msg;
    }

    private static <T> T getFieldValue(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (Exception var3) {
            return null;
        }
    }

    public static Locate getDefaultLocate() {
        return defaultLocate;
    }

    public void setDefaultLocate(Locate defaultLocate) {
        defaultLocate = defaultLocate;
    }

    public void setLanguageLocation(String languageLocation) {
        this.languageLocation = languageLocation;
    }

    public void setSupportedLanguages(List<Locate> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }

    public static void checkEnvironment(String function, String rejectEnvironment) throws UnsupportedEnvironmentException {
        if (runEnvironment.equals(rejectEnvironment)) {
            throw new UnsupportedEnvironmentException("Khong duoc phep goi ham " + function + " trong moi truong " + rejectEnvironment);
        }
    }

    public static String getSortCode() {
        return languageSort;
    }

    public void setSortCode(String sortCode) {
        languageSort = sortCode;
    }

    public void setRunEnvironment(String runEnvironment) {
        runEnvironment = runEnvironment;
    }

    public static String getRunEnvironment() {
        return runEnvironment;
    }
}
