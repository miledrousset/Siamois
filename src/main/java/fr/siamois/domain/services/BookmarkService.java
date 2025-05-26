package fr.siamois.domain.services;

import fr.siamois.domain.models.Bookmark;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.infrastructure.database.repositories.BookmarkRepository;
import fr.siamois.ui.bean.panel.models.panel.AbstractPanel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    public List<AbstractPanel> findPanelsOf(UserInfo userInfo) {
        Person person = userInfo.getUser();
        Institution institution = userInfo.getInstitution();

        Optional<Bookmark> optBookmark = bookmarkRepository.findByPersonAndInstitution(person, institution);
        if (optBookmark.isPresent()) {
            return optBookmark.get().getSavedPanels();
        } else {
            return List.of();
        }

    }

}
