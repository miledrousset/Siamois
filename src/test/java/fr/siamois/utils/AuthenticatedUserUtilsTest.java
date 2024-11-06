package fr.siamois.utils;

import fr.siamois.models.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AuthenticatedUserUtilsTest {

    private final AuthenticatedUserUtils utils = new AuthenticatedUserUtils();

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @WithAnonymousUser
    void getAuthenticatedUser_shouldReturnEmptyOptional_whenNoUserSignedIn() {
        Optional<Person> opt = utils.getAuthenticatedUser();
        assertTrue(opt.isEmpty(), "Optional is not empty");
    }

    @Test
    void getAuthenticatedUser_shouldReturnPerson_whenUserSignedIn() {
        Person base = new Person();
        base.setUsername("testUsername");
        Authentication authentication = new TestingAuthenticationToken(base, base.getPassword(), base.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        Optional<Person> opt = utils.getAuthenticatedUser();

        // Assert
        assertTrue(opt.isPresent(), "Optional is empty");
        assertEquals("testUsername", opt.get().getUsername(), "Usernames do not match");
    }

}