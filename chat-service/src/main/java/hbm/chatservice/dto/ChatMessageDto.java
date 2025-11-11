package hbm.chatservice.dto;

import hbm.chatservice.constants.MessageType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageDto {
    private Long id;
    private Long sessionId;
    private Long senderId;
    private Boolean isFromBot;
    private MessageType messageType;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isRead;
}
