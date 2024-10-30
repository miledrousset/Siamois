package fr.siamois.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "person", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "person_username_key", columnNames = {"username"}),
        @UniqueConstraint(name = "person_mail_key", columnNames = {"mail"})
})
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id", nullable = false)
    private Integer id;

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

}