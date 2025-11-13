package hbm.chatservice.controller;

import hbm.chatservice.dto.ChatMessageDto;
import hbm.chatservice.entity.ChatMessage;
import hbm.chatservice.repository.ChatMessageRepository;
import hbm.chatservice.repository.ChatSessionRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;

    public ChatController(SimpMessagingTemplate messagingTemplate,
                          ChatMessageRepository chatMessageRepository,
                          ChatSessionRepository chatSessionRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageRepository = chatMessageRepository;
        this.chatSessionRepository = chatSessionRepository;
    }

    @MessageMapping("/chat.send") // client gửi tới /app/chat.send
    public void sendMessage(@Payload ChatMessageDto chatMessageDTO) {
        // Lưu vào DB
        ChatMessage message = new ChatMessage();
        message.setSessionId(chatMessageDTO.getSessionId());
        message.setSenderId(chatMessageDTO.getSenderId());
        message.setContent(chatMessageDTO.getContent());
        message.setMessageType(chatMessageDTO.getMessageType());
        chatMessageRepository.save(message);

        // gửi tới tất cả subscribe topic
        messagingTemplate.convertAndSend(
                "/topic/session." + chatMessageDTO.getSessionId(),
                chatMessageDTO
        );
    }
}
