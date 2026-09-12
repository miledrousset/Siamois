package fr.siamois.ui.config;

import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Chronomètre chaque phase du cycle de vie Faces et journalise le détail en fin de requête.
 *
 * <p>Sert à répondre à une question qu'on ne peut pas trancher en lisant le code : combien coûte
 * réellement la reconstruction de l'arbre de composants quand plusieurs panneaux sont ouverts.
 * Restore View est la phase qui porte ce coût — comparer sa durée à un panneau puis à six
 * indique si l'architecture du flux doit évoluer.</p>
 *
 * <p>Entièrement inactif tant que le logger n'est pas en {@code DEBUG} :</p>
 * <pre>logging.level.fr.siamois.ui.config.LifecycleTimingListener: DEBUG</pre>
 *
 * <p>Enregistré dans {@code META-INF/faces-config.xml} : Faces instancie lui-même les
 * {@code PhaseListener}, ce n'est donc pas un bean Spring.</p>
 */
public class LifecycleTimingListener implements PhaseListener, Serializable {

    private static final Logger log = LoggerFactory.getLogger(LifecycleTimingListener.class);

    private static final String TIMINGS_KEY = LifecycleTimingListener.class.getName() + ".timings";
    private static final String PHASE_START_KEY = LifecycleTimingListener.class.getName() + ".phaseStart";

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        if (!log.isDebugEnabled()) {
            return;
        }
        event.getFacesContext().getAttributes().put(PHASE_START_KEY, System.nanoTime());
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        if (!log.isDebugEnabled()) {
            return;
        }

        FacesContext context = event.getFacesContext();
        Object start = context.getAttributes().get(PHASE_START_KEY);
        if (!(start instanceof Long startedAt)) {
            return;
        }

        long elapsedMs = (System.nanoTime() - startedAt) / 1_000_000L;
        timings(context).put(event.getPhaseId(), elapsedMs);

        // Render Response ferme la requête, qu'elle soit complète ou partielle.
        if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
            logSummary(context);
        }
    }

    private void logSummary(FacesContext context) {
        Map<PhaseId, Long> timings = timings(context);
        long total = timings.values().stream().mapToLong(Long::longValue).sum();

        StringBuilder detail = new StringBuilder();
        timings.forEach((phase, elapsed) -> detail.append(phase.getName()).append('=').append(elapsed).append("ms "));

        log.debug("⏱ Cycle de vie [{}] {}ajax — total {} ms — {}",
                context.getViewRoot() != null ? context.getViewRoot().getViewId() : "?",
                context.getPartialViewContext().isAjaxRequest() ? "" : "non-",
                total,
                detail.toString().trim());
    }

    @SuppressWarnings("unchecked")
    private static Map<PhaseId, Long> timings(FacesContext context) {
        // PhaseId est une classe, pas une enum : LinkedHashMap conserve l'ordre des phases.
        return (Map<PhaseId, Long>) context.getAttributes()
                .computeIfAbsent(TIMINGS_KEY, key -> new LinkedHashMap<PhaseId, Long>());
    }
}
