package fr.siamois.services;

/**
 * <p>Subscriber of a signal</p>
 *
 * @author  Julien Linget
 */
public interface Subscriber {

    /**
     * Method called when the team changes
     */
    void onSignal(String signal);
}
