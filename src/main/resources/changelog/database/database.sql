CREATE TABLE concept -- TODO : properly define this table which is just a placeholder for opentheso/opentypo relationships now.
(
    concept_id  INT GENERATED BY DEFAULT AS IDENTITY,

    PRIMARY KEY (concept_id)
);

CREATE TABLE document
(
    document_id                 INT GENERATED BY DEFAULT AS IDENTITY,
    title VARCHAR(255), -- might be useful
    fk_nature INT,
    fk_scale INT,
    -- author TODO : tbd
    fk_format INT,
    fk_parent INT,
    --localisation_document       VARCHAR,TODO : tbd
    --metadata_document           TEXT,TODO : tbd
    --stockage_document           VARCHAR,TODO : tbd
    --license_document            TEXT,TODO : tbd
    --statut_document             VARCHAR,TODO : tbd

    PRIMARY KEY (document_id),
    FOREIGN KEY (fk_scale) REFERENCES concept (concept_id),
    FOREIGN KEY (fk_nature) REFERENCES concept (concept_id),
    FOREIGN KEY (fk_format) REFERENCES concept (concept_id),
    FOREIGN KEY (fk_parent) REFERENCES document (document_id)
);

CREATE TABLE recording_unit
(
    recording_unit_id   INT GENERATED BY DEFAULT AS IDENTITY, -- TODO: Think about ARK, should we use ARK as id?
    fk_type INT,
    begin_date          TIMESTAMP,
    end_date            TIMESTAMP,
    -- geospatial_extent   , -- TODO : Lien avec le SIG

    PRIMARY KEY (recording_unit_id),
    FOREIGN KEY (fk_type) REFERENCES concept (concept_id)
);

CREATE TABLE recording_unit_media
(
    fk_media_id       INT,
    fk_recording_unit_id INT,

    PRIMARY KEY (fk_media_id, fk_recording_unit_id),
    FOREIGN KEY (fk_media_id) REFERENCES document (document_id),
    FOREIGN KEY (fk_recording_unit_id) REFERENCES recording_unit (recording_unit_id)
);

CREATE TABLE recording_unit_group
(
    fk_parent_id INT,
    fk_child_id INT,

    PRIMARY KEY (fk_parent_id, fk_child_id),
    FOREIGN KEY (fk_parent_id) REFERENCES recording_unit (recording_unit_id),
    FOREIGN KEY (fk_child_id) REFERENCES recording_unit (recording_unit_id)
);


CREATE TABLE stratigraphic_relationship
(
    fk_recording_unit_1_id         INT,
    fk_recording_unit_2_id         INT,
    fk_relationship_concept_id  INT,

    -- TODO : constraint about the RU ids ? if relationship RU1 -> RU2 exists, don't allow for RU2 -> RU1 ?
    PRIMARY KEY (fk_recording_unit_1_id, fk_recording_unit_2_id),
    FOREIGN KEY (fk_recording_unit_1_id) REFERENCES recording_unit (recording_unit_id),
    FOREIGN KEY (fk_recording_unit_2_id) REFERENCES recording_unit (recording_unit_id),
    FOREIGN KEY (fk_relationship_concept_id) REFERENCES concept (concept_id)
);

CREATE TABLE recording_unit_study
(
    recording_unit_study_id     INT GENERATED BY DEFAULT AS IDENTITY,
    author                      INT, -- TODO : link to author or user
    study_date                  TIMESTAMP,
    fk_method        INT,

    PRIMARY KEY (recording_unit_study_id),
    FOREIGN KEY (fk_method) REFERENCES concept (concept_id)
);

CREATE TABLE ru_study_document
(
    fk_document_id       INT,
    fk_ru_study_id INT,

    PRIMARY KEY (fk_document_id, fk_ru_study_id),
    FOREIGN KEY (fk_document_id) REFERENCES document (document_id),
    FOREIGN KEY (fk_ru_study_id) REFERENCES recording_unit_study (recording_unit_study_id)
);

CREATE TABLE ru_study_typology
(
    fk_ru_study_id              INT,
    fk_typologie_concept_id     INT,

    PRIMARY KEY (fk_ru_study_id, fk_typologie_concept_id),
    FOREIGN KEY (fk_typologie_concept_id) REFERENCES concept (concept_id),
    FOREIGN KEY (fk_ru_study_id) REFERENCES recording_unit_study (recording_unit_study_id)
);

CREATE TABLE specimen
(
    specimen_id                          INT GENERATED BY DEFAULT AS IDENTITY,
    fk_recording_unit_id                       INT,
    -- todo : morphometrie INT ?
    fk_specimen_category INT,
    -- todo : traitement INT
    -- todo : clarify relationship between specimen and specimec study. On Miro: N to N. Why?
    fk_collection_method INT, -- todo : faut il deux champs? il est ecrit COMMENT et POURQUOI dans le miro
    collection_date TIMESTAMP, -- quand a il été prelevé
    -- location INT -- ou a t'il été prelévé todo :GIS
    -- storage -- todo : c'est quoi stockage prelevement??

    PRIMARY KEY (specimen_id),
    FOREIGN KEY (fk_recording_unit_id) REFERENCES recording_unit (recording_unit_id),
    FOREIGN KEY (fk_specimen_category) REFERENCES concept (concept_id),
    FOREIGN KEY (fk_collection_method) REFERENCES concept (concept_id)
);

CREATE TABLE specimen_document
(
    fk_document_id       INT,
    fk_specimen_id INT,

    PRIMARY KEY (fk_document_id, fk_specimen_id),
    FOREIGN KEY (fk_document_id) REFERENCES document (document_id),
    FOREIGN KEY (fk_specimen_id) REFERENCES specimen (specimen_id)
);

CREATE TABLE specimen_movement
(
    movement_id              INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    fk_specimen_id           INT NOT NULL,
    departure_date           TIMESTAMP NOT NULL,
    return_date           TIMESTAMP NOT NULL,
    origin_location     VARCHAR(255), -- todo : define
    destination_location     VARCHAR(255), -- todo : define
    movement_reason          VARCHAR(255), -- todo : useful?
    handled_by               VARCHAR(255), -- todo : useful?
    notes                   TEXT, -- todo : useful?

    FOREIGN KEY (fk_specimen_id) REFERENCES specimen(specimen_id)
);

CREATE TABLE specimen_study
(
    specimen_study_id        INT GENERATED BY DEFAULT AS IDENTITY,
    --author   INT, -- TODO : Lien avec auteur
    study_date      TIMESTAMP,
    fk_method   INT,

    PRIMARY KEY (specimen_study_id),
    FOREIGN KEY (fk_method) REFERENCES concept (concept_id)
);

CREATE TABLE specimen_study_document
(
    fk_document_id       INT,
    fk_specimen_study_id INT,

    PRIMARY KEY (fk_document_id, fk_specimen_study_id),
    FOREIGN KEY (fk_document_id) REFERENCES document (document_id),
    FOREIGN KEY (fk_specimen_study_id) REFERENCES specimen_study (specimen_study_id)
);

CREATE TABLE specimen_study_typology
(
    fk_specimen_study_id              INT,
    fk_typologie_concept_id     INT,

    PRIMARY KEY (fk_specimen_study_id , fk_typologie_concept_id),
    FOREIGN KEY (fk_typologie_concept_id) REFERENCES concept (concept_id),
    FOREIGN KEY (fk_specimen_study_id) REFERENCES specimen_study (specimen_study_id)
);