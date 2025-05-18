package fr.siamois.domain.models.permission;

import fr.siamois.domain.models.institution.Team;
import jakarta.persistence.*;
import jakarta.ws.rs.DefaultValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "entity_permission")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "entity_type", discriminatorType = DiscriminatorType.STRING)
@EqualsAndHashCode
@Data
public abstract class EntityPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    protected Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_team_id", nullable = false)
    protected Team team;

    @DefaultValue("false")
    @Column(name= "read_permission", nullable = false)
    protected boolean read = false;

    @DefaultValue("false")
    @Column(name= "update_permission", nullable = false)
    protected boolean update = false;

    public boolean hasPermission(PermissionType type) {
        return switch (type) {
            case READ -> read;
            case UPDATE -> update;
        };
    }

    public enum PermissionType {
        READ,
        UPDATE
    }

}
