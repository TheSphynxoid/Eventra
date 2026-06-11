package com.thesphynx.services;

import com.thesphynx.entities.Evenement;
import com.thesphynx.utils.DB;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EvenementsServices implements IService<Evenement>, Initialisable {
    private final Connection connection;
    private boolean initialise = false;

    public EvenementsServices() {
        connection = DB.getInstance().getConnection();
    }

    @Override
    public void initialiser() throws InitialisationException {
        try {
            if (connection == null || connection.isClosed()) {
                throw new InitialisationException("La connexion à la base de données n'est pas disponible");
            }

            try (Statement stmt = connection.createStatement()) {
                stmt.executeQuery("SELECT 1 FROM evenement LIMIT 1");
            }

            initialise = true;
        } catch (SQLException e) {
            throw new InitialisationException("Échec de l'initialisation du service des événements", e);
        }
    }

    @Override
    public boolean estInitialise() {
        return initialise;
    }

    @Override
    public void ajouter(Evenement evenement) throws SQLException {
        String req = "INSERT INTO evenement (titre, description, dateDebut, dateFin, id_organisateur, latitude, longitude, adresse, categorie, visibilite, capacite) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, evenement.getTitre());
            pst.setString(2, evenement.getDescription());
            pst.setTimestamp(3, Timestamp.valueOf(evenement.getDateDebut()));
            pst.setTimestamp(4, Timestamp.valueOf(evenement.getDateFin()));
            pst.setInt(5, evenement.getId_organisateur());
            pst.setString(6, evenement.getLatitude());
            pst.setString(7, evenement.getLongitude());
            pst.setString(8, evenement.getAdresse());
            pst.setString(9, evenement.getCategorie());
            pst.setString(10, evenement.getVisibilite());
            pst.setInt(11, evenement.getCapacite());

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de la création, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    evenement.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Échec de la création, aucun ID obtenu.");
                }
            }
        }
    }

    @Override
    public void supprimer(Evenement evenement) throws SQLException {
        supprimer(evenement.getId());
    }

    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM evenement WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, id);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucun événement trouvé avec l'ID: " + id);
            }
        }
    }

    @Override
    public void modifier(Evenement evenement) throws SQLException {
        String req = "UPDATE evenement SET titre = ?, description = ?, dateDebut = ?, dateFin = ?, id_organisateur = ?, latitude = ?, longitude = ?, adresse = ?, categorie = ?, visibilite = ?, capacite = ? WHERE id = ?";

        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, evenement.getTitre());
            pst.setString(2, evenement.getDescription());
            pst.setTimestamp(3, Timestamp.valueOf(evenement.getDateDebut()));
            pst.setTimestamp(4, Timestamp.valueOf(evenement.getDateFin()));
            pst.setInt(5, evenement.getId_organisateur());
            pst.setString(6, evenement.getLatitude());
            pst.setString(7, evenement.getLongitude());
            pst.setString(8, evenement.getAdresse());
            pst.setString(9, evenement.getCategorie());
            pst.setString(10, evenement.getVisibilite());
            pst.setInt(11, evenement.getCapacite());
            pst.setInt(12, evenement.getId());

            if (pst.executeUpdate() == 0) {
                throw new SQLException("Aucun événement trouvé avec l'ID: " + evenement.getId());
            }
        }
    }

    @Override
    public List<Evenement> afficher() throws SQLException {
        return getAll();
    }

    @Override
    public List<Evenement> getAll() throws SQLException {
        List<Evenement> evenements = new ArrayList<>();
        String req = "SELECT * FROM evenement ORDER BY dateDebut ASC";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                evenements.add(mapResultSetToEvenement(rs));
            }
        }
        return evenements;
    }

    public Evenement getById(int id) throws SQLException {
        String req = "SELECT * FROM evenement WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvenement(rs);
                }
            }
        }
        return null;
    }

    private Evenement mapResultSetToEvenement(ResultSet rs) throws SQLException {
        return new Evenement(
                rs.getInt("id"),
                rs.getString("titre"),
                rs.getString("description"),
                rs.getTimestamp("dateDebut").toLocalDateTime(),
                rs.getTimestamp("dateFin").toLocalDateTime(),
                rs.getInt("id_organisateur"),
                rs.getString("latitude"),
                rs.getString("longitude"),
                rs.getString("adresse"),
                rs.getString("categorie"),
                rs.getString("visibilite"),
                rs.getInt("capacite")
        );
    }
}