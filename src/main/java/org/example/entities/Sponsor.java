package org.example.entities;

import java.util.Date;

public class Sponsor {
    private int id;
    private String nomEntreprise;
    private String secteurActivite;
    private String email;
    private String telephone;
    private double budget;
    private String motDePasse;
    private Date dateCreation;
    private boolean conditionsAcceptees;

    // Constructeurs
    public Sponsor() {
    }

    public Sponsor(String nomEntreprise, String secteurActivite, String email,
                   String telephone, double budget, String motDePasse,
                   boolean conditionsAcceptees) {
        this.nomEntreprise = nomEntreprise;
        this.secteurActivite = secteurActivite;
        this.email = email;
        this.telephone = telephone;
        this.budget = budget;
        this.motDePasse = motDePasse;
        this.conditionsAcceptees = conditionsAcceptees;
        this.dateCreation = new Date();
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomEntreprise() {
        return nomEntreprise;
    }

    public void setNomEntreprise(String nomEntreprise) {
        this.nomEntreprise = nomEntreprise;
    }

    public String getSecteurActivite() {
        return secteurActivite;
    }

    public void setSecteurActivite(String secteurActivite) {
        this.secteurActivite = secteurActivite;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isConditionsAcceptees() {
        return conditionsAcceptees;
    }

    public void setConditionsAcceptees(boolean conditionsAcceptees) {
        this.conditionsAcceptees = conditionsAcceptees;
    }

    @Override
    public String toString() {
        return "Sponsor{" +
                "id=" + id +
                ", nomEntreprise='" + nomEntreprise + '\'' +
                ", secteurActivite='" + secteurActivite + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", budget=" + budget +
                ", dateCreation=" + dateCreation +
                ", conditionsAcceptees=" + conditionsAcceptees +
                '}';
    }
}