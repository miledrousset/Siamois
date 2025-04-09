package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
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
    private final transient ConceptService conceptService;
    private final SessionSettingsBean sessionSettingsBean;

    // locals
    private String spatialUnitListErrorMessage;
    private List<Concept> selectedCategories;
    private LazyDataModel<SpatialUnit> lazyDataModel ;

    public SpatialUnitListPanel(SpatialUnitService spatialUnitService, ConceptService conceptService, SessionSettingsBean sessionSettingsBean) {
        super("Unités géographiques", "bi bi-geo-alt", "siamois-panel spatial-unit-panel spatial-unit-list-panel");
        this.spatialUnitService = spatialUnitService;
        this.conceptService = conceptService;
        this.sessionSettingsBean = sessionSettingsBean;
    }

    public class SpatialUnitLazyDataModel extends LazyDataModel<SpatialUnit> {

        Integer first ;
        Integer pageSize ;


        @Override
        public int count(Map<String, FilterMeta> map) {
            return 0;
        }

        @Override
        public List<SpatialUnit> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {

            this.first = first; // Save to use in getters
            this.pageSize = pageSize;

            int pageNumber = first / pageSize;
            Pageable pageable = PageRequest.of(pageNumber, pageSize, buildSort(sortBy));

            String nameFilter = null;
            Long[] categoryIds = null; // sql server needs a Long[] to cast argument value (the list) into bigint[] for null check
            String globalFilter = null;

            if (filterBy != null) {
                FilterMeta nameMeta = filterBy.get("name");
                if (nameMeta != null && nameMeta.getFilterValue() != null) {
                    nameFilter = nameMeta.getFilterValue().toString();
                }

                FilterMeta categoryMeta = filterBy.get("category");
                if (categoryMeta != null && categoryMeta.getFilterValue() != null) {
                    selectedCategories = (List<Concept>) categoryMeta.getFilterValue();
                    categoryIds = selectedCategories.stream()
                            .filter(Objects::nonNull) // exclude null Concepts
                            .map(Concept::getId)
                            .filter(Objects::nonNull) // exclude null IDs
                            .toArray(Long[]::new);
                }

                FilterMeta globalMeta = filterBy.get("globalFilter");
                if (globalMeta != null && globalMeta.getFilterValue() != null) {
                    globalFilter = globalMeta.getFilterValue().toString();
                }
            }

            Page<SpatialUnit> result = spatialUnitService.findAllByInstitutionAndByNameContainingAndByCategoriesAndByGlobalContaining(
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

        public int getFirstIndexOnPage() {
            return first + 1; // Adding 1 because indexes are zero-based
        }

        public int getLastIndexOnPage() {
            int last = first + pageSize;
            int total = this.getRowCount();
            return Math.min(last, total); // Ensure it doesn’t exceed total records
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
            // Get all the spatial unit within the institution
            selectedCategories = new ArrayList<>();
            lazyDataModel = new SpatialUnitLazyDataModel();

        } catch (RuntimeException e) {
            spatialUnitListErrorMessage = "Failed to load spatial units: " + e.getMessage();
        }
    }

    public List<Concept> categoriesAvailable() {
        return conceptService.findAllConceptsByInstitution(sessionSettingsBean.getSelectedInstitution());


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





}
