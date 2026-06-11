package com.thesphynx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        try {
            // Debug : affiche le chemin recherché
            URL fxmlUrl = getClass().getResource("/com/thesphynx/Home.fxml");
            System.out.println("Chemin FXML: " + (fxmlUrl != null ? fxmlUrl.toString() : "NULL"));

            if (fxmlUrl == null) {
                throw new IOException("Fichier FXML introuvable");
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            stage.setScene(new Scene(root));
            stage.show();
            stage.setTitle("Gestion d'Événements");
        } catch (IOException e) {
            System.err.println("Erreur critique: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        // Vérification préalable des ressources
        checkResources();
        launch(args);
    }

    private static void checkResources() {
        String[] fichiers = {
                "/com/thesphynx/AjouterEvent.fxml",
                "/com/thesphynx/EvenementInfo.fxml",
                "/com/thesphynx/AfficherEvent.fxml"
        };

        for (String fichier : fichiers) {
            if (App.class.getResource(fichier) == null) {
                System.err.println("ERREUR: Fichier manquant - " + fichier);
                System.exit(1);
            }
        }
    }
}
