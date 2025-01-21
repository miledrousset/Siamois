package fr.siamois.bean;

import fr.siamois.services.Subscriber;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@SessionScoped
public class ObserverBean {

    private final Map<String, List<Subscriber>> subscriptions = new HashMap<>();

    public void subscribeToSignal(Subscriber subscriber, String signal) {
        subscriptions.putIfAbsent(signal, new ArrayList<>());
        subscriptions.get(signal).add(subscriber);
    }

    public void notify(String signal) {
        for (Subscriber subscriber : subscriptions.getOrDefault(signal, List.of())) {
            subscriber.onSignal(signal);
        }
    }

}
