package fr.siamois.utils.stratigraphy;

import fr.siamois.models.exceptions.stratigraphy.StratigraphicConflictFound;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.recordingunit.StratigraphicRelationship;
import fr.siamois.services.recordingunit.StratigraphicRelationshipService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class StratigraphyOrderRelationshipProcessorTest {

    @Test
    void testProcess_CircuitDetected() {
        // Create Synchronous Groups
        SynchronousGroup unitA = new SynchronousGroup(); unitA.setFullIdentifier("A"); unitA.setId(1L);
        unitA.setRelationshipsAsUnit1(new HashSet<>()); unitA.setRelationshipsAsUnit2(new HashSet<>());
        SynchronousGroup unitB = new SynchronousGroup(); unitB.setFullIdentifier("B"); unitB.setId(2L);
        unitB.setRelationshipsAsUnit1(new HashSet<>()); unitB.setRelationshipsAsUnit2(new HashSet<>());
        SynchronousGroup unitC = new SynchronousGroup(); unitC.setFullIdentifier("C"); unitC.setId(3L);
        unitC.setRelationshipsAsUnit1(new HashSet<>()); unitC.setRelationshipsAsUnit2(new HashSet<>());


        createAsynchronousRelationship(unitA, unitC);
        createAsynchronousRelationship(unitB, unitC);
        createAsynchronousRelationship(unitC, unitA);

        // Initialize the builder
        List<SynchronousGroup> units = List.of(unitA, unitB, unitC);
        StratigraphyOrderRelationshipProcessor processor = new StratigraphyOrderRelationshipProcessor(units);

        // Act
        processor.process();

        // Assert
        assertEquals(true, processor.isSignalConflict());

    }

    @Test
    void testProcess_Success() {
        // Create Synchronous Groups
        SynchronousGroup unitA = new SynchronousGroup(); unitA.setFullIdentifier("A"); unitA.setId(1L);
        unitA.setRelationshipsAsUnit1(new HashSet<>()); unitA.setRelationshipsAsUnit2(new HashSet<>());
        SynchronousGroup unitB = new SynchronousGroup(); unitB.setFullIdentifier("B"); unitB.setId(2L);
        unitB.setRelationshipsAsUnit1(new HashSet<>()); unitB.setRelationshipsAsUnit2(new HashSet<>());
        SynchronousGroup unitC = new SynchronousGroup(); unitC.setFullIdentifier("C"); unitC.setId(3L);
        unitC.setRelationshipsAsUnit1(new HashSet<>()); unitC.setRelationshipsAsUnit2(new HashSet<>());

        createAsynchronousRelationship(unitB, unitA);
        createAsynchronousRelationship(unitC, unitB);

        // Initialize the builder
        List<SynchronousGroup> units = List.of(unitA, unitB, unitC);
        StratigraphyOrderRelationshipProcessor processor = new StratigraphyOrderRelationshipProcessor(units);

        // Build synchronous groups
        processor.process();
        List<SynchronousGroup> groups = processor.getGroupList();

        // Assertions
        assertEquals(false, processor.isSignalConflict());
        assertEquals(0,unitA.getRelationshipsAsUnit1().size());
        assertEquals(1,unitB.getRelationshipsAsUnit1().size());
        assertEquals(2,unitC.getRelationshipsAsUnit1().size());
        // Make sure we deducted that C is anterior to A
        StratigraphicRelationship expectedRelationship = new StratigraphicRelationship();
        expectedRelationship.setUnit1(unitC);
        expectedRelationship.setUnit2(unitA);
        expectedRelationship.setType(StratigraphicRelationshipService.ASYNCHRONOUS_DEDUCTED);
        assertTrue(unitC.getRelationshipsAsUnit1().contains(expectedRelationship));


    }

    private void createAsynchronousRelationship(RecordingUnit unit1, RecordingUnit unit2) {
        StratigraphicRelationship relationship = new StratigraphicRelationship();
        relationship.setUnit1(unit1);
        relationship.setUnit2(unit2);
        relationship.setType(StratigraphicRelationshipService.ASYNCHRONOUS);
        unit1.getRelationshipsAsUnit1().add(relationship);
    }
}
