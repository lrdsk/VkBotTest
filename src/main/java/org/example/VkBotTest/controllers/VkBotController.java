package org.example.VkBotTest.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Callback для обработки сообщений от VK",
            description = "Получает сообщение от VK, обрабатывает его и отправляет ответ.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сообщение успешно обработано"),
            @ApiResponse(responseCode = "500", description = "Ошибка при обработке сообщения")
    })
    @PostMapping("/")
    public ResponseEntity<String> callback(@RequestBody String body){
        return botMessageService.callback(body);
    }
}
