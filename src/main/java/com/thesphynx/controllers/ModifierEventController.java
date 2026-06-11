package com.thesphynx.controllers;

import com.thesphynx.entities.Evenement;
import com.thesphynx.services.EvenementsServices;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
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
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;

public class ModifierEventController {

    private Evenement evenementAModifier;

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
    @FXML private ComboBox<String> categorieComboBox;
    @FXML private ToggleGroup visibiliteGroup;
    @FXML private RadioButton publicRadio;
    @FXML private RadioButton priveRadio;
    @FXML private Label dureeLabel;

    private WebEngine webEngine;
    private boolean isMapLoaded = false;

    @FXML
    public void initialize() {
        initializeSpinners();
        initializeComboBoxes();
        initializeMap();
        setupMapControls();
        setupCoordinateValidators();
        setupDurationCalculation();
    }

    private void initializeSpinners() {
        SpinnerValueFactory<Integer> heureDebutFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12);
        SpinnerValueFactory<Integer> minuteDebutFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        SpinnerValueFactory<Integer> heureFinFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 14);
        SpinnerValueFactory<Integer> minuteFinFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        SpinnerValueFactory<Integer> capaciteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100000, 50);

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

    private void initializeComboBoxes() {
        organisateurComboBox.getItems().addAll(1, 2, 3);
        categorieComboBox.getItems().addAll(
                "CONCERT", "SPECTACLE", "EXPOSITION", "FESTIVAL", "CONFERENCE", "SEMINAIRE",
                "ATELIER (WORKSHOP)", "SALON / FOIRE", "MATCH", "COMPETITION", "MARATHON",
                "COURS DE SPORT", "COURS / FORMATION", "CONFERENCE ACADEMIQUE",
                "JOURNEE PORTES OUVERTES", "RENCONTRE / NETWORKING", "EVENEMENT CARITATIF",
                "REUNION ASSOCIATIVE", "MARIAGE", "ANNIVERSAIRE", "SOIREE PRIVEE"
        );
    }

    private void initializeMap() {
        webEngine = mapWebView.getEngine();
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        webEngine.loadContent(createMapHtml());

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                isMapLoaded = true;
                initializeMapInteraction();
                mapWebView.requestLayout();
                // Re-attempt setting map location if event data was loaded before map
                if (evenementAModifier != null) {
                    chargerDonneesEvenement();
                }
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
                "        .leaflet-control-zoom { margin-left: 10px; margin-top: 50px; }\n" +
                "        #searchContainer { position: absolute; top: 10px; left: 50px; z-index: 1000; display: flex; align-items: center; }\n" +
                "        #searchInput { padding: 6px; width: 200px; border: 1px solid #ccc; border-radius: 4px; }\n" +
                "        #searchButton { padding: 6px 12px; margin-left: 5px; background: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer; }\n" +
                "        #searchButton:hover { background: #45a049; }\n" +
                "        #suggestions { position: absolute; top: 40px; left: 50px; width: 200px; max-height: 150px; overflow-y: auto; background: white; border: 1px solid #ccc; border-radius: 4px; z-index: 1001; display: none; }\n" +
                "        .suggestion-item { padding: 8px; cursor: pointer; }\n" +
                "        .suggestion-item:hover { background: #f0f0f0; }\n" +
                "        #loading { margin-left: 10px; color: #666; font-size: 12px; display: none; }\n" +
                "        #resetButton { position: absolute; top: 10px; left: 10px; padding: 6px 12px; background: #f44336; color: white; border: none; border-radius: 4px; cursor: pointer; z-index: 1000; }\n" +
                "        #resetButton:hover { background: #d32f2f; }\n" +
                "        #toast { position: absolute; bottom: 10px; left: 50%; transform: translateX(-50%); background: rgba(0, 0, 0, 0.8); color: white; padding: 10px 20px; border-radius: 4px; z-index: 1002; display: none; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <button id=\"resetButton\" onclick=\"resetToDefault()\">Réinitialiser</button>\n" +
                "    <div id=\"searchContainer\">\n" +
                "        <input type=\"text\" id=\"searchInput\" placeholder=\"Rechercher un lieu (ex. Paris, France)\">\n" +
                "        <button id=\"searchButton\" onclick=\"searchLocation()\">Rechercher</button>\n" +
                "        <span id=\"loading\">Chargement...</span>\n" +
                "    </div>\n" +
                "    <div id=\"suggestions\"></div>\n" +
                "    <div id=\"map\"></div>\n" +
                "    <div id=\"toast\"></div>\n" +
                "    <script>\n" +
                "        var map = L.map('map', { doubleClickZoom: false }).setView([48.8566, 2.3522], 13);\n" +
                "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "            attribution: '© <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'\n" +
                "        }).addTo(map);\n" +
                "        var marker = L.marker([48.8566, 2.3522], { draggable: true }).addTo(map);\n" +
                "        marker.bindPopup('Déplacez-moi ou cliquez sur la carte').openPopup();\n" +
                "        var searchCache = {};\n" +
                "        var lastQuery = '';\n" +
                "        marker.on('drag', function(e) {\n" +
                "            var coord = marker.getLatLng();\n" +
                "            updateLocation(coord.lat, coord.lng, false);\n" +
                "            marker.setPopupContent('Position : ' + coord.lat.toFixed(4) + ', ' + coord.lng.toFixed(4));\n" +
                "        });\n" +
                "        marker.on('dragend', function(e) {\n" +
                "            var coord = marker.getLatLng();\n" +
                "            updateLocation(coord.lat, coord.lng, true);\n" +
                "            showToast('Position sélectionnée');\n" +
                "        });\n" +
                "        map.on('click', function(e) {\n" +
                "            if (confirm('Voulez-vous placer le marqueur ici ?')) {\n" +
                "                marker.setLatLng(e.latlng);\n" +
                "                updateLocation(e.latlng.lat, e.latlng.lng, true);\n" +
                "                marker.setPopupContent('Position sélectionnée : ' + e.latlng.lat.toFixed(4) + ', ' + e.latlng.lng.toFixed(4)).openPopup();\n" +
                "                showToast('Position sélectionnée');\n" +
                "            }\n" +
                "        });\n" +
                "        map.on('zoomend', function() {\n" +
                "            document.getElementById('toast').innerText = 'Zoom : ' + map.getZoom();\n" +
                "            showToast('Zoom : ' + map.getZoom());\n" +
                "        });\n" +
                "        function updateLocation(lat, lng, reverseGeocode) {\n" +
                "            if (window.updateCoordinates) {\n" +
                "                window.updateCoordinates(lat, lng);\n" +
                "            }\n" +
                "            if (reverseGeocode) {\n" +
                "                fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}`, {\n" +
                "                    headers: { 'User-Agent': 'TheSphynxEventApp/1.0 (contact@example.com)' }\n" +
                "                })\n" +
                "                    .then(response => response.json())\n" +
                "                    .then(data => {\n" +
                "                        if (window.updateAddress) {\n" +
                "                            window.updateAddress(data.display_name || 'Adresse non trouvée');\n" +
                "                        }\n" +
                "                    })\n" +
                "                    .catch(error => {\n" +
                "                        if (window.updateAddress) {\n" +
                "                            window.updateAddress('Adresse non disponible');\n" +
                "                        }\n" +
                "                    });\n" +
                "            }\n" +
                "        }\n" +
                "        function searchLocation() {\n" +
                "            var query = document.getElementById('searchInput').value.trim();\n" +
                "            if (!query) {\n" +
                "                showToast('Veuillez entrer un lieu à rechercher');\n" +
                "                return;\n" +
                "            }\n" +
                "            document.getElementById('loading').style.display = 'inline';\n" +
                "            document.getElementById('suggestions').style.display = 'none';\n" +
                "            if (searchCache[query]) {\n" +
                "                handleSearchResult(searchCache[query]);\n" +
                "                document.getElementById('loading').style.display = 'none';\n" +
                "                return;\n" +
                "            }\n" +
                "            fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}&limit=1`, {\n" +
                "                headers: { 'User-Agent': 'TheSphynxEventApp/1.0 (contact@example.com)' }\n" +
                "            })\n" +
                "                .then(response => response.json())\n" +
                "                .then(data => {\n" +
                "                    document.getElementById('loading').style.display = 'none';\n" +
                "                    searchCache[query] = data;\n" +
                "                    handleSearchResult(data);\n" +
                "                })\n" +
                "                .catch(error => {\n" +
                "                    document.getElementById('loading').style.display = 'none';\n" +
                "                    showToast('Erreur lors de la recherche');\n" +
                "                });\n" +
                "        }\n" +
                "        function handleSearchResult(data) {\n" +
                "            if (data && data.length > 0) {\n" +
                "                var lat = parseFloat(data[0].lat);\n" +
                "                var lon = parseFloat(data[0].lon);\n" +
                "                map.setView([lat, lon], 13);\n" +
                "                marker.setLatLng([lat, lon]);\n" +
                "                updateLocation(lat, lon, true);\n" +
                "                marker.setPopupContent('Position : ' + data[0].display_name).openPopup();\n" +
                "                showToast('Lieu trouvé : ' + data[0].display_name);\n" +
                "            } else {\n" +
                "                showToast('Aucun résultat pour cette recherche');\n" +
                "            }\n" +
                "        }\n" +
                "        function fetchSuggestions(query) {\n" +
                "            if (!query || query === lastQuery) return;\n" +
                "            lastQuery = query;\n" +
                "            fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}&limit=5`, {\n" +
                "                headers: { 'User-Agent': 'TheSphynxEventApp/1.0 (contact@example.com)' }\n" +
                "            })\n" +
                "                .then(response => response.json())\n" +
                "                .then(data => {\n" +
                "                    var suggestions = document.getElementById('suggestions');\n" +
                "                    suggestions.innerHTML = '';\n" +
                "                    if (data.length > 0) {\n" +
                "                        data.forEach(item => {\n" +
                "                            var div = document.createElement('div');\n" +
                "                            div.className = 'suggestion-item';\n" +
                "                            div.innerText = item.display_name;\n" +
                "                            div.onclick = function() {\n" +
                "                                document.getElementById('searchInput').value = item.display_name;\n" +
                "                                suggestions.style.display = 'none';\n" +
                "                                map.setView([parseFloat(item.lat), parseFloat(item.lon)], 13);\n" +
                "                                marker.setLatLng([parseFloat(item.lat), parseFloat(item.lon)]);\n" +
                "                                updateLocation(parseFloat(item.lat), parseFloat(item.lon), true);\n" +
                "                                marker.setPopupContent('Position : ' + item.display_name).openPopup();\n" +
                "                                showToast('Lieu sélectionné : ' + item.display_name);\n" +
                "                            };\n" +
                "                            suggestions.appendChild(div);\n" +
                "                        });\n" +
                "                        suggestions.style.display = 'block';\n" +
                "                    } else {\n" +
                "                        suggestions.style.display = 'none';\n" +
                "                    }\n" +
                "                })\n" +
                "                .catch(error => {\n" +
                "                    document.getElementById('suggestions').style.display = 'none';\n" +
                "                });\n" +
                "        }\n" +
                "        function goToMyLocation() {\n" +
                "            if (navigator.geolocation) {\n" +
                "                navigator.geolocation.getCurrentPosition(\n" +
                "                    function(position) {\n" +
                "                        var latlng = [position.coords.latitude, position.coords.longitude];\n" +
                "                        map.setView(latlng, 15);\n" +
                "                        marker.setLatLng(latlng);\n" +
                "                        updateLocation(latlng[0], latlng[1], true);\n" +
                "                        marker.setPopupContent('Votre position : ' + latlng[0].toFixed(4) + ', ' + latlng[1].toFixed(4)).openPopup();\n" +
                "                        showToast('Position actuelle sélectionnée');\n" +
                "                    },\n" +
                "                    function(error) {\n" +
                "                        showToast('Géolocalisation échouée : Position par défaut utilisée');\n" +
                "                        resetToDefault();\n" +
                "                    }\n" +
                "                );\n" +
                "            } else {\n" +
                "                showToast('Géolocalisation non supportée : Position par défaut utilisée');\n" +
                "                resetToDefault();\n" +
                "            }\n" +
                "        }\n" +
                "        function resetToDefault() {\n" +
                "            var defaultLatLng = [48.8566, 2.3522];\n" +
                "            map.setView(defaultLatLng, 13);\n" +
                "            marker.setLatLng(defaultLatLng);\n" +
                "            updateLocation(defaultLatLng[0], defaultLatLng[1], true);\n" +
                "            marker.setPopupContent('Position par défaut : Paris, France').openPopup();\n" +
                "            showToast('Réinitialisé à Paris, France');\n" +
                "        }\n" +
                "        function zoomIn() { map.zoomIn(); }\n" +
                "        function zoomOut() { map.zoomOut(); }\n" +
                "        function setLocation(lat, lng) {\n" +
                "            if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {\n" +
                "                showToast('Coordonnées invalides');\n" +
                "                return;\n" +
                "            }\n" +
                "            map.setView([lat, lng], map.getZoom());\n" +
                "            marker.setLatLng([lat, lng]);\n" +
                "            marker.setPopupContent('Position : ' + lat.toFixed(4) + ', ' + lng.toFixed(4)).openPopup();\n" +
                "            showToast('Position mise à jour');\n" +
                "        }\n" +
                "        function showToast(message) {\n" +
                "            var toast = document.getElementById('toast');\n" +
                "            toast.innerText = message;\n" +
                "            toast.style.display = 'block';\n" +
                "            setTimeout(() => { toast.style.display = 'none'; }, 3000);\n" +
                "        }\n" +
                "        document.getElementById('searchInput').addEventListener('input', function(e) {\n" +
                "            fetchSuggestions(e.target.value);\n" +
                "        });\n" +
                "        document.getElementById('searchInput').addEventListener('keypress', function(e) {\n" +
                "            if (e.key === 'Enter') {\n" +
                "                searchLocation();\n" +
                "                document.getElementById('suggestions').style.display = 'none';\n" +
                "            }\n" +
                "        });\n" +
                "        document.getElementById('searchInput').addEventListener('blur', function() {\n" +
                "            setTimeout(() => { document.getElementById('suggestions').style.display = 'none'; }, 200);\n" +
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
                        "};" +
                        "window.updateAddress = function(address) {" +
                        "    window.address = address;" +
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
                    }
                    if (address != null) {
                        adresseField.setText(String.valueOf(address));
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
        zoomInBtn.setOnAction(event -> {
            if (isMapLoaded) webEngine.executeScript("zoomIn();");
        });
        zoomOutBtn.setOnAction(event -> {
            if (isMapLoaded) webEngine.executeScript("zoomOut();");
        });
        myLocationBtn.setOnAction(event -> {
            if (isMapLoaded) webEngine.executeScript("goToMyLocation();");
        });

        latitudeField.textProperty().addListener((obs, oldVal, newVal) -> updateMapFromFields());
        longitudeField.textProperty().addListener((obs, oldVal, newVal) -> updateMapFromFields());
    }

    private void updateMapFromFields() {
        if (!isMapLoaded) return;
        try {
            double lat = Double.parseDouble(latitudeField.getText());
            double lng = Double.parseDouble(longitudeField.getText());
            if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {
                showAlert(Alert.AlertType.WARNING, "Coordonnées invalides", "Latitude doit être entre -90 et 90, longitude entre -180 et 180.");
                return;
            }
            webEngine.executeScript(String.format("if (typeof setLocation === 'function') { setLocation(%f, %f); }", lat, lng));
        } catch (NumberFormatException e) {
            System.err.println("Invalid coordinate format: " + e.getMessage());
        }
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

    public void setEvenementAModifier(Evenement evenement) {
        this.evenementAModifier = evenement;
        if (isMapLoaded) {
            chargerDonneesEvenement();
        }
    }

    private void chargerDonneesEvenement() {
        if (evenementAModifier != null) {
            titreTF.setText(evenementAModifier.getTitre());
            descriptionTA.setText(evenementAModifier.getDescription());
            dateDebutPicker.setValue(evenementAModifier.getDateDebut().toLocalDate());
            heureDebutSpinner.getValueFactory().setValue(evenementAModifier.getDateDebut().getHour());
            minuteDebutSpinner.getValueFactory().setValue(evenementAModifier.getDateDebut().getMinute());
            dateFinPicker.setValue(evenementAModifier.getDateFin().toLocalDate());
            heureFinSpinner.getValueFactory().setValue(evenementAModifier.getDateFin().getHour());
            minuteFinSpinner.getValueFactory().setValue(evenementAModifier.getDateFin().getMinute());
            organisateurComboBox.setValue(evenementAModifier.getId_organisateur());
            latitudeField.setText(evenementAModifier.getLatitude());
            longitudeField.setText(evenementAModifier.getLongitude());
            adresseField.setText(evenementAModifier.getAdresse());
            categorieComboBox.setValue(evenementAModifier.getCategorie());
            if (evenementAModifier.getVisibilite().equals("Public")) {
                publicRadio.setSelected(true);
            } else {
                priveRadio.setSelected(true);
            }
            capaciteSpinner.getValueFactory().setValue(evenementAModifier.getCapacite());

            // Update map with event location
            if (isMapLoaded) {
                try {
                    double lat = Double.parseDouble(evenementAModifier.getLatitude());
                    double lng = Double.parseDouble(evenementAModifier.getLongitude());
                    if (lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180) {
                        webEngine.executeScript(String.format("if (typeof setLocation === 'function') { setLocation(%f, %f); }", lat, lng));
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Coordonnées invalides", "Les coordonnées de l'événement sont hors des limites valides.");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid coordinates: " + e.getMessage());
                    showAlert(Alert.AlertType.WARNING, "Erreur de coordonnées", "Les coordonnées de l'événement ne sont pas valides.");
                }
            }
        }
    }

    @FXML
    void modifierEvenement(ActionEvent event) {
        if (evenementAModifier == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun événement sélectionné pour modification");
            return;
        }

        if (!validateForm()) {
            showAlert(Alert.AlertType.ERROR, "Formulaire incomplet", "Veuillez remplir tous les champs obligatoires");
            return;
        }

        LocalDateTime debut = dateDebutPicker.getValue().atTime(
                heureDebutSpinner.getValue(), minuteDebutSpinner.getValue());
        LocalDateTime fin = dateFinPicker.getValue().atTime(
                heureFinSpinner.getValue(), minuteFinSpinner.getValue());

        if (fin.isBefore(debut)) {
            showAlert(Alert.AlertType.ERROR, "Erreur de date", "La date de fin doit être après la date de début");
            return;
        }

        try {
            double lat = Double.parseDouble(latitudeField.getText());
            double lng = Double.parseDouble(longitudeField.getText());
            if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {
                showAlert(Alert.AlertType.ERROR, "Coordonnées invalides", "Latitude doit être entre -90 et 90, longitude entre -180 et 180.");
                return;
            }

            Evenement evenementModifie = new Evenement(
                    evenementAModifier.getId(),
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

            EvenementsServices es = new EvenementsServices();
            es.modifier(evenementModifie);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "L'événement a été modifié avec succès !");
            retourAListe(event);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de coordonnées", "Les coordonnées saisies ne sont pas valides.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
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
    void retourAListe(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/thesphynx/AfficherEvent.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la liste: " + e.getMessage());
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