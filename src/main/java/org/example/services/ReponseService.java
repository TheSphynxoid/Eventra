package org.example.services;

import org.example.entities.Reponse;
import org.example.utils.EmailSender;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReponseService implements ICrud<Reponse> {
    private final Connection con;

    public ReponseService() {
        this.con = MyDataBase.getInstance().getConnection();
    }


    @Override
    public void ajouter(Reponse r) throws SQLException {
        String req = "INSERT INTO reponse (id_reclamation, contenu, date_reponse) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, r.getIdReclamation());
            ps.setString(2, r.getContenu());
            ps.setTimestamp(3, Timestamp.valueOf(r.getDateReponse()));
            ps.executeUpdate();
        }
    }

    public void ajouterReponse(Reponse r, String emailUtilisateur) throws SQLException {
        ajouter(r);

        // Envoi d'email automatique
        //envoyerEmail(emailUtilisateur, "Réponse à votre réclamation", r.getContenu());

        // Envoi d'email automatique
        new EmailSender().envoyerEmail(emailUtilisateur, "Réponse à votre réclamation", r.getContenu());


        // Mise à jour de l'état de la réclamation à "Traité"
        String updateEtat = "UPDATE reclamation SET etat = 'Traité' WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(updateEtat)) {
            ps.setInt(1, r.getIdReclamation());
            ps.executeUpdate();
        }
    }

    @Override
    public void modifier(Reponse r) throws SQLException {
        String req = "UPDATE reponse SET contenu = ?, date_reponse = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setString(1, r.getContenu());
            ps.setTimestamp(2, Timestamp.valueOf(r.getDateReponse()));
            ps.setInt(3, r.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM reponse WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Reponse> afficher() throws SQLException {
        List<Reponse> list = new ArrayList<>();
        String req = "SELECT * FROM reponse";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(req)) {
            while (rs.next()) {
                Reponse r = new Reponse();
                r.setId(rs.getInt("id"));
                r.setIdReclamation(rs.getInt("id_reclamation"));
                r.setContenu(rs.getString("contenu"));
                r.setDateReponse(rs.getTimestamp("date_reponse").toLocalDateTime());
                list.add(r);
            }
        }
        return list;
    }

    public List<Reponse> getReponsesParReclamation(int idReclamation) throws SQLException {
        List<Reponse> list = new ArrayList<>();
        String req = "SELECT * FROM reponse WHERE id_reclamation = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, idReclamation);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reponse r = new Reponse();
                r.setId(rs.getInt("id"));
                r.setIdReclamation(rs.getInt("id_reclamation"));
                r.setContenu(rs.getString("contenu"));
                r.setDateReponse(rs.getTimestamp("date_reponse").toLocalDateTime());
                list.add(r);
            }
        }
        return list;
    }

    private void envoyerEmail(String to, String sujet, String contenu) {
        // Remplace par ton système d'envoi d'email réel (JavaMail, etc.)
        System.out.println("[EMAIL ENVOYÉ] À: " + to + "\nSUJET: " + sujet + "\nCONTENU: " + contenu);
    }


    @Override
    public List<Reponse> getAll() throws SQLException{
        return afficher();
    }
}
