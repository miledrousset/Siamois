package fr.siamois.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "system_role", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "system_role_role_name_key", columnNames = {"role_name"})
})
public class SystemRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "system_role_id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "role_name", nullable = false, length = Integer.MAX_VALUE)
    private String roleName;

}