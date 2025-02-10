package fr.siamois.models.ark;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;

@Data
@Entity
@Table(name = "ark_server", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "ark_server_server_ark_uri_key", columnNames = {"server_ark_uri"})
})
public class ArkServer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ark_server_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "server_ark_uri", nullable = false, length = Integer.MAX_VALUE)
    private String serverArkUri;

    @ColumnDefault("false")
    @Column(name = "is_local_server")
    private Boolean isLocalServer;

}