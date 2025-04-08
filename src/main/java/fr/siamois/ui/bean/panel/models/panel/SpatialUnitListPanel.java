package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import jakarta.faces.model.SelectItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.menu.DefaultMenuItem;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;


import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SpatialUnitListPanel extends AbstractPanel {

    private final transient SpatialUnitService spatialUnitService;
    private final SessionSettingsBean sessionSettingsBean;

    // locals
    private List<SpatialUnit> filteredSpatialUnits = new ArrayList<>();
    private List<SpatialUnit> spatialUnitList;
    private String spatialUnitListErrorMessage;

    private LazyDataModel<SpatialUnit> lazyDataModel ;

    public SpatialUnitListPanel(SpatialUnitService spatialUnitService, SessionSettingsBean sessionSettingsBean) {
        super("Unités géographiques", "bi bi-geo-alt", "siamois-panel spatial-unit-panel spatial-unit-list-panel");
        this.spatialUnitService = spatialUnitService;
        this.sessionSettingsBean = sessionSettingsBean;
    }

    public class SpatialUnitLazyDataModel extends LazyDataModel<SpatialUnit> {
        @Override
        public int count(Map<String, FilterMeta> map) {
            return 0;
        }

        @Override
        public List<SpatialUnit> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
            int pageNumber = first / pageSize;
            Pageable pageable = PageRequest.of(pageNumber, pageSize, buildSort(sortBy));

            String nameFilter = null;
            List<Long> categoryIds = null;
            String globalFilter = null;

            if (filterBy != null) {
                FilterMeta nameMeta = filterBy.get("name");
                if (nameMeta != null && nameMeta.getFilterValue() != null) {
                    nameFilter = nameMeta.getFilterValue().toString();
                }

                FilterMeta categoryMeta = filterBy.get("category");
                if (categoryMeta != null && categoryMeta.getFilterValue() != null) {
                    categoryIds = Arrays.stream((Object[]) categoryMeta.getFilterValue())
                            .map(o -> ((Concept) o).getId())
                            .filter(Objects::nonNull) // Ne garder que les IDs non null
                            .toList();
                }

                FilterMeta globalMeta = filterBy.get("globalFilter");
                if (globalMeta != null && globalMeta.getFilterValue() != null) {
                    globalFilter = globalMeta.getFilterValue().toString();
                }
            }

            Page<SpatialUnit> result = spatialUnitService.findFiltered(
                    sessionSettingsBean.getSelectedInstitution().getId(),
                    nameFilter, categoryIds, globalFilter,
                    pageable);

            setRowCount((int) result.getTotalElements());
            return result.getContent();
        }

        private Sort buildSort(Map<String, SortMeta> sortBy) {
            if (sortBy == null || sortBy.isEmpty()) {
                return Sort.unsorted();
            }

            List<Sort.Order> orders = new ArrayList<>();
            for (Map.Entry<String, SortMeta> entry : sortBy.entrySet()) {
                String field = entry.getKey();
                if(Objects.equals(field, "category.label")) {
                    field = "c_label";
                }
                SortMeta meta = entry.getValue();
                Sort.Order order = new Sort.Order(meta.getOrder() == SortOrder.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC, field);
                orders.add(order);
            }

            return Sort.by(orders);
        }
    }

    public void init()  {
        try {
            // Add current item to breadcrumb
            DefaultMenuItem item = DefaultMenuItem.builder()
                    .value("Unités géographiques")
                    .icon("bi bi-geo-alt")
                    .build();
            this.getBreadcrumb().getModel().getElements().add(item);
            // Get all the spatial unit within the institution that don't have a parent

            lazyDataModel = new SpatialUnitLazyDataModel();
            spatialUnitList = spatialUnitService.findAllWithoutParentsOfInstitution(sessionSettingsBean.getSelectedInstitution());
        } catch (RuntimeException e) {
            spatialUnitList = null;
            spatialUnitListErrorMessage = "Failed to load spatial units: " + e.getMessage();
        }
    }


    @Override
    public String display() {
        return "/panel/spatialUnitListPanel.xhtml";
    }

    public static class SpatialUnitListPanelBuilder {

        private final SpatialUnitListPanel spatialUnitListPanel;

        public SpatialUnitListPanelBuilder(ObjectProvider<SpatialUnitListPanel> spatialUnitListPanelProvider) {
            this.spatialUnitListPanel = spatialUnitListPanelProvider.getObject();
        }

        public SpatialUnitListPanel.SpatialUnitListPanelBuilder breadcrumb(PanelBreadcrumb breadcrumb) {
            spatialUnitListPanel.setBreadcrumb(breadcrumb);

            return this;
        }

        public SpatialUnitListPanel build() {
            spatialUnitListPanel.init();
            return spatialUnitListPanel;
        }
    }

    public List<SelectItem> getCategoriesSelectItems() {
        Concept c = new Concept();
        c.setId(14L);
        return List.of(
                new SelectItem(c, "Testing"),
                new SelectItem(new Concept(), "Another Category"),
                new SelectItem(new Concept(), "Demo")
        );
    }



}
