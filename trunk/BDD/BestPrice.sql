-- phpMyAdmin SQL Dump
-- version 5.1.0
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost:8889
-- Généré le : dim. 27 mars 2022 à 08:18
-- Version du serveur :  5.7.34
-- Version de PHP : 7.4.21

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `BestPrice`
--
CREATE DATABASE IF NOT EXISTS `BestPrice` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `BestPrice`;

-- --------------------------------------------------------

--
-- Structure de la table `Localisation`
--

CREATE TABLE IF NOT EXISTS `Localisation` (
  `id` tinyint(2) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT,
  `latitude` decimal(8,6) NOT NULL,
  `longitude` decimal(7,6) NOT NULL,
  `nom` varchar(70) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `latitude_UNIQUE` (`latitude`),
  UNIQUE KEY `longitude_UNIQUE` (`longitude`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `Prix`
--

CREATE TABLE IF NOT EXISTS `Prix` (
  `id` tinyint(3) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Produit_codeBarres` varchar(15) NOT NULL,
  `prix` double UNSIGNED NOT NULL,
  `datePrix` datetime NOT NULL,
  `Localisation_id` tinyint(2) UNSIGNED ZEROFILL NOT NULL,
  PRIMARY KEY (`id`,`Produit_codeBarres`,`Localisation_id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_Prix_Produit1_idx` (`Produit_codeBarres`),
  KEY `fk_Prix_Localisation1_idx` (`Localisation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `Produit`
--

CREATE TABLE IF NOT EXISTS `Produit` (
  `codeBarres` varchar(15) NOT NULL,
  `marque` varchar(255) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `contenu` varchar(255) DEFAULT NULL,
  `imagePath` mediumtext,
  PRIMARY KEY (`codeBarres`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `Prix`
--
ALTER TABLE `Prix`
  ADD CONSTRAINT `fk_Prix_Localisation1` FOREIGN KEY (`Localisation_id`) REFERENCES `Localisation` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Prix_Produit1` FOREIGN KEY (`Produit_codeBarres`) REFERENCES `Produit` (`codeBarres`) ON DELETE NO ACTION ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
