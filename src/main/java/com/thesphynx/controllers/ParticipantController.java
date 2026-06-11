package com.thesphynx.controllers;

import com.thesphynx.entities.Evenement;
import com.thesphynx.services.EvenementsServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ParticipantController {

    @FXML private TableView<Evenement> tvEvenements;
    @FXML private TableColumn<Evenement, String> colTitre;
    @FXML private TableColumn<Evenement, String> colDescription;
    @FXML private TableColumn<Evenement, String> colDateDebut;
    @FXML private TableColumn<Evenement, String> colDateFin;
    @FXML private TableColumn<Evenement, String> colCategorie;
    @FXML private TableColumn<Evenement, Void> colActions;
    @FXML private ComboBox<String> cbCategories;

    private ObservableList<Evenement> tousLesEvenements;
    private FilteredList<Evenement> evenementsFiltres;

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));

        // Ajout de la colonne d'action (participer)
        ajouterColonneAction();

        // Chargement des données
        chargerEvenements();

        // Initialiser la ComboBox des catégories
        initialiserCategories();
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

    private void ajouterColonneAction() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnParticiper = new Button("Participer");

            {
                btnParticiper.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                btnParticiper.setOnAction(event -> {
                    Evenement evenement = getTableView().getItems().get(getIndex());
                    participerEvenement(evenement);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnParticiper);
                }
            }
        });
    }

    private void chargerEvenements() {
        EvenementsServices es = new EvenementsServices();
        try {
            tousLesEvenements = FXCollections.observableArrayList(es.afficher());
            evenementsFiltres = new FilteredList<>(tousLesEvenements, p -> true);
            tvEvenements.setItems(evenementsFiltres);
        } catch (SQLException e) {
            afficherErreur("Erreur de chargement", "Impossible de charger les événements: " + e.getMessage());
        }
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
    private void reinitialiserFiltre() {
        cbCategories.getSelectionModel().selectFirst();
        evenementsFiltres.setPredicate(evenement -> true);
    }

    private void participerEvenement(Evenement evenement) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Participation");
        alert.setHeaderText(null);
        alert.setContentText("Vous êtes inscrit à l'événement: " + evenement.getTitre());
        alert.showAndWait();
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void retourAccueil() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/thesphynx/Home.fxml"));
            Stage stage = (Stage) tvEvenements.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur de navigation", "Impossible de retourner à l'accueil: " + e.getMessage());
        }
    }
}