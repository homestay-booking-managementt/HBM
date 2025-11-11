package hbm.chatservice.entity;

import hbm.chatservice.constants.InitiatedBy;
import hbm.chatservice.constants.SessionStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_session")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "customer_id")
    Long customerId;

    @Column(name = "host_id")
    Long hostId;

    @Enumerated(EnumType.STRING)
    @Column(name = "initiated_by")
    InitiatedBy initiatedBy;

    @Column(name = "is_with_bot")
    Boolean isWithBot;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    SessionStatus status;

    @Column(name = "started_at", updatable = false)
    LocalDateTime startedAt;

    @Column(name = "ended_at")
    LocalDateTime endedAt;

    // Trường bổ sung để giúp sắp xếp danh sách session hiệu quả
    @Column(name = "last_message_at")
    LocalDateTime lastMessageAt;
}
