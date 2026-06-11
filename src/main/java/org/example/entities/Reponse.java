package org.example.entities;
import java.time.LocalDateTime;

public class Reponse {
    private int id;
    private int idReclamation;
    private String contenu;
    private LocalDateTime dateReponse;

    // Constructeurs
    public Reponse() {}

    public Reponse(int idReclamation, String contenu, LocalDateTime dateReponse) {
        this.idReclamation = idReclamation;
        this.contenu = contenu;
        this.dateReponse = dateReponse;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdReclamation() {
        return idReclamation;
    }

    public void setIdReclamation(int idReclamation) {
        this.idReclamation = idReclamation;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDateReponse() {
        return dateReponse;
    }

    public void setDateReponse(LocalDateTime dateReponse) {
        this.dateReponse = dateReponse;
    }
}
