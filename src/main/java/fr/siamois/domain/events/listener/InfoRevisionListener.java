package fr.siamois.domain.events.listener;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.history.InfoRevisionEntity;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.infrastructure.database.repositories.institution.InstitutionRepository;
import fr.siamois.infrastructure.database.repositories.person.PersonRepository;
import fr.siamois.ui.bean.SessionSettingsBean;
import jakarta.persistence.PrePersist;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class InfoRevisionListener {

    private final ApplicationContext applicationContext;

    private SessionSettingsBean sessionSettingsBean;
    private PersonRepository personRepository;
    private InstitutionRepository institutionRepository;

    public InfoRevisionListener(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PrePersist
    private void onPersist(InfoRevisionEntity entity) {
        if (sessionSettingsBean == null) {
            sessionSettingsBean = applicationContext.getBean(SessionSettingsBean.class);
        }

        if (personRepository == null) {
            personRepository = applicationContext.getBean(PersonRepository.class);
        }

        if (institutionRepository == null) {
            institutionRepository = applicationContext.getBean(InstitutionRepository.class);
        }

        UserInfo info = sessionSettingsBean.getUserInfo();
        if (info == null) {
            Person admin = personRepository.findAllSuperAdmin().get(0);
            Institution defaultInsti = institutionRepository.findInstitutionByIdentifier("siamois").orElse(null);
            info = new UserInfo(defaultInsti, admin, "en");
        }

        entity.setUpdatedBy(info.getUser());
        entity.setUpdatedFrom(info.getInstitution());
    }

}
