package org.example.services;

import org.example.entities.Sponsor;
import org.example.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SponsorService implements ICrud<Sponsor> {

    private Connection connection;

    public SponsorService() {
        this.connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Sponsor sponsor) throws SQLException {
        String query = "INSERT INTO sponsors (nom_entreprise, secteur_activite, email, telephone, "
                + "budget, mot_de_passe, conditions_acceptees) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sponsor.getNomEntreprise());
            ps.setString(2, sponsor.getSecteurActivite());
            ps.setString(3, sponsor.getEmail());
            ps.setString(4, sponsor.getTelephone());
            ps.setDouble(5, sponsor.getBudget());
            ps.setString(6, sponsor.getMotDePasse());
            ps.setBoolean(7, sponsor.isConditionsAcceptees());

            ps.executeUpdate();

            // Récupération de l'ID généré
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    sponsor.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void modifier(Sponsor sponsor) throws SQLException {
        String query = "UPDATE sponsors SET nom_entreprise = ?, secteur_activite = ?, email = ?, "
                + "telephone = ?, budget = ?, mot_de_passe = ?, conditions_acceptees = ? "
                + "WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, sponsor.getNomEntreprise());
            ps.setString(2, sponsor.getSecteurActivite());
            ps.setString(3, sponsor.getEmail());
            ps.setString(4, sponsor.getTelephone());
            ps.setDouble(5, sponsor.getBudget());
            ps.setString(6, sponsor.getMotDePasse());
            ps.setBoolean(7, sponsor.isConditionsAcceptees());
            ps.setInt(8, sponsor.getId());

            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM sponsors WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Sponsor> afficher() throws SQLException {
        List<Sponsor> sponsors = new ArrayList<>();
        String query = "SELECT * FROM sponsors";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Sponsor sponsor = new Sponsor();
                sponsor.setId(rs.getInt("id"));
                sponsor.setNomEntreprise(rs.getString("nom_entreprise"));
                sponsor.setSecteurActivite(rs.getString("secteur_activite"));
                sponsor.setEmail(rs.getString("email"));
                sponsor.setTelephone(rs.getString("telephone"));
                sponsor.setBudget(rs.getDouble("budget"));
                sponsor.setMotDePasse(rs.getString("mot_de_passe"));
                sponsor.setDateCreation(rs.getTimestamp("date_creation"));
                sponsor.setConditionsAcceptees(rs.getBoolean("conditions_acceptees"));

                sponsors.add(sponsor);
            }
        }

        return sponsors;
    }

    @Override
    public List<Sponsor> getAll() throws SQLException{
        return afficher();
    }

    // Méthodes supplémentaires spécifiques aux sponsors
    public Sponsor findByEmail(String email) throws SQLException {
        String query = "SELECT * FROM sponsors WHERE email = ?";
        Sponsor sponsor = null;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sponsor = new Sponsor();
                    sponsor.setId(rs.getInt("id"));
                    sponsor.setNomEntreprise(rs.getString("nom_entreprise"));
                    sponsor.setSecteurActivite(rs.getString("secteur_activite"));
                    sponsor.setEmail(rs.getString("email"));
                    sponsor.setTelephone(rs.getString("telephone"));
                    sponsor.setBudget(rs.getDouble("budget"));
                    sponsor.setMotDePasse(rs.getString("mot_de_passe"));
                    sponsor.setDateCreation(rs.getTimestamp("date_creation"));
                    sponsor.setConditionsAcceptees(rs.getBoolean("conditions_acceptees"));
                }
            }
        }

        return sponsor;
    }

    public List<Sponsor> findBySecteur(String secteur) throws SQLException {
        List<Sponsor> sponsors = new ArrayList<>();
        String query = "SELECT * FROM sponsors WHERE secteur_activite = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, secteur);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sponsor sponsor = new Sponsor();
                    sponsor.setId(rs.getInt("id"));
                    sponsor.setNomEntreprise(rs.getString("nom_entreprise"));
                    sponsor.setSecteurActivite(rs.getString("secteur_activite"));
                    sponsor.setEmail(rs.getString("email"));
                    sponsor.setTelephone(rs.getString("telephone"));
                    sponsor.setBudget(rs.getDouble("budget"));
                    sponsor.setMotDePasse(rs.getString("mot_de_passe"));
                    sponsor.setDateCreation(rs.getTimestamp("date_creation"));
                    sponsor.setConditionsAcceptees(rs.getBoolean("conditions_acceptees"));

                    sponsors.add(sponsor);
                }
            }
        }

        return sponsors;
    }
}