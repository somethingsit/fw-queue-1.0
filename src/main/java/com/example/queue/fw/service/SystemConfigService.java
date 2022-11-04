package com.example.queue.fw.service;

import org.springframework.stereotype.Service;

import javax.jws.WebMethod;
import javax.jws.WebParam;

@Service
public class SystemConfigService {
    private static boolean enableAllProducer = true;
    private static boolean enableWsRequestLog = false;
    private static boolean enableWsResponseLog = false;
    private static boolean enableFullPerformanceLog = false;

    public SystemConfigService() {
    }

    @WebMethod
    public static boolean isEnableWsRequestLog() {
        return enableWsRequestLog;
    }

    @WebMethod
    public static void setEnableWsRequestLog(@WebParam(name = "enable") boolean enable) {
        enableWsRequestLog = enable;
    }

    @WebMethod
    public static boolean isEnableWsResponseLog() {
        return enableWsResponseLog;
    }

    @WebMethod
    public static void setEnableWsResponseLog(@WebParam(name = "enable") boolean enable) {
        enableWsResponseLog = enable;
    }

    @WebMethod
    public static boolean isEnableFullPerformanceLog() {
        return enableFullPerformanceLog;
    }

    @WebMethod
    public static void setEnableFullPerformanceLog(@WebParam(name = "enable") boolean enable) {
        enableFullPerformanceLog = enable;
    }

    @WebMethod
    public static boolean isEnableAllProducer() {
        return enableAllProducer;
    }

    @WebMethod
    public static void setEnableAllProducer(@WebParam(name = "enable") boolean enable) {
        enableAllProducer = enable;
    }
}
