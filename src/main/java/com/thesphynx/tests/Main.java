package com.thesphynx.tests;

import com.thesphynx.entities.Evenement;
import com.thesphynx.services.EvenementsServices;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class Main extends Application {
    public static void main(String[] args) {
        // Set the JavaFX version property
        System.setProperty("javafx.version", "17");

        // Run database tests before launching JavaFX
        testDatabaseOperations();

        // Launch the JavaFX application
        launch(args);
    }

    private static void testDatabaseOperations() {
        System.out.println("=== DÉBUT DES TESTS BASE DE DONNÉES ===");
        EvenementsServices es = new EvenementsServices();

        try {
            // Test CRUD complet
            Evenement testEvent = new Evenement(
                    "Événement de Test",
                    "Description de test",
                    LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(2),
                    1,  // id_organisateur
                    "48.8566",  // latitude (Paris)
                    "2.3522",   // longitude (Paris)
                    "Paris, France",  // adresse
                    "BLANC",  // categorie
                    "Public",  // visibilite
                    52  // capacite
            );

            // Create
            es.ajouter(testEvent);
            System.out.println("[SUCCÈS] Ajout - Événement créé avec ID: " + testEvent.getId());

            // Read
            Evenement retrieved = es.getById(testEvent.getId());
            System.out.println("[SUCCÈS] Lecture - Événement récupéré: " + retrieved.getTitre());
            System.out.println("[SUCCÈS] Coordonnées: " + retrieved.getLatitude() + ", " + retrieved.getLongitude());
            System.out.println("[SUCCÈS] Adresse: " + retrieved.getAdresse());
            System.out.println("[SUCCÈS] Catégorie: " + retrieved.getCategorie());
            System.out.println("[SUCCÈS] Visibilité: " + retrieved.getVisibilite());
            System.out.println("[SUCCÈS] Capacité: " + retrieved.getCapacite());

            // Update
            retrieved.setTitre("Titre modifié");
            retrieved.setDescription("Nouvelle description");
            retrieved.setLatitude("48.8534");  // Modification de la latitude
            retrieved.setLongitude("2.3488");  // Modification de la longitude
            retrieved.setAdresse("Nouveau lieu, Paris");
            retrieved.setCategorie("ROUGE");
            retrieved.setVisibilite("Privé");
            retrieved.setCapacite(100);
            es.modifier(retrieved);
            System.out.println("[SUCCÈS] Mise à jour - Événement modifié");

            // Delete
            es.supprimer(retrieved);
            System.out.println("[SUCCÈS] Suppression - Événement supprimé");

            // Test affichage complet
            System.out.println("\nListe complète des événements:");
            es.afficher().forEach(e -> System.out.println(" - " + e.getTitre() +
                    " (" + e.getDateDebut() + ") - " + e.getLatitude() + "," + e.getLongitude() +
                    " - Adresse: " + e.getAdresse() +
                    " - Catégorie: " + e.getCategorie() +
                    " - Visibilité: " + e.getVisibilite() +
                    " - Capacité: " + e.getCapacite()));

        } catch (SQLException e) {
            System.err.println("[ÉCHEC] Erreur SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ÉCHEC] Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=== FIN DES TESTS BASE DE DONNÉES ===\n");
    }

    private static void verifyFXMLFiles() throws Exception {
        String[] requiredFXML = {
                "/com/thesphynx/AjouterEvent.fxml",
                "/com/thesphynx/EvenementInfo.fxml",
                "/com/thesphynx/AfficherEvent.fxml",
                "/com/thesphynx/Home.fxml",
                "/com/thesphynx/ModifierEvent.fxml"
        };

        for (String fxml : requiredFXML) {
            if (Main.class.getResource(fxml) == null) {
                throw new Exception("Fichier FXML manquant: " + fxml);
            }
            System.out.println("[SUCCÈS] Fichier trouvé: " + fxml);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("=== TEST INTERFACE UTILISATEUR ===");
        try {
            // Vérification des fichiers FXML
            verifyFXMLFiles();

            // Chargement de l'interface principale
            System.out.println("Démarrage de l'interface JavaFX...");
            Parent root = FXMLLoader.load(getClass().getResource("/com/thesphynx/Home.fxml"));
            Scene scene = new Scene(root, 800, 600);

            try {
                scene.getStylesheets().add(getClass().getResource("/com/thesphynx/styles.css").toExternalForm());
            } catch (NullPointerException e) {
                System.out.println("[INFO] Fichier CSS non trouvé, continuation sans style");
            }

            primaryStage.setTitle("Gestion d'Événements - Test");
            primaryStage.setScene(scene);
            primaryStage.show();
            System.out.println("Interface JavaFX chargée avec succès");
        } catch (Exception e) {
            System.err.println("[ÉCHEC] Erreur JavaFX: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}