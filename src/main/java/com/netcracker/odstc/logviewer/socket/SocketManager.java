package com.netcracker.odstc.logviewer.socket;

import com.netcracker.odstc.logviewer.models.Log;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SocketManager {
    private final SimpMessagingTemplate template;
    private static final String LOGS_EVENTS_DESTINATION = "/events/logs";

    public SocketManager(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void sendNewLogs(List<Log> logs){
        for(Log log : logs) {
            this.template.convertAndSend(LOGS_EVENTS_DESTINATION+"/"+log.getParentId(), log);
        }
    }
}
