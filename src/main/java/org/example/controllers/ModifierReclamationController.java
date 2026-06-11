package org.example.controllers;

import org.example.entities.Reclamation;
import org.example.services.ReclamationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ModifierReclamationController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField idClientField;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextArea descriptionField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> etatCombo;

    private Reclamation selectedReclamation;

    private final ReclamationService reclamationService = new ReclamationService();

    @FXML
    public void initialize() {
        // Initialisation des types de réclamation
        typeComboBox.getItems().addAll("Technique", "Service", "Facturation");

        // Désactiver la modification de l'état
        etatCombo.setDisable(true);
    }

    public void setReclamation(Reclamation r) {
        this.selectedReclamation = r;

        // Remplir les champs
        emailField.setText(r.getEmail());
        emailField.setDisable(true);  // Bloqué

        idClientField.setText(r.getId_client());
        idClientField.setDisable(true);  // Bloqué

        typeComboBox.setValue(r.getType());
        descriptionField.setText(r.getDescription());
        datePicker.setValue(r.getDateReclamation());

        etatCombo.setValue(r.getEtat());
        etatCombo.setDisable(true);  // Déjà désactivé par initialize
    }

    @FXML
    private void enregistrerModification(ActionEvent event) {
        try {
            // Email, ID Client, et État sont bloqués donc pas modifiés
            selectedReclamation.setType(typeComboBox.getValue());
            selectedReclamation.setDescription(descriptionField.getText().trim());
            selectedReclamation.setDateReclamation(datePicker.getValue());

            reclamationService.modifier(selectedReclamation);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la modification");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void fermerFenetre(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
