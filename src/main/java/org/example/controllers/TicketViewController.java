package org.example.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.entities.Ticket;
import org.example.services.TicketService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TicketViewController implements Initializable {

    @FXML
    private TableView<Ticket> ticketsTable;

    private TicketService ticketService;
    private ObservableList<Ticket> ticketsData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ticketService = new TicketService();
        refreshTable();
    }

    private void refreshTable() {
        try {
            ticketsData = FXCollections.observableArrayList(ticketService.afficher());
            ticketsTable.setItems(ticketsData);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les tickets", e.getMessage(), AlertType.ERROR);
        }
    }

    @FXML
    private void handleEditTicket() throws IOException {
        Ticket selected = ticketsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Rsvp.fxml"));
            Parent root = loader.load();

            TicketController controller = loader.getController();

            controller.SetTicket(selected);

            // Create new scene and stage
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Ticket");
            stage.show();

            // Rafraîchir la table après la modification
            refreshTable();
        } else {
            showAlert("Avertissement", "Aucune sélection", "Veuillez sélectionner un ticket à modifier", AlertType.WARNING);
        }
    }

    @FXML
    private void handleDeleteTicket() {
        Ticket selected = ticketsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                ticketService.supprimer(selected.getTicketID());
                refreshTable();
                showAlert("Succès", "Suppression réussie", "Le ticket a été supprimé avec succès", AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur", "Échec de la suppression", e.getMessage(), AlertType.ERROR);
            }
        } else {
            showAlert("Avertissement", "Aucune sélection", "Veuillez sélectionner un ticket à supprimer", AlertType.WARNING);
        }
    }

    private void showAlert(String title, String header, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}