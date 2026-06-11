package org.example.controllers;

import org.example.entities.Evenement;
import org.example.services.EvenementsServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ParticipantController {

    @FXML private ListView<Evenement> lvEvenements;
    @FXML private ComboBox<String> cbCategories;
    @FXML private Label lblTitre;
    @FXML private Label lblDescription;
    @FXML private Label lblDates;
    @FXML private Label lblAdresse;
    @FXML private Label lblCapacite;
    @FXML private VBox detailsContainer;

    private ObservableList<Evenement> tousLesEvenements;
    private FilteredList<Evenement> evenementsFiltres;

    @FXML
    public void initialize() {
        // Style initial
        detailsContainer.setVisible(false);
        detailsContainer.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-border-color: #dee2e6; -fx-border-width: 1; -fx-border-radius: 5;");

        // Chargement des données
        chargerEvenements();

        // Configuration de la ListView
        configurerListView();

        // Initialiser les catégories
        initialiserCategories();
        
    }

    private void configurerListView() {
        lvEvenements.setCellFactory(param -> new ListCell<Evenement>() {
            @Override
            protected void updateItem(Evenement evenement, boolean empty) {
                super.updateItem(evenement, empty);
                if (empty || evenement == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(evenement.getTitre());
                    setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 5;");
                }
            }
        });

        // Gestion de la sélection
        lvEvenements.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                afficherDetailsEvenement(newVal);
            }
        });
    }

    private void afficherDetailsEvenement(Evenement evenement) {
        detailsContainer.setVisible(true);
        lblTitre.setText(evenement.getTitre());
        lblDescription.setText(evenement.getDescription());
        lblDates.setText("Du " + evenement.getDateDebut() + " au " + evenement.getDateFin());
        lblAdresse.setText(evenement.getAdresse());
        lblCapacite.setText("Capacité: " + evenement.getCapacite() + " personnes");
    }

    private void chargerEvenements() {
        EvenementsServices es = new EvenementsServices();
        try {
            tousLesEvenements = FXCollections.observableArrayList(es.afficher());
            evenementsFiltres = new FilteredList<>(tousLesEvenements, p -> true);
            lvEvenements.setItems(evenementsFiltres);
        } catch (SQLException e) {
            afficherErreur("Erreur de chargement", "Impossible de charger les événements: " + e.getMessage());
        }
    }

    private void initialiserCategories() {
        List<String> categories = tousLesEvenements.stream()
                .map(Evenement::getCategorie)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        categories.add(0, "Toutes les catégories");
        cbCategories.setItems(FXCollections.observableArrayList(categories));
        cbCategories.getSelectionModel().selectFirst();
    }

    @FXML
    private void filtrerParCategorie() {
        String categorieSelectionnee = cbCategories.getSelectionModel().getSelectedItem();

        if (categorieSelectionnee == null || "Toutes les catégories".equals(categorieSelectionnee)) {
            evenementsFiltres.setPredicate(evenement -> true);
        } else {
            evenementsFiltres.setPredicate(evenement ->
                    categorieSelectionnee.equals(evenement.getCategorie())
            );
        }
    }

    @FXML
    private void participerEvenement() throws IOException {
        Evenement selected = lvEvenements.getSelectionModel().getSelectedItem();
        if (selected != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Rsvp.fxml"));
            Parent root = loader.load();

            TicketController controller = loader.getController();
            controller.SetEventement(selected);
            // Create new scene and stage
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Confirmer Ticket");
            stage.show();
        } else {
            afficherErreur("Aucune sélection", "Veuillez sélectionner un événement à rejoindre");
        }
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}