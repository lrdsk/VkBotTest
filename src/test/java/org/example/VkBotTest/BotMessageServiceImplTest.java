package org.example.VkBotTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.VkBotTest.services.servicesImpl.BotMessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BotMessageServiceImplTest {

    @InjectMocks
    private BotMessageServiceImpl botMessageService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RestTemplate restTemplate;

    private final String CONFIRMATION_TOKEN = "test_confirmation_token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        botMessageService = new BotMessageServiceImpl(objectMapper, restTemplate);
        ReflectionTestUtils.setField(botMessageService, "CONFIRMATION_TOKEN", CONFIRMATION_TOKEN);
    }

    @Test
    void testCallbackConfirmation() throws JsonProcessingException {
        String body = BodyType.CONFIRMATION.getType();

        Map<String, Object> inputMap = Map.of("type", "confirmation");

        when(objectMapper.readValue(eq(body), any(TypeReference.class)))
                .thenReturn(inputMap);

        ResponseEntity<String> response = botMessageService.callback(body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(CONFIRMATION_TOKEN, response.getBody());
    }

    @Test
    void testCallbackNewMessage() throws Exception {
        String body = BodyType.MESSAGE_NEW.getType();

        Map<String, Object> messageObj = Map.of("message", Map.of("from_id", 12345, "text", "Hello"));
        Map<String, Object> inputMap = Map.of("type", "message_new", "object", messageObj);

        when(objectMapper.readValue(eq(body), any(TypeReference.class)))
                .thenReturn(inputMap);

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(new ResponseEntity<>("Вы сказали: Hello",HttpStatus.OK));

        ResponseEntity<String> response = botMessageService.callback(body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Ok", response.getBody());
        verify(restTemplate, times(1)).postForEntity(any(String.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    void testCallbackJsonProcessingException() throws JsonProcessingException {
        String body = BodyType.MESSAGE_NEW.getType();

        when(objectMapper.readValue(eq(body), any(TypeReference.class)))
                .thenThrow(new JsonProcessingException("Error parsing JSON"){});

        ResponseEntity<String> response = botMessageService.callback(body);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error parsing JSON", response.getBody());
    }


    private enum BodyType {
        CONFIRMATION("{\"type\":\"confirmation\"}"),
        MESSAGE_NEW("{\"type\":\"message_new\",\"object\":{\"message\":{\"from_id\":123,\"text\":\"Hello\"}}}");

        private final String type;

        BodyType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
