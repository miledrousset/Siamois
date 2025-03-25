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
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "person", schema = "public")
public class Person implements UserDetails {

    // This limit allows the UI to be controlled
    public static final int NAME_MAX_LENGTH = 64;

    // In UNIX, usernames are limited to 32 characters
    // If the system should one day communicate with a UNIX system, it should respect this limit.
    public static final int USERNAME_MAX_LENGTH = 32;

    // https://www.rfc-editor.org/errata/eid1003
    // RFC 3696 applies a limit 320 characters from email adresses.
    // 64 chars for the local part and 255 chars for the domain part
    public static final int LOCAL_MAIL_MAX_LENGTH = 64;
    public static final int DOMAIN_MAIL_MAX_LENGTH = 255;
    public static final int MAIL_MAX_LENGTH = LOCAL_MAIL_MAX_LENGTH + 1 + DOMAIN_MAIL_MAX_LENGTH;

    public static final int PASSWORD_MAX_LENGTH = 1024;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id", nullable = false)
    private Long id;

    @Column(name = "name", length = NAME_MAX_LENGTH)
    private String name;

    @Column(name = "lastname", length = NAME_MAX_LENGTH)
    private String lastname;

    @NotNull
    @Column(name = "username", nullable = false, length = USERNAME_MAX_LENGTH, unique = true)
    private String username;

    // The password length shouldn't be set in the database as we don't know their size after hash.
    @NotNull
    @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
    private String password;

    @NotNull
    @Column(name = "mail", nullable = false, length = MAIL_MAX_LENGTH, unique = true)
    private String mail;

    @ColumnDefault("false")
    @Column(name = "pass_to_modify")
    private boolean passToModify = false;

    @ColumnDefault("false")
    @Column(name = "alert_mail")
    private boolean alertMail = false;

    @ColumnDefault("false")
    @Column(name = "is_super_admin")
    @Getter(AccessLevel.NONE)
    private boolean isSuperAdmin = false;

    @Column(name = "api_key", length = Integer.MAX_VALUE)
    private String apiKey;

    @ColumnDefault("false")
    @Column(name = "key_never_expire")
    private boolean keyNeverExpire = false;

    @Column(name = "key_expires_at")
    private OffsetDateTime keyExpiresAt;

    @ColumnDefault("false")
    @Column(name = "is_service_account")
    private boolean isServiceAccount = false;

    @Column(name = "key_description", length = Integer.MAX_VALUE)
    private String keyDescription;

    @ColumnDefault("false")
    @Column(name = "is_expired")
    private boolean isExpired = false;

    @ColumnDefault("false")
    @Column(name = "is_locked")
    private boolean isLocked = false;

    @ColumnDefault("true")
    @Column(name = "is_enabled")
    private boolean isEnabled = true;

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SystemRole> roles = new ArrayList<>();
        if (Boolean.FALSE.equals(this.isSuperAdmin))
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

    // For displaying person full names in forms
    // Used in xhtml files
    public String displayName() {
        return name + " " + lastname;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return isExpired() == person.isExpired()
                && isLocked() == person.isLocked()
                && isEnabled() == person.isEnabled()
                && Objects.equals(getId(), person.getId())
                && Objects.equals(getName(), person.getName())
                && Objects.equals(getLastname(), person.getLastname())
                && Objects.equals(getUsername(), person.getUsername())
                && Objects.equals(getPassword(), person.getPassword())
                && Objects.equals(getMail(), person.getMail())
                && Objects.equals(passToModify, person.passToModify)
                && Objects.equals(alertMail, person.alertMail)
                && Objects.equals(isSuperAdmin, person.isSuperAdmin)
                && Objects.equals(getApiKey(), person.getApiKey())
                && Objects.equals(keyNeverExpire, person.keyNeverExpire)
                && Objects.equals(getKeyExpiresAt(), person.getKeyExpiresAt())
                && Objects.equals(isServiceAccount, person.isServiceAccount)
                && Objects.equals(getKeyDescription(), person.getKeyDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(),
                getLastname(), getUsername(),
                getPassword(), getMail(),
                passToModify,  alertMail,
                isSuperAdmin, getApiKey(),
                keyNeverExpire, getKeyExpiresAt(),
                isServiceAccount, getKeyDescription(),
                isExpired(), isLocked(), isEnabled());
    }
}