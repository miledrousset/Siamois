package fr.siamois.domain.services.person;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.TeamAlreadyExistException;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.institution.Team;
import fr.siamois.domain.models.institution.TeamPerson;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.infrastructure.database.repositories.person.TeamPersonRepository;
import fr.siamois.infrastructure.database.repositories.person.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamPersonRepository teamPersonRepository;

    @InjectMocks
    private TeamService teamService;

    private Institution institution;
    private Person person;
    private Team team;

    @BeforeEach
    void setUp() {
        institution = new Institution();
        institution.setId(1L);
        institution.setName("Test Institution");

        person = new Person();
        person.setId(1L);
        person.setName("Test Person");

        team = new Team();
        team.setId(1L);
        team.setName("Test Team");
        team.setInstitution(institution);
    }

    @Test
    void testAddPersonToTeamIfNotAdded_NewPerson() {
        when(teamPersonRepository.findByPersonAndTeam(person, team)).thenReturn(Optional.empty());

        teamService.addPersonToTeamIfNotAdded(person, team, null);

        verify(teamPersonRepository).save(any(TeamPerson.class));
    }

    @Test
    void testAddPersonToTeamIfNotAdded_ExistingPerson() {
        TeamPerson existingTeamPerson = new TeamPerson(team, person, null);
        when(teamPersonRepository.findByPersonAndTeam(person, team)).thenReturn(Optional.of(existingTeamPerson));

        teamService.addPersonToTeamIfNotAdded(person, team, new Concept());

        verify(teamPersonRepository).save(existingTeamPerson);
        assertNotNull(existingTeamPerson.getRoleInTeam());
    }

    @Test
    void testAddPersonToInstitutionIfNotExist_DefaultTeamExists() {
        when(teamRepository.findDefaultOf(institution.getId())).thenReturn(Optional.of(team));

        teamService.addPersonToInstitutionIfNotExist(person, institution);

        verify(teamPersonRepository).findByPersonAndTeam(person, team);
    }

    @Test
    void testAddPersonToInstitutionIfNotExist_DefaultTeamDoesNotExist() {
        when(teamRepository.findDefaultOf(institution.getId())).thenReturn(Optional.empty());
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        teamService.addPersonToInstitutionIfNotExist(person, institution);

        verify(teamRepository).save(any(Team.class));
        verify(teamPersonRepository).findByPersonAndTeam(person, team);
    }

    @Test
    void testFindTeamsOfInstitution_DefaultTeamExists() {
        when(teamRepository.findTeamsByInstitution(institution)).thenReturn(List.of(team));
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        Set<Team> result = teamService.findTeamsOfInstitution(person, institution);

        assertEquals(1, result.size());
        assertTrue(result.contains(team));
    }

    @Test
    void testFindTeamsOfInstitution_DefaultTeamDoesNotExist() {
        when(teamRepository.findTeamsByInstitution(institution)).thenReturn(List.of());
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        Set<Team> result = teamService.findTeamsOfInstitution(person, institution);

        assertEquals(1, result.size());
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void testNumberOfMembersInTeam() {
        when(teamRepository.countMembersOfTeam(team.getId())).thenReturn(5L);

        long result = teamService.numberOfMembersInTeam(team);

        assertEquals(5L, result);
    }

    @Test
    void testCreate_TeamAlreadyExists() {
        when(teamRepository.findTeamByNameInInstitution(institution.getId(), team.getName()))
                .thenReturn(Optional.of(team));

        assertThrows(TeamAlreadyExistException.class, () -> {
            teamService.create(team);
        });
    }

    @Test
    void testCreate_TeamDoesNotExist() throws TeamAlreadyExistException {
        when(teamRepository.findTeamByNameInInstitution(institution.getId(), team.getName()))
                .thenReturn(Optional.empty());
        when(teamRepository.save(team)).thenReturn(team);

        Team result = teamService.create(team);

        assertEquals(team, result);
        verify(teamRepository).save(team);
    }

    @Test
    void testUpdate_TeamNameChangedAndAlreadyExists() {
        Team existingTeam = new Team();
        existingTeam.setId(2L);
        existingTeam.setName("Existing Team");
        existingTeam.setInstitution(institution);

        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(existingTeam));
        when(teamRepository.findTeamByNameInInstitution(institution.getId(), team.getName()))
                .thenReturn(Optional.of(existingTeam));

        assertThrows(TeamAlreadyExistException.class, () -> {
            teamService.update(team);
        });

    }

    @Test
    void testUpdate_TeamNameNotChanged() throws TeamAlreadyExistException {
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        when(teamRepository.save(team)).thenReturn(team);

        Team result = teamService.update(team);

        assertEquals(team, result);
        verify(teamRepository).save(team);
    }

    @Test
    void testFindMembersOf() {
        TeamPerson teamPerson = new TeamPerson(team, person, null);
        when(teamPersonRepository.findAllOfInstitution(institution.getId())).thenReturn(List.of(teamPerson));

        List<TeamPerson> result = teamService.findMembersOf(institution);

        assertEquals(1, result.size());
        assertEquals(teamPerson, result.get(0));
    }

    @Test
    void testFindTeamPersonByTeam_DefaultTeam() {
        team.setDefaultTeam(true);
        TeamPerson teamPerson = new TeamPerson(team, person, null);
        when(teamPersonRepository.findAllOfInstitution(institution.getId())).thenReturn(List.of(teamPerson));

        List<TeamPerson> result = teamService.findTeamPersonByTeam(team);

        assertEquals(1, result.size());
        assertEquals(teamPerson, result.get(0));
    }

    @Test
    void testFindTeamPersonByTeam_NonDefaultTeam() {
        team.setDefaultTeam(false);
        TeamPerson teamPerson = new TeamPerson(team, person, null);
        when(teamPersonRepository.findByTeam(team)).thenReturn(List.of(teamPerson));

        List<TeamPerson> result = teamService.findTeamPersonByTeam(team);

        assertEquals(1, result.size());
        assertEquals(teamPerson, result.get(0));
    }

    @Test
    void testFindEarliestAddDateInInstitution() {
        OffsetDateTime date = OffsetDateTime.now();
        when(teamPersonRepository.findEarliestAddDateInInstitution(institution.getId(), person.getId()))
                .thenReturn(date);

        OffsetDateTime result = teamService.findEarliestAddDateInInstitution(institution, person);

        assertEquals(date, result);
    }

    @Test
    void testFindTeamsOfPersonInInstitution() {
        TeamPerson teamPerson = new TeamPerson(team, person, null);
        when(teamPersonRepository.findAllOfInstitution(institution.getId())).thenReturn(List.of(teamPerson));

        SortedSet<Team> result = teamService.findTeamsOfPersonInInstitution(person, institution);

        assertEquals(1, result.size());
        assertTrue(result.contains(team));
    }
}