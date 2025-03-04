package fr.siamois.domain.models.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

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
}