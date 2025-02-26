package fr.siamois.services.recordingunit;

import fr.siamois.infrastructure.repositories.recordingunit.StratigraphicRelationshipRepository;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.recordingunit.StratigraphicRelationship;
import fr.siamois.models.recordingunit.StratigraphicRelationshipId;
import fr.siamois.models.vocabulary.Concept;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StratigraphicRelationshipService {

    private final StratigraphicRelationshipRepository relationshipRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public static final Concept SYNCHRONOUS;
    public static final Concept ASYNCHRONOUS;
    public static final Concept ASYNCHRONOUS_DEDUCTED;

    // Init concepts. We might do it differently later.
    // To be defined how we want to store the relationships and their labels.
    // maybe three columns : concept + synchron/async + certainty. We might need to store mapping between concepts and
    // sync/async
    static {
        SYNCHRONOUS = new Concept();
        SYNCHRONOUS.setId(-1L);
        ASYNCHRONOUS = new Concept();
        ASYNCHRONOUS.setId(-2L);
        ASYNCHRONOUS_DEDUCTED = new Concept();
        ASYNCHRONOUS_DEDUCTED.setId(-3L);
    }

    public StratigraphicRelationshipService(
            StratigraphicRelationshipRepository relationshipRepository) {

        this.relationshipRepository = relationshipRepository;

    }


    public List<RecordingUnit> getSynchronousUnits(RecordingUnit usq) {
        List<RecordingUnit> synchronousUnits = new ArrayList<>();

        // Find synchronous relationships where usq is unit1
        synchronousUnits.addAll(relationshipRepository.findByUnit1AndType(usq, SYNCHRONOUS)
                .stream()
                .map(StratigraphicRelationship::getUnit2)
                .toList());

        // Find synchronous relationships where usq is unit2
        synchronousUnits.addAll(relationshipRepository.findByUnit2AndType(usq, SYNCHRONOUS)
                .stream()
                .map(StratigraphicRelationship::getUnit1) // Here we take unit1 instead of unit2
                .toList());

        return synchronousUnits;
    }

    public List<RecordingUnit> getAnteriorUnits(RecordingUnit usq) {
        return relationshipRepository.findByUnit2AndType(usq, ASYNCHRONOUS)
                .stream()
                .map(StratigraphicRelationship::getUnit1) // If unit2 = usq, unit1 is anterior
                .toList();
    }

    public List<RecordingUnit> getPosteriorUnits(RecordingUnit usq) {
        return relationshipRepository.findByUnit1AndType(usq, ASYNCHRONOUS)
                .stream()
                .map(StratigraphicRelationship::getUnit2) // If unit1 = usq, unit2 is posterior
                .toList();
    }

    @Transactional
    public StratigraphicRelationship saveOrGet(RecordingUnit unit1, RecordingUnit unit2, Concept type) {

        // First, try to find the relationship with the given order of units (unit1, unit2)
        Optional<StratigraphicRelationship> opt = relationshipRepository.findByUnit1AndUnit2AndType(unit1, unit2, type);

        // If found, update the type and return the relationship
        if (opt.isPresent()) {
            StratigraphicRelationship existingRel = opt.get();
            existingRel.setType(type);
            return relationshipRepository.save(existingRel);
        }

        // If not found, check for the reversed relationship (unit2, unit1)
        opt = relationshipRepository.findByUnit1AndUnit2AndType(unit2, unit1, type);

        // If reversed relationship found, delete the old one and create a new one with the correct order
        if (opt.isPresent()) {
            StratigraphicRelationship existingRel = opt.get();
            relationshipRepository.delete(existingRel); // Delete the reversed relationship
            entityManager.flush(); // Ensure the deletion is flushed before proceeding because
            // there is a trigger in DB for reversed relationships

            // Create a new relationship with the correct order
            return save(unit1, unit2, type);
        }

        // If neither exists, create a new relationship
        return save(unit1, unit2, type);
    }

    private StratigraphicRelationship save(RecordingUnit unit1, RecordingUnit unit2, Concept type) {
        StratigraphicRelationship newRel = new StratigraphicRelationship();
        StratigraphicRelationshipId id = new StratigraphicRelationshipId();
        id.setUnit1Id(unit1.getId());
        id.setUnit2Id(unit2.getId());
        newRel.setId(id);
        newRel.setType(type);
        newRel.setUnit1(unit1);
        newRel.setUnit2(unit2);

        return relationshipRepository.save(newRel);
    }

}
