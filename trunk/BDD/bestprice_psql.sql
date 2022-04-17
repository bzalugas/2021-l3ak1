
--
-- Base de données : BestPrice
--

-- --------------------------------------------------------

-- 
-- Creation d'un bigint unsigned
-- 

CREATE DOMAIN uint8 as int8
	CHECK(VALUE >= 0 AND VALUE < 18446744073709551616);


--
-- Structure de la table Localisation
--

DROP TABLE Localisation;

CREATE TABLE Localisation (
  id SERIAL PRIMARY KEY,
  latitude DECIMAL(8,6) NOT NULL,
  longitude DECIMAL(7,6) NOT NULL,
  nom VARCHAR(100) NOT NULL,
  CONSTRAINT lat_long_unique UNIQUE (latitude, longitude)
);

-- --------------------------------------------------------

--
-- Structure de la table Produit
--

DROP TABLE Produit;

CREATE TABLE Produit (
  codeBarres VARCHAR(15) PRIMARY KEY,
  marque VARCHAR(255) DEFAULT NULL,
  nom VARCHAR(255) NOT NULL,
  quantite VARCHAR(255) DEFAULT NULL,
  imagePath TEXT
);

-- --------------------------------------------------------

--
-- Structure de la table Prix
--

DROP TABLE Prix;

CREATE TABLE Prix (
  id SERIAL PRIMARY KEY,
  Produit_codeBarres VARCHAR(15) NOT NULL REFERENCES Produit(codeBarres) ON DELETE CASCADE ON UPDATE CASCADE,
  prix NUMERIC(6, 2) NOT NULL,
  datePrix TIMESTAMP (0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  Localisation_id SERIAL NOT NULL REFERENCES Localisation(id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT cb_prix_datePrix_loc_unique UNIQUE(Produit_codeBarres, prix, datePrix, Localisation_id)
);