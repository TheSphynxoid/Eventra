package org.example.controllers;

import org.example.entities.Evenement;
import org.example.services.EvenementsServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public class AfficherEventController {
    @FXML
    private TableView<Evenement> tvEvenements;
    @FXML
    private TableColumn<Evenement, String> colTitre;
    @FXML
    private TableColumn<Evenement, String> colDescription;
    @FXML
    private TableColumn<Evenement, LocalDateTime> colDateDebut;
    @FXML
    private TableColumn<Evenement, LocalDateTime> colDateFin;
    @FXML
    private TableColumn<Evenement, String> colAdresse;
    @FXML
    private TableColumn<Evenement, String> colCategorie;
    @FXML
    private TableColumn<Evenement, String> colVisibilite;
    @FXML
    private TableColumn<Evenement, Integer> colCapacite;

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colVisibilite.setCellValueFactory(new PropertyValueFactory<>("visibilite"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));

        // Ajout de la colonne des actions
        addActionsColumn();

        // Charger les données depuis la base de données
        loadEvenements();
    }

    private void addActionsColumn() {
        TableColumn<Evenement, Void> colActions = new TableColumn<>("Actions");

        Callback<TableColumn<Evenement, Void>, TableCell<Evenement, Void>> cellFactory =
                new Callback<TableColumn<Evenement, Void>, TableCell<Evenement, Void>>() {
                    @Override
                    public TableCell<Evenement, Void> call(final TableColumn<Evenement, Void> param) {
                        return new TableCell<Evenement, Void>() {
                            private final HBox buttons = new HBox(5);
                            {
                                buttons.setAlignment(Pos.CENTER);

                                // Bouton Modifier
                                Button btnEdit = createActionButton("/images/edit.png");
                                btnEdit.setOnAction(event -> {
                                    Evenement evenement = getTableView().getItems().get(getIndex());
                                    chargerInterfaceModification(evenement);
                                });

                                // Bouton Supprimer
                                Button btnDelete = createActionButton("/images/delete.png");
                                btnDelete.setOnAction(event -> {
                                    Evenement evenement = getTableView().getItems().get(getIndex());
                                    supprimerEvenement(evenement);
                                });

                                buttons.getChildren().addAll(btnEdit, btnDelete);
                            }

                            @Override
                            protected void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                setGraphic(empty ? null : buttons);
                            }
                        };
                    }
                };

        colActions.setCellFactory(cellFactory);
        tvEvenements.getColumns().add(colActions);
    }

    private Button createActionButton(String imagePath) {
        Button button = new Button();
        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
            icon.setFitHeight(16);
            icon.setFitWidth(16);
            button.setGraphic(icon);
            button.setStyle("-fx-background-color: transparent;");
        } catch (Exception e) {
            button.setText(imagePath.contains("edit") ? "Mod" : "Supp");
            System.err.println("Erreur de chargement de l'icône: " + imagePath);
        }
        return button;
    }

    private void chargerInterfaceModification(Evenement evenement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEvent.fxml"));
            Parent root = loader.load();

            ModifierEventController controller = loader.getController();
            controller.setEvenementAModifier(evenement);

            Stage stage = (Stage) tvEvenements.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'interface de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void supprimerEvenement(Evenement evenement) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer l'événement \"" + evenement.getTitre() + "\" ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                EvenementsServices es = new EvenementsServices();
                es.supprimer(evenement);
                loadEvenements();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement supprimé avec succès");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la suppression: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void loadEvenements() {
        EvenementsServices es = new EvenementsServices();
        try {
            ObservableList<Evenement> observableList = FXCollections.observableArrayList(es.afficher());
            tvEvenements.setItems(observableList);
            if (observableList.isEmpty()) {
                tvEvenements.setPlaceholder(new Label("Aucun contenu dans la table"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du chargement des événements: " + e.getMessage());
            tvEvenements.setPlaceholder(new Label("Erreur de connexion à la base de données: " + e.getMessage()));
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors du chargement des événements: " + e.getMessage());
            tvEvenements.setPlaceholder(new Label("Erreur inattendue: " + e.getMessage()));
            e.printStackTrace();
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