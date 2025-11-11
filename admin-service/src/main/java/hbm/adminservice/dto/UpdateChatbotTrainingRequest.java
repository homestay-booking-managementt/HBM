package hbm.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChatbotTrainingRequest {
    
    private String questionPattern;
    private String answerTemplate;
    private String category;
    private Boolean isActive;
}
