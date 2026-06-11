package org.example.services;

import org.example.entities.Reclamation;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService implements ICrud<Reclamation> {

    Connection con;

    public ReclamationService() {
        con = MyDataBase.getInstance().getConnection();
    }

    // ✅ AJOUT avec email
    public void ajouter(Reclamation r) throws SQLException {
        if (con == null || con.isClosed()) {
            throw new SQLException("La connexion à la base de données est fermée.");
        }

        String req = "INSERT INTO reclamation (id_client, description, type, date_reclamation, etat, email) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = con.prepareStatement(req)) {
            statement.setString(1, r.getId_client());
            statement.setString(2, r.getDescription());
            statement.setString(3, r.getType());
            statement.setDate(4, Date.valueOf(r.getDateReclamation()));
            statement.setString(5, r.getEtat());
            statement.setString(6, r.getEmail()); // 👈 nouveau champ
            statement.executeUpdate();
            System.out.println("Réclamation ajoutée avec succès.");
        }
    }

    // ✅ MODIFIER avec email (optionnel, à activer si tu modifies aussi l’e-mail dans l’UI)
    @Override
    public void modifier(Reclamation r) throws SQLException {
        if (con == null || con.isClosed()) {
            throw new SQLException("La connexion à la base de données est fermée.");
        }

        String req = "UPDATE reclamation SET id_client=?, description=?, type=?, date_reclamation=?, etat=?, email=? WHERE id=?";

        try (PreparedStatement statement = con.prepareStatement(req)) {
            statement.setString(1, r.getId_client());
            statement.setString(2, r.getDescription());
            statement.setString(3, r.getType());
            statement.setDate(4, Date.valueOf(r.getDateReclamation()));
            statement.setString(5, r.getEtat());
            statement.setString(6, r.getEmail()); // 👈 nouveau champ
            statement.setInt(7, r.getId());
            statement.executeUpdate();
            System.out.println("Réclamation modifiée avec succès.");
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM reclamation WHERE id=?";
        PreparedStatement statement = con.prepareStatement(req);
        statement.setInt(1, id);
        statement.executeUpdate();
        System.out.println("Réclamation supprimée");
    }

    // ✅ AFFICHER toutes les réclamations avec email

    public List<Reclamation> afficher() throws SQLException {
        List<Reclamation> reclamations = new ArrayList<>();
        String req = "SELECT * FROM reclamation";
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            Reclamation r = new Reclamation();
            r.setId(rs.getInt("id"));
            r.setId_client(rs.getString("id_client"));
            r.setType(rs.getString("type"));
            r.setDescription(rs.getString("description"));
            r.setDateReclamation(rs.getDate("date_reclamation").toLocalDate());
            r.setEtat(rs.getString("etat"));
            r.setEmail(rs.getString("email")); // 👈 important
            reclamations.add(r);
        }
        return reclamations;
    }

    // ✅ Récupérer les réclamations pour un utilisateur spécifique par email
    public List<Reclamation> getReclamationsParEmail(String email) throws SQLException {
        List<Reclamation> list = new ArrayList<>();
        String req = "SELECT * FROM reclamation WHERE email = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reclamation r = new Reclamation();
                r.setId(rs.getInt("id"));
                r.setId_client(rs.getString("id_client"));
                r.setEmail(rs.getString("email"));
                r.setType(rs.getString("type"));
                r.setDescription(rs.getString("description"));
                r.setDateReclamation(rs.getDate("date_reclamation").toLocalDate());
                r.setEtat(rs.getString("etat"));
                list.add(r);
            }
        }
        return list;
    }

    // ✅ Optionnel : getAll avec email aussi
    @Override
    public List<Reclamation> getAll() throws SQLException {
        return afficher(); // ou garde ta propre logique si différente
    }
}
