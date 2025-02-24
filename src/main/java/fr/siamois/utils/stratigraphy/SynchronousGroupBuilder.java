package fr.siamois.utils.stratigraphy;

import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.recordingunit.StratigraphicRelationship;
import fr.siamois.services.recordingunit.StratigraphicRelationshipService;
import jakarta.validation.constraints.NotNull;

import java.util.*;
import java.util.stream.IntStream;

public class SynchronousGroupBuilder {


    // Stratigraphic unit list and their relationship
    private final List<RecordingUnit> recordingUnits;

    private long[] enSynch; // ensemble synchrone (ES) de l'US (O si pas en synchronisme)
    private final String[] saiUstatut; // statut de l'US (Fait, MES, US simple par défaut)


    private List<String> collecComm = new ArrayList<>();

    public SynchronousGroupBuilder(@NotNull List<RecordingUnit> recordingUnits) {
        this.recordingUnits = recordingUnits;


        saiUstatut = new String[recordingUnits.size()];
        Arrays.fill(saiUstatut, "US");

    }

    public void findTransitiveAndReflexiveRelationships() {

        // Variables locales
        boolean signalModif = false;
        // coche keeps tracks of the visited cells of the synchronisms matrix / matrice signal de relation traitée (accération traitement)
        boolean[][] coche = new boolean[recordingUnits.size()][recordingUnits.size()];

        do {
            signalModif = false;
            for (int u = 0; u < recordingUnits.size(); u++) { // We are going to iterate among all the recording units
                if (enSynch[u] > 0) { // If the current US is part of a synchronous group
                    // We compare the current recordingUnits (u) with every other recordingUnits (u2) to find synchronisms
                    for (int u2 = 0; u2 < recordingUnits.size(); u2++) {

                        if (hasSynchronousRelationship(recordingUnits.get(u), recordingUnits.get(u2)) && u != u2 && !coche[u][u2]) {

                            coche[u][u2] = true;

                            // Déduction par symétrie
                            if (!hasSynchronousRelationship(recordingUnits.get(u2), recordingUnits.get(u))) { // MSyn(u2, u) != Synchro
                                // The following lines are the equivalent of "MSyn(u2, u) = Synchro;"
                                StratigraphicRelationship newRelationship = new StratigraphicRelationship();
                                newRelationship.setUnit1(recordingUnits.get(u2));
                                newRelationship.setUnit2(recordingUnits.get(u));
                                newRelationship.setType(StratigraphicRelationshipService.SYNCHRONOUS); // Assuming this is your synchronous concept
                                recordingUnits.get(u2).getRelationshipsAsUnit1().add(newRelationship);
                                //  End
                                signalModif = true;
                            }

                            // We iterate over the whole list again to find synchronisms by transitivity


                            // Déduction par transitivité
                            for (int u3 = 0; u3 < recordingUnits.size(); u3++) {
                                if ((hasSynchronousRelationship(recordingUnits.get(u2), recordingUnits.get(u3)) ||
                                        hasSynchronousRelationship(recordingUnits.get(u3), recordingUnits.get(u2))) &&
                                        !hasSynchronousRelationship(recordingUnits.get(u), recordingUnits.get(u3))) {
                                    //  The following lines are the equivalent of MSyn(u, u3) = Synchro;
                                    StratigraphicRelationship newRelationship = new StratigraphicRelationship();
                                    newRelationship.setUnit1(recordingUnits.get(u));
                                    newRelationship.setUnit2(recordingUnits.get(u3));
                                    newRelationship.setType(StratigraphicRelationshipService.SYNCHRONOUS); // Assuming this is your synchronous concept
                                    recordingUnits.get(u).getRelationshipsAsUnit1().add(newRelationship);
                                    //  End
                                }
                            }
                        }
                    }
                }
            }
        } while (signalModif);
    }

    // La méthode qui simule la logique de "EnsemblesSynchrones" en Java
    // constitution des ensembles synchrones certains
    // déduction des synchronismes

    public List<SynchronousGroup> build() {


        // Store the synchronous group master index
        long[] maitreES = new long[recordingUnits.size()];
        // The list of syncronous group to return
        List<SynchronousGroup> synchronousGroupList = new ArrayList<>();

        // Initialisation des tableaux
        Arrays.fill(maitreES, 0);
        enSynch = IntStream.range(0, recordingUnits.size()) // ensemble synchrone (ES) de l'US (O si pas en synchronisme)
                // Initialisation : each element get the value of its index because each US is in its on synchronous group
                .mapToLong(i -> i + 1) // Assigns index + 1 to each element
                .toArray();



        // Traitement des ensembles synchrones
        for (int u = 0; u < recordingUnits.size(); u++) {
            if (hasSynchronousRelationship(
                    recordingUnits.get(u),
                    recordingUnits.get(u)
            )) { // relation réflexive indiquant un ensemble synchrone

                SynchronousGroup newGroup = new SynchronousGroup();
                newGroup.addUnit(recordingUnits.get(u));
                newGroup.setMaster(recordingUnits.get(u));

                for (int u2 = 0; u2 < recordingUnits.size(); u2++) {
                    if (hasSynchronousRelationship(
                            recordingUnits.get(u),
                            recordingUnits.get(u2)
                    )) {

                        enSynch[u2] = u; // Indique la 1ère US de l'ensemble synchrone
                        newGroup.addUnit(recordingUnits.get(u2));

                        if (saiUstatut[u2].equals("maître d'ES")) {
                            // If we declared u2 as a group master and u is also a group mastr
                            // , we have a problem because there already is a group master
                            if (maitreES[u] == 0) {
                                newGroup.setMaster(recordingUnits.get(u2));
                            } else {
                                collecComm.add("Attention : plusieurs maîtres pour l'ensemble synchrone " + u);
                            }
                        }

                        // Remove relationship between u2 and itself so it cant be detected anymore as a group
                        if (hasSynchronousRelationship(
                                recordingUnits.get(u2),
                                recordingUnits.get(u2)
                        )) {
                            // Remove self-referential synchronous relationship
                            int finalU2 = u2;
                            recordingUnits.get(u2).getRelationshipsAsUnit1().removeIf(
                                    rel -> rel.getUnit2().equals(recordingUnits.get(finalU2)) && isSynchronous(rel)
                            );
                        }


                    }
                }

                // Add the group to the list of group
                synchronousGroupList.add(newGroup);
            }
        }

        // Add units that are not in any group
        for (RecordingUnit unit : recordingUnits) {
            boolean isInGroup = synchronousGroupList.stream()
                    .anyMatch(group -> group.contains(unit));

            if (!isInGroup) {
                SynchronousGroup singleUnitGroup = new SynchronousGroup();
                singleUnitGroup.addUnit(unit);
                singleUnitGroup.setMaster(unit); // The unit itself is its own master
                synchronousGroupList.add(singleUnitGroup);
            }
        }

        // Give all relationship of the units inside the group to the group itself
        return synchronousGroupList;
    }

    /**
     * Checks if unit1 as a synchronous relationship with unit2
     * (Equivalent of (MSyn(U, U2) == Synchro) in Stratifiant)
     *
     * @param unit1 The first RecordingUnit.
     * @param unit2 The second RecordingUnit.
     * @return true if they are in a synchronous relationship, false otherwise.
     */
    private boolean hasSynchronousRelationship(RecordingUnit unit1, RecordingUnit unit2) {
        return unit1.getRelationshipsAsUnit1().stream()
                .anyMatch(rel -> rel.getUnit2().equals(unit2) && isSynchronous(rel));
    }

    /**
     * Determines if a given relationship is synchronous.
     *
     * @param relationship The StratigraphicRelationship to check.
     * @return true if it represents a synchronous relationship.
     */
    private boolean isSynchronous(StratigraphicRelationship relationship) {

        return StratigraphicRelationshipService.SYNCHRONOUS.equals(relationship.getType());
    }

}
