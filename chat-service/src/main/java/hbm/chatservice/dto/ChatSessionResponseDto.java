package hbm.chatservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatSessionResponseDto {
    private Long id;
    private Long customerId;
    private Long hostId;
    // Có thể thêm thông tin tóm tắt của người còn lại (tên, avatar)
    private String lastMessagePreview;
    private LocalDateTime lastMessageAt;
    private Long unreadCount;
}
