package fr.siamois.utils.stratigraphy;

import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.recordingunit.StratigraphicRelationship;
import fr.siamois.services.recordingunit.StratigraphicRelationshipService;

import java.util.*;
import java.util.stream.IntStream;

public class SynchronousGroup {


    // Stratigraphic unit list and their relationship
    private final List<RecordingUnit> recordingUnits;
    private final Set<StratigraphicRelationship> stratigraphicRelationships;

    private long[] EnSynch; // ensemble synchrone (ES) de l'US (O si pas en synchronisme)
    private String[] SaiUstatut; // statut de l'US (Fait, MES, US simple par défaut)


    private boolean AlerteUmaitre = false;
    private List<String> CollecComm = new ArrayList<>();

    public SynchronousGroup(@org.jetbrains.annotations.NotNull List<RecordingUnit> recordingUnits, Set<StratigraphicRelationship> stratigraphicRelationships) {
        this.recordingUnits = recordingUnits;
        this.stratigraphicRelationships = stratigraphicRelationships;


        SaiUstatut = new String[recordingUnits.size()];

    }

    // La méthode qui simule la logique de "EnsemblesSynchrones" en Java
    // constitution des ensembles synchrones certains
    // déduction des synchronismes

    public void ensemblesSynchrones() {

        // Variables locales
        boolean signalModif = false;
        // Store the synchronous group master index
        long[] MaitreES = new long[recordingUnits.size()];

        // coche keeps tracks of the visited cells of the synchronisms matrix / matrice signal de relation traitée (accération traitement)
        boolean[][] coche = new boolean[recordingUnits.size()][recordingUnits.size()];

        // Initialisation des tableaux
        Arrays.fill(MaitreES, 0);
        EnSynch = IntStream.range(0, recordingUnits.size()) // ensemble synchrone (ES) de l'US (O si pas en synchronisme)
                // Initialisation : each element get the value of its index because each US is in its on synchronous group
                .asLongStream()
                .toArray();

        do {
            signalModif = false;
            for (int u = 0; u < recordingUnits.size(); u++) { // We are going to iterate among all the recording units
                if (EnSynch[u] > 0) { // If the current US is part of a synchronous group
                    // We compare the current recordingUnits (u) with every other recordingUnits (u2) to find synchronisms
                    for (int u2 = 0; u2 < recordingUnits.size(); u2++) {

                        if (hasSynchronousRelationship(recordingUnits.get(u), recordingUnits.get(u2)) && u != u2 && !coche[u][u2]) {

                            coche[u][u2] = true;

                            // Déduction par symétrie
                            if (!hasSynchronousRelationship(recordingUnits.get(u2), recordingUnits.get(u))) { // MSyn(u2, u) != Synchro
                                // --------- The following lines are the equivalent of MSyn(u2, u) = Synchro;
                                StratigraphicRelationship newRelationship = new StratigraphicRelationship();
                                newRelationship.setUnit1(recordingUnits.get(u2));
                                newRelationship.setUnit2(recordingUnits.get(u));
                                newRelationship.setType(StratigraphicRelationshipService.SYNCHRONOUS); // Assuming this is your synchronous concept
                                recordingUnits.get(u2).getRelationshipsAsUnit1().add(newRelationship);
                                // --------- End
                                signalModif = true;
                            }

                            // We iterate over the whole list again to find synchronisms by transitivity


                            // Déduction par transitivité
                            for (int u3 = 0; u3 < recordingUnits.size(); u3++) {
                                if( hasSynchronousRelationship(recordingUnits.get(u2), recordingUnits.get(u3)) ||
                                        hasSynchronousRelationship(recordingUnits.get(u3), recordingUnits.get(u2)) )   {
                                    if(!hasSynchronousRelationship(recordingUnits.get(u), recordingUnits.get(u3))) {
                                        // --------- The following lines are the equivalent of MSyn(u, u3) = Synchro;
                                        StratigraphicRelationship newRelationship = new StratigraphicRelationship();
                                        newRelationship.setUnit1(recordingUnits.get(u));
                                        newRelationship.setUnit2(recordingUnits.get(u3));
                                        newRelationship.setType(StratigraphicRelationshipService.SYNCHRONOUS); // Assuming this is your synchronous concept
                                        recordingUnits.get(u).getRelationshipsAsUnit1().add(newRelationship);
                                        // --------- End
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } while (signalModif);

        // Traitement des ensembles synchrones
        for (int u = 0; u < recordingUnits.size(); u++) {
            if (hasSynchronousRelationship(
                    recordingUnits.get(u),
                    recordingUnits.get(u)
            )) { // relation réflexive indiquant un ensemble synchrone
                for (int u2 = 0; u2 < recordingUnits.size(); u2++) {
                    if (hasSynchronousRelationship(
                            recordingUnits.get(u),
                            recordingUnits.get(u2)
                    )) {
                        EnSynch[u] = u; // Indique la 1ère US de l'ensemble synchrone
                        if (SaiUstatut[u2].equals("maître d'ES")) {
                            if (MaitreES[u] == 0) {
                                MaitreES[u] = u;
                            } else {
                                // Ajouter l'alerte
                                AlerteUmaitre = true;
                                CollecComm.add("Attention : plusieurs maîtres pour l'ensemble synchrone " + u);
                            }
                        }
                    }
                }
            }
        }
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

        if (relationship.getId() == null) {
            throw new IllegalArgumentException("Stratigraphic relationship must have an ID");
        }

        return StratigraphicRelationshipService.SYNCHRONOUS.getId().equals(relationship.getType().getId());
    }

}
