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

CREATE TABLE type_unite_enregistrement (

  type_unite_enregistrement_id INT NOT NULL,
  type_unite_enregistrement_label VARCHAR(255) NOT NULL,

  PRIMARY KEY (type_unite_enregistrement_id)

);

CREATE TABLE unite_enregistrement (

  unite_enregistrement_id INT NOT NULL, -- todo : on utilise quoi comme ID ??
  id_interne VARCHAR(255) NOT NULL,
  type_unite_enregistrement_id INT,
  ue_date TIMESTAMP,
  PRIMARY KEY (unite_enregistrement_id),
  FOREIGN KEY (type_unite_enregistrement_id) REFERENCES type_unite_enregistrement (type_unite_enregistrement_id)

);

CREATE TABLE stratigraphie (
    unite_enregistrement_id_1 INT, -- la combinaison des deux ue est elle unique ?
    unite_enregistrement_id_2 INT,
    type_relation INT -- todo : table séparé?
    stratigraphie_id INT NOT NULL,

    PRIMARY KEY (stratigraphie_id),
    FOREIGN KEY (unite_enregistrement_id_1) REFERENCES unite_enregistrement (unite_enregistrement_id)
    FOREIGN KEY (unite_enregistrement_id_2) REFERENCES unite_enregistrement (unite_enregistrement_id)

)

-- todo INSERT enumeration of values for record unit type

CREATE TABLE equipe (
    equipe_id INT NOT NULL ,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    date_debut TIMESTAMP,
    date_fin TIMESTAMP,

    PRIMARY KEY (equipe_id)
);

CREATE TABLE type_role (

  type_role_id INT NOT NULL ,
  type_role_label VARCHAR(255) NOT NULL,

  PRIMARY KEY (type_role_id)
);-- todo INSERT enumeration of values for type role

CREATE TABLE role (
    role_id INT NOT NULL ,
    type_role_id INT NOT NULL,
    role_scope INT, -- todo : C'est quoi?
    role_date_debut DATE,
    role_date_fin DATE,
    equipe_id INT,
    droit_access_role INT, -- todo : fk vers quoi? INT ?

    PRIMARY KEY (role_id),
    FOREIGN KEY (equipe_id) REFERENCES equipe (equipe_id), -- todo : verifier ce lien, pas sur d'avoir compris (GB)
    FOREIGN KEY (type_role_id) REFERENCES type_role (type_role_id)
)

CREATE TABLE auteur_role (

  role_id INT NOT NULL,
  auteur_id  INT NOT NULL,
  -- todo : finir
);

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

CREATE TABLE intervention (
    intervention_id INT NOT NULL ,
    operation_archeo_id INT,
    name VARCHAR(255),
    description TEXT,
    type_intervention_label INT, -- todo : pas du tout sur (gb)
    annee_intervention INT,
    parent_intervention_id INT,
    coordonnee_intervention_id INT, -- si pas de SIG
    -- todo : systeme_coordo stockage spatial
    date_debut_intervention TIMESTAMP,
    date_fin_intervention TIMESTAMP

    PRIMARY KEY (intervention_id),
    FOREIGN KEY (operation_archeo_id) REFERENCES operation_archeo (operation_archeo_id)
    FOREIGN KEY (parent_intervention_id) REFERENCES intervention (intervention_id)
    FOREIGN KEY (coordonnee_intervention) REFERENCES systeme_coordo (systeme_coordo_id)
);

CREATE TABLE intervention_equipe (
    -- pour
    intervention_id  INT NOT NULL,
    equipe_id  INT NOT NULL,
    -- todo : finir
);




