package hbm.chatservice.dto;

import hbm.chatservice.constants.MessageType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class ChatMessageRequestDto {

    @NotNull
    private Long homestayId;
    @NotBlank
    private String content;
    private MessageType messageType = MessageType.TEXT;
}
