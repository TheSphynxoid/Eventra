package org.example.controllers;

import org.example.MainApp;
import org.example.entities.SponsorDemande;
import org.example.entities.SponsorDemande.DemandeStatus;
import org.example.services.SponsorDemandeService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;

public class AddSponsorDemandeController {

    @FXML
    TextField eventIdField;

    @FXML
    TextField amountField;

    @FXML
    TextArea descriptionField;

    private SponsorDemandeService sponsorDemandeService = new SponsorDemandeService();
    private SponsorDemande modifierDemande;
    private boolean modifier = false;

    public void SetModifier(boolean b, SponsorDemande demande){
        modifier = b;
        modifierDemande = demande;
    }
    @FXML
    private void handleSubmit() {
        try {
            // Validate inputs
            if (!validateInputs()) {
                return;
            }


            if(modifier){
                modifierDemande.setId_event(Integer.parseInt(eventIdField.getText()));
                modifierDemande.setMontant(Float.parseFloat(amountField.getText()));
                modifierDemande.setDescription(descriptionField.getText());
                modifierDemande.setStatus(DemandeStatus.WAITING); // Default to Waiting
                modifierDemande.setDate_creation(Date.valueOf(LocalDate.now()));
                sponsorDemandeService.modifier(modifierDemande);
            }else{
                // Create new demande
                SponsorDemande demande = new SponsorDemande();
                demande.setId_event(Integer.parseInt(eventIdField.getText()));
                demande.setMontant(Float.parseFloat(amountField.getText()));
                demande.setDescription(descriptionField.getText());
                demande.setStatus(DemandeStatus.WAITING); // Default to Waiting
                demande.setDate_creation(Date.valueOf(LocalDate.now()));
                demande.setId_organisateur(MainApp.LAYOUT_CONTROLLER.getCurrentUser().getId());
                // Save to database
                sponsorDemandeService.ajouter(demande);
            }

            // Close the window
            closeWindow();
        } catch (Exception e) {
            showAlert("Error", "Could not save the sponsorship request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        // Validate Event ID
        try {
            int eventId = Integer.parseInt(eventIdField.getText());
            if (eventId <= 0) {
                errors.append("- Event ID must be a positive number\n");
            }
        } catch (NumberFormatException e) {
            errors.append("- Event ID must be a valid number\n");
        }

        // Validate Amount
        try {
            float amount = Float.parseFloat(amountField.getText());
            if (amount <= 0) {
                errors.append("- Amount must be greater than 0\n");
            }
        } catch (NumberFormatException e) {
            errors.append("- Amount must be a valid number\n");
        }

        // Validate Description
        if (descriptionField.getText() == null || descriptionField.getText().trim().isEmpty()) {
            errors.append("- Description is required\n");
        }

        if (errors.length() > 0) {
            showAlert("Validation Error", "Please fix the following errors:\n\n" + errors.toString());
            return false;
        }

        return true;
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) eventIdField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}