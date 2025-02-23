package fr.siamois.utils.stratigraphy;

import fr.siamois.models.recordingunit.RecordingUnit;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class SynchronousGroup extends RecordingUnit {

    private final Set<RecordingUnit> units = new HashSet<>();
    private RecordingUnit master;

    public void addUnit(RecordingUnit unit) {
        units.add(unit);
        // transfer all the async relationships of the unit to the group
    }

    public void setMaster(RecordingUnit master) {
        if (units.contains(master)) {
            this.master = master;
        } else {
            throw new IllegalArgumentException("Master must be part of the synchronous group.");
        }
    }

    public boolean contains(RecordingUnit unit) {
        return units.contains(unit);
    }

}
