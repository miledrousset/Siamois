package fr.siamois.services;

/**
 * <p>Interface to implement for classes that want to be notified when the team changes</p>
 *
 * @author  Julien Linget
 */
public interface TeamTopicSubscriber {

    /**
     * Method called when the team changes
     */
    void onTeamChange();
}
