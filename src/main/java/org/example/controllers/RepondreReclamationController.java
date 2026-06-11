package org.example.controllers;

import org.example.entities.Reclamation;
import org.example.entities.Reponse;
import org.example.services.ReponseService;
import org.example.utils.RefreshCallback;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class RepondreReclamationController {

    @FXML
    private Label emailLabel;

    @FXML
    private TextArea reponseTextArea;

    @FXML
    private Button envoyerButton;

    private Reclamation reclamation;
    private final ReponseService reponseService = new ReponseService();

    private RefreshCallback callback;

    public void setCallback(RefreshCallback callback) {
        this.callback = callback;
    }

    public void setReclamation(Reclamation r) {
        this.reclamation = r;

        // Si r != null et que l'email est défini
        if (r != null && r.getEmail() != null) {
            emailLabel.setText("Email utilisateur : " + r.getEmail()); // Affiche l'email
        } else {
            emailLabel.setText("Email utilisateur : [non défini]"); // Si l'email n'est pas défini
        }
    }


    @FXML
    private void envoyerReponse() {
        if (reclamation == null) {
            showAlert(Alert.AlertType.ERROR, "Aucune réclamation sélectionnée.");
            return;
        }

        String contenu = reponseTextArea.getText().trim();

        if (contenu.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Le contenu de la réponse ne peut pas être vide.");
            return;
        }

        Reponse r = new Reponse();
        r.setIdReclamation(reclamation.getId());
        r.setContenu(contenu);
        r.setDateReponse(LocalDateTime.now());

        try {
            reponseService.ajouterReponse(r, reclamation.getEmail());
            showAlert(Alert.AlertType.INFORMATION, "Réponse envoyée et réclamation marquée comme traitée.");
            if (callback != null) {
                callback.rafraichir();
            }

            fermer();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'enregistrement de la réponse : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void fermer() {
        // Obtenir la fenêtre actuelle (Stage)
        Stage stage = (Stage) reponseTextArea.getScene().getWindow();
        stage.close();
    }

}
