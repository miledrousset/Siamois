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

    id_type_ue    SERIAL,
    label_type_ue VARCHAR(255) NOT NULL,

    PRIMARY KEY (id_type_ue)

);

CREATE TABLE unite_enregistrement
(

    id_ue      SERIAL,
    uri_ue     VARCHAR(255) NOT NULL,
    type_id_ue INT,
    date_ue    TIMESTAMP,
    PRIMARY KEY (id_ue),
    FOREIGN KEY (type_id_ue) REFERENCES type_unite_enregistrement (id_type_ue)

);

CREATE TABLE stratigraphie
(
    ue_id_1_stratigraphie       INT, -- la combinaison des deux ue est-elle unique ?
    ue_id_2_stratigraphie       INT,
    type_relation_stratigraphie INT, -- todo : table séparé?

    PRIMARY KEY (ue_id_1_stratigraphie, ue_id_2_stratigraphie),
    FOREIGN KEY (ue_id_1_stratigraphie) REFERENCES unite_enregistrement (ue_id),
    FOREIGN KEY (ue_id_2_stratigraphie) REFERENCES unite_enregistrement (ue_id)

);

-- todo INSERT enumeration of values for record unit type

CREATE TABLE equipe
(
    id_equipe          SERIAL,
    nom_equipe         VARCHAR(255) NOT NULL,
    description_equipe VARCHAR(255) NOT NULL,
    date_debut_equipe  TIMESTAMP,
    date_fin_equipe    TIMESTAMP,

    PRIMARY KEY (id_equipe)
);

CREATE TABLE type_role
(

    id_type_role    SERIAL,
    label_type_role VARCHAR(255) NOT NULL,

    PRIMARY KEY (id_type_role)
);-- todo INSERT enumeration of values for type role

CREATE TABLE role
(
    id_role           SERIAL,
    type_role_id_role INT NOT NULL,
    scope_role        INT,                                      -- todo : C'est quoi?
    date_debut_role   TIMESTAMP,
    date_fin_role     TIMESTAMP,
    equipe_id_role    INT,
    droit_acces_role  INT,                                      -- todo : fk vers quoi? INT ?

    PRIMARY KEY (id_role),
    FOREIGN KEY (equipe_id_role) REFERENCES equipe (id_equipe), -- todo : verifier ce lien, pas sur d'avoir compris (GB)
    FOREIGN KEY (type_role_id_role) REFERENCES type_role (id_type_role)
);

CREATE TABLE auteur
(
    id_auteur          SERIAL,
    nom_auteur         VARCHAR,
    mail_auteur        VARCHAR,

    role_auteur        VARCHAR,
    droit_acces_auteur VARCHAR,

    PRIMARY KEY (id_auteur)
);

CREATE TABLE auteur_role
(

    id_role   INT NOT NULL,
    id_auteur INT NOT NULL,

    PRIMARY KEY (id_auteur, id_role),
    FOREIGN KEY (id_role) REFERENCES role (id_role),
    FOREIGN KEY (id_auteur) REFERENCES auteur (id_auteur)
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
    id_intervention                  SERIAL,
    id_operation_archeo_intervention INT,
    nom_intervention                 VARCHAR(255),
    description_intervention         TEXT,
    -- type_intervention_label INT, -- todo : pas du tout sur (gb)
    annee_intervention               INT,
    id_parent_intervention           INT,
    sig_intervention                 INT, -- si pas de SIG
    -- todo : systeme_coordo stockage spatial
    date_debut_intervention          TIMESTAMP,
    date_fin_intervention            TIMESTAMP,

    PRIMARY KEY (id_intervention),
    -- FOREIGN KEY (operation_archeo_id) REFERENCES operation_archeo (operation_archeo_id),
    FOREIGN KEY (id_parent_intervention) REFERENCES intervention (id_intervention)
    -- FOREIGN KEY (coordonnee_intervention) REFERENCES systeme_coordo (systeme_coordo_id)
);

CREATE TABLE intervention_equipe
(
    id_intervention_equipe INT NOT NULL,
    id_equipe              INT NOT NULL,


    PRIMARY KEY (id_intervention_equipe, id_equipe),
    FOREIGN KEY (id_intervention_equipe) REFERENCES intervention (id_intervention),
    FOREIGN KEY (id_equipe) REFERENCES equipe (id_equipe)
);

CREATE TABLE etude_ue_g2
(
    id_etude_ue        SERIAL,
    auteur_etude_ue    VARCHAR,
    date_etude_ue      TIMESTAMP,

    typologie_etude_ue VARCHAR,
    document_etude_ue  VARCHAR,

    PRIMARY KEY (id_etude_ue)
    -- Open form et Open Typo
);

CREATE TABLE interpretation_etude_ue
(
    id_ue       INT,
    id_etude_ue INT,
    PRIMARY KEY (id_ue, id_etude_ue),
    FOREIGN KEY (id_ue) REFERENCES unite_enregistrement (id_ue),
    FOREIGN KEY (id_etude_ue) REFERENCES etude_ue_g2 (id_etude_ue)
);

CREATE TABLE document_g3
(
    id_document           SERIAL PRIMARY KEY,
    id_nature_document    INT,
    id_echelle_document   INT,
    id_auteur_document    INT,
    id_format_document    INT,
    id_parent_document    INT DEFAULT NULL,
    localisation_document VARCHAR,
    -- TODO

    FOREIGN KEY (id_parent_document) REFERENCES document_g3 (id_document),
    FOREIGN KEY (id_auteur_document) REFERENCES auteur (id_auteur)
);

CREATE TABLE etude_prelevement_g2
(
    id_etude_prelevement           SERIAL,
    id_auteur_etude_prelevement    INT,
    date_etude_prelevement         TIMESTAMP,
    id_methode_etude_prelevement   INT,
    id_typologie_etude_prelevement INT,
    id_document_etude_prelevement  INT,

    PRIMARY KEY (id_etude_prelevement),
    FOREIGN KEY (id_auteur_etude_prelevement) REFERENCES auteur (id_auteur)
);

CREATE TABLE mobilier_g3
(
    id_mobilier           SERIAL PRIMARY KEY,
    localisation_mobilier VARCHAR,
    media_mobilier        VARCHAR,
    stoage_mobilier       VARCHAR
);

CREATE TABLE categorie_prelevement
(
    id_categorie_prelevement  SERIAL PRIMARY KEY,
    nom_categorie_prelevement VARCHAR
);

CREATE TYPE operation_traitement_type_label_enum AS ENUM ('Consolidation', 'Radiographie', 'Nettoyage', 'Restauration');
CREATE TABLE operation_traitement
(
    id_operation_traitement              SERIAL,
    type_label_operation_traitement      operation_traitement_type_label_enum,
    type_definition_operation_traitement TEXT,

    PRIMARY KEY (id_operation_traitement)
);


CREATE TABLE mouvement_prelevement
(
    id_mouvement_prelevement             SERIAL,
    date_sortie_mouvement_prelevement    TIMESTAMP,
    date_retour_mouvement_prelevement    TIMESTAMP,
    lieu_mouvement_prelevement           TEXT, -- table de lieu ?
    id_prelevement_mouvement_prelevement INT,

    PRIMARY KEY (id_mouvement_prelevement)
);

CREATE TABLE prelevement
(
    id_prelevement                    SERIAL,
    id_ue_prelevement                 INT,
    id_document_prelevement           INT,
    morphometrie_prelevement          VARCHAR,
    id_categorie_prelevement          INT,
    id_traitement_prelevement         INT,
    id_etude_prelevement              INT,
    methode_collecte_prelevement      VARCHAR DEFAULT 'Fouille',
    date_collecte_prelevement         TIMESTAMP,
    localisation_collecte_prelevement VARCHAR,
    stockage_prelevement              VARCHAR,
    mouvement_id_prelevement          INT,

    PRIMARY KEY (id_prelevement),
    FOREIGN KEY (id_ue_prelevement) REFERENCES unite_enregistrement (id_ue),
    FOREIGN KEY (id_document_prelevement) REFERENCES document_g3 (id_document),
    FOREIGN KEY (id_categorie_prelevement) REFERENCES categorie_prelevement (id_categorie_prelevement),
    FOREIGN KEY (id_etude_prelevement) REFERENCES etude_prelevement_g2 (id_etude_prelevement),
    FOREIGN KEY (mouvement_id_prelevement) REFERENCES mouvement_prelevement (id_mouvement_prelevement)
);

ALTER TABLE mouvement_prelevement
    ADD FOREIGN KEY (id_prelevement_mouvement_prelevement) REFERENCES prelevement (id_prelevement)


