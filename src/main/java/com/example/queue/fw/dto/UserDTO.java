package com.example.queue.fw.dto;

import java.io.Serializable;

public class UserDTO implements Serializable {
    private String staffCode;
    private String staffName;
    private String shopCode;
    private String shopName;
    private Long staffId;
    private Long shopId;
    private Long userId;
    private String ipAddress;

    public UserDTO() {
    }

    public UserDTO(Long userId, Long staffId, String staffCode, Long shopId, String shopCode, String ipAddress) {
        this.userId = userId;
        this.staffCode = staffCode;
        this.shopCode = shopCode;
        this.staffId = staffId;
        this.shopId = shopId;
        this.ipAddress = ipAddress;
    }

    public UserDTO(Long staffId, String staffCode, Long shopId, String shopCode) {
        this.staffId = staffId;
        this.staffCode = staffCode;
        this.shopId = shopId;
        this.shopCode = shopCode;
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

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getStaffName() {
        return this.staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getShopName() {
        return this.shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
