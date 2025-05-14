package fr.siamois.domain.services.auth;

import fr.siamois.domain.models.auth.pending.PendingInstitutionInvite;
import fr.siamois.domain.models.auth.pending.PendingPerson;
import fr.siamois.domain.models.auth.pending.PendingTeamInvite;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.institution.Team;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.LangService;
import fr.siamois.infrastructure.database.repositories.person.PendingInstitutionInviteRepository;
import fr.siamois.infrastructure.database.repositories.person.PendingPersonRepository;
import fr.siamois.infrastructure.database.repositories.person.PendingTeamInviteRepository;
import fr.siamois.ui.email.EmailManager;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PendingPersonServiceTest {

    @Mock
    private PendingPersonRepository pendingPersonRepository;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private PendingInstitutionInviteRepository pendingInstitutionInviteRepository;
    @Mock
    private PendingTeamInviteRepository pendingTeamInviteRepository;
    @Mock
    private EmailManager emailManager;
    @Mock
    private LangService langService;

    @InjectMocks
    private PendingPersonService pendingPersonService;

    private PendingPerson pendingPerson;
    private Institution institution;

    @BeforeEach
    void setUp() {
        pendingPerson = new PendingPerson();
        pendingPerson.setEmail("test@example.com");
        pendingPerson.setRegisterToken("token123");
        pendingPerson.setPendingInvitationExpirationDate(OffsetDateTime.now().plusDays(3));

        institution = new Institution();
        institution.setName("Test Institution");
    }

    @Test
    void testGenerateToken_UniqueToken() {
        when(pendingPersonRepository.existsByRegisterToken(anyString())).thenReturn(false);

        String token = pendingPersonService.generateToken();

        assertNotNull(token);
        assertEquals(20, token.length());
        verify(pendingPersonRepository, atLeastOnce()).existsByRegisterToken(anyString());
    }

    @Test
    void testGenerateToken_MaxAttemptsReached() {
        when(pendingPersonRepository.existsByRegisterToken(anyString())).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> pendingPersonService.generateToken());

        assertEquals("Unable to generate a unique token after 1000 attempts", exception.getMessage());
    }

    @Test
    void testInvitationLink() {
        when(httpServletRequest.getScheme()).thenReturn("http");
        when(httpServletRequest.getServerName()).thenReturn("localhost");
        when(httpServletRequest.getServerPort()).thenReturn(8080);
        when(httpServletRequest.getContextPath()).thenReturn("/app");

        String link = pendingPersonService.invitationLink(pendingPerson);

        assertEquals("http://localhost:8080/app/register/token123", link);
    }

    @Test
    void testCreateOrGetPendingPerson_ExistingPerson() {
        when(pendingPersonRepository.findByEmail("test@example.com")).thenReturn(Optional.of(pendingPerson));

        PendingPerson result = pendingPersonService.createOrGetPendingPerson("test@example.com");

        assertEquals(pendingPerson, result);
        verify(pendingPersonRepository, never()).save(any());
    }

    @Test
    void testCreateOrGetPendingPerson_NewPerson() {
        when(pendingPersonRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(pendingPersonRepository.save(any(PendingPerson.class))).thenReturn(pendingPerson);

        PendingPerson result = pendingPersonService.createOrGetPendingPerson("test@example.com");

        assertEquals(pendingPerson, result);
        verify(pendingPersonRepository).save(any(PendingPerson.class));
    }

    @Test
    void testSendPendingInstitutionInvite_AlreadyExists() {
        when(pendingInstitutionInviteRepository.findByInstitutionAndPendingPerson(institution, pendingPerson))
                .thenReturn(Optional.of(new PendingInstitutionInvite()));

        boolean result = pendingPersonService.sendPendingInstitutionInvite(pendingPerson, institution, "en");

        assertFalse(result);
        verify(emailManager, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testSendPendingInstitutionInvite_NewInvite() {
        pendingPerson.setEmail("someMail@mail.com");

        when(pendingInstitutionInviteRepository.findByInstitutionAndPendingPerson(institution, pendingPerson))
                .thenReturn(Optional.empty());

        when(langService.msg(anyString(), any(Locale.class), anyString())).thenReturn("SUBJECT");
        when(langService.msg(anyString(), any(Locale.class), anyString(), anyString(), anyString(), anyString())).thenReturn("BODY");

        boolean result = pendingPersonService.sendPendingInstitutionInvite(pendingPerson, institution, "en");

        assertTrue(result);
        verify(emailManager).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testAddTeamToInvitation_NewTeam() {
        PendingInstitutionInvite invite = new PendingInstitutionInvite();
        Team team = new Team();
        team.setId(1L);
        Concept role = new Concept();

        when(pendingTeamInviteRepository.findByPendingInstitutionInvite(invite)).thenReturn(Set.of());

        pendingPersonService.addTeamToInvitation(invite, team, role);

        verify(pendingTeamInviteRepository).save(any(PendingTeamInvite.class));
    }

    @Test
    void testDeletePendingPerson() {
        pendingPersonService.delete(pendingPerson);

        verify(pendingPersonRepository).delete(pendingPerson);
    }

    @Test
    void testFindByToken() {
        when(pendingPersonRepository.findByRegisterToken("token123")).thenReturn(Optional.of(pendingPerson));

        Optional<PendingPerson> result = pendingPersonService.findByToken("token123");

        assertTrue(result.isPresent());
        assertEquals(pendingPerson, result.get());
    }
}