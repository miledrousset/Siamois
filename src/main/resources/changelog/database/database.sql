CREATE TABLE unite_enregistrement
(
    ue_id                  SERIAL,
    type_ue                INT, -- TODO : Lien avec un thésaurus
    date_enregistrement_ue TIMESTAMP,
    emprise_ue             INT, -- TODO : Lien avec le SIG
    PRIMARY KEY (ue_id)
);

CREATE TABLE lien_stratigraphique (
    nom_lien_stratigraphique VARCHAR(50),

    PRIMARY KEY (nom_lien_stratigraphique)
);

CREATE TABLE stratigraphie(
    ue_id_1_stratigraphie   INT,
    ue_id_2_stratigraphie   INT,
    lien_stratigraphie      VARCHAR(50),

    PRIMARY KEY (ue_id_1_stratigraphie, ue_id_2_stratigraphie),
    FOREIGN KEY (ue_id_1_stratigraphie) REFERENCES unite_enregistrement(ue_id),
    FOREIGN KEY (ue_id_2_stratigraphie) REFERENCES unite_enregistrement(ue_id),
    FOREIGN KEY (lien_stratigraphie) REFERENCES lien_stratigraphique(nom_lien_stratigraphique)
);

CREATE TABLE document (
    document_id SERIAL,
    -- TODO : Table document

    PRIMARY KEY (document_id)
);

CREATE TABLE media_ue (
    ue_id_media_ue INT,
    document_id_media_ue INT,

    PRIMARY KEY (ue_id_media_ue, document_id_media_ue),
    FOREIGN KEY (ue_id_media_ue) REFERENCES unite_enregistrement (ue_id),
    FOREIGN KEY (document_id_media_ue) REFERENCES document(document_id)
);

CREATE TABLE hierarchie_ue
(
    parent_id_hierarchie_ue INT,
    enfant_id_hierarchie_ue INT,

    PRIMARY KEY (parent_id_hierarchie_ue, enfant_id_hierarchie_ue),
    FOREIGN KEY (parent_id_hierarchie_ue) REFERENCES unite_enregistrement (ue_id),
    FOREIGN KEY (enfant_id_hierarchie_ue) REFERENCES unite_enregistrement (ue_id)
);

CREATE TABLE etude_ue
(
    etude_ue_id      SERIAL,
    auteur_etude_ue  INT,     -- TODO : Lien vers auteur
    date_etude_ue    TIMESTAMP,
    methode_etude_ue VARCHAR, -- TODO : À définir

    PRIMARY KEY (etude_ue_id)
);

CREATE TABLE interpretation_ue
(
    ue_id_interpretation_ue    INT,
    etude_id_interpreration_ue INT,

    PRIMARY KEY (ue_id_interpretation_ue, etude_id_interpreration_ue),
    FOREIGN KEY (ue_id_interpretation_ue) REFERENCES unite_enregistrement (ue_id),
    FOREIGN KEY (etude_id_interpreration_ue) REFERENCES etude_ue (etude_ue_id)
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
    prelevement_id                    SERIAL,
    ue_id_prelevement                 INT,
    morphometrie_prelevement          INT, -- TODO : FK (???)
    categorie_prelevement             INT, -- TODO : Lien avec un thésaurus
    traitement_prelevement            INT, -- TODO : Lien avec Opération traitement
    methode_collecte_prelevement      INT, -- TODO : Lien avec un thésaurus
    date_collecte_prelevement         TIMESTAMP,
    localisation_collecte_prelevement INT, -- TODO : Lien avec le SIG (?)
    stockage_prelevement              VARCHAR,
    mouvement_prelevement_id          INT,

    PRIMARY KEY (prelevement_id),
    FOREIGN KEY (ue_id_prelevement) REFERENCES unite_enregistrement (ue_id),
    FOREIGN KEY (mouvement_prelevement_id) REFERENCES mouvement_prelevement (mouvement_prelevement_id)
);

CREATE TABLE document_prelevement (
    prelevement_id_document_prelevement INT,
    document_id_document_prelevement INT,

    PRIMARY KEY (prelevement_id_document_prelevement, document_id_document_prelevement),
    FOREIGN KEY (prelevement_id_document_prelevement) REFERENCES prelevement (prelevement_id),
    FOREIGN KEY (document_id_document_prelevement) REFERENCES document (document_id)
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

CREATE TABLE lien_prelevment_etude (
    prelevement_id_lien INT,
    etude_prelevement_id_lien INT,

    PRIMARY KEY (prelevement_id_lien, etude_prelevement_id_lien),
    FOREIGN KEY (prelevement_id_lien) REFERENCES prelevement(prelevement_id),
    FOREIGN KEY (etude_prelevement_id_lien) REFERENCES etude_prelevement(etude_prelevement_id)
);

CREATE TABLE document_etude_prelevement (
    etude_prelevement_id_document INT,
    document_id_etude_prelevement INT,

    PRIMARY KEY (etude_prelevement_id_document, document_id_etude_prelevement),
    FOREIGN KEY (etude_prelevement_id_document) REFERENCES etude_prelevement (etude_prelevement_id),
    FOREIGN KEY (document_id_etude_prelevement) REFERENCES document(document_id)
);



