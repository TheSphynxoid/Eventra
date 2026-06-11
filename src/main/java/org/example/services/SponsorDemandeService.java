package org.example.services;

import org.example.entities.SponsorDemande;
import org.example.entities.SponsorDemande.DemandeStatus;
import org.example.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SponsorDemandeService implements ICrud<SponsorDemande> {

    @Override
    public void ajouter(SponsorDemande sponsorDemande) throws SQLException {
        String sql = "INSERT INTO demendes_sponsor (id_organisateur, id_event, id_sponsor, status, montant, date_creation, date_acceptation,description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt  = MyDataBase.getInstance().getConnection().prepareStatement(sql)) {

            stmt.setInt(1, sponsorDemande.getId_organisateur());
            stmt.setInt(2, sponsorDemande.getId_event());
            stmt.setInt(3, sponsorDemande.getId_sponsor());
            stmt.setString(4, sponsorDemande.getStatus().name());
            stmt.setFloat(5, sponsorDemande.getMontant());
            stmt.setDate(6, sponsorDemande.getDate_creation());
            stmt.setDate(7, sponsorDemande.getDate_acceptance());
            stmt.setString(8, sponsorDemande.getDescription());

            stmt.executeUpdate();
        }
    }

    @Override
    public void modifier(SponsorDemande sponsorDemande) throws SQLException {
        String sql = "UPDATE demendes_sponsor SET id_event = ?, id_sponsor = ?, status = ?, montant = ?, date_creation = ?, date_acceptation = ?, description = ? WHERE id_demande = ?";
        try (PreparedStatement stmt  = MyDataBase.getInstance().getConnection().prepareStatement(sql)) {

            stmt.setInt(1, sponsorDemande.getId_event());
            stmt.setInt(2, sponsorDemande.getId_sponsor());
            stmt.setString(3, sponsorDemande.getStatus().name());
            stmt.setFloat(4, sponsorDemande.getMontant());
            stmt.setDate(5, sponsorDemande.getDate_creation());
            stmt.setDate(6, sponsorDemande.getDate_acceptance());
            stmt.setString(7, sponsorDemande.getDescription());
            stmt.setInt(8, sponsorDemande.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM demendes_sponsor WHERE id_demande = ?";
        try (PreparedStatement stmt  = MyDataBase.getInstance().getConnection().prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<SponsorDemande> afficher() throws SQLException {
        List<SponsorDemande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demendes_sponsor";

        try (PreparedStatement stmt  = MyDataBase.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                SponsorDemande demande = new SponsorDemande();
                demande.setId(rs.getInt("id_demande"));
                demande.setId_event(rs.getInt("id_event"));
                demande.setId_sponsor(rs.getInt("id_sponsor"));
                demande.setId_organisateur(rs.getInt("id_organisateur"));
                demande.setStatus(DemandeStatus.valueOf(rs.getString("status")));
                demande.setMontant(rs.getFloat("montant"));
                demande.setDate_creation(rs.getDate("date_creation"));
                demande.setDate_acceptance(rs.getDate("date_acceptation"));
                demande.setDescription(rs.getString("description"));

                demandes.add(demande);
            }
        }
        return demandes;
    }

    @Override
    public List<SponsorDemande> getAll() throws SQLException{
        return afficher();
    }
}