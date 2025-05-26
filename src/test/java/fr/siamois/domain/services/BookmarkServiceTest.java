package fr.siamois.domain.services;

import fr.siamois.domain.models.Bookmark;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.infrastructure.database.repositories.BookmarkRepository;
import fr.siamois.ui.bean.panel.models.panel.AbstractPanel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @InjectMocks
    private BookmarkService bookmarkService;

    @Test
    void testFindPanelsOf_BookmarkFound() {
        // Arrange
        UserInfo userInfo = mock(UserInfo.class);
        Person person = mock(Person.class);
        Institution institution = mock(Institution.class);
        Bookmark bookmark = mock(Bookmark.class);
        List<AbstractPanel> savedPanels = List.of(mock(AbstractPanel.class), mock(AbstractPanel.class));

        when(userInfo.getUser()).thenReturn(person);
        when(userInfo.getInstitution()).thenReturn(institution);
        when(bookmarkRepository.findByPersonAndInstitution(person, institution)).thenReturn(Optional.of(bookmark));
        when(bookmark.getSavedPanels()).thenReturn(savedPanels);

        // Act
        List<AbstractPanel> result = bookmarkService.findPanelsOf(userInfo);

        // Assert
        assertEquals(savedPanels, result);
        verify(bookmarkRepository).findByPersonAndInstitution(person, institution);
    }

    @Test
    void testFindPanelsOf_BookmarkNotFound() {
        // Arrange
        UserInfo userInfo = mock(UserInfo.class);
        Person person = mock(Person.class);
        Institution institution = mock(Institution.class);

        when(userInfo.getUser()).thenReturn(person);
        when(userInfo.getInstitution()).thenReturn(institution);
        when(bookmarkRepository.findByPersonAndInstitution(person, institution)).thenReturn(Optional.empty());

        // Act
        List<AbstractPanel> result = bookmarkService.findPanelsOf(userInfo);

        // Assert
        assertEquals(List.of(), result);
        verify(bookmarkRepository).findByPersonAndInstitution(person, institution);
    }

}