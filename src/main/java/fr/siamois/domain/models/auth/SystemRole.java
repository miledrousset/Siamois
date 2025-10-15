package fr.siamois.domain.models.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

@Data
@AllArgsConstructor
public class SystemRole implements GrantedAuthority {

    private String roleName;

    public String getAuthority() {
        return roleName.toUpperCase();
    }

    public String toString() {
        return roleName.toUpperCase();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SystemRole systemRole)) return false;

        return roleName.equals(systemRole.roleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleName);
    }

}