package org.example.entities;

import java.time.LocalDateTime;

public class Evenement {
    private int id;
    private String titre;
    private String description;
    private int id_organisateur;
    private String longitude;
    private String latitude;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String adresse;
    private String categorie;
    private String visibilite;
    private int capacite;

    // Constructor for new events (without ID)
    public Evenement(String titre, String description, LocalDateTime dateDebut,
                     LocalDateTime dateFin, int id_organisateur, String latitude,
                     String longitude, String adresse, String categorie,
                     String visibilite, int capacite) {
        this.titre = titre;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.id_organisateur = id_organisateur;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adresse = adresse;
        this.categorie = categorie;
        this.visibilite = visibilite;
        this.capacite = capacite;
    }

    // Constructor for existing events (with ID)
    public Evenement(int id, String titre, String description, LocalDateTime dateDebut,
                     LocalDateTime dateFin, int id_organisateur, String latitude,
                     String longitude, String adresse, String categorie,
                     String visibilite, int capacite) {
        this(titre, description, dateDebut, dateFin, id_organisateur, latitude,
                longitude, adresse, categorie, visibilite, capacite);
        this.id = id;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public int getId_organisateur() { return id_organisateur; }
    public void setId_organisateur(int id_organisateur) { this.id_organisateur = id_organisateur; }

    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude = latitude; }

    public String getLongitude() { return longitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getVisibilite() { return visibilite; }
    public void setVisibilite(String visibilite) { this.visibilite = visibilite; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    @Override
    public String toString() {
        return "Evenement{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", id_organisateur=" + id_organisateur +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", adresse='" + adresse + '\'' +
                ", categorie='" + categorie + '\'' +
                ", visibilite='" + visibilite + '\'' +
                ", capacite=" + capacite +
                '}';
    }
}