package org.example.VkBotTest.util;

public enum MessageType {
    CONFIRMATION("confirmation"),
    MESSAGE_NEW("message_new");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
