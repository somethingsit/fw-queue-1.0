package com.example.queue.fw.exception;

import com.example.queue.fw.concurrent.LanguageBundleUtil;
import com.example.queue.fw.dto.BaseMessage;
import com.example.queue.fw.utils.DataUtil;
import org.apache.log4j.Logger;

import java.text.MessageFormat;

public class LogicException extends  Exception{
    private static final Logger logger = Logger.getLogger(LogicException.class);
    private String errorCode;
    private String description;
    private String keyMsg;
    private BaseMessage baseMessage;
    private String[] paramsMsg;

    public LogicException() {
    }

    public LogicException(BaseMessage baseMessage) {
        if (baseMessage != null) {
            this.baseMessage = baseMessage;
            this.errorCode = baseMessage.getErrorCode();
            this.keyMsg = baseMessage.getKeyMsg();
            this.paramsMsg = baseMessage.getParamsMsg();
            this.description = LanguageBundleUtil.getTextParam(this.keyMsg, this.paramsMsg);
        }

    }

    public LogicException(String errorCode) {
        this.errorCode = errorCode;
        this.keyMsg = errorCode;
        if (!DataUtil.isNullOrEmpty(this.keyMsg)) {
            this.description = LanguageBundleUtil.getText(this.keyMsg);
        } else {
            this.description = null;
        }

    }

    public LogicException(String errorCode, String keyMsg, Throwable cause, Object... params) {
        super(cause);
        this.errorCode = errorCode;
        this.keyMsg = keyMsg;
        if (!DataUtil.isNullOrEmpty(keyMsg)) {
            this.description = MessageFormat.format(LanguageBundleUtil.getText(keyMsg), params);
        } else {
            this.description = null;
        }

    }

    public LogicException(int any, String errorCode, String keyMsg) {
        this.errorCode = errorCode;
        this.keyMsg = keyMsg;
        if (!DataUtil.isNullOrEmpty(keyMsg)) {
            this.description = LanguageBundleUtil.getText(keyMsg);
        } else {
            this.description = null;
        }

    }

    public LogicException(String errorCode, String keyMsg, Object... params) {
        this.errorCode = errorCode;
        this.keyMsg = keyMsg;
        this.paramsMsg = this.convertParamsToStringArray(params);
        if (!DataUtil.isNullOrEmpty(keyMsg)) {
            this.description = LanguageBundleUtil.getTextParam(keyMsg, this.paramsMsg);
        } else {
            this.description = null;
        }

    }

    public LogicException(String errorCode, Object... params) {
        this.errorCode = errorCode;
        this.keyMsg = errorCode;
        this.paramsMsg = this.convertParamsToStringArray(params);
        if (!DataUtil.isNullOrEmpty(this.keyMsg)) {
            try {
                this.description = MessageFormat.format(LanguageBundleUtil.getText(this.keyMsg), params);
            } catch (Exception var4) {
                logger.error(var4.getMessage());
            }
        } else {
            this.description = null;
        }

    }

    public String getMessage() {
        return MessageFormat.format("{0}:{1}", this.errorCode, this.description);
    }

    public String toString() {
        return MessageFormat.format("{0}:{1}", this.errorCode, this.description);
    }

    private String[] convertParamsToStringArray(Object... params) {
        if (DataUtil.isNullOrEmpty(params)) {
            return null;
        } else {
            String[] lst = new String[params.length];

            for(int i = 0; i < params.length; ++i) {
                lst[i] = DataUtil.safeToString(params[i]);
            }

            return lst;
        }
    }

    public String[] getParamsMsg() {
        return this.paramsMsg;
    }

    public void setParamsMsg(String[] paramsMsg) {
        this.paramsMsg = paramsMsg;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getDescription() {
        return this.description != null && !this.description.isEmpty() ? this.description : null;
    }

    public String getKeyMsg() {
        return this.keyMsg;
    }

    public void setKeyMsg(String keyMsg) {
        this.keyMsg = keyMsg;
    }

    public BaseMessage getBaseMessage() {
        return this.baseMessage;
    }

    public void setBaseMessage(BaseMessage baseMessage) {
        this.baseMessage = baseMessage;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
