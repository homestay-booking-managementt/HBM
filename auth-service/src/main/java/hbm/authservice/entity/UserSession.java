package hbm.authservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "user_session")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long userId;
    String refreshToken;
    LocalDateTime expiresAt;
    LocalDateTime createdAt;
}
