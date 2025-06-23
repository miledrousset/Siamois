package fr.siamois.domain.services;

import fr.siamois.domain.models.Bookmark;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.infrastructure.database.repositories.BookmarkRepository;
import fr.siamois.ui.bean.panel.models.panel.AbstractPanel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    @Transactional(readOnly = true)
    public List<Bookmark> findAll(UserInfo userInfo) {
        return bookmarkRepository.findByPersonAndInstitution(userInfo.getUser(), userInfo.getInstitution());
    }


    @Transactional
    public Bookmark save(UserInfo userInfo, AbstractPanel panel) {
        Bookmark bookmark = new Bookmark();
        bookmark.setPerson(userInfo.getUser());
        bookmark.setInstitution(userInfo.getInstitution());
        bookmark.setResourceUri(panel.ressourceUri());
        bookmark.setTitleCode(panel.getTitleCodeOrTitle());
        return bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void delete(UserInfo userInfo, AbstractPanel panel) {
        bookmarkRepository.deleteBookmarkByPersonAndInstitutionAndResourceUri(
                userInfo.getUser(),
                userInfo.getInstitution(),
                panel.ressourceUri()
        );
    }

    @Transactional
    public Bookmark save(UserInfo userInfo, String ressourceUri, String titleCodeOrTitle) {
        Bookmark bookmark = new Bookmark();
        bookmark.setPerson(userInfo.getUser());
        bookmark.setInstitution(userInfo.getInstitution());
        bookmark.setResourceUri(ressourceUri);
        bookmark.setTitleCode(titleCodeOrTitle);
        return bookmarkRepository.save(bookmark);
    }

    @Transactional(readOnly = true)
    public Boolean isRessourceBookmarkedByUser(UserInfo userInfo, String ressourceUri) {
        return bookmarkRepository.countBookmarkByPersonAndInstitutionAndResourceUri(
                userInfo.getUser(),
                userInfo.getInstitution(),
                ressourceUri) > 0;
    }

    @Transactional
    public void deleteBookmark(UserInfo userInfo, String ressourceUri) {
        bookmarkRepository.deleteBookmarkByPersonAndInstitutionAndResourceUri(
                userInfo.getUser(),
                userInfo.getInstitution(),
                ressourceUri
        );
    }
}
