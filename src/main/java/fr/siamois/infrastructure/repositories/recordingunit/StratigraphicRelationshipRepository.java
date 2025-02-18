package fr.siamois.infrastructure.repositories.recordingunit;

import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.recordingunit.StratigraphicRelationship;
import fr.siamois.models.recordingunit.StratigraphicRelationshipId;
import fr.siamois.models.vocabulary.Concept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StratigraphicRelationshipRepository extends JpaRepository<StratigraphicRelationship, StratigraphicRelationshipId> {

    List<StratigraphicRelationship> findByUnit1AndRelationshipType(RecordingUnit unit, Concept relationshipType);

    List<StratigraphicRelationship> findByUnit2AndRelationshipType(RecordingUnit unit, Concept relationshipType);
}