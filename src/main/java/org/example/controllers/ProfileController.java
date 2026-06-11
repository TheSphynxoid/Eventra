package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import org.example.MainApp;
import org.example.entities.User;
import org.example.services.UserServices;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ProfileController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ImageView profileImageView;

    private final UserServices userServices = new UserServices();
    private User currentUser;
    private File selectedImageFile;

    @FXML
    public void initialize() {
        currentUser = MainApp.LAYOUT_CONTROLLER.getCurrentUser();

        // Make the profile image circular
        Circle clip = new Circle(50, 50, 50);
        profileImageView.setClip(clip);

        if (currentUser != null) {
            nomField.setText(currentUser.getNom());
            prenomField.setText(currentUser.getPrenom());
            emailField.setText(currentUser.getEmail());
            telephoneField.setText(currentUser.getNumeroTelephone());

            String path = currentUser.getProfilePicture();
            if (path != null && !path.isEmpty()) {
                File file = new File(System.getProperty("user.dir") + File.separator + path);
                if (file.exists()) {
                    profileImageView.setImage(new Image(file.toURI().toString()));
                }
            }
        }
    }

    @FXML
    private void handleSave() {
        try {
            currentUser.setNom(nomField.getText());
            currentUser.setPrenom(prenomField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setNumeroTelephone(telephoneField.getText());

            if (selectedImageFile != null) {
                File destDir = new File(System.getProperty("user.dir") + File.separator + "profile_images");
                if (!destDir.exists()) destDir.mkdirs();

                String extension = selectedImageFile.getName().substring(selectedImageFile.getName().lastIndexOf("."));
                String uniqueFileName = "user_" + currentUser.getId() + "_" + System.currentTimeMillis() + extension;
                File destFile = new File(destDir, uniqueFileName);

                Files.copy(selectedImageFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Store relative path like "profile_images/user_3_1715281987.jpg"
                currentUser.setProfilePicture("profile_images" + File.separator + uniqueFileName);
            }

            userServices.modifier(currentUser);
            showAlert("Succès", "Profil mis à jour !");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec de la copie de l'image.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de mettre à jour : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        MainApp.LAYOUT_CONTROLLER.loadContent("/Dashboard.fxml");
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image de profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            profileImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
