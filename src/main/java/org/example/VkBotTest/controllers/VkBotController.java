package org.example.VkBotTest.controllers;

import org.example.VkBotTest.services.servicesImpl.BotMessageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VkBotController {
    private final BotMessageServiceImpl botMessageService;

    @Autowired
    public VkBotController(BotMessageServiceImpl botMessageService) {
        this.botMessageService = botMessageService;
    }

    @PostMapping("/")
    public ResponseEntity<String> callback(@RequestBody String body){
        return botMessageService.callback(body);
    }
}
