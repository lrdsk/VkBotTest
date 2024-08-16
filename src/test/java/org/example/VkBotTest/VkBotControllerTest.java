package org.example.VkBotTest;

import org.example.VkBotTest.controllers.VkBotController;
import org.example.VkBotTest.services.servicesImpl.BotMessageServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(VkBotController.class)
public class VkBotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BotMessageServiceImpl botMessageService;

    @Value("${CONFIRMATION_TOKEN}")
    private String CONFIRMATION_TOKEN;

    @Test
    void testCallbackConfirmation() throws Exception{
        String body = "{\"type\":\"confirmation\"}";

        Mockito.when(botMessageService.callback(any())).thenReturn(ResponseEntity.ok(CONFIRMATION_TOKEN));

        mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string(CONFIRMATION_TOKEN));
    }

    @Test
    void testCallbackNewMessage() throws Exception {
        String body = "{\"type\":\"message_new\",\"object\":{\"message\":{\"from_id\":123,\"text\":\"Hello\"}}}";

        Mockito.when(botMessageService.callback(any())).thenReturn(ResponseEntity.ok("Ok"));

        mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("Ok"));
    }
}
