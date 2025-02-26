package fr.siamois.domain.utils;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;

public class HistoryCopyUtils {

    private HistoryCopyUtils() {}

    /**
     * Copy attributes from history to target. The ID field of the target is set to the tableId of the history.
     * @param history The history object
     * @param target The target object
     * @param <A> The type of the history object
     * @param <B> The type of the target object
     */
    public static <A, B> void copyAttributesFromHistToTarget(A history, B target) {
        BeanUtils.copyProperties(history, target);
        try {
            Class<?> targetClass = target.getClass();
            Field id = targetClass.getDeclaredField("id");
            id.setAccessible(true);
            Field tableId = history.getClass().getDeclaredField("tableId");
            tableId.setAccessible(true);
            id.set(target, tableId.get(history));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to copy attributes", e);
        }
    }

}
