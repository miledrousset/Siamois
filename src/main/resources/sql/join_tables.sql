CREATE TABLE spatial_hierarchy
(
    fk_parent_id BIGINT REFERENCES spatial_unit(spatial_unit_id),
    fk_child_id BIGINT REFERENCES spatial_unit(spatial_unit_id),

    PRIMARY KEY (fk_parent_id, fk_child_id)
);

CREATE TABLE spatial_unit_document
(

    fk_document_id BIGINT REFERENCES siamois_document(document_id),
    fk_spatial_unit_id BIGINT REFERENCES spatial_unit(spatial_unit_id),

    PRIMARY KEY (fk_spatial_unit_id, fk_document_id)
);

CREATE TABLE action_action_code
(
    fk_action_id BIGINT REFERENCES action_unit(action_unit_id),
    fk_action_code_id VARCHAR REFERENCES action_code(action_code_id),

    PRIMARY KEY (fk_action_id, fk_action_code_id)
);

CREATE TABLE action_hierarchy
(
    fk_parent_id BIGINT REFERENCES action_unit(action_unit_id),
    fk_child_id BIGINT REFERENCES action_unit(action_unit_id),

    PRIMARY KEY (fk_parent_id, fk_child_id)
);

CREATE TABLE action_unit_document
(
    fk_document_id BIGINT REFERENCES siamois_document(document_id),
    fk_action_unit_id BIGINT REFERENCES action_unit(action_unit_id),

    PRIMARY KEY (fk_document_id, fk_action_unit_id)
);

CREATE TABLE recording_unit_document
(
    fk_recording_unit_id BIGINT REFERENCES recording_unit(recording_unit_id),
    fk_document_id BIGINT REFERENCES siamois_document(document_id),

    PRIMARY KEY (fk_recording_unit_id, fk_document_id)
);

CREATE TABLE recording_unit_hierarchy
(
    fk_parent_id BIGINT REFERENCES recording_unit(recording_unit_id),
    fk_child_id BIGINT REFERENCES recording_unit(recording_unit_id),

    PRIMARY KEY (fk_parent_id, fk_child_id)
);

CREATE TABLE ru_study_document
(
    fk_document_id BIGINT REFERENCES siamois_document(document_id),
    fk_ru_study_id BIGINT REFERENCES recording_unit_study(recording_unit_study_id),

    PRIMARY KEY (fk_document_id, fk_ru_study_id)
);

CREATE TABLE specimen_document
(
    fk_document_id BIGINT REFERENCES siamois_document(document_id),
    fk_specimen_id BIGINT REFERENCES specimen(specimen_id),

    PRIMARY KEY (fk_document_id, fk_specimen_id)
);

CREATE TABLE specimen_study_document
(
    fk_document_id BIGINT REFERENCES siamois_document(document_id),
    fk_specimen_study_id BIGINT REFERENCES specimen_study(specimen_study_id),

    PRIMARY KEY (fk_document_id, fk_specimen_study_id)
);

CREATE TABLE specimen_group_attribution
(
    fk_specimen_id BIGINT REFERENCES specimen(specimen_id),
    fk_specimen_group_id BIGINT REFERENCES specimen_group(specimen_group_id),

    PRIMARY KEY (fk_specimen_id, fk_specimen_group_id)
);
