package hbm.authservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Table(name = "role")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role {

    @Id
    Short id;
    String name;
    String description;
}
