package com.thesphynx.controllers;

import com.thesphynx.entities.Evenement;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;

public class EventControllerInfo {

    @FXML private TextField titreTF;
    @FXML private TextArea descriptionTA;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private Spinner<Integer> heureDebutSpinner;
    @FXML private Spinner<Integer> minuteDebutSpinner;
    @FXML private Spinner<Integer> heureFinSpinner;
    @FXML private Spinner<Integer> minuteFinSpinner;
    @FXML private TextField organisateurField;
    @FXML private WebView mapWebView;
    @FXML private TextField adresseField;
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;
    @FXML private TextField capaciteField;
    @FXML private TextField categorieField;
    @FXML private TextField visibiliteField;
    @FXML private Button retourBtn;

    private WebEngine webEngine;

    @FXML
    public void initialize() {
        initializeSpinners();
        initializeMap();
    }

    private void initializeSpinners() {
        SpinnerValueFactory<Integer> heureDebutFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0);
        SpinnerValueFactory<Integer> minuteDebutFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        SpinnerValueFactory<Integer> heureFinFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0);
        SpinnerValueFactory<Integer> minuteFinFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);

        heureDebutSpinner.setValueFactory(heureDebutFactory);
        minuteDebutSpinner.setValueFactory(minuteDebutFactory);
        heureFinSpinner.setValueFactory(heureFinFactory);
        minuteFinSpinner.setValueFactory(minuteFinFactory);
    }

    private void initializeMap() {
        webEngine = mapWebView.getEngine();
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        webEngine.loadContent(createMapHtml());
    }

    private String createMapHtml() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Carte OpenStreetMap</title>\n" +
                "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.css\" />\n" +
                "    <script src=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.js\"></script>\n" +
                "    <style>\n" +
                "        html, body { height: 100%; margin: 0; padding: 0; }\n" +
                "        #map { height: 200px; width: 400px; min-height: 200px; min-width: 400px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"map\"></div>\n" +
                "    <script>\n" +
                "        var map = L.map('map', { doubleClickZoom: false, dragging: false, zoomControl: false }).setView([48.8566, 2.3522], 13);\n" +
                "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "            attribution: '© <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'\n" +
                "        }).addTo(map);\n" +
                "        var marker = L.marker([48.8566, 2.3522]).addTo(map);\n" +
                "        marker.bindPopup('Position de l\\'événement').openPopup();\n" +
                "        function setLocation(lat, lng) {\n" +
                "            map.setView([lat, lng], 13);\n" +
                "            marker.setLatLng([lat, lng]);\n" +
                "            marker.setPopupContent('Position : ' + lat.toFixed(4) + ', ' + lng.toFixed(4)).openPopup();\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    public void setEvenementInfo(Evenement evenement) {
        titreTF.setText(evenement.getTitre());
        descriptionTA.setText(evenement.getDescription());
        dateDebutPicker.setValue(evenement.getDateDebut().toLocalDate());
        heureDebutSpinner.getValueFactory().setValue(evenement.getDateDebut().getHour());
        minuteDebutSpinner.getValueFactory().setValue(evenement.getDateDebut().getMinute());
        dateFinPicker.setValue(evenement.getDateFin().toLocalDate());
        heureFinSpinner.getValueFactory().setValue(evenement.getDateFin().getHour());
        minuteFinSpinner.getValueFactory().setValue(evenement.getDateFin().getMinute());
        organisateurField.setText(String.valueOf(evenement.getId_organisateur()));
        latitudeField.setText(evenement.getLatitude());
        longitudeField.setText(evenement.getLongitude());
        adresseField.setText(evenement.getAdresse());
        categorieField.setText(evenement.getCategorie());
        visibiliteField.setText(evenement.getVisibilite());
        capaciteField.setText(String.valueOf(evenement.getCapacite()));

        // Update map with event location
        try {
            double lat = Double.parseDouble(evenement.getLatitude());
            double lng = Double.parseDouble(evenement.getLongitude());
            webEngine.executeScript(String.format("setLocation(%f, %f);", lat, lng));
        } catch (NumberFormatException e) {
            System.err.println("Invalid coordinates: " + e.getMessage());
        }
    }

    @FXML
    public void retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/thesphynx/AfficherEvent.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}