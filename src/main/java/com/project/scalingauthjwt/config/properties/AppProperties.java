package com.project.scalingauthjwt.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @NotBlank
    private String instanceId = "instance-1";

    @Valid
    private Security security = new Security();

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public static class Security {

        @NotBlank
        private String jwtSecret = "local-dev-secret-key-for-scaling-auth-jwt-lab-1234567890";

        @Min(1)
        private long accessTokenExpirationMinutes = 15;

        public String getJwtSecret() {
            return jwtSecret;
        }

        public void setJwtSecret(String jwtSecret) {
            this.jwtSecret = jwtSecret;
        }

        public long getAccessTokenExpirationMinutes() {
            return accessTokenExpirationMinutes;
        }

        public void setAccessTokenExpirationMinutes(long accessTokenExpirationMinutes) {
            this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        }
    }
}