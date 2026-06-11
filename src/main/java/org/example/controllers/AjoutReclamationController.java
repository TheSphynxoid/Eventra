package org.example.controllers;

import org.example.MainApp;
import org.example.entities.Reclamation;
import org.example.model.OpenAiResponse;
import org.example.services.ReclamationService;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class AjoutReclamationController {

    @FXML
    private TextField emailField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TextArea descriptionField;
    @FXML
    private DatePicker datePickerReclamation;

    private final ReclamationService reclamationService = new ReclamationService();
    private static final String API_KEY = "Bearer ...";

    private final List<String> badWords = Arrays.asList("idiot", "stupide", "imbécile", "nul", "con", "insultant");

    @FXML
    public void initialize() {
        typeComboBox.getItems().addAll("Probléme Technique", "Service", "Facturation", "Sécurité", "Autre");
    }

    @FXML
    private void ajouterReclamation() {
        String typeReclamation = typeComboBox.getValue();
        String description = descriptionField.getText().trim();
        LocalDate dateReclamation = datePickerReclamation.getValue() != null ? datePickerReclamation.getValue() : LocalDate.now();

        if (typeReclamation == null || description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Tous les champs doivent être remplis.");
            return;
        }

        if (contientDesMotsInappropries(description)) {
            showAlert(Alert.AlertType.WARNING, "Votre description contient des propos inappropriés. Veuillez reformuler.");
            return;
        }

        Reclamation reclamation = new Reclamation();
        reclamation.setId_client(String.valueOf(MainApp.LAYOUT_CONTROLLER.getCurrentUser().getId()));
        reclamation.setType(typeReclamation);
        reclamation.setDescription(description);
        reclamation.setDateReclamation(dateReclamation);
        reclamation.setEtat("En cours");
        reclamation.setEmail(MainApp.LAYOUT_CONTROLLER.getCurrentUser().getEmail());

        try {
            reclamationService.ajouter(reclamation);
            showAlert(Alert.AlertType.INFORMATION, "Réclamation ajoutée avec succès.");
            clearFields();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Échec de l'ajout : " + e.getMessage());
        }
    }

    @FXML
    private void clearFields() {
        typeComboBox.getSelectionModel().clearSelection();
        descriptionField.clear();
        datePickerReclamation.setValue(null);
    }

    private boolean contientDesMotsInappropries(String texte) {
        String texteMinuscule = texte.toLowerCase();
        return badWords.stream().anyMatch(texteMinuscule::contains);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void allerVersGestion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GererReclamations.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Mes Réclamations");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur d’ouverture de l’interface.");
        }
    }

    @FXML
    private void handleAideClick() {
        String prompt = "Aide-moi à formuler une réclamation pour un problème de service.";
        String requestBody = String.format("""
            {
                "model": "gpt-3.5-turbo",
                "messages": [{"role": "user", "content": "%s"}]
            }
        """, prompt);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(responseBody -> {
                    System.out.println("Réponse brute : " + responseBody);

                    String suggestion = extractSuggestion(responseBody);
                    Platform.runLater(() -> {
                        if (!suggestion.isEmpty()) {
                            descriptionField.setText(suggestion);
                            showAlert(Alert.AlertType.INFORMATION, "Suggestion IA :\n" + suggestion);
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Aucune suggestion disponible pour le moment.");
                        }
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Erreur API : " + ex.getMessage()));
                    return null;
                });
    }


    private String extractSuggestion(String responseBody) {
        try {
            Gson gson = new Gson();
            OpenAiResponse openAiResponse = gson.fromJson(responseBody, OpenAiResponse.class);
            if (openAiResponse.choices != null && !openAiResponse.choices.isEmpty()) {
                return openAiResponse.choices.get(0).message.content.trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
