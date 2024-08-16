package org.example.VkBotTest.services.servicesImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.VkBotTest.exceptions.VkApiException;
import org.example.VkBotTest.services.BotMessageService;
import org.example.VkBotTest.util.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class BotMessageServiceImpl implements BotMessageService {

    @Value("${TOKEN}")
    private String TOKEN;

    @Value("${CONFIRMATION_TOKEN}")
    private String CONFIRMATION_TOKEN;

    @Value("${API_VERSION}")
    private String API_VERSION;

    private final ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(BotMessageServiceImpl.class);

    @Autowired
    public BotMessageServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseEntity<String> callback(String body) {
        try {
            Map<String, Object> data = objectMapper.readValue(body,  new TypeReference<>(){});
            String type = (String) data.get("type");

            if(MessageType.CONFIRMATION.getType().equals(type)){
                return ResponseEntity.ok(CONFIRMATION_TOKEN);
            }

            if(MessageType.MESSAGE_NEW.getType().equals(type)){
                Map<String, Object> messageData = (Map<String, Object>) data.get("object");
                Map<String, Object> messageContent = (Map<String, Object>) messageData.get("message");

                Integer userId = (Integer) messageContent.get("from_id");

                String messageText = (String) messageContent.get("text");
                String responseMessage = "Вы сказали: " + messageText;
                sendMessage(userId, responseMessage);
            }

            return ResponseEntity.ok("Ok");

        }catch (VkApiException e){
            logger.error("Vk API error: {}", e.getMessage(),e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e){
            logger.error("Error processing request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
        }
    }

    private void sendMessage(long userId, String message) throws VkApiException{
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        String url = "https://api.vk.com/method/messages.send?access_token=" + TOKEN + "&v=" + API_VERSION;

        String params = "peer_id=" + userId + "&message=" + encodedMessage + "&random_id=" + System.currentTimeMillis();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> requestEntity = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new VkApiException("Failed to send message to VK API: " + response.getBody());
        }

        logger.info("Message sent successfully: {}", response.getBody());
    }

}
