package org.example.entities;

import java.sql.Date;


public class SponsorDemande {
    private int id;
    private int id_organisateur;
    private int id_event;
    private int id_sponsor;
    private DemandeStatus status;
    private float montant;
    private Date date_creation;
    private Date date_acceptance;
    private String description;

    public SponsorDemande() {
    }

    public int getId_organisateur() {
        return id_organisateur;
    }

    public void setId_organisateur(int id_organisateur) {
        this.id_organisateur = id_organisateur;
    }

    public enum DemandeStatus{
        ACCEPTED, WAITING
    }

    public SponsorDemande(int id_organisateur ,int id_event, int id_sponsor, DemandeStatus status, float montant, Date date_creation, Date date_acceptance) {
        this.id_event = id_event;
        this.id_sponsor = id_sponsor;
        this.status = status;
        this.montant = montant;
        this.date_creation = date_creation;
        this.date_acceptance = date_acceptance;
        this.id_organisateur = id_organisateur;
    }

    // ... getters and setters ...
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Date getDate_acceptance() {
        return date_acceptance;
    }

    public void setDate_acceptance(Date date_acceptance) {
        this.date_acceptance = date_acceptance;
    }


    public float getMontant() {
        return montant;
    }

    public void setMontant(float montant) {
        this.montant = montant;
    }

    public DemandeStatus getStatus() {
        return status;
    }

    public void setStatus(DemandeStatus status) {
        this.status = status;
    }

    public Date getDate_creation() {
        return date_creation;
    }

    public void setDate_creation(Date date_creation) {
        this.date_creation = date_creation;
    }

    public int getId_sponsor() {
        return id_sponsor;
    }

    public void setId_sponsor(int id_sponsor) {
        this.id_sponsor = id_sponsor;
    }

    public int getId_event() {
        return id_event;
    }

    public void setId_event(int id_event) {
        this.id_event = id_event;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
