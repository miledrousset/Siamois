package fr.siamois.models;

import fr.siamois.models.ark.Ark;
import fr.siamois.models.auth.Person;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "log_entry", schema = "public", indexes = {
        @Index(name = "idx_log_entry_user_id", columnList = "fk_user_id"),
        @Index(name = "idx_log_entry_log_date", columnList = "log_date")
})
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_entry_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id")
    private Person person;

    @TimeZoneStorage(TimeZoneStorageType.NATIVE)
    @Column(name = "log_date")
    private OffsetDateTime logDate;

    @Column(name = "message", length = Integer.MAX_VALUE)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_ark_id")
    private Ark ark;

}