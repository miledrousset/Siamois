package fr.siamois.services.recordingunit;

import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitRepository;
import fr.siamois.infrastructure.repositories.recordingunit.StratigraphicRelationshipRepository;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.vocabulary.Concept;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.siamois.models.recordingunit.StratigraphicRelationship;

@Service
public class StratigraphicRelationshipService {

    private final RecordingUnitRepository unitRepository;
    private final StratigraphicRelationshipRepository relationshipRepository;
    private final Concept synchronous ;
    private final Concept asynchronous ;

    public StratigraphicRelationshipService(RecordingUnitRepository unitRepository, StratigraphicRelationshipRepository relationshipRepository) {

        this.unitRepository = unitRepository;
        this.relationshipRepository = relationshipRepository;

        // Init concepts. We might do it differently later.
        // To be defined how we want to store the relationships and their labels.
        synchronous = new Concept();
        asynchronous = new Concept();
        synchronous.setId(-1L);
        asynchronous.setId(-2L);

    }


    public List<RecordingUnit> getSynchronousUnits(RecordingUnit usq) {
        List<RecordingUnit> synchronousUnits = new ArrayList<>();

        // Find synchronous relationships where usq is unit1
        synchronousUnits.addAll(relationshipRepository.findByUnit1AndRelationshipType(usq, synchronous)
                .stream()
                .map(StratigraphicRelationship::getUnit2)
                .toList());

        // Find synchronous relationships where usq is unit2
        synchronousUnits.addAll(relationshipRepository.findByUnit2AndRelationshipType(usq, synchronous)
                .stream()
                .map(StratigraphicRelationship::getUnit1) // Here we take unit1 instead of unit2
                .toList());

        return synchronousUnits;
    }

    public List<RecordingUnit> getAnteriorUnits(RecordingUnit usq) {
        return relationshipRepository.findByUnit2AndRelationshipType(usq, asynchronous)
                .stream()
                .map(StratigraphicRelationship::getUnit1) // If unit2 = usq, unit1 is anterior
                .collect(Collectors.toList());
    }

    public List<RecordingUnit> getPosteriorUnits(RecordingUnit usq) {
        return relationshipRepository.findByUnit1AndRelationshipType(usq, asynchronous)
                .stream()
                .map(StratigraphicRelationship::getUnit2) // If unit1 = usq, unit2 is posterior
                .collect(Collectors.toList());
    }

    public Optional<RecordingUnit> getUnitById(Long id) {
        return unitRepository.findById(id);
    }
}
