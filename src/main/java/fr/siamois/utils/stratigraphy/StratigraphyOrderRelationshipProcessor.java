package fr.siamois.utils.stratigraphy;

import fr.siamois.models.recordingunit.StratigraphicRelationship;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.recordingunit.StratigraphicRelationshipService;
import fr.siamois.models.exceptions.stratigraphy.StratigraphicConflictFound;
import lombok.Data;

import java.util.Optional;
import java.util.Set;
import java.util.List;

@Data
public class StratigraphyOrderRelationshipProcessor {

    // Stratigraphic unit list and their relationship
    private final List<SynchronousGroup> groupList;
    List<String> collecComm;
    Boolean reiterate;
    boolean[] coche ;

    public StratigraphyOrderRelationshipProcessor(List<SynchronousGroup> groupList, List<String> collecComm) {

        this.groupList = groupList;
        this.collecComm = collecComm;
        coche = new boolean[groupList.size()];
        reiterate = false;

    }

    public boolean containsRelationshipOfType(Set<StratigraphicRelationship> relationships,
                                        Concept type) {
        return relationships.stream()
                .anyMatch(rel -> rel.getType().equals(type));
    }

    public boolean hasRelationshipWithUnit2OfType(SynchronousGroup unit1, SynchronousGroup unit2, Concept type) {
        return unit1.getRelationshipsAsUnit1().stream()
                .anyMatch(rel -> rel.getUnit2().equals(unit2) && rel.getType().equals(type));
    }

    public boolean hasRelationshipWithUnit2(SynchronousGroup unit1, SynchronousGroup unit2) {
        return unit1.getRelationshipsAsUnit1().stream()
                .anyMatch(rel -> rel.getUnit2().equals(unit2));
    }

    public Optional<StratigraphicRelationship> getRelationshipWithUnit2(SynchronousGroup unit1, SynchronousGroup unit2) {
        return unit1.getRelationshipsAsUnit1().stream()
                .filter(rel -> rel.getUnit2().equals(unit2))
                .findFirst(); // Returns an Optional containing the first match, or empty if none found
    }


    public Boolean processUnitRelationships(SynchronousGroup group1, boolean[] coche) {
        Boolean reiterate = false;

        for(int u2 = 0; u2 < groupList.size(); u2++) {
            SynchronousGroup group2 = groupList.get(u2);
            // If group2 has not been processed and there is a relationship between group1 and group2
            if(!coche[u2] && hasRelationshipWithUnit2(group1, group2)) {
                coche[u2] = true;

                // Check for reflexive relationship
                if(group1 == group2) {
                    throw new StratigraphicConflictFound("Conflit");
                }
                else {
                    for(SynchronousGroup group3 : groupList) {
                        // We look for rels between group 2 and group 3
                        if(hasRelationshipWithUnit2(group2, group3)) {
                            // if the relationship has not been deducted yet
                            if(!hasRelationshipWithUnit2OfType(
                                    group1, group3, StratigraphicRelationshipService.ASYNCHRONOUS_DEDUCTED
                            )) {
                                // If no relationships at all
                                if(!hasRelationshipWithUnit2(
                                        group1, group3
                                )) {
                                    reiterate = true;
                                }
                                // Get and modify the rel
                                Optional<StratigraphicRelationship> rel = getRelationshipWithUnit2(group1,group3);
                                if(rel.isPresent()) {
                                    // type is now asynchronous deducted
                                    rel.get().setType(StratigraphicRelationshipService.ASYNCHRONOUS_DEDUCTED);
                                }
                                else {
                                    // we add it as asynchronous deducted
                                    StratigraphicRelationship newRel = new StratigraphicRelationship();
                                    newRel.setUnit1(group1);
                                    newRel.setUnit2(group3);
                                    newRel.setType(StratigraphicRelationshipService.ASYNCHRONOUS_DEDUCTED);
                                    group1.getRelationshipsAsUnit1().add(newRel);
                                }
                            }
                        }
                    }
                }


            }
        }

        return reiterate;
    }

    public void deductRelationshipByTransitivity() {

        // dedeucation par transitivité de toute les realtions redondantes et non redondantes
        for(SynchronousGroup group : groupList ) {
            // If the group has certain asynch relationships
            if(containsRelationshipOfType(group.getRelationshipsAsUnit1(), StratigraphicRelationshipService.ASYNCHRONOUS)) {
                coche = new boolean[groupList.size()]; // reinit marks
                do {
                    reiterate = processUnitRelationships(group, coche);
                }
                while(reiterate);
            }
        }
    }

    public void process() {

        coche = new boolean[groupList.size()];
        reiterate = false;

        deductRelationshipByTransitivity();

    }

}
