package com.example.queue.fw.concurrent;

import com.example.queue.fw.dto.GenericWebInfo;
import com.example.queue.fw.dto.Locate;
import com.example.queue.fw.utils.BundleUtil;
import com.example.queue.fw.utils.DataUtil;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LanguageBundleUtil {
    public static final Logger logger = LoggerFactory.getLogger(LanguageBundleUtil.class);
    private static ThreadLocal<GenericWebInfo> webInfoContext = new ThreadLocal();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmmss");

    public LanguageBundleUtil() {
    }

    public static String getRandomKpiId() {
        return sdf.format(Calendar.getInstance().getTime()) + RandomStringUtils.randomNumeric(5);
    }

    public static Locate getLocate() {
        try {
            GenericWebInfo genericWebInfo = getGenericWebInfo();
            if (DataUtil.isNullOrEmpty(genericWebInfo.getLanguage())) {
                genericWebInfo.setLanguage(BundleUtil.getDefaultLocate().getLanguage());
                genericWebInfo.setCountry(BundleUtil.getDefaultLocate().getCountry());
            }

            return new Locate(genericWebInfo.getLanguage(), genericWebInfo.getCountry());
        } catch (Exception var1) {
            return BundleUtil.getDefaultLocate();
        }
    }

    public static void setGenericWebInfo(GenericWebInfo webInfo) {
        webInfoContext.set(webInfo);
    }

    public static GenericWebInfo getGenericWebInfo() {
        return getGenericWebInfo(false, (String)null);
    }

    public static GenericWebInfo getGenericWebInfo(boolean reset, String kpiId) {
        GenericWebInfo genericWebInfo = (GenericWebInfo)webInfoContext.get();
        if (genericWebInfo == null || reset) {
            genericWebInfo = new GenericWebInfo();
            genericWebInfo.setReqId(getRandomKpiId());
            genericWebInfo.setLanguage("vi");
            genericWebInfo.setCountry("vn");
            webInfoContext.set(genericWebInfo);
            ThreadContext.put("kpi", genericWebInfo.getReqId());
        }

        return genericWebInfo;
    }

    public static String getText(String key) {
        return BundleUtil.getText(getLocate(), key);
    }

    public static String getTextParam(String key, String... params) {
        String msg = getText(key);
        if (DataUtil.isNullOrEmpty(msg)) {
            return params == null ? key : Joiner.on("|").join(key, params, new Object[0]);
        } else {
            return MessageFormat.format(msg, params);
        }
    }

    public static String getAndResetReqId(String reqId) {
        return getGenericWebInfo(true, reqId).getReqId();
    }

    public static String getReqId() {
        return getGenericWebInfo().getReqId();
    }
}
