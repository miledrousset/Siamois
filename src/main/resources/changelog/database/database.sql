-- todo Date de creation/modifs?
-- todo champs optionels?

-- todo : note du miro :
-- POUR DES RAISONS DE SÉCURITÉ une information de gestion est  AUTOMATIQUEMENT associée à CHAQUE enregistrement, en référence aux 3 questions "organisationnelles" fondamentales :
-- - QUI : auteur de l'enregistrement (obtenu par son login) qui peut être différent du référent (prise de vue, auteur de la fiche)
-- - QUAND : date de l'enregistrement (donnée par l'heure machine)
-- - OU : "lieu" de l'enregistrement (donnée par l'IP machine)
-- - QUOI : identifiant unique ARK (automatiquement créé par l'appli) pour générer un URI pérenne
-- Ces informations ne sont pas indiquées dans les tables
-- IMPLÉMENTATION Ces informations pourraient être dans une table spécifique (LOG)

-- POUR DISTINGUER dans le nommage des entités informatiques les champs renseignés avec un VOCABULAIRE CONTRÔLÉ des champs en TEXTE LIBRE, deux formulations sont employées :
--    - name (nom) : texte libre
--  - label (libellé) : vocabulaire contrôlé

CREATE TABLE type_unite_enregistrement
(

    type_ue_id    SERIAL,
    type_ue_label VARCHAR(255) NOT NULL,

    PRIMARY KEY (type_ue_id)

);

CREATE TABLE unite_enregistrement
(

    ue_id      SERIAL,
    ue_uri     VARCHAR(255) NOT NULL,
    ue_type_id INT,
    ue_date    TIMESTAMP,
    PRIMARY KEY (ue_id),
    FOREIGN KEY (ue_type_id) REFERENCES type_unite_enregistrement (type_ue_id)

);

CREATE TABLE stratigraphie
(
    stratigraphie_ue_id_1 INT, -- la combinaison des deux ue est elle unique ?
    stratigraphie_ue_id_2 INT,
    type_relation         INT, -- todo : table séparé?

    PRIMARY KEY (stratigraphie_ue_id_1, stratigraphie_ue_id_2),
    FOREIGN KEY (stratigraphie_ue_id_1) REFERENCES unite_enregistrement (ue_id),
    FOREIGN KEY (stratigraphie_ue_id_2) REFERENCES unite_enregistrement (ue_id)

);

-- todo INSERT enumeration of values for record unit type

CREATE TABLE equipe
(
    equipe_id          SERIAL,
    equipe_name        VARCHAR(255) NOT NULL,
    equipe_description VARCHAR(255) NOT NULL,
    equipe_date_debut  TIMESTAMP,
    equipe_date_fin    TIMESTAMP,

    PRIMARY KEY (equipe_id)
);

CREATE TABLE type_role
(

    type_role_id    SERIAL,
    type_role_label VARCHAR(255) NOT NULL,

    PRIMARY KEY (type_role_id)
);-- todo INSERT enumeration of values for type role

CREATE TABLE role
(
    role_id           SERIAL,
    role_type_id      INT NOT NULL,
    role_scope        INT,                                      -- todo : C'est quoi?
    role_date_debut   TIMESTAMP,
    role_date_fin     TIMESTAMP,
    role_equipe_id    INT,
    role_droit_access INT,                                      -- todo : fk vers quoi? INT ?

    PRIMARY KEY (role_id),
    FOREIGN KEY (role_equipe_id) REFERENCES equipe (equipe_id), -- todo : verifier ce lien, pas sur d'avoir compris (GB)
    FOREIGN KEY (role_type_id) REFERENCES type_role (type_role_id)
);

CREATE TABLE auteur
(
    auteur_id          SERIAL,
    auteur_nom         VARCHAR,
    auteur_mail        VARCHAR,

    auteur_role        VARCHAR,
    auteur_droit_acces VARCHAR,

    PRIMARY KEY (auteur_id)
);

CREATE TABLE auteur_role
(

    role_id   INT NOT NULL,
    auteur_id INT NOT NULL,

    PRIMARY KEY (auteur_id, role_id),
    FOREIGN KEY (role_id) REFERENCES role (role_id),
    FOREIGN KEY (auteur_id) REFERENCES auteur (auteur_id)
);

/*
CREATE TABLE operation_archeo ( -- c'est quoi??
    operation_archeo_id INT NOT NULL ,
    PRIMARY KEY (operation_archeo_id)
);

CREATE TABLE systeme_coordo (
    system_coordo_id INT NOT NULL,
    systeme_coordo_label VARCHAR(255),
    system_coordo_definition TEXT,

    PRIMARY KEY (systeme_coordo)
)
*/

CREATE TABLE intervention
(
    intervention_id               SERIAL,
    intervention_operation_archeo INT,
    intervention_nom              VARCHAR(255),
    intervention_description      TEXT,
    -- type_intervention_label INT, -- todo : pas du tout sur (gb)
    intervention_annee            INT,
    intervention_parent_id        INT,
    intervention_coordo           INT, -- si pas de SIG
    -- todo : systeme_coordo stockage spatial
    intervention_date_debut       TIMESTAMP,
    intervention_date_fin         TIMESTAMP,

    PRIMARY KEY (intervention_id),
    -- FOREIGN KEY (operation_archeo_id) REFERENCES operation_archeo (operation_archeo_id),
    FOREIGN KEY (intervention_parent_id) REFERENCES intervention (intervention_id)
    -- FOREIGN KEY (coordonnee_intervention) REFERENCES systeme_coordo (systeme_coordo_id)
);

CREATE TABLE intervention_equipe
(
    intervention_id INT NOT NULL,
    equipe_id       INT NOT NULL,



    PRIMARY KEY (intervention_id, equipe_id),
    FOREIGN KEY (intervention_id) REFERENCES intervention (intervention_id),
    FOREIGN KEY (equipe_id) REFERENCES equipe (equipe_id)
);

CREATE TABLE etude_ue_g2
(
    etude_ue_id        SERIAL,
    etude_ue_auteur    VARCHAR,
    etude_ue_date      TIMESTAMP,

    etude_ue_typologie VARCHAR,
    etude_ue_document  VARCHAR,

    PRIMARY KEY (etude_ue_id)
    -- Open form et Open Typo
);

CREATE TABLE interpretation_etude_ue (
    interpretation_etude_ue INT,
    interpreration_ue INT,

    PRIMARY KEY (interpreration_ue, interpretation_etude_ue),
    FOREIGN KEY (interpreration_ue) REFERENCES unite_enregistrement(ue_id),
    FOREIGN KEY (interpretation_etude_ue) REFERENCES etude_ue_g2(etude_ue_id)
);

CREATE TABLE document_g3
(
    document_id           SERIAL PRIMARY KEY,
    document_nature_id    INT,
    document_echelle_id   INT,
    document_auteur_id    INT,
    document_format_id    INT,
    document_parent_id    INT DEFAULT NULL,
    document_localisation VARCHAR,
    -- TODO

    FOREIGN KEY (document_parent_id) REFERENCES document_g3 (document_id)
);

CREATE TABLE etude_prelevement_g2
(
    etude_prelevement_id               SERIAL,
    etude_prelevement_auteur_id        INT,
    etude_prelevement_date_etude       TIMESTAMP,
    etude_prelevement_methode_etude_id INT,
    etude_prelevement_typologie_id     INT,
    etude_prelevement_document_id      INT,

    PRIMARY KEY (etude_prelevement_id)
);

CREATE TABLE mobilier_g3
(
    mobilier_id           SERIAL PRIMARY KEY,
    mobilier_localisation VARCHAR,
    mobilier_media        VARCHAR,
    mobilier_stockage     VARCHAR
);

CREATE TABLE categorie_prelevement
(
    categorie_prelevement_id  SERIAL PRIMARY KEY,
    categorie_prelevement_nom VARCHAR
);

CREATE TYPE operation_traitement_type_label_enum AS ENUM ('Consolidation', 'Radiographie', 'Nettoyage', 'Restauration');
CREATE TABLE operation_traitement
(
    operation_traitement_id              SERIAL,
    operation_traitement_type_label      operation_traitement_type_label_enum,
    operation_traitement_type_definition TEXT,

    PRIMARY KEY (operation_traitement_id)
);


CREATE TABLE mouvement_prelevement
(
    mouvement_prelevement_id             SERIAL,
    mouvement_prelevement_date_sortie    TIMESTAMP,
    mouvement_prelevement_date_retour    TIMESTAMP,
    mouvement_prelevement_lieu           TEXT, -- table de lieu ?
    mouvement_prelevement_prelevement_id INT,

    PRIMARY KEY (mouvement_prelevement_id)
);

CREATE TABLE prelevement
(
    prelevement_id                    SERIAL,
    prelevement_ue_id                 INT,
    prelevement_document_ID           INT,
    prelevement_morphometrie          VARCHAR,
    prelevement_categorie_id          INT,
    prelevement_traitement_id         INT,
    prelevement_etude_id              INT,
    prelevement_methode_collecte      VARCHAR DEFAULT 'Fouille',
    prelevement_date_collecte         TIMESTAMP,
    prelevement_localisation_collecte VARCHAR,
    prelevement_stockage              VARCHAR,
    prelevement_mouvement_id               INT,

    PRIMARY KEY (prelevement_id),
    FOREIGN KEY (prelevement_ue_id) REFERENCES unite_enregistrement (ue_id),
    FOREIGN KEY (prelevement_document_ID) REFERENCES document_g3 (document_id),
    FOREIGN KEY (prelevement_categorie_id) REFERENCES categorie_prelevement (categorie_prelevement_id),
    FOREIGN KEY (prelevement_etude_id) REFERENCES etude_prelevement_g2 (etude_prelevement_id),
    FOREIGN KEY (prelevement_mouvement_id) REFERENCES mouvement_prelevement(mouvement_prelevement_id)
);

ALTER TABLE mouvement_prelevement
ADD FOREIGN KEY (mouvement_prelevement_prelevement_id) REFERENCES prelevement (prelevement_id)


