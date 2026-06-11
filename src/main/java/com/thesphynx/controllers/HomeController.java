package com.thesphynx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private Button btnOrganisateur;

    @FXML
    private Button btnParticipant;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuration des actions des boutons
        setupButtonActions();
    }

    private void setupButtonActions() {
        btnOrganisateur.setOnAction(event -> handleOrganisateurClick());
        btnParticipant.setOnAction(event -> handleParticipantClick());
    }

    private void handleOrganisateurClick() {
        try {
            chargerInterfaceOrganisateur();
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation",
                    "Impossible de charger l'interface organisateur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleParticipantClick() {
        try {
            chargerInterfaceParticipant();
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation",
                    "Impossible de charger l'interface participant: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void chargerInterfaceOrganisateur() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/thesphynx/AjouterEvent.fxml"));
        Parent root = loader.load();

        Stage currentStage = (Stage) btnOrganisateur.getScene().getWindow();
        Stage newStage = new Stage();

        newStage.setScene(new Scene(root));
        newStage.setTitle("Interface Organisateur");
        newStage.centerOnScreen();

        // Fermer la fenêtre actuelle si nécessaire
        // currentStage.close();

        newStage.show();
    }

    @FXML
    private void chargerInterfaceParticipant() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/thesphynx/ParticipantInterface.fxml"));
        Parent root = loader.load();

        Stage currentStage = (Stage) btnParticipant.getScene().getWindow();
        Stage newStage = new Stage();

        newStage.setScene(new Scene(root));
        newStage.setTitle("Interface Participant");
        newStage.centerOnScreen();

        // Fermer la fenêtre actuelle si nécessaire
        // currentStage.close();

        newStage.show();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.ERROR);
    }

    private void showInfoAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.INFORMATION);
    }
}