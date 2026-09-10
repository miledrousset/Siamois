package fr.siamois.ui.bean.panel;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.dto.entity.SearchResultDTO;
import fr.siamois.infrastructure.database.repositories.misc.SearchRepository;
import fr.siamois.ui.bean.FocusViewBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
@Getter
@Setter
public class SearchBean implements Serializable {

    private final SessionSettingsBean sessionSettingsBean;
    private final transient SearchRepository searchRepository;
    private final FlowBean flowBean;

    @Nullable
    private SearchResultDTO selected;

    private UserInfo userInfo;

    private transient List<ThemeMode> themeModes;

    @PostConstruct
    public void init() {
        userInfo = sessionSettingsBean.getUserInfo();
        themeModes = List.of(
                new ThemeMode("filemaker", "filemaker-mode",
                        sessionSettingsBean::isFilemakerMode, sessionSettingsBean::setFilemakerMode, null),
                new ThemeMode("excel", "excel-mode",
                        sessionSettingsBean::isExcelMode, sessionSettingsBean::setExcelMode, null),
                new ThemeMode("goofy", "goofy-mode",
                        sessionSettingsBean::isGoofyMode, sessionSettingsBean::setGoofyMode, null),
                new ThemeMode("matrix", "matrix-mode",
                        sessionSettingsBean::isMatrixMode, sessionSettingsBean::setMatrixMode, "siaMatrixRain"),
                new ThemeMode("noel", "noel-mode",
                        sessionSettingsBean::isNoelMode, sessionSettingsBean::setNoelMode, "siaSnow"),
                new ThemeMode("bobleponge", "bob-mode",
                        sessionSettingsBean::isBobMode, sessionSettingsBean::setBobMode, "siaBubbles")
        );
    }

    private static final String SNAKE_EASTER_EGG_KEYWORD = "motherlode";

    private record ThemeMode(String keyword, String cssClass, Supplier<Boolean> getter,
                              Consumer<Boolean> setter, String jsEffect) {
    }

    public List<SearchResultDTO> completeText(String input) {
        if (input != null && SNAKE_EASTER_EGG_KEYWORD.equalsIgnoreCase(input.trim())) {
            PrimeFaces.current().ajax().update("snakeGameForm");
            PrimeFaces.current().executeScript("PF('snakeGameDiag').show()");
            return List.of();
        }
        if (input != null) {
            String trimmed = input.trim();
            for (ThemeMode mode : themeModes) {
                if (mode.keyword().equalsIgnoreCase(trimmed)) {
                    applyThemeMode(mode);
                    return List.of();
                }
            }
        }
        return searchRepository.findResultsFor(input,
                sessionSettingsBean.getSelectedInstitution(),
                userInfo.getUser());
    }

    private void applyThemeMode(ThemeMode mode) {
        boolean enabled = !mode.getter().get();
        for (ThemeMode m : themeModes) {
            m.setter().accept(m == mode && enabled);
        }

        StringBuilder script = new StringBuilder();
        for (ThemeMode m : themeModes) {
            boolean active = m == mode && enabled;
            script.append("document.body.classList.toggle('").append(m.cssClass()).append("', ").append(active).append(");");
        }
        for (ThemeMode m : themeModes) {
            if (m.jsEffect() == null) {
                continue;
            }
            script.append("if (window.").append(m.jsEffect()).append(") {")
                    .append("if (document.body.classList.contains('").append(m.cssClass()).append("')) { window.").append(m.jsEffect()).append(".start(); }")
                    .append("else { window.").append(m.jsEffect()).append(".stop(); }")
                    .append("}");
        }
        PrimeFaces.current().executeScript(script.toString());
    }

    public void onResultSelect(AjaxBehaviorEvent event) {
        if (selected == null) return;
        FacesContext ctx = FacesContext.getCurrentInstance();
        FocusViewBean focusViewBean = ctx.getApplication()
                .evaluateExpressionGet(ctx, "#{focusViewBean}", FocusViewBean.class);
        if (focusViewBean == null || focusViewBean.getMainPanel() == null) return;

        var panel = focusViewBean.getMainPanel();
        if (selected.getRecordingUnitId() != null) {
            flowBean.addRecordingUnitToOverview(selected.getRecordingUnitId(), panel, null);
        } else if (selected.getSpatialUnitId() != null) {
            flowBean.addSpatialUnitToOverview(selected.getSpatialUnitId(), panel, null);
        } else if (selected.getActionUnitId() != null) {
            flowBean.addActionUnitToOverview(selected.getActionUnitId(), panel, null);
        } else if (selected.getSpecimenId() != null) {
            flowBean.addSpecimenToOverview(selected.getSpecimenId(), panel, null);
        }
    }

}
