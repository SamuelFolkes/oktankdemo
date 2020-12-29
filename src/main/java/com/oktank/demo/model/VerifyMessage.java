package com.oktank.demo.model;

public class VerifyMessage {
    private String id;
    private String name;
    private String email;
    private String messageType;
    private Boolean verified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) { this.messageType = messageType; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) { this.email = email; }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) { this.verified = verified; }
}
