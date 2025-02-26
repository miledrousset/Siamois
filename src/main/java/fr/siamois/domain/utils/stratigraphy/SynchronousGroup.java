package fr.siamois.domain.utils.stratigraphy;

import fr.siamois.domain.models.recordingunit.RecordingUnit;
import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
public class SynchronousGroup extends RecordingUnit {

    private final Set<RecordingUnit> units = new HashSet<>();
    private RecordingUnit master;

    public void addUnit(RecordingUnit unit) {
        units.add(unit);
        // transfer all the async relationships of the unit to the group ?
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;  // Same reference
        if (obj == null || getClass() != obj.getClass()) return false;  // Different types

        SynchronousGroup that = (SynchronousGroup) obj;
        return Objects.equals(this.getId(), that.getId());  // Compare IDs (null-safe)
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());  // Generate hash based on ID
    }

}
