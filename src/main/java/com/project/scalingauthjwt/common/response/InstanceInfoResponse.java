package com.project.scalingauthjwt.common.response;

public class InstanceInfoResponse {

    private String instanceId;
    private String username;
    private boolean authenticated;

    public InstanceInfoResponse(String instanceId, String username, boolean authenticated) {
        this.instanceId = instanceId;
        this.username = username;
        this.authenticated = authenticated;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}