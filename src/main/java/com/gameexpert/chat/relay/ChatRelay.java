package com.gameexpert.chat.relay;

import java.util.Map;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.gameexpert.chat.service.LocalChatSender;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatRelay implements MessageListener {
    public static final String CHANNEL = "webcraft:chat";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final LocalChatSender localChatSender;

    public void publish(Long worldId, Object message) {
        // Lv 20: worldId와 message를 JSON으로 묶어 채팅 채널에 발행
        Map<String, Object> envelope = Map.of("worldId", worldId, "message", message);
        String json =objectMapper.writeValueAsString(envelope);
        redisTemplate.convertAndSend(CHANNEL, json);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // Lv 20: JSON에서 worldId와 message를 읽어 localChatSender.send()로 전달
        JsonNode envelope = objectMapper.readTree(message.getBody());
        Long worldId = envelope.path("worldId").asLong();
        JsonNode payload = envelope.path("message");
        localChatSender.send(worldId, payload);
    }
}
