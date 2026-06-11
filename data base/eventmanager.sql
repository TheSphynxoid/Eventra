CREATE DATABASE IF NOT EXISTS eventmanager;
USE eventmanager;

CREATE TABLE IF NOT EXISTS evenement (

                                         id INT AUTO_INCREMENT PRIMARY KEY,
                                         titre VARCHAR(100) NOT NULL,



    description TEXT,
    id_organisateur INT,
    longitude VARCHAR(100),
    latitude VARCHAR(100),
    dateDebut DATETIME NOT NULL,
    dateFin DATETIME NOT NULL,
    adresse VARCHAR(100),
    categorie VARCHAR(100),
    visibilite VARCHAR(100),
    capacite INT
    );