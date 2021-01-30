package com.netcracker.odstc.logviewer.socket;

import com.netcracker.odstc.logviewer.models.Log;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class SocketManager {
    private static final String LOGS_EVENTS_DESTINATION = "/events/logs";
    private final SimpMessagingTemplate template;

    public SocketManager(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void sendNewLog(Log log) {
        this.template.convertAndSend(LOGS_EVENTS_DESTINATION + "/" + log.getParentId(), log);
    }
}
