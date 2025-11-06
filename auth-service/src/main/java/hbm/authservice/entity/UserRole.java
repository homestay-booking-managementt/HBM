package hbm.authservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_role")
@FieldDefaults(level = AccessLevel.PRIVATE)
@IdClass(UserRoleId.class)
public class UserRole {

    @Id
    Long userId;
    @Id
    Short roleId;
}
