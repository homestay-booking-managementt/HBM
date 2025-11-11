package hbm.chatservice.entity;

import hbm.chatservice.constants.MessageType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "session_id", nullable = false)
    Long sessionId;

    @Column(name = "sender_id")
    Long senderId;

    @Column(name = "is_from_bot")
    Boolean isFromBot = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    MessageType messageType = MessageType.TEXT;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    String content;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @Column(name = "is_read")
    Boolean isRead = false;
}
