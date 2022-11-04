package com.example.queue.fw.dto;

import java.io.Serializable;

public class GenericWebInfo implements Serializable {
    private String staffCode;
    private String shopCode;
    private Long staffId;
    private Long shopId;
    private String ipAddress;
    private String language;
    private String country;
    private String reqId;
    private Long userId;
    private String serverAddress;
    private String serverPort;
    private String sessionId;

    public GenericWebInfo() {
    }

    public GenericWebInfo(String staffCode, String shopCode, String ipAddress, String language, String country) {
        this.staffCode = staffCode;
        this.shopCode = shopCode;
        this.ipAddress = ipAddress;
        this.language = language;
        this.country = country;
    }

    public UserDTO getStaffDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setStaffCode(this.getStaffCode());
        userDTO.setStaffId(this.getStaffId());
        userDTO.setShopCode(this.getShopCode());
        userDTO.setShopId(this.getShopId());
        return userDTO;
    }

    public String getStaffCode() {
        return this.staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getShopCode() {
        return this.shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getStaffId() {
        return this.staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getShopId() {
        return this.shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getReqId() {
        return this.reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getServerAddress() {
        return this.serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
