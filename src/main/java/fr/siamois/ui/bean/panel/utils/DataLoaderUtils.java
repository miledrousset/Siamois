package fr.siamois.ui.bean.panel.utils;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class DataLoaderUtils {

    private DataLoaderUtils() {}
    public static <T> void loadData(Supplier<List<T>> supplier, Consumer<List<T>> setter, Consumer<String> errorSetter, String errorMessage) {
        try {
            errorSetter.accept(null);
            setter.accept(supplier.get());
        } catch (RuntimeException e) {
            setter.accept(null);
            errorSetter.accept(errorMessage + e.getMessage());
        }
    }
}
