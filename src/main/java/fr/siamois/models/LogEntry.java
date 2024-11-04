package fr.siamois.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
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
    private Person fkUser;

    @Column(name = "log_date")
    private OffsetDateTime logDate;

    @Column(name = "message", length = Integer.MAX_VALUE)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_ark_id")
    private Ark fkArk;

}