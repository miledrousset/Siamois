package fr.siamois.models.auth;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
@Entity
@Table(name = "system_role", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "system_role_role_name_key", columnNames = {"role_name"})
})
public class SystemRole implements GrantedAuthority {
    @Id
    @Column(name = "system_role_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "role_name", nullable = false, length = Integer.MAX_VALUE)
    private String roleName;

    @Override
    public String getAuthority() {
        return roleName.toUpperCase();
    }

    @Override
    public String toString() {
        return roleName.toUpperCase();
    }
}