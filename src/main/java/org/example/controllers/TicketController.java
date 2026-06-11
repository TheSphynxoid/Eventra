package org.example.controllers;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.MainApp;
import org.example.entities.Evenement;
import org.example.entities.Ticket;
import org.example.services.TicketService;
import org.example.utils.QRCodeGenerator;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class TicketController {

    private final TicketService ticketService = new TicketService();
    public TextField name;
    public Button confirm;

    @FXML private Label prixField;

    @FXML private TextField num_carte;
    @FXML private TextField cvv;
    @FXML private Spinner<Integer> quantityField;

    private Evenement currentEvent;
    private Ticket currentTicket;

    public void SetEventement(Evenement e){
        currentEvent = e;
        quantityField = new Spinner<>(1, currentEvent.getCapacite(), 1);
    }

    public void SetTicket(Ticket ticket) {
        this.currentTicket = ticket;
        name.setText(MainApp.LAYOUT_CONTROLLER.getCurrentUser().getNom());
        quantityField.getValueFactory().setValue(ticket.getQuantity());
    }

    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> quant = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 1);
        quantityField.setValueFactory(quant);
    }

    @FXML
    private void handleConfirm() {
        if (validateInput()) {
            try {
                Ticket ticket;
                if (currentTicket == null) {
                    // Création d'un nouveau ticket
                    ticket = createTicketFromFields();
                    ticket = ticketService.ajouterEtRetourner(ticket);
                } else {
                    // Modification d'un ticket existant
                    ticket = currentTicket;
                    ticket.setQuantity(quantityField.getValue());
                    ticketService.modifier(ticket);
                }

                String qrCodeData = generateQRCodeData(ticket);
                Image qrCodeImage = QRCodeGenerator.generateQRCodeImage(qrCodeData, 200, 200);

                showQRCodeAlert(ticket, qrCodeImage);
                clearFields();

                Stage stage = (Stage) cvv.getScene().getWindow();
                stage.close();

            } catch (SQLException e) {
                showAlert("Erreur", "Erreur d'enregistrement", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private Ticket createTicketFromFields() {
        return new Ticket(
                0, // ID auto-généré
                currentEvent.getId(),
                MainApp.LAYOUT_CONTROLLER.getCurrentUser().getId(),
                quantityField.getValue(),
                "",
                Date.valueOf(LocalDate.now())
        );
    }

    private String generateQRCodeData(Ticket ticket) {
        return String.format("TICKET|%d|%d|%s|%d|%s",
                ticket.getTicketID(),
                ticket.getEventID(),
                ticket.getType(),
                ticket.getQuantity(),
                ticket.getPurchaseDate().toString());
    }

    private void showQRCodeAlert(Ticket ticket, Image qrCodeImage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ticket confirmé");
        alert.setHeaderText("Votre ticket #" + ticket.getTicketID() + " est prêt !");

        ImageView imageView = new ImageView(qrCodeImage);
        imageView.setFitHeight(150);
        imageView.setFitWidth(150);

        alert.setGraphic(imageView);
        alert.setContentText("Présentez ce QR code à l'entrée de l'événement.");
        alert.showAndWait();
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        if (num_carte.getText().isEmpty()) errors.append("• Numero Carte requis\n");
        if (cvv.getText().isEmpty()) errors.append("• CVV requis\n");
        if (quantityField.getValue() == 0) errors.append("• Quantité requise\n");

        // Validation numérique
        try {
            Integer.parseInt(num_carte.getText());
            Integer.parseInt(cvv.getText());
        } catch (NumberFormatException e) {
            errors.append("• Les IDs et la quantité doivent être des nombres\n");
        }

        if (errors.length() > 0) {
            showAlert("Erreurs de validation", "Veuillez corriger :", errors.toString(), Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void clearFields() {
        num_carte.clear();
        cvv.clear();

        quantityField.decrement(quantityField.getValue());
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

}