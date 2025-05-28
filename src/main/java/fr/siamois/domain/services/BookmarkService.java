package fr.siamois.domain.services;

import fr.siamois.domain.models.Bookmark;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.infrastructure.database.repositories.BookmarkRepository;
import fr.siamois.ui.bean.panel.models.panel.AbstractPanel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    public List<Bookmark> findAll(UserInfo userInfo) {
        return bookmarkRepository.findByPersonAndInstitution(userInfo.getUser(), userInfo.getInstitution());
    }

    public Bookmark save(UserInfo userInfo, AbstractPanel panel) {
        Bookmark bookmark = new Bookmark();
        bookmark.setPerson(userInfo.getUser());
        bookmark.setInstitution(userInfo.getInstitution());
        bookmark.setResourceUri(panel.ressourceUri());
        bookmark.setTitle(panel.getTitle());
        return bookmarkRepository.save(bookmark);
    }
}
