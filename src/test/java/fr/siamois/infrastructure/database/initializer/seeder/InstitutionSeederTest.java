package fr.siamois.infrastructure.database.initializer.seeder;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.infrastructure.database.repositories.institution.InstitutionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstitutionSeederTest {

    @Mock
    InstitutionRepository institutionRepository;

    @Mock
    PersonSeeder personSeeder;

    @InjectMocks
    InstitutionSeeder institutionSeeder;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void seed_AlreadyExists() {

        Institution i = new Institution();
        i.setIdentifier("test");

        List<InstitutionSeeder.InstitutionSpec> toInsert = List.of(
                new InstitutionSeeder.InstitutionSpec("Mon institution", "Test", "test", null)
        );

        when(institutionRepository.findInstitutionByIdentifier(anyString())).thenReturn(Optional.of(i));

        institutionSeeder.seed(toInsert);

        verify(institutionRepository, never()).save(any(Institution.class));

    }

    @Test
    void seed_PersonDoesNotExist() {

        Institution i = new Institution();
        i.setIdentifier("test");

        List<InstitutionSeeder.InstitutionSpec> toInsert = List.of(
                new InstitutionSeeder.InstitutionSpec("Mon institution", "Test", "test",
                        List.of("user@siamois.fr"))
        );

        when(personSeeder.findPersonOrReturnNull(anyString())).thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> institutionSeeder.seed(toInsert)
        );


        assertThat(ex.getMessage()).contains("Invalid email: user@siamois.fr");

    }

    @Test
    void seed_success() {

        Institution i = new Institution();
        Person p = new Person();
        i.setIdentifier("test");

        List<InstitutionSeeder.InstitutionSpec> toInsert = List.of(
                new InstitutionSeeder.InstitutionSpec("Mon institution", "Test", "test",
                        List.of("user@siamois.fr"))
        );

        when(personSeeder.findPersonOrReturnNull(anyString())).thenReturn(p);
        when(institutionRepository.findInstitutionByIdentifier(anyString())).thenReturn(Optional.empty());

        institutionSeeder.seed(toInsert);

        verify(institutionRepository, times(1)).save(any(Institution.class));

    }
}