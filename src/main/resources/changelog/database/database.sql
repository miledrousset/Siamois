CREATE TABLE unite_enregistrement
(
    ue_id                  SERIAL,
    type_ue                INT, -- TODO : Lien avec un thésaurus
    date_enregistrement_ue TIMESTAMP,
    emprise_ue             INT, -- TODO : Lien avec le SIG
    PRIMARY KEY (ue_id)
);

CREATE TABLE lien_stratigraphique
(
    nom_lien_stratigraphique VARCHAR(50),

    PRIMARY KEY (nom_lien_stratigraphique)
);

CREATE TABLE stratigraphie
(
    ue_id_1 INT,
    ue_id_2 INT,
    lien_stratigraphie    VARCHAR(50),

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
    etude_ue_id      SERIAL,
    auteur_etude_ue  INT,     -- TODO : Lien vers auteur
    date_etude_ue    TIMESTAMP,
    methode_etude_ue VARCHAR, -- TODO : À définir
    date_ouverture_ue   TIMESTAMP,
    date_fermeture_ue   TIMESTAMP,

    PRIMARY KEY (etude_ue_id)
);

CREATE TABLE document_etude_ue (
    etude_ue_id INT,
    document_id INT,

    PRIMARY KEY (etude_ue_id, document_id),
    FOREIGN KEY (etude_ue_id) REFERENCES etude_ue(etude_ue_id),
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
    document_id INT,

    PRIMARY KEY (etude_prelevement_id, document_id),
    FOREIGN KEY (etude_prelevement_id) REFERENCES etude_prelevement (etude_prelevement_id),
    FOREIGN KEY (document_id) REFERENCES document (document_id)
);



