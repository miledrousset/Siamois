package fr.siamois.domain.services.auth;

import fr.siamois.domain.models.auth.pending.PendingInstitutionInvite;
import fr.siamois.domain.models.auth.pending.PendingPerson;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.LangService;
import fr.siamois.domain.utils.DateUtils;
import fr.siamois.infrastructure.database.repositories.person.PendingInstitutionInviteRepository;
import fr.siamois.infrastructure.database.repositories.person.PendingPersonRepository;
import fr.siamois.ui.email.EmailManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class PendingPersonService {

    private final PendingPersonRepository pendingPersonRepository;
    private final SecureRandom random = new SecureRandom();

    public static final int MAX_GENERATION = 1000;
    public static final int TOKEN_LENGTH = 20;
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final HttpServletRequest httpServletRequest;
    private final PendingInstitutionInviteRepository pendingInstitutionInviteRepository;
    private final EmailManager emailManager;
    private final LangService langService;

    public PendingPersonService(PendingPersonRepository pendingPersonRepository,
                                HttpServletRequest httpServletRequest,
                                PendingInstitutionInviteRepository pendingInstitutionInviteRepository,
                                EmailManager emailManager,
                                LangService langService) {
        this.pendingPersonRepository = pendingPersonRepository;
        this.httpServletRequest = httpServletRequest;
        this.pendingInstitutionInviteRepository = pendingInstitutionInviteRepository;
        this.emailManager = emailManager;
        this.langService = langService;
    }

    /**
     * Generate a random token for the pending person.
      * @return a random token
     */
    String generateToken() {
        StringBuilder token;
        int attempts = 0;

        do {
            attempts++;
            token = new StringBuilder();
            for (int i = 0; i < TOKEN_LENGTH; i++) {
                int randomIndex = random.nextInt(ALLOWED_CHARS.length());
                token.append(ALLOWED_CHARS.charAt(randomIndex));
            }
        } while (attempts < MAX_GENERATION && pendingPersonRepository.existsByRegisterToken(token.toString()));

        if (attempts == MAX_GENERATION) {
            throw new IllegalStateException("Unable to generate a unique pending person token after " + MAX_GENERATION + " attempts");
        }

        return token.toString();
    }

    /**
     * Generate the invitation link for the pending person.
     * @param pendingPerson the pending person
     * @return the invitation link
     */
    String invitationLink(PendingPerson pendingPerson) {
        String domain = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() +
                (httpServletRequest.getServerPort() != 80 && httpServletRequest.getServerPort() != 443 ? ":" + httpServletRequest.getServerPort() : "");
        return String.format("%s%s/register/%s", domain, httpServletRequest.getContextPath(), pendingPerson.getRegisterToken());
    }

    /**
     * Create or get a pending person by email.
     * @param email the email of the pending person
     * @return the pending person
     */
    public PendingPerson createOrGetPendingPerson(@Email String email) {
        Optional<PendingPerson> pendingPerson = pendingPersonRepository.findByEmail(email);
        if (pendingPerson.isPresent()) {
            return pendingPerson.get();
        } else {
            PendingPerson person = new PendingPerson();
            person.setEmail(email);
            person.setId(-1L);
            person.setRegisterToken(generateToken());
            person.setPendingInvitationExpirationDate(OffsetDateTime.now().plusDays(3));
            person = pendingPersonRepository.save(person);

            return person;
        }
    }

    /**
     * Send an invitation email to the pending person.
     * @param pendingPerson the pending person
     * @param institution the institution
     * @param mailLang the language of the email
     * @return true if the email was sent, false if the invitation already exists
     */
    public boolean sendPendingInstitutionInvite(PendingPerson pendingPerson, Institution institution, String mailLang) {
        return sendPendingInstitutionInvite(pendingPerson, institution, false, mailLang);
    }

    /**
     * Send an invitation email to the pending person with the option to set them as a manager.
     * @param pendingPerson the pending person
     * @param institution the institution
     * @param isManager true if the pending person should be a manager, false otherwise
     * @param mailLang the language of the email
     * @return true if the email was sent, false if the invitation already exists
     */
    public boolean sendPendingInstitutionInvite(PendingPerson pendingPerson, Institution institution, boolean isManager, String mailLang) {
        Optional<PendingInstitutionInvite> pendingInstitutionInvite = pendingInstitutionInviteRepository.findByInstitutionAndPendingPerson(institution, pendingPerson);
        if (pendingInstitutionInvite.isPresent()) {
            PendingInstitutionInvite invite = pendingInstitutionInvite.get();
            invite.setManager(isManager);
            pendingInstitutionInviteRepository.save(invite);
            return false;
        } else {
            PendingInstitutionInvite invite = new PendingInstitutionInvite();
            invite.setPendingPerson(pendingPerson);
            invite.setInstitution(institution);
            invite.setId(-1L);
            invite.setManager(isManager);
            invite = pendingInstitutionInviteRepository.save(invite);

            Locale locale = new Locale(mailLang);
            String institutionName = institution.getName();
            String invitationLink = invitationLink(pendingPerson);
            String expirationDate = DateUtils.formatOffsetDateTime(pendingPerson.getPendingInvitationExpirationDate());

            emailManager.sendEmail(pendingPerson.getEmail(),
                    langService.msg("mail.invitation.subject", locale, institutionName),
                    langService.msg("mail.invitation.body", locale, institutionName, invitationLink, expirationDate, expirationDate)
            );

            return true;
        }
    }

    public void delete(PendingPerson pendingPerson) {
        pendingPersonRepository.delete(pendingPerson);
    }

    public Optional<PendingPerson> findByToken(String token) {
        return pendingPersonRepository.findByRegisterToken(token);
    }

    public PendingInstitutionInvite createOrGetInstitutionInviteOf(PendingPerson pendingPerson, Institution institution, boolean isManager) {
        Optional<PendingInstitutionInvite> pendingInstitutionInvite = pendingInstitutionInviteRepository.findByInstitutionAndPendingPerson(institution, pendingPerson);
        if (pendingInstitutionInvite.isPresent()) {
            return pendingInstitutionInvite.get();
        } else {
            PendingInstitutionInvite invite = new PendingInstitutionInvite();
            invite.setPendingPerson(pendingPerson);
            invite.setInstitution(institution);
            invite.setId(-1L);
            invite.setManager(isManager);
            return pendingInstitutionInviteRepository.save(invite);
        }
    }

    public PendingInstitutionInvite createOrGetInstitutionInviteOf(PendingPerson pendingPerson, Institution institution) {
        return createOrGetInstitutionInviteOf(pendingPerson, institution, false);
    }

    public Set<PendingInstitutionInvite> findInstitutionsByPendingPerson(PendingPerson pendingPerson) {
        return pendingInstitutionInviteRepository.findAllByPendingPerson(pendingPerson);
    }

    public void deleteInstitutionInvite(PendingInstitutionInvite invite) {
        pendingInstitutionInviteRepository.delete(invite);
    }
}
