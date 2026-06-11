package org.example.controllers;

import org.example.entities.Evenement;
import org.example.services.EvenementsServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class SupprimerEventController {

    private Evenement evenementASupprimer;

    @FXML private Label titreLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label datesLabel;

    public void setEvenementASupprimer(Evenement evenement) {
        this.evenementASupprimer = evenement;
        afficherDetailsEvenement();
    }

    private void afficherDetailsEvenement() {
        if (evenementASupprimer != null) {
            titreLabel.setText(evenementASupprimer.getTitre());
            descriptionLabel.setText(evenementASupprimer.getDescription());
            datesLabel.setText("Du " + evenementASupprimer.getDateDebut() + " au " + evenementASupprimer.getDateFin());
        }
    }

    @FXML
    void confirmerSuppression(ActionEvent event) {
        if (evenementASupprimer == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun événement sélectionné pour suppression");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer l'événement \"" +
                evenementASupprimer.getTitre() + "\" ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            EvenementsServices es = new EvenementsServices();
            try {
                es.supprimer(evenementASupprimer.getId());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "L'événement a été supprimé avec succès !");
                retourAListe(event);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void annulerSuppression(ActionEvent event) {
        retourAListe(event);
    }

    @FXML
    void retourAListe(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/thesphynx/AfficherEvent.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la liste: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}