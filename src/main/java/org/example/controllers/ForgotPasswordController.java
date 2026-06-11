package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import org.example.MainApp;
import org.example.services.MailService;

import java.util.UUID;

public class ForgotPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleSendResetLink() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            messageLabel.setText("Veuillez entrer votre email.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            // Générer un token (tu peux aussi le stocker en base)
            String token = UUID.randomUUID().toString();
            String resetLink = "http://localhost:8080/reset?token=" + token;

            // Appeler MailService via AuthService → MainApp
            MailService mailService = MainApp.AUTH_SERVICE.getMailService();

            mailService.sendSimple(
                    email,
                    "Réinitialisation de votre mot de passe",
                    "Bonjour,\n\nCliquez sur le lien suivant pour réinitialiser votre mot de passe :\n" +
                            resetLink + "\n\nCe lien est valide pendant 24h."
            );

            messageLabel.setText("Lien envoyé à votre adresse email.");
            messageLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Erreur lors de l'envoi de l'e-mail.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Erreur lors du retour.");
        }
    }
}
