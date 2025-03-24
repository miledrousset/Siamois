package fr.siamois.infrastructure.database;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

/**
 * Database initializer are services that are executed after the application startup.
 * They should consider having an {@link org.springframework.core.annotation.Order} annotation to specify when to execute.
 * If the application can't start without the initialized data. {@link DatabaseInitializer#exitApplication(int)} should be called.
 */
public interface DatabaseInitializer {
    ApplicationContext getApplicationContext();

    /**
     * Initialize the data mandatory for application's logic.
     */
    void initialize();

    /**
     * Exit the application with the given exit code.
     * @param exitCode The exit code of the application
     */
    default void exitApplication(int exitCode) {
        int finalExistCode = SpringApplication.exit(getApplicationContext(), () -> exitCode);
        System.exit(finalExistCode);
    }
}
