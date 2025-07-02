package fr.siamois.domain.services;

import fr.siamois.domain.models.Bookmark;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.infrastructure.database.repositories.BookmarkRepository;
import fr.siamois.ui.bean.panel.models.panel.AbstractPanel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing bookmarks.
 * This service provides methods to find, save, and delete bookmarks for a user.
 */
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    /**
     * Finds all bookmarks for a user.
     *
     * @param userInfo the user information containing the user and institution
     * @return a list of bookmarks associated with the user and institution
     */
    @Transactional(readOnly = true)
    public List<Bookmark> findAll(UserInfo userInfo) {
        return bookmarkRepository.findByPersonAndInstitution(userInfo.getUser(), userInfo.getInstitution());
    }

    /**
     * Saves a bookmark for a user based on the provided panel.
     *
     * @param userInfo the user information containing the user and institution
     * @param panel    the panel containing the resource URI and title code or title
     * @return the saved bookmark
     */
    @Transactional
    public Bookmark save(UserInfo userInfo, AbstractPanel panel) {
        Bookmark bookmark = new Bookmark();
        bookmark.setPerson(userInfo.getUser());
        bookmark.setInstitution(userInfo.getInstitution());
        bookmark.setResourceUri(panel.ressourceUri());
        bookmark.setTitleCode(panel.getTitleCodeOrTitle());
        return bookmarkRepository.save(bookmark);
    }

    /**
     * Deletes a bookmark for a user based on the provided panel.
     *
     * @param userInfo the user information containing the user and institution
     * @param panel    the panel containing the resource URI
     */
    @Transactional
    public void delete(UserInfo userInfo, AbstractPanel panel) {
        bookmarkRepository.deleteBookmarkByPersonAndInstitutionAndResourceUri(
                userInfo.getUser(),
                userInfo.getInstitution(),
                panel.ressourceUri()
        );
    }

    /**
     * Saves a bookmark for a user with the specified resource URI and title code or title.
     *
     * @param userInfo         the user information containing the user and institution
     * @param ressourceUri     the URI of the resource to bookmark
     * @param titleCodeOrTitle The title code if the panel has a generic name, or the title of the resource
     * @return the saved bookmark
     */
    @Transactional
    public Bookmark save(UserInfo userInfo, String ressourceUri, String titleCodeOrTitle) {
        Bookmark bookmark = new Bookmark();
        bookmark.setPerson(userInfo.getUser());
        bookmark.setInstitution(userInfo.getInstitution());
        bookmark.setResourceUri(ressourceUri);
        bookmark.setTitleCode(titleCodeOrTitle);
        return bookmarkRepository.save(bookmark);
    }

    /**
     * Checks if a resource is bookmarked by a user.
     *
     * @param userInfo     the user information containing the user and institution
     * @param ressourceUri the URI of the resource to check
     * @return true if the resource is bookmarked by the user, false otherwise
     */
    @Transactional(readOnly = true)
    public Boolean isRessourceBookmarkedByUser(UserInfo userInfo, String ressourceUri) {
        return bookmarkRepository.countBookmarkByPersonAndInstitutionAndResourceUri(
                userInfo.getUser(),
                userInfo.getInstitution(),
                ressourceUri) > 0;
    }

    /**
     * Deletes a bookmark for a user based on the resource URI.
     *
     * @param userInfo     the user information containing the user and institution
     * @param ressourceUri the URI of the resource to delete the bookmark for
     */
    @Transactional
    public void deleteBookmark(UserInfo userInfo, String ressourceUri) {
        bookmarkRepository.deleteBookmarkByPersonAndInstitutionAndResourceUri(
                userInfo.getUser(),
                userInfo.getInstitution(),
                ressourceUri
        );
    }
}
