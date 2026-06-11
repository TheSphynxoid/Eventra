-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : sam. 10 mai 2025 à 15:12
-- Version du serveur : 9.1.0
-- Version de PHP : 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `myapp`
--

-- --------------------------------------------------------

--
-- Structure de la table `evenement`
--

DROP TABLE IF EXISTS `evenement`;
CREATE TABLE IF NOT EXISTS `evenement` (
  `id` int NOT NULL AUTO_INCREMENT,
  `titre` varchar(100) NOT NULL,
  `description` text,
  `id_organisateur` int DEFAULT NULL,
  `longitude` text,
  `latitude` text,
  `dateDebut` datetime NOT NULL,
  `dateFin` datetime NOT NULL,
  `adresse` text,
  `categorie` text,
  `visibilite` text,
  `capacite` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

--
-- Déchargement des données de la table `evenement`
--

INSERT INTO `evenement` (`id`, `titre`, `description`, `id_organisateur`, `longitude`, `latitude`, `dateDebut`, `dateFin`, `adresse`, `categorie`, `visibilite`, `capacite`) VALUES
(2, 'erzerezr', 'Ne manquez pas cette occasion unique : Tones remarquable, où chaque détail a été pensé pour votre plus grand plaisir.', 3, '2.3522', '48.8566', '2025-05-21 09:00:00', '2025-05-31 17:00:00', 'Hôtel de ville, Place de l\'Hôtel de Ville, Quartier Saint-Merri, Paris 4e Arrondissement, Paris, France métropolitaine, 75004, France', 'SALON / FOIRE', 'Public', 50);

-- --------------------------------------------------------

--
-- Structure de la table `reclamation`
--

DROP TABLE IF EXISTS `reclamation`;
CREATE TABLE IF NOT EXISTS `reclamation` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_client` varchar(50) NOT NULL,
  `type` varchar(100) NOT NULL,
  `description` text NOT NULL,
  `date_reclamation` date NOT NULL,
  `etat` varchar(50) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id` (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=32 DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `reclamation`
--

INSERT INTO `reclamation` (`id`, `id_client`, `type`, `description`, `date_reclamation`, `etat`, `email`) VALUES
(26, '123', 'Probléme Technique', 'mauvaise', '2025-05-02', 'Traité', 'maryeemferjaani@gmail.com'),
(27, '145', 'Service', 'stupit', '2025-05-02', 'En cours', 'ferjaanimaryeem@gmail.com'),
(28, '174', 'Service', 'hello', '2025-05-01', 'En cours', 'ferjanimariem@gmail.com'),
(29, '789', 'Service', 'chedi', '2025-05-09', 'En cours', 'chedibelgacem@gmail.com'),
(30, '753', 'Service', 'mal', '2025-04-29', 'Traité', 'chedi@gmail.com'),
(31, '753', 'Service', 'hhhh', '2025-05-07', 'En cours', 'ranimlahbaieb@gmail.com');

-- --------------------------------------------------------

--
-- Structure de la table `reponse`
--

DROP TABLE IF EXISTS `reponse`;
CREATE TABLE IF NOT EXISTS `reponse` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_reclamation` int NOT NULL,
  `contenu` text NOT NULL,
  `date_reponse` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_reclamation` (`id_reclamation`)
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `reponse`
--

INSERT INTO `reponse` (`id`, `id_reclamation`, `contenu`, `date_reponse`) VALUES
(1, 10, 'hiii raniiii', '2025-05-07'),
(2, 11, 'hiiii testt raniaa', '2025-05-07'),
(3, 24, 'raniaaaaaa', '2025-05-07'),
(4, 24, 'hhhhhhh', '2025-05-07'),
(5, 24, 'hello raniiiiiiii', '2025-05-07'),
(6, 24, 'helllooooooo', '2025-05-07'),
(7, 24, 'fok aliaaaa', '2025-05-07'),
(8, 24, 'besmelehhhhh', '2025-05-07'),
(9, 24, 'hhhhhh', '2025-05-07'),
(10, 24, 'hhhhhhh', '2025-05-07'),
(11, 13, 'oooooooo', '2025-05-07'),
(12, 24, 'fafaffafa', '2025-05-07'),
(13, 16, 'dddddddd', '2025-05-07'),
(14, 12, 'kv,njnv', '2025-05-08'),
(15, 25, 'hiiiiiiiiiiiiiiiiiiiiii', '2025-05-08'),
(16, 26, 'j\'ai bien recus', '2025-05-09'),
(17, 30, 'd\'acc', '2025-05-09');

-- --------------------------------------------------------

--
-- Structure de la table `roles`
--

DROP TABLE IF EXISTS `roles`;
CREATE TABLE IF NOT EXISTS `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;

--
-- Déchargement des données de la table `roles`
--

INSERT INTO `roles` (`id`, `name`) VALUES
(1, 'admin'),
(3, 'Agent_de_reclamation'),
(7, 'Client'),
(6, 'En_attente'),
(2, 'Gestionnaire_des_tickets '),
(5, 'Organisateur'),
(4, 'Responsable_sponsor');

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  `prenom` varchar(100) NOT NULL,
  `email` varchar(255) NOT NULL,
  `numero_telephone` varchar(20) DEFAULT NULL,
  `role_id` int DEFAULT NULL,
  `mot_de_passe` varchar(255) NOT NULL,
  `active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `profile_picture` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `nom`, `prenom`, `email`, `numero_telephone`, `role_id`, `mot_de_passe`, `active`, `created_at`, `profile_picture`) VALUES
(4, 'admin', 'admin', 'rlahbaieb@gmail.com', '51663528', 1, '$2a$10$3qYqSK97.r3y1pDMETKoI.1E1qOIF7Sa7JE8T3/LUqCH.b3RHApjK', 1, '2025-05-03 18:30:07', 'profile_images\\user_4_1746880065652.jpg'),
(10, 'ben aziza', 'montassar', 'montassar121@gmail.com', '27890056', 3, '123456', 0, '2025-05-04 19:43:10', NULL),
(11, 'hamza', 'lahbaieb', 'hamza@ranim.com', '23456789', 4, '$2a$10$OuXdRCwcsNAQvA95LNyuO.qGiNchO/eh/vG2r6/YP8s4zj0vqfiri', 1, '2025-05-04 20:17:07', NULL),
(12, 'teg', 'teg', 'teg.dev.m@gmail.com', '12345566', 3, '$2a$10$ao1OPuCaeSKa1Z7s4XSfneflbs519aLzZgDzPjMjtp4jeGzTJ3cZC', 1, '2025-05-04 21:06:39', NULL),
(13, 'Lahbaieb', 'Siwar', 'ranim.lahbaieb@esprit.com', '51663328', 5, '$2a$10$eH2HfxVrFqvEQ95ZX7LTHu2wxGs188GXZy8p2EdZY62eW.9l63dwS', 1, '2025-05-05 08:15:46', NULL),
(14, 'saied', 'hamza', 'jmail.hamza@gmail.com', '12345678', 3, '$2a$10$GB5Jlf2gMIboeXEuPo34NejDdh3FYwqFFuTAa4wb6phbmFImJHt/m', 1, '2025-05-05 08:27:46', NULL),
(16, 'abc', 'az', 'a@g.c', '12345667', 1, '$2a$10$Mre7.sby6eSmNj1EmsL1R.olr8Rfhr3ktsfKh/MxlWtPjdEOOv33O', 1, '2025-05-10 14:14:53', NULL),
(17, 'ferjani', 'mariem', 'mariem@gmail.com', '123456789', 3, '$2a$10$AJ9oD0AahAZm0q/LGhgiROfuIE11NnSaLds58RpnGpoIuU8s4wgf6', 1, '2025-05-10 14:25:37', NULL),
(18, 'client', 'client', 'a@b.c', '12345678', 7, '$2a$10$NyHpUx2mUgEhuuST0AEITuuy39KIE27HTxao0fWb722zsjTRjC7c6', 1, '2025-05-10 14:51:23', NULL);

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
