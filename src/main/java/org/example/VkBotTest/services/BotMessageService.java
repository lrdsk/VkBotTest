package org.example.VkBotTest.services;

import org.springframework.http.ResponseEntity;

public interface BotMessageService {
    ResponseEntity<String> callback(String body);
}
