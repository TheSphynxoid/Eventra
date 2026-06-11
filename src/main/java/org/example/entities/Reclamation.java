package org.example.entities;

import java.time.LocalDate;

public class Reclamation {
    private int id;
    private String id_client;
    private String description;
    private String type;
    private LocalDate dateReclamation;
    private String etat;
    private String email; // 👈 Nouveau champ ajouté

    // Constructeur avec id (pour modification)
    public Reclamation(int id, String id_client, String description, LocalDate dateReclamation, String etat, String email) {
        this.id = id;
        this.id_client = id_client;
        this.description = description;
        this.dateReclamation = dateReclamation;
        this.etat = etat;
        this.email = email;
    }

    // Constructeur sans id (pour l'ajout)
    public Reclamation(String id_client, String description, LocalDate dateReclamation, String etat, String email) {
        this.id_client = id_client;
        this.description = description;
        this.dateReclamation = dateReclamation;
        this.etat = etat;
        this.email = email;
    }

    public Reclamation() {
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getId_client() {
        return id_client;
    }

    public void setId_client(String id_client) {
        this.id_client = id_client;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateReclamation() {
        return dateReclamation;
    }

    public void setDateReclamation(LocalDate dateReclamation) {
        this.dateReclamation = dateReclamation;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", id_client='" + id_client + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", dateReclamation=" + dateReclamation +
                ", etat='" + etat + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
