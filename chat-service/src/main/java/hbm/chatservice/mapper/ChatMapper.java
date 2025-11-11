package hbm.chatservice.mapper;

import hbm.chatservice.dto.ChatMessageDto;
import hbm.chatservice.dto.ChatMessageRequestDto;
import hbm.chatservice.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    ChatMessageDto toDto(ChatMessage message);
    ChatMessage toEntity(ChatMessageDto dto);

    // Ánh xạ từ Request (Client) sang Entity (DB)
    @Mapping(target = "sessionId", ignore = true) // Sẽ được Service gán
    @Mapping(target = "senderId", ignore = true)  // Sẽ được Service gán
    @Mapping(target = "isFromBot", constant = "false")
    @Mapping(target = "isRead", constant = "false")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ChatMessage toEntity(ChatMessageRequestDto requestDto);
}