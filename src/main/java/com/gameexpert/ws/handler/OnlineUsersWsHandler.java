package com.gameexpert.ws.handler;

import java.util.List;
import com.gameexpert.ws.NicknameHandshakeInterceptor;
import com.gameexpert.ws.WorldBroadcaster;
import com.gameexpert.ws.WorldSessionRegistry;
import com.gameexpert.ws.WsMessageContext;
import com.gameexpert.ws.dto.OnlineUsersResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.JsonNode;

@Component
@RequiredArgsConstructor
public class OnlineUsersWsHandler implements WsMessageHandler {
    private final WorldSessionRegistry registry;
    private final WorldBroadcaster broadcaster;

    @Override
    public String type() {
        return "onlineUsers";
    }

    @Override
    public void handle(WsMessageContext context, JsonNode message) {
        // Lv 15: 열린 연결에서 닉네임 조회, 자연 순서 정렬 후 요청자에게만 응답
        List<String>users = registry.entries(context.worldId()).stream()
                .map(WorldSessionRegistry.Entry::session)
                .filter(WebSocketSession::isOpen)
                .map(session -> (String)session.getAttributes().get(NicknameHandshakeInterceptor.ATTR_NICKNAME))
                .sorted()
                .toList();
        broadcaster.sendTo(context.session(), new OnlineUsersResponse(users, users.size()));
    }
}
