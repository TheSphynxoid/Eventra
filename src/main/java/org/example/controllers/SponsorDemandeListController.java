package org.example.controllers;

import jakarta.mail.MessagingException;
import org.example.MainApp;
import org.example.entities.Sponsor;
import org.example.entities.SponsorDemande;
import org.example.entities.SponsorDemande.DemandeStatus;
import org.example.entities.User;
import org.example.services.MailService;
import org.example.services.SponsorDemandeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.example.services.UserServices;
import org.example.utils.EmailSender;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class SponsorDemandeListController implements Initializable {

    @FXML
    private TableView<SponsorDemande> demandeTableView;

    @FXML
    private TableColumn<SponsorDemande, Integer> idColumn;

    @FXML
    private TableColumn<SponsorDemande, Integer> eventColumn;

    @FXML
    private TableColumn<SponsorDemande, Integer> sponsorColumn;

    @FXML
    private TableColumn<SponsorDemande, Float> montantColumn;

    @FXML
    private TableColumn<SponsorDemande, DemandeStatus> statusColumn;

    @FXML
    private TableColumn<SponsorDemande, Date> dateCreationColumn;

    @FXML
    private TableColumn<SponsorDemande, Date> dateAcceptanceColumn;

    @FXML
    private TableColumn<SponsorDemande, Void> actionsColumn;

    @FXML
    private ComboBox<DemandeStatus> filterStatusComboBox;

    @FXML
    private TextField searchField;

    private ObservableList<SponsorDemande> demandesList = FXCollections.observableArrayList();
    private FilteredList<SponsorDemande> filteredDemandes;
    private SponsorDemandeService sponsorDemandeService;

    private Sponsor currentSponsor;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize service
        sponsorDemandeService = new SponsorDemandeService();

        // Set up table columns
        setupTableColumns();

        // Setup status filter
        setupStatusFilter();


        // Initialize filtered list
        filteredDemandes = new FilteredList<>(demandesList);
        demandeTableView.setItems(filteredDemandes);

        // Load data
        loadDemandes();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        eventColumn.setCellValueFactory(new PropertyValueFactory<>("id_event"));
        sponsorColumn.setCellValueFactory(new PropertyValueFactory<>("id_sponsor"));
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateCreationColumn.setCellValueFactory(new PropertyValueFactory<>("date_creation"));
        dateAcceptanceColumn.setCellValueFactory(new PropertyValueFactory<>("date_acceptance"));

        // Format the montant column to show currency
        montantColumn.setCellFactory(tc -> new TableCell<SponsorDemande, Float>() {
            @Override
            protected void updateItem(Float amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f DT", amount));
                }
            }
        });

        // Colorize status cells
        statusColumn.setCellFactory(tc -> new TableCell<SponsorDemande, DemandeStatus>() {
            @Override
            protected void updateItem(DemandeStatus status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.toString());
                    switch (status) {
                        case ACCEPTED:
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            break;
                        case WAITING:
                            setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });

        // Set up the actions column if it exists
        if (actionsColumn != null) {
            actionsColumn.setCellFactory(new Callback<>() {
                @Override
                public TableCell<SponsorDemande, Void> call(final TableColumn<SponsorDemande, Void> param) {
                    return new TableCell<>() {
                        private final HBox container = new HBox(5);
                        private final Button acceptBtn = new Button("Apply");
//                        private final Button rejectBtn = new Button("Reject");

                        {
                            container.setAlignment(Pos.CENTER);

                            acceptBtn.setStyle("-fx-background-color: #107C10; -fx-text-fill: white;");
//                            rejectBtn.setStyle("-fx-background-color: #E81123; -fx-text-fill: white;");

                            acceptBtn.setOnAction(event -> {
                                SponsorDemande demande = getTableView().getItems().get(getIndex());
                                handleAccept(demande);
                            });

//                            rejectBtn.setOnAction(event -> {
//                                SponsorDemande demande = getTableView().getItems().get(getIndex());
//                                handleReject(demande);
//                            });

//                            container.getChildren().addAll(acceptBtn, rejectBtn);
                            if(currentSponsor != null){
                                container.getChildren().addAll(acceptBtn);
                            }
                        }

                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                SponsorDemande demande = getTableView().getItems().get(getIndex());
                                // Only show buttons for Waiting status
                                if (demande.getStatus() == DemandeStatus.WAITING) {
                                    setGraphic(container);
                                } else {
                                    setGraphic(null);
                                }
                            }
                        }
                    };
                }
            });
        }
    }

    private void setupStatusFilter() {
        // Add all status options plus an "All" option
        filterStatusComboBox.getItems().addAll(DemandeStatus.values());

        // Set converter to display "All" for null value
        filterStatusComboBox.setConverter(new StringConverter<DemandeStatus>() {
            @Override
            public String toString(DemandeStatus status) {
                return status == null ? "All Statuses" : status.toString();
            }

            @Override
            public DemandeStatus fromString(String string) {
                if ("All Statuses".equals(string)) return null;
                return DemandeStatus.valueOf(string);
            }
        });

        // Add listener for filter changes
        filterStatusComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Set default value to show all
        filterStatusComboBox.setValue(null);
    }

    private void loadDemandes() {
        try {
            // Clear existing items
            demandesList.clear();

            // Load data from database
            demandesList.addAll(sponsorDemandeService.afficher());

            // Apply filters
            applyFilters();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load sponsor demands: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyFilters() {
        Predicate<SponsorDemande> statusPredicate = demande -> {
            DemandeStatus selectedStatus = filterStatusComboBox.getValue();
            return demande.getId_organisateur() == MainApp.LAYOUT_CONTROLLER.getCurrentUser().getId() || selectedStatus == null ||  demande.getStatus() == selectedStatus;
        };

        filteredDemandes.setPredicate(statusPredicate);
    }

    @FXML
    private void refreshButtonClicked() {
        loadDemandes();  // Reload data from database
    }

    @FXML
    private void viewDetailsButtonClicked() {
        SponsorDemande selectedDemande = demandeTableView.getSelectionModel().getSelectedItem();
        if (selectedDemande != null) {
            showAlert(Alert.AlertType.INFORMATION, "Demande Details",
                    "ID: " + selectedDemande.getId() + "\n" +
                            "Event ID: " + selectedDemande.getId_event() + "\n" +
                            "Sponsor ID: " + selectedDemande.getId_sponsor() + "\n" +
                            "Amount: " + String.format("%.2f DT", selectedDemande.getMontant()) + "\n" +
                            "Status: " + selectedDemande.getStatus() + "\n" +
                            "Created on: " + selectedDemande.getDate_creation() + "\n" +
                            "Accepted on: " + (selectedDemande.getDate_acceptance() != null ?
                            selectedDemande.getDate_acceptance() : "N/A"));
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a request to view.");
        }
    }

    private void handleAccept(SponsorDemande demande) {
        if (demande.getStatus() != DemandeStatus.WAITING) {
            showAlert(Alert.AlertType.WARNING, "Action Not Allowed",
                    "Only requests with 'Waiting' status can be accepted.");
            return;
        }

        try {
            // Update the status
            demande.setStatus(DemandeStatus.ACCEPTED);
            demande.setDate_acceptance(Date.valueOf(LocalDate.now()));
            demande.setId_sponsor(MainApp.LAYOUT_CONTROLLER.getCurrentUser().getId());

            // Save to database
            sponsorDemandeService.modifier(demande);

            // Refresh the table
            demandeTableView.refresh();

            MailService sender = MainApp.AUTH_SERVICE.getMailService();

            UserServices userServices = new UserServices();
            System.out.println("Organisateur ID = " + demande.getId_organisateur());
            userServices.getAll().forEach(user -> {
                if(user.getId() == demande.getId_organisateur()){
                    System.out.println("Org Email: " + user.getEmail());
                    try {
                        sender.sendSimple(user.getEmail(), "Demande Sponsor Accepte",
                                "Demande ID" + demande.getId() + "Has been accepted by SponsorID" + MainApp.LAYOUT_CONTROLLER.getCurrentUser().getId());
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            System.out.println("Sponsor Email: " + MainApp.LAYOUT_CONTROLLER.getCurrentUser().getEmail());
            try {
                sender.sendSimple(MainApp.LAYOUT_CONTROLLER.getCurrentUser().getEmail(), "Demande Sponsor",
                        "Demande ID" + demande.getId() + "Has been accepted");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Request ID " + demande.getId() + " has been accepted.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not update request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleReject(SponsorDemande demande) {
        if (demande.getStatus() != DemandeStatus.WAITING) {
            showAlert(Alert.AlertType.WARNING, "Action Not Allowed",
                    "Only requests with 'Waiting' status can be rejected.");
            return;
        }

        try {

            // Save to database
            sponsorDemandeService.modifier(demande);

            // Refresh the table
            demandeTableView.refresh();

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Request ID " + demande.getId() + " has been rejected.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not update request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setCurrentSponsor(Sponsor currentSponsor) {
        this.currentSponsor = currentSponsor;
    }

    private void redirectToSponsorDemande() throws IOException {
        // Load the new FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SponsorDemande.fxml"));
        Parent root = loader.load();

        // Pass the logged in sponsor to the next controller if needed
//        SponsorDemandeController controller = loader.getController();
        //

        // Create new scene
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Sponsoring");
        stage.show();
    }

    public void ajouter(ActionEvent actionEvent) throws IOException {
        redirectToSponsorDemande();
        refreshButtonClicked();
    }



    public void supprimer(ActionEvent actionEvent) throws SQLException {
        sponsorDemandeService.supprimer(demandeTableView.getSelectionModel().getSelectedItem().getId());
        refreshButtonClicked();
    }

    public void modifier(ActionEvent actionEvent) {
        SponsorDemande selectedDemande = demandeTableView.getSelectionModel().getSelectedItem();
        if (selectedDemande != null) {
            try {
                // Load the SponsorDemande FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SponsorDemande.fxml"));
                Parent root = loader.load();

                // Get the controller and pass the selected demande data
                AddSponsorDemandeController controller = loader.getController();

                controller.SetModifier(true, selectedDemande);

                // Pre-fill the form with existing data
                controller.eventIdField.setText(String.valueOf(selectedDemande.getId_event()));
                controller.amountField.setText(String.valueOf(selectedDemande.getMontant()));
                controller.descriptionField.setText(selectedDemande.getDescription());

                // Create new scene and stage
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier Demande de Sponsor");
                stage.show();

                // Refresh the table after modification (optional)
                stage.setOnHidden(e -> refreshButtonClicked());

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not load the modification form: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a request to modify.");
        }
    }
}