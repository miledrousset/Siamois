CREATE TABLE unite_enregistrement
(
    ue_id                  SERIAL,
    concept_id_type_ue     VARCHAR,
    date_enregistrement_ue TIMESTAMP,
    emprise_ue             INT, -- TODO : Lien avec le SIG
    PRIMARY KEY (ue_id)
);

CREATE TABLE lien_stratigraphique
(
    nom_lien_stratigraphique VARCHAR(50),

    PRIMARY KEY (nom_lien_stratigraphique)
);

CREATE TABLE vocabulaire_type
(
    vocabulaire_type_id    SERIAL,
    vocabulaire_type_label VARCHAR,

    PRIMARY KEY (vocabulaire_type_id)
);

CREATE TABLE vocabulaire
(
    vocabulaire_id      VARCHAR(256),
    type_id_vocabulaire INT NOT NULL,
    nom_vocabulaire     VARCHAR,

    PRIMARY KEY (vocabulaire_id),
    FOREIGN KEY (type_id_vocabulaire) REFERENCES vocabulaire_type (vocabulaire_type_id)
);

CREATE TABLE concept
(
    concept_id     VARCHAR(256),
    vocabulaire_id VARCHAR(256)          NOT NULL,
    concept_label  VARCHAR(256) NOT NULL,

    PRIMARY KEY (concept_id),
    FOREIGN KEY (vocabulaire_id) REFERENCES vocabulaire (vocabulaire_id)
);

CREATE TABLE stratigraphie
(
    ue_id_1            INT,
    ue_id_2            INT,
    lien_stratigraphie VARCHAR(50),

    PRIMARY KEY (ue_id_1, ue_id_2),
    FOREIGN KEY (ue_id_1) REFERENCES unite_enregistrement (ue_id),
    FOREIGN KEY (ue_id_2) REFERENCES unite_enregistrement (ue_id),
    FOREIGN KEY (lien_stratigraphie) REFERENCES lien_stratigraphique (nom_lien_stratigraphique)
);

CREATE TABLE document
(
    document_id SERIAL,
    -- TODO : Table document

    PRIMARY KEY (document_id)
);

CREATE TABLE media_ue
(
    ue_id       INT,
    document_id INT,

    PRIMARY KEY (ue_id, document_id),
    FOREIGN KEY (ue_id) REFERENCES unite_enregistrement (ue_id),
    FOREIGN KEY (document_id) REFERENCES document (document_id)
);

CREATE TABLE hierarchie_ue
(
    parent_id INT,
    enfant_id INT,

    PRIMARY KEY (parent_id, enfant_id),
    FOREIGN KEY (parent_id) REFERENCES unite_enregistrement (ue_id),
    FOREIGN KEY (enfant_id) REFERENCES unite_enregistrement (ue_id)
);

CREATE TABLE etude_ue
(
    etude_ue_id                 SERIAL,
    auteur_etude_ue             INT,     -- TODO : Lien vers auteur
    date_etude_ue               TIMESTAMP,
    concept_id_methode_etude_ue VARCHAR,
    date_ouverture_ue           TIMESTAMP,
    date_fermeture_ue           TIMESTAMP,

    PRIMARY KEY (etude_ue_id)
);

CREATE TABLE document_etude_ue
(
    etude_ue_id INT,
    document_id INT,

    PRIMARY KEY (etude_ue_id, document_id),
    FOREIGN KEY (etude_ue_id) REFERENCES etude_ue (etude_ue_id),
    FOREIGN KEY (document_id) REFERENCES document (document_id)
);

CREATE TABLE interpretation_ue
(
    ue_id    INT,
    etude_id INT,

    PRIMARY KEY (ue_id, etude_id),
    FOREIGN KEY (ue_id) REFERENCES unite_enregistrement (ue_id),
    FOREIGN KEY (etude_id) REFERENCES etude_ue (etude_ue_id)
);

CREATE TABLE mouvement_prelevement
(
    mouvement_prelevement_id          SERIAL,
    date_sortie_mouvement_prelevement TIMESTAMP,
    date_retour_mouvement_prelevement TIMESTAMP,
    lieu_mouvement_prelevement        VARCHAR,

    PRIMARY KEY (mouvement_prelevement_id)
);

CREATE TABLE prelevement
(
    prelevement_id                          SERIAL,
    ue_id_prelevement                       INT,
    morphometrie_prelevement                INT, -- TODO : FK (???)
    concept_id_categorie_prelevement        VARCHAR,
    traitement_prelevement                  INT, -- TODO : Lien avec Opération traitement
    concept_id_methode_collecte_prelevement VARCHAR,
    date_collecte_prelevement               TIMESTAMP,
    localisation_collecte_prelevement       INT, -- TODO : Lien avec le SIG (?)
    stockage_prelevement                    VARCHAR,
    mouvement_prelevement_id                INT,
    concept_id_typologie_prelevement        VARCHAR,

    PRIMARY KEY (prelevement_id),
    FOREIGN KEY (ue_id_prelevement) REFERENCES unite_enregistrement (ue_id),
    FOREIGN KEY (mouvement_prelevement_id) REFERENCES mouvement_prelevement (mouvement_prelevement_id)
);

CREATE TABLE document_prelevement
(
    prelevement_id INT,
    document_id    INT,

    PRIMARY KEY (prelevement_id, document_id),
    FOREIGN KEY (prelevement_id) REFERENCES prelevement (prelevement_id),
    FOREIGN KEY (document_id) REFERENCES document (document_id)
);

CREATE TABLE etude_prelevement
(
    etude_prelevement_id        SERIAL,
    auteur_etude_prelevement    INT, -- TODO : Lien avec auteur
    date_etude_prelevement      TIMESTAMP,
    methode_etude_prelevement   INT, -- TODO : Lien avec Thésaurus
    typologie_etude_prelevement INT, -- TODO : Lien avec Thésaurus


    PRIMARY KEY (etude_prelevement_id)
);

CREATE TABLE lien_prelevment_etude
(
    prelevement_id       INT,
    etude_prelevement_id INT,

    PRIMARY KEY (prelevement_id, etude_prelevement_id),
    FOREIGN KEY (prelevement_id) REFERENCES prelevement (prelevement_id),
    FOREIGN KEY (etude_prelevement_id) REFERENCES etude_prelevement (etude_prelevement_id)
);

CREATE TABLE document_etude_prelevement
(
    etude_prelevement_id INT,
    document_id          INT,

    PRIMARY KEY (etude_prelevement_id, document_id),
    FOREIGN KEY (etude_prelevement_id) REFERENCES etude_prelevement (etude_prelevement_id),
    FOREIGN KEY (document_id) REFERENCES document (document_id)
);

ALTER TABLE unite_enregistrement
    ADD FOREIGN KEY (concept_id_type_ue) REFERENCES concept (concept_id);

ALTER TABLE etude_ue
    ADD FOREIGN KEY (concept_id_methode_etude_ue) REFERENCES concept (concept_id);

CREATE TABLE etude_ue_typologie_concept
(
    concept_id  VARCHAR,
    etude_ue_id INT,

    PRIMARY KEY (etude_ue_id, concept_id),
    FOREIGN KEY (concept_id) REFERENCES concept (concept_id),
    FOREIGN KEY (etude_ue_id) REFERENCES etude_ue (etude_ue_id)
);

ALTER TABLE prelevement
    ADD FOREIGN KEY (concept_id_categorie_prelevement) REFERENCES concept (concept_id);

ALTER TABLE prelevement
    ADD FOREIGN KEY (concept_id_methode_collecte_prelevement) REFERENCES concept (concept_id);

CREATE TABLE etude_prelevement_typologie_concept
(
    concept_id           VARCHAR,
    etude_prelevement_id INT,

    PRIMARY KEY (etude_prelevement_id, concept_id),
    FOREIGN KEY (concept_id) REFERENCES concept (concept_id),
    FOREIGN KEY (etude_prelevement_id) REFERENCES etude_prelevement (etude_prelevement_id)
);

ALTER TABLE prelevement
    ADD FOREIGN KEY (concept_id_typologie_prelevement) REFERENCES concept (concept_id);



