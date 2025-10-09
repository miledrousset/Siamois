package fr.siamois.ui.bean.panel.models.panel.single;

import fr.siamois.ui.bean.dialog.document.DocumentCreationBean;

import java.io.Serializable;

public abstract class AbstractSingleMultiHierarchicalEntityPanel<T, H> extends AbstractSingleEntityPanel<T, H>  implements Serializable {

    protected AbstractSingleMultiHierarchicalEntityPanel(String titleCodeOrTitle, String icon, String panelClass, DocumentCreationBean documentCreationBean,
                                        AbstractSingleEntity.Deps deps) {
        super(titleCodeOrTitle, icon, panelClass, documentCreationBean, deps);
    }

}


