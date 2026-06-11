package org.example.controllers;

import org.example.entities.Reclamation;
import org.example.services.ReclamationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.controllers.ModifierReclamationController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GererReclamationsController {

    @FXML
    private TextField emailSearchField;

    @FXML private TableView<Reclamation> tableView;
    @FXML private TableColumn<Reclamation, Integer> colId;
    @FXML private TableColumn<Reclamation, String> colClient;
    @FXML private TableColumn<Reclamation, String> colEmail;
    @FXML private TableColumn<Reclamation, String> colType;
    @FXML private TableColumn<Reclamation, String> colDescription;
    @FXML private TableColumn<Reclamation, String> colDate;
    @FXML private TableColumn<Reclamation, String> colEtat;

    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;

    private final ReclamationService service = new ReclamationService();
    private ObservableList<Reclamation> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("id_client"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDateReclamation() != null) {
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDateReclamation().toString());
            } else {
                return new javafx.beans.property.SimpleStringProperty("");
            }
        });
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));

        // Désactiver les boutons au démarrage
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
    }

    @FXML
    private void filtrerParEmail() {
        String emailSaisi = emailSearchField.getText().trim();

        if (emailSaisi.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Veuillez entrer un Email.");
            return;
        }

        try {
            List<Reclamation> list = service.afficher();
            data.clear();

            for (Reclamation r : list) {
                if (r.getEmail().equalsIgnoreCase(emailSaisi)) {
                    data.add(r);
                }
            }

            tableView.setItems(data);
            boolean hasResults = !data.isEmpty();
            btnModifier.setDisable(!hasResults);
            btnSupprimer.setDisable(!hasResults);

            if (!hasResults) {
                showAlert(Alert.AlertType.INFORMATION, "Aucune réclamation trouvée pour cet Email.");
            } else {
                System.out.println("📧 Réclamations trouvées pour l'Email : " + emailSaisi + " - Total : " + data.size());
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du filtrage : " + e.getMessage());
        }
    }

    @FXML
    private void modifierReclamation() {
        Reclamation selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sélectionnez une réclamation à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierReclamation.fxml"));
            Parent root = loader.load();
            ModifierReclamationController controller = loader.getController();
            controller.setReclamation(selected);

            Stage stage = new Stage();
            stage.setTitle("Modifier Réclamation");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            filtrerParEmail(); // Rafraîchir après modification

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de modification : " + e.getMessage());
        }
    }

    @FXML
    private void supprimerReclamation() {
        Reclamation selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Supprimer cette réclamation ?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        service.supprimer(selected.getId());
                        filtrerParEmail(); // Rafraîchir après suppression
                        showAlert(Alert.AlertType.INFORMATION, "Réclamation supprimée.");
                    } catch (SQLException e) {
                        showAlert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner une réclamation.");
        }
    }

    @FXML
    private void retourAjout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReclamation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Réclamation");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
