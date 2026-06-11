package com.thesphynx.controllers;

import com.thesphynx.entities.Evenement;
import com.thesphynx.services.EvenementsServices;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class AjouterEventController implements Initializable {

    @FXML private TextField titreTF;
    @FXML private TextArea descriptionTA;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private Spinner<Integer> heureDebutSpinner;
    @FXML private Spinner<Integer> minuteDebutSpinner;
    @FXML private Spinner<Integer> heureFinSpinner;
    @FXML private Spinner<Integer> minuteFinSpinner;
    @FXML private ComboBox<Integer> organisateurComboBox;
    @FXML private WebView mapWebView;
    @FXML private Button zoomInBtn;
    @FXML private Button zoomOutBtn;
    @FXML private Button myLocationBtn;
    @FXML private TextField adresseField;
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;
    @FXML private Spinner<Integer> capaciteSpinner;
    @FXML private ColorPicker couleurPicker;
    @FXML private ComboBox<String> categorieComboBox;
    @FXML private ToggleGroup visibiliteGroup;
    @FXML private RadioButton publicRadio;
    @FXML private RadioButton priveRadio;
    @FXML private Label dureeLabel;

    private WebEngine webEngine;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation des spinners pour les heures et minutes
        initializeSpinners();

        // Initialisation de la ComboBox des organisateurs
        organisateurComboBox.getItems().addAll(1, 2, 3);

        // Initialisation de la ComboBox des catégories
        categorieComboBox.getItems().addAll("CONCERT", "SPECTACLE", "EXPOSITION", "FESTIVAL","CONFERENCE", "SEMINAIRE", "ATELIER (WORKSHOP)", "SALON / FOIRE",
                "MATCH", "COMPETITION", "MARATHON", "COURS DE SPORT", "COURS / FORMATION", "CONFERENCE ACADEMIQUE", "JOURNEE PORTES OUVERTES", "RENCONTRE / NETWORKING",
                "EVENEMENT CARITATIF", "REUNION ASSOCIATIVE", "MARIAGE", "ANNIVERSAIRE", "SOIREE PRIVEE");
        categorieComboBox.setValue("");

        // Initialisation de la carte interactive
        initializeMap();
        setupMapControls();

        // Configuration des validateurs pour les champs de coordonnées
        setupCoordinateValidators();

        // Ajout des listeners pour calculer la durée automatiquement
        setupDurationCalculation();
    }

    private void initializeSpinners() {
        SpinnerValueFactory<Integer> heureDebutFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 9);
        SpinnerValueFactory<Integer> minuteDebutFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        SpinnerValueFactory<Integer> heureFinFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 17);
        SpinnerValueFactory<Integer> minuteFinFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        SpinnerValueFactory<Integer> capaciteFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100000, 50);

        heureDebutSpinner.setValueFactory(heureDebutFactory);
        minuteDebutSpinner.setValueFactory(minuteDebutFactory);
        heureFinSpinner.setValueFactory(heureFinFactory);
        minuteFinSpinner.setValueFactory(minuteFinFactory);
        capaciteSpinner.setValueFactory(capaciteFactory);

        setSpinnerTextFormatter(heureDebutSpinner);
        setSpinnerTextFormatter(minuteDebutSpinner);
        setSpinnerTextFormatter(heureFinSpinner);
        setSpinnerTextFormatter(minuteFinSpinner);
    }

    private void setSpinnerTextFormatter(Spinner<Integer> spinner) {
        spinner.getEditor().setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        }));
    }

    private void setupCoordinateValidators() {
        latitudeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("-?\\d*(\\.\\d*)?")) {
                latitudeField.setText(oldValue);
            }
        });

        longitudeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("-?\\d*(\\.\\d*)?")) {
                longitudeField.setText(oldValue);
            }
        });
    }

    private void setupDurationCalculation() {
        ChangeListener<Object> durationListener = (observable, oldValue, newValue) -> updateDuration();

        dateDebutPicker.valueProperty().addListener(durationListener);
        dateFinPicker.valueProperty().addListener(durationListener);
        heureDebutSpinner.valueProperty().addListener(durationListener);
        minuteDebutSpinner.valueProperty().addListener(durationListener);
        heureFinSpinner.valueProperty().addListener(durationListener);
        minuteFinSpinner.valueProperty().addListener(durationListener);
    }

    private void updateDuration() {
        if (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null) {
            dureeLabel.setText("Calculée automatiquement");
            return;
        }

        try {
            LocalDateTime debut = dateDebutPicker.getValue().atTime(
                    heureDebutSpinner.getValue(), minuteDebutSpinner.getValue());
            LocalDateTime fin = dateFinPicker.getValue().atTime(
                    heureFinSpinner.getValue(), minuteFinSpinner.getValue());

            Duration duration = Duration.between(debut, fin);
            if (duration.isNegative()) {
                dureeLabel.setText("Date de fin antérieure à la date de début");
                return;
            }

            long days = duration.toDays();
            duration = duration.minusDays(days);
            long hours = duration.toHours();
            duration = duration.minusHours(hours);
            long minutes = duration.toMinutes();

            StringBuilder durationText = new StringBuilder();
            if (days > 0) {
                durationText.append(days).append(" jour").append(days > 1 ? "s " : " ");
            }
            if (hours > 0 || days > 0) {
                durationText.append(hours).append(" heure").append(hours > 1 ? "s " : " ");
            }
            durationText.append(minutes).append(" minute").append(minutes > 1 ? "s" : "");

            dureeLabel.setText(durationText.toString().trim());
        } catch (Exception e) {
            dureeLabel.setText("Erreur de calcul");
        }
    }

    private void initializeMap() {
        webEngine = mapWebView.getEngine();
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        webEngine.loadContent(createMapHtml());

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                initializeMapInteraction();
                System.out.println("Map loaded successfully");
                mapWebView.requestLayout();
            } else if (newValue == Worker.State.FAILED) {
                System.err.println("Map failed to load: " + webEngine.getLoadWorker().getException());
            }
        });
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
                "        .leaflet-control-zoom { margin-left: 10px; margin-top: 10px; }\n" +
                "        #searchContainer { position: absolute; top: 10px; left: 50px; z-index: 1000; }\n" +
                "        #searchInput { padding: 5px; width: 200px; }\n" +
                "        #searchButton { padding: 5px 10px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"searchContainer\">\n" +
                "        <input type=\"text\" id=\"searchInput\" placeholder=\"Rechercher un lieu (ex. Paris, France)\">\n" +
                "        <button id=\"searchButton\" onclick=\"searchLocation()\">Rechercher</button>\n" +
                "    </div>\n" +
                "    <div id=\"map\"></div>\n" +
                "    <script>\n" +
                "        console.log('Initializing Leaflet map');\n" +
                "        var map = L.map('map', { doubleClickZoom: false }).setView([48.8566, 2.3522], 13);\n" +
                "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "            attribution: '© <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'\n" +
                "        }).addTo(map);\n" +
                "\n" +
                "        var marker = L.marker([48.8566, 2.3522], { draggable: true }).addTo(map);\n" +
                "        marker.bindPopup('Déplacez-moi ou cliquez sur la carte pour choisir une position').openPopup();\n" +
                "        console.log('Marker added at: 48.8566, 2.3522');\n" +
                "\n" +
                "        marker.on('dragstart', function(e) {\n" +
                "            marker.setPopupContent('Déplacement en cours...');\n" +
                "        });\n" +
                "\n" +
                "        marker.on('dragend', function(e) {\n" +
                "            var coord = marker.getLatLng();\n" +
                "            updateLocation(coord.lat, coord.lng);\n" +
                "            marker.setPopupContent('Position sélectionnée : ' + coord.lat.toFixed(4) + ', ' + coord.lng.toFixed(4)).openPopup();\n" +
                "            console.log('Marker dragged to: ' + coord.lat + ', ' + coord.lng);\n" +
                "        });\n" +
                "\n" +
                "        map.on('click', function(e) {\n" +
                "            if (confirm('Voulez-vous placer le marqueur ici ?')) {\n" +
                "                marker.setLatLng(e.latlng);\n" +
                "                updateLocation(e.latlng.lat, e.latlng.lng);\n" +
                "                marker.setPopupContent('Position sélectionnée : ' + e.latlng.lat.toFixed(4) + ', ' + e.latlng.lng.toFixed(4)).openPopup();\n" +
                "                console.log('Map clicked and confirmed at: ' + e.latlng.lat + ', ' + e.latlng.lng);\n" +
                "            }\n" +
                "        });\n" +
                "\n" +
                "        function updateLocation(lat, lng) {\n" +
                "            console.log('Updating location with lat: ' + lat + ', lng: ' + lng);\n" +
                "            if (window.updateCoordinates) {\n" +
                "                window.updateCoordinates(lat, lng);\n" +
                "            }\n" +
                "            fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}`, {\n" +
                "                headers: { 'User-Agent': 'TheSphynxEventApp/1.0 (contact@example.com)' }\n" +
                "            })\n" +
                "                .then(response => response.json())\n" +
                "                .then(data => {\n" +
                "                    console.log('Reverse geocoding result:', data);\n" +
                "                    if (window.updateAddress) {\n" +
                "                        window.updateAddress(data.display_name || 'Adresse non trouvée');\n" +
                "                    }\n" +
                "                })\n" +
                "                .catch(error => {\n" +
                "                    console.error('Erreur de géocodage:', error);\n" +
                "                    if (window.updateAddress) {\n" +
                "                        window.updateAddress('Adresse non disponible');\n" +
                "                    }\n" +
                "                });\n" +
                "        }\n" +
                "\n" +
                "        function searchLocation() {\n" +
                "            var query = document.getElementById('searchInput').value;\n" +
                "            if (!query) {\n" +
                "                if (window.showAlert) {\n" +
                "                    window.showAlert('Erreur de recherche', 'Veuillez entrer un lieu à rechercher.');\n" +
                "                }\n" +
                "                return;\n" +
                "            }\n" +
                "            fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}&limit=1`, {\n" +
                "                headers: { 'User-Agent': 'TheSphynxEventApp/1.0 (contact@example.com)' }\n" +
                "            })\n" +
                "                .then(response => response.json())\n" +
                "                .then(data => {\n" +
                "                    console.log('Search result:', data);\n" +
                "                    if (data && data.length > 0) {\n" +
                "                        var lat = parseFloat(data[0].lat);\n" +
                "                        var lon = parseFloat(data[0].lon);\n" +
                "                        console.log('Setting map view to lat: ' + lat + ', lon: ' + lon);\n" +
                "                        map.setView([lat, lon], 13);\n" +
                "                        console.log('Setting marker to lat: ' + lat + ', lon: ' + lon);\n" +
                "                        marker.setLatLng([lat, lon]);\n" +
                "                        updateLocation(lat, lon);\n" +
                "                        marker.setPopupContent('Position : ' + data[0].display_name).openPopup();\n" +
                "                        console.log('Location searched: ' + lat + ', ' + lon);\n" +
                "                    } else {\n" +
                "                        if (window.showAlert) {\n" +
                "                            window.showAlert('Lieu non trouvé', 'Aucun résultat pour ' + query);\n" +
                "                        }\n" +
                "                    }\n" +
                "                })\n" +
                "                .catch(error => {\n" +
                "                    console.error('Erreur de recherche:', error);\n" +
                "                    if (window.showAlert) {\n" +
                "                        window.showAlert('Erreur de recherche', 'Une erreur s\\'est produite lors de la recherche.');\n" +
                "                    }\n" +
                "                });\n" +
                "        }\n" +
                "\n" +
                "        function goToMyLocation() {\n" +
                "            if (navigator.geolocation) {\n" +
                "                navigator.geolocation.getCurrentPosition(\n" +
                "                    function(position) {\n" +
                "                        var latlng = [position.coords.latitude, position.coords.longitude];\n" +
                "                        console.log('Geolocation success: ' + latlng[0] + ', ' + latlng[1]);\n" +
                "                        map.setView(latlng, 15);\n" +
                "                        marker.setLatLng(latlng);\n" +
                "                        updateLocation(latlng[0], latlng[1]);\n" +
                "                        marker.setPopupContent('Votre position : ' + latlng[0].toFixed(4) + ', ' + latlng[1].toFixed(4)).openPopup();\n" +
                "                    },\n" +
                "                    function(error) {\n" +
                "                        console.error('Geolocation error:', error);\n" +
                "                        var defaultLatLng = [48.8566, 2.3522];\n" +
                "                        map.setView(defaultLatLng, 13);\n" +
                "                        marker.setLatLng(defaultLatLng);\n" +
                "                        updateLocation(defaultLatLng[0], defaultLatLng[1]);\n" +
                "                        marker.setPopupContent('Position par défaut : Paris, France').openPopup();\n" +
                "                        if (window.showAlert) {\n" +
                "                            window.showAlert('Géolocalisation échouée', 'Position par défaut utilisée : Paris, France.');\n" +
                "                        }\n" +
                "                    }\n" +
                "                );\n" +
                "            } else {\n" +
                "                var defaultLatLng = [48.8566, 2.3522];\n" +
                "                map.setView(defaultLatLng, 13);\n" +
                "                marker.setLatLng(defaultLatLng);\n" +
                "                updateLocation(defaultLatLng[0], defaultLatLng[1]);\n" +
                "                marker.setPopupContent('Position par défaut : Paris, France').openPopup();\n" +
                "                if (window.showAlert) {\n" +
                "                    window.showAlert('Géolocalisation non supportée', 'Position par défaut utilisée : Paris, France.');\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        function zoomIn() { map.zoomIn(); }\n" +
                "        function zoomOut() { map.zoomOut(); }\n" +
                "\n" +
                "        function setLocation(lat, lng) {\n" +
                "            console.log('Setting location in setLocation: ' + lat + ', ' + lng);\n" +
                "            map.setView([lat, lng], map.getZoom());\n" +
                "            marker.setLatLng([lat, lng]);\n" +
                "            marker.setPopupContent('Position : ' + lat.toFixed(4) + ', ' + lng.toFixed(4)).openPopup();\n" +
                "        }\n" +
                "\n" +
                "        document.getElementById('searchInput').addEventListener('keypress', function(e) {\n" +
                "            if (e.key === 'Enter') {\n" +
                "                searchLocation();\n" +
                "            }\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    private void initializeMapInteraction() {
        webEngine.executeScript(
                "window.updateCoordinates = function(lat, lng) {" +
                        "    window.latitude = lat;" +
                        "    window.longitude = lng;" +
                        "    console.log('Coordinates updated in window: ' + lat + ', ' + lng);" +
                        "};" +
                        "window.updateAddress = function(address) {" +
                        "    window.address = address;" +
                        "    console.log('Address updated in window: ' + address);" +
                        "};" +
                        "window.showAlert = function(title, message) {" +
                        "    window.alertCallback(title + '\\n' + message);" +
                        "};"
        );

        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(500), event -> {
                    Object lat = webEngine.executeScript("window.latitude");
                    Object lng = webEngine.executeScript("window.longitude");
                    Object address = webEngine.executeScript("window.address");

                    if (lat != null && lng != null) {
                        latitudeField.setText(String.valueOf(lat));
                        longitudeField.setText(String.valueOf(lng));
                        System.out.println("Updated coordinates: " + lat + ", " + lng);
                    }
                    if (address != null) {
                        adresseField.setText(String.valueOf(address));
                        System.out.println("Updated address: " + address);
                    }
                })
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();

        webEngine.setOnAlert(event -> {
            showAlert(Alert.AlertType.WARNING, event.getData().split("\n")[0], event.getData().split("\n")[1]);
        });
    }

    private void setupMapControls() {
        zoomInBtn.setOnAction(event -> webEngine.executeScript("zoomIn();"));
        zoomOutBtn.setOnAction(event -> webEngine.executeScript("zoomOut();"));
        myLocationBtn.setOnAction(event -> webEngine.executeScript("goToMyLocation();"));

        latitudeField.textProperty().addListener((obs, oldVal, newVal) -> updateMapFromFields());
        longitudeField.textProperty().addListener((obs, oldVal, newVal) -> updateMapFromFields());
    }

    private void updateMapFromFields() {
        try {
            double lat = Double.parseDouble(latitudeField.getText());
            double lng = Double.parseDouble(longitudeField.getText());
            webEngine.executeScript(String.format("setLocation(%f, %f);", lat, lng));
            System.out.println("Set location from fields: " + lat + ", " + lng);
        } catch (NumberFormatException e) {
            System.err.println("Invalid coordinate format: " + e.getMessage());
        }
    }

    @FXML
    private void ajouterEvenement(ActionEvent event) {
        if (!validateForm()) {
            showAlert(Alert.AlertType.ERROR, "Formulaire incomplet",
                    "Veuillez remplir tous les champs obligatoires");
            return;
        }

        LocalDateTime debut = dateDebutPicker.getValue().atTime(
                heureDebutSpinner.getValue(), minuteDebutSpinner.getValue());
        LocalDateTime fin = dateFinPicker.getValue().atTime(
                heureFinSpinner.getValue(), minuteFinSpinner.getValue());

        if (fin.isBefore(debut)) {
            showAlert(Alert.AlertType.ERROR, "Erreur de date",
                    "La date de fin doit être après la date de début");
            return;
        }

        Evenement evenement = new Evenement(
                titreTF.getText(),
                descriptionTA.getText(),
                debut,
                fin,
                organisateurComboBox.getValue(),
                latitudeField.getText(),
                longitudeField.getText(),
                adresseField.getText(),
                categorieComboBox.getValue(),
                publicRadio.isSelected() ? "Public" : "Privé",
                capaciteSpinner.getValue()
        );

        try {
            EvenementsServices es = new EvenementsServices();
            es.ajouter(evenement);

            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Événement ajouté avec succès");
            redirectToEventList(event);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL",
                    "Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        return !titreTF.getText().isEmpty() &&
                !descriptionTA.getText().isEmpty() &&
                dateDebutPicker.getValue() != null &&
                dateFinPicker.getValue() != null &&
                organisateurComboBox.getValue() != null &&
                !latitudeField.getText().isEmpty() &&
                !longitudeField.getText().isEmpty() &&
                !adresseField.getText().isEmpty() &&
                categorieComboBox.getValue() != null &&
                capaciteSpinner.getValue() != null;
    }

    @FXML
    private void afficherListeEvenements(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/thesphynx/AfficherEvent.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation",
                    "Impossible de charger l'interface d'affichage des événements: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/thesphynx/Home.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'interface: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void redirectToEventList(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/thesphynx/AfficherEvent.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger la liste: " + e.getMessage());
        }
    }

    @FXML
    private void reformulerDescription(ActionEvent event) {
        String originalDescription = descriptionTA.getText().trim();
        if (originalDescription.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez entrer une description à reformuler.");
            return;
        }

        String reformulated = reformulateText(originalDescription);
        descriptionTA.setText(reformulated);
    }

    private String reformulateText(String input) {
        // Dictionnaire de synonymes et reformulations pour différents types d'événements
        Map<String, List<String>> eventSynonyms = Map.of(
                "concert", List.of("un spectacle musical époustouflant", "une performance live inoubliable",
                        "une expérience musicale immersive", "un événement musical de premier ordre"),
                "réunion", List.of("une session collaborative productive", "un rassemblement professionnel efficace",
                        "une rencontre stratégique", "une assemblée organisée"),
                "conférence", List.of("un symposium enrichissant", "un événement de partage de connaissances",
                        "une convention inspirante", "un rassemblement éducatif"),
                "fête", List.of("une célébration mémorable", "un événement festif animé",
                        "une soirée inoubliable", "un moment de convivialité"),
                "atelier", List.of("une session pratique interactive", "une expérience d'apprentissage pratique",
                        "un laboratoire éducatif", "un workshop immersif")
        );

        // Phrases d'introduction variées
        List<String> introductions = List.of(
                "Nous avons le plaisir de vous présenter ",
                "Venez découvrir ",
                "Ne manquez pas ",
                "Participez à ",
                "Plongez dans l'expérience de ",
                "Soyez des nôtres pour "
        );

        // Phrases de conclusion variées
        List<String> conclusions = List.of(
                " - une expérience à ne pas manquer !",
                ", garantissant des souvenirs mémorables.",
                ", conçu pour votre plus grand plaisir.",
                ", une occasion unique cette saison.",
                ", promettant des moments exceptionnels."
        );

        // Trouver le type d'événement le plus pertinent
        Optional<Map.Entry<String, List<String>>> matchedType = eventSynonyms.entrySet().stream()
                .filter(entry -> input.toLowerCase().contains(entry.getKey()))
                .findFirst();

        // Construire la nouvelle description
        String reformulated;
        if (matchedType.isPresent()) {
            List<String> synonyms = matchedType.get().getValue();
            String synonym = synonyms.get(new Random().nextInt(synonyms.size()));
            reformulated = introductions.get(new Random().nextInt(introductions.size())) +
                    synonym +
                    conclusions.get(new Random().nextInt(conclusions.size()));
        } else {
            // Reformulation générique si aucun type spécifique n'est détecté
            String[] sentences = input.split("[.!?]");
            if (sentences.length > 0) {
                String firstSentence = sentences[0].trim();
                reformulated = "Un événement exceptionnel : " + firstSentence +
                        (firstSentence.endsWith(".") ? "" : ".") +
                        " Une occasion unique de vivre une expérience inoubliable.";
            } else {
                reformulated = "Un événement remarquable soigneusement organisé pour votre plaisir : " + input;
            }
        }

        // Capitaliser la première lettre
        if (!reformulated.isEmpty()) {
            reformulated = reformulated.substring(0, 1).toUpperCase() + reformulated.substring(1);
        }

        return reformulated;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}