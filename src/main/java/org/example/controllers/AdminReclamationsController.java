package org.example.controllers;

import org.example.entities.Reclamation;
import org.example.services.ReclamationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AdminReclamationsController {

    @FXML private TableView<Reclamation> tableReclamations;
    @FXML private TableColumn<Reclamation, Integer> colId;
    @FXML private TableColumn<Reclamation, String> colClient;
    @FXML private TableColumn<Reclamation, String> colEmail;
    @FXML private TableColumn<Reclamation, String> colType;
    @FXML private TableColumn<Reclamation, String> colDescription;
    @FXML private TableColumn<Reclamation, LocalDate> colDate;
    @FXML private TableColumn<Reclamation, String> colEtat;
    @FXML private TableColumn<Reclamation, Void> colAction;
    @FXML private Label infoEmail;
    @FXML private Label infoClientId;
    @FXML private Label infoTotalRec;
    @FXML private Label infoDerniereDate;
    @FXML private VBox userInfoBox;
    @FXML private Label totalLabel;
    @FXML private Label traiteLabel;
    @FXML private Label encoursLabel;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> etatFilter;

    private final ReclamationService reclamationService = new ReclamationService();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("id_client"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateReclamation"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));

        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button traiterBtn = new Button("Traiter");
            {
                traiterBtn.setOnAction(event -> {
                    Reclamation selected = getTableView().getItems().get(getIndex());
                    ouvrirInterfaceReponse(selected);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : traiterBtn);
            }
        });
        tableReclamations.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                infoEmail.setText("Email : " + selected.getEmail());
                infoClientId.setText("ID Client : " + selected.getId_client());

                try {
                    List<Reclamation> all = reclamationService.afficher();
                    long total = all.stream().filter(r -> r.getEmail().equals(selected.getEmail())).count();
                    infoTotalRec.setText("Nombre total de réclamations : " + total);

                    LocalDate lastDate = all.stream()
                            .filter(r -> r.getEmail().equals(selected.getEmail()))
                            .map(Reclamation::getDateReclamation)
                            .max(LocalDate::compareTo).orElse(null);

                    infoDerniereDate.setText("Date de dernière réclamation : " +
                            (lastDate != null ? lastDate.toString() : "-"));
                } catch (SQLException e) {
                    infoTotalRec.setText("Erreur lors du calcul.");
                    infoDerniereDate.setText("-");
                }
            } else {
                infoEmail.setText("Email : -");
                infoClientId.setText("ID Client : -");
                infoTotalRec.setText("Nombre total de réclamations : -");
                infoDerniereDate.setText("Date de dernière réclamation : -");
            }
        });


        chargerReclamations();

        // ✅ Coloration dynamique des lignes selon l'état
        tableReclamations.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Reclamation item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    String etat = item.getEtat();
                    if ("Traité".equalsIgnoreCase(etat)) {
                        setStyle("-fx-background-color: #C8E6C9;");
                    } else if ("En cours".equalsIgnoreCase(etat)) {
                        setStyle("-fx-background-color: #FFECB3;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    private void chargerReclamations() {
        try {
            List<Reclamation> list = reclamationService.afficher();
            ObservableList<Reclamation> masterData = FXCollections.observableArrayList(list);

            // 📊 Statistiques
            long total = masterData.size();
            long traite = masterData.stream().filter(r -> "Traité".equalsIgnoreCase(r.getEtat())).count();
            long enCours = masterData.stream().filter(r -> "En cours".equalsIgnoreCase(r.getEtat())).count();

            totalLabel.setText("Total : " + total);
            traiteLabel.setText("Traitées : " + traite);
            encoursLabel.setText("En cours : " + enCours);

            // 🔍 Filtrage dynamique
            FilteredList<Reclamation> filteredData = new FilteredList<>(masterData, p -> true);

            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredData.setPredicate(rec -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    String lower = newVal.toLowerCase();
                    return rec.getEmail().toLowerCase().contains(lower)
                            || rec.getType().toLowerCase().contains(lower)
                            || rec.getDescription().toLowerCase().contains(lower);
                });
            });

            etatFilter.getItems().setAll("Tous", "Traité", "En cours");
            etatFilter.setValue("Tous");

            etatFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
                filteredData.setPredicate(rec -> {
                    if (newVal == null || newVal.equals("Tous")) return true;
                    return rec.getEtat().equalsIgnoreCase(newVal);
                });
            });

            SortedList<Reclamation> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tableReclamations.comparatorProperty());
            tableReclamations.setItems(sortedData);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement des réclamations : " + e.getMessage());
        }
    }


    private void ouvrirInterfaceReponse(Reclamation r) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RepondreReclamation.fxml"));
            Parent root = loader.load();

            RepondreReclamationController controller = loader.getController();
            controller.setReclamation(r);
            controller.setCallback(this::chargerReclamations); // refresh after response

            Stage stage = new Stage();
            stage.setTitle("Répondre à la Réclamation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de l'interface de réponse.");
            e.printStackTrace();
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
