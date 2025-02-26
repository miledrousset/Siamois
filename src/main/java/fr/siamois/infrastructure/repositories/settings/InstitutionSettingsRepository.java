package fr.siamois.infrastructure.repositories.settings;

import fr.siamois.domain.models.settings.InstitutionSettings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionSettingsRepository extends CrudRepository<InstitutionSettings, Long> {

}
