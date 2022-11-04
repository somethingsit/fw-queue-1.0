package com.example.queue.fw.dto;

import com.example.queue.fw.concurrent.LanguageBundleUtil;
import com.example.queue.fw.utils.DataUtil;

import java.io.Serializable;
import java.util.Arrays;

public class BaseMessage implements Serializable {
    private String errorCode;
    /** @deprecated */
    @Deprecated
    private String description;
    private boolean success;
    private String keyMsg;
    private String[] paramsMsg;

    public BaseMessage() {
    }

    public BaseMessage(boolean success) {
        this.success = success;
    }

    public BaseMessage(String errorCode, boolean success) {
        this.errorCode = errorCode;
        this.success = success;
    }

    public BaseMessage(boolean success, String errorCode, String keyMsg, String... paramsMsg) {
        this.errorCode = errorCode;
        this.success = success;
        this.setKeyMsg(keyMsg, paramsMsg);
    }

    public BaseMessage(String errorCode, boolean success, String description) {
        this.errorCode = errorCode;
        this.description = description;
        this.success = success;
    }

    public BaseMessage(BaseMessage msg) {
        this.errorCode = msg.errorCode;
        this.description = msg.description;
        this.success = msg.success;
        this.keyMsg = msg.keyMsg;
        this.paramsMsg = msg.paramsMsg;
    }

    public String getKeyMsg() {
        return this.keyMsg;
    }

    public void setKeyMsg(String keyMsg) {
        if (!DataUtil.isNullOrEmpty(keyMsg)) {
            this.description = LanguageBundleUtil.getText(keyMsg);
        }

        this.keyMsg = keyMsg;
    }

    public void setKeyMsg(String keyMsg, String... params) {
        if (!DataUtil.isNullOrEmpty(keyMsg)) {
            if (DataUtil.isNullOrEmpty(params)) {
                this.description = LanguageBundleUtil.getText(keyMsg);
            } else {
                this.description = LanguageBundleUtil.getTextParam(keyMsg, params);
            }
        }

        this.paramsMsg = params;
        this.keyMsg = keyMsg;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getDescription() {
        if (DataUtil.isNullOrEmpty(this.description)) {
            return DataUtil.isNullOrEmpty(this.paramsMsg) ? LanguageBundleUtil.getText(this.keyMsg) : LanguageBundleUtil.getTextParam(this.keyMsg, this.paramsMsg);
        } else {
            return this.description;
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public BaseMessage getBaseMsg() {
        return new BaseMessage(this);
    }

    public String[] getParamsMsg() {
        return this.paramsMsg;
    }

    public void setParamsMsg(String[] paramsMsg) {
        this.paramsMsg = paramsMsg;
    }

    public String toString() {
        return "BaseMessage[errorCode='" + this.errorCode + '\'' + ", description='" + this.description + '\'' + ", success=" + this.success + ", keyMsg='" + this.keyMsg + '\'' + ", paramsMsg=" + Arrays.toString(this.paramsMsg) + ']';
    }
}
