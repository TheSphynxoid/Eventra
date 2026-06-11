package org.example.services;

import org.example.dao.UserDAO;
import org.example.dao.RoleDAO;
import org.example.entities.User;
import org.example.entities.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import jakarta.mail.MessagingException;

public class AuthService {
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final MailService mailService;
    private final Connection conn;

    public AuthService(Connection conn, MailService mailService) {
        this.conn = conn;
        this.userDAO = new UserDAO(conn);
        this.roleDAO = new RoleDAO(conn);
        this.mailService = mailService;
    }

    public MailService getMailService() {
        return mailService;
    }

    public Optional<User> login(String email, String password) throws SQLException {
        Optional<User> opt = userDAO.findByEmail(email);
        if (opt.isEmpty()) {
            System.out.println("[AuthService] Aucun utilisateur trouvé pour : " + email);
            return Optional.empty();
        }

        User u = opt.get();
        System.out.println("[AuthService] Utilisateur trouvé : " + u.getEmail() +
                " (actif=" + u.isActive() + "), hash enregistré=" + u.getMotDePasse());

        if (!BCrypt.checkpw(password, u.getMotDePasse())) {
            System.out.println("[AuthService] Mot de passe incorrect pour : " + email);
            return Optional.empty();
        }

        return Optional.of(u);
    }

    public void signup(User user) throws SQLException {
        if (!user.getMotDePasse().equals(user.getConfirmerMotDePasse())) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
        }
        if (userDAO.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }

        String hashed = BCrypt.hashpw(user.getMotDePasse(), BCrypt.gensalt());
        user.setMotDePasse(hashed);
        user.setActive(false);

        Role pending = roleDAO.findByName("En_attente");
        if (pending == null) {
            throw new IllegalStateException("Rôle 'En_attente' introuvable");
        }
        user.setRole(pending);

        userDAO.save(user);

        try {
            mailService.sendSimple(
                    user.getEmail(),
                    "Inscription reçue",
                    "Bonjour " + user.getNom() + ",\n\n"
                            + "Votre inscription a bien été reçue. Un administrateur activera votre compte.\n\n"
                            + "Cordialement."
            );
        } catch (MessagingException me) {
            me.printStackTrace();
        }

        List<User> admins = userDAO.findAllByRoleName("admin");
        for (User admin : admins) {
            try {
                mailService.sendSimple(
                        admin.getEmail(),
                        "Nouvelle inscription",
                        "Un nouvel utilisateur (" + user.getEmail() + ") vient de s'inscrire.\n"
                                + "Veuillez valider son compte."
                );
            } catch (MessagingException me) {
                me.printStackTrace();
            }
        }
    }

    /**
     * Retourne un administrateur (le premier trouvé).
     */
    public User findAnyAdmin() {
        try {
            List<User> admins = userDAO.findAllByRoleName("admin");
            if (!admins.isEmpty()) {
                return admins.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Optional<User> findByEmail(String email) throws SQLException {
        return userDAO.findByEmail(email);
    }

}
