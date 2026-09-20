package com.gameexpert.chat.service;

import com.gameexpert.ws.WorldBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocalChatSender {
    private final WorldBroadcaster broadcaster;

    public void send(Long worldId, Object message) {
        // Lv 14: 같은 월드 참여자 전체에게 브로드캐스트
        broadcaster.broadcast(worldId, message);
    }
}
