package org.example.VkBotTest.services.servicesImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.VkBotTest.services.BotMessageService;
import org.example.VkBotTest.util.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
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

    @Autowired
    public BotMessageServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseEntity<String> callback(String body) {
        try {
            Map<String, Object> data = objectMapper.readValue(body,  new TypeReference<Map<String, Object>>(){});
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

        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
        }
    }

    private void sendMessage(long userId, String message) {
        try {
            String encodedMessage = URLEncoder.encode(message, "UTF-8");
            String url = "https://api.vk.com/method/messages.send?access_token=" + TOKEN + "&v=" + API_VERSION;

            String params = "peer_id=" + userId + "&message=" + encodedMessage + "&random_id=" + System.currentTimeMillis();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<String> requestEntity = new HttpEntity<>(params, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            System.out.println(response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
