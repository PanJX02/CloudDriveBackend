package com.panjx.clouddrive.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 邀请码配置类
 */
@Component
@ConfigurationProperties(prefix = "invite-code")
public class InviteCodeConfig {
    /**
     * 是否强制要求邀请码
     */
    private boolean required = false;

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
} 