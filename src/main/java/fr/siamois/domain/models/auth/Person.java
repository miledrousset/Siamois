package fr.siamois.domain.models.auth;

import fr.siamois.domain.models.FieldCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@Entity
@Table(name = "person", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "person_username_key", columnNames = {"username"}),
        @UniqueConstraint(name = "person_mail_key", columnNames = {"mail"})
})
public class Person implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id", nullable = false)
    private Long id;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "lastname", length = Integer.MAX_VALUE)
    private String lastname;

    @NotNull
    @Column(name = "username", nullable = false, length = Integer.MAX_VALUE)
    private String username;

    @NotNull
    @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
    private String password;

    @NotNull
    @Column(name = "mail", nullable = false, length = Integer.MAX_VALUE)
    private String mail;

    @ColumnDefault("false")
    @Column(name = "pass_to_modify")
    private Boolean passToModify;

    @ColumnDefault("false")
    @Column(name = "alert_mail")
    private Boolean alertMail;

    @ColumnDefault("false")
    @Column(name = "is_super_admin")
    @Getter(AccessLevel.NONE)
    private Boolean isSuperAdmin;

    @Column(name = "api_key", length = Integer.MAX_VALUE)
    private String apiKey;

    @ColumnDefault("false")
    @Column(name = "key_never_expire")
    private Boolean keyNeverExpire;

    @Column(name = "key_expires_at")
    private OffsetDateTime keyExpiresAt;

    @ColumnDefault("false")
    @Column(name = "is_service_account")
    private Boolean isServiceAccount;

    @Column(name = "key_description", length = Integer.MAX_VALUE)
    private String keyDescription;

    @ColumnDefault("false")
    @Column(name = "is_expired")
    private boolean isExpired;

    @ColumnDefault("false")
    @Column(name = "is_locked")
    private boolean isLocked;

    @ColumnDefault("true")
    @Column(name = "is_enabled")
    private boolean isEnabled;

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SystemRole> roles = new ArrayList<>();
        if (isSuperAdmin)
            roles.add(new SystemRole("SUPER_ADMIN"));
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !isExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @FieldCode
    public static final String USER_ROLE_FIELD_CODE = "SIAP.ROLE";
}